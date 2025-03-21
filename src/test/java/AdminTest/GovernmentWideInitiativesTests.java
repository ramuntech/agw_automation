package AdminTest;

import Core.BaseTest;
import Model.Menu;
import Pages.*;
import org.testng.annotations.Test;

public class GovernmentWideInitiativesTests extends BaseTest {

   /* @Test
    public void validateAGApplication(){
        //validate Home page

        //verify Menu options and page content

    }*/

    @Test (testName = "Validate Category Management")
    public void validateCategoryManagement(){
        HomePage homePage = new HomePage(driver);
        CategoryManagementPage categoryManagementPage = homePage.selectMenuOption(Menu.GovernmenWideInitiatives, Menu.CategoryManagement, CategoryManagementPage.class);
        categoryManagementPage.validateCategoryManagementPageContent();
    }

    @Test (testName = "Validate Shared Services")
    public void validateSharedServices(){
        HomePage homePage = new HomePage(driver);
        SharedServicesPage sharedServicesPage = homePage.selectMenuOption(Menu.GovernmenWideInitiatives, Menu.SharedServices, SharedServicesPage.class);
        sharedServicesPage.validateSharedServicesPageContent();
    }

    @Test (testName = "Validate Hi Def Initiative")
    public void validateHiDefInitiative(){
        HomePage homePage = new HomePage(driver);
        HiDefInitiativePage hiDefInitiativePage = homePage.selectMenuOption(Menu.GovernmenWideInitiatives, Menu.HiDefInitiative, HiDefInitiativePage.class);
        hiDefInitiativePage.validateHiDefInitiativePageContent();
    }

    @Test (testName = "Validate Periodic Table")
    public void validatePeriodicTable(){
        HomePage homePage = new HomePage(driver);
        PeriodicTablePage periodicTablePage = homePage.selectMenuOption(Menu.GovernmenWideInitiatives, Menu.PeriodicableOfAcquisitionInnovations, PeriodicTablePage.class);
        periodicTablePage.validatePeriodicTablePageContent();
    }

    @Test (testName = "validate Additional Resources")
    public void validateAdditionalResources(){
        HomePage homePage = new HomePage(driver);
        AdditionalResourcesPage additionalResourcesPage = homePage.selectMenuOption(Menu.GovernmenWideInitiatives, Menu.AdditionalResources, AdditionalResourcesPage.class);
        additionalResourcesPage.validateAdditionalResourcesPageContent();
    }
}
