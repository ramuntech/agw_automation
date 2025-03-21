package Pages;

import Core.BasePage;
import org.apache.poi.ss.usermodel.Row;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;

public class CommonPage extends BasePage {

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
            String expectedTitle = topLevelItems.get(i).getString("title");
            By locator = By.xpath("(//nav[@class='usa-nav']//button/span)[" + (i + 1) + "]");
            String actualTitle =  getElement(locator).getText();
            Assert.assertEquals(actualTitle, expectedTitle, "Order mismatch at index " + (i + 1));
        }
    }

    public void subMenuValidation() throws IOException, InterruptedException {
        Thread.sleep(5000);
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

                By mainMenu= By.xpath("(//nav[@class='usa-nav']//button/span)[" + (i + 1) + "]");
                clickElement(mainMenu);
                for (JSONObject subMenuItem : subMenuItems) {
                    String expectedTitle = subMenuItem.getString("title");
                    By subMenu = By.xpath("(//ul[@class='usa-nav__submenu']//span)[" + counter + "]");
                    String subMenuTitle =  getElement(subMenu).getText().trim();
                    Assert.assertEquals(expectedTitle.trim(), subMenuTitle, "Submenu title not matched");
                    counter++;
                }
            }
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
