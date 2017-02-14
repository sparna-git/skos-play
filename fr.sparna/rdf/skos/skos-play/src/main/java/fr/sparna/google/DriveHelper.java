package fr.sparna.google;

import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

public class DriveHelper {
	
	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	private static final String MIME_TYPE_EXCEL = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"; 

	protected Drive drive;

	public DriveHelper(Drive drive) {
		super();
		this.drive = drive;
	}
	
	public FileList listSpreadsheets() throws IOException {
		FileList listeComplete = new FileList();
		String pageToken = null;
		do {
			FileList result = drive.files().list()
					.setQ("mimeType='application/vnd.google-apps.spreadsheet'")
					.setSpaces("drive")
					.setOrderBy("modifiedTime desc")
					.setFields("nextPageToken, files(id, name, modifiedTime)")
					.setPageToken(pageToken)
					.execute();
			
			listeComplete.putAll(result);
			
			for(File file: result.getFiles()) {
				log.debug("Found file : "+file.getName()+" ("+file.getId()+"), modified "+file.getModifiedTime());
			}
			pageToken = result.getNextPageToken();
		} while (pageToken != null);
		
		return listeComplete;
	}
	
	public void readSpreadsheet(String docId, OutputStream out) throws IOException {
		drive.files().export(docId, MIME_TYPE_EXCEL).executeMediaAndDownloadTo(out);
	}
	
}
