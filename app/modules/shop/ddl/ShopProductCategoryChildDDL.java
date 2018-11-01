package modules.shop.ddl;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;

/**
 * @author auto
 * @createDate 2017-12-11 11:36:30
 **/
@Table(name = "shop_product_category_child")
public class ShopProductCategoryChildDDL {
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

    @Column(name = "category_id", type = DbType.Varchar)
    private String categoryId;

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    @Column(name = "p_cagegory_id", type = DbType.Varchar)
    private String pCagegoryId;

    public String getPCagegoryId() {
        return pCagegoryId;
    }

    public void setPCagegoryId(String pCagegoryId) {
        this.pCagegoryId = pCagegoryId;
    }

    @Column(name = "category_name", type = DbType.Varchar)
    private String categoryName;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public static ShopProductCategoryChildDDL newExample() {
        ShopProductCategoryChildDDL object = new ShopProductCategoryChildDDL();
        object.setId(null);
        object.setCategoryId(null);
        object.setPCagegoryId(null);
        object.setCategoryName(null);
        return object;
    }
}
