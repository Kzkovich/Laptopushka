package ru.kzkovich.laptopushka;

import android.inputmethodservice.Keyboard;
import android.util.Log;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static android.content.res.Resources.getSystem;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BLANK;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BOOLEAN;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_ERROR;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_FORMULA;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING;
import static org.apache.poi.ss.usermodel.Row.RETURN_BLANK_AS_NULL;
import static ru.kzkovich.laptopushka.R.string.article_col_name;
import static ru.kzkovich.laptopushka.R.string.empty_cell;
import static ru.kzkovich.laptopushka.R.string.not_found;

public class PriceListParcer {
    private String articleNumber;
    private String localPathToPriceList;
    private XSSFWorkbook workBook;
    private XSSFSheet sheet;
    private Row row;
    private String sheetName = "Tablet";

    public PriceListParcer() {
        this.articleNumber = null;
        this.localPathToPriceList = null;
        this.workBook = null;
        this.sheet = null;
        this.row = null;
    }

    public PriceListParcer(String articleNumber, String localPathToPriceList) {
        this.articleNumber = articleNumber;
        this.localPathToPriceList = localPathToPriceList;
        FileInputStream price = null;
        try {
            price = new FileInputStream(new File(localPathToPriceList));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        workBook = null;
        try {
            workBook = new XSSFWorkbook(price);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.sheet = workBook.getSheet(sheetName);
        this.row = sheet.getRow(getLaptopRowNum());
    }

    private int getLaptopRowNum() {
        int rowNum = 0;
        for (Row row : sheet) {
            String articleStringCellValue = row.getCell(getColNumByName(articleNumber)).getStringCellValue();

            if (articleStringCellValue.equals(articleNumber)) {
                rowNum = row.getRowNum();
                Log.d("article", articleStringCellValue);
            }
        }

        return rowNum;
    }

    public int getColNumByName(String colName) {
        Row headerRow = sheet.getRow(sheet.getFirstRowNum());
        int colNum = 0;
        for (Cell cell : headerRow) {

            if (cell.getStringCellValue().equals(colName)) {
                colNum = cell.getColumnIndex();
            }
        }

        return colNum;
    }

    public String getCellStringValue(int colNum) {
        Cell cell = row.getCell(colNum, RETURN_BLANK_AS_NULL);

        if (cell != null) {
            switch (cell.getCellType()) {
                case CELL_TYPE_FORMULA:
                    return cell.getCellFormula();
                case CELL_TYPE_NUMERIC:
                    return String.valueOf(cell.getNumericCellValue());
                case CELL_TYPE_STRING:
                    return cell.getStringCellValue();
                case CELL_TYPE_BLANK:
                    return "";
                case CELL_TYPE_BOOLEAN:
                    return cell.getBooleanCellValue() ? "true" : "false";
                case CELL_TYPE_ERROR:
                    return String.valueOf(cell.getErrorCellValue());
                default:
                    String notFoundString = getSystem().getString(not_found);
                    return notFoundString + " " + cell.getCellType();
            }
        }
        else {
            return getSystem().getString(empty_cell);
        }
    }
}
