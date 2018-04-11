package controllers.external.dto;

public class ConsumeHappyBeanReqDto {

	private String productName;
	private String token;
	private int happyBean;
	
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public int getHappyBean() {
		return happyBean;
	}
	public void setHappyBean(int happyBean) {
		this.happyBean = happyBean;
	}
	
	
}
