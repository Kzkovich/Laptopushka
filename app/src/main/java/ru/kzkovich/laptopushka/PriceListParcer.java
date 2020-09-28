package ru.kzkovich.laptopushka;

import android.util.Log;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BLANK;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BOOLEAN;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_ERROR;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_FORMULA;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING;
import static org.apache.poi.ss.usermodel.Row.RETURN_BLANK_AS_NULL;
import static ru.kzkovich.laptopushka.utils.Constants.ARTICLE_COL_NAME;
import static ru.kzkovich.laptopushka.utils.Constants.BRAND_COL_NAME;
import static ru.kzkovich.laptopushka.utils.Constants.CPU_COL_NAME;
import static ru.kzkovich.laptopushka.utils.Constants.EMPTY_CELL;
import static ru.kzkovich.laptopushka.utils.Constants.GRAPHICS_COL_NAME;
import static ru.kzkovich.laptopushka.utils.Constants.HDD_COL_NAME;
import static ru.kzkovich.laptopushka.utils.Constants.MATRIX_COL_NAME;
import static ru.kzkovich.laptopushka.utils.Constants.MODEL_COL_NAME;
import static ru.kzkovich.laptopushka.utils.Constants.NOT_FOUND;
import static ru.kzkovich.laptopushka.utils.Constants.PRICE_COL_NAME;
import static ru.kzkovich.laptopushka.utils.Constants.RAM_COL_NAME;
import static ru.kzkovich.laptopushka.utils.Constants.RESOLUTION_COL_NAME;
import static ru.kzkovich.laptopushka.utils.Constants.SCREEN_COL_NAME;

public class PriceListParcer {
    private String articleNumber;
    private String localPathToPriceList;
    private XSSFWorkbook workBook;
    private XSSFSheet sheet;
    private XSSFRow row;

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
            String articleStringCellValue = getCellStringValue(getColNumByName(ARTICLE_COL_NAME), row);
            Log.d("lookingForArticleRow", getCellStringValue(getColNumByName(ARTICLE_COL_NAME), row));

            if (articleStringCellValue.equals(articleNumber)) {
                rowNum = row.getRowNum();
                Log.d("article", "Found in a row " + rowNum + ": " + articleStringCellValue);
                break;
            }
        }

        return rowNum;
    }

    public int getColNumByName(String colName) {
        Row headerRow = sheet.getRow(sheet.getFirstRowNum());
        int colNum = 0;
        for (Cell cell : headerRow) {

            if (getCellStringValue(cell).equals(colName)) {
                colNum = cell.getColumnIndex();
            }
        }

        return colNum;
    }

    private String getCellStringValue(int colNum, Row row) {
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
                    return NOT_FOUND + " " + cell.getCellType();
            }
        }
        else {
            return EMPTY_CELL;
        }
    }

    private String getCellStringValue(Cell cell) {
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
                    return NOT_FOUND + " " + cell.getCellType();
            }
        }
        else {
            return EMPTY_CELL;
        }
    }

    public LaptopCharacteristics getCharacteristicsObject() {
//        LaptopCharacteristics characteristics = new LaptopCharacteristics(
//                getCellStringValue(getColNumByName(ARTICLE_COL_NAME), row),
//                getCellStringValue(getColNumByName(BRAND_COL_NAME), row),
//                getCellStringValue(getColNumByName(MODEL_COL_NAME), row),
//                getCellStringValue(getColNumByName(CPU_COL_NAME), row),
//                getCellStringValue(getColNumByName(SCREEN_COL_NAME), row),
//                getCellStringValue(getColNumByName(RAM_COL_NAME), row),
//                getCellStringValue(getColNumByName(HDD_COL_NAME), row),
//                getCellStringValue(getColNumByName(GRAPHICS_COL_NAME), row),
//                getCellStringValue(getColNumByName(RESOLUTION_COL_NAME), row),
//                getCellStringValue(getColNumByName(MATRIX_COL_NAME), row),
//                25
//        );
        LaptopCharacteristics characteristics = new LaptopCharacteristics();
        characteristics.setArticul(getCellStringValue(getColNumByName(ARTICLE_COL_NAME), row));
        characteristics.setBrand(getCellStringValue(getColNumByName(BRAND_COL_NAME), row));
        characteristics.setCpu(getCellStringValue(getColNumByName(CPU_COL_NAME), row));
        characteristics.setGraphics(getCellStringValue(getColNumByName(GRAPHICS_COL_NAME), row));
        characteristics.setHdd(getCellStringValue(getColNumByName(HDD_COL_NAME), row));
        characteristics.setMatrixType(getCellStringValue(getColNumByName(MATRIX_COL_NAME), row));
        characteristics.setModel(getCellStringValue(getColNumByName(MODEL_COL_NAME), row));
        characteristics.setRam(getCellStringValue(getColNumByName(RAM_COL_NAME), row));
        characteristics.setResolution(getCellStringValue(getColNumByName(RESOLUTION_COL_NAME), row));
        characteristics.setScreen(getCellStringValue(getColNumByName(SCREEN_COL_NAME), row));
        characteristics.setPriceInDollars(Double.parseDouble(getCellStringValue(getColNumByName(PRICE_COL_NAME), row)));

        return characteristics;
    }
}
