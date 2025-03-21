package Core;

import Common.Config;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import IoUtils.ExcelUtil;
import IoUtils.JSONUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;

import java.io.File;
import java.io.IOException;

public class APIBase {

    public JSONObject executeAPI(String endpoint){
        RequestSpecification request = RestAssured.given().baseUri(Config.getEnvData("apibase"));
        request.header("Content-Type", "application/json");
        Response response = request.get(endpoint);
        Assert.assertTrue(response.getStatusCode() == 200, "Status code is not 200");
        return new JSONObject(response.getBody().prettyPrint());
    }

    public JSONObject executeAPI(String url, String authToken, String limit, String offset) throws JSONException {
        RequestSpecification request = RestAssured.given().baseUri(url);
        request.header("Content-Type", "application/json");
        request.header("Authorization", authToken);
        request.param("limit",limit);
        request.param("offset",offset);
        String response =  request.get().getBody().prettyPrint();
        return new JSONObject(response);
    }

    public Response runApiRequest(String url, String authToken, String limit, String offset) throws JSONException {
        Response response = null;
        try{
            RequestSpecification request = RestAssured.given().baseUri(url);
            request.header("Content-Type", "application/json");
            request.header("Authorization", authToken);
            request.param("limit",limit);
            request.param("offset",offset);
            response = request.get();
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        return response;

    }

    public JSONObject runAPI(String url, String authToken, String limit, String where) throws JSONException {
        RequestSpecification request = RestAssured.given().baseUri(url);
        request.header("Content-Type", "application/json");
        request.header("Authorization", authToken);
        request.param("withTotal",false);
        request.param("limit",limit);
        request.param("sort","key");
        if(where != null) {
            request.param("where", where.split("_")[0]);
        }
        String response =  request.get().getBody().prettyPrint();
       return new JSONObject(response);
    }
    private static double getFileSizeMegaBytes(File file) {
        return (double) file.length() / (1024 * 1024);
    }


}
