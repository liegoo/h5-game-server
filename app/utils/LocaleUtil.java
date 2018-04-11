package utils;

import java.lang.reflect.Field;

import org.apache.commons.lang3.StringUtils;

import jws.Logger;

/**
 * 获取国际化字段值得
 * @author fish
 *
 */
public class LocaleUtil {

	/**
	 * 从目标对象中获取需要的lang语言文本
	 * 规则：
	 * 1、按语言+区域国际化，fieldSrc+lang组合，如果完全字段匹配存在，则复制给dest对象的fieldDest
	 * 2、只按语言国际化，从lang中得到语言，然后从src对象中遍历分析那个字段的语言满足，取第一个复制给fieldDest
	 * 3、只按默认en语言显示
	 * @param src
	 * @param dest
	 * @param fieldSrc  保存有多国语言的对象的伪变量名，一般是DDL；伪变量名，如ddl有成员变量msgText_en_us,则fieldSrc=msgText
	 * @param fieldDest 被赋值对象的成员变量名
	 * @param lang
	 */
	public static void lang(Object src,Object dest,String fieldSrc,String fieldDest,String lang){
		if(src == null || dest == null || StringUtils.isEmpty(fieldSrc) || StringUtils.isEmpty(fieldDest)  || StringUtils.isEmpty(lang) ){
			return ;
		}
		try{
			if(fieldSrc.equals(fieldDest)){
				lang(src,dest,fieldSrc,lang);
			}
			
			String field = new String(fieldSrc);//新对象
			if(lang.split("-").length==1){
				field = field +"_"+lang.split("-")[0];
			}
			if(lang.split("-").length==2){
				field = field +"_"+lang.split("-")[0]+"_"+lang.split("-")[1];
			}
			
			Field[] srcFs= src.getClass().getDeclaredFields();
			if(srcFs == null || srcFs.length==0){
				return;
			}
			
			boolean geted = false;
			for(Field srcF:srcFs){
				String srcFieldName = srcF.getName();
				if(StringUtils.isEmpty(srcFieldName))continue;
				//完全匹配
				if(srcFieldName.equals(field)){
					PropertyUtil.setProperty(dest, fieldDest, PropertyUtil.getProperty(src, srcFieldName));
					geted = true;
					break; 
				}
			}
			
			if(geted) return ; 
			
			for(Field srcF:srcFs){
				String srcFieldName = srcF.getName();
				if(StringUtils.isEmpty(srcFieldName) || !srcFieldName.contains(fieldSrc))continue;
				//模糊匹配
				String[] names = srcFieldName.split("_");//下划线
				if(names !=null && names[1].equalsIgnoreCase(lang.split("-")[0])){
					PropertyUtil.setProperty(dest, fieldDest, PropertyUtil.getProperty(src, srcFieldName));
					geted = true;
					break;
				}
			}
			
			if(geted) return ; 
			
			for(Field srcF:srcFs){
				String srcFieldName = srcF.getName();
				if(StringUtils.isEmpty(srcFieldName) || !srcFieldName.contains(fieldSrc))continue;
				//取en
				String[] names = srcFieldName.split("_");//下划线
				if(names !=null && names[1].equalsIgnoreCase("en")){
					PropertyUtil.setProperty(dest, fieldDest, PropertyUtil.getProperty(src, srcFieldName));
					geted = true;
					break;
				}
			}
			
			if(geted) return ; 
			else Logger.info("field [%s] of class [%s] not match lang from dest[%s]", fieldSrc,dest.getClass().getName(),src.getClass().getName());
			
		}catch(Exception e){
			Logger.error(e, "");
		}
	}
	/**
	 * 重载一次，如果2个对象变量命名相同就只传一个field就可以了
	 * 比如msgText 和 msgText_zh_cn是相同的； 
	 * @param src
	 * @param dest
	 * @param field
	 * @param lang
	 */
	public static void lang(Object src,Object dest,String fieldSrc,String lang){
		if(src == null || dest == null || StringUtils.isEmpty(fieldSrc) || StringUtils.isEmpty(lang) ){
			return ;
		}
		try{
			String field = new String(fieldSrc);//新对象
			if(lang.split("-").length==1){
				field = field +"_"+lang.split("-")[0];
			}
			if(lang.split("-").length==2){
				field = field +"_"+lang.split("-")[0]+"_"+lang.split("-")[1];
			}
			
			Field[] srcFs= src.getClass().getDeclaredFields();
			if(srcFs == null || srcFs.length==0){
				return;
			}
			
			boolean geted = false;
			for(Field srcF:srcFs){
				String srcFieldName = srcF.getName();
				if(StringUtils.isEmpty(srcFieldName))continue;
				//完全匹配
				if(srcFieldName.equals(field)){
					PropertyUtil.setProperty(dest, fieldSrc, PropertyUtil.getProperty(src, srcFieldName));;
					geted = true;
					break; 
				}
			}
			
			if(geted) return ; 
			
			for(Field srcF:srcFs){
				String srcFieldName = srcF.getName();
				if(StringUtils.isEmpty(srcFieldName) || !srcFieldName.contains(fieldSrc))continue;
				//模糊匹配
				String[] names = srcFieldName.split("_");//下划线
				if(names !=null && names[1].equalsIgnoreCase(lang.split("-")[0])){
					PropertyUtil.setProperty(dest, fieldSrc, PropertyUtil.getProperty(src, srcFieldName));;
					geted = true;
					break;
				}
			}
			
			if(geted) return ; 
			
			for(Field srcF:srcFs){
				String srcFieldName = srcF.getName();
				if(StringUtils.isEmpty(srcFieldName) || !srcFieldName.contains(fieldSrc))continue;
				//取en
				String[] names = srcFieldName.split("_");//下划线
				if(names !=null && names[1].equalsIgnoreCase("en")){
					PropertyUtil.setProperty(dest, fieldSrc, PropertyUtil.getProperty(src, srcFieldName));;
					geted = true;
					break;
				}
			}
			
			if(geted) return ; 
			else Logger.info("field [%s] of class [%s] not match lang from src[%s]", fieldSrc,dest.getClass().getName(),src.getClass().getName());
			
		}catch(Exception e){
			Logger.error(e, "");
		}
	}
	
 
}
