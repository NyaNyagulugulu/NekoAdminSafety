package neko.nekoAdminSafety;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.List;
import java.util.ArrayList;

public class ComprehensiveInterceptor implements Listener, PluginMessageListener {
    private final NekoAdminSafety plugin;
    private List<String> blockedChannels;
    
    public ComprehensiveInterceptor(NekoAdminSafety plugin) {
        this.plugin = plugin;
        initializeBlockedChannels();
    }
    
    private void initializeBlockedChannels() {
        // 从配置文件加载需要拦截的通道列表
        blockedChannels = new ArrayList<>(plugin.getConfig().getStringList("blocked-channels"));
    }
    
    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        // 检查是否启用拦截功能
        if (!plugin.getConfig().getBoolean("enabled", true)) {
            return;
        }
        
        Player player = event.getPlayer();
        
        // 获取命令（去除开头的/）
        String message = event.getMessage().substring(1);
        String[] parts = message.split(" ");
        String command = parts[0].toLowerCase();
        
        // 获取配置中的拦截命令列表
        List<String> blockedCommands = plugin.getConfig().getStringList("blocked-commands");
        
        // 检查是否是需要拦截的命令
        if (isBlockedCommand(command, blockedCommands)) {
            // 取消命令执行
            event.setCancelled(true);
            
            // 发送拦截消息给玩家
            String interceptMessage = plugin.getConfig().getString("intercept-message", "§c§l[NekoAdminSafety] §f该指令已被服务器拦截，您无权执行此操作。");
            player.sendMessage(interceptMessage);
            
            // 记录拦截日志
            if (plugin.getConfig().getBoolean("log-interceptions", true)) {
                plugin.getLogger().info("拦截了玩家 " + player.getName() + " 执行的命令: /" + message);
            }
        }
    }
    
    @EventHandler
    public void onPlayerCommandSend(PlayerCommandSendEvent event) {
        // 检查是否启用拦截功能
        if (!plugin.getConfig().getBoolean("enabled", true)) {
            return;
        }
        
        // 获取配置中的拦截命令列表
        List<String> blockedCommands = plugin.getConfig().getStringList("blocked-commands");
        
        // 从可用命令列表中移除被拦截的命令
        event.getCommands().removeIf(command -> 
            isBlockedCommand(command.toLowerCase(), blockedCommands)
        );
    }
    
    // 实现PluginMessageListener接口来拦截插件消息
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        // 检查是否启用拦截功能
        if (!plugin.getConfig().getBoolean("enabled", true) || 
            !plugin.getConfig().getBoolean("enable-vc-interception", true)) {
            return;
        }
        
        // 检查是否是需要拦截的通道
        if (isBlockedChannel(channel)) {
            // 记录拦截日志
            if (plugin.getConfig().getBoolean("log-interceptions", true)) {
                plugin.getLogger().info("拦截了玩家 " + player.getName() + " 发送到通道 " + channel + " 的消息");
            }
            
            // 发送拦截消息给玩家
            String interceptMessage = plugin.getConfig().getString("intercept-message", "§c§l[NekoAdminSafety] §f该指令已被服务器拦截，您无权执行此操作。");
            player.sendMessage(interceptMessage);
        }
    }
    
    private boolean isBlockedCommand(String command, List<String> blockedCommands) {
        // 检查是否是直接匹配的命令
        return blockedCommands.contains(command);
    }
    
    private boolean isBlockedChannel(String channel) {
        // 检查是否是需要拦截的通道
        return blockedChannels.contains(channel);
    }


public class ComprehensiveInterceptor implements Listener, PluginMessageListener {
    private final NekoAdminSafety plugin;
    private List<String> blockedChannels;
    
    public ComprehensiveInterceptor(NekoAdminSafety plugin) {
        this.plugin = plugin;
        initializeBlockedChannels();
    }
    
    private void initializeBlockedChannels() {
        // 从配置文件加载需要拦截的通道列表
        blockedChannels = new ArrayList<>(plugin.getConfig().getStringList("blocked-channels"));
    }
    
    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        // 检查是否启用拦截功能
        if (!plugin.getConfig().getBoolean("enabled", true)) {
            return;
        }
        
        Player player = event.getPlayer();
        
        // 获取命令（去除开头的/）
        String message = event.getMessage().substring(1);
        String[] parts = message.split(" ");
        String command = parts[0].toLowerCase();
        
        // 获取配置中的拦截命令列表
        List<String> blockedCommands = plugin.getConfig().getStringList("blocked-commands");
        
