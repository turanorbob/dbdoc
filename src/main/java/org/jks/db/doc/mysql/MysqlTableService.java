package org.jks.db.doc.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.compress.utils.Lists;
import org.jks.db.doc.DBUtil;
import org.jks.db.doc.TableService;
import org.jks.db.doc.model.FieldModel;

/**
 * @Description
 * @Author legend <liaojian.2008.ok@163.com>
 * @Version v1.0.0
 * @Since 1.0
 * @Date 2019/4/17
 */
public class MysqlTableService implements TableService {

    @Override
    public List<String> allTables() throws SQLException {
        List<String> tables = Lists.newArrayList();
        ResultSet rs = null;
        DBUtil instance = DBUtil.getInstance("mysql", MysqlConstant.DB_HOST, MysqlConstant.DB_PORT,
                MysqlConstant.DB_NAME, MysqlConstant.DB_USERNAME, MysqlConstant.DB_PASSWORD);
        String sql = "select distinct table_name from information_schema.columns where TABLE_SCHEMA='cloudsolv'";
        rs = instance.query(sql);

        while (rs.next()) {
            tables.add(rs.getString("table_name"));
        }
        instance.close(rs);
        instance.close();

        return tables;
    }

    @Override
    public List<FieldModel> allField(String tablename) throws SQLException {
        List<FieldModel> data = Lists.newArrayList();
        ResultSet rs = null;
        System.out.println(tablename + "doc generate begin");
        DBUtil instance = DBUtil.getInstance("mysql", MysqlConstant.DB_HOST, MysqlConstant.DB_PORT,
                MysqlConstant.DB_NAME, MysqlConstant.DB_USERNAME, MysqlConstant.DB_PASSWORD);
        String sql = String.format(
                "select COLUMN_NAME, IS_NULLABLE, COLUMN_TYPE, COLUMN_COMMENT "
                        + "from information_schema.columns where TABLE_SCHEMA='cloudsolv' and TABLE_NAME='%s'",
                tablename);
        rs = instance.query(sql);

        while (rs.next()) {
            String fname = rs.getString("COLUMN_NAME");
            String ftype = rs.getString("COLUMN_TYPE");
            String status = rs.getString("IS_NULLABLE");
            String mandotory = status != null ? (status.equals("YES") ? "是" : "否") : ""; // Mandotory
            String desc = rs.getString("COLUMN_COMMENT");

            FieldModel fm = FieldModel.builder().fieldName(fname).type(ftype).isNullable(mandotory).desc(desc)
                    .tablename(tablename).build();

            data.add(fm);
        }
        instance.close(rs);
        instance.close();
        System.out.println(tablename + "doc generate end");

        return data;
    }
}
