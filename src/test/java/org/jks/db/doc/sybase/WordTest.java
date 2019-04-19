package org.jks.db.doc.sybase;


import org.jks.db.doc.WordService;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @Description
 * @Author legend <liaojian.2008.ok@163.com>
 * @Version v1.0.0
 * @Since 1.0
 * @Date 2019/4/17
 */
public class WordTest {

    public static void main(String args[]) throws IOException, SQLException, InterruptedException {
        WordService wordService = new WordService();
        SybaseTableService tableService = new SybaseTableService();
        wordService.exportAllDocx(tableService);
    }
}
