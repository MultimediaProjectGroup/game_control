package com.example.jyqiu.multpro;
import java.io.OutputStream;
/**
 * Created by JYQiu on 22/11/15.
 */
public class RootShellCmd {
    private OutputStream os;
    /**
     * 执行shell指令 *
     *
     * @param cmd * 指令
     */
    public final void exec(String cmd) {
        try {
            if (os == null) {
                // 申请获取root权限，这一步很重要，不然会没有作用
                os = Runtime.getRuntime().exec("su").getOutputStream();
            }
            os.write(cmd.getBytes());
            if(!cmd.trim().endsWith("\n")){
                os.write("\n".getBytes());
            }
            os.flush();
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 后台模拟全局按键 *
     * @param keyCode * 键值
     */
    public final void simulateKey(int keyCode) {
        exec("input keyevent " + keyCode + "\n");
    }

    public final void simulateSwip(String direction) {
        exec("input swipe " + direction + "\n");
    }
}
