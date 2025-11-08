package neko.vcplugin;

import org.bukkit.plugin.java.JavaPlugin;
import neko.nekoAdminSafety.ComprehensiveInterceptor;

public class VcPlugin extends JavaPlugin {
    private VcCommunicationHandler communicationHandler;

    @Override
    public void onEnable() {
        // 保存默认配置文件
        saveDefaultConfig();
        
        // 初始化通信处理器
        communicationHandler = new VcCommunicationHandler(this);
        
        // 注册插件消息通道
        communicationHandler.registerChannels();
        
        getLogger().info("VC端插件已启用，开始与NekoAdminSafety通信。");
        
        // 向主插件发送连接确认
        communicationHandler.sendConnectionConfirmation();
    }

    @Override
    public void onDisable() {
        if (communicationHandler != null) {
            communicationHandler.unregisterChannels();
        }
        
        getLogger().info("VC端插件已禁用！");
    }

    public VcCommunicationHandler getCommunicationHandler() {
        return communicationHandler;
    }
}