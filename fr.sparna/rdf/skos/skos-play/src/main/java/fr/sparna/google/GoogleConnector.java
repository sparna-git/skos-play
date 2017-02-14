package fr.sparna.google;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GoogleConnector {
	
	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	protected static final String CLIENT_SECRET_LOCATION = "/client_secret.json";
	
//	private static final String CLIENT_SECRET = "vN-Q9H2dI7Oc4-I4KVaRJ2RI";
//	private static final String CLIENT_ID = "611030822832-ea9cimuarqabdaof7e1munk90hr67mlo.apps.googleusercontent.com";
	
	// the URL to redirect to after login
	protected String redirectUrl;
	// the name of the application
	protected String applicationName = null;
	// JSON parsing
	protected JsonFactory jsonFactory = new JacksonFactory();
    // HTTP transport factory
	protected HttpTransport httpTransport = new NetHttpTransport();
	// the client secret and client ID read from client_secret.json
	protected String clientSecret;
	protected String clientId;
	// the credential obtained after successfull login
	protected GoogleCredential credential;
    protected Drive drive;
    
	
	public GoogleConnector(String applicationName, String redirectUrl) throws IOException {
		super();
		this.applicationName = applicationName;
		this.redirectUrl = redirectUrl;
		// init the client secret and client ID
		GoogleClientSecrets gcs = readClientSecrets();
		this.clientSecret = gcs.getWeb().getClientSecret();
		this.clientId = gcs.getWeb().getClientId();
	}
	
    private GoogleClientSecrets readClientSecrets() throws IOException {
        Reader reader = new InputStreamReader(GoogleAuthHelper.class.getResourceAsStream(CLIENT_SECRET_LOCATION));
        return GoogleClientSecrets.load(jsonFactory, reader);
    }

	/**
	 * Renvoie l'URL pour qu'un utilisateur se loggue et donne les droits d'accès appropriés à l'application
	 * @return
	 * @throws MalformedURLException
	 */
	public URL generateLoginUrl() {
		URL loginUrl = null;
		try {
			final String BASE_AUTH_URL = "https://accounts.google.com/o/oauth2/auth";
			loginUrl = new URL(BASE_AUTH_URL+""
					// see https://developers.google.com/identity/protocols/OpenIDConnect#scope-param
					+ "?scope="+URLEncoder.encode("openid profile email https://www.googleapis.com/auth/drive.readonly https://www.googleapis.com/auth/drive.metadata.readonly", "UTF-8")
					+ "&redirect_uri="+redirectUrl
					+ "&response_type=code"
					+ "&client_id="+this.clientId+""
					// 'force' ou 'auto'. Si 'force', la demande d'autorisation est refaite à chaque fois
					// avec 'auto', si l'utilisateur a déjà donné son accord, les autorisations ne sont pas redemandées
					+ "&approval_prompt=auto");			
		} catch (MalformedURLException ignore) {
			ignore.printStackTrace();
		} catch (UnsupportedEncodingException ignore) {
			ignore.printStackTrace();
		}		
		return loginUrl;
	}
	
	/**
	 * Récupère un token d'accès à partir du code renvoyé par l'API Google après le login de l'utilisateur.
	 * @param code
	 * @return
	 * @throws IOException
	 */
	public String getAccessToken(String code) throws IOException {
		final String BASE_TOKEN_URL = "https://accounts.google.com/o/oauth2/token";
		
		String urlParameters = "code="
				+ code
				+ "&client_id="+this.clientId
				+ "&client_secret="+this.clientSecret
				+ "&redirect_uri="+redirectUrl
				+ "&grant_type=authorization_code";

		//post parameters
		URL url = new URL(BASE_TOKEN_URL);
		URLConnection urlConn = url.openConnection();
		urlConn.setDoOutput(true);
		
		try(OutputStreamWriter writer = new OutputStreamWriter(urlConn.getOutputStream())) {
			writer.write(urlParameters);
			writer.flush();
		}	

		//get output in outputString 
		String line, outputString = "";
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()))) {
			while ((line = reader.readLine()) != null) {
				outputString += line;
			}
		}
		
		log.debug("Retour de l'appel Google 'token' : "+outputString);

		//get Access Token 
		JsonObject json = (JsonObject)new JsonParser().parse(outputString);
		String access_token = json.get("access_token").getAsString();
		log.debug("Access Token récupéré : "+access_token);
		
		return access_token;
	}
	
	/**
	 * Récupère les informations de l'utilisateur loggué à partir du token d'accès.
	 * 
	 * @param access_token
	 * @return
	 * @throws IOException
	 */
	public GoogleUser readUserInfo(String access_token) throws IOException {
		//get User Info 
		URL url = new URL(
				"https://www.googleapis.com/oauth2/v1/userinfo?access_token="
				+ access_token);
		URLConnection urlConn = url.openConnection();
		
		String line, outputString = "";
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()))) {
			while ((line = reader.readLine()) != null) {
				outputString += line;
			}
		}
		
		log.debug("Retour de l'appel Google 'userinfo' : "+outputString);

		GoogleUser user = new Gson().fromJson(outputString, GoogleUser.class);
		return user;
	}
	
	/**
	 * Revoke the access token
	 * see http://stackoverflow.com/questions/21405274/this-app-would-like-to-have-offline-access-when-access-type-online
	 * @throws IOException
	 */
	public void revokeToken() throws IOException {
		if(this.credential != null) {
			log.debug("Revoke token : "+this.credential.getAccessToken());
			final String BASE_REVOKE_URL = "https://accounts.google.com/o/oauth2/revoke";
			
			String urlParameters = "token="+this.credential.getAccessToken();

			//post parameters
			URL url = new URL(BASE_REVOKE_URL+"?"+urlParameters);
			URLConnection urlConn = url.openConnection();
			urlConn.setDoOutput(true);

			String line, outputString = "";
			try(BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()))) {
				while ((line = reader.readLine()) != null) {
					outputString += line;
				}
			}
			
			// returns empty JSON {}
			log.debug("Retour de l'appel Google 'revoke' : "+outputString);
		}
	}
	
	public GoogleCredential createAndRegisterCredential(String access_token) {
		this.credential = new GoogleCredential().setAccessToken(access_token);
		return credential;
	}
	
    public Drive getDriveService() throws IOException {
    	if(drive == null) {
    		drive = new Drive.Builder(
                    httpTransport, jsonFactory, credential)
                    .setApplicationName(applicationName)
                    .build();
    	}
        return drive;
    }

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public JsonFactory getJsonFactory() {
		return jsonFactory;
	}

	public void setJsonFactory(JsonFactory jsonFactory) {
		this.jsonFactory = jsonFactory;
	}

	public HttpTransport getHttpTransport() {
		return httpTransport;
	}

	public void setHttpTransport(HttpTransport httpTransport) {
		this.httpTransport = httpTransport;
	}

	public GoogleCredential getCredential() {
		return credential;
	}
	
	
	
}
