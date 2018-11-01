package modules.shop.ddl;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;

/**
 * @author auto
 * @createDate 2017-12-11 11:36:08
 **/
@Table(name = "shop_product_attr")
public class ShopProductAttrDDL {
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

    public static ShopProductAttrDDL newExample() {
        ShopProductAttrDDL object = new ShopProductAttrDDL();
        object.setId(null);
        object.setAttrId(null);
        object.setAttrName(null);
        return object;
    }
}
