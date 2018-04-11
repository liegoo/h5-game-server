package utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jws.libs.Codec;

import org.apache.commons.lang.StringUtils;

import constants.GlobalConstants;

public class CommonUtil {
    private static final String BLANK_CHAR = "　";
    private static final DecimalFormat decimalF = new DecimalFormat("###.");
    private static final DecimalFormat decimalF2 = new DecimalFormat("###.##");
    private static final DecimalFormat decimalF3 = new DecimalFormat("###");
    private static char[] PASS_ARRAY = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
        'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1',
        '2', '3', '4', '5', '6', '7', '8', '9' };
    
    /**
     * 自动补全对应的字符
     * @param str
     * @param len
     * @return
     */
    public static String formatBlankStr(String str,int len)
    {
        StringBuilder sb = new StringBuilder();
        if(StringUtils.isEmpty(str))
        {
            for(int i = 0;i<len;i++)
            {
                sb.append(BLANK_CHAR);
            }
            return sb.toString();
        }
        
        int remainLen = len-str.length();
        if(remainLen < 1)
        {
            return str;
        }
        sb.append(str);
        for(int i = 0;i<remainLen;i++)
        {
            sb.append(BLANK_CHAR);
        }
        return sb.toString();
    }
    
    public static float formatFloat(float val)
    {
        try {
            String num =  decimalF.format(val);
            return Float.parseFloat(num);
        } catch (Exception e) {
            //igore
        }
        return val;
    }
    
    public static float formatFloat2(float val)
    {
        try {
            String num =  decimalF2.format(val);
            return Float.parseFloat(num);
        } catch (Exception e) {
            //igore
        }
        return val;
    }
    
    
    public static int parseIntVal(String val,int defVal)
    {
        try {
            return Integer.parseInt(val);
        } catch (Exception e) {
            //igore
        }
        return defVal;
    }
    
    public static long parseLongVal(String val,long defVal)
    {
        try {
            return Long.parseLong(val);
        } catch (Exception e) {
            //igore
        }
        return defVal;
    }
    
    public static int calcPercent(int num,int totalNum)
    {
         float rst = (num*100f)/totalNum;
         return Integer.parseInt(decimalF3.format(rst));
    }
    
    /**
     * 简单的将数组数据打乱
     * @return
     */
    public static int[] upsetArray(int[] vals)
    {
        if(null == vals || vals.length < 1)
        {
            return vals;
        }
        Map<String,List<Integer>> tmpMap = new HashMap<String,List<Integer>>();  
        Random random = new Random();
        for(int val:vals)
        {
            String key = random.nextInt(100) + String.valueOf(IdGenerator2.getBasicId());
            List<Integer> tmpLst = tmpMap.get(key);
            if(null == tmpLst)
            {
                tmpLst = new ArrayList<Integer>();
                tmpMap.put(key, tmpLst);
            }
            tmpLst.add(Integer.valueOf(val));
        }
        int[] rstVal = new int[vals.length];
        int i = 0;
        for(List<Integer> valLst :tmpMap.values())
        {
            if(i > rstVal.length-1){
                break;
            }
            
            for(Integer ele:valLst)
            {
                rstVal[i] = ele.intValue();
                i++;
            }
        }
        return rstVal;
    }
    
    /**
     * 生成随机密码
     *
     * @param pwd_len 生成的密码的总长度
     * @return 密码的字符串
     */
    public static String genRandomPass(int pwd_len) {
        if (pwd_len < 1 || pwd_len > 10) {
            pwd_len = 6;
        }
        final int maxNum = PASS_ARRAY.length;
        int i; // 生成的随机数
        int count = 0; // 生成的密码的长度
        StringBuilder pwd = new StringBuilder("");
        Random random = new Random();
        while (count < pwd_len) {
            // 生成随机数，取绝对值，防止生成负数，
            i = Math.abs(random.nextInt(maxNum)); // 生成的数最大为36-1
            if (i >= 0 && i < maxNum) {
                pwd.append(PASS_ARRAY[i]);
                count++;
            }
        }
        return pwd.toString();
    }
    
    /**
     * 判断是否与豌豆荚绑定
     * @param ve
     * @return
     * @author surong, 2016年3月2日.
     */
    public static boolean isWdjCombinedSDK(String ve) {
		if (StringUtils.isBlank(ve)) {
			return false;
		}
		int pos = -1;
		if ((pos = StringUtils.indexOf(ve, '/')) != StringUtils.INDEX_NOT_FOUND) {
			String flag = StringUtils.substring(ve, pos + 1);
			if (GlobalConstants.WDJ_COMBINED_SDK_FLAG.equalsIgnoreCase(flag)) {
				return true;
			}
		}
		
		return false;
	}
    /**
	 * 将list的数组转换为二维数组，主要用于Db.batch()，批量处理
	 * 
	 * @param list
	 * @return
	 */
	public static Object[][] listTo2Array(List<Object[]> list){
		if(list.size() == 0){
			return null;
		}
		int size = list.get(0).length;
		Object[][] paramsArr = new Object[list.size()][size];
		for(int i=0;i<list.size();i++){
			paramsArr[i] = list.get(i);
		}
		return paramsArr;
	}
    
}