        // 检查是否是需要拦截的命令
        if (isBlockedCommand(command, blockedCommands)) {
            // 取消命令执行
            event.setCancelled(true);
            
            // 发送拦截消息给玩家
            String interceptMessage = plugin.getConfig().getString("intercept-message", "§c§l[NekoAdminSafety] §f该指令已被服务器拦截，您无权执行此操作。");
            player.sendMessage(interceptMessage);
            
            // 记录拦截日志
            if (plugin.getConfig().getBoolean("log-interceptions", true)) {
                plugin.getLogger().info("拦截了玩家 " + player.getName() + " 执行的命令: /" + message);
            }
        }
    }
    
    @EventHandler
    public void onPlayerCommandSend(PlayerCommandSendEvent event) {
        // 检查是否启用拦截功能
        if (!plugin.getConfig().getBoolean("enabled", true)) {
            return;
        }
        
        // 获取配置中的拦截命令列表
        List<String> blockedCommands = plugin.getConfig().getStringList("blocked-commands");
        
        // 从可用命令列表中移除被拦截的命令
        event.getCommands().removeIf(command -> 
            isBlockedCommand(command.toLowerCase(), blockedCommands)
        );
    }
    
    // 实现PluginMessageListener接口来拦截插件消息
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        // 检查是否是与VC端插件的通信
        if (channel.equals("neko:adminsafe")) {
            handleVcPluginMessage(message);
            return;
        }
        
        // 检查是否启用拦截功能
        if (!plugin.getConfig().getBoolean("enabled", true) || 
            !plugin.getConfig().getBoolean("enable-vc-interception", true)) {
            return;
        }
        
        // 检查是否是需要拦截的通道
        if (isBlockedChannel(channel)) {
            // 记录拦截日志
            if (plugin.getConfig().getBoolean("log-interceptions", true)) {
                plugin.getLogger().info("拦截了玩家 " + player.getName() + " 发送到通道 " + channel + " 的消息");
            }
            
            // 发送拦截消息给玩家
            String interceptMessage = plugin.getConfig().getString("intercept-message", "§c§l[NekoAdminSafety] §f该指令已被服务器拦截，您无权执行此操作。");
            player.sendMessage(interceptMessage);
        }
    }
    
    // 处理来自VC端插件的消息
    private void handleVcPluginMessage(byte[] message) {
        try {
            ByteArrayInputStream b = new ByteArrayInputStream(message);
            DataInputStream in = new DataInputStream(b);
            
            String type = in.readUTF();
            
            switch (type) {
                case "vc_connect":
                    String connectMessage = in.readUTF();
                    plugin.getLogger().info("收到来自VC端插件的连接确认: " + connectMessage);
                    break;
                case "vc_intercept_confirm":
                    String command = in.readUTF();
                    String playerName = in.readUTF();
                    plugin.getLogger().info("收到来自VC端插件的拦截确认: " + command + " 由玩家 " + playerName + " 尝试执行");
                    break;
                default:
                    plugin.getLogger().info("收到来自VC端插件的未知消息类型: " + type);
                    break;
            }
        } catch (IOException e) {
            plugin.getLogger().severe("处理来自VC端插件的消息时出错: " + e.getMessage());
        }
    }
    
    private boolean isBlockedCommand(String command, List<String> blockedCommands) {
        // 检查是否是直接匹配的命令
        return blockedCommands.contains(command);
    }
    
    private boolean isBlockedChannel(String channel) {
        // 检查是否是需要拦截的通道
        return blockedChannels.contains(channel);
    }
    
    // 注册插件消息通道
    public void registerChannels() {
        // 从配置加载通道列表并注册
        List<String> channels = plugin.getConfig().getStringList("blocked-channels");
        for (String channel : channels) {
            plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, channel, this);
            plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, channel);
        }
        
        // 注册与VC端插件通信的通道
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "neko:adminsafe", this);
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "neko:adminsafe");
    }
    
    // 注销插件消息通道
    public void unregisterChannels() {
        // 从配置加载通道列表并注销
        List<String> channels = plugin.getConfig().getStringList("blocked-channels");
        for (String channel : channels) {
            plugin.getServer().getMessenger().unregisterIncomingPluginChannel(plugin, channel, this);
            plugin.getServer().getMessenger().unregisterOutgoingPluginChannel(plugin, channel);
        }
        
        // 注销与VC端插件通信的通道
        plugin.getServer().getMessenger().unregisterIncomingPluginChannel(plugin, "neko:adminsafe", this);
        plugin.getServer().getMessenger().unregisterOutgoingPluginChannel(plugin, "neko:adminsafe");
    }
}
}