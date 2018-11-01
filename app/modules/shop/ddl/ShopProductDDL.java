package modules.shop.ddl;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;

/**
 * @author auto
 * @createDate 2018-05-24 15:58:48
 **/
@Table(name = "shop_product")
public class ShopProductDDL {
    @Id
    @GeneratedValue(generationType = GenerationType.Auto)
    @Column(name = "id", type = DbType.Int)
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "shop_id", type = DbType.Varchar)
    private String shopId;

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    @Column(name = "product_id", type = DbType.Varchar)
    private String productId;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    @Column(name = "product_banner", type = DbType.Varchar)
    private String productBanner;

    public String getProductBanner() {
        return productBanner;
    }

    public void setProductBanner(String productBanner) {
        this.productBanner = productBanner;
    }

    @Column(name = "show_index", type = DbType.Int)
    private Integer showIndex;

    public Integer getShowIndex() {
        return showIndex;
    }

    public void setShowIndex(Integer showIndex) {
        this.showIndex = showIndex;
    }

    @Column(name = "product_name", type = DbType.Varchar)
    private String productName;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    @Column(name = "product_category", type = DbType.Varchar)
    private String productCategory;

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    @Column(name = "product_desc", type = DbType.Varchar)
    private String productDesc;

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    @Column(name = "product_origin_amount", type = DbType.Int)
    private Integer productOriginAmount;

    public Integer getProductOriginAmount() {
        return productOriginAmount;
    }

    public void setProductOriginAmount(Integer productOriginAmount) {
        this.productOriginAmount = productOriginAmount;
    }

    @Column(name = "product_now_amount", type = DbType.Int)
    private Integer productNowAmount;

    public Integer getProductNowAmount() {
        return productNowAmount;
    }

    public void setProductNowAmount(Integer productNowAmount) {
        this.productNowAmount = productNowAmount;
    }

    @Column(name = "join_together", type = DbType.Int)
    private Integer joinTogether;

    public Integer getJoinTogether() {
        return joinTogether;
    }

    public void setJoinTogether(Integer joinTogether) {
        this.joinTogether = joinTogether;
    }

    @Column(name = "together_expir_hour", type = DbType.Int)
    private Integer togetherExpirHour;

    public Integer getTogetherExpirHour() {
        return togetherExpirHour;
    }

    public void setTogetherExpirHour(Integer togetherExpirHour) {
        this.togetherExpirHour = togetherExpirHour;
    }

    @Column(name = "product_together_amount", type = DbType.Int)
    private Integer productTogetherAmount;

    public Integer getProductTogetherAmount() {
        return productTogetherAmount;
    }

    public void setProductTogetherAmount(Integer productTogetherAmount) {
        this.productTogetherAmount = productTogetherAmount;
    }

    @Column(name = "together_number", type = DbType.Int)
    private Integer togetherNumber;

    public Integer getTogetherNumber() {
        return togetherNumber;
    }

    public void setTogetherNumber(Integer togetherNumber) {
        this.togetherNumber = togetherNumber;
    }

    @Column(name = "together_sales", type = DbType.Int)
    private Integer togetherSales;

    public Integer getTogetherSales() {
        return togetherSales;
    }

    public void setTogetherSales(Integer togetherSales) {
        this.togetherSales = togetherSales;
    }

    @Column(name = "store", type = DbType.Int)
    private Integer store;

    public Integer getStore() {
        return store;
    }

    public void setStore(Integer store) {
        this.store = store;
    }

    @Column(name = "seller_user_id", type = DbType.Int)
    private Integer sellerUserId;

    public Integer getSellerUserId() {
        return sellerUserId;
    }

    public void setSellerUserId(Integer sellerUserId) {
        this.sellerUserId = sellerUserId;
    }

    @Column(name = "platform_gets_rate", type = DbType.Int)
    private Integer platformGetsRate;

    public Integer getPlatformGetsRate() {
        return platformGetsRate;
    }

    public void setPlatformGetsRate(Integer platformGetsRate) {
        this.platformGetsRate = platformGetsRate;
    }

    @Column(name = "status", type = DbType.Int)
    private Integer status;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Column(name = "create_time", type = DbType.DateTime)
    private Long createTime;

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    @Column(name = "update_time", type = DbType.DateTime)
    private Long updateTime;

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    @Column(name = "up_time", type = DbType.DateTime)
    private Long upTime;

    public Long getUpTime() {
        return upTime;
    }

    public void setUpTime(Long upTime) {
        this.upTime = upTime;
    }

    @Column(name = "platform_checked", type = DbType.Int)
    private Integer platformChecked;

    public Integer getPlatformChecked() {
        return platformChecked;
    }

    public void setPlatformChecked(Integer platformChecked) {
        this.platformChecked = platformChecked;
    }

    @Column(name = "order_by", type = DbType.Int)
    private Integer orderBy;

    public Integer getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(Integer orderBy) {
        this.orderBy = orderBy;
    }

    @Column(name = "seller_tel_number", type = DbType.Varchar)
    private String sellerTelNumber;

    public String getSellerTelNumber() {
        return sellerTelNumber;
    }

    public void setSellerTelNumber(String sellerTelNumber) {
        this.sellerTelNumber = sellerTelNumber;
    }

    @Column(name = "seller_wx_number", type = DbType.Varchar)
    private String sellerWxNumber;

    public String getSellerWxNumber() {
        return sellerWxNumber;
    }

    public void setSellerWxNumber(String sellerWxNumber) {
        this.sellerWxNumber = sellerWxNumber;
    }

    @Column(name = "product_type", type = DbType.Int)
    private Integer productType;

    public Integer getProductType() {
        return productType;
    }

    public void setProductType(Integer productType) {
        this.productType = productType;
    }

    @Column(name = "pv", type = DbType.Int)
    private Integer pv;

    public Integer getPv() {
        return pv;
    }

    public void setPv(Integer pv) {
        this.pv = pv;
    }

    @Column(name = "deal", type = DbType.Int)
    private Integer deal;

    public Integer getDeal() {
        return deal;
    }

    public void setDeal(Integer deal) {
        this.deal = deal;
    }

    @Column(name = "is_hot", type = DbType.Int)
    private Integer isHot;

    public Integer getIsHot() {
        return isHot;
    }

    public void setIsHot(Integer isHot) {
        this.isHot = isHot;
    }

    @Column(name = "is_sale", type = DbType.Int)
    private Integer isSale;

    public Integer getIsSale() {
        return isSale;
    }

    public void setIsSale(Integer isSale) {
        this.isSale = isSale;
    }

    @Column(name = "join_seckilling", type = DbType.Int)
    private Integer joinSeckilling;

    public Integer getJoinSeckilling() {
        return joinSeckilling;
    }

    public void setJoinSeckilling(Integer joinSeckilling) {
        this.joinSeckilling = joinSeckilling;
    }

    @Column(name = "seckilling_price", type = DbType.Int)
    private Integer seckillingPrice;

    public Integer getSeckillingPrice() {
        return seckillingPrice;
    }

    public void setSeckillingPrice(Integer seckillingPrice) {
        this.seckillingPrice = seckillingPrice;
    }

    @Column(name = "seckilling_time", type = DbType.Int)
    private Integer seckillingTime;

    public Integer getSeckillingTime() {
        return seckillingTime;
    }

    public void setSeckillingTime(Integer seckillingTime) {
        this.seckillingTime = seckillingTime;
    }

    public static ShopProductDDL newExample() {
        ShopProductDDL object = new ShopProductDDL();
        object.setId(null);
        object.setShopId(null);
        object.setProductId(null);
        object.setProductBanner(null);
        object.setShowIndex(null);
        object.setProductName(null);
        object.setProductCategory(null);
        object.setProductDesc(null);
        object.setProductOriginAmount(null);
        object.setProductNowAmount(null);
        object.setJoinTogether(null);
        object.setTogetherExpirHour(null);
        object.setProductTogetherAmount(null);
        object.setTogetherNumber(null);
        object.setTogetherSales(null);
        object.setStore(null);
        object.setSellerUserId(null);
        object.setPlatformGetsRate(null);
        object.setStatus(null);
        object.setCreateTime(null);
        object.setUpdateTime(null);
        object.setUpTime(null);
        object.setPlatformChecked(null);
        object.setOrderBy(null);
        object.setSellerTelNumber(null);
        object.setSellerWxNumber(null);
        object.setProductType(null);
        object.setPv(null);
        object.setDeal(null);
        object.setIsHot(null);
        object.setIsSale(null);
        object.setJoinSeckilling(null);
        object.setSeckillingPrice(null);
        object.setSeckillingTime(null);
        return object;
    }
}
