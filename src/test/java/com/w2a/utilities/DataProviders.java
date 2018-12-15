package com.w2a.utilities;

import java.lang.reflect.Method;

import org.testng.annotations.DataProvider;

public class DataProviders {
	
	//Handling multiple data providers and suites in one single class
	
	@DataProvider(name="BankManagerDP",parallel=true) //To execute tests parallely on different nodes
	public static Object[][] getDataSuite1(Method m) {
		System.out.println("Method Name is: "+m.getName());
		ExcelReader excel = new ExcelReader(Constants.Suite1_XL_Path);
		String testcase = m.getName();  
//		return DataUtil.getData("AddCustomerTest", new ExcelReader(
//			System.getProperty("user.dir") + "/src/test/resources/testdata/BankManagerSuite.xlsx"));
		return DataUtil.getData(testcase, excel);
		}
	
	@DataProvider(name="CustomerDP")
	public static Object[][] getDataSuite2(Method m) {
		System.out.println("Method Name is: "+m.getName());
		ExcelReader excel = new ExcelReader(Constants.Suite2_XL_Path);
		String testcase = m.getName();  
//		return DataUtil.getData("AddCustomerTest", new ExcelReader(
//			System.getProperty("user.dir") + "/src/test/resources/testdata/CustomerSuite.xlsx"));
		return DataUtil.getData(testcase, excel);
		}

}
