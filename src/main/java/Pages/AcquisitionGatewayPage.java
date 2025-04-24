package Pages;

import Common.ReportUtil;
import Core.BasePage;
import IoUtils.JSONUtil;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;

import java.time.Duration;
import java.util.*;

public class AcquisitionGatewayPage extends BasePage {

    private  JSONObject jsonResponse;

    public AcquisitionGatewayPage(WebDriver driver,JSONObject jsonResponse){
        super(driver);
        this.jsonResponse = jsonResponse;
    }


    //locators
    protected SoftAssert softAssert = new SoftAssert();

    //common locators
    protected String menu = "//span[contains(text(),'%s')]/parent::button";
    protected String menuOpiton = "//span[contains(text(),'%s')]/parent::a";
    private String banner = "//div[@class='header-banner-inner usa-banner__inner']//p[contains(text(),'%s')]";
    private String title = "//a[text()=' Home ']/parent::li/following-sibling::li/a[contains(text(),'%s')]";
    private By welcomeToTitle = By.xpath("//h4[contains(text(),'Welcome to ')]");
    private By welcomeToContent = By.xpath("//div[@role='textbox']/child::p");
    private String menuSecondary = "//span[text()='Share']/parent::div/parent::button";
    private String sliderElement = "//h3[contains(text(),'%s')]";
    private String termTitle = "//span[contains(text(),'%s')]";
    private String termDesc = "";
    private String filterElement ="//form[@class='usa-search usa-search--small search-form ng-pristine ng-valid ng-touched']";
    private String headerTitle = "//h4[@class='ag-header__title' and contains(normalize-space(.), '%s')]";

    private String titleBody = "//h4[@class='ag-header__title' and contains(normalize-space(.),'%s')]/ancestor::section/following-sibling::section//div[@class='ag-body-body']//p[normalize-space(text()) != '']";
    private String keyValue = "//section[contains(normalize-space(.),'%s')]/following-sibling::*/div//b[contains(text(),'%s')]/parent::div/following-sibling::div[contains(text(),'%s')]";
    private String keyLable = "//section[contains(normalize-space(.),'%s')]/following-sibling::*/div//b[contains(text(),'%s')]";
    private String titleContent = "//div[@class='ag-body-additional_content__key']/following-sibling::div[contains(text(),'%s')]";
    private String filterLabelXpath ="//div[@class='usa-label ag-filter-label']/span[text()='%s']";
    private String filterTextBox = "//label[text()='%s']/following::input[1]";
    private String filterOptions ="//ul[@id='search-result']/li[contains(.,'%s')]";
    private String filterkeyValue ="//div[@class='ag-body-additional_content__key']/b[contains(text(),'%s')]/parent::div/following-sibling::div[contains(text(),'%s')]";
    private String filterKey ="//div[@class='ag-body-additional_content__key']/b[contains(text(),'%s')]";
    private String filterResult = "//div[@class='grid-row ag-items-wrapper' and contains(text(),' No Results Found ')]";
    private String subLinkButton ="//button[@title='%s']";
    private String subLinkMainTitle ="//h2[contains(text(),'%s')]";
    private By subLinkBody =By.xpath("//div[@role='textbox']");
    private String sublinkTitle ="//a[contains(@title,'%s')]";
    private String sliderHeader = "//h3[contains(.,'%s')]";
    private String sliderContent = "//h3[contains(.,'%s')]/following-sibling::div";
    private String slideXpath= "//a[contains(@title,'%s')]";

    //banner validation
    public void verifyBannerAttr(){
        if(jsonResponse.isEmpty()){
            return;
        }
        String banner_response = JSONUtil.getAttributeValue(jsonResponse, "banner/heading/value");
        softAssert.assertNotNull(isElementPresent(By.xpath(String.format(banner, banner_response))),
                banner_response + " is not displayed");
        ReportUtil.PASS(driver, "Verified banner as " + banner_response);
        System.out.println("Banner Verification: " + banner_response + " is done");
    }

