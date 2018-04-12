package controllers.dto;

import java.util.ArrayList;
import java.util.List;

public class ProductInfoDto {

	public int productType;
	public int store;
	public String productId;
	public String title;
	public String p_category_id;
	public String sub_category_id;
	public String attrs_1;
	public String attrs_2;
	public String attrs_3;
	public List<Image> play_pics;
	public Image banner_pic;
	public String[] price = new String[2];
	public boolean join_together;
	public TogetherInfo together_info;
	public List<Group> groups;
	public String contact_mobile;
	public String contact_wx;
	public List<TextDetail> text_details;
	public List<Image> pic_details;
	//商品关联多个分类分组
	public List<Category> selectedCategoryParams ;
	public List<String> selectedAttrs;
	public boolean isHot;
	public boolean isSale;

	
	
	public class TogetherInfo{
		public String price;
		public int num;
		public int hour;
		public int vcount;		
	}
	public class Group{
		public String title;
		public String price1;
		public String price2;
		public String logo;
		public String remoteUrl;
		public String osskey;
	}
	
	public class TextDetail{
		public String value;
	}
	
	public class Image{
		public String localUrl;
		public String remoteUrl;
		public String osskey;
	}
	
	public class Category{
		public String pCategoryId;
		public String subCategoryId;
	}
}
