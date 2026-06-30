# ADB SQLite JDBC Driver 使用指南

## 为什么需要这个工具

在测试 App 端时，经常需要查看 Android 应用本地存储的 SQLite 数据库来验证数据是否正确。

**在没有这个工具之前**，通常做法是：

1. 打开命令行，执行 `adb pull` 将数据库文件从设备导出到电脑
2. 用 DB Browser 等数据库管理软件打开导出的文件查看
3. 如果数据有更新，重复第 1~2 步，重新导出再查看

这个过程非常繁琐，而且会带来几个问题：

- **版本混乱**：每次 pull 出来的文件都是独立副本，多了之后分不清哪个是最新的，容易搞混
- **效率低下**：每次数据变更都要重新走一遍导出流程，哪怕只是查一条记录
- **协作不便**：想查看不同设备（如多台模拟器、真机）上的数据，操作更加复杂

还有另一种方式——直接用 `adb shell` 拼接 SQL 命令去操作，但这种方式同样不理想：

- 需要手动拼接完整的 shell 命令，SQL 语句稍长就容易出错，还要小心转义字符
- 查询结果是纯文本表格输出，没有格式化、排序、过滤等能力，遇到字段多的时候几乎没法看
- 只能执行单条 SQL，没办法像数据库客户端那样交互式操作

## 这个工具做了什么

**ADB SQLite JDBC Driver** 是一个 JDBC 驱动程序，它让你可以在 IntelliJ IDEA（或 DataGrip）的 Database 工具窗口中，像连接普通数据库一样，**直接连接到 Android 设备上的 SQLite 数据库文件**。

即：

- 不需要再 `adb pull` 导出文件
- 在 IDEA 里双击表名就能看到数据
- 可以写任意 SQL 查询、筛选、排序、关联查询
- 数据是实时读取设备的，设备数据更新后，刷新一下就能看到最新结果
- 支持同时连接多个设备、多个应用数据库，随意切换

## 环境要求

- **JDK 8+**
- **ADB** 已安装并配置在系统 PATH 中（确认方法：打开命令行输入 `adb devices` 能看到设备）
- **默认需要设备已 Root**；1.0.10 起可在 URL 增加 `root=false` 使用非 su 模式
- **设备上存在 sqlite3 命令**（大部分模拟器自带）
- **设备已通过 ADB 连接**（`adb devices` 能列出目标设备）

## 在 IDEA / DataGrip 中使用

### 第一步：添加驱动

[此处插入示意图：添加驱动的操作截图]

1. 打开 **Database** 工具窗口：`View → Tool Windows → Database`
2. 点击左侧工具栏的 **+** → **Driver**
3. 在弹出的配置窗口中填写以下信息：

| 配置项 | 填写内容 |
|--------|----------|
| **Name** | `ADB SQLite`（自定义，你自己记得住就行） |
| **Driver Files** | 选择 `adb-sqlite-jdbc-1.0.10.jar`（点击右侧文件夹图标找到 jar 包的位置） |
| **Class** | `com.adbsqlite.AdbSqliteDriver`（选择 jar 后点击下拉框会自动识别） |
| **URL template** | `jdbc:adb:sqlite://{host:port}?package={package}&db={db}` |

4. 点击 **OK** 保存

### 第二步：添加数据源

[此处插入示意图：添加数据源的操作截图]

1. 在 Database 窗口点击 **+** → **Data Source** → 选择刚才添加的 **ADB SQLite**
2. 填写 **URL**，格式如下：

```
jdbc:adb:sqlite://<设备地址>?package=<包名>&db=<数据库路径>
```

> 建议：数据库路径统一使用**绝对路径**，兼容各种情况

各参数说明：

| 参数 | 说明 | 示例 |
|------|------|------|
| **设备地址** | ADB 连接地址，格式 `IP:端口` | `127.0.0.1:7555` |
| **package** | 应用包名，不包含空格 | `com.baimeihome.pre` |
| **db** | 数据库路径 | 见下方说明 |
| **root** | 可选，默认 `true`；当 `adb shell sqlite3 <db>` 能看到表而 `su -c sqlite3 <db>` 看不到表时设为 `false` | `false` |

> `root=false` 的原因：部分设备上普通 `adb shell sqlite3` 和 `su -c sqlite3` 访问到的数据库视图不一致，会出现不加 su 能看到表、加 su 反而看不到表的情况。

**数据库路径说明**：

- **相对路径**：相对于 `/data/data/{包名}/` 目录。绝大多数 App 的数据库都存放在 `databases/` 下，比如 `databases/main.db`，实际对应的是 `/data/data/com.baimeihome.pre/databases/main.db`
- **绝对路径**：以 `/` 开头。一些跨平台 App（如 uni-app）的数据库可能存放在 SD 卡目录，比如 `/sdcard/Android/data/com.baimeihome.pre/apps/__UNI__436FFE1/doc/demo.db`

3. 点击 **Test Connection** 测试连接是否成功
4. 点击 **OK** 完成

### 完整的 URL 示例

```
# 普通 App（相对路径）
jdbc:adb:sqlite://127.0.0.1:7555?package=com.giga.qc&db=databases/main.db

# uni-app SD 卡路径（绝对路径）
jdbc:adb:sqlite://127.0.0.1:7555?package=com.baimeihome.pre&db=/sdcard/Android/data/com.baimeihome.pre/apps/__UNI__436FFE1/doc/demo.db

# 非 su 模式
jdbc:adb:sqlite://127.0.0.1:16384?package=com.giga.qc&db=databases/qcapp_localSQLite.db&root=false

```

### 第三步：开始使用

连接成功后，你就可以像操作普通 MySQL / PostgreSQL 数据库一样操作设备上的 SQLite 了：

- 展开数据库后双击表名，快速浏览全部数据
- 在 Console 中写 SQL 进行查询、筛选、分组、排序
- 支持 `SELECT` / `INSERT` / `UPDATE` / `DELETE` 全部四种操作
- 可以在同一个 IDEA 窗口中同时连接多台设备、多个应用的数据库

[此处插入示意图：IDEA 中查看到数据的效果截图]

## 常见设备的 ADB 地址

| 模拟器 | ADB 地址 |
|--------|----------|
| MuMu 模拟器 | `127.0.0.1:7555` |
| 夜神模拟器 | `127.0.0.1:62001` |
| 雷电模拟器 | `127.0.0.1:5555` |
| 真机（USB 连接） | 使用 `adb devices` 显示的序列号，如 `0123456789ABCDEF` |

### 真机连接说明

真机通过 USB 连接时，ADB 使用序列号标识设备，可能没有 IP:端口。`adb devices` 显示的序列号可以直接作为设备地址使用。URL 示例：

```
jdbc:adb:sqlite://0123456789ABCDEF?package=com.giga.qc&db=/data/data/com.giga.qc/databases/main.db
```

## 注意事项

- **数据库文件必须在设备上存在**才能连接成功，如果 App 还未创建过数据库，请先运行 App 触发数据库初始化再连接
- **不支持事务**：每次 SQL 执行都是独立的 shell 调用，不支持 `BEGIN` / `COMMIT` / `ROLLBACK`
- **查询速度受 ADB 通信延迟影响**，适合查数据量不大的场景（几千条以内体验流畅）
- **不支持 BLOB 类型字段**（二进制数据如图片、文件存储）
