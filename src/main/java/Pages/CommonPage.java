package Pages;

import Common.ReportUtil;
import Core.BasePage;
import IoUtils.JSONUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v131.network.Network;
import org.openqa.selenium.devtools.v131.network.model.RequestId;
import org.openqa.selenium.devtools.v131.network.model.Response;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class CommonPage extends BasePage {
  String subMenu = "//ul[@class='usa-nav__submenu']//span[contains(.,'%s')]";
  String header ="//h4[@class='usa-alert__heading']";

  public CommonPage(WebDriver driver){
    super(driver);
  }

  public void captureMenuData() throws InterruptedException {
    DevTools devTools = ((ChromeDriver) driver).getDevTools();
    devTools.createSession();
    Thread.sleep(2000);
    devTools.send(Network.enable(Optional.of(100000000), Optional.of(100000000), Optional.of(100000000)));
    devTools.send(Network.setCacheDisabled(true));
    devTools.addListener(Network.responseReceived(), responseReceived -> {
      Response response = responseReceived.getResponse();
      RequestId requestId = responseReceived.getRequestId();
      String url = response.getUrl();

      if (url.contains("/sbc")) {
        System.out.println("Captured Response URL: " + url);
        try {
          Network.GetResponseBodyResponse bodyResponse = devTools.send(Network.getResponseBody(requestId));
          String body = bodyResponse.getBody();
          JSONObject jsonResponse = new JSONObject(body);
          JSONObject mainObject = jsonResponse.getJSONObject("header").getJSONObject("main");
          JSONArray childrenArray = mainObject.getJSONArray("children");
          JSONObject finalOutput = new JSONObject();
          JSONObject mainOutput = new JSONObject();
          JSONArray outputChildrenArray = new JSONArray();
          for (int i = 0; i < childrenArray.length(); i++) {
            JSONObject menuItem = childrenArray.getJSONObject(i);
            JSONObject outputItem = new JSONObject();
            outputItem.put("title", menuItem.getString("title"));
            outputItem.put("weight", menuItem.getString("weight"));
            outputItem.put("has_children", menuItem.optBoolean("has_children", false));
            if (menuItem.has("children") && menuItem.get("children") instanceof JSONObject) {
              JSONObject childrenObject = menuItem.getJSONObject("children");
              JSONObject outputChildren = new JSONObject();
              for (String key : childrenObject.keySet()) {
                JSONObject child = childrenObject.getJSONObject(key);
                JSONObject childData = new JSONObject();

                childData.put("title", child.getString("title"));
                childData.put("weight", child.getString("weight"));
                childData.put("link", child.getString("link"));

                outputChildren.put(key, childData);
              }

              outputItem.put("children", outputChildren);
            }

            outputChildrenArray.put(outputItem);
          }
          mainOutput.put("children", outputChildrenArray);
          finalOutput.put("main", mainOutput);

          try (FileWriter file = new FileWriter("menu.json")) {
            file.write(finalOutput.toString(4));
            System.out.println("JSON file created successfully.");
          } catch (IOException e) {
            e.printStackTrace();
          }

        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
    driver.navigate().refresh();

    Thread.sleep(5000);
  }



  public void validateMenu() throws IOException, InterruptedException {
    Thread.sleep(5000);
    String content = new String(Files.readAllBytes(Paths.get("menu.json")));
    JSONObject jsonObject = new JSONObject(content);
    JSONObject mainObject = jsonObject.getJSONObject("main");
    JSONArray children = mainObject.getJSONArray("children");
    List<JSONObject> topLevelItems = new ArrayList<>();
    for (int i = 0; i < children.length(); i++) {

      topLevelItems.add(children.getJSONObject(i));
    }
    topLevelItems.sort(Comparator.comparingInt(o -> o.getInt("weight")));
    for (int i = 0; i < topLevelItems.size(); i++) {
      try{
      String expectedTitle = topLevelItems.get(i).getString("title");
      By locator = By.xpath("(//nav[@class='usa-nav']//button/span)[" + (i + 1) + "]");
      String actualTitle = getElement(locator).getText();
      Assert.assertEquals(actualTitle, expectedTitle, "Order mismatch at index " + (i + 1));
    }
      catch (Exception e){
        ReportUtil.FAIL(driver,e.getMessage());
      }
    }
  }

  public void subMenuValidation() throws IOException, InterruptedException {
    Thread.sleep(5000);
    String content = new String(Files.readAllBytes(Paths.get("menu.json")));
    JSONObject jsonObject = new JSONObject(content);
    JSONArray children = jsonObject.getJSONObject("main").getJSONArray("children");
    int counter = 1;
    for (int i = 0; i < children.length(); i++) {
      try {
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

          By mainMenu = By.xpath("(//nav[@class='usa-nav']//button/span)[" + (i + 1) + "]");
          clickElement(mainMenu);
          for (JSONObject subMenuItem : subMenuItems) {
            try {
              String expectedTitle = subMenuItem.getString("title");

              String subMenuTitle = getElement(By.xpath(String.format(subMenu, expectedTitle))).getText();
              Assert.assertEquals(expectedTitle.trim(), subMenuTitle, "Submenu title not matched");
            } catch (Exception ex) {
              ReportUtil.FAIL(driver, "");
            }
            counter++;
          }
        }
      }catch (Exception e){
        ReportUtil.FAIL(driver, e.getMessage());
      }
    }
  }






  public JSONObject captureResponseFromDevTools(String endpoint) throws InterruptedException {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(header)));
    AtomicReference<RequestId> targetRequestId = new AtomicReference<>();
    AtomicReference<JSONObject> capturedResponse = new AtomicReference<>();
    DevTools devTools = ((ChromeDriver) driver).getDevTools();
    devTools.createSession();
    devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
    devTools.send(Network.setCacheDisabled(true));

    devTools.addListener(Network.responseReceived(), responseReceived -> {
      Response response = responseReceived.getResponse();
      String url = response.getUrl();

      if (url.contains(endpoint) && url.contains("/api")) {
        System.out.println("Target API URL matched: " + url);
        targetRequestId.set(responseReceived.getRequestId());
      }
    });

    // Let the page or interaction trigger the network call
    Thread.sleep(15000);
    driver.navigate().refresh(); // or trigger UI event
    Thread.sleep(15000); // wait for response to arrive

    // Try to get the body using stored request ID
    if (targetRequestId.get() != null) {
      try {
        Network.GetResponseBodyResponse bodyResponse = devTools.send(Network.getResponseBody(targetRequestId.get()));
        String body = bodyResponse.getBody();
        if (body != null && !body.isEmpty()) {
          capturedResponse.set(new JSONObject(body));
        }
      } catch (Exception e) {
        System.out.println("Error fetching response body: " + e.getMessage());
      }
    } else {
      System.out.println("Request ID was not captured.");
    }

    return capturedResponse.get();
  }



  public void validateEachSubMenu() throws IOException, InterruptedException {

    String content = new String(Files.readAllBytes(Paths.get("menu.json")));
    JSONObject jsonObject = new JSONObject(content);
    JSONArray children = jsonObject.getJSONObject("main").getJSONArray("children");
    for (int i = 1; i < children.length(); i++) {
      try {
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
            try {
              JSONObject subMenuItem = subMenuItems.get(j);
              String endpoint = subMenuItem.getString("link");

              By mainMenu = By.xpath("(//nav[@class='usa-nav']//button/span)[" + (i + 1) + "]");
              scrollToElement(mainMenu);
              Thread.sleep(2000);
              clickElement(mainMenu);

              By subMenu = By.xpath("//span[contains(text(),'" + subMenuItem.getString("title") + "')]/parent::a");
              WebElement subMenuElement = driver.findElement(subMenu);
              String href = subMenuElement.getAttribute("href");

              if (href != null && href.contains("javascript:void(0)")) {
                System.out.println("Skipping submenu with invalid link: " + href);
                continue;
              }

              clickElement(subMenu);

              JSONObject object = captureResponseFromDevTools(endpoint);
              if (object == null) {
                throw new NullPointerException("API response object is null for endpoint: " + endpoint);
              }

              String template = JSONUtil.getAttributeValue(object, "template");
              if (template.equals("portfolio")) {
               // portfolioTemplate(object);
              }

              comparePageContentWithAPIResponse(object);

            } catch (Exception e) {
              ReportUtil.FAIL(driver, "Failed validating submenu: " + e.getMessage());
              System.out.println("Submenu failed: " + e.getMessage());
              // Continue to next submenu
            }
          }
        }
      } catch (Exception e) {
        ReportUtil.FAIL(driver, "Failed processing parent menu: " + e.getMessage());
        System.out.println("Parent menu failed: " + e.getMessage());
        // Continue to next parent
      }
    }
  }







//
//
//  public void validateForecast() throws IOException, InterruptedException {
//    String content = new String(Files.readAllBytes(Paths.get("menu.json")));
//    JSONObject jsonObject = new JSONObject(content);
//    JSONArray children = jsonObject.getJSONObject("main").getJSONArray("children");
//    By mainMenu = By.xpath("(//nav[@class='usa-nav']//button/span)[2]");
//    clickElement(mainMenu);
//    By subMenu = By.xpath("(//ul[@class='usa-nav__submenu']//span)[11]");
//    clickElement(subMenu);
//    JSONObject object = captureResponseFromDevTools("/forecast");
//    comparePageContent(object);
//
//
//  }





}