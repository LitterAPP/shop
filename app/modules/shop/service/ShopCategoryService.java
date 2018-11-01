package modules.shop.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import common.GlobalConstants;
import controllers.dto.SelectSourceDto;
import jws.Logger;
import jws.cache.Cache;
import jws.dal.Dal;
import jws.dal.sqlbuilder.Condition;
import jws.dal.sqlbuilder.Sort;
import modules.shop.ddl.ShopProductCategoryChildDDL;
import modules.shop.ddl.ShopProductCategoryDDL;
import modules.shop.ddl.ShopProductCategoryRelDDL;
import util.IDUtil;
import util.ThreadUtil;

public class ShopCategoryService {

    public static ShopProductCategoryRelDDL get(int id) {
        return Dal.select("ShopProductCategoryRelDDL.*", id);
    }

    public static List<ShopProductCategoryRelDDL> listByProductId(String productId) {
        Condition condition = new Condition("ShopProductCategoryRelDDL.productId", "=", productId);
        return Dal.select("ShopProductCategoryRelDDL.*", condition, null, 0, -1);
    }

    public static void deleteCategoryRel(String productId) {
        Condition condition = new Condition("ShopProductCategoryRelDDL.productId", "=", productId);
        Dal.delete(condition);
    }

    public static List<ShopProductCategoryRelDDL> listByCategory(String pCategoryId, String subCategoryId) {
        if (StringUtils.isEmpty(pCategoryId) && StringUtils.isEmpty(subCategoryId)) {
            return null;
        }
        if (StringUtils.isEmpty(subCategoryId)) {
            Condition condition = new Condition("ShopProductCategoryRelDDL.pCategoryId", "=", pCategoryId);
            return Dal.select("ShopProductCategoryRelDDL.*", condition, null, 0, -1);
        } else {
            Condition condition = new Condition("ShopProductCategoryRelDDL.subCategoryId", "=", subCategoryId);
            return Dal.select("ShopProductCategoryRelDDL.*", condition, null, 0, -1);
        }
    }

    public static ShopProductCategoryDDL createPCategory(String shopId, String pCategoryName) {
        ShopProductCategoryDDL p = new ShopProductCategoryDDL();
        p.setCategoryId(IDUtil.gen("CAT"));
        p.setCategoryName(pCategoryName);
        p.setShopId(shopId);
        ShopProductCategoryDDL maxSortP = getMaxSortPCategory();
        p.setOrderBy(maxSortP == null ? 0 : (maxSortP.getOrderBy() + 1));
        int id = (int) Dal.insertSelectLastId(p);
        p.setId(id);

        return p;
    }

