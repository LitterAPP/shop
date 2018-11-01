package modules.shop.ddl;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;

/**
 * @author auto
 * @createDate 2017-12-25 16:03:31
 **/
@Table(name = "shop_product_attr_rel")
public class ShopProductAttrRelDDL {
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

    @Column(name = "product_id", type = DbType.Varchar)
    private String productId;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    @Column(name = "attr_id", type = DbType.Varchar)
    private String attrId;

    public String getAttrId() {
        return attrId;
    }

    public void setAttrId(String attrId) {
        this.attrId = attrId;
    }

    @Column(name = "attr_name", type = DbType.Varchar)
    private String attrName;

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    @Column(name = "order_by", type = DbType.Int)
    private Integer orderBy;

    public Integer getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(Integer orderBy) {
        this.orderBy = orderBy;
    }

    public static ShopProductAttrRelDDL newExample() {
        ShopProductAttrRelDDL object = new ShopProductAttrRelDDL();
        object.setId(null);
        object.setProductId(null);
        object.setAttrId(null);
        object.setAttrName(null);
        object.setOrderBy(null);
        return object;
    }
}
