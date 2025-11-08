package neko.nekoadminsafety.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

@Plugin(
    id = "nekoadminsafety",
    name = "NekoAdminSafety",
    version = "1.0-SNAPSHOT",
    authors = {"不穿胖次の小奶猫"}
)
public class NekoAdminSafetyVelocity {
    
    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private VelocityCommunicationHandler communicationHandler;
    private List<String> blockedCommands;

    @Inject
    public NekoAdminSafetyVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.blockedCommands = new ArrayList<>();
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        // 初始化通信处理器
        communicationHandler = new VelocityCommunicationHandler(this);
        
        // 注册通信通道
        communicationHandler.registerChannels();
        
        // 注册事件监听器
        server.getEventManager().register(this, communicationHandler);
        
        logger.info("NekoAdminSafety Velocity端已启用！");
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        if (communicationHandler != null) {
            communicationHandler.unregisterChannels();
        }
        
        logger.info("NekoAdminSafety Velocity端已禁用！");
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        if (event.getIdentifier().equals(MinecraftChannelIdentifier.from("neko:adminsafe"))) {
            handlePluginMessage(event);
        }
    }

    private void handlePluginMessage(PluginMessageEvent event) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(event.getData());
            DataInputStream dis = new DataInputStream(bis);

            String messageType = dis.readUTF();
            if ("config_update".equals(messageType)) {
                int commandCount = dis.readInt();
                blockedCommands.clear();
                for (int i = 0; i < commandCount; i++) {
                    blockedCommands.add(dis.readUTF().toLowerCase());
                }
                logger.info("从Bukkit端接收配置更新，已拦截指令数量: " + commandCount);
            }
        } catch (IOException e) {
            logger.error("处理插件消息时出错", e);
        }
    }

    public ProxyServer getServer() {
        return server;
    }

    public Logger getLogger() {
        return logger;
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }

    public List<String> getBlockedCommands() {
        return blockedCommands;
    }
}