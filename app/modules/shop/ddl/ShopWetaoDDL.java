package modules.shop.ddl;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;

/**
 * @author auto
 * @createDate 2018-04-25 18:01:04
 **/
@Table(name = "shop_wetao")
public class ShopWetaoDDL {
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

    @Column(name = "content", type = DbType.Varchar)
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Column(name = "images", type = DbType.Varchar)
    private String images;

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    @Column(name = "seo_title", type = DbType.Varchar)
    private String seoTitle;

    public String getSeoTitle() {
        return seoTitle;
    }

    public void setSeoTitle(String seoTitle) {
        this.seoTitle = seoTitle;
    }

    @Column(name = "seo_desc", type = DbType.Varchar)
    private String seoDesc;

    public String getSeoDesc() {
        return seoDesc;
    }

    public void setSeoDesc(String seoDesc) {
        this.seoDesc = seoDesc;
    }

    @Column(name = "seo_key", type = DbType.Varchar)
    private String seoKey;

    public String getSeoKey() {
        return seoKey;
    }

    public void setSeoKey(String seoKey) {
        this.seoKey = seoKey;
    }

    @Column(name = "view", type = DbType.Int)
    private Integer view;

    public Integer getView() {
        return view;
    }

    public void setView(Integer view) {
        this.view = view;
    }

    @Column(name = "zan", type = DbType.Int)
    private Integer zan;

    public Integer getZan() {
        return zan;
    }

    public void setZan(Integer zan) {
        this.zan = zan;
    }

    @Column(name = "comment", type = DbType.Int)
    private Integer comment;

    public Integer getComment() {
        return comment;
    }

    public void setComment(Integer comment) {
        this.comment = comment;
    }

    @Column(name = "shopping_guide", type = DbType.Varchar)
    private String shoppingGuide;

    public String getShoppingGuide() {
        return shoppingGuide;
    }

    public void setShoppingGuide(String shoppingGuide) {
        this.shoppingGuide = shoppingGuide;
    }

    @Column(name = "deleted", type = DbType.Int)
    private Integer deleted;

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    @Column(name = "create_time", type = DbType.DateTime)
    private Long createTime;

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public static ShopWetaoDDL newExample() {
        ShopWetaoDDL object = new ShopWetaoDDL();
        object.setId(null);
        object.setShopId(null);
        object.setContent(null);
        object.setImages(null);
        object.setSeoTitle(null);
        object.setSeoDesc(null);
        object.setSeoKey(null);
        object.setView(null);
        object.setZan(null);
        object.setComment(null);
        object.setShoppingGuide(null);
        object.setDeleted(null);
        object.setCreateTime(null);
        return object;
    }
}
