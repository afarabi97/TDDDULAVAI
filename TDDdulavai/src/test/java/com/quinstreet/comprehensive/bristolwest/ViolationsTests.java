package com.quinstreet.comprehensive.dairyland;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.quinstreet.test.BaseTest;
import com.quinstreet.test.Input;

import ExcelResults.ExcelReportWriter;
import Utility.Utilities;
import junit.framework.Assert;

public class ViolationsTests extends BaseTest {
	public static final String TRANSPORT_ID="DairylandInsurance";
	public static final String CARRIER_NAME ="Dairyland Insurance";

	@BeforeMethod
	public void setUp() throws IOException {
		super.setUp();
		userName = "marsh@email.com";
		password = "R/&\\GD8)GAF/`;]<H90;ML_aMO3ZKN@Q\\J[`'`FB";
		replaceSid("src/test/resources/Comprehensive_Payloads", "1099107546");
		replaceDate("src/test/resources/Comprehensive_Payloads");

	}
	

	@DataProvider(name = "violationsData")
	public Object[][] violationsData() {
		return createDataProvider();
	}

	private Object[][] createDataProvider() {
		List<List<Object>> output = new ArrayList<>();
		List<String> states = Arrays.asList("FL");
		for (int i = 0; i < states.size(); i++) {
			String state = states.get(i);
			Input input = new Input(state,
					String.format("src/test/resources/Comprehensive_Payloads/%s_RC1.xml", state));
			Properties prop = read(
					"src/test/resources/Carriers/Dairyland/Violations/"+state+".properties");
			Enumeration enuKeys = prop.keys();
			while (enuKeys.hasMoreElements()) {
				String key = (String) enuKeys.nextElement();
				String value = prop.getProperty(key);
				output.add(Arrays.asList(input, key, value));
			}
		}
		return output.stream()
				.map(argumentList -> new Object[] { argumentList.get(0), argumentList.get(1), argumentList.get(2) })
				.toArray(Object[][]::new);
	}

	@Test(dataProvider = "violationsData")
	public void endToEndTesting(Input state,String acordValue,String expected) throws Exception {
		String xmlPath="/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersPolicy/AccidentViolation/AccidentViolationCd";
		String xmlPath2="/root/Drivers[1]/Incidents/Code";
		
		Utilities.updateNode(state.getFilePath(),acordValue,xmlPath);
		String sessionId = postRC1Request(state, CARRIER_NAME);
		
		String requestXml = getCarrierRequestXmlFromDatabase(sessionId,TRANSPORT_ID);
		
		String actual = Utilities.getNodeValue(requestXml, xmlPath2);
		
		reportAttributes.put(ExcelReportWriter.SESSION_ID, sessionId);
		reportAttributes.put(ExcelReportWriter.EXPECTED_VALUE, expected);
		reportAttributes.put(ExcelReportWriter.ACTUAL_VALUE, actual);
		reportWriter.write(reportAttributes, "Violations");
		Assert.assertEquals(expected, actual);
		}	
}


