package utils;

import java.nio.charset.Charset;
import java.util.zip.CRC32;

/**
 * CRC32校验，多线程安全
 * Cyclic Redundancy Check -- 循环冗余校验
 * @author junefsh@warthog.cn
 * @createDate 2015年4月29日
 *
 */
public class Crc32Utils {
    private static final Charset utf8Charset = Charset.forName("utf-8");

    /**
     * 获取对应的CRC32校验码
     * @param input 待校验的字符串
     * @return 返回对应的校验和
     */
    public static long getChecksum(String input) {
        if (null == input) {
            return 0;
        }
        byte[] data = input.getBytes(utf8Charset);
        CRC32 crc32 = new CRC32();
        crc32.update(data);
        return crc32.getValue();
    }
}
