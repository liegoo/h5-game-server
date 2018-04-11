package controllers.external;

import constants.MessageCode;
import controllers.ucgc.api.ApiController;
import exception.BusinessException;
import jws.Jws;
import jws.Logger;
import jws.module.ucgc.api.ApiResponse;
import jws.module.utils.UCGCIPUtil;
import jws.mvc.Before;
import jws.mvc.Catch;
import jws.mvc.Finally;
import utils.StatWriteUtil;

public class ExternalController extends ApiController {

	@Before
	public static void beforeAction() {
		String clientIp = UCGCIPUtil.getIpAddr(request);
		Logger.info("external access ip is %s", clientIp);
		String[] ips = Jws.configuration.getProperty("acl.external.allow","").split(",");
		if(ips[0].equals("*")){
			return;
		}
		for(String ip:ips){
			if(ip.equals(clientIp)){
				return;
			}
		}
		Logger.error("IP[%s]非法", clientIp);
		ApiResponse res = new ApiResponse();
	    res.setId(System.currentTimeMillis());
	    res.setCode(500);
	    res.setMsg("非法请求");
		renderJSON(res);
//		getHelper().returnError(MessageCode.ERROR_CODE_500.msgCode(), "IP"+clientIp+"非法");
	}
	
	@Finally
	public static void finallyAction(){
		StatWriteUtil.pvAction(StatWriteUtil.Caller.h5gameservice, request.current().action, System.currentTimeMillis());
	}
	
	@Catch(Exception.class)
	protected static void onException(Throwable throwable) {
		/*if(throwable instanceof InvalidParameterException) {
			InvalidParameterException ipe = (InvalidParameterException)throwable;
			Logger.warn(throwable, "@Catch Exception: " + ipe.getMessage());
			getHelper().returnError(ipe.getMessageCode().msgCode(), ipe.getMessage());
		} else if (throwable instanceof ValidationException){
			ValidationException ve = (ValidationException)throwable;
			Logger.warn(throwable, "@Catch Exception: " + ve.getMessage());
			getHelper().returnError(MessageCode.INVALID_PARAM.msgCode(), MessageCode.INVALID_PARAM.msg());
		} else */
		if (throwable instanceof BusinessException){
			BusinessException be = (BusinessException)throwable;
			Logger.warn(throwable, "@Catch Exception: " + be.getMessage());
			getHelper().returnError(be.getMessageCode().msgCode(), be.getMessage());
		} else {
			Logger.error(throwable, "Unknown Exception: " + throwable.getMessage());
			getHelper().returnError(MessageCode.ERROR_CODE_500.msgCode(), "服务器异常");
		}
	}
}
