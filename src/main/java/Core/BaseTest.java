package Core;

import Common.Config;
import Common.ReportUtil;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.time.Duration;

public class BaseTest {

    protected WebDriver driver = null;
    private ReportUtil report = null;

    @BeforeSuite
    public void beforeSuite() {
        new Config().loadEnvProperties(System.getProperty("env"));
        report = new ReportUtil();
        report.initializeReport(System.getProperty("env"), "Regression");
    }

    @BeforeMethod
    public void beforeTestMethos(Method method) {
        Test test = method.getAnnotation(Test.class);
        String testName = test.testName();
        //ReportUtil.addTestToReport(testName);
        launchApplication();
    }

    @AfterMethod
    public void afterTestMethos(ITestResult result) throws IOException {
        if (result.getStatus() == ITestResult.SUCCESS) {
            ReportUtil.PASS("Test is Passed");
            System.out.println(result.getMethod().getMethodName() + ": Test is PASSED");
        } else if (result.getStatus() == ITestResult.FAILURE) {
            ReportUtil.FAIL(result.getThrowable().getMessage());
            System.out.println(result.getMethod().getMethodName() + ": Test is FAILED");
            ReportUtil.FAIL(driver, "Test is failed");
        } else if (result.getStatus() == ITestResult.SKIP) { // Test passed with out any interruption
            ReportUtil.FAIL(result.getThrowable().getMessage());
            System.out.println(result.getMethod().getMethodName() + ": Test is SKIPPED");
            ReportUtil.FAIL(driver, result.getThrowable().getLocalizedMessage());
            ReportUtil.FAIL("Test is Skipped");
        }
        driver.close();
    }

    @AfterSuite
    public void afterSuite() {
        ReportUtil.flushReport();
    }

    public void launchApplication() {
        //launch application
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
        driver.manage().window().maximize();
        driver.get(Config.getEnvData("appurl"));
        //ReportUtil.PASS(driver, Config.getEnvData("appurl") + " applicaiton is launched");

    }
}