    //title validation
    public void verifyTitleAttr(){
        if(jsonResponse.isEmpty()  ){
            return;
        }
        String title_response = JSONUtil.getAttributeValue(jsonResponse, "title");
        softAssert.assertNotNull(isElementPresent(By.xpath(String.format(title, title_response))),
                title_response + " Page title validation");
        ReportUtil.PASS(driver, "Verified title as " + title_response);
        System.out.println("Page Title Verification: " + title_response + " is done");
    }

    //welcome title validation
    public void verifyWelcomeTitle(){
        String title_response = JSONUtil.getAttributeValue(jsonResponse, "title");
        softAssert.assertTrue(getElement(welcomeToTitle).getText().contains(title_response),
                title_response + " Welcome To title validation");
        ReportUtil.PASS(driver, "Verified Welcome To Title as " + title_response);
        System.out.println("Welcome To Title Verification: " + title_response + " is done");
    }

    //welcome content section
    public void verifyWelcomeSection(){
        String message = jsonResponse.optString("message");
        if(jsonResponse.isEmpty() || message == null  ){
            return;
        }
        String title_response = JSONUtil.getAttributeValue(jsonResponse, "title");
        String title_Content = JSONUtil.getAttributeValue(jsonResponse, "message");
        String expectedText = Jsoup.parse(title_Content).text();
        String actualText = Jsoup.parse(getElement(welcomeToContent).getAttribute("innerHTML")).text();
        softAssert.assertTrue(expectedText.contains(actualText), title_Content + " Welcome To Message validation");
        System.out.println("Welcome To Message Verification: " + title_response + " is done");
    }

    //Menu validation
    public void verifyMenuButtons(){
        if(jsonResponse.isEmpty() ){
            return;
        }
        // Verify export CSV
        boolean csvAvailable = Boolean.parseBoolean(JSONUtil.getAttributeValue(jsonResponse, "menu/secondary/export/export"));
        if (csvAvailable) {
            String csv_response = JSONUtil.getAttributeValue(jsonResponse, "menu/secondary/export/label");
            softAssert.assertNotNull(isElementPresent(By.xpath(String.format(menuSecondary, csv_response))),
                    csv_response + " validation failed");
            ReportUtil.PASS(driver, "Verified CSV button visibility");
            System.out.println("Share CSV Button Verification: " + csv_response + " is done");
        } else {
            System.out.println("Export CSV Button Verification: Export CSV is not enabled on the screen");
        }

        // Verify share button
        boolean shareAvailable = Boolean.parseBoolean(JSONUtil.getAttributeValue(jsonResponse, "menu/secondary/share/share"));
        if (shareAvailable) {
            String share_response = JSONUtil.getAttributeValue(jsonResponse, "menu/secondary/share/label");
            softAssert.assertNotNull(isElementPresent(By.xpath(String.format(menuSecondary, share_response))),
                    share_response + " validation failed");
            ReportUtil.PASS(driver, "Verified Share button visibility");
            System.out.println("Share Button Verification: " + share_response + " is done");
        } else {
            System.out.println("Share Button Verification: Share is not enabled on the screen");
        }

        // Verify print button
        boolean printAvailable = Boolean.parseBoolean(JSONUtil.getAttributeValue(jsonResponse, "menu/secondary/print/print"));
        if (printAvailable) {
            String print_response = JSONUtil.getAttributeValue(jsonResponse, "menu/secondary/print/label");
            softAssert.assertNotNull(isElementPresent(By.xpath(String.format(menuSecondary, print_response))),
                    print_response + " validation failed");
            ReportUtil.PASS(driver, "Verified Print button visibility");
            System.out.println("Print Button Verification: " + print_response + " is done");
        } else {
            System.out.println("Print Button Verification: Print is not enabled on the screen");
        }
    }

    //listing validation
    public void validateListing(String templateName) throws InterruptedException {
        try {

            //return if list is empty
            if (jsonResponse.isEmpty()) {
                return;
            }

            if (templateName.equals("result")) {
                validateResultTemplate();
            } else if (templateName.equals("collection")) {
                validateCollectionTemplate();
            } else if (templateName.equals("portfolio")) {
                validatePortfolioTemplate();
            }
        } catch (Exception e) {
            ReportUtil.FAIL(driver,e.getMessage());
        }
    }

