package neko.nekoadminsafety.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VelocityCommunicationHandler {
    private final NekoAdminSafetyVelocity plugin;
    private final List<String> blockedCommands;
    private final List<MinecraftChannelIdentifier> registeredChannels;
    
    public VelocityCommunicationHandler(NekoAdminSafetyVelocity plugin) {
        this.plugin = plugin;
        this.blockedCommands = new ArrayList<>();
        this.registeredChannels = new ArrayList<>();
        
        // 初始化被拦截的命令列表
        initializeBlockedCommands();
    }
    
    private void initializeBlockedCommands() {
        blockedCommands.add("lpv");
        blockedCommands.add("send");
        blockedCommands.add("server");
        blockedCommands.add("end");
        blockedCommands.add("connect");
        blockedCommands.add("gconnect");
        blockedCommands.add("bukkit:version");
        blockedCommands.add("version");
    }
    
    // 注册插件消息通道
    public void registerChannels() {
        MinecraftChannelIdentifier channel = MinecraftChannelIdentifier.from("neko:adminsafe");
        plugin.getServer().getChannelRegistrar().register(channel);
        registeredChannels.add(channel);
        
        plugin.getLogger().info("Velocity端通信通道已注册");
    }
    
    // 注销插件消息通道
    public void unregisterChannels() {
        for (MinecraftChannelIdentifier channel : registeredChannels) {
            plugin.getServer().getChannelRegistrar().unregister(channel);
        }
        registeredChannels.clear();
        
        plugin.getLogger().info("Velocity端通信通道已注销");
    }
    
    // 发送拦截消息到Bukkit端插件
    public void sendInterceptMessage(String command, String player) {
        // 向所有服务器发送消息
        for (var server : plugin.getServer().getAllServers()) {
            try {
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(b);
                
                out.writeUTF("velocity_intercept");
                out.writeUTF(command);
                out.writeUTF(player);
                
                server.sendPluginMessage(
                    MinecraftChannelIdentifier.from("neko:adminsafe"), 
                    b.toByteArray()
                );
                
                plugin.getLogger().info("已向后端服务器发送拦截消息: " + command);
            } catch (IOException e) {
                plugin.getLogger().error("发送拦截消息时出错: " + e.getMessage());
            }
        }
    }
    
    // 检查命令是否被拦截
    public boolean isBlockedCommand(String command) {
        return blockedCommands.contains(command.toLowerCase());
    }
    
    public NekoAdminSafetyVelocity getPlugin() {
        return plugin;
    }
}