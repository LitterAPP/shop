package modules.shop.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import jws.dal.sqlbuilder.Sort;
import modules.shop.ddl.ShopProductAttrDDL;
import modules.shop.ddl.ShopProductAttrRelDDL;
import modules.shop.service.dto.AutoCompleteDto;
import util.IDUtil;

public class ShopProductAttrService {

    public static List<ShopProductAttrRelDDL> listByProduct(String productId) {
        Condition cond = new Condition("ShopProductAttrRelDDL.productId", "=", productId);
        return Dal.select("ShopProductAttrRelDDL.*", cond, new Sort("ShopProductAttrRelDDL.orderBy", true), 0, -1);
    }

    public static ShopProductAttrRelDDL get(int id) {
        return Dal.select("ShopProductAttrRelDDL.*", id);
    }


    public static ShopProductAttrDDL createAttr(String name) {
        ShopProductAttrDDL old = getByName(name);
        if (old != null) {
            return old;
        }
        ShopProductAttrDDL attr = new ShopProductAttrDDL();
        attr.setAttrId(IDUtil.gen("ATTR"));
        attr.setAttrName(name);
        int id = (int) Dal.insertSelectLastId(attr);
        attr.setId(id);
        return attr;
    }

    public static ShopProductAttrDDL getByAttrId(String attrId) {
        Condition condition = new Condition("ShopProductAttrDDL.attrId", "=", attrId);
        List<ShopProductAttrDDL> list = Dal.select("ShopProductAttrDDL.*", condition, null, 0, 1);
        if (list == null || list.size() == 0) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public static ShopProductAttrDDL getByName(String name) {
        Condition condition = new Condition("ShopProductAttrDDL.attrName", "=", name);
        List<ShopProductAttrDDL> list = Dal.select("ShopProductAttrDDL.*", condition, null, 0, 1);
        if (list == null || list.size() == 0) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public static List<ShopProductAttrDDL> searchByName(String name) {
        if (!StringUtils.isEmpty(name)) {
            Condition condition = new Condition("ShopProductAttrDDL.attrName", "like", "'%" + name + "%'");
            List<ShopProductAttrDDL> list = Dal.select("ShopProductAttrDDL.*", condition, null, 0, -1);
            return list;
        } else {
            List<ShopProductAttrDDL> list = Dal.select("ShopProductAttrDDL.*", null, null, 0, -1);
            return list;
        }

    }

    public static boolean delAttrRelByProductId(String productId) {
        return Dal.delete(new Condition("ShopProductAttrRelDDL.productId", "=", productId)) > 0;
    }

    public static List<AutoCompleteDto> listAttrs() {
        List<AutoCompleteDto> result = new ArrayList<AutoCompleteDto>();
        List<ShopProductAttrDDL> list = Dal.select("ShopProductAttrDDL.*", null, null, 0, -1);
        if (list != null && list.size() > 0) {
            for (ShopProductAttrDDL attr : list) {
                AutoCompleteDto one = new AutoCompleteDto();
                one.data = attr.getAttrId();
                one.value = attr.getAttrName();
                result.add(one);
            }
        }
        return result;
    }
}
