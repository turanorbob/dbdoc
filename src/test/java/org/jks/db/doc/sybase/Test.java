package org.jks.db.doc.sybase;

import org.jks.db.doc.DBUtil;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 * @Author legend <legendl@synnex.com>
 * @Date 2019/6/18
 */
public class Test {
    public static void main(String args[]) throws Exception {
        ResultSet rs = null;
        Connection connection = DBUtil.getInstance("sybase", SybaseConstant.DB_HOST, SybaseConstant.DB_PORT, SybaseConstant.DB_NAME, SybaseConstant.DB_USERNAME, SybaseConstant.DB_PASSWORD).getConnection();
        String sql = "SELECT count(DISTINCT sch.contract_no) \n" +
                "        FROM service_contract_header sch\n" +
                "        INNER JOIN service_contract_line_sum scls\n" +
                "            ON sch.contract_type = scls.contract_type\n" +
                "                AND sch.contract_no = scls.contract_no\n" +
                "        WHERE sch.contract_type = 1\n" +
                "            AND sch.contract_status = 4\n" +
                "            AND sch.reseller_no = 110486\n" +
                "            AND sch.delete_id IS NULL\n" +
                "            AND sch.delete_date IS NULL\n" +
                "            AND (\n" +
                "                scls.ot_type = 125\n" +
                "                OR scls.ot_type IS NULL\n" +
                "                )\n" +
                "            AND scls.delete_id IS NULL\n" +
                "            AND scls.delete_datetime IS NULL";
        long start = System.currentTimeMillis();
        rs = DBUtil.query(connection, sql);
        System.out.println("cost time:" + (System.currentTimeMillis()-start));
        while (rs.next()) {
            System.out.println(rs.getInt(1));
        }
        DBUtil.close(rs);
        DBUtil.close(connection);
    }
}
