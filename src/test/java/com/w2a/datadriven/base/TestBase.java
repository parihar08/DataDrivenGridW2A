package com.w2a.datadriven.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.w2a.utilities.ExtentManager;


public class TestBase {
	
	/*
	 * WebDriver
	 * Logs
	 * Properties
	 * Excel
	 * DB
	 * Mail
	 * Extent Reports
	 * Support for Grid Model using Thread Local class that will help executing our test on unique nodes(Parallel Threading concept)
	 * 
	 */
	public static ThreadLocal<RemoteWebDriver> dr = new ThreadLocal<RemoteWebDriver>();
	public RemoteWebDriver driver = null;
	public Properties OR = new Properties();
	public Properties Config = new Properties();
	public FileInputStream fis;
	public Logger log = Logger.getLogger("devpinoyLogger");
	public WebDriverWait wait;
	public ExtentReports rep = ExtentManager.getInstance();
	public ExtentTest test;
	public static ThreadLocal<ExtentTest> exTest = new ThreadLocal<ExtentTest>();
	public String browser;
	
	public static String screenshotPath;
	public static String screenshotName;
	
	//For creating application and selenium logs in the specified format
	public void addLog(String message){
		//log.debug("Browser: "+browser+" : "+" : "+message);
		log.debug("Browser: "+browser+" : "+"Thread: "+getThreadValue(dr.get())+" : "+message);
	}

	public void captureScreenshot() {

		File scrFile = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.FILE);

		Date d = new Date();
		screenshotName = d.toString().replace(":", "_").replace(" ", "_") + ".jpg";
		String fileName = System.getProperty("user.dir") + "/reports/" + screenshotName;
		//String fileName = System.getProperty("user.dir") + "/target/surefire-reports/html/" + screenshotName;

		try {
			FileUtils.copyFile(scrFile,
					new File(fileName));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Attach screenshot to extent report
		getExtentTest().log(LogStatus.INFO, "Screenshot<-> "+test.addScreenCapture(fileName));

	}
	
	//@BeforeSuite  --Since Before suite is executed only once. Once the suite is started before suite is called either by Thread1 or Thread2. So need to remove this as this causes issues with multiple Threads. Call setup method before the start of a test
	public void setUp(){
		if(driver==null){
			try {
				fis = new FileInputStream(System.getProperty("user.dir")+"/src/test/resources/properties/Config.properties");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Config.load(fis);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				fis = new FileInputStream(System.getProperty("user.dir")+"/src/test/resources/properties/OR.properties");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				OR.load(fis);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	//Defining getter and setter methods of Thread Local class for WebDriver
	public WebDriver getDriver(){
		return dr.get();  //returns the driver reference
	}
	
	public void setWebDriver(RemoteWebDriver driver){
		dr.set(driver);
	}
	
	// Defining getter and setter methods of Thread Local class for Extent Report
	public ExtentTest getExtentTest() {
		return exTest.get(); // returns the driver reference
	}

	public void setExtentTest(ExtentTest et) {
		exTest.set(et);
	}
	
	public String getThreadValue(Object value){  //Defining a global type Object so that it takes whatever type is passed into it
		String threadName = value.toString();
		String[] threadName1 = threadName.split(" "); //Since split function returns an array
		String threadName2 = threadName1[threadName1.length-1].replace("(", "").replace(")", ""); //This returns the value at last index of the array without ( and )
		return threadName2;
	}
	
	public void openBrowser(String browser){
		
		//value of browser we pass in this method should be set for global browser variable to be used for logging purpose
		this.browser = browser;
		
		DesiredCapabilities cap = null;
		if(browser.equalsIgnoreCase("firefox")){
			cap = DesiredCapabilities.firefox();
			cap.setBrowserName("firefox");
			cap.setPlatform(Platform.ANY);
		}
		else if(browser.equalsIgnoreCase("chrome")){
			cap = DesiredCapabilities.chrome();
			cap.setBrowserName("chrome");
			cap.setPlatform(Platform.ANY);
		}
		try {
			driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), cap); //Docker Hub Url
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setWebDriver(driver);  //This will set the driver instance with the Remote WebDriver of Thread Local Class
		getDriver().manage().timeouts().implicitlyWait(Integer.parseInt(Config.getProperty("implicit.wait")), TimeUnit.SECONDS);
		//getDriver().manage().window().fullscreen();
		//test.log(LogStatus.INFO, browser+" browser opened succesfully");
		getExtentTest().log(LogStatus.INFO, browser+" browser opened succesfully"); //so that it runs on unique thread all the time
		
		//To get current thread value
		//System.out.println("Thread value is: "+dr.get());
		System.out.println("Thread value is: "+getThreadValue(dr.get()));
				
	}
	
	public void reportPass(String msg){
		//test.log(LogStatus.PASS, msg);
		getExtentTest().log(LogStatus.PASS, msg);
	}
	
	public void reportFailure(String msg){
		//test.log(LogStatus.FAIL, msg);
		getExtentTest().log(LogStatus.FAIL, msg);
		//takes screenshot
		captureScreenshot();
		Assert.fail(msg);
		
	}
	
	public void navigate(String url){
		getDriver().get(Config.getProperty(url));
		//test.log(LogStatus.INFO, "Navigating to: "+Config.getProperty(url));
		getExtentTest().log(LogStatus.INFO, "Navigating to: "+Config.getProperty(url));
	}
	
	public void click(String locator) {
		
		try{
		if (locator.endsWith("_CSS")) {
			getDriver().findElement(By.cssSelector(OR.getProperty(locator))).click();
		} else if (locator.endsWith("_XPATH")) {
			getDriver().findElement(By.xpath(OR.getProperty(locator))).click();
		} else if (locator.endsWith("_ID")) {
			getDriver().findElement(By.id(OR.getProperty(locator))).click();
		}
		addLog("Clicking on the element: "+locator);
		}catch(Throwable t){
			reportFailure("Failing while clicking on the element "+locator);
		}
		//test.log(LogStatus.INFO, "Clicking on : " + locator);
	}

	public void type(String locator, String value) {
		
		try{
		if (locator.endsWith("_CSS")) {
			getDriver().findElement(By.cssSelector(OR.getProperty(locator))).sendKeys(value);
		} else if (locator.endsWith("_XPATH")) {
			getDriver().findElement(By.xpath(OR.getProperty(locator))).sendKeys(value);
		} else if (locator.endsWith("_ID")) {
			getDriver().findElement(By.id(OR.getProperty(locator))).sendKeys(value);
		}
		addLog("Typing on the element: "+locator);
		}catch(Throwable t){
			reportFailure("Failing while typing in the element "+locator);
		}

		//test.log(LogStatus.INFO, "Typing in : " + locator + " entered value as " + value);

	}
	
	static WebElement dropdown;

	public void select(String locator, String value) {
		
		try{
		if (locator.endsWith("_CSS")) {
			dropdown = getDriver().findElement(By.cssSelector(OR.getProperty(locator)));
		} else if (locator.endsWith("_XPATH")) {
			dropdown = getDriver().findElement(By.xpath(OR.getProperty(locator)));
		} else if (locator.endsWith("_ID")) {
			dropdown = getDriver().findElement(By.id(OR.getProperty(locator)));
		}
		
		Select select = new Select(dropdown);
		select.selectByVisibleText(value);
		addLog("Selecting the element: "+locator);
		}
		catch(Throwable t){
			reportFailure("Failing while selecting on the element "+locator);
		}
		//test.log(LogStatus.INFO, "Selecting from dropdown : " + locator + " value as " + value);

	}

	public boolean isElementPresent(By by) {

		try {

			getDriver().findElement(by);
			return true;

		} catch (NoSuchElementException e) {

			return false;

		}

	}

//	public static void verifyEquals(String expected, String actual) throws IOException {
//
//		try {
//
//			Assert.assertEquals(actual, expected);
//
//		} catch (Throwable t) {
//
//			TestUtil.captureScreenshot();
//			// ReportNG
//			Reporter.log("<br>" + "Verification failure : " + t.getMessage() + "<br>");
//			Reporter.log("<a target=\"_blank\" href=" + TestUtil.screenshotName + "><img src=" + TestUtil.screenshotName
//					+ " height=200 width=200></img></a>");
//			Reporter.log("<br>");
//			Reporter.log("<br>");
//			// Extent Reports
//			test.log(LogStatus.FAIL, " Verification failed with exception : " + t.getMessage());
//			test.log(LogStatus.FAIL, test.addScreenCapture(TestUtil.screenshotName));
//
//		}
//
//	}

	
	
}
