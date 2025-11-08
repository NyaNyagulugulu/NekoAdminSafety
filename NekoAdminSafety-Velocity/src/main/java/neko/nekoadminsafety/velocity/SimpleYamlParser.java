package neko.nekoadminsafety.velocity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

/**
 * 简单的YAML配置文件解析器
 */
public class SimpleYamlParser {
    
    /**
     * 从文件解析拦截服务器列表
     */
    public static Set<String> parseInterceptedServers(Path configFile) {
        Set<String> servers = new HashSet<>();
        
        try {
            if (!Files.exists(configFile)) {
                return servers;
            }
            
            List<String> lines = Files.readAllLines(configFile);
            boolean inInterceptedServersSection = false;
            
            for (String line : lines) {
                line = line.trim();
                
                // 跳过注释和空行
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                // 检查是否进入拦截服务器部分
                if (line.equals("intercepted-servers:") || line.equals("intercepted-servers:")) {
                    inInterceptedServersSection = true;
                    continue;
                }
                
                // 如果在拦截服务器部分，解析服务器名称
                if (inInterceptedServersSection) {
                    // 检查是否是新的部分开始
                    if (line.contains(":") && !line.startsWith("-")) {
                        break; // 结束当前部分
                    }
                    
                    // 解析服务器名称
                    if (line.startsWith("-")) {
                        String serverName = line.substring(1).trim();
                        // 移除引号（如果有的话）
                        if (serverName.startsWith("\"") && serverName.endsWith("\"")) {
                            serverName = serverName.substring(1, serverName.length() - 1);
                        }
                        servers.add(serverName);
                    }
                }
            }
        } catch (IOException e) {
            // 忽略解析错误，返回空集合
        }
        
        return servers;
    }
    
    /**
     * 从文件解析拦截命令列表
     */
    public static List<String> parseBlockedCommands(Path configFile) {
        List<String> commands = new ArrayList<>();
        
        try {
            if (!Files.exists(configFile)) {
                return commands;
            }
            
            List<String> lines = Files.readAllLines(configFile);
            boolean inBlockedCommandsSection = false;
            
            for (String line : lines) {
                line = line.trim();
                
                // 跳过注释和空行
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                // 检查是否进入拦截命令部分
                if (line.equals("blocked-commands:")) {
                    inBlockedCommandsSection = true;
                    continue;
                }
                
                // 如果在拦截命令部分，解析命令名称
                if (inBlockedCommandsSection) {
                    // 检查是否是新的部分开始
                    if (line.contains(":") && !line.startsWith("-")) {
                        break; // 结束当前部分
                    }
                    
                    // 解析命令名称
                    if (line.startsWith("-")) {
                        String commandName = line.substring(1).trim();
                        // 移除引号（如果有的话）
                        if (commandName.startsWith("\"") && commandName.endsWith("\"")) {
                            commandName = commandName.substring(1, commandName.length() - 1);
                        }
                        commands.add(commandName.toLowerCase());
                    }
                }
            }
        } catch (IOException e) {
            // 忽略解析错误，返回空列表
        }
        
        return commands;
    }
    
    /**
     * 从文件解析拦截消息
     */
    public static String parseInterceptMessage(Path configFile) {
        try {
            if (!Files.exists(configFile)) {
                return "§c§l[NekoAdminSafety] §f该指令已被服务器拦截，您无权执行此操作。";
            }
            
            List<String> lines = Files.readAllLines(configFile);
            
            for (String line : lines) {
                line = line.trim();
                
                // 跳过注释和空行
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                // 检查是否是拦截消息配置
                if (line.startsWith("intercept-message:")) {
                    String[] parts = line.split(":", 2);
                    if (parts.length > 1) {
                        String message = parts[1].trim();
                        // 移除引号（如果有的话）
                        if (message.startsWith("\"") && message.endsWith("\"")) {
                            message = message.substring(1, message.length() - 1);
                        }
                        return message;
                    }
                }
            }
        } catch (IOException e) {
            // 忽略解析错误，返回默认值
        }
        
        return "§c§l[NekoAdminSafety] §f该指令已被服务器拦截，您无权执行此操作。";
    }
    
    /**
     * 从文件解析是否记录拦截日志
     */
    public static boolean parseLogInterceptions(Path configFile) {
        try {
            if (!Files.exists(configFile)) {
                return true; // 默认记录日志
            }
            
            List<String> lines = Files.readAllLines(configFile);
            
            for (String line : lines) {
                line = line.trim();
                
                // 跳过注释和空行
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                // 检查是否是日志记录配置
                if (line.startsWith("log-interceptions:")) {
                    String[] parts = line.split(":", 2);
                    if (parts.length > 1) {
                        String value = parts[1].trim().toLowerCase();
                        return value.equals("true") || value.equals("yes") || value.equals("1");
                    }
                }
            }
        } catch (IOException e) {
            // 忽略解析错误，返回默认值
        }
        
        return true; // 默认记录日志
    }
}