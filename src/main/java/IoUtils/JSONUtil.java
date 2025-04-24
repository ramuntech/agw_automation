package IoUtils;

import org.apache.commons.io.FileUtils;
import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class JSONUtil {
    public static String commerceToolsOutputFile = null;  //file name for attributes
    public static String commerceToolsResponseOutputFile = null; // file name for complete response
    public static String fileFullName = null;
    public static JSONArray arrayData = null;

    //create new folder
    private void createNewFolder(String folderName) {
        String filePath = System.getProperty("user.dir") + "\\target\\" + folderName;
        Path path = Paths.get(filePath);
        if (Files.isDirectory(path)) {
            new File(folderName).deleteOnExit();
        }
        File file = new File(filePath);
        file.mkdir();
    }

    //create new file
    public void createResponseOutputFile(String fileName) throws IOException {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        commerceToolsResponseOutputFile = System.getProperty("user.dir") + "\\OutPutFiles\\" + fileName +"_Response_"+ dateFormat.format(date)+".json";
        System.out.println("Response Output File: " + commerceToolsResponseOutputFile);
        FileWriter writer = new FileWriter(commerceToolsResponseOutputFile);
        writer.write("");
        writer.close();

        commerceToolsOutputFile = System.getProperty("user.dir") + "\\OutPutFiles\\" + fileName +"_"+ dateFormat.format(date)+".json";
        System.out.println("Response Output File: " + commerceToolsOutputFile);
        writer = new FileWriter(commerceToolsOutputFile);
        writer.write("");
        writer.close();
    }

    public void createResponseOutputFile(String fileName, String env) throws IOException {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        createNewFolder("OutPutFiles");

        //for individual data extraction
        fileFullName = env+"_"+ fileName +"_Response_"+ dateFormat.format(date);
        commerceToolsResponseOutputFile = System.getProperty("user.dir") + "\\target\\OutPutFiles\\" +fileFullName+".json";
        System.out.println("Response Output File: " + commerceToolsResponseOutputFile);
        FileWriter writer = new FileWriter(commerceToolsResponseOutputFile);
        writer.write("");
        writer.close();

        //for consolidated data extraction
        /*commerceToolsOutputFile = System.getProperty("user.dir") + "\\target\\OutPutFiles\\" + fileName +"_"+ dateFormat.format(date)+".json";
        System.out.println("Response Output File: " + commerceToolsOutputFile);
        writer = new FileWriter(commerceToolsOutputFile);
        writer.write("");
        writer.close();*/
    }

    public void createResponseOutputFileByName(String fileName, String env) throws IOException {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        createNewFolder("OutPutFiles");

        //for individual data extraction
        fileFullName = env+"_"+ fileName +"_Response";
        commerceToolsResponseOutputFile = System.getProperty("user.dir") + "\\target\\OutPutFiles\\" +fileFullName+".json";
        System.out.println("Response Output File: " + commerceToolsResponseOutputFile);
        FileWriter writer = new FileWriter(commerceToolsResponseOutputFile);
        writer.write("");
        writer.close();
    }

    public void appendToOutputFile(String charSeq){
        try{
            String outputPath = commerceToolsResponseOutputFile;
            // User FileWriter to write content to text file
            FileWriter writer = new FileWriter(outputPath, true);
            writer.write(charSeq+ "\n");
            writer.close();
        }catch (Exception ex){

        }
    }

    public void writeDataToJSONFile(){
        try{
            String outputPath = commerceToolsResponseOutputFile;
            // User FileWriter to write content to text file
            FileWriter writer = new FileWriter(outputPath, true);
            writer.write(arrayData.toString());
            writer.close();
        }catch (Exception ex){

        }
    }

    public void writeToOutputFile(String fileName, String env, String content) throws IOException {
        if(commerceToolsResponseOutputFile == null){
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = new Date();
            createNewFolder("OutPutFiles");

            //for individual data extraction
            commerceToolsResponseOutputFile = System.getProperty("user.dir") + "\\target\\OutPutFiles\\" +env+"_"+ fileName +"_Response_"+ dateFormat.format(date)+".json";
            System.out.println("Response Output File: " + commerceToolsResponseOutputFile);
        }
        FileWriter writer = new FileWriter(commerceToolsResponseOutputFile, true);
        writer.append(content + "\n");
        writer.close();


    }

    public static String getAttributeValue(JSONObject json, String attr) {
        String value = null;
        try{
            if(!attr.contains("/")) {
                return  String.valueOf(json.get(attr));
            } else {
                String[] attributes = attr.split("/");
                for(int index=0; index < attributes.length; index++){
                    if(index != attributes.length-1){
                        JSONObject obj = json.getJSONObject(attributes[index]);
                       return getAttributeValue(obj, attr.replace(attributes[index]+"/",""));
                    } else {
                        return getAttributeValue(json, json.getString(attributes[index-1]));
                    }
                }
            }
        }catch (Exception ex){
            return null;
        }
        return value;
    }

    public boolean isJSONArray(String json) {
        try {
            JSONArray array = new JSONArray(json.trim());
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean isJSONObject(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json.trim());
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static JSONArray readTemplateData(String templateName) {
        try {
            String separator = File.separator;
            String filepath =  System.getProperty("user.dir")+"\\src\\main\\resources\\template.json";

            String content = new String(Files.readAllBytes(Paths.get(filepath.replace("\\",separator))));
            JSONObject jsonObject = new JSONObject(content);

            if (jsonObject.has(templateName)) {
                return jsonObject.getJSONArray(templateName);
            } else {
                System.out.println("Template not found: " + templateName);
                return null;
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}
