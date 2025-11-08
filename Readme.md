# NekoAdminSafety

NekoAdminSafety 是一个用于 Minecraft Velocity 代理服务器的插件，用于拦截指定服务器上的特定指令，防止玩家执行可能影响服务器安全的命令。

## 功能特点

- **服务器级别指令拦截**：可以配置在特定服务器上拦截指定的指令
- **配置文件驱动**：所有配置通过 `config.yml` 文件进行管理
- **无后端依赖**：插件完全在 Velocity 端运行，无需后端 Bukkit/Spigot 服务器插件
- **灵活配置**：支持自定义拦截消息和日志记录选项

## 安装说明

1. 将编译好的插件 JAR 文件放入 Velocity 服务器的 `plugins` 目录
2. 启动服务器，插件会自动生成默认配置文件 `plugins/nekoadminsafety/config.yml`
3. 根据需要修改配置文件
4. 重启服务器或使用命令重载插件配置

## 配置文件说明

配置文件位于 `plugins/nekoadminsafety/config.yml`：

```yaml
# 需要拦截指令的服务器列表
# 在这些服务器中，所有配置的指令将被拦截
intercepted-servers:
  - "server1"
  - "server2"
  - "lobby"

# 要拦截的指令列表
blocked-commands:
  - "lpv"
  - "send"
  - "server"
  - "end"
  - "connect"
  - "gconnect"
  - "bukkit:version"
  - "version"

# 拦截时发送给玩家的消息
intercept-message: "§c§l[NekoAdminSafety] §f该指令已被服务器拦截，您无权执行此操作。"

# 是否记录拦截日志
log-interceptions: true
```

### 配置项说明

- `intercepted-servers`：需要拦截指令的服务器名称列表，与 Velocity 配置文件中的服务器名称一致
- `blocked-commands`：要拦截的指令列表，不区分大小写
- `intercept-message`：当玩家尝试执行被拦截的指令时显示的消息
- `log-interceptions`：是否在服务器控制台记录拦截事件

## 使用方法

1. 在配置文件中添加需要拦截指令的服务器名称到 `intercepted-servers` 列表
2. 在配置文件中添加需要拦截的指令到 `blocked-commands` 列表
3. 重启服务器或重载插件配置
4. 当玩家在配置的服务器上执行被拦截的指令时，指令将被阻止执行并显示拦截消息

## 指令拦截范围

插件只在配置文件中指定的服务器上拦截指令。如果玩家从拦截服务器切换到未配置的服务器，则可以在未配置的服务器上正常执行指令。

## 日志记录

当 `log-interceptions` 设置为 `true` 时，插件会在控制台记录所有拦截事件，包括：
- 玩家名称
- 服务器名称
- 被拦截的指令

## 构建说明

```bash
# 克隆项目
git clone <项目地址>

# 进入项目目录
cd NekoAdminSafety

# 使用 Maven 构建
mvn clean package

# 构建后的插件文件位于 target/ 目录下
```

## 依赖

- Velocity 3.0.0 或更高版本

## 许可证

本项目仅供学习和防御安全用途。