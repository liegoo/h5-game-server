package utils;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import jws.cache.Cache;
import jws.libs.Codec;

/**
 * 分布式锁
 * 分两个层次控制：
 * 1）应用层；
 * 2）分布式缓存；
 * @author yuyj@ucweb.com
 * @createDate 2014-12-22
 *
 */
public class DistributeCacheLock {
    private static DistributeCacheLock lock = new DistributeCacheLock();
    private static ConcurrentHashMap<String, String> records = new ConcurrentHashMap<String, String>();
    /* 缓存锁失效时间，不能过大，最好控制在1min以内 */
    public static String cacheTimeout = "1min";
    /* 缓存锁失效时间 */
    public static String cacheTimeout_2 = "2min";

    private DistributeCacheLock() {
    }

    /** 单件 */
    public static DistributeCacheLock getInstance() {
        return lock;
    }

    /** 进程锁：是否被锁住 */
    public boolean isLocked(String key) {
        return records.containsKey(key);
    }
    
    public String getLockedVal(String key)
    {
        return records.get(key);
    }
    
    /** 缓存锁：是否被锁住 */
    public boolean isCacheLocked(String key)
    {
        //缓存锁
        Object rst = Cache.get(reviseKey(key));
        return null != rst;
    }
    
    /**
     * 获取对应缓存锁保存的值
     * @param key
     * @return
     */
    public String getCachedLockedVal(String key)
    {
        Object rst = Cache.get(reviseKey(key));
        if(rst instanceof String)
        {
            return (String) rst;
        }
        return null;
    }
    
    private String reviseKey(String key)
    {
        //首先看key的长度是否超过250，如果是，则直接存md5值
        if(key.length() > 250)
        {
            return Codec.hexMD5(key);
        }
        return key;
    }
    
    /** 尝试加锁 */
    public boolean tryLock(String key) {
        String preVal = records.putIfAbsent(key, key);
        return preVal != key;
    }
    
    public boolean tryLock(String key,String val) {
        String preVal = records.putIfAbsent(key, val);
        return preVal != val;
    }

    public boolean tryCacheLock(String key) {
        //缓存锁
        return Cache.safeAdd(reviseKey(key), "locked", cacheTimeout);
    }
    public boolean tryCacheLock(String key,String val) {
        //缓存锁
        return Cache.safeAdd(reviseKey(key), val, cacheTimeout);
    }
    
    /**
     * 可以设定缓存时间的锁
     * expiration Ex: 10s, 3mn, 8h
     * @return
     */
    public boolean tryCacheLock(String key,String val,String expiration){
        return Cache.safeAdd(reviseKey(key), val, expiration);
    }
    
    /**
     * 重置对应缓存锁的时间
     * @param key
     * @param expiration
     */
    public void setCacheLock(String key,String val,String expiration)
    {
        Cache.set(reviseKey(key), val, expiration);
    }
    
    /** 解锁 */
    public void unLock(String key) {
            records.remove(key);
    }

    /** 解锁 */
    public void cacheUnLock(String key) {
        //缓存锁
        Cache.delete(reviseKey(key));
    }
    
    public void clearCacheLock(Collection<String> keys) {
        if (null == keys || keys.isEmpty()) {
            return;
        }
        for (String key : keys) {
            try {
                //缓存锁
                Cache.delete(reviseKey(key));
            } catch (Exception e) {
                //ignore
            }
        }
    }
    
    
}