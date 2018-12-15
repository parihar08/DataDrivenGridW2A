package com.w2a.utilities;

import org.testng.annotations.Test;
import java.util.Hashtable;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestParameterization {
	
	//Create the test case for Add Customer Test
//	@Test(dataProvider = "getData")
//	public void testData(String runmode, String firstname, String lastname, String postcode){
//		//Generally we don't hard code the column names here and implement in the form of hash tables
//		System.out.println(runmode+"---"+firstname+"---"+lastname+"---"+postcode);
//		
//	}
	
	//Create the test case for Open Account Test
//		@Test(dataProvider = "getData")
//		public void testData1(String runmode, String customer, String currency){
//			System.out.println(runmode+"---"+customer+"---"+currency);
//			
//		}
	
	//Now to implement the Hash tables
	//Another change is where we are defining the object of 2-D array at the bottom of this class
	@Test(dataProvider = "getData")
	public void testData(Hashtable<String,String> data){
		System.out.println(data.get("Runmode")+"---"+data.get("firstname")+"---"+data.get("lastname")+"---"+data.get("postcode"));
		//System.out.println(data.get("Runmode")+"---"+data.get("customer")+"---"+data.get("currency"));
	}

	@DataProvider
	public Object[][] getData() {

		ExcelReader excel = new ExcelReader(
				System.getProperty("user.dir") + "/src/test/resources/testdata/BankManagerSuite.xlsx");
		int rows = excel.getRowCount(Constants.Data_Sheet);
		System.out.println("Total rows are:: " + rows);

		//String testName = "OpenAccountTest";
		String testName = "AddCustomerTest";  //Test case name in Test Data sheet of BankManager Suite

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
			// checking from 0th column and till the time a row is not blank ,
			// increment the rows and get data
			// data start row should be testcase name row num + 2
			// We have added a blank after every test case in the excel sheet so
			// it identifies the end of test data for a test case
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

		// Get entire data based on a particular test case using 2D Object

//		Object[][] data = new Object[testRows][testCols]; // since our method
//															// accepts 2D array,
//															// pass total number
//															// of rows and columns
//		
//		for (int rNum = dataStartRowNum; rNum < (dataStartRowNum + testRows); rNum++) {
//			for (int cNum = 0; cNum < testCols; cNum++) {
//				// data[0][0] = excel.getCellData(Constants.Data_Sheet, cNum,rNum); //store the data
//				data[rNum - dataStartRowNum][cNum] = excel.getCellData(Constants.Data_Sheet, cNum, rNum); 
//				//iterate over the data and store the data																						
//			}
//		}
//		
//		return data;
		
		//For Hash Table, in object array rows will stay as it is and the column will become one
		//because we have just given one argument in our test
		//Also for each row we need to create a hash table,
		//where the column name(eg. runmode/firstname/lastname/postcode) will be the key
		//and actual data(eg. Y/Sandeep/Parihar/T3J4Y8) will be the value
		
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
