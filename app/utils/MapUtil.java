package utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jws.Logger;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author chenxs
 * @date 2013年12月7日
 */
public class MapUtil {

	/**
	 * 根据字段名称和列表封装成map
	 * 其中的map的key为字段值，value为该字段值的对象
	 * @param list        列表值
	 * @param fieldName    属性名称
	 * @return
	 * 	由列表的属性值和列表值封装得到map值
	 */
	public static <K, T> Map<K, T> wrapToMap(List<T> list, String fieldName, K k) {
		Map map = new HashMap();
		Object fieldVal = null;
		for (T t:list) {
			//取得对象的属性值
			if (t != null) {
				fieldVal = ReflectionUtil.getFieldValue(t, fieldName);
				//把属性值放入map中
				if (fieldVal != null) {
                    if (k instanceof String) {
                        map.put(fieldVal.toString(), t);
                    } else if (k instanceof Long) {
                        map.put(Long.valueOf(fieldVal.toString()), t);
                    } else if (k instanceof Double) {
                        map.put(Double.valueOf(fieldVal.toString()), t);
                    } else if (k instanceof Float) {
                        map.put(Long.valueOf(fieldVal.toString()), t);
                    } else if (k instanceof Integer) {
                        map.put(Integer.valueOf(fieldVal.toString()), t);
                    } else if (k instanceof Short) {
                        map.put(Integer.valueOf(fieldVal.toString()), t);
                    } else if (k instanceof Boolean) {
                        map.put(Boolean.valueOf(fieldVal.toString()), t);
                    }
				} else {
					Logger.warn("找不到%s属性值", fieldName);
				}
			}
		}
		return map;
	}
	
	/**
	 * 把对象封装成map
	 * @param instance	需要封装成map的对象
	 * @return
	 */
	public static <T> Map<String,Object> wrapInstanceToMap(T instance) {
		Class clazz = instance.getClass();
		Field[] fields = clazz.getDeclaredFields();
		
		Map<String,Object> map = new HashMap<String,Object>();
		for (Field field:fields) {
			Method method;
			try {
				//如果是boolean类型，则在取得方法的时候跟其他类型的不一亲，直接属性名称即可
				if (!(field.getGenericType() == boolean.class)
						|| field.getGenericType() == Boolean.class) {
					method = clazz.getMethod("get" + StringUtils.capitalize(field.getName()));
				} else {
					//如果是is开关的boolean类型变量，则会直接以属性名作为get方法名
					//否则，则会把属性名首字母大写，并在前面添加is
					if (field.getName().startsWith("is")) {
						method = clazz.getMethod(field.getName());
					} else {
						method = clazz.getMethod("is" + StringUtils.capitalize(field.getName()));
					}
				}
				map.put(field.getName(), method.invoke(instance));
		    } catch (Exception e) {
				Logger.error(e, "把对象封装成map出错:%s", e.getMessage());
			}
		}
		return map;
	}
	
}
