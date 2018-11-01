package modules.shop.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import modules.shop.ddl.ShopProductImagesDDL;

public class ShopProductImageService {
    public static final int SCREENSHOT_TYPE = 1;
    public static final int DETAIL_TYPE = 2;
    public static final int BUYER_SHOW = 3;

    public static List<ShopProductImagesDDL> listImages(String productId, int type, int page, int pageSize) {
        if (StringUtils.isEmpty(productId)) {
            return null;
        }
        page = page == 0 ? 1 : page;
        pageSize = pageSize == 0 ? 5 : pageSize;
        Condition cond = new Condition("ShopProductImagesDDL.productId", "=", productId);
        cond.add(new Condition("ShopProductImagesDDL.type", "=", type), "and");
        return Dal.select("ShopProductImagesDDL.*", cond, null, (page - 1) * pageSize, pageSize);
    }

    public static boolean delImageByProductIdAndType(String productId, int type) {
        Condition cond = new Condition("ShopProductImagesDDL.productId", "=", productId);
        cond.add(new Condition("ShopProductImagesDDL.type", "=", type), "and");
        return Dal.delete(cond) > 0;
    }
}
