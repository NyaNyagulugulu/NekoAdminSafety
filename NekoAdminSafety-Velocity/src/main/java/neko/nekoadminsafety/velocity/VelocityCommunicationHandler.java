package neko.nekoadminsafety.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;

import java.io.ByteArrayOutputStream;
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
    
    // 监听命令执行事件
    @Subscribe
    public void onCommandExecute(CommandExecuteEvent event) {
        if (!(event.getCommandSource() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getCommandSource();
        String command = event.getCommand().toLowerCase();
        
        // 检查命令是否在拦截列表中
        List<String> allBlockedCommands = new ArrayList<>(blockedCommands);
        allBlockedCommands.addAll(plugin.getBlockedCommands());
        
        if (allBlockedCommands.contains(command.split(" ")[0])) {
            // 取消命令执行
            event.setResult(CommandExecuteEvent.CommandResult.denied());
            
            // 发送拦截消息给玩家
            player.sendMessage(
                net.kyori.adventure.text.Component.text("§c§l[NekoAdminSafety] §f该指令已被服务器拦截，您无权执行此操作。")
            );
            
            // 记录拦截日志
            plugin.getLogger().info("拦截了玩家 " + player.getUsername() + " 执行的命令: /" + command);
            
            // 发送拦截确认到Bukkit端
            sendInterceptConfirmation(command, player.getUsername());
        }
    }
    
    // 发送拦截消息到Bukkit端插件
    public void sendInterceptConfirmation(String command, String player) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            
            out.writeUTF("velocity_intercept");
            out.writeUTF(command);
            out.writeUTF(player);
            
            // 向所有服务器发送消息
            for (var server : plugin.getServer().getAllServers()) {
                server.sendPluginMessage(
                    MinecraftChannelIdentifier.from("neko:adminsafe"), 
                    b.toByteArray()
                );
            }
            
            plugin.getLogger().info("已向Bukkit端发送拦截确认消息: " + command);
        } catch (IOException e) {
            plugin.getLogger().error("发送拦截确认消息时出错: " + e.getMessage());
        }
    }
    
    // 检查命令是否被拦截
    public boolean isBlockedCommand(String command) {
        List<String> allBlockedCommands = new ArrayList<>(blockedCommands);
        allBlockedCommands.addAll(plugin.getBlockedCommands());
        return allBlockedCommands.contains(command.toLowerCase());
    }
    
    public NekoAdminSafetyVelocity getPlugin() {
        return plugin;
    }
}