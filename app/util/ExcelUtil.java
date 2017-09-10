package util;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.util.ArrayList;

/**
 * Created by weng on 15-11-25.
 */
public class ExcelUtil {
    public static Sheet getOrCreateSheet(Workbook wb, String sheetname) {
        Sheet sheet = wb.getSheet(sheetname);
        if (sheet == null) {
            sheet = wb.createSheet(sheetname);
        }
        return sheet;
    }

    public static Row getOrCreateRow(Sheet sheet, int var1) {
        Row row = sheet.getRow(var1);
        if (row == null) {
            row = sheet.createRow(var1);
        }
        return row;

    }

    public static Cell getOrCreateCell(Row row, int var1) {
        Cell cell = row.getCell((short) var1);
        if (cell == null) {
            cell = row.createCell((short) var1);
            CellStyle style = cell.getSheet().getWorkbook().createCellStyle();
            cell.setCellStyle(style);
        }
        return cell;
    }

    public static Cell getOrCreateCell(Sheet sheet, int r, int c) {
        Row row = getOrCreateRow(sheet, r);
        Cell cell = row.getCell((short) c);
        if (cell == null) {
            cell = row.createCell((short) c);
            CellStyle style = cell.getSheet().getWorkbook().createCellStyle();
            cell.setCellStyle(style);
        }
        return cell;
    }

    public static void setRegionCellStyle(Sheet sheet, CellStyle style, int startrow, int endrow, int startcolumn, int endcolumn) {
        for (int i = startrow; i <= endrow; i++) {
            for (int j = startcolumn; j <= endcolumn; j++) {
                Cell c = getOrCreateCell(sheet, i, j);
                c.setCellStyle(style);
            }
        }
    }


    public static void setThickBorder(Cell cell, boolean left, boolean top, boolean right, boolean bottom) {
        CellStyle style = cell.getCellStyle();
        if (left) {
            style.setBorderLeft(CellStyle.BORDER_THICK);
        }
        if (top) {
            style.setBorderTop(CellStyle.BORDER_THICK);
        }
        if (right) {
            style.setBorderRight(CellStyle.BORDER_THICK);
        }
        if (bottom) {
            style.setBorderBottom(CellStyle.BORDER_THICK);
        }
        cell.setCellStyle(style);
    }

    public static void setCellColor(Cell cell, short colorIndex) {
        CellStyle style = cell.getCellStyle();
        style.setFillForegroundColor(colorIndex); // for instance, style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cell.setCellStyle(style);
    }

    public static void setRegionColor(Sheet sheet, short colorIndex, int left, int top, int right, int bottom) {
        for (int i = top; i <= bottom; i++) {
            for (int j = left; j <= right; j++) {
                setCellColor(getOrCreateCell(sheet, i, j), colorIndex);
            }
        }
    }


    public static String getCellString(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue().trim();
        }
        return null;
    }

    // for test
    public static ArrayList<ArrayList<String>> parse(String filepath) {
        ArrayList<ArrayList<String>> ret = new ArrayList<ArrayList<String>>();
        try {
            POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(filepath));
            HSSFWorkbook wb = new HSSFWorkbook(fs);

            HSSFSheet sheet = wb.getSheetAt(0);
            HSSFRow row;
            HSSFCell cell;
            int cols = 0; // No of columns
            // get all rows
            int rows; // No of rows
            rows = sheet.getPhysicalNumberOfRows();

            //get max column
            for (int i = 0; i < rows; i++) {
                row = sheet.getRow(i);
                if (row != null) {
                    int tmp = row.getPhysicalNumberOfCells();
                    if (cols < tmp) {
                        cols = tmp;
                    }
                }
            }


            for (int r = 0; r < rows; r++) {
                row = sheet.getRow(r);
                if (row != null) {
                    ArrayList<String> ret_i = new ArrayList<String>();
                    for (int c = 0; c < cols; c++) {
                        String s = getCellString(row.getCell(c));
                        if (s != null) {
                            ret_i.add(s);
                        } else {
                            ret_i.add("");
                        }
                    }
                    ret.add(ret_i);
                }
            }

            wb.close();
            fs.close();

        } catch (Exception ioe) {
            ioe.printStackTrace();
            return null;
        }

        return ret;
    }


    public static int getPicTypeByString(String type) {
        switch (type.toLowerCase()) {
            case "jpg":
            case "jpeg": {
                return Workbook.PICTURE_TYPE_JPEG;
            }
            case "png": {
                return Workbook.PICTURE_TYPE_PNG;
            }
        }
        return Workbook.PICTURE_TYPE_JPEG;//默认当jpg吧
    }

    public static String getCellString(HSSFSheet sheet, int row, int column) {
        Cell cell = sheet.getRow(row).getCell(column);
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC: {
                // Set type as String
                cell.setCellType(Cell.CELL_TYPE_STRING);
                return cell.getStringCellValue().trim();
            }
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue().trim();
        }
        return null;
    }


    public static Double getCellDouble(HSSFSheet sheet, int row, int column) {
        Cell cell = sheet.getRow(row).getCell(column);
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC:
                return cell.getNumericCellValue();
            case Cell.CELL_TYPE_STRING:
                return Double.valueOf(cell.getStringCellValue().trim());
        }
        return null;
    }

    public static Integer getCellInt(HSSFSheet sheet, int row, int column) {
        Double d = getCellDouble(sheet, row, column);
        if (d == null) {
            return null;
        }
        return d.intValue();
    }

    public static boolean hasRow(HSSFSheet sheet, int row) {
        Row r = sheet.getRow(row);
        if (r == null) {
            return false;
        }
        return true;
    }


    /**
     * 根据列序号获取字母,只支持小于702的数字
     */
    public static String getColumnLabels(int columnNum) {
        String sources[] = new String[]{
                "A", "B", "C", "D", "E", "F", "G", "H",
                "I", "J", "K", "L", "M", "N", "O", "P",
                "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
        };

        if (columnNum < 0 || columnNum > 701) {
            throw new IllegalArgumentException();
        } else if (columnNum < 26) {    //序号小于26列,不用组合
            return sources[columnNum];
        } else {
            return (sources[columnNum/26 - 1] + sources[columnNum % 26]);
        }
    }
}