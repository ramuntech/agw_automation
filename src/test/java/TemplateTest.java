import Common.ReportUtil;
import Core.BaseTest;
import Model.Menu;
import Pages.AcquisitionGatewayPage;
import Pages.CategoryManagementPage;
import Pages.CommonPage;
import Pages.HomePage;
import org.json.JSONObject;
import org.testng.annotations.Test;

public class TemplateTest extends BaseTest {

    @Test(testName = "Validate Category Management")
    public void validateCollectionTemplate() throws InterruptedException {
        ReportUtil.addTestToReport("Collection Template");
        CommonPage commonPage = new CommonPage(driver);
        HomePage homePage = new HomePage(driver);
        CategoryManagementPage categoryManagementPage = homePage.selectMenuOption(Menu.GovernmenWideInitiatives, Menu.CategoryManagement, AcquisitionGatewayPage.class);
        JSONObject object = commonPage.captureResponseFromDevTools("/category-management");
        AcquisitionGatewayPage acquisitionGatewayPage = new AcquisitionGatewayPage(driver, object);
        acquisitionGatewayPage.verifyBannerAttr();
        acquisitionGatewayPage.verifyTitleAttr();
        acquisitionGatewayPage.verifyWelcomeTitle();
        acquisitionGatewayPage.verifyWelcomeSection();
        acquisitionGatewayPage.verifyMenuButtons();
        acquisitionGatewayPage.validateListing("collection");
    }

    @Test(testName = "Validate portfolio template")
    public void validatePortfolioTemplate() throws InterruptedException {
        ReportUtil.addTestToReport("portfolio Template");
        CommonPage commonPage = new CommonPage(driver);
        HomePage homePage = new HomePage(driver);
        CategoryManagementPage categoryManagementPage = homePage.selectMenuOption(Menu.ToolsAndResources, Menu.ProcurementCoPilot, AcquisitionGatewayPage.class);
        JSONObject object = commonPage.captureResponseFromDevTools("/procurementcopilot");
        AcquisitionGatewayPage acquisitionGatewayPage = new AcquisitionGatewayPage(driver, object);
        acquisitionGatewayPage.verifyBannerAttr();
        acquisitionGatewayPage.verifyTitleAttr();
        acquisitionGatewayPage.verifyWelcomeTitle();
        acquisitionGatewayPage.verifyWelcomeSection();
        acquisitionGatewayPage.verifyMenuButtons();
        acquisitionGatewayPage.validateListing("portfolio");
    }

    @Test(testName = "Validate Result template")
    public void validateResultTemplate() throws InterruptedException {
        ReportUtil.addTestToReport("result Template");
        CommonPage commonPage = new CommonPage(driver);
        HomePage homePage = new HomePage(driver);
        CategoryManagementPage categoryManagementPage = homePage.selectMenuOption(Menu.ToolsAndResources, Menu.Forcaste, AcquisitionGatewayPage.class);
        JSONObject object = commonPage.captureResponseFromDevTools("/forecast");
        AcquisitionGatewayPage acquisitionGatewayPage = new AcquisitionGatewayPage(driver, object);
        acquisitionGatewayPage.verifyBannerAttr();
        acquisitionGatewayPage.verifyTitleAttr();
        acquisitionGatewayPage.verifyWelcomeTitle();
        acquisitionGatewayPage.verifyWelcomeSection();
        acquisitionGatewayPage.verifyMenuButtons();
        acquisitionGatewayPage.validateListing("result");
    }
}