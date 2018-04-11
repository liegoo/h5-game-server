package exception;

import constants.MessageCode;

public class BusinessException extends Exception  {

    private String message;
    
    private MessageCode msgCode;
    
    @Deprecated
    public BusinessException(String message) {
        super(message);
        this.message = message;
    }

    public BusinessException(MessageCode msgCode, Object... args) {
    	this.msgCode = msgCode;
    	this.message = String.format(msgCode.msg(), args);
    }
    
    public BusinessException(MessageCode mc) {
    	this.msgCode = mc;
    	this.message = String.format(mc.msg());
    }
    
    public String getMessage() {
    	return this.message;
    }
    
    public MessageCode getMessageCode() {
    	return this.msgCode;
    }
}
