package moudles.gae.assist;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 奖品
 * 
 * @author caixb
 *
 */
public class Prize {

	private int id; //奖品id
	private int beans; //奖品金额
	private int probability; //奖品中奖概率
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getBeans() {
		return beans;
	}
	public void setBeans(int beans) {
		this.beans = beans;
	}
	public BigDecimal getProbability() {
		DecimalFormat df = new DecimalFormat("0.00");   
        return new BigDecimal(df.format(this.probability/100d));
	}
	public void setProbability(int probability) {
		this.probability = probability;
	}
}
