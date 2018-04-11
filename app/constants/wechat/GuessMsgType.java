package constants.wechat;

public enum GuessMsgType {

	JOIN(1, "已参与"), NOT_HIT(2, "未中奖"), HIT(3, "已中奖"), AWARD_BEAN(4, "参与送豆"), GUESS_AWARD_BEAN(5, "夺宝安慰奖赠送豆");

	private int type;
	private String desc;

	private GuessMsgType(int type, String desc) {
		this.type = type;
		this.desc = desc;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public static GuessMsgType getType(int value) {
		if (value == 0) {
			return null;
		}
		for (GuessMsgType award : GuessMsgType.values()) {
			if (award.type == value) {
				return award;
			}
		}
		return null;
	}

}
