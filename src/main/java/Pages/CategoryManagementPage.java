package Pages;

import Core.BasePage;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CategoryManagementPage extends BasePage{

    public CategoryManagementPage(WebDriver driver){
        super(driver);
    }

    public void validateCategoryManagementPageContent(){
       comparePageContentWithAPIResponse("/category-management");
    }

}
