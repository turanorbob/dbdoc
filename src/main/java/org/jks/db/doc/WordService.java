package org.jks.db.doc;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.poi.xwpf.usermodel.*;
import org.jks.db.doc.model.FieldModel;
import org.jks.db.doc.model.Name;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STVerticalJc;
import sun.reflect.misc.FieldUtil;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description
 * @Author legend <liaojian.2008.ok@163.com>
 * @Version v1.0.0
 * @Since 1.0
 * @Date 2019/4/17
 */
public class WordService {
    private ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*2);

    /**
     * 表信息数据量不大， 所以可以考虑全部查询表结构后，将数据全部导出
     *
     * @param tableService
     * @throws SQLException
     * @throws IOException
     */
    public void exportAllDocx(final TableService tableService) throws SQLException, IOException, InterruptedException {
        XWPFDocument doc = new XWPFDocument();

        List<String> tables = tableService.allTables();
        final CountDownLatch count = new CountDownLatch(tables.size());
        List<List<FieldModel>> fieldData = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(tables)) {
            tables.forEach(table -> {
                executorService.submit(() -> {
                    try {
                        List<FieldModel> data = tableService.allField(table);
                        fieldData.add(data);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    finally {
                        count.countDown();
                    }
                });
            });
            count.await();
            executorService.shutdownNow();
            fieldData.forEach(fieldModels -> {
                // 表备注
                XWPFParagraph para = doc.createParagraph();
                XWPFRun run = para.createRun();
                run.setBold(true); //加粗
                // 表备注
                try {
                    table(doc, fieldModels);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            });

            //文件不存在时会自动创建
            OutputStream os = new FileOutputStream("cloudsolv.docx");
            //写入文件
            doc.write(os);
            close(os);


        }
    }

    public synchronized void table(XWPFDocument doc, List<FieldModel> data) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if(CollectionUtils.isEmpty(data)){
            throw new NullPointerException("data is null");
        }

        int rows = data.size() + 1;
        int cols = 0;
        Map<String, String> headers = new LinkedHashMap<>();
        String tablename = data.get(0).getTablename();

        // 表备注
        XWPFParagraph para = doc.createParagraph();
        XWPFRun run = para.createRun();
        run.setBold(true); //加粗
        run.setText("表名:" + tablename);

        Field[] fields = FieldUtil.getDeclaredFields(FieldModel.class);
        Arrays.asList(fields).forEach(field -> {
            Name name = field.getAnnotation(Name.class);
            if(name != null){
                headers.put(field.getName(), name.value());
            }

        });
        cols = headers.size();

        XWPFTable table = doc.createTable(rows, cols);
        List<XWPFTableRow> tableRows = table.getRows();

        CTTblPr tablePr = table.getCTTbl().addNewTblPr();

        CTTblWidth width = tablePr.addNewTblW();
        // table width
        width.setW(BigInteger.valueOf(8000));

        XWPFTableRow row;
        List<XWPFTableCell> cells;

        XWPFTableCell cell;

        // header
        row = tableRows.get(0);
        cells = row.getTableCells();
        Set<String> keys = headers.keySet();

        int j = 0;
        for (String key : keys) {
            cell = cells.get(j);
            //单元格属性
            CTTcPr cellPr = cell.getCTTc().addNewTcPr();
            cellPr.addNewVAlign().setVal(STVerticalJc.CENTER);
            cell.setText(headers.get(key));
            j++;
        }

        // data
        for (int i = 1; i < rows; i++) {
            row = tableRows.get(i);
            row.setHeight(500);
            cells = row.getTableCells();

            j = 0;
            FieldModel fieldModel = data.get(i - 1);
            for (String key : keys) {
                cell = cells.get(j);

                String value = BeanUtils.getProperty(fieldModel, key);
                cell.setText(value);
                j++;
            }

        }
    }

    private static void close(OutputStream os) {
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
