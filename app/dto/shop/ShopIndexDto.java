package dto.shop;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class ShopIndexDto {
	public int shopId;
	public String shopAvatar;
	public String shopAvatarKey;
	
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
	
	public static void main(String[] args){
		ShopIndexDto index = new ShopIndexDto();
		index.shopBanner = "/images/twjx_banner.png";
		
		ShopNavDto one = new ShopNavDto();
		one.sort=1;
		one.type=1;
		one.url="/pages/shop/list";
		one.text="全部宝贝";
		one.linkType = 1;
		index.firstNavList.add(one);
		
		ShopNavDto tow = new ShopNavDto();
		tow.sort=2;
		tow.type=2;
		tow.linkType = 2;
		tow.url="http://www.baidu.com";
		tow.img="/images/twjx_banner.png";
		index.firstNavList.add(tow);
		
		
		ShopNavDto one_sec = new ShopNavDto();
		one_sec.sort=1;
		one_sec.type=1;
		one_sec.url="/pages/shop/list?new=1";
		one_sec.text="NEW";
		one_sec.linkType=1;
		index.secondNavList.add(one_sec);
		
		ShopNavDto tow_sec = new ShopNavDto();
		tow_sec.sort=2;
		tow_sec.type=1;
		tow_sec.url="/pages/shop/list?sale=1";
		tow_sec.text="NEW";
		tow_sec.linkType=1;
		index.secondNavList.add(tow_sec);
		
		ShopNavDto third_sec = new ShopNavDto();
		third_sec.sort=3;
		third_sec.type=1;
		third_sec.url="/pages/shop/list?hot=1";
		third_sec.text="NEW";
		third_sec.linkType=1;
		index.secondNavList.add(third_sec);
		 
		
		
		ShopNavDto third_one = new ShopNavDto();
		third_one.sort=1;
		third_one.type=1;
		third_one.linkType = 2;
		third_one.url="/pages/shop/detail?productId=PRO-20171225154938-173050";
		third_one.img="/images/twjx_banner.png";
		index.thirdNavList.add(third_one);
		
		third_one = new ShopNavDto();
		third_one.sort=1;
		third_one.type=1;
		third_one.linkType = 2;
		third_one.url="/pages/shop/detail?productId=PRO-20171225154938-173050";
		third_one.img="/images/twjx_banner.png";
		index.thirdNavList.add(third_one);
		
		third_one = new ShopNavDto();
		third_one.sort=1;
		third_one.type=1;
		third_one.linkType = 2;
		third_one.url="/pages/shop/detail?productId=PRO-20171225154938-173050";
		third_one.img="/images/twjx_banner.png";
		index.thirdNavList.add(third_one);
		
		third_one = new ShopNavDto();
		third_one.sort=1;
		third_one.type=1;
		third_one.linkType = 2;
		third_one.url="/pages/shop/detail?productId=PRO-20171225154938-173050";
		third_one.img="/images/twjx_banner.png";
		index.thirdNavList.add(third_one);
		
		
		
		ShopNavDto four_one = new ShopNavDto();
		four_one.sort=1;
		four_one.type=1;
		four_one.linkType = 1;
		four_one.url="/pages/shop/category";
		four_one.text="全部分类";
		index.fourthNavList.add(four_one);
		
		four_one = new ShopNavDto();
		four_one.sort=1;
		four_one.type=1;
		four_one.linkType = 1;
		four_one.url="/pages/shop/myspace";
		four_one.text="个人中心";
		index.fourthNavList.add(four_one);
		
		
		String json = new Gson().toJson(index);
		System.out.println(json); 
		
	}
	
}
