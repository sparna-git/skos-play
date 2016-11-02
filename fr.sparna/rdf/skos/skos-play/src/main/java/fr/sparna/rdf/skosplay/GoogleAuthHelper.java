
package fr.sparna.rdf.skosplay;

import java.awt.Desktop;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;



/**
 * A helper class for Google's OAuth2 authentication API
 *
 * @author <a href="http://www.matelli.fr">Matelli</a>
 * @see <a href="https://developers.google.com/accounts/docs/OAuth2WebServer">https://developers.google.com/accounts/docs/OAuth2WebServer</a>
 * @see <a href="https://code.google.com/p/google-api-java-client/wiki/OAuth2">https://code.google.com/p/google-api-java-client/wiki/OAuth2</a>
 */
public class GoogleAuthHelper {

    // =========================================================================
    // ATTRIBUTES
    // =========================================================================

    protected static Logger logger = Logger.getLogger(GoogleAuthHelper.class);
    protected String clientSecretsLocation = "/client_secret.json";
    protected JsonFactory jsonFactory = new JacksonFactory();
    protected HttpTransport httpTransport = new NetHttpTransport();

    protected List<String> scopes = new ArrayList<String>();
    protected String stateToken = null;
    protected String refreshToken = null;
    Drive serviceDrive=null;
    /**
     * Authorization code to exchange for OAuth 2.0 credentials.
     */
    protected String authorizationCode = null;
    protected GoogleAuthorizationCodeFlow flow = null;
    protected GoogleClientSecrets googleClientSecrets = this.importClientSecrets();
    protected String applicationName = null;
    protected String redirectUri;
    // protected String baseRedirectUri = "http://localhost:8080/skos-play/convert";

    // =========================================================================
    // CONSTRUCTORS
    // =========================================================================

    /**
     * Contructeur par defaut
     *
     * @param redirectUri CallBalk
     * @throws Exception
     */
    public GoogleAuthHelper(String redirectUri) throws Exception {
        if (redirectUri != null) {
            this.redirectUri = redirectUri;
        }
    }

    // =========================================================================
    // METHODS
    // =========================================================================

    /**
     * Import le credential Json de Google
     *
     * @return
     * @throws IOException
     */
    private GoogleClientSecrets importClientSecrets() throws IOException {
        Reader reader = new InputStreamReader(GoogleAuthHelper.class.getResourceAsStream(clientSecretsLocation));
        return GoogleClientSecrets.load(jsonFactory, reader);
    }


    /**
     * Initialise le GoogleAuthorizationCodeFlow avec le googleClientSecrets et
     * les scopes
     *
     * @return Le flow de GoogleAuthorization
     */
    public GoogleAuthorizationCodeFlow getFlow() {
        if (flow == null) {
            jsonFactory = new JacksonFactory();
            httpTransport = new NetHttpTransport();

            String approvalPrompt = "force";
            if (refreshToken != null) {
                approvalPrompt = "auto";
            }

            flow = new GoogleAuthorizationCodeFlow
                    .Builder(httpTransport, jsonFactory, googleClientSecrets, scopes)
                    .setApprovalPrompt(approvalPrompt)
                    .setAccessType("offline").build();
            generateStateToken();
        }
        return flow;
    }

    /**
     * Exchange an authorization code for OAuth 2.0 credentials.
     *
     * @return OAuth 2.0 credentials
     * @throws java.io.IOException
     */
    public Credential exchangeCode() throws IOException {
        this.flow = getFlow();
        Credential credential = null;

        if (refreshToken == null) {
            GoogleTokenResponse response = flow.newTokenRequest(authorizationCode)
                    .setRedirectUri(redirectUri)
                    .setGrantType("authorization_code")
                    .execute();

            credential = new GoogleCredential.Builder()
                    .setTransport(httpTransport)
                    .setJsonFactory(jsonFactory)
                    .setClientSecrets(googleClientSecrets)
                    .build();
            credential.setFromTokenResponse(response);
        } else {
            credential = new GoogleCredential.Builder()
                    .setJsonFactory(jsonFactory)
                    .setTransport(httpTransport)
                    .setClientSecrets(googleClientSecrets)
                    .build();
            credential.setRefreshToken(refreshToken);
        }
        System.out.println("RefreshToken : " + credential.getRefreshToken());
        System.out.println("AccessToken : " + credential.getAccessToken());
        return credential;
    }
    
    public  Drive getDriveService(Credential credential) throws IOException {
         return new Drive.Builder(
                 httpTransport, jsonFactory, credential)
                 .setApplicationName(applicationName)
                 .build();
     }

    /**
     * Retrieve the authorization URL
     *
     * @return L'url d'authorisation
     */
    public String getAuthorizationUrl() {
        this.flow = getFlow();
        GoogleAuthorizationCodeRequestUrl urlBuilder = flow.newAuthorizationUrl()
                .setRedirectUri(redirectUri)
                .setState(stateToken);
        return urlBuilder.build();
    }
    public Drive createServiceDrive(Credential credential) {
        if (serviceDrive == null) {
            serviceDrive = new Drive.Builder(httpTransport, jsonFactory, credential)
                    .setApplicationName(applicationName)
                    .build();
        }
        return serviceDrive;
}

    /**
     * Generates a secure state token
     */
    private void generateStateToken() {
        SecureRandom sr1 = new SecureRandom();
        stateToken = "google;" + sr1.nextInt();
    }

    /**
     * Ajoute des scopes supplementaires
     *
     * @param scopes
     */
    public void addScopes(List<String> scopes) {
        this.scopes.addAll(scopes);
    }

    // =========================================================================
    // OVERRIDES
    // =========================================================================

    // =========================================================================
    // GETTERS & SETTERS
    // =========================================================================


    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }

    public List<String> getScopes() {
        return scopes;
    }
    
    public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	/*public static void main(String...args) throws Exception {
		GoogleAuthHelper me = new GoogleAuthHelper("http://localhost:8080/skos-play/convert");
    	me.setScopes(Arrays.asList(DriveScopes.DRIVE));
    	me.setApplicationName("SKOS Play");
    	GoogleAuthorizationCodeFlow flow = me.getFlow();
    	// flow....
    	System.out.println(me.getAuthorizationUrl());
    	 try {
       	  Desktop desktop = java.awt.Desktop.getDesktop();
       	  URI oURL = new URI(me.getAuthorizationUrl());
       	  desktop.browse(oURL);
       	} catch (Exception e) {
       	  e.printStackTrace();
       	}
		
    }*/
   
   
}