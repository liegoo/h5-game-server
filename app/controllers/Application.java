package controllers;

import externals.DicService;
import externals.gameproduct.GameProductService;
import jws.module.api.doll.DollAPI;
import jws.mvc.*;
import jws.mvc.Http.Request;
import externals.gameproduct.GameProductService;
import jws.mvc.Controller;
import jws.mvc.Scope.Flash;
import jws.mvc.With;

@With(Handler.class)
public class Application extends Controller {
    public static void index() {
    	Flash.current().error("xxx");
        render();
    }
    
    public static void error() {
    	throw new RuntimeException("a test exception");
    }
    
    public static void test(){
//    	renderJSON(DicService.service.resaleEnabled(401+""));
//    	renderJSON(DicService.service.getReferenceDiscount(266+""));
//    	renderJSON(GameProductService.get8868ChannelGames());
//    	renderJSON(GameProductService.getGames());
    	renderJSON(GameProductService.get8868ChannelGames());
    }
    
    public static void main(String[] args){
    	
    }
    
}