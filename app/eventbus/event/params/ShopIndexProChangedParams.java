package eventbus.event.params;

public class ShopIndexProChangedParams {
	private String shopId;
	private String productId;
	private String oldProductId;
	public ShopIndexProChangedParams(String shopId,String productId,String oldProductId){
		 this.shopId =  shopId;
		 this.productId = productId;
		 this.oldProductId = oldProductId;
	}
	
	public String getShopId() {
		return shopId;
	}
	public void setShopId(String shopId) {
		this.shopId = shopId;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getOldProductId() {
		return oldProductId;
	}
	public void setOldProductId(String oldProductId) {
		this.oldProductId = oldProductId;
	}
	
	
}
