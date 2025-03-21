package IoUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExcelUtil {

    Workbook workBook = null;
    static String outputFile = null;
    static List<Map<String, String>> PDPData = new ArrayList<>();
    static List<String> responseData = new ArrayList<>();
    static JsonObject data = null;

    private void readRequiredResponseAttributesFromFile(String fileName) {
        try {
            String path = System.getProperty("user.dir") + "\\src\\test\\resources\\outparams\\" + fileName + ".txt";
            data = (JsonObject) JsonParser.parseReader(new FileReader(path));
        } catch (Exception ex) {
            System.out.println("Unable to read required Response Attributes data from " + fileName + ".json");
        }
    }

    public boolean isJSONArray(String input) {
        try {
            new JSONArray(input);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public void createResponseOutputFile() throws IOException {
        if(outputFile == null) {
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = new Date();
            System.out.println(dateFormat.format(date));
            outputFile = System.getProperty("user.dir") + "\\OutPutFiles\\ResponseOutput_" + dateFormat.format(date) + ".json";
            System.out.println("Response Output File: " + outputFile);
            FileWriter writer = new FileWriter(outputFile);
            writer.write("==================API Response===============\n");
            writer.close();
        }
    }

    public void writeResponseToOutputFile(String response, String APITitle) {
        JSONObject json = null;
        try {
            String outputPath = outputFile;
            // User FileWriter to write content to text file
            FileWriter writer = new FileWriter(outputPath, true);
            if(APITitle != null && !APITitle.isEmpty()) {
                writer.write("==================" + APITitle + "===============\n");
            }
            try {
                json = new JSONObject(response);
            }catch (Exception e) {
                json = new JSONObject("{\"ERROR\":\"Unable to write JSON data to request file....\","+
                                      "\"RESPONSE\":\""+response+"\""+"}\n");
            }
            System.out.println("RESPONSE TO FILE:::"+json.toString());
            writer.write(json.toString()+"\n");
            writer.close();
        } catch (Exception ex) {
            System.out.println("Unable to write response data to text file. " + ex.getLocalizedMessage()+"\n");
        } finally {
            responseData.clear();
        }
    }

    public void writeMultiResponseToFile(String response, String APITitle) {
        JSONObject json = null;
        try {
            String outputPath = outputFile;

            // User FileWriter to write content to text file
            FileWriter writer = new FileWriter(outputPath, true);
            if(APITitle != null && !APITitle.isEmpty()) {
                writer.write("==================" + APITitle + "===============\n");
            }

            try {
                json = new JSONObject(response);
            }catch (Exception e) {
                json = new JSONObject("{\"ERROR\":\"Unable to write JSON data to request file....\","+
                        "\"RESPONSE\":\""+response+"\""+"}\n");
            }
            String formattedString = json.getJSONArray("results").toString().substring(1);
            formattedString = formattedString.substring(0, formattedString.length()-1);
            System.out.println("RESPONSE TO FILE:::"+json.toString());
            writer.append(formattedString+",");
            writer.close();
        } catch (Exception ex) {
            System.out.println("Unable to write response data to text file. " + ex.getLocalizedMessage()+"\n");
        } finally {
            responseData.clear();
        }
    }

    public void saveResponseData(Object data, String params) throws JSONException {
        responseData.add(data.toString());
        if (!params.isEmpty()) {
            if (isJSONArray(data.toString())) {
                JSONArray records = new JSONArray(data.toString());
                for (int index = 0; index < records.length(); index++) {
                    JSONObject response = records.getJSONObject(index);
                    Map<String, String> product = null;
                    product = new HashMap<>();
                    String[] columns = params.split(",");
                    for (String each : columns) {
                        product.put(each, getKey(response, each));
                    }
                    PDPData.add(product);
                }
            } else {
                JSONObject response = (JSONObject) data;
                Map<String, String> product = null;
                product = new HashMap<>();
                String[] columns = params.split(",");
                for (String each : columns) {
                    product.put(each, getKey(response, each));
                }
                PDPData.add(product);
            }
        }
    }

    public void loadResponseData(Object data) {
        responseData.add(data.toString());
        JsonObject response = JsonParser.parseString(data.toString()).getAsJsonObject();
        Map<String, String> product = null;

        JsonArray online = response.getAsJsonArray("online");
        for (int index = 0; index < online.size(); index++) {
            product = new HashMap<>();
            product.put("productId", response.get("productId").toString());
            product.put("isStoreBopisEligible", response.get("isStoreBopisEligible").toString());
            product.put("availableQuantity", String.valueOf(online.get(index).getAsJsonObject().get("availableQuantity")));
            product.put("inventoryStatus", String.valueOf(online.get(index).getAsJsonObject().get("inventoryStatus")));
            product.put("isClearance", String.valueOf(online.get(index).getAsJsonObject().get("isClearance")));
            product.put("SPECIALORDER", String.valueOf(online.get(index).getAsJsonObject().get("SPECIALORDER")));
            product.put("isStoreSTSEligible", String.valueOf(online.get(index).getAsJsonObject().get("isStoreSTSEligible")));
            String deliveryMessageKey = online.get(index).getAsJsonObject().getAsJsonObject("deliveryMessage").getAsJsonObject("storeDeliveryMessage").get("key").toString();
            product.put("deliveryMessage", deliveryMessageKey);
            String onlineDeliveryKey = online.get(index).getAsJsonObject().getAsJsonObject("deliveryMessage").getAsJsonObject("onlineDeliveryMessage").get("key").toString();
            product.put("onlineDelivery", onlineDeliveryKey);
            PDPData.add(product);
        }
    }

    public void loadRecentlyViewedData(Object data) {
        responseData.add(data.toString());
        JsonObject response = JsonParser.parseString(data.toString()).getAsJsonObject();
        Map<String, String> product = null;

        JsonArray items = response.getAsJsonObject("profile").getAsJsonObject("recentlyViewedProducts").getAsJsonArray("item");
        for (int index = 0; index < items.size(); index++) {
            product = new HashMap<>();
            product.put("name", String.valueOf(items.get(index).getAsJsonObject().get("name")));
            product.put("giftCardFlag", String.valueOf(items.get(index).getAsJsonObject().get("giftCardFlag")));
            product.put("freeShipping", String.valueOf(items.get(index).getAsJsonObject().get("freeShipping")));
            product.put("mapPriceFlag", String.valueOf(items.get(index).getAsJsonObject().get("mapPriceFlag")));
            product.put("mapPrice", String.valueOf(items.get(index).getAsJsonObject().get("mapPrice")));
           /* String salePrice = items.get(index).getAsJsonObject().getAsJsonObject("defaultSku").getAsJsonObject("salePrice").getAsString();
            product.put("salePrice", salePrice);
            String skuId = items.get(index).getAsJsonObject().getAsJsonObject("defaultSku").getAsJsonObject("skuId").toString();
            product.put("skuId", skuId);*/
            PDPData.add(product);
        }
    }

    private Object getData(JSONObject data, String path) throws JSONException {
        if (!path.contains("/")) {
            return path;
        } else {
            String attr = path.substring(0, path.indexOf("/"));
            JSONObject flt = (JSONObject) data.get(attr);
            return getData(flt, attr.substring(path.indexOf("/") + 1));
        }
    }

    private void createWorkBook(String fileName) {
        try {
            workBook = new XSSFWorkbook();
        } catch (Exception ex) {
            System.out.println("Unable to create " + fileName + " file. " + ex.getLocalizedMessage());
        }
    }

    public void writePDPDataToFile(String fileName) {
        System.out.println("LOADED DATA: " + PDPData.toString());
        //JsonObject response =JsonParser.parseString(list.toString()).getAsJsonObject();
        //System.out.println("Product ID: " + response.get("productId").toString());
        createWorkBook(fileName);
        XSSFSheet sheet = (XSSFSheet) workBook.createSheet("PDPData");
        List<String> columns = new ArrayList<>();
        PDPData.get(0).keySet().stream().forEach(x -> columns.add(x));
        int columnCount = columns.size();

        //add columns to sheet
        Row row = sheet.createRow(0);
        for (int col = 0; col < columnCount; col++) {
            row.createCell(col).setCellValue(columns.get(col).toString());
        }

        //add data to sheet
        for (int index = 1; index < PDPData.size(); index++) {
            row = sheet.createRow(index);
            Map<String, String> record = PDPData.get(index);
            for (int colIndex = 0; colIndex < columnCount; colIndex++) {
                row.createCell(colIndex).setCellValue(record.get(columns.get(colIndex).toString()));
            }
        }

        try {
            FileOutputStream out = new FileOutputStream(
                    new File(System.getProperty("user.dir") + "\\OutPutFiles\\" + fileName + ".xlsx"));
            workBook.write(out);
            out.close();
        } catch (Exception ex) {
            System.out.println("Unable to write response data to Excel file. " + ex.getLocalizedMessage());
            System.out.println(ex.getMessage());
        }
    }

    public void writeResponse(String fileName) {
        try {
            String outputPath = System.getProperty("user.dir") + "\\OutPutFiles\\output_" + fileName + ".txt";

            // User FileWriter to write content to text file
            FileWriter writer = new FileWriter(outputPath);
            for (String each : responseData) {
                writer.write(each);
                writer.write("\n");
            }
            writer.close();
        } catch (Exception ex) {
            System.out.println("Unable to write response data to text file. " + ex.getLocalizedMessage());
        } finally {
            responseData.clear();
        }
    }

    public void writeResponseToFile(Object response, String fileName) {
        try {
            String outputPath = System.getProperty("user.dir") + "\\OutPutFiles\\output_" + fileName + ".txt";

            // User FileWriter to write content to text file
            FileWriter writer = new FileWriter(outputPath);
            for (String each : responseData) {
                writer.write(each);
                writer.write("\n");
            }
            writer.close();
        } catch (Exception ex) {
            System.out.println("Unable to write response data to text file. " + ex.getLocalizedMessage());
        } finally {
            responseData.clear();
        }
    }

    public static String getKey(JSONObject json, String key) throws JSONException {
        String value = null;
        boolean exists = json.has(key);
        Iterator<?> keys;
        String nextKeys;
        if (!exists) {
            keys = json.keys();
            while (keys.hasNext()) {
                nextKeys = (String) keys.next();
                try {
                    if (json.get(nextKeys) instanceof JSONObject) {
                        if (exists == false) {
                            getKey(json.getJSONObject(nextKeys), key);
                        }
                    } else if (json.get(nextKeys) instanceof JSONArray) {
                        JSONArray jsonarray = json.getJSONArray(nextKeys);
                        for (int i = 0; i < jsonarray.length(); i++) {
                            String jsonarrayString = jsonarray.get(i).toString();
                            JSONObject innerJSOn = new JSONObject(jsonarrayString);
                            if (exists == false) {
                                getKey(innerJSOn, key);
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
        } else {
            value = parseObject(json, key);
        }
        return value;
    }

    public static String parseObject(JSONObject json, String key) throws JSONException {
        return json.get(key).toString();
    }

    public String[] readExcelData(String filePath, String sheetName){
        List<String> sheetData = new ArrayList<String>();
        try {
            FileInputStream fis=new FileInputStream(new File(filePath));
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            XSSFSheet sheet = wb.getSheet(sheetName);
            Iterator<Row> row = sheet.rowIterator();
            boolean isFirst = true;

            while (row.hasNext()) {
                if(!isFirst) {
                    Row eachRow = row.next();
                    Iterator<Cell> cellIterator
                            = eachRow.cellIterator();

                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        String cellValue = new DataFormatter().formatCellValue(cell);
                        sheetData.add(cellValue);
                    }
                } else {
                    isFirst = false;
                }
            }

        }catch (Exception e) {

        }
        return  sheetData.stream().toArray(String[] ::new);
    }
}