    //validate Filter
    public void validateFilter(){
        String filtersString = JSONUtil.getAttributeValue(jsonResponse, "filters");
        try {
            if (filtersString.equals("[]")) {
                return;
            }
            JSONObject filtersJson = new JSONObject(filtersString);
            if (filtersJson.isEmpty() || jsonResponse.isEmpty()) {
                return;
            }
        } catch (Exception e) {
            if (filtersString.equals("[]")) {
                return;
            }
        }
        //filter
        String filterString = JSONUtil.getAttributeValue(jsonResponse, "filters");
        assert filterString != null;
        JSONObject filtersObject = new JSONObject(filterString);
        for (String key : filtersObject.keySet()) {
            try {
                JSONObject filter = filtersObject.getJSONObject(key);
                JSONObject viewObject = filter.getJSONObject("view");
                // Get the filter label
                String label = viewObject.getString("label");
                softAssert.assertNotNull(isElementPresent(By.xpath(String.format(filterLabelXpath, label))), "filter label is not present" + label);
                System.out.println("Filter Label: " + label);
                softAssert.assertNotNull(isElementPresent(By.xpath(String.format(filterTextBox, label))), "filter label textbox not present is not present" + label);
//               if(label.equals("Estimated Award FY-QTR")){
//                   continue;
//               }
                scrollToElement(By.xpath(String.format(filterTextBox, label)));
                Thread.sleep(3000);
                clickElement(By.xpath(String.format(filterTextBox, label)));
                Thread.sleep(3000);
                JSONArray options = filter.optJSONArray("options");
                for (int i = 0; i < Math.min(options.length(), 50); i++) {
                    JSONObject option = options.getJSONObject(i);
                    String optionValue = option.optString("name").trim();
                    softAssert.assertNotNull(isElementPresent(By.xpath(String.format(filterOptions, optionValue))), "filter option not available");
                    System.out.println(" - " + optionValue);
                }
                scrollToElement(By.xpath("//button[@id='ag-reset-filters']"));
                Thread.sleep(1000);
                clickElement(By.xpath("//button[@id='ag-reset-filters']"));
                Thread.sleep(1000);
                scrollToElement(By.xpath("(//section[@class='usa-banner'])[2]"));
                Thread.sleep(1000);

            } catch (Exception e) {
                ReportUtil.FAIL(driver,e.getMessage());
            }
            softAssert.assertAll();
        }
        validateFilterCombinations();

    }

    public void validateFilterCombinations(){

    }



