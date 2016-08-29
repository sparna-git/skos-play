package fr.sparna.rdf.datapress;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.rdf4j.rio.RDFHandler;

import fr.sparna.commons.io.InputStreamUtil;

public abstract class DataPressBase implements DataPress {
	
	@Override
	public void press(
			String uri,
			RDFHandler out
	) throws DataPressException {
		// read URI to byte data, and handle it to lower-level method
		try {
			press(
					read(uri),
					uri,
					out
			);
		} catch (IOException e) {
			throw new DataPressException("Can't read URI to press "+uri, e);
		}
	}
	
	private byte[] read(String url) throws IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpget = new HttpGet(url);
		CloseableHttpResponse response = httpclient.execute(httpget);
		
		byte[] result = null;
		try {
		    HttpEntity entity = response.getEntity();
		    if (entity != null) {
		        InputStream instream = entity.getContent();
		        try {
		            result = InputStreamUtil.readToBytes(instream);
		        } finally {
		            instream.close();
		        }
		    }
		} finally {
		    response.close();
		}
		
		return result;
	}
	
}
