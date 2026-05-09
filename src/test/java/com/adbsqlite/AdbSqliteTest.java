package com.adbsqlite;

import java.sql.*;

/**
 * 简单测试：连接 MuMu 模拟器上的 SQLite 数据库并执行 CRUD。
 *
 * 使用前请修改 URL 中的参数为你的实际值。
 * 运行方式: mvn exec:java 或在 IDE 中直接运行 main 方法。
 */
public class AdbSqliteTest {

    public static void main(String[] args) throws Exception {
        // 修改为你的实际参数
        String url = "jdbc:adb:sqlite://127.0.0.1:7555?package=com.giga.qc&db=main.db";

        System.out.println("=== ADB SQLite JDBC Driver Test ===\n");

        // Driver 已在 static 块中自动注册，直接获取连接
        try (Connection conn = DriverManager.getConnection(url)) {
            System.out.println("[OK] 连接成功!\n");

            try (Statement stmt = conn.createStatement()) {

                // SELECT
                System.out.println("--- SELECT ---");
                ResultSet rs = stmt.executeQuery(
                        "SELECT * FROM sqlite_master WHERE type='table'");
                while (rs.next()) {
                    System.out.println("  " + rs.getString("name") + " (" + rs.getString("type") + ")");
                }
                rs.close();

                // INSERT
                System.out.println("\n--- INSERT ---");
                stmt.executeUpdate(
                        "INSERT INTO test_table(id, name) VALUES(1, 'hello')");
                System.out.println("[OK] INSERT 完成");

                // UPDATE
                System.out.println("\n--- UPDATE ---");
                stmt.executeUpdate(
                        "UPDATE test_table SET name='world' WHERE id=1");
                System.out.println("[OK] UPDATE 完成");

                // SELECT 验证
                System.out.println("\n--- SELECT 验证 ---");
                rs = stmt.executeQuery("SELECT * FROM test_table");
                ResultSetMetaData md = rs.getMetaData();
                for (int i = 1; i <= md.getColumnCount(); i++) {
                    System.out.print(md.getColumnLabel(i) + "\t");
                }
                System.out.println();
                while (rs.next()) {
                    for (int i = 1; i <= md.getColumnCount(); i++) {
                        System.out.print(rs.getString(i) + "\t");
                    }
                    System.out.println();
                }
                rs.close();

                // DELETE
                System.out.println("\n--- DELETE ---");
                stmt.executeUpdate("DELETE FROM test_table WHERE id=1");
                System.out.println("[OK] DELETE 完成");
            }

            System.out.println("\n=== 测试完成 ===");
        } catch (SQLException e) {
            System.err.println("[FAIL] " + e.getMessage());
            e.printStackTrace();
        }
    }
}
