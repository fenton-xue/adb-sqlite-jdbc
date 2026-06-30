package com.adbsqlite;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

public class AdbSqliteDriver implements Driver {

    private static final String URL_PREFIX = "jdbc:adb:sqlite:";

    static {
        try {
            DriverManager.registerDriver(new AdbSqliteDriver());
        } catch (SQLException e) {
            throw new RuntimeException("注册 ADB SQLite Driver 失败", e);
        }
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        if (!acceptsURL(url)) return null;

        UrlInfo ui = parseUrl(url);
        String dbPath;
        if (ui.db.startsWith("/")) {
            dbPath = ui.db; // 绝对路径直接使用
        } else {
            dbPath = "/data/data/" + ui.packageName + "/" + ui.db; // 相对路径拼接
        }
        return new AdbSqliteConnection(url, ui.device, dbPath, ui.root);
    }

    @Override
    public boolean acceptsURL(String url) {
        return url != null && url.startsWith(URL_PREFIX);
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) {
        return new DriverPropertyInfo[0];
    }

    @Override
    public int getMajorVersion() { return 1; }

    @Override
    public int getMinorVersion() { return 2; }

    @Override
    public boolean jdbcCompliant() { return false; }

    @Override
    public Logger getParentLogger() {
        return Logger.getLogger("com.adbsqlite");
    }

    // ---- URL 解析 ----

    static class UrlInfo {
        String device;
        String packageName;
        String db;
        boolean root = true;
    }

    /**
     * 解析 JDBC URL: jdbc:adb:sqlite://host:port?package=xxx&db=xxx.db&root=false
     */
    static UrlInfo parseUrl(String url) throws SQLException {
        String body = url.substring(URL_PREFIX.length());

        UrlInfo info = new UrlInfo();

        // 移除开头的 //
        if (body.startsWith("//")) body = body.substring(2);

        // 分割 host:port 和 query
        int qIdx = body.indexOf('?');
        String hostPart, queryPart;
        if (qIdx >= 0) {
            hostPart = body.substring(0, qIdx);
            queryPart = body.substring(qIdx + 1);
        } else {
            hostPart = body;
            queryPart = "";
        }

        info.device = hostPart;

        // 解析 query 参数
        for (String pair : queryPart.split("&")) {
            if (pair.isEmpty()) continue;
            int eq = pair.indexOf('=');
            if (eq < 0) throw new SQLException("无效的 URL 参数: " + pair);
            String key = pair.substring(0, eq);
            String val = pair.substring(eq + 1);
            if ("package".equals(key)) info.packageName = val;
            else if ("db".equals(key)) info.db = val;
            else if ("device".equals(key)) info.device = val;
            else if ("root".equals(key) || "useRoot".equals(key)) info.root = parseBooleanParameter(key, val);
        }

        if (info.device == null || info.device.isEmpty())
            throw new SQLException("URL 中缺少设备地址 (host:port)");
        if (info.packageName == null || info.packageName.isEmpty())
            throw new SQLException("URL 中缺少 package 参数");
        if (info.db == null || info.db.isEmpty())
            throw new SQLException("URL 中缺少 db 参数");

        return info;
    }

    private static boolean parseBooleanParameter(String key, String val) throws SQLException {
        if ("true".equalsIgnoreCase(val) || "1".equals(val) || "yes".equalsIgnoreCase(val)) {
            return true;
        }
        if ("false".equalsIgnoreCase(val) || "0".equals(val) || "no".equalsIgnoreCase(val)) {
            return false;
        }
        throw new SQLException("无效的 URL 参数 " + key + "=" + val + "，仅支持 true/false");
    }
}
