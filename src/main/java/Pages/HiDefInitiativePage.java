package Pages;

import Core.BasePage;
import org.openqa.selenium.WebDriver;

public class HiDefInitiativePage extends BasePage{

    public HiDefInitiativePage(WebDriver driver){
        super(driver);
    }

    public void validateHiDefInitiativePageContent(){
       comparePageContentWithAPIResponse("/hi-def");
    }

}
