package modules.shop.ddl;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;

/**
 * @author auto
 * @createDate 2017-12-15 10:32:08
 **/
@Table(name = "shop_product_community_rel")
public class ShopProductCommunityRelDDL {
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

    @Column(name = "community_id", type = DbType.Varchar)
    private String communityId;

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    @Column(name = "community_name", type = DbType.Varchar)
    private String communityName;

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    @Column(name = "product_id", type = DbType.Varchar)
    private String productId;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    @Column(name = "create_time", type = DbType.DateTime)
    private Long createTime;

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public static ShopProductCommunityRelDDL newExample() {
        ShopProductCommunityRelDDL object = new ShopProductCommunityRelDDL();
        object.setId(null);
        object.setCommunityId(null);
        object.setCommunityName(null);
        object.setProductId(null);
        object.setCreateTime(null);
        return object;
    }
}
