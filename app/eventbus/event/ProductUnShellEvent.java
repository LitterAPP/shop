package eventbus.event;

import org.apache.commons.lang.StringUtils;

import com.google.common.eventbus.Subscribe;
import com.google.gson.Gson;

import eventbus.event.params.ShopIndexProChangedParams;
import jws.Logger;
import jws.dal.Dal;
import modules.shop.ddl.ShopIndexDDL;
import modules.shop.service.ShopIndexService;

public class ProductUnShellEvent {

    @Subscribe
    public void shopIndexNavChanged(ShopIndexProChangedParams params) {
        Logger.info("recv msg -> %s", new Gson().toJson(params));

        ShopIndexDDL shopIdex = ShopIndexService.getByShopId(params.getShopId());
        String config = shopIdex.getConfig();
        if (StringUtils.isEmpty(config)) {
            return;
        }
        config = config.replaceAll(params.getOldProductId(), params.getProductId());
        shopIdex.setConfig(config);
        Dal.replace(shopIdex);
    }
}
