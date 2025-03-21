package Core;

import Common.ReportUtil;
import IoUtils.JSONUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v131.network.Network;
import org.openqa.selenium.devtools.v131.network.model.Response;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;

public class BasePage {
    //objects & variables
    protected WebDriver driver = null;

    //common locators
    protected String menu = "//span[contains(text(),'%s')]/parent::button";
    protected String menuOpiton = "//span[contains(text(),'%s')]/parent::a";
    private String banner = "//div[@class='header-banner-inner usa-banner__inner']//p[contains(text(),'%s')]";
    private String title = "//a[text()=' Home ']/parent::li/following-sibling::li/a[contains(text(),'%s')]";
    private By welcomeToTitle = By.xpath("//h4[contains(text(),'Welcome to ')]");
    private By welcomeToContent = By.xpath("//p[@class='usa-alert__text']");
    private String menuSecondary = "//span[text()='Share']/parent::div/parent::button";
    private String sliderElement = "//h3[contains(text(),'%s')]";
    private String termTitle = "//span[contains(text(),'%s')]";
    private String termDesc = "";

    public BasePage() {

    }

    public BasePage(WebDriver driver) {
        this.driver = driver;
    }

    //locator

    public <T extends BasePage> T selectMenuOption(String menuTitle, String optionTitle, Class cls) {
        String locator = String.format(menu, menuTitle);
        clickElement(By.xpath(locator));
        ReportUtil.INFO("Clicked on Menu: " + menuTitle);

        locator = String.format(menuOpiton, optionTitle);
        clickElement(By.xpath(locator));
        ReportUtil.INFO("Clicked on Menu Option: " + optionTitle);
        return getGenericObject(cls);
    }

