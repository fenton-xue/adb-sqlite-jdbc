# ADB SQLite JDBC Driver

通过 ADB + Root + sqlite3 命令操作 Android 设备上 SQLite 数据库的 JDBC Driver。

**适用场景**：在 IntelliJ IDEA Database 工具中直接连接 MuMu 模拟器（或其他 Android 设备）上非 debuggable App 的 SQLite 数据库。

## 环境要求

- JDK 8+
- ADB 已安装并配置在 PATH 中
- 目标设备已 Root
- 目标设备已通过 ADB 连接（`adb devices` 可见）

## 构建

```bash
mvn clean package
```

生成的 JAR 在 `target/adb-sqlite-jdbc-1.0.0.jar`。

## JDBC URL 格式

```
jdbc:adb:sqlite://<host:port>?package=<包名>&db=<数据库路径>
```

| 参数 | 说明 | 示例 |
|------|------|------|
| host:port | ADB 设备地址 | `127.0.0.1:7555` (MuMu) |
| package | App 包名 | `com.giga.qc` |
| db | 相对于 `/data/data/{package}/` 的数据库路径 | `databases/main.db` 或 `apps/__UNI__/doc/xxx.db` |

**完整示例**：

传统 App：
```
jdbc:adb:sqlite://127.0.0.1:7555?package=com.giga.qc&db=databases/main.db
```
实际访问路径：`/data/data/com.giga.qc/databases/main.db`

uni-app：
```
jdbc:adb:sqlite://127.0.0.1:7555?package=com.giga.qc&db=apps/__UNI__435FFE1/doc/xxx.db
```
实际访问路径：`/data/data/com.giga.qc/apps/__UNI__435FFE1/doc/xxx.db`

## 在 IntelliJ IDEA 中使用

### 1. 添加 Driver

1. 打开 **Database** 工具窗口（View → Tool Windows → Database）
2. 点击 **+** → **Driver**
3. 填写：
   - **Name**: `ADB SQLite`
   - **Driver Files**: 选择 `target/adb-sqlite-jdbc-1.0.0.jar`
   - **Class**: `com.adbsqlite.AdbSqliteDriver`
   - **URL template**: `jdbc:adb:sqlite://{host:port}?package={package}&db={db}`
4. 点击 **OK**

### 2. 创建 Data Source

1. 在 Database 窗口点击 **+** → **Data Source** → 选择刚添加的 **ADB SQLite**
2. 填写 **URL**，例如：
   ```
   jdbc:adb:sqlite://127.0.0.1:7555?package=com.giga.qc&db=main.db
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
      → sqlite3 -header -csv <dbPath> "<SQL>"
        → 解析 CSV 输出
          → ResultSet
```

## License

MIT
