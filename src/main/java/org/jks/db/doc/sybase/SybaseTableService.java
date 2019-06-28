package org.jks.db.doc.sybase;

import org.apache.commons.compress.utils.Lists;
import org.jks.db.doc.DBUtil;
import org.jks.db.doc.TableService;
import org.jks.db.doc.model.FieldModel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @Description
 * @Author legend <liaojian.2008.ok@163.com>
 * @Version v1.0.0
 * @Since 1.0
 * @Date 2019/4/17
 */
public class SybaseTableService implements TableService {

    @Override
    public List<String> allTables() throws SQLException {
        List<String> tables = Lists.newArrayList();
        DBUtil instance = null;
        ResultSet rs = null;
        try {
            instance = extracted();
            String sql = "select name from dbo.sysobjects where type='U' order by name asc";
            rs = instance.query(sql);

            while (rs.next()) {
                tables.add(rs.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            instance.close(rs);
            instance.close();
        }

        return tables;
    }

    private DBUtil extracted() throws SQLException, Exception {
        return DBUtil.getInstance("sybase", SybaseConstant.DB_HOST, SybaseConstant.DB_PORT, SybaseConstant.DB_NAME,
                SybaseConstant.DB_USERNAME, SybaseConstant.DB_PASSWORD);
    }

    @Override
    public List<FieldModel> allField(String tablename) throws SQLException {
        List<FieldModel> data = Lists.newArrayList();
        DBUtil instance = null;
        ResultSet rs = null;
        System.out.println(tablename + "doc generate begin");
        try {
            instance = extracted();
            String sql = String.format("select c.name fname,t.name ftype, t.length flength, c.status status  " +
                    "from syscolumns c, systypes t where c.usertype = t.usertype and c.id=object_id('%s')", tablename);
            rs = instance.query(sql);

            while (rs.next()) {
                String fname = rs.getString("fname");
                String ftype = rs.getString("ftype");
                if(ftype != null && ftype.equals("varchar")){
                    ftype = ftype + "(" + rs.getString("flength") + ")";
                }

                Integer status = rs.getInt("status");

                String mandotory = status != null ? (status == 0 ? "是": "") : ""; //Mandotory

                FieldModel fm = FieldModel.builder().fieldName(fname).type(ftype).isNullable(mandotory).tablename(tablename).build();

                data.add(fm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            instance.close(rs);
            instance.close();
        }
        System.out.println(  tablename + "doc generate end");

        return data;
    }
}
