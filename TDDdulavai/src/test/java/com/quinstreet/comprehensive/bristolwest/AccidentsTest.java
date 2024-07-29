package com.quinstreet.comprehensive.bristolwest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.quinstreet.test.BaseTest;
import com.quinstreet.test.Input;

import ExcelResults.ExcelReportWriter;
import Utility.Utilities;
import junit.framework.Assert;

public class AccidentsTest extends BaseTest{
	public static final String TRANSPORT_ID="BristolWest";
	public static final String CARRIER_NAME="Bristol West";

	
	@BeforeMethod
	public void setUp() throws IOException {
		super.setUp();
		userName = "marsh@email.com";
		password = "YQ2#(P28X\\;+]`@]S]K%%Q?QVH(//]TdRFb\"P=3Y";
		replaceSid("src/test/resources/Comprehensive_Payloads", "1099107603");

	}
	
	@DataProvider(name = "accidentsData")
	public Object[][] accidentsData() {
		return createDataProvider();
	}

	private Object[][] createDataProvider() {
		List<List<Object>> output = new ArrayList<>();
		List<String> states = Arrays.asList("UT");
		for (int i = 0; i < states.size(); i++) {
			String state = states.get(i);
			Input input = new Input(state,
					String.format("src/test/resources/Comprehensive_Payloads/%s_RC1.xml", state));
			Properties prop = read("src/test/resources/Carriers/BristolWest/Accidents/"+state+"/Accidents2.properties");
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
	

	@Test(dataProvider = "accidentsData")
	public void endToEndTesting(Input state,String key, String expected) throws Exception {
		String xmlPath="ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersPolicy/AccidentViolation/AccidentViolationCd";

			Utilities.updateNode(state.getFilePath(),key,xmlPath);
			String sessionId = postRC1Request(state,CARRIER_NAME);
			
			String requestXml = getCarrierRequestXmlFromDatabase(sessionId,TRANSPORT_ID);
			String actual = Utilities.getNodeValue(requestXml, xmlPath);
			

			reportAttributes.put(ExcelReportWriter.SESSION_ID, sessionId);
			reportAttributes.put(ExcelReportWriter.ACORD_VALUE, key);
			reportAttributes.put(ExcelReportWriter.EXPECTED_VALUE, expected);
			reportAttributes.put(ExcelReportWriter.ACTUAL_VALUE, actual);
			reportWriter.write(reportAttributes, "Accidents");
			Assert.assertEquals(expected, actual);
			
		}
	
}


