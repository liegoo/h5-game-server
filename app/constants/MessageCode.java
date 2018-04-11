package constants;

import org.apache.commons.lang3.StringUtils;

import jws.i18n.Lang;
import jws.i18n.Messages;
import utils.MessageUtil;

/**
 * @author shun
 * 业务操作信息类，根据相应的提示key，在messages文件中取得相应的提示语
 * 其中的msgCode会返回给调用端作为结果码
 */
public enum MessageCode {
	 
 	 
	ERROR_CODE_500("%s",500),
	ERROR_CODE_510("%s",510);
	
	private String msg;
	
	private int msgCode;
	
	private MessageCode(String msg, int msgCode) {
		
		this.msg = msg;
		this.msgCode = msgCode;
	}
	
	public String msg() {
		return msg;
	}

	public int msgCode() {
		return msgCode;
	}
	
	public String getMsg() {
		return msg;
	}

	public String toString() {
		return Integer.valueOf(this.msgCode).toString();
	}
	
	public static MessageCode getMessageCode(String msgKey) {
		if(StringUtils.isEmpty(msgKey)) {
			return null;
		}
		for(MessageCode msgCode : MessageCode.values()) {
			if(msgCode.name().equalsIgnoreCase((msgKey))) {
				return msgCode;
			}
		}
		return null;
	}
}
