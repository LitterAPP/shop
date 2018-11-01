package modules.shop.ddl;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;

/**
 * @author auto
 * @createDate 2017-12-12 16:55:51
 **/
@Table(name = "shop_product_images")
public class ShopProductImagesDDL {
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

    @Column(name = "image_key", type = DbType.Varchar)
    private String imageKey;

    public String getImageKey() {
        return imageKey;
    }

    public void setImageKey(String imageKey) {
        this.imageKey = imageKey;
    }

    @Column(name = "type", type = DbType.Int)
    private Integer type;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public static ShopProductImagesDDL newExample() {
        ShopProductImagesDDL object = new ShopProductImagesDDL();
        object.setId(null);
        object.setProductId(null);
        object.setImageKey(null);
        object.setType(null);
        return object;
    }
}
