package common;

public class GlobalConstants {
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public final static int DEFAULT_PAGE = 1;
    
    public final static int DEFAULT_PAGE_SIZE = 20;
    
    /**
     * 业务系统ID
     */
    public final static String BIZ_ID = "warthog-biz";
    
    /**
     * 默认编码
     */
    public final static String DEFAULT_CHARSET = "utf-8";
    
    /**
     * JAVA内部解析默认编码
     */
    public final static String JAVA_DEFAULT_CHARSET = "ISO8859-1";
    
    /**
     * 查询接口的最大值，超过这个值，服务器直接不进行处理
     */
    public final static int MAX_LIMIT = 100;
    
    /**
     * 对应数据库中的判断字段
     */
    public final static Integer FALSE = 0;
    
    /**
     * 对应数据库中的判断字段
     */
    public final static Integer TRUE = 1;
    
    /**
     * 男生
     */
    public final static int BOY = 1;
    
    /**
     * 女生
     */
    public final static int GIRL = 2;
    
    /**
     * 默认头像，男生个数
     */
    public final static int DEFAULT_AVATAR_BOY_CNT = 22;
    
    /**
     * 默认头像，女生个数
     */
    public final static int DEFAULT_AVATAR_GIRL_CNT = 25;
    
    /**
     * 默认头像URL模板
     */
    public final static String DEFAULT_AVATAR_TEMPLATE = UPaiYun.ONLINE_SCALABLE_IMG_URL+"/%d/15/07/16/%d.jpg";
    
    
    /**
     * 豌豆荚相关
     */
    public final static  String WDJ_COMBINED_SDK_FLAG = "WDJ";
    
    /**
     * sdk业务标识码
     */
    public static final String SDK_BUSI_CODE = "sdk" ;
    
    /**
     * 游戏相关
     */
    public static enum ADMIN {
        
        /**
         * 游易小助手
         */
        YOUYI_ASSISTANT_ID(10000),

        /**开黑助手*/
        KAIHEI_ASSISTANT_ID(10001),
        
        /**LOL英雄联盟公众号助手*/
        LOL_ASSISTANT_ID(10002),

        /**刀塔传奇公众号*/
        DAOTAO_ASSISTANT_ID(10003);

        private int uid;

        private ADMIN(int uid) {
            this.uid = uid;
        }

        public int uid() {
            return uid;
        }

        /**
         * 判断是否官方帐号
         * @param uid
         * @return
         */
        public static boolean isOfficialAccount(int uid) {
            ADMIN[] admins = values();
            for (ADMIN admin:admins) {
                if (uid == admin.uid) {
                    return true;
                }
            }
            return false;
        }

    }
    
    /**
     * session状态
     * @author caidx
     * 2014年1月3日
     */
    public static class SessionStatus {
        /**
         * session失效
         */
        public static final int INVALID_SESSION = 1;
        
        /**
         * session正常
         */
        public static final int VALID_SESSION = 2;
        
        /**
         * session需要更新
         */
        public static final int NEED_UPDATE_SESSION = 3;
        
    }
    
    /**
     * 客户端默认版本号
     */
    public static final int DEFAULT_VERSION_CODE = 100;
    public static final int CLIENT_1_0_0_VERSION_CODE = 1000000;
    public static final int CLIENT_1_0_1_VERSION_CODE = 1000100;
    public static final int CLIENT_1_1_0_VERSION_CODE = 1001000;
    public static final int CLIENT_1_4_0_VERSION_CODE = 1004000;
    public static final int CLIENT_2_0_0_VERSION_CODE = 2000000;

    public enum PlatformType {

        /**
         * 安卓
         */
        ANDROID("android", 0),
        
        
        /**
         * ios
         */
        IOS("ios", 1);
        
        private Integer value;
        
        private String platformName;
        
        private PlatformType(String platformName, Integer value) {
            this.value = value;
            this.platformName = platformName;
        }
        
        public Integer getValue() {
            return this.value;
        }
        
        public String getPlatformName() {
            return this.platformName;
        }
        
        public static PlatformType getPlatformType(String platformName) {
            for (PlatformType pfType : PlatformType.values()) {
                if (pfType.getPlatformName().equals(platformName.toLowerCase())) {
                    return pfType;
                }
            }
            return null;
        }
    }

    public static class Game{
        public static final String DEFAULT_TIER_ICON = "http://wfiles.b0.upaiyun.com/lol/tier/255.png";
    }
    
    public static class DateFormat {
        //日期
        public static final String ONLY_DATE_FORMAT = "yyyy-MM-dd";
        //24小时制
        public static final String NORMAL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    }
    
    public static class Separator {
        public static final String COMMON_SEPERATOR_COMME = ",";   // 英文逗号
        public static final String COMMON_SEPERATOR_BL = "_";   // 下划线
        public static final String COMMON_SEPERATOR_ML = "-";   // 中划线
        public static final String COMMON_SEPARATOR_SLASH = "/";
        public static final String COMMON_SEPARATOR_COLON = ":";
        public static final String COMMON_SEPARATOR_COLON_CH = "：";
        public static final String COMMON_SEPARATOR_OR = "|";
        public static final String COMMON_SEPARATOR_BLANK = " ";
        public static final String COMMON_SEPARATOR_TAB = "\t";
    }
    public static class UPaiYun{
        public static final String ONLINE_FILE_URL = "http://otherfiles.b0.upaiyun.com";
        public static final String ONLINE_SCALABLE_IMG_URL = "http://scalableimages.b0.upaiyun.com";
    }
    
    public static final String dbSource = "dbbase";
    public static final String dbSoruceCook="cookbook_db";
}