    //collection listing validation
    public void validateCollectionTemplate(){

        WebDriverWait hardWait = new WebDriverWait(driver, Duration.ofSeconds(30));
        // Process slider sections sorted by weight
        String title_response = JSONUtil.getAttributeValue(jsonResponse, "title");
        JSONObject slider = new JSONObject(JSONUtil.getAttributeValue(jsonResponse, "listing"));
        List<JSONObject> sortedSliderEntries = new ArrayList<>();

        // Collect and sort slider entries
        Iterator<String> sliderKeys = slider.keys();
        while (sliderKeys.hasNext()) {
            String key = sliderKeys.next();
            if (!key.matches("\\d+")) continue;
            try {
                JSONObject entry = slider.getJSONObject(key);
                sortedSliderEntries.add(entry);
            } catch (Exception e) {
                System.out.println(key + " is not a valid slider entry");
            }
        }

        // Sort slider entries by weight
        sortedSliderEntries.sort(Comparator.comparingInt(e -> e.getInt("weight")));

        // Validate sorted slider entries
        for (JSONObject listingEntity : sortedSliderEntries) {
            try {
                // Verify slider title
                String listingTitle_response = JSONUtil.getAttributeValue(listingEntity, "title");
                softAssert.assertNotNull(isElementPresent(By.xpath(String.format(sliderElement, listingTitle_response))),
                        listingTitle_response + " validation failed");
                ReportUtil.PASS(driver, "Verified " + listingTitle_response + " visibility");
                System.out.println(listingTitle_response + " Verification is done");

                // Verify slider description
                String listingDescription_response = JSONUtil.getAttributeValue(listingEntity, "description");
                if (listingDescription_response != null) {
                    listingDescription_response = Jsoup.parse(listingDescription_response).wholeText();
                    if (!listingDescription_response.contains("null")) {
                        softAssert.assertNotNull(isElementPresent(By.xpath("//*[contains(text(),'" + listingDescription_response + "')]")),
                                listingTitle_response + " description validation failed");
                        ReportUtil.PASS(driver, "Verified " + listingTitle_response + " description");
                        System.out.println(listingTitle_response + " Description Verification is done");
                    }
                }

                // Process terms sorted by weight
                JSONObject terms = new JSONObject(JSONUtil.getAttributeValue(listingEntity, "terms"));
                List<JSONObject> sortedTerms = new ArrayList<>();

                // Collect and sort terms
                Iterator<String> termKeys = terms.keys();
                while (termKeys.hasNext()) {
                    String termKey = termKeys.next();
                    try {
                        JSONObject term = terms.getJSONObject(termKey);
                        sortedTerms.add(term);
                    } catch (Exception e) {
                        System.out.println(termKey + " is not a valid term entry");
                    }
                }

                // Sort terms by weight
                sortedTerms.sort(Comparator.comparingInt(t -> t.getInt("weight")));

                // Validate sorted terms
                for (JSONObject term : sortedTerms) {
                    try{
                        String entityName_response = term.getString("name");
                        String entityDescription_response = String.valueOf(term.opt("description"));

                        // Verify term name
                        if (!entityName_response.contains("null")) {
                            softAssert.assertNotNull(isElementPresent(By.xpath(String.format(termTitle, entityName_response))),
                                    entityName_response + " validation failed");
                            ReportUtil.PASS(driver, "Verified " + entityName_response + " visibility");
                            System.out.println(entityName_response + " Verification is done");
//                            getElement(By.xpath(String.format(termTitle, entityName_response))).click();
//                            hardWait.until(ExpectedConditions.elementToBeClickable(By.xpath(String.format(title, title_response))));
//                            JSONObject object = captureResponseFromDevTool(term.getString("tid"));
//                            validateSubmenuLinks(object);
//                            driver.navigate().back();
//                            hardWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(String.format(title, title_response))));
                        }

                        // Verify term description
                        if (!entityDescription_response.contains("null")) {
                            String cleanDescription = Jsoup.parse(entityDescription_response).wholeText();
                            softAssert.assertNotNull(isElementPresent(By.xpath("//*[contains(text(),'" + cleanDescription + "')]")),
                                    entityName_response + " description validation failed");
                            ReportUtil.PASS(driver, "Verified " + entityName_response + " description");
                            System.out.println(entityName_response + " Description Verification is done");
                        }
                    }catch (Exception e){
                        ReportUtil.FAIL(driver,e.getMessage());
                    }
                }
            }catch (Exception e){
                ReportUtil.FAIL(driver,e.getMessage());
            }
        }
        softAssert.assertAll();
    }



//    public void validateSubmenuLinks(JSONObject jsonResponse) throws InterruptedException {
//        String name = JSONUtil.getAttributeValue(jsonResponse,"alias") + "/"+JSONUtil.getAttributeValue(jsonResponse,"title") ;
//        ReportUtil.addTestToReport(name);
//        WebDriverWait hardWait = new WebDriverWait(driver, Duration.ofSeconds(30));
//        try {
//            // Verify banner
//            String banner_response = JSONUtil.getAttributeValue(jsonResponse, "banner/heading/value");
//            softAssert.assertNotNull(isElementPresent(By.xpath(String.format(banner, banner_response))), banner_response + " is not displayed");
//            ReportUtil.PASS(driver, "Verified banner as " + banner_response);
//            System.out.println("Banner Verification: " + banner_response + " is done");
//
//            // Verify title
//            String title_response = JSONUtil.getAttributeValue(jsonResponse, "title");
//            softAssert.assertNotNull(isElementPresent(By.xpath(String.format(title, title_response))),
//                    title_response + " Page title validation");
//            ReportUtil.PASS(driver, "Verified title as " + title_response);
//            System.out.println("Page Title Verification: " + title_response + " is done");
//
//            //share
//            boolean shareAvailable = Boolean.parseBoolean(JSONUtil.getAttributeValue(jsonResponse, "menu/secondary/share/share"));
//            if (shareAvailable) {
//                String share_response = JSONUtil.getAttributeValue(jsonResponse, "menu/secondary/share/label");
//                softAssert.assertNotNull(isElementPresent(By.xpath(String.format(menuSecondary, share_response))),
//                        share_response + " validation failed");
//                ReportUtil.PASS(driver, "Verified Share button visibility");
//                System.out.println("Share Button Verification: " + share_response + " is done");
//            } else {
//                System.out.println("Share Button Verification: Share is not enabled on the screen");
//            }
//
//            JSONObject details = jsonResponse.getJSONObject("details");
//            JSONArray data = details.getJSONArray("data");
//
//            for (int i = 0; i < data.length(); i++) {
//                try {
//                    JSONObject category = data.getJSONObject(i);
//                    System.out.println("Category: " + category.getString("name"));
//
//
//                    WebElement buttonElement = getElement(By.xpath(String.format(subLinkButton, category.getString("name"))));
//                    String ariaExpanded = buttonElement.getAttribute("aria-expanded");
//                    if (ariaExpanded.equals("false")) {
//                        //  driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
//                        hardWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(String.format(subLinkButton, category.getString("name")))));
//                        scrollToElement(By.xpath(String.format(subLinkButton, category.getString("name"))));
//                        Thread.sleep(2000);
//                        hardWait.until(ExpectedConditions.elementToBeClickable(By.xpath(String.format(subLinkButton, category.getString("name")))));
//                        clickElement(By.xpath(String.format(subLinkButton, category.getString("name"))));
//                        //Thread.sleep(1000);
//                    }
//                    JSONArray nodes = category.getJSONArray("nodes");
//                    for (int j = 0; j < nodes.length(); j++) {
//                        JSONObject node = nodes.getJSONObject(j);
//                        JSONArray titleArray = node.getJSONArray("title");
//                        String title = titleArray.getJSONObject(0).getString("value");
//
//                        JSONArray fieldLabelArray = node.getJSONArray("field_label");
//                        String label = fieldLabelArray.getJSONObject(0).getString("value");
//
//                        //driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
//                        hardWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(String.format(sublinkTitle, label))));
//                        //   Thread.sleep(2000);
//
//                        //title
//                        scrollToElement(By.xpath(String.format(sublinkTitle, label)));
//                        //driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
//                        Thread.sleep(2000);
//                        hardWait.until(ExpectedConditions.elementToBeClickable(By.xpath(String.format(sublinkTitle, label))));
//                        clickElement(By.xpath(String.format(sublinkTitle, label)));
//
//                        getElement(By.xpath(String.format(subLinkMainTitle, title)));
//
//                        softAssert.assertNotNull(getElement(By.xpath(String.format(subLinkMainTitle, title))), "Title not found: " + title);
//
//
//                        //body
//                        JSONArray bodyArray = node.getJSONArray("body");
//                        String value = bodyArray.getJSONObject(0).getString("value");
//                        String expectedText = Jsoup.parse(value).text();
//                        String actualText = Jsoup.parse(getElement(subLinkBody).getAttribute("innerHTML")).text();
//                        softAssert.assertTrue(actualText.contains(expectedText), "Body content mismatch for: " + value);
//
//
//                    }
//                }
//                catch (Exception e){
//                    ReportUtil.FAIL(driver,e.getMessage());
//                }
//            }
//        }
//        catch (Exception e){
//            ReportUtil.FAIL(driver,e.getMessage());
//        }
//
//    }


