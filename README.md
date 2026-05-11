# ADB SQLite JDBC Driver

通过 ADB + Root + sqlite3 命令操作 Android 设备上 SQLite 数据库的 JDBC Driver。

**适用场景**：在 IntelliJ IDEA Database 工具中直接连接 MuMu 模拟器（或其他 Android 设备）上非 debuggable App 的 SQLite 数据库。

GitHub：https://github.com/fenton-xue/adb-sqlite-jdbc

## 环境要求

- JDK 8+
- ADB 已安装并配置在 PATH 中
- 目标设备已 Root
- 目标设备已通过 ADB 连接（`adb devices` 可见）

## 获取 JAR

下载 `adb-sqlite-jdbc-1.0.1.jar`（项目根目录），或自行构建：

```bash
git clone https://github.com/fenton-xue/adb-sqlite-jdbc.git
cd adb-sqlite-jdbc
mvn clean package -DskipTests
# JAR 在 target/adb-sqlite-jdbc-1.0.1.jar
```

## JDBC URL 格式

```
jdbc:adb:sqlite://<host:port>?package=<包名>&db=<数据库路径>[&device=<设备地址>]
```

### 参数说明

| 参数 | 必填 | 说明 | 示例 |
|------|------|------|------|
| host:port | 是 | ADB 设备地址（URL 中） | `127.0.0.1:7555` |
| package | 是 | App 包名 | `com.baimeihome.pre` |
| db | 是 | 数据库路径。相对路径相对于 `/data/data/{package}/`，绝对路径以 `/` 开头 | `databases/main.db` 或 `/sdcard/.../demo.db` |
| device | 否 | 覆盖 ADB 设备地址，优先级高于 host:port | `192.168.1.100:5555` |

### 完整示例

传统 App（相对路径）：
```
jdbc:adb:sqlite://127.0.0.1:7555?package=com.giga.qc&db=databases/main.db
```
→ `/data/data/com.giga.qc/databases/main.db`

uni-app（SD 卡绝对路径）：
```
jdbc:adb:sqlite://127.0.0.1:7555?package=com.baimeihome.pre&db=/sdcard/Android/data/com.baimeihome.pre/apps/__UNI__436FFE1/doc/demo.db
```
→ 直接使用绝对路径

通过 device 参数指定地址：
```
jdbc:adb:sqlite://?package=com.giga.qc&db=databases/main.db&device=127.0.0.1:7555
```

## 在 IntelliJ IDEA 中使用

### 1. 添加 Driver

1. 打开 **Database** 工具窗口（View → Tool Windows → Database）
2. 点击 **+** → **Driver**
3. 填写：
   - **Name**: `ADB SQLite`
   - **Driver Files**: 选择 `adb-sqlite-jdbc-1.0.1.jar`
   - **Class**: `com.adbsqlite.AdbSqliteDriver`
   - **URL template**: `jdbc:adb:sqlite://{host:port}?package={package}&db={db}`
4. 点击 **OK**

### 2. 创建 Data Source

1. 在 Database 窗口点击 **+** → **Data Source** → 选择刚添加的 **ADB SQLite**
2. 填写 **URL**，例如：
   ```
   jdbc:adb:sqlite://127.0.0.1:7555?package=com.baimeihome.pre&db=/sdcard/Android/data/com.baimeihome.pre/apps/__UNI__436FFE1/doc/demo.db
   ```
3. 点击 **Test Connection** 验证连接
4. 点击 **OK** 完成

### 3. 使用

- 双击表名查看数据
- 在 Console 中执行 SQL
- 支持 SELECT / INSERT / UPDATE / DELETE

## 常见设备 ADB 地址

| 模拟器 | ADB 地址 |
|--------|----------|
| MuMu 模拟器 | `127.0.0.1:7555` |
| 夜神模拟器 | `127.0.0.1:62001` |
| 雷电模拟器 | `127.0.0.1:5555` |
| 真机 USB | `(adb devices 显示的序列号)` |

## 限制

- 每次 SQL 执行都是独立的 shell 调用，不支持事务
- 性能取决于 ADB 通信延迟，不适合大数据量查询
- 不支持 BLOB 二进制数据
- 所有列类型报告为 VARCHAR（因为 CSV 输出无类型信息）

## 工作原理

```
Java JDBC 调用
  → adb -s <device> shell
    → su -c
      → echo "base64(SQL)" | base64 -d | sqlite3 -header -csv <dbPath>
        → 解析 CSV 输出
          → ResultSet
```

SQL 通过 base64 编码传递给设备端，避免 shell 引号和特殊字符转义问题。

## License

MIT
