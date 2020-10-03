package ru.kzkovich.laptopushka.services;

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

import ru.kzkovich.laptopushka.models.CharacteristicsConfig;
import ru.kzkovich.laptopushka.models.LaptopCharacteristics;

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
    private XSSFWorkbook workBook;
    private XSSFSheet sheet;
    private XSSFRow row;
    private CharacteristicsConfig config;
    private String sheetName = "Tablet";

    public PriceListParcer() {
        this.articleNumber = null;
        this.workBook = null;
        this.sheet = null;
        this.row = null;
    }

    public PriceListParcer(CharacteristicsConfig config, String localPathToPriceList) {
        this.config = config;
        this.articleNumber = config.getArticul();
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
        int nArticle = getColNumByName(ARTICLE_COL_NAME);
        int nBrand = getColNumByName(BRAND_COL_NAME);
        int nModel = getColNumByName(MODEL_COL_NAME);
        int nCPU = getColNumByName(CPU_COL_NAME);
        int nScreen = getColNumByName(SCREEN_COL_NAME);
        int nRAM = getColNumByName(RAM_COL_NAME);
        int nHDD = getColNumByName(HDD_COL_NAME);
        int nGraphics = getColNumByName(GRAPHICS_COL_NAME);
        int nResolution = getColNumByName(RESOLUTION_COL_NAME);
        int nMatrix = getColNumByName(MATRIX_COL_NAME);
        Double nPrice = Double.parseDouble(getCellStringValue(getColNumByName(PRICE_COL_NAME), row));

        Double priceInUAH = calculatePrice(nPrice, config);

        LaptopCharacteristics characteristics = new LaptopCharacteristics(
                getCellStringValue(nArticle, row),
                getCellStringValue(nBrand, row),
                getCellStringValue(nModel, row),
                getCellStringValue(nCPU, row),
                getCellStringValue(nScreen, row),
                getCellStringValue(nRAM, row),
                getCellStringValue(nHDD, row),
                getCellStringValue(nGraphics, row),
                getCellStringValue(nResolution, row),
                getCellStringValue(nMatrix, row),
                priceInUAH
        );


        return characteristics;
    }

    private Double calculatePrice(Double nPrice, CharacteristicsConfig config) {
        Double rate = config.getRate();
        return rate * nPrice;
    }

}