    public void validateResultTemplate() throws InterruptedException {
        try {
            String dataString = JSONUtil.getAttributeValue(jsonResponse, "listing/data");
            assert dataString != null;
            JSONObject data = new JSONObject(dataString);
            Iterator<String> keys = data.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject id = data.getJSONObject(key);
                JSONObject render = id.getJSONObject("render");
                //header title
                String title = Jsoup.parse(render.getString("title")).text().replaceAll("\\s+", " ").trim();


                softAssert.assertNotNull(isElementPresent(By.xpath(String.format(headerTitle, title))), "Header title not found: " + title);

                //title body
                String body = Jsoup.parse(render.getString("body")).text();
                softAssert.assertNotNull(isElementPresent(By.xpath(String.format(titleBody, title))), "Body not found: " + body);

                Iterator<String> values = render.keys();
                //title content
                while (values.hasNext()) {
                    String value = values.next();
                    if (value.equals("nid") || value.equals("title") || value.equals("body") || value.equals("field_naics_code")) {
                        continue;
                    }

                    String label = convertFieldKeyToLabel(value);
                    String content = Jsoup.parse(render.getString(value)).text();
                    softAssert.assertNotNull(isElementPresent(By.xpath(String.format(keyLable, title, label))), "label not found");
                    if (content.isEmpty()) {
                        continue;
                    }
                    softAssert.assertNotNull(isElementPresent(By.xpath(String.format(keyValue, title, label, content))), "Key-Value pair not found: " + label + " → " + content);


                }
                break;
            }

            softAssert.assertAll();
        } catch (Exception e) {
            ReportUtil.FAIL(driver,e.getMessage());
        }
    }

    private String convertFieldKeyToLabel(String fieldKey) {
        switch (fieldKey) {
            case "field_result_id": return "Agency:";
            case "field_organization": return "Organization/Contracting Office:";
            case "field_place_of_performance": return "Place of Performance:";
            case "field_source_listing_id": return "Listing ID:";
            case "field_award_status": return "Acquisition Phase:";
            case "field_contract_type": return "Contract Type:";
            case "field_estimated_award_fy": return "Estimated Award FY:";
            case "field_estimated_contract_v_max": return "Estimated Contract Value:";
            case "field_naics_code": return "NAICS Code:";
            case "field_region": return "Region:";
            case "field_acquisition_strategy": return "Acquisition Strategy/Type of Set-Aside:";
            case "field_type_of_awardee": return "Type of Awardee:";
            case "field_period_of_performance": return "Period of Performance:";
            case "field_hi_def_targeted_outcomes": return "Hi-Def Targeted Outcomes:";
            case "field_agency": return "Managing Agency:";
            case "field_availability_info": return "Availability:";

            default: return fieldKey;
        }
    }



    public void validatePortfolioTemplate() throws InterruptedException {
        String tabsString = JSONUtil.getAttributeValue(jsonResponse,"sidebar/tabs");
        JSONObject tabs = new JSONObject(tabsString);
        // TreeMap to sort by weight (ascending)
        TreeMap<Integer, String> sortedLabels = new TreeMap<>();

        for (String key : tabs.keySet()) {
            JSONObject tab = tabs.getJSONObject(key);
            int weight = Integer.parseInt(tab.getString("weight"));
            String label = tab.getString("label");
            sortedLabels.put(weight, label);
        }
        for (String label : sortedLabels.values()) {
            String formattedXpath = String.format(slideXpath, label);
            softAssert.assertTrue(isElementPresent(By.xpath(formattedXpath)), label + " not present");
        }
        for (String label : sortedLabels.values()) {
            String formattedXpath = String.format(slideXpath, label);
            softAssert.assertTrue(isElementPresent(By.xpath(formattedXpath)), label + " not present");
            scrollToElement(By.xpath(formattedXpath));
            clickElement(By.xpath(formattedXpath));
            Thread.sleep(3000);
            validateSlides(jsonResponse);
            break;
        }
    }
    public void validateSlides(JSONObject jsonResponse) {
        String dataString = JSONUtil.getAttributeValue(jsonResponse, "listing/data");
        assert dataString != null;

        JSONArray data = new JSONArray(dataString);
        // Iterate over the array elements
        for (int i = 0; i < data.length(); i++) {
            JSONObject id = data.getJSONObject(i); // Get the individual item
            JSONObject render = id.getJSONObject("render");
            String title = render.getString("title");
            String decodedTitle = StringEscapeUtils.unescapeHtml4(title);
            String content = render.getString("body");
            // Validate the title and content using the XPath
            softAssert.assertNotNull(isElementPresent(By.xpath(String.format(sliderHeader, decodedTitle))), decodedTitle + " slider title not present");
            softAssert.assertNotNull(isElementPresent(By.xpath(String.format(sliderContent, decodedTitle))), decodedTitle + " slider content not present");
            softAssert.assertTrue(getElement(By.xpath(String.format(sliderContent, decodedTitle))).getText().equals(content));
        }
    }








}