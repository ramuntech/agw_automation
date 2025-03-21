package Pages;

import Core.BasePage;
import org.openqa.selenium.WebDriver;

public class PeriodicTablePage extends BasePage{

    public PeriodicTablePage(WebDriver driver){
        super(driver);
    }

    public void validatePeriodicTablePageContent(){
       comparePageContentWithAPIResponse("/periodic-table");
    }

}
