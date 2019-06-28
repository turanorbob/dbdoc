package org.jks.db.doc.excel;

import org.apache.commons.compress.utils.Lists;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

/**
 * @Description
 * @Author legend <legendl@synnex.com>
 * @Version v1.0.0
 * @Since 1.0
 * @Date 2019/6/12
 */
public class MergeUtil {

    public static void copyCellStyle(XSSFCellStyle fromStyle, XSSFCellStyle toStyle) {
        toStyle.cloneStyleFrom(fromStyle);
    }

    public static void mergeSheetAllRegion(XSSFSheet fromSheet, XSSFSheet toSheet) {
        int num = fromSheet.getNumMergedRegions();
        CellRangeAddress cellR = null;
        for (int i = 0; i < num; i++) {
            cellR = fromSheet.getMergedRegion(i);
            toSheet.addMergedRegion(cellR);
        }
    }

    public static void copyCell(XSSFWorkbook wb, XSSFCell fromCell, XSSFCell toCell) {
        XSSFCellStyle newstyle = wb.createCellStyle();
        copyCellStyle(fromCell.getCellStyle(), newstyle);
        toCell.setCellStyle(newstyle);
        if (fromCell.getCellComment() != null) {
            toCell.setCellComment(fromCell.getCellComment());
        }
        CellType fromCellType = fromCell.getCellType();
        toCell.setCellType(fromCellType);
        if (fromCellType == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(fromCell)) {
                toCell.setCellValue(fromCell.getDateCellValue());
            } else {
                toCell.setCellValue(fromCell.getNumericCellValue());
            }
        } else if (fromCellType == CellType.STRING) {
            toCell.setCellValue(fromCell.getRichStringCellValue());
        } else if (fromCellType == CellType.BLANK) {
        } else if (fromCellType == CellType.BOOLEAN) {
            toCell.setCellValue(fromCell.getBooleanCellValue());
        } else if (fromCellType == CellType.ERROR) {
            toCell.setCellErrorValue(fromCell.getErrorCellValue());
        } else if (fromCellType == CellType.FORMULA) {
            toCell.setCellFormula(fromCell.getCellFormula());
        } else {
        }
    }

    public static void copyRow(XSSFWorkbook wb, XSSFRow oldRow, XSSFRow toRow) {
        toRow.setHeight(oldRow.getHeight());
        for (Iterator<Cell> cellIt = oldRow.cellIterator(); cellIt.hasNext(); ) {
            XSSFCell tmpCell = (XSSFCell) cellIt.next();
            XSSFCell newCell = toRow.createCell(tmpCell.getColumnIndex());
            copyCell(wb, tmpCell, newCell);
        }
    }

    public static void copySheet(XSSFWorkbook wb, XSSFSheet fromSheet, XSSFSheet toSheet) {
        mergeSheetAllRegion(fromSheet, toSheet);
        int length = fromSheet.getRow(fromSheet.getFirstRowNum()).getLastCellNum();
        for (int i = 0; i <= length; i++) {
            toSheet.setColumnWidth(i, fromSheet.getColumnWidth(i));
        }
        for (Iterator<Row> rowIt = fromSheet.rowIterator(); rowIt.hasNext(); ) {
            XSSFRow oldRow = (XSSFRow) rowIt.next();
            XSSFRow newRow = toSheet.createRow(oldRow.getRowNum());
            copyRow(wb, oldRow, newRow);
        }
    }

    public static void main(String[] args) throws Exception {
        String root = System.getProperty("user.dir") + "/src/main/resources";
        File file = new File(root);
        File[] files = file.listFiles();
        if(files.length == 0){
            System.out.println("该目录下对象个数：" + files.length);
            System.out.println("运行结束!");
            return;
        }
        String filepaths[] = new String[files.length];
        System.out.println("该目录下对象个数：" + files.length);
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                filepaths[i] = files[i].toString();
                System.out.println("文件:" + filepaths[i] + " 待处理");
            }
        }

        List<String> sheetNames = Lists.newArrayList();
        XSSFWorkbook newsheet = new XSSFWorkbook();
        for (String fromName : filepaths) {
            InputStream in = new FileInputStream(fromName);
            XSSFWorkbook fromExcel = new XSSFWorkbook(in);
            int length = fromExcel.getNumberOfSheets();
            try {
                if (length <= 1) {
                    XSSFSheet oldSheet = fromExcel.getSheetAt(0);
                    String sheetName = oldSheet.getSheetName();
                    if (!sheetNames.contains(sheetName)) {
                        sheetNames.add(sheetName);
                    } else {
                        sheetName = sheetName + "_" + System.currentTimeMillis();
                    }
                    XSSFSheet newSheet = newsheet.createSheet(sheetName);
                    copySheet(newsheet, oldSheet, newSheet);
                } else {
                    for (int i = 0; i < length; i++) {
                        XSSFSheet oldSheet = fromExcel.getSheetAt(i);
                        String sheetName = oldSheet.getSheetName();
                        if (!sheetNames.contains(sheetName)) {
                            sheetNames.add(sheetName);
                        } else {
                            sheetName = sheetName + "_" + System.currentTimeMillis();
                        }
                        XSSFSheet newSheet = newsheet.createSheet(sheetName);
                        copySheet(newsheet, oldSheet, newSheet);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally{
                fromExcel.close();
            }

        }
        String finalName = root + "\\New.xlsx";
        FileOutputStream fileOutputStream = new FileOutputStream(finalName);
        newsheet.write(fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
        System.out.println("运行结束!");
    }

}
