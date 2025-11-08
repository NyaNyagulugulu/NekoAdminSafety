package neko.nekoadminsafety.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.nio.file.Files;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.io.InputStream;
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
    private Set<String> configuredInterceptedServers; // 配置文件中定义的需要拦截的服务器
    private String interceptMessage; // 拦截消息
    private boolean logInterceptions; // 是否记录拦截日志

    @Inject
    public NekoAdminSafetyVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.blockedCommands = new ArrayList<>();
        this.configuredInterceptedServers = new HashSet<>();
        this.interceptMessage = "§c§l[NekoAdminSafety] §f该指令已被服务器拦截，您无权执行此操作。";
        this.logInterceptions = true;
        
        // 加载配置文件
        loadConfig();
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        // 初始化通信处理器
        communicationHandler = new VelocityCommunicationHandler(this);
        
        // 注册事件监听器
        server.getEventManager().register(this, communicationHandler);
        
        logger.info("NekoAdminSafety Velocity端已启用！");
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        logger.info("NekoAdminSafety Velocity端已禁用！");
    }

    // 加载配置文件
    private void loadConfig() {
        // 创建默认配置文件（如果不存在）
        Path configFile = dataDirectory.resolve("config.yml");
        if (!Files.exists(configFile)) {
            try {
                // 确保数据目录存在
                if (!Files.exists(dataDirectory)) {
                    Files.createDirectories(dataDirectory);
                }
                
                // 从资源文件复制默认配置
                try (InputStream in = getClass().getClassLoader().getResourceAsStream("config.yml")) {
                    if (in != null) {
                        Files.copy(in, configFile);
                        logger.info("已创建默认配置文件: " + configFile.toString());
                    } else {
                        logger.warn("未找到默认配置文件资源");
                    }
                }
            } catch (IOException e) {
                logger.error("创建默认配置文件时出错", e);
            }
        }
        
        // 使用配置文件解析器读取配置
        this.configuredInterceptedServers = SimpleYamlParser.parseInterceptedServers(configFile);
        this.blockedCommands = SimpleYamlParser.parseBlockedCommands(configFile);
        this.interceptMessage = SimpleYamlParser.parseInterceptMessage(configFile);
        this.logInterceptions = SimpleYamlParser.parseLogInterceptions(configFile);
        
        logger.info("已加载配置文件，配置的拦截服务器数量: " + configuredInterceptedServers.size());
        logger.info("已加载配置文件，配置的拦截指令数量: " + blockedCommands.size());
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

    // 检查服务器是否在配置文件中被配置为需要拦截
    public boolean isServerConfiguredForInterception(String serverName) {
        return configuredInterceptedServers.contains(serverName);
    }
    
    // 获取所有配置的拦截服务器
    public Set<String> getConfiguredInterceptedServers() {
        return new HashSet<>(configuredInterceptedServers);
    }
    
    // 获取拦截消息
    public String getInterceptMessage() {
        return interceptMessage;
    }
    
    // 是否记录拦截日志
    public boolean isLogInterceptions() {
        return logInterceptions;
    }
}