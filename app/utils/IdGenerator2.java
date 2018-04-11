package utils;

import java.net.NetworkInterface;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import jws.Logger;
import sun.misc.CRC16;

/**
 * 全局唯一ID生成器
 * 1）支持跨机房
 * 2）支持多个机器（同机房，部署多个机器）
 * 系统时间戳（10位） + 应用唯一ID（3位）+自增随机数（6位）
 * 1）系统时间戳精确至秒级别；
 * 2）应用唯一ID：
 *     2.1）支持配置，由"机房ID_机器ID"组成 ，例如：1_01，表示上海机房的01号机器
 *     2.2）如果未人工干预配置，则取当前运行应用所在的机器MAC地址，通过循环冗余校验算法(CRC)生成唯一的ID
 * 3）在上述两步骤的基础上，再次添加自增随机数（使用JDK并发包的AtomicInteger ），防止单个应用的高并发场景
 *  
 * 下面是一个生成后的ID示例：
 * 1401154783101999389
 * 1400920291是10位时间戳
 * 101是应用唯一ID
 * 999389是自增随机码
 *  
 * 通过上述算法，可以保证同一时间点，不同机房不同物理机器上部署的相同应用，生成的对应的ID唯一。
 *
 */
public class IdGenerator2 {
    public static final String CLUSTER_APPID_KEY = "cluster.appid";
    private static final Charset utf8Charset = Charset.forName("utf-8");
    private static Pattern ptn = Pattern.compile("([0-9]{1})_([0-9]{1,2})");
    private static final AtomicInteger atomicId = new AtomicInteger(0);
    private static final int APP_ID_INC = 1000000;
    private static int appId = 101 * APP_ID_INC;
    static {
        initAppId("");
    }
    /*
     *根据配置的ID，做解析，配置示例：
     *appId=IdcId_HostId，
     *例如：appId=1_01,appId=1_02;appId=2_01,appId=2_02;
     * */
    public static void initAppId(String cfgAppId) {
        appId = parseAppId(cfgAppId);
        if (0 == appId) {
            appId = generateRandId();
        }
        Logger.warn("IdGenerator: APP-ID: %d", appId);
    }

    private static int parseAppId(String cfgAppId) {
        try {
            if (StringUtils.isEmpty(cfgAppId)) {
                return 0;
            }

            Matcher matcher = ptn.matcher(cfgAppId);
            if (matcher.find()) {
                String idcId = matcher.group(1);
                int nIdcId = Integer.parseInt(idcId);
                String hostId = matcher.group(2);
                int nHostId = Integer.parseInt(hostId);
                int appId = nIdcId * 100 + nHostId;
                return appId * APP_ID_INC;
            }
        } catch (Exception e) {
            //ignore
        }
        return 0;
    }

    private static int generateRandId() {
        String mac = UUID.randomUUID().toString();
        try {
            String tmpMac = getMacAddress();
            if (null != tmpMac) {
                mac = tmpMac;
            }
        } catch (Exception e) {
            //ignore
        }
        int tmpRst = getChecksum(mac);
        if (tmpRst < 999 && tmpRst > 0) {
            return tmpRst * APP_ID_INC;
        }
        //大于999，取余数
        int mod = tmpRst % 999;
        if (mod == 0) {
            //不允许取0
            mod = 1;
        }
        return mod * APP_ID_INC;
    }

    private static String getMacAddress() throws Exception {
        Enumeration<NetworkInterface> ni = NetworkInterface.getNetworkInterfaces();
        while (ni.hasMoreElements()) {
            NetworkInterface netI = ni.nextElement();
            if (null == netI) {
                continue;
            }
            byte[] macBytes = netI.getHardwareAddress();
            if (netI.isUp() && !netI.isLoopback() && null != macBytes && macBytes.length == 6) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0, nLen = macBytes.length; i < nLen; i++) {
                    byte b = macBytes[i];
                    //与11110000作按位与运算以便读取当前字节高4位  
                    sb.append(Integer.toHexString((b & 240) >> 4));
                    //与00001111作按位与运算以便读取当前字节低4位  
                    sb.append(Integer.toHexString(b & 15));
                    if (i < nLen - 1) {
                        sb.append("-");
                    }
                }
                return sb.toString().toUpperCase();
            }
        }
        return null;
    }

    /**
     * 获取对应的CRC16校验码
     * @param input 待校验的字符串
     * @return 返回对应的校验和
     */
    private static int getChecksum(String input) {
        if (null == input) {
            return 0;
        }
        byte[] data = input.getBytes(utf8Charset);
        CRC16 crc16 = new CRC16();
        for (byte b : data) {
            crc16.update(b);
        }
        return crc16.value;
    }

    public static Long getId() {
        long id = getBasicId();
        return Long.valueOf(id);
    }

    public static long getBasicId() {
        return (System.currentTimeMillis() / 1000) * 1000000000 + appId + getRandNum();
    }

    /**
     * 获取本机的标示
     * @return
     */
    public static int getHostTag()
    {
        return appId/APP_ID_INC;
    }
    
    /**
     * 获取随机数,加大随机数位数，是为了防止高并发，且单个并发中存在循环获取ID的场景
     * 如果您的应用并发有200以上，且每个并发中都存在循环调用获取ID的场景，可能会发生ID冲突
     * 对应的解决方法是：在循环逻辑中加入休眠1-5ms的逻辑
     * @return
     */
    private static int getRandNum() {
        int num = atomicId.getAndIncrement();
        if (num >= 999999) {
            atomicId.set(0);
            return atomicId.getAndIncrement();
        }
        return num;
    }
    
}
