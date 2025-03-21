package Pages;

import Core.BasePage;
import org.openqa.selenium.WebDriver;

public class AdditionalResourcesPage extends BasePage{

    public AdditionalResourcesPage(WebDriver driver){
        super(driver);
    }

    public void validateAdditionalResourcesPageContent(){
       comparePageContentWithAPIResponse("/additional-resources");
    }

}
