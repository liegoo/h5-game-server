package moudles.gae.assist;

/**
 * 自定义异常
 * 
 * @author caixb
 *
 */
public class UnfitResultException extends Exception{
	private static final long serialVersionUID = 1L;
	
	private String message;
	
	public UnfitResultException(String message){
		super(message);
		this.message = message;
	}
	@Override
	public String getMessage() {
		return this.message;
	}
}
