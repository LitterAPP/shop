package common.script;
/**
 * 可执行脚本的基类，目的是约束构造函数的定义，以保证可以从脚本传递参数
 * @author eason
 *
 */
public abstract class ScriptRunnable implements Runnable {
    public ScriptRunnable(String[] args){
    }
}