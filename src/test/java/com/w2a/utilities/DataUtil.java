package com.w2a.utilities;

import java.util.Hashtable;

import javax.xml.crypto.Data;

import org.testng.SkipException;
import org.testng.annotations.DataProvider;

public class DataUtil {
	
	public static void checkExecution(String testSuiteName, String testCaseName,
			String dataRunMode, ExcelReader excel){
		if(!isSuiteRunnable(testSuiteName)){
			throw new SkipException("Skipping the test: "+testCaseName+" as the Runmode of Test Suite: "
					+testSuiteName+" is NO");
		}
		if(!isTestRunnable(testCaseName,excel)){
			throw new SkipException("Skipping the test: "+testCaseName+" as the Runmode of Test Case: "
					+testCaseName+" is NO");
		}
		if(dataRunMode.equalsIgnoreCase(Constants.RunMode_NO)){
			throw new SkipException("Skipping the test: "+testCaseName+" as the Runmode of Data Set is NO");
		}
		
	}

	public static boolean isSuiteRunnable(String suiteName) {

		ExcelReader excel = new ExcelReader(Constants.Suite_XL_Path);
		int rows = excel.getRowCount(Constants.Suite_Sheet);

		for (int rowNum = 2; rowNum <= rows; rowNum++) {

			String data = excel.getCellData(Constants.Suite_Sheet, Constants.SuiteName_Col, rowNum);

			if (data.equals(suiteName)) {

				String runMode = excel.getCellData(Constants.Suite_Sheet, Constants.RunMode_Col, rowNum);

				if (runMode.equals(Constants.RunMode_YES))
					return true;
				else
					return false;
			}
		}
		return false;
	}

	public static boolean isTestRunnable(String testCaseName, ExcelReader excel) {
		
		int rows = excel.getRowCount(Constants.TestCase_Sheet);

		for (int rowNum = 2; rowNum <= rows; rowNum++) {

			String data = excel.getCellData(Constants.TestCase_Sheet, Constants.TestCase_Col, rowNum);

			if (data.equals(testCaseName)) {

				String runMode = excel.getCellData(Constants.TestCase_Sheet, Constants.RunMode_Col, rowNum);

				if (runMode.equals(Constants.RunMode_YES))
					return true;
				else
					return false;
			}
		}
		return false;

	}
	
	@DataProvider
	public static Object[][] getData(String testCase, ExcelReader excel) {

//		ExcelReader excel = new ExcelReader(
//				System.getProperty("user.dir") + "/src/test/resources/testdata/BankManagerSuite.xlsx");
		//Also we will be passing multiple excel files, so instead of passing hard coded excel sheet here
		//We can pass the reference of excel sheet reader class in the data provider method so that the object can be created on the run time
		int rows = excel.getRowCount(Constants.Data_Sheet);
		System.out.println("Total rows are:: " + rows);

		//String testName = "OpenAccountTest";
		//String testName = "AddCustomerTest";  //Test case name in Test Data sheet of BankManager Suite
		//Also we should get the sheet name on the runtime, so passing as parameter in our data provider method
		String testName = testCase;

		// Find from which row the test case starts
		int testCaseRowNum = 1;

		for (testCaseRowNum = 1; testCaseRowNum <= rows; testCaseRowNum++) {

			String testCaseName = excel.getCellData(Constants.Data_Sheet, 0, testCaseRowNum);

			if (testCaseName.equalsIgnoreCase(testName))
				break;
		}
		System.out.println("Test Case starts from Row number:: " + testCaseRowNum);

		// Find actual data rows in each test case(Checking total rows in test
		// case)
		int dataStartRowNum = testCaseRowNum + 2;
		int testRows = 0;
		while (!excel.getCellData(Constants.Data_Sheet, 0, dataStartRowNum + testRows).equals("")) {
			testRows++;
		}
		System.out.println("Total rows of data are:: " + testRows);

		// Find total number of columns inside the particular test case
		int testCols = 0;
		int colStartcolNum = testCaseRowNum + 1; // columns name start in next
													// row after the test case
													// name
		while (!excel.getCellData(Constants.Data_Sheet, testCols, colStartcolNum).equals("")) {
			testCols++;
		}
		System.out.println("Total columns are:: " + testCols);
		
		Object[][] data = new Object[testRows][1]; 
		
		int i=0;
		for (int rNum = dataStartRowNum; rNum < (dataStartRowNum + testRows); rNum++) {

			Hashtable<String, String> table = new Hashtable<String, String>();
			for (int cNum = 0; cNum < testCols; cNum++) {
				// data[rNum - dataStartRowNum][cNum] = excel.getCellData(Constants.Data_Sheet, cNum, rNum);
				// Instead of putting the data in to 2-D array, store the data in a variable
				String testData = excel.getCellData(Constants.Data_Sheet, cNum, rNum);
				// To get the column name
				// columns name start in next row after the test case name
				String colName = excel.getCellData(Constants.Data_Sheet, cNum, colStartcolNum);
				//Put the data into table. Key- ColumnName and Data would be testData
				
				table.put(colName, testData);
			}
			//For every row "i", table should be created
			data[i][0] = table;
			i++;  //Increment for each row
		}

		return data;
	}

}
