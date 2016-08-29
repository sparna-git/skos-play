package fr.sparna.rdf.datapress;

import java.util.List;

import org.eclipse.rdf4j.rio.RDFHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompositePress extends DataPressBase implements DataPress {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	protected List<DataPress> presses;
	
	@Override
	public void press(
			byte[] in,
			String documentUrl,
			RDFHandler out
	) throws DataPressException {
		log.debug("Presse {}", documentUrl);
		
		if(presses != null) {
			for (DataPress aPress : this.presses) {
				try {
					aPress.press(in, documentUrl, out);
				} catch (Exception e) {
					log.error("Error in press {} : {}",aPress.getClass().getSimpleName(), e.getMessage());
					e.printStackTrace();
				}			
			}
		}
	}

	public List<DataPress> getPresses() {
		return presses;
	}

	public void setPresses(List<DataPress> presses) {
		this.presses = presses;
	}
	
}
