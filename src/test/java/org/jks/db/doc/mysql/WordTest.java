package org.jks.db.doc.mysql;


import org.jks.db.doc.WordService;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @Description
 * @Author legend <legendl@synnex.com>
 * @Version v1.0.0
 * @Since 1.0
 * @Date 2019/4/17
 */
public class WordTest {

    public static void main(String args[]) throws IOException, SQLException, InterruptedException {
        WordService wordService = new WordService();
        long start = System.currentTimeMillis();
        MysqlTableService tableService = new MysqlTableService();
        wordService.exportAllDocx(tableService);
        System.out.println("cost time:" + (System.currentTimeMillis() - start) + "ms");
    }
}
