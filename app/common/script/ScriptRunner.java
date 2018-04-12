package common.script;

import java.io.File;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import common.GlobalConstants;
import jws.Jws;
/**
 * 脚本执行引导程序
 * @author eason
 *
 */
public class ScriptRunner{
    /**
     * 退出码:用于标识成功或失败原因(1-9为系统错误，已占用，请使用10以上的代号)
     * 由于，本脚本运行模式不涉及多线程抢占资源的问题，所以该变量无需做线程同步
     */
    public static int exitCode = 0;
    /**
     * 标识失败代号
     * @param code 1-9为系统错误，已占用，请使用10以上的代号
     */
    public static void markFail(int code){
        exitCode = code;
    }
    
    private static void printHelp()
    {
        System.out.println("Usage: java common.script.ScriptRunner scripClassName scriptArgs");
        //找出当前所有实现了ScriptRunnable的类
        List<Class> allCls = Jws.classloader.getAllClasses();
        if(CollectionUtils.isNotEmpty(allCls))
        {
            StringBuilder sb = new StringBuilder();
            for(Class cls:allCls)   
            {
                if(null == cls)
                {
                    continue;
                }
                
                if (cls.isAnnotation() || cls.isInterface() || cls.isAnonymousClass()
                        || cls.isMemberClass()) {
                    continue;
                }             
                
                if(null == cls.getSuperclass())
                {
                    continue;
                }

                
                if("common.script.ScriptRunnable".equals(cls.getSuperclass().getName()))
                {
                    sb.append(cls.getName()).append(GlobalConstants.LINE_SEPARATOR);
                }
            }
            
            if(sb.length() > 0)
            {
                System.out.println("current implements class:"+GlobalConstants.LINE_SEPARATOR+sb.toString());
            }
        }
    }
    
    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        if (System.getProperty("precompiled", "false").equals("true")) {
            Jws.usePrecompiled = true;
        }
        File root = null;
        if (System.getProperty("application.path") == null){
            root = new File(".");
        }else{
            root = new File(System.getProperty("application.path"));
        }
        String jwsId = "script";
        if (System.getProperty("jws.id") != null){
            jwsId = System.getProperty("jws.id");
        }
        
        // 初始化JWS运行环境
        Jws.init(root, jwsId);
        Jws.start();
       

        if (args.length == 0 || "h".equals(args[0])){
            printHelp();
            failStop(1);
        }
        
        // 加载待运行脚本
        Class runnableClass = null;
        try {
            runnableClass = Jws.classloader.loadClass(args[0]);
        } catch (ClassNotFoundException e) {
            System.out.println(args[0] + "类不存在");
            failStop(2);
        }
        
        // 建立执行线程
        Thread thread = null;
        try {
            // 带给方法的参数列表 (去除args[0])
            String[] methodArgs = new String[args.length-1];

            for (int i=1; i<args.length; i++){
                methodArgs[i-1] = args[i];
            }

            thread = new Thread((Runnable) runnableClass.getConstructor(new Class[]{String[].class}).newInstance(new Object[]{methodArgs}));
        } catch (Exception e) {
        	e.printStackTrace();
            System.out.println("业务Runner类需继承common.script.ScriptRunnable类");
            failStop(3); 
        }
        thread.setContextClassLoader(Jws.classloader);
        thread.start();
        
        // 等待执行线程结束
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            failStop(4);
        }

        stop();
    }
    /**
     * 停止脚本成功的执行，触发退出JWS执行环境
     */
    private static void stop(){
        System.exit(exitCode);
    }
    /**
     * 停止脚本失败的执行，触发退出JWS执行环境
     */
    private static void failStop(int errorCode){
        markFail(errorCode);
        stop();
    }
}
