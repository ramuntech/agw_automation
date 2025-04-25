package Pages;

import org.json.JSONObject;
import org.openqa.selenium.WebDriver;

public class TemplateHelper {

    WebDriver driver= null;

    public TemplateHelper(WebDriver driver){
        this.driver = driver;
    }

    public void validatePageVsTemplate(String endpoint) throws InterruptedException {
        CommonPage commonPage = new CommonPage(driver);
        JSONObject response = commonPage.captureResponseFromDevTools(endpoint);
        if(response == null){
            response = commonPage.captureResponseFromDevTools(endpoint);
        }
        AcquisitionGatewayPage acquisitionGatewayPage = new AcquisitionGatewayPage(driver,response);
        acquisitionGatewayPage.verifyBannerAttr();
        acquisitionGatewayPage.verifyTitleAttr();
        acquisitionGatewayPage.verifyWelcomeTitle();
        acquisitionGatewayPage.verifyWelcomeSection();
        acquisitionGatewayPage.verifyMenuButtons();
        String templateName = response.getString("template");
        acquisitionGatewayPage.validateListing(templateName);
        acquisitionGatewayPage.validateFilter();

    }
}