package fr.sparna.rdf.datapress;

public class DataPressException extends Exception {

    public DataPressException(String message) {
        super(message);
    }

    public DataPressException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public DataPressException(Throwable cause) {
        super(cause);
    }
	
}
