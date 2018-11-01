package modules.shop.ddl;

import jws.dal.annotation.Column;
import jws.dal.annotation.GeneratedValue;
import jws.dal.annotation.GenerationType;
import jws.dal.annotation.Id;
import jws.dal.annotation.Table;
import jws.dal.common.DbType;

/**
 * @author auto
 * @createDate 2018-04-20 10:46:58
 **/
@Table(name = "shop_wetao_comment")
public class ShopWetaoCommentDDL {
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

    @Column(name = "wetao_id", type = DbType.Int)
    private Integer wetaoId;

    public Integer getWetaoId() {
        return wetaoId;
    }

    public void setWetaoId(Integer wetaoId) {
        this.wetaoId = wetaoId;
    }

    @Column(name = "comment", type = DbType.Varchar)
    private String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Column(name = "nick_name", type = DbType.Varchar)
    private String nickName;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Column(name = "user_id", type = DbType.Int)
    private Integer userId;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Column(name = "avatar", type = DbType.Varchar)
    private String avatar;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Column(name = "client_ip", type = DbType.Varchar)
    private String clientIp;

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
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

    public static ShopWetaoCommentDDL newExample() {
        ShopWetaoCommentDDL object = new ShopWetaoCommentDDL();
        object.setId(null);
        object.setWetaoId(null);
        object.setComment(null);
        object.setNickName(null);
        object.setUserId(null);
        object.setAvatar(null);
        object.setClientIp(null);
        object.setDeleted(null);
        object.setCreateTime(null);
        return object;
    }
}