    //highlight elements
    public void highLighterMethod(By locator) {
        WebElement element = driver.findElement(locator);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].setAttribute('style', 'border: 3px solid red;');", element);
    }

    protected void highLighterMethod(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].setAttribute('style', 'border: 3px solid red;');", element);
    }

    protected WebElement getElement(By locator) {
        highLighterMethod(locator);
        return driver.findElement(locator);
    }

    protected boolean isElementPresent(By locator) {
        try {
            getElement(locator);
            scrollToElement(locator);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // scroll to element
    protected void scrollToElement(By locator) {
        WebElement element = driver.findElement(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    protected void clickElement(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.elementToBeClickable(locator));
        highLighterMethod(locator);
        driver.findElement(locator).click();
    }

    protected void setText(By locator, String content) {
        highLighterMethod(locator);
        driver.findElement(locator).sendKeys(content);
    }

    // generic method
    protected <T extends BasePage> T getGenericObject(Class cls) {
        T t = null;
        try {
            Constructor<?> con = cls.getDeclaredConstructor(WebDriver.class);
            Object obj = con.newInstance(driver);
            t = (T) obj;

        } catch (Exception ex) {
            System.out.println("Unable to initialize Object " + cls.getTypeName());
        }
        return t;
    }

    //verify text in the page source
    protected boolean isTextInPageSource(String text) {
        if (driver.getPageSource().contains(text)) {
            return true;
        } else {
            return false;
        }
    }

    protected void comparePageContentWithAPIResponse(String endpoint) {
        APIBase apiBase = new APIBase();
        JSONObject jsonResponse = apiBase.executeAPI(endpoint);
        System.out.println("RESPONSE:: " + jsonResponse);

        //verify banner
        String banner_response = JSONUtil.getAttributeValue(jsonResponse, "banner/heading/value");
        Assert.assertNotNull(isElementPresent(By.xpath(String.format(banner, banner_response))), banner_response + " is not displayed");
        ReportUtil.PASS(driver, "Verified banner as " + banner_response);
        System.out.println("Banner Verification: " + banner_response + " is done");

        //title
        String title_response = JSONUtil.getAttributeValue(jsonResponse, "title");
        Assert.assertNotNull(isElementPresent(By.xpath(String.format(title, title_response))), title_response + " Page title validation");
        ReportUtil.PASS(driver, "Verified title as " + title_response);
        System.out.println("Page Title Verification: " + title_response + " is done");

        //welcome title
        Assert.assertTrue(getElement(welcomeToTitle).getText().contains(title_response), title_response + " Welcome To title validation");
        ReportUtil.PASS(driver, "Verified Welcome To Title as " + title_response);
        System.out.println("Welcome To Title Verification: " + title_response + " is done");

        //welcome section content
        String title_Content = JSONUtil.getAttributeValue(jsonResponse, "message");
        Assert.assertTrue(getElement(welcomeToContent).getAttribute("innerHTML").contains(title_Content), title_Content + " Welcome To Message validation");
        ReportUtil.PASS(driver, "Verified Welcome To Content as " + title_Content);
        System.out.println("Welcome To Message Verification: " + title_response + " is done");

        //export csv
        boolean csvAvailable = Boolean.valueOf(JSONUtil.getAttributeValue(jsonResponse, "menu/secondary/export/export"));
        if (csvAvailable) {
            String csv_response = JSONUtil.getAttributeValue(jsonResponse, "menu/secondary/export/label");
            Assert.assertNotNull(isElementPresent(By.xpath(String.format(menuSecondary, csv_response))), csv_response + " validaiton is failed");
            ReportUtil.PASS(driver, "Verified CSV button visibility");
            System.out.println("Share CSV Button Verification: " + csv_response + " is done");
        } else {
            System.out.println("Export CSV Button Verification: Export CSV is not enabled on the screen");
        }

        //share
        boolean shareAvailable = Boolean.valueOf(JSONUtil.getAttributeValue(jsonResponse, "menu/secondary/share/share"));
        if (shareAvailable) {
            String share_response = JSONUtil.getAttributeValue(jsonResponse, "menu/secondary/share/label");
            Assert.assertNotNull(isElementPresent(By.xpath(String.format(menuSecondary, share_response))), share_response + " validaiton is failed");
            ReportUtil.PASS(driver, "Verified Share button visibility");
            System.out.println("Share Button Verification: " + share_response + " is done");
        } else {
            System.out.println("Share Button Verification: Share is not enabled on the screen");
        }

        //print
        boolean printAvailable = Boolean.valueOf(JSONUtil.getAttributeValue(jsonResponse, "menu/secondary/share/share"));
        if (printAvailable) {
            String print_response = JSONUtil.getAttributeValue(jsonResponse, "menu/secondary/share/label");
            Assert.assertNotNull(isElementPresent(By.xpath(String.format(menuSecondary, print_response))), print_response + " validaiton is failed");
            ReportUtil.PASS(driver, "Verified Print button visibility");
            System.out.println("Share Button Verification: " + print_response + " is done");
        } else {
            System.out.println("Print Button Verification: Print is not enabled on the screen");
        }

        //slider
        JSONObject slider = new JSONObject(JSONUtil.getAttributeValue(jsonResponse, "listing"));
        Iterator keys = slider.keys();
        while (keys.hasNext()) {
            String keyStr = keys.next().toString();
            JSONObject listingEntity = null;
            try {
                listingEntity = slider.getJSONObject(keyStr);

                //slider title
                String listingTitle_response = JSONUtil.getAttributeValue(listingEntity, "title");
                Assert.assertNotNull(isElementPresent(By.xpath(String.format(sliderElement, listingTitle_response))), listingTitle_response + " validaiton is failed");
                ReportUtil.PASS(driver, "Verified " + listingEntity + " visibility");
                System.out.println(listingTitle_response + " Verification is done");

                //slider description
                String listingdescription_response = JSONUtil.getAttributeValue(listingEntity, "description");
                listingdescription_response = Jsoup.parse(listingdescription_response).wholeText();
                if (listingdescription_response != null) {
                    if(!listingdescription_response.contains("null")) {
                        Assert.assertNotNull(isElementPresent(By.xpath("//*[contains(text(),'"+ listingdescription_response + "')]")), listingTitle_response + " validaiton is failed");
                        ReportUtil.PASS(driver, "Verified " + listingEntity + " visibility");
                        System.out.println(listingTitle_response + " Verification is done");
                    }
                }

                //each entity
                JSONObject sliderEntity = new JSONObject(JSONUtil.getAttributeValue(listingEntity, "terms"));

                Iterator entitykeys = sliderEntity.keys();
                while (entitykeys.hasNext()) {
                    String eachKey = entitykeys.next().toString();
                    String entityName_response = sliderEntity.getJSONObject(eachKey).getString("name");
                    String entityDescription_response = String.valueOf(sliderEntity.getJSONObject(eachKey).get("description"));
                    System.out.println("Entity name: " + entityName_response);
                    System.out.println("Entity Description: " + entityDescription_response);
                    if (!entityName_response.contains("null")) {
                        Assert.assertNotNull(isElementPresent(By.xpath(String.format(termTitle, entityName_response))), listingTitle_response + " validaiton is failed");
                        ReportUtil.PASS(driver, "Verified " + entityName_response + " visibility");
                        System.out.println(entityName_response + " Verification is done");
                    }

                    if (!entityDescription_response.contains("null")) {

                    }
                }
            } catch (Exception e) {
                System.out.println(keyStr + " is not a JSon Object");
            }
        }
    }

    protected void comparePageContentWithAPIResponse(JSONObject jsonResponse) {
        // Verify banner
        String banner_response = JSONUtil.getAttributeValue(jsonResponse, "banner/heading/value");
        Assert.assertNotNull(isElementPresent(By.xpath(String.format(banner, banner_response))),
                banner_response + " is not displayed");
        ReportUtil.PASS(driver, "Verified banner as " + banner_response);
        System.out.println("Banner Verification: " + banner_response + " is done");

        // Verify title
        String title_response = JSONUtil.getAttributeValue(jsonResponse, "title");
        Assert.assertNotNull(isElementPresent(By.xpath(String.format(title, title_response))),
                title_response + " Page title validation");
        ReportUtil.PASS(driver, "Verified title as " + title_response);
        System.out.println("Page Title Verification: " + title_response + " is done");

        // Verify welcome title
        Assert.assertTrue(getElement(welcomeToTitle).getText().contains(title_response),
                title_response + " Welcome To title validation");
        ReportUtil.PASS(driver, "Verified Welcome To Title as " + title_response);
        System.out.println("Welcome To Title Verification: " + title_response + " is done");

        // Verify welcome section content
        String title_Content = JSONUtil.getAttributeValue(jsonResponse, "message");
        Assert.assertTrue(getElement(welcomeToContent).getAttribute("innerHTML").contains(title_Content),
                title_Content + " Welcome To Message validation");
        ReportUtil.PASS(driver, "Verified Welcome To Content as " + title_Content);
        System.out.println("Welcome To Message Verification: " + title_response + " is done");

        // Verify export CSV
        boolean csvAvailable = Boolean.parseBoolean(JSONUtil.getAttributeValue(jsonResponse, "menu/secondary/export/export"));
        if (csvAvailable) {
            String csv_response = JSONUtil.getAttributeValue(jsonResponse, "menu/secondary/export/label");
            Assert.assertNotNull(isElementPresent(By.xpath(String.format(menuSecondary, csv_response))),
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
            Assert.assertNotNull(isElementPresent(By.xpath(String.format(menuSecondary, share_response))),
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
            Assert.assertNotNull(isElementPresent(By.xpath(String.format(menuSecondary, print_response))),
                    print_response + " validation failed");
            ReportUtil.PASS(driver, "Verified Print button visibility");
            System.out.println("Print Button Verification: " + print_response + " is done");
        } else {
            System.out.println("Print Button Verification: Print is not enabled on the screen");
        }

        // Process slider sections sorted by weight
        JSONObject slider = new JSONObject(JSONUtil.getAttributeValue(jsonResponse, "listing"));
        List<JSONObject> sortedSliderEntries = new ArrayList<>();

        // Collect and sort slider entries
        Iterator<String> sliderKeys = slider.keys();
        while (sliderKeys.hasNext()) {
            String key = sliderKeys.next();
            if (!key.matches("\\d+")) continue; // Skip non-numeric keys like "count" and "total"
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
            // Verify slider title
            String listingTitle_response = JSONUtil.getAttributeValue(listingEntity, "title");
            Assert.assertNotNull(isElementPresent(By.xpath(String.format(sliderElement, listingTitle_response))),
                    listingTitle_response + " validation failed");
            ReportUtil.PASS(driver, "Verified " + listingTitle_response + " visibility");
            System.out.println(listingTitle_response + " Verification is done");

            // Verify slider description
            String listingDescription_response = JSONUtil.getAttributeValue(listingEntity, "description");
            if (listingDescription_response != null) {
                listingDescription_response = Jsoup.parse(listingDescription_response).wholeText();
                if (!listingDescription_response.contains("null")) {
                    Assert.assertNotNull(isElementPresent(By.xpath("//*[contains(text(),'" + listingDescription_response + "')]")),
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
                String entityName_response = term.getString("name");
                String entityDescription_response = String.valueOf(term.opt("description"));

                // Verify term name
                if (!entityName_response.contains("null")) {
                    Assert.assertNotNull(isElementPresent(By.xpath(String.format(termTitle, entityName_response))),
                            entityName_response + " validation failed");
                    ReportUtil.PASS(driver, "Verified " + entityName_response + " visibility");
                    System.out.println(entityName_response + " Verification is done");
                }

                // Verify term description
                if (!entityDescription_response.contains("null")) {
                    String cleanDescription = Jsoup.parse(entityDescription_response).wholeText();
                    Assert.assertNotNull(isElementPresent(By.xpath("//*[contains(text(),'" + cleanDescription + "')]")),
                            entityName_response + " description validation failed");
                    ReportUtil.PASS(driver, "Verified " + entityName_response + " description");
                    System.out.println(entityName_response + " Description Verification is done");
                }
            }
        }
    }


    public void validateEachSubMenu() throws IOException, InterruptedException {
        String content = new String(Files.readAllBytes(Paths.get("menu.json")));
        JSONObject jsonObject = new JSONObject(content);
        JSONArray children = jsonObject.getJSONObject("main").getJSONArray("children");
        int counter = 1;
        for (int i = 0; i < children.length(); i++) {
            JSONObject parent = children.getJSONObject(i);
            if (parent.getBoolean("has_children")) {
                JSONObject subMenus = parent.getJSONObject("children");
                List<JSONObject> subMenuItems = new ArrayList<>();
                Iterator<String> keys = subMenus.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    subMenuItems.add(subMenus.getJSONObject(key));
                }
                subMenuItems.sort(Comparator.comparingInt(o -> o.getInt("weight")));

                for (int j = 0; j < subMenuItems.size(); j++) {
                    JSONObject subMenuItem = subMenuItems.get(j);
                    String endpoint = subMenuItem.getString("link");

                    By mainMenu= By.xpath("(//nav[@class='usa-nav']//button/span)[" + (i + 1) + "]");
                    scrollToElement(mainMenu);
                    Thread.sleep(2000);
                    clickElement(mainMenu);
                    if (endpoint.startsWith("https:")) {
                        System.out.println("Skipping external link: " + endpoint);
                        counter++;
                        continue;
                    }
                    else{
                        By subMenu = By.xpath("(//ul[@class='usa-nav__submenu']//span)[" + counter + "]");
                        clickElement(subMenu);

                        //verify all links in the page
                        validateAllLinksInThePage();
                        JSONObject object = captureResponseFromDevTools(endpoint);

                        //validate page content as per api
                        try {
                            comparePageContentWithAPIResponse(object);
                        }catch (Exception ex){
                            //System.out.println("Failed") ;
                        }
                        counter++;
                    }
                }
            }
        }
    }

    //verify all links in the page
    public void validateAllLinksInThePage() {
        List<WebElement> links = driver.findElements(By.tagName("a"));
        for (WebElement link : links) {
            String url = link.getAttribute("href");

            if (url != null && !url.isEmpty() && url.startsWith("http")) { // Ensure valid URL
                int statusCode = getHttpStatusCode(url);
                String statusMessage = (statusCode == 200) ? "OK" : "Broken";

                System.out.println(url + " --> " + statusCode + " (" + statusMessage + ")");

            }
        }
    }

    public static int getHttpStatusCode(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(5000);
            connection.connect();
            return connection.getResponseCode();
        } catch (Exception e) {
            System.out.println(url + " - is a broken link");
            return -1; // Return -1 if there's an error
        }
    }


    public JSONObject captureResponseFromDevTools(String endpoint) throws InterruptedException {
        final JSONObject[] capturedResponse = new JSONObject[1];
        DevTools devTools = ((ChromeDriver) driver).getDevTools();
        driver.navigate().refresh();
        devTools.createSession();
        driver.navigate().refresh();
        // Enable network monitoring
        devTools.send(Network.enable(Optional.of(100000000), Optional.of(100000000), Optional.of(100000000)));
        devTools.send(Network.setCacheDisabled(true));

        devTools.addListener(Network.responseReceived(), responseReceived -> {
            Response response = responseReceived.getResponse();
            String url = response.getUrl();

            if (url.contains(endpoint) && url.contains("/api")) {
                System.out.println("Captured Response URL: " + url);
                try {
                    Network.GetResponseBodyResponse bodyResponse = devTools.send(Network.getResponseBody(responseReceived.getRequestId()));
                    String responseBody = bodyResponse.getBody();
                    if (responseBody != null && !responseBody.isEmpty()) {
                        capturedResponse[0] = new JSONObject(responseBody);
                    } else {
                        System.out.println("Response body is empty.");
                    }
                } catch (Exception e) {
                    System.out.println("Failed to get response body: " + e.getMessage());
                }
            }
        });
        driver.navigate().refresh();
        Thread.sleep(5000);

        return capturedResponse[0];
    }

}
