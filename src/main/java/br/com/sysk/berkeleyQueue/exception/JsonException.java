package br.com.sysk.berkeleyQueue.exception;

public class JsonException extends RuntimeException implements BaseException {

	private static final long serialVersionUID = 9073663482100736465L;
	
	public JsonException(String message) {
		super(message);
	}
	
	public JsonException(String message, Throwable cause) {
		super(message, cause);
	}

	public JsonException(Throwable cause) {
		super(cause);
	}

	@Override
    public String getOriginalMessage(){
    	return getOriginalMessage(this);
    }
    
    private String getOriginalMessage(Throwable e){
    	return (e.getCause() == null) ? e.getMessage().replaceAll("\n", " ") : getOriginalMessage(e.getCause());
    }

}
