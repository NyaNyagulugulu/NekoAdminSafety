package neko.vcplugin;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VcCommunicationHandler implements PluginMessageListener {
    private final VcPlugin plugin;
    private List<String> registeredChannels;
    
    public VcCommunicationHandler(VcPlugin plugin) {
        this.plugin = plugin;
        this.registeredChannels = new ArrayList<>();
    }
    
    // 注册插件消息通道
    public void registerChannels() {
        // 注册与主插件通信的通道
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "neko:adminsafe", this);
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "neko:adminsafe");
        registeredChannels.add("neko:adminsafe");
        
        plugin.getLogger().info("VC端通信通道已注册");
    }
    
    // 注销插件消息通道
    public void unregisterChannels() {
        for (String channel : registeredChannels) {
            plugin.getServer().getMessenger().unregisterIncomingPluginChannel(plugin, channel, this);
            plugin.getServer().getMessenger().unregisterOutgoingPluginChannel(plugin, channel);
        }
        registeredChannels.clear();
        
        plugin.getLogger().info("VC端通信通道已注销");
    }
    
    // 发送连接确认消息到主插件
    public void sendConnectionConfirmation() {
        // 获取在线玩家列表中的第一个玩家（用于发送插件消息）
        Player player = getFirstOnlinePlayer();
        if (player == null) {
            plugin.getLogger().warning("没有在线玩家，无法发送连接确认消息");
            return;
        }
        
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            
            // 发送连接确认消息
            out.writeUTF("vc_connect");
            out.writeUTF("VC端插件已连接");
            
            // 发送消息到主插件
            player.sendPluginMessage(plugin, "neko:adminsafe", b.toByteArray());
            
            plugin.getLogger().info("已向主插件发送连接确认消息");
        } catch (IOException e) {
            plugin.getLogger().severe("发送连接确认消息时出错: " + e.getMessage());
        }
    }
    
    // 发送拦截确认消息到主插件
    public void sendInterceptConfirmation(String command, String player) {
        Player p = getFirstOnlinePlayer();
        if (p == null) {
            plugin.getLogger().warning("没有在线玩家，无法发送拦截确认消息");
            return;
        }
        
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            
            // 发送拦截确认消息
            out.writeUTF("vc_intercept_confirm");
            out.writeUTF(command);
            out.writeUTF(player);
            
            // 发送消息到主插件
            p.sendPluginMessage(plugin, "neko:adminsafe", b.toByteArray());
            
            plugin.getLogger().info("已向主插件发送拦截确认消息: " + command);
        } catch (IOException e) {
            plugin.getLogger().severe("发送拦截确认消息时出错: " + e.getMessage());
        }
    }
    
    // 实现PluginMessageListener接口来接收来自主插件的消息
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("neko:adminsafe")) {
            return;
        }
        
        // 处理来自主插件的消息（此处可以根据需要添加处理逻辑）
        plugin.getLogger().info("收到来自主插件的消息，通道: " + channel);
    }
    
    // 获取第一个在线玩家
    private Player getFirstOnlinePlayer() {
        if (plugin.getServer().getOnlinePlayers().isEmpty()) {
            return null;
        }
        return plugin.getServer().getOnlinePlayers().iterator().next();
    }
}