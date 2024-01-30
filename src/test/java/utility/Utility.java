package utility;

import constants.Constants;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import reporting.ExtentReportManager;
import static org.hamcrest.Matchers.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

import static io.restassured.RestAssured.given;

public class Utility extends Constants {
    public static FileInputStream fi;
    public static XSSFWorkbook wb;
    public static XSSFSheet ws;
    public static XSSFRow row;
    Response response;
    public Response validateResponseGet(String endPoint, int statusCode) {
        response = given().
                baseUri(commonBaseUri).
                when().
                get(endPoint).
                then().
//                log().all().
                statusCode(statusCode).
                header("Content-Type", "application/json; charset=utf-8").
                time(lessThan(5000L)).
                extract().response();

        reportDisplay(response);
        return response;
    }

    public static String getCellDataExcel(String excelPath, int sheetAt, int row, int col) throws IOException {

        FileInputStream inputStream = new FileInputStream(excelPath);
        XSSFWorkbook workBook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = workBook.getSheetAt(sheetAt); // sheetAt -0 means first sheet of the Excel
        XSSFCell cell = sheet.getRow(row).getCell(col);// to get on the cell from which we have to extract data

        String data;
        try {
            DataFormatter formatter = new DataFormatter();//typecasting the cell data to string using DataFormatter
            data = formatter.formatCellValue(cell);
        } catch (Exception e) {
            data = "";
        }

        workBook.close();
        inputStream.close();
        return data;
    }

    public static int getRowCount(String excelPath, String xlSheet) throws IOException {
        fi = new FileInputStream(excelPath);
        wb = new XSSFWorkbook(fi);
        ws = wb.getSheet(xlSheet);
        int rowCount = ws.getLastRowNum();
        wb.close();
        fi.close();
        return rowCount;
    }
    public static int getCellCount(String excelPath, String xlSheet, int rowNum) throws IOException{
        fi = new FileInputStream(excelPath);
        wb = new XSSFWorkbook(fi);
        ws = wb.getSheet(xlSheet);
        row = ws.getRow(rowNum);
        int columnCount = row.getLastCellNum();
        wb.close();
        fi.close();
        return columnCount;
    }

    public String customBodyRequestPostUser(String name, String gender, String email, String status) {
        String jsonString;
        jsonString = "{";
        jsonString += "\"name\": \"" + name + "\",";
        jsonString += "\"gender\": \"" + gender + "\",";
        jsonString += "\"email\": \"" + email + "\",";
        jsonString += "\"status\": \"" + status + "\"";
        jsonString += "}";

        return jsonString;
    }

    public Response validatePostCall(String bearer, String postBody, int expStatusCode){
        response=given().
                baseUri(commonBaseUri).
                header("Authorization",
                        "Bearer " + bearer).
                contentType(ContentType.JSON).
                body(postBody).
                when().
                post(endPoint).
                then().
                log().all().
                statusCode(expStatusCode).
                header("Content-Type", "application/json; charset=utf-8").
                extract().response();

        reportDisplay(response);

        return response;
    }

    public String randomStringGenerator(){

        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";// create a string of all characters
        StringBuilder sb = new StringBuilder();// create random string builder
        Random random = new Random();// create an object of Random class

        int length = 4;// specify length of random string

        for(int i = 0; i < length; i++) {

            int index = random.nextInt(alphabet.length());// generate random index number
            char randomChar = alphabet.charAt(index);// get character specified by index
            sb.append(randomChar);// append the character to string builder
        }
        return sb.toString();
    }

    public String newEmailGenerator(){
        String email= randomStringGenerator();
        email= email+"@"+email+"."+"com";
        return email;
    }

    public void reportDisplay(Response response){
        ExtentReportManager.logInfoDetails("Status code is "+response.getStatusCode());
        ExtentReportManager.logInfoDetails("Response body is: ");
        ExtentReportManager.logJson(response.getBody().prettyPrint());
    }

}
