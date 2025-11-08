package neko.nekoadminsafety.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import java.nio.file.Path;

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

    @Inject
    public NekoAdminSafetyVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        // 初始化通信处理器
        communicationHandler = new VelocityCommunicationHandler(this);
        
        // 注册通信通道
        communicationHandler.registerChannels();
        
        logger.info("NekoAdminSafety Velocity端已启用！");
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        if (communicationHandler != null) {
            communicationHandler.unregisterChannels();
        }
        
        logger.info("NekoAdminSafety Velocity端已禁用！");
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
}