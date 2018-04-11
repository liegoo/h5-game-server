package constants;

public enum CouponStatus {

	INIT(1,"初始状态"),TO_AUDIT(2,"待审核"),AUDIT_FAIL(6,"审核失败"),APPROVED(5,"审核通过"),ON_SALE(3,"在售"),ACTED(4,"已激活");
	
	private int status;
	private String name;
	
	private CouponStatus(int status,String name){
		this.status = status;
		this.name= name;
	}

	public int getStatus() {
		return status;
	}

	public String getName() {
		return name;
	}
	
}
