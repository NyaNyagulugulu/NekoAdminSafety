package neko.nekoadminsafety.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VelocityCommunicationHandler {
    private final NekoAdminSafetyVelocity plugin;
    private final List<String> blockedCommands;
    
    public VelocityCommunicationHandler(NekoAdminSafetyVelocity plugin) {
        this.plugin = plugin;
        this.blockedCommands = new ArrayList<>();
        
        // 从插件获取配置的拦截命令
        this.blockedCommands.addAll(plugin.getBlockedCommands());
    }
    
    // 监听命令执行事件
    @Subscribe
    public void onCommandExecute(CommandExecuteEvent event) {
        if (!(event.getCommandSource() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getCommandSource();
        String command = event.getCommand().toLowerCase();
        
        // 检查玩家当前连接的服务器
        Optional<ServerConnection> serverConnection = player.getCurrentServer();
        if (!serverConnection.isPresent()) {
            return;
        }
        
        String serverName = serverConnection.get().getServerInfo().getName();
        
        // 检查服务器是否在配置文件中被配置为需要拦截
        boolean shouldIntercept = plugin.isServerConfiguredForInterception(serverName);
        
        // 如果服务器不需要拦截，则不拦截
        if (!shouldIntercept) {
            return;
        }
        
        // 检查命令是否在拦截列表中
        if (blockedCommands.contains(command.split(" ")[0])) {
            // 取消命令执行
            event.setResult(CommandExecuteEvent.CommandResult.denied());
            
            // 发送拦截消息给玩家
            player.sendMessage(
                net.kyori.adventure.text.Component.text(plugin.getInterceptMessage())
            );
            
            // 记录拦截日志
            if (plugin.isLogInterceptions()) {
                plugin.getLogger().info("拦截了玩家 " + player.getUsername() + " 在服务器 " + serverName + " 上执行的命令: /" + command);
            }
        }
    }
    
    public NekoAdminSafetyVelocity getPlugin() {
        return plugin;
    }
}