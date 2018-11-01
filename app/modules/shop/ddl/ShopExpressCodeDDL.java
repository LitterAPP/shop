package modules.shop.ddl;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;

/**
 * @author auto
 * @createDate 2017-12-21 10:22:09
 **/
@Table(name = "shop_express_code")
public class ShopExpressCodeDDL {
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

    @Column(name = "shipper_name", type = DbType.Varchar)
    private String shipperName;

    public String getShipperName() {
        return shipperName;
    }

    public void setShipperName(String shipperName) {
        this.shipperName = shipperName;
    }

    @Column(name = "shipper_code", type = DbType.Varchar)
    private String shipperCode;

    public String getShipperCode() {
        return shipperCode;
    }

    public void setShipperCode(String shipperCode) {
        this.shipperCode = shipperCode;
    }

    public static ShopExpressCodeDDL newExample() {
        ShopExpressCodeDDL object = new ShopExpressCodeDDL();
        object.setId(null);
        object.setShipperName(null);
        object.setShipperCode(null);
        return object;
    }
}
