package com.w2a.suite.bankmanager.testcases;

import java.util.Hashtable;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.LogStatus;
import com.w2a.datadriven.base.TestBase;
import com.w2a.utilities.Constants;
import com.w2a.utilities.DataProviders;
import com.w2a.utilities.DataUtil;
import com.w2a.utilities.ExcelReader;

public class OpenAccountTest extends TestBase {
	
	@Test(dataProviderClass=DataProviders.class,dataProvider="BankManagerDP")
	public void openAccountTest(Hashtable<String,String> data){
		super.setUp();
		test = rep.startTest("OpenAccountTest"+" "+data.get("browser"));
		setExtentTest(test); //So that this test should follow a unique thread for generating extent report
		ExcelReader excel = new ExcelReader(Constants.Suite1_XL_Path);
		DataUtil.checkExecution("BankManagerSuite", "OpenAccountTest", data.get("Runmode"), excel);
		openBrowser(data.get("browser")); //Get the browser from the excel sheet
		navigate("testsiteurl");
		click("bmlBtn_CSS");
		click("openaccount_CSS");
		select("customer_CSS",data.get("customer"));
		select("currency_CSS",data.get("currency"));
		click("process_CSS");
		reportPass("Open Account Test Pass");
	}
	
	@AfterMethod
	public void tearDown(){
		if(rep!=null){
			//rep.endTest(test);
			rep.endTest(getExtentTest());
			rep.flush();
		}
		getDriver().quit();
	}

}
