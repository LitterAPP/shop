package controllers;

import jws.Logger;
import jws.mvc.Controller;
import modules.common.service.FormIdService;
import modules.shop.ddl.UsersDDL;
import modules.shop.service.UserService;
import util.RtnUtil;

public class FormId extends Controller {

    public static void collect(String session, String appId, String formId) {
        try {
            UsersDDL user = UserService.findBySession(session);
            if (user == null) {
                renderJSON(RtnUtil.returnLoginFail());
            }
            FormIdService.addFormId(appId, user.getOpenId(), user.getId().intValue(), formId);
            renderJSON(RtnUtil.returnSuccess("OK"));
        } catch (Exception e) {
            Logger.error(e, e.getMessage());
            renderJSON(RtnUtil.returnFail(e.getMessage()));
        }

    }
}
