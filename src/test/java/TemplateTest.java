import Common.ReportUtil;
import Core.BaseTest;
import IoUtils.JSONUtil;
import Model.Menu;
import Pages.*;
import com.google.gson.JsonArray;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.Test;

public class TemplateTest extends BaseTest {

    @Test(testName = "Validate Category Management")
    public void validateCollectionTemplate() throws InterruptedException {
        ReportUtil.addTestToReport("Collection Template");
        //read menu list from file for the selected template
        JSONArray jsonArray =JSONUtil.readTemplateData("collection");
        //verify all menus page content
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject eachMenu = jsonArray.getJSONObject(i);
            String menu = eachMenu.optString("menu");
            String option = eachMenu.optString("option");
            String endpoint = eachMenu.optString("endpoint");
            HomePage homePage = new HomePage(driver);
            homePage.selectMenuOption(menu, option, AcquisitionGatewayPage.class);
            TemplateHelper templateHelper = new TemplateHelper(driver);
            templateHelper.validatePageVsTemplate(endpoint);
        }
    }

    @Test(testName = "Validate Result template")
    public void validateResultTemplate() throws InterruptedException {
        ReportUtil.addTestToReport("result Template");
        //read menu list from file for the selected template
        JSONArray jsonArray =JSONUtil.readTemplateData("result");
        //verify all menus page content
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject eachMenu = jsonArray.getJSONObject(i);
            String menu = eachMenu.optString("menu");
            String option = eachMenu.optString("option");
            String endpoint = eachMenu.optString("endpoint");
            HomePage homePage = new HomePage(driver);
            homePage.selectMenuOption(menu, option, AcquisitionGatewayPage.class);
            TemplateHelper templateHelper = new TemplateHelper(driver);
            templateHelper.validatePageVsTemplate(endpoint);
        }
    }

    @Test(testName = "Validate portfolio template")
    public void validatePortfolioTemplate() throws InterruptedException {
        ReportUtil.addTestToReport("portfolio Template");
        //read menu list from file for the selected template
        JSONArray jsonArray =JSONUtil.readTemplateData("portfolio");
        //verify all menus page content
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject eachMenu = jsonArray.getJSONObject(i);
            String menu = eachMenu.optString("menu");
            String option = eachMenu.optString("option");
            String endpoint = eachMenu.optString("endpoint");
            HomePage homePage = new HomePage(driver);
            homePage.selectMenuOption(menu, option, AcquisitionGatewayPage.class);
            TemplateHelper templateHelper = new TemplateHelper(driver);
            templateHelper.validatePageVsTemplate(endpoint);
        }
    }
}