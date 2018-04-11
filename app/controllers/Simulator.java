
package controllers;

import jws.Logger;
import jws.mvc.Controller;

public class Simulator extends Controller{
	
	public static void callMe(){
		String body = request.params.get("body");
		Logger.info("body->%s", body);
		renderText("SUCCESS");
	}
}
