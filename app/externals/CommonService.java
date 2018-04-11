package externals;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Strings;

public class CommonService {
	/**
	 * 根据优惠券名称截取其中的优惠券金额 如：大唐无双20元代金券、50元代金券(7折限量)、10元代金券
	 * 
	 * @param couponName
	 * @return
	 */
	public static Integer getCouponPrice(String couponName) {
		if(Strings.isNullOrEmpty(couponName)){
			return null;
		}
		Pattern p = Pattern.compile("\\d+元");
		Matcher m = p.matcher(couponName);
		if(m.find()){
			String price = m.group().replaceAll("元", "");
			return (int) Math.round(Double.parseDouble(price));
		}
		return null;
	}

	public static void main(String[] args) {
		System.err.println(getCouponPrice("格斗江湖30代金券"));
//		System.err.println(Math.round(Double.parseDouble("050.499")));
	}
}
