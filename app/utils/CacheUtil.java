package utils;

import java.lang.reflect.Type;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import jws.dal.Cache;
import jws.libs.Codec;

/**
 * 
 * 自管理的缓存
 * 
 * @author 
 * @createDate 2015年5月14日
 *
 */
public class CacheUtil {
    private static final Logger logger = LoggerFactory.getLogger(CacheUtil.class);
    private static final Gson gson = new Gson();

    /**
     * 将指定的至转化为json后，在设置到缓存中
     * 
     * @param key
     * @param value
     * @param prefix
     * @return
     */
    public static <T> boolean set(String key, T value, String prefix) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(prefix) || null == value) {
            return false;
        }
        try {
            // 将对应的实体转化为json
            String json = gson.toJson(value, value.getClass());
            return Cache.set(reviseKey(key), json, prefix);
        } catch (Exception e) {
            logger.error("", e);
        }
        return false;
    }

    /**
     * 根据指定的key，获取其对应的实体
     * 
     * @param key
     * @param clazz
     * @param prefix
     * @return
     */
    public static <T> T get(String key, Class<T> clazz, String prefix) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(prefix)) {
            return null;
        }
        //
        try {
            String json = (String) Cache.get(reviseKey(key), prefix);
            if (null == json) {
                return null;
            }
            Type type = TypeToken.get(clazz).getType();
            return gson.fromJson(json, type);
        } catch (Exception e) {
            logger.error("", e);
        }
        return null;
    }
    
    public static <T> T get(String key, TypeToken<T> typeToken, String prefix) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(prefix)) {
            return null;
        }
        try {
            String json = (String) Cache.get(reviseKey(key), prefix);
            if (null == json) {
                return null;
            }
            return gson.fromJson(json, typeToken.getType());
        } catch (Exception e) {
            logger.error("", e);
        }
        return null;
    }

    private static String reviseKey(String key) {
        // 首先看key的长度是否超过250，如果是，则直接存md5值
        if (key.length() > 250) {
            return Codec.hexMD5(key);
        }
        return key;
    }

}
