package org.jks.db.doc;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Properties;

/**
 * @Description
 * @Author legend <legendl@synnex.com>
 * @Version v1.0.0
 * @Since 1.0
 * @Date 2019/4/17
 */
public class DBUtil {
    public static DBUtil instance = null;
    public static DataSource dataSource;

    public static DBUtil getInstance(String type, String host, Integer port, String dbname, String username, String password) throws Exception {
        if(instance == null){
            instance = new DBUtil();

            String url = null;
            String driverClassName = null;
            if(type.equals("mysql")){
                url = String.format("jdbc:mysql://%s:%d/%s?allowMultiQueries=true", host, port, dbname);
                driverClassName = "com.mysql.jdbc.Driver";
            }
            else if(type.equals("sybase")){
                url = String.format("jdbc:jtds:sybase://%s:%d/%s", host, port, dbname);
                driverClassName = "net.sourceforge.jtds.jdbc.Driver";
            }

            Properties properties = new Properties();
            properties.setProperty("driverClassName", driverClassName);
            properties.setProperty("url", url);
            properties.setProperty("username", username);
            properties.setProperty("password", password);

            dataSource = BasicDataSourceFactory.createDataSource(properties);
        }

        return instance;
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static ResultSet query(Connection conn, String sql) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        return rs;
    }

    public static void close(ResultSet rs) throws SQLException {
        if (rs != null) {
            rs.close();
        }
    }

    public static void close(Connection conn) throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }
}
