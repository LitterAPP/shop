package dto.shop;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class ShopIndexDto {
	public int shopId;
	public String shopAvatar;
	public String shopAvatarKey;
	
	public String contactMobile;
	public String contactWx;
	
	public String activityBg;
	public String activityBgKey;
	public String activityText;
	
	public String shopBanner;
	public String shopBannerKey;
	public String shopName; 	
	public String wellcomeText="欢迎来撩我们！";
	public List<ShopNavDto> firstNavList = new ArrayList<ShopNavDto>();
	public List<ShopNavDto> secondNavList = new ArrayList<ShopNavDto>();
	public List<ShopNavDto> swiperList = new ArrayList<ShopNavDto>();
	public List<ShopNavDto> thirdNavList = new ArrayList<ShopNavDto>();
	public List<ShopNavDto> fourthNavList = new ArrayList<ShopNavDto>();
	public List<ShopNavDto> fiveNavList = new ArrayList<ShopNavDto>();
	
	public List<ShopNavWrap> shopNavWrapList = new ArrayList<ShopNavWrap>();;//灵活布局
	
	public class ShopNavWrap{
		public int layout;//1=一行一个，2=一行2个流式布局
		public int sort;//排序，越大越靠前
		public String title;
		public String color;
		public int fontSize;
		public String position;
		public List<ShopNavDto> list = new ArrayList<ShopNavDto>();
	}
	
}
