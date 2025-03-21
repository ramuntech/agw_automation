package Pages;

import Core.BasePage;
import org.openqa.selenium.WebDriver;

public class SharedServicesPage extends BasePage{

    public SharedServicesPage(WebDriver driver){
        super(driver);
    }

    public void validateSharedServicesPageContent(){
       comparePageContentWithAPIResponse("/shared-services");
    }

}
