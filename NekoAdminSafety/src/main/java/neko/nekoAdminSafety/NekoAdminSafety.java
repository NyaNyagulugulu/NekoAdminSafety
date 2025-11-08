package neko.nekoAdminSafety;

import org.bukkit.plugin.java.JavaPlugin;

public final class NekoAdminSafety extends JavaPlugin {
    private ComprehensiveInterceptor interceptor;

    @Override
    public void onEnable() {
        // 保存默认配置文件
        saveDefaultConfig();
        
        // 创建拦截器实例
        interceptor = new ComprehensiveInterceptor(this);
        
        // 注册事件监听器
        getServer().getPluginManager().registerEvents(interceptor, this);
        
        // 注册插件消息通道
        interceptor.registerChannels();
        
        getLogger().info("NekoAdminSafety 已启用！开始全面拦截代理端指令和通信。");
    }

    @Override
    public void onDisable() {
        // 注销插件消息通道
        if (interceptor != null) {
            interceptor.unregisterChannels();
        }
        
        getLogger().info("NekoAdminSafety 已禁用！");
    }
}
