package ltd.newbee.mall.controller.mall;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.*;

public class PersonalTest_Automated {
  private WebDriver driver;
  private Map<String, Object> vars;
  JavascriptExecutor js;
  Actions actions;
  WebDriverWait wait;

  @Before
  public void setUp() {
    System.setProperty("webdriver.gecko.driver", "D:\\selenium\\geckodriver.exe");
    driver = new FirefoxDriver();
    js = (JavascriptExecutor) driver;
    actions = new Actions(driver);
    vars = new HashMap<String, Object>();
    wait = new WebDriverWait(driver, Duration.ofSeconds(5)); // 设置显式等待时间为5秒
  }

  @After
  public void tearDown() {
    driver.quit();
  }

  @Test
  public void PersonalTest_Automated() {
    driver.get("http://localhost:28079/login");
    driver.manage().window().setSize(new Dimension(2062, 1118));
    driver.findElement(By.id("loginName")).click();
    driver.findElement(By.id("loginName")).sendKeys("13616745302");
    driver.findElement(By.id("password")).click();
    driver.findElement(By.id("password")).sendKeys("tsh123456");
    String captchaCode = getCaptchaCode("mallVerifyCode");
    driver.findElement(By.id("verifyCode")).click();
    driver.findElement(By.id("verifyCode")).sendKeys(captchaCode);
    driver.findElement(By.cssSelector(".submit")).click();

    WebElement userDiv = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("user")));
    actions.moveToElement(userDiv).perform();

    WebElement personalCenter = wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("个人中心")));
    personalCenter.click();

    WebElement changeInfo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("更改个人信息")));
    changeInfo.click();

    WebElement introduceSign = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("introduceSign")));
    introduceSign.click();
    introduceSign.sendKeys("1234567878");

    WebElement address = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("address")));
    address.click();
    address.sendKeys("123456787878");

    WebElement saveButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("saveButton")));
    saveButton.click();

    WebElement logout = wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("退出登录")));
    logout.click();
  }

  private String getCaptchaCode(String attributeName) {
    try {
      String script = "var xhr = new XMLHttpRequest();" +
              "xhr.open('GET', 'http://localhost:28079/getSessionAttribute?attributeName=' + arguments[0], false);" +
              "xhr.send(null);" +
              "return xhr.responseText;";
      return (String) js.executeScript(script, attributeName);
    } catch (Exception e) {
      e.printStackTrace();
      return "error";
    }
  }
}
