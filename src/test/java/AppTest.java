import Core.BaseTest;
import Pages.CommonPage;
import org.testng.annotations.Test;

import java.io.IOException;

public class AppTest extends BaseTest {

    @Test(testName = "Validate App CMS Data")
    public void valdiateAppCMSData() throws InterruptedException, IOException {
        CommonPage commonPage = new CommonPage(driver);
        commonPage.validateEachSubMenu();

    }
}
