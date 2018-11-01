package util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.google.gson.GsonBuilder;

import jws.Jws;
import jws.Logger;
import jws.http.Request;
import jws.http.Response;
import jws.http.sf.HTTP;
import modules.shop.service.WXAccessTokenService;

public class WxQRCodeUtil {

    /**
     * @param appid
     * @param scene
     * @param path
     * @param cos   1=图片保存到腾讯云 其他=保存到阿里云
     * @return
     */
    public static String genRQCode(String appid, Map<String, String> scene, String path, int cos) {
        InputStream is = null;
        OutputStream os = null;
        try {

            String token = WXAccessTokenService.fromCache(appid);
            if (StringUtils.isEmpty(token)) {
                Logger.error("get weixin access token empty!");
                return null;
            }

            Map<String, Object> params = new HashMap<String, Object>();

            if (scene != null) {
                StringBuffer sb = new StringBuffer();
                for (Map.Entry<String, String> entry : scene.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    sb.append(key).append("=").append(value).append("&");
                }
                params.put("scene", sb.substring(0, sb.lastIndexOf("&")));
            } else {
                params.put("scene", "1");
            }

            Request request = new Request("wx", "codeunlimit", "?access_token=" + token);
            GsonBuilder gb = new GsonBuilder();
            gb.disableHtmlEscaping();
            params.put("page", path);
            //params.put("auto_color", "false");
            //params.put("line_color", "{\"r\":\"216\",\"g\":\"63\",\"b\":\"88\"}");


            String bodyStr = gb.create().toJson(params);

            Logger.info("post qrcode params %s", bodyStr);

            request.setBody(bodyStr.getBytes("utf-8"));
            Response response = HTTP.POST(request);
            is = response.getContentAsStream();


            File outFileDir = new File(Jws.applicationPath.getAbsolutePath() + File.separator + "qcodertmp");
            if (!outFileDir.exists()) {
                outFileDir.mkdirs();
            }
            String dirPath = outFileDir.getAbsolutePath();
            String tmpName = MD5.md5(bodyStr, "utf-8") + ".png";
            File qrcodeFile = new File(dirPath + File.separator + tmpName);


            os = new FileOutputStream(qrcodeFile);
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = is.read(buffer, 0, 1024)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();

            String osskey = "";
            if (cos == 1) {
                osskey = API.uploadImageToTencent(qrcodeFile, 200, 0.9f);
            } else {
                osskey = API.uploadImage(qrcodeFile, 200, 0.9f);
            }

            qrcodeFile.delete();

            return osskey;

        } catch (Exception e) {
            Logger.error(e, "");
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                Logger.error(e, "");
            }
        }
        return "";
    }
}
