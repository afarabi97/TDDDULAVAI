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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.quinstreet.test.BaseTest;
import com.quinstreet.test.Input;

import ExcelResults.ExcelReportWriter;
import Utility.Utilities;
import junit.framework.Assert;

public class PriorInsuranceTests extends BaseTest {

	public static final String TRANSPORT_ID="DairylandInsurance";
	private static final String CARRIER_NAME = "Dairyland Insurance";
	
	@BeforeMethod
	public void setUp() throws IOException {
		super.setUp();
		userName = "qnst_uapi@qrp.com";
		password = "Test@1234";
		replaceSid("src/test/resources/Comprehensive_Payloads", "1099107546");

	}
	@DataProvider(name = "priorCarriersData")
	public Object[][] priorCarriersData() {
		String priorCarriers ="Prior_Carriers";
		return createDataProvider(priorCarriers);
	}

	private Object[][] createDataProvider(String filePath) {
		List<List<Object>> output = new ArrayList<>();
		List<String> states = Arrays.asList("FL");
		for (int i = 0; i < states.size(); i++) {
			String state = states.get(i);
			Input input = new Input(state,
					String.format("src/test/resources/Comprehensive_Payloads/%s_RC1.xml", state));
			Properties prop = read(
					"src/test/resources/Carriers/Dairyland/Prior_Insurance/" + state+ "_"+filePath+".properties");
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
	
	@Test(dataProvider = "priorCarriersData")
	public void priorCarriersRC1Testing(Input state, String key, String expected) throws Exception {
		String xmlPath = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersPolicy/OtherOrPriorPolicy[PolicyCd='Prior' and LOBCd='AUTOP']/NAICCd";
		String xmlPath1 = "/root/PolicyInfo/PriorInsurerName";
		Utilities.updateNode(state.getFilePath(), key, xmlPath);
		String sessionId = postRC1Request(state, CARRIER_NAME);
		String requestXml = getCarrierRequestXmlFromDatabase(sessionId,TRANSPORT_ID);
		String Insurer_Name = Utilities.getNodeValue(requestXml, xmlPath1);
		reportAttributes.put(ExcelReportWriter.SESSION_ID, sessionId);
		reportAttributes.put(ExcelReportWriter.EXPECTED_VALUE, expected);
		reportAttributes.put(ExcelReportWriter.ACTUAL_VALUE, Insurer_Name);
		reportAttributes.put(ExcelReportWriter.INSURER_NAME, Insurer_Name);
		reportWriter.write(reportAttributes, "Prior_Carriers");
		Assert.assertEquals(expected, Insurer_Name);
		
	}

}


