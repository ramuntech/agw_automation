import Common.ReportUtil;
import Core.BaseTest;
import Model.Menu;
import Pages.AcquisitionGatewayPage;
import Pages.CategoryManagementPage;
import Pages.CommonPage;
import Pages.HomePage;
import org.json.JSONObject;
import org.testng.annotations.Test;

import java.io.IOException;

public class AppTest extends BaseTest {

    @Test(testName = "Validate App CMS Data")
    public void valdiateAppCMSData() throws InterruptedException, IOException {
        CommonPage commonPage = new CommonPage(driver);
        //get menu details
        //commonPage.captureMenuData();
        commonPage.validateMenu();
        commonPage.subMenuValidation();

    }

}