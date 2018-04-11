package controllers.external.dto;

public class GetMemberRspDto {

	private String nickname;
	private int suid;
	private int happyBean;
	
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public int getSuid() {
		return suid;
	}
	public void setSuid(int suid) {
		this.suid = suid;
	}
	public int getHappyBean() {
		return happyBean;
	}
	public void setHappyBean(int happyBean) {
		this.happyBean = happyBean;
	}
	
	
}
