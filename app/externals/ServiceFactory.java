package externals;

import java.util.HashMap;
import java.util.Map;

//import com.alibaba.dubbo.config.ApplicationConfig;
//import com.alibaba.dubbo.config.ReferenceConfig;
//import com.alibaba.dubbo.config.RegistryConfig;
//import com.alibaba.dubbo.config.utils.ReferenceConfigCache;

import jws.Jws;

//public class ServiceFactory {
//	private static String RegistUrl = Jws.configuration.getProperty("regist_url");
//	
//	private final static Map<String, String> VERSIONMAP = new HashMap<String, String>();
//	private static ApplicationConfig application = new ApplicationConfig();
//	private static RegistryConfig registry = new RegistryConfig();
//	private static Map<String, Object> HttpServiceCache = new HashMap<String, Object>();
//	
//	static {
//		VERSIONMAP.put("cn.jugame.order.api.IOrderReadService", "1.0.0");
//		VERSIONMAP.put("cn.jugame.shop.api.IShopReadService", "1.1.0");
//		VERSIONMAP.put("cn.jugame.redenvelope.api.IRedEnvelopeService", "1.0.3");
//		VERSIONMAP.put("cn.jugame.account_center.api.IAccountCenterService", "1.1.2");
//		VERSIONMAP.put("cn.jugame.dic.api.IDictionaryService", "1.0.0");
//		VERSIONMAP.put("cn.jugame.service.gameproduct.api.IGameService", "1.0.0");
//		VERSIONMAP.put("cn.jugame.service.gameproduct.api.IProductService", "1.0.0");
//		VERSIONMAP.put("cn.jugame.gift.api.IAppGiftService", "3.5.0");
//		VERSIONMAP.put("cn.jugame.order.api.IOrderOperateService", "1.0.0");
//		VERSIONMAP.put("cn.juhaowan.custserver.service.api.ILocalKefuService", "1.0.0");
//		VERSIONMAP.put("cn.jugame.service.order.api.IOrderService", "1.0.0");
//		
//		application.setName("H5-game-server");
//		registry.setTimeout(50000);
//		//注册中心地址
//		registry.setAddress(RegistUrl);
//	}
//	
//	public static <T> T createService(Class<? extends T> clazz){
//		return createService(clazz, VERSIONMAP.get(clazz.getName()));
//	}
//	
//	public static <T> T createService(Class<? extends T> clazz, String version){
//		ReferenceConfigCache cache = ReferenceConfigCache.getCache();
//    	ReferenceConfig<T> reference = new ReferenceConfig<T>(); // 此实例很重，封装了与注册中心的连接以及与提供者的连接，请自行缓存，否则可能造成内存和连接泄漏
//    	reference.setTimeout(50000);
//    	reference.setApplication(application);
//    	reference.setRegistry(registry); // 多个注册中心可以用setRegistries()
//    	reference.setInterface(clazz);
//    	reference.setVersion(version);
//    	T service = cache.get(reference);
//		return service;
//	}
//	
//	
//}


