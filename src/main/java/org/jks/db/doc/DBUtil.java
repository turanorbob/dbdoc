package org.jks.db.doc;

import org.apache.commons.dbcp.BasicDataSourceFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * @Description
 * @Author legend <liaojian.2008.ok@163.com>
 * @Version v1.0.0
 * @Since 1.0
 * @Date 2019/4/17
 */
public class DBUtil {
    public static DBUtil instance = null;
    public static DataSource dataSource;
    private Connection connection;

    public static DBUtil getInstance(String type, String host, Integer port, String dbname, String username, String password) {
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
            
            try{
                dataSource = BasicDataSourceFactory.createDataSource(properties);
                instance.setConnection(dataSource.getConnection());
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

        return instance;
    }

    public Connection getConnection() throws SQLException {
        return connection;
    }

    public void setConnection(Connection connection){
        this.connection = connection;
    }

    public ResultSet query(String sql) throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        return rs;
    }

    public void close(ResultSet rs) throws SQLException {
        if (rs != null) {
            rs.close();
        }
    }

    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}
