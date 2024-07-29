package com.quinstreet.test;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import ExcelResults.ExcelReportWriter;

public class BristolWestRegressionTest extends BaseTest {

	@BeforeMethod
	public void setUp() throws IOException {
		super.setUp();
		userName = "marsh@email.com";
		password = "R/&\\GD8)GAF/`;]<H90;ML_aMO3ZKN@Q\\J[`'`FB";
		replaceSid("src/test/resources/Regression_Payloads", "1099107729");
		replaceSid("src/test/resources/TestScenario_Payloads", "1099107603");

	}

	// @Test(dataProvider = "multipleStatesRegression")
	@Test(dataProvider = "singleStateTestScenario")
	public void endToEndTesting(Input state) throws Exception {
		String todayDate = getCurrentDateForRC2Request();
		String sessionId = postRC1Request(state,"Bristol West");
		
		reportAttributes.put(ExcelReportWriter.SESSION_ID, sessionId);
		reportWriter.write(reportAttributes, "Regression");

	}

	protected String getCurrentDateForRC2Request() {
		DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		return formatter.format(new java.util.Date());
	}

}