    public static ShopProductCategoryDDL getMaxSortPCategory() {
        List<ShopProductCategoryDDL> list = Dal.select("ShopProductCategoryDDL.*", null, new Sort("ShopProductCategoryDDL.orderBy", false), 0, 1);
        if (list == null || list.size() == 0) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public static void updatePCategoryName(String pid, String newName) throws Exception {
        ShopProductCategoryDDL p = getByPCategoryId(pid);
        if (p == null) {
            throw new Exception("分类不存在");
        }
        p.setCategoryName(newName);
        Dal.update(p, "ShopProductCategoryDDL.categoryName", new Condition("ShopProductCategoryDDL.id", "=", p.getId()));
    }

    public static void updateSubCategoryName(String subId, String newName) throws Exception {
        ShopProductCategoryChildDDL sub = getBySubCategoryId(subId);
        if (sub == null) {
            throw new Exception("分类不存在");
        }
        sub.setCategoryName(newName);
        Dal.update(sub, "ShopProductCategoryChildDDL.categoryName", new Condition("ShopProductCategoryChildDDL.id", "=", sub.getId()));
    }

    public static void changePCategoryOrder(String pid1, String pid2) throws Exception {
        ShopProductCategoryDDL p1 = getByPCategoryId(pid1);
        if (p1 == null) {
            throw new Exception("分类不存在");
        }

        ShopProductCategoryDDL p2 = getByPCategoryId(pid2);
        if (p2 == null) {
            throw new Exception("分类不存在");
        }
        int p1OrderByTmp = p1.getOrderBy();
        int p2OrderByTmp = p2.getOrderBy();
        p1.setOrderBy(p2OrderByTmp);
        p2.setOrderBy(p1OrderByTmp);
        Dal.update(p1, "ShopProductCategoryDDL.orderBy", new Condition("ShopProductCategoryDDL.id", "=", p1.getId()));
        Dal.update(p2, "ShopProductCategoryDDL.orderBy", new Condition("ShopProductCategoryDDL.id", "=", p2.getId()));

    }

    public static void delPCategory(String pCategoryId) {
        //先删除子类
        Condition subDelCond = new Condition("ShopProductCategoryChildDDL.pCagegoryId", "=", pCategoryId);
        Dal.delete(subDelCond);
        //再删除关联分类的商品
        Condition productRelDelCond = new Condition("ShopProductCategoryRelDDL.pCategoryId", "=", pCategoryId);
        Dal.delete(productRelDelCond);
        //再删除一级分类
        Condition parentDelCond = new Condition("ShopProductCategoryDDL.categoryId", "=", pCategoryId);
        Dal.delete(parentDelCond);

    }

    public static void delSubCategory(final String shopId, String subCategoryId) {

        //再删除关联分类的商品
        Condition productRelDelCond = new Condition("ShopProductCategoryRelDDL.subCategoryId", "=", subCategoryId);
        Dal.delete(productRelDelCond);

        Condition subDelCond = new Condition("ShopProductCategoryChildDDL.categoryId", "=", subCategoryId);
        Dal.delete(subDelCond);

        ThreadUtil.sumbit(new Runnable() {
            @Override
            public void run() {
                reflushCategoryALL(shopId, true);
            }
        });
    }

    public static ShopProductCategoryChildDDL createChildCategory(final String shopId, String subCategoryName, String pid) {
        ShopProductCategoryChildDDL sub = new ShopProductCategoryChildDDL();
        sub.setPCagegoryId(pid);
        sub.setCategoryId(IDUtil.gen("CAT_SUB"));
        sub.setCategoryName(subCategoryName);
        int id = (int) Dal.insertSelectLastId(sub);
        sub.setId(id);
        ThreadUtil.sumbit(new Runnable() {
            @Override
            public void run() {
                reflushCategoryALL(shopId, true);
            }
        });
        return sub;
    }

    public static ShopProductCategoryDDL getByPCategoryId(String categoryId) {
        if (StringUtils.isEmpty(categoryId)) {
            return null;
        }
        Condition condition = new Condition("ShopProductCategoryDDL.categoryId", "=", categoryId);
        List<ShopProductCategoryDDL> list = Dal.select("ShopProductCategoryDDL.*", condition, null, 0, 1);
        if (list == null || list.size() == 0) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public static ShopProductCategoryDDL getByPCategoryName(String pCategoryName) {
        Condition condition = new Condition("ShopProductCategoryDDL.categoryName", "=", pCategoryName);
        List<ShopProductCategoryDDL> list = Dal.select("ShopProductCategoryDDL.*", condition, null, 0, 1);
        if (list == null || list.size() == 0) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public static List<ShopProductCategoryDDL> searchByPCategoryName(String shopId, String pCategoryName) {
        Sort sort = new Sort("ShopProductCategoryDDL.orderBy", true);

        Condition condition = new Condition("ShopProductCategoryDDL.id", ">", 0);
        if (!StringUtils.isEmpty(shopId)) {
            condition.add(new Condition("ShopProductCategoryDDL.shopId", "=", shopId), "and");
        }

        if (!StringUtils.isEmpty(pCategoryName)) {
            condition.add(new Condition("ShopProductCategoryDDL.categoryName", "like", "'%" + pCategoryName + "%'"), "and");
        }
        return Dal.select("ShopProductCategoryDDL.*", condition, sort, 0, -1);
    }

    public static ShopProductCategoryChildDDL getBySubCategoryName(String subCategoryName) {
        Condition condition = new Condition("ShopProductCategoryChildDDL.categoryName", "=", subCategoryName);
        List<ShopProductCategoryChildDDL> list = Dal.select("ShopProductCategoryChildDDL.*", condition, null, 0, 1);
        if (list == null || list.size() == 0) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public static ShopProductCategoryChildDDL getBySubCategoryId(String categoryId) {
        if (StringUtils.isEmpty(categoryId)) {
            return null;
        }
        Condition condition = new Condition("ShopProductCategoryChildDDL.categoryId", "=", categoryId);
        List<ShopProductCategoryChildDDL> list = Dal.select("ShopProductCategoryChildDDL.*", condition, null, 0, 1);
        if (list == null || list.size() == 0) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public static List<ShopProductCategoryChildDDL> searchBySubCategoryName(String subCategoryName) {
        if (!StringUtils.isEmpty(subCategoryName)) {
            Condition condition = new Condition("ShopProductCategoryChildDDL.categoryName", "like", "'%" + subCategoryName + "%'");
            List<ShopProductCategoryChildDDL> list = Dal.select("ShopProductCategoryChildDDL.*", condition, null, 0, -1);
            return list;
        }
        return Dal.select("ShopProductCategoryChildDDL.*", null, null, 0, -1);
    }


    public static List<ShopProductCategoryChildDDL> listByParentId(String pCagegoryId) {
        if (!StringUtils.isEmpty(pCagegoryId)) {
            Condition condition = new Condition("ShopProductCategoryChildDDL.pCagegoryId", "=", pCagegoryId);
            List<ShopProductCategoryChildDDL> list = Dal.select("ShopProductCategoryChildDDL.*", condition, null, 0, -1);
            return list;
        }
        return null;
    }

    /**
     * 后台更新完后，传true进入，刷新当前缓存
     * 小程序默认是false，保证永远从cache读取
     *
     * @param force
     * @return
     */
    public static SelectSourceDto reflushCategoryALL(String shopId, boolean force) {

        //加个缓存
        SelectSourceDto selectSourceFromCache = Cache.get(GlobalConstants.CacheKey.CATEGORY_ALL + "_" + shopId, SelectSourceDto.class);
        if (selectSourceFromCache != null && !force) {
            Logger.info("loading category from cache", "");
            return selectSourceFromCache;
        }
        Logger.info("loading category from db", "");
        SelectSourceDto selectSource = new SelectSourceDto();
        selectSource.selected = "";
        List<ShopProductCategoryDDL> pList = ShopCategoryService.searchByPCategoryName(shopId, null);
        if (pList != null && pList.size() > 0) {
            for (ShopProductCategoryDDL pcat : pList) {

                SelectSourceDto.Soruce source = new SelectSourceDto().new Soruce();
                source.value = pcat.getCategoryId();
                source.text = pcat.getCategoryName();

                List<ShopProductCategoryChildDDL> childCats = ShopCategoryService.listByParentId(pcat.getCategoryId());
                if (childCats != null && childCats.size() > 0) {
                    SelectSourceDto subSelectSource = new SelectSourceDto();
                    for (ShopProductCategoryChildDDL child : childCats) {
                        subSelectSource.selected = "0";
                        SelectSourceDto.Soruce subSource = new SelectSourceDto().new Soruce();
                        subSource.value = child.getCategoryId();
                        subSource.text = child.getCategoryName();
                        subSelectSource.options.add(subSource);
                    }
                    source.subCategory = subSelectSource;
                }
                selectSource.options.add(source);
            }
        }
        Cache.set(GlobalConstants.CacheKey.CATEGORY_ALL + "_" + shopId, selectSource, "20d");
        return selectSource;
    }


}
