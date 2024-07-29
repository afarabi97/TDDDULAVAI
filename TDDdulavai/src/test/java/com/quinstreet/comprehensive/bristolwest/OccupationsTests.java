package com.quinstreet.comprehensive.bristolwest;

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

public class OccupationTests extends BaseTest{
	public static final String TRANSPORT_ID="BristolWest";
	public static final String CARRIER_NAME="Bristol West";

	
	@BeforeMethod
	public void setUp() throws IOException {
		super.setUp();
		userName = "marsh@email.com";
		password = "R/&\\GD8)GAF/`;]<H90;ML_aMO3ZKN@Q\\J[`'`FB";
		replaceSid("src/test/resources/Comprehensive_Payloads", "1099107603");

	}

	
	@DataProvider(name = "occupationsData")
	public Object[][] occupationsData() {
		String occupations="Occupations";
		return createDataProvider(occupations);
	}

	private Object[][] createDataProvider(String filePath) {
		List<List<Object>> output = new ArrayList<>();
		List<String> states = Arrays.asList("GA");
		for (int i = 0; i < states.size(); i++) {
			String state = states.get(i);
			Input input = new Input(state,
					String.format("src/test/resources/Comprehensive_Payloads/%s_RC1.xml", state));
			Properties prop = read("src/test/resources/Carriers/BristolWest/Occupations/"+filePath+".properties");
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
	

	@Test(dataProvider = "occupationsData")
	public void endToEndTesting(Input state,String acordValue, String expected) throws Exception {
		
		String expectedIndustryValue=expected.split("  ")[0];
		String expectedOccupationValue=expected.split("  ")[1];

		    String xmlPath="/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersDriver[@id=\"D100\"]/DriverInfo/PersonInfo/OccupationClassCd";
		
		    String carrierXmlPathIndustry="/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersDriver[@id=\"Drv1\"]/DriverInfo/PersonInfo/com.safeco_IndustryCd";
		    String carrierXmlPathOccupation="/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersDriver[@id=\"Drv1\"]/DriverInfo/PersonInfo/com.safeco_OccupationCd";

		    
			Utilities.updateNode(state.getFilePath(),acordValue,xmlPath);
			String sessionId = postRC1Request(state,CARRIER_NAME);
			
			String requestXml = getCarrierRequestXmlFromDatabase(sessionId,TRANSPORT_ID);
			String actualIndustryValue = Utilities.getNodeValue(requestXml, carrierXmlPathIndustry);
			String actualOccupationValue = Utilities.getNodeValue(requestXml, carrierXmlPathOccupation);

			
			System.out.println("SessionID: " +sessionId);
			reportAttributes.put(ExcelReportWriter.ACORD_VALUE, acordValue);
			System.out.println("Expected Value: " +expectedIndustryValue);
			System.out.println("Actual Value: " +actualIndustryValue);
			System.out.println("Expected Value: " +expectedOccupationValue);
			System.out.println("Actual Value: " +actualOccupationValue);
			reportAttributes.put(ExcelReportWriter.SESSION_ID, sessionId);
			reportAttributes.put(ExcelReportWriter.EXPECTED_INDUSTRY_VALUE, expectedIndustryValue);
			reportAttributes.put(ExcelReportWriter.ACTUAL_INDUSTRY_VALUE, actualIndustryValue);
			reportAttributes.put(ExcelReportWriter.EXPECTED_OCCUPATION_VALUE, expectedOccupationValue);
			reportAttributes.put(ExcelReportWriter.ACTUAL_OCCUPATION_VALUE, actualOccupationValue);

			reportWriter.write(reportAttributes, "Occupations");
			Assert.assertEquals(expectedIndustryValue, actualIndustryValue);
			Assert.assertEquals(expectedOccupationValue, actualOccupationValue);
		}
	
	@DataProvider(name = "educationsData")
	public Object[][] educationsData() {
		String education="Education";
		return createDataProvider(education);
	}
	
	@Test(dataProvider = "educationsData")
	public void educationRC1Testing(Input state,String acordValue, String expected) throws Exception {
		
		    String xmlPath="/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersDriver/DriverInfo/PersonInfo/EducationLevelCd";
		
		    String carrierXmlPathEducation="/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersDriver/DriverInfo/QuestionAnswer/Explanation";

		    
			Utilities.updateNode(state.getFilePath(),acordValue,xmlPath);
			String sessionId = postRC1Request(state,CARRIER_NAME);
			
			String requestXml = getCarrierRequestXmlFromDatabase(sessionId,TRANSPORT_ID);
			String actualEducationValue = Utilities.getNodeValue(requestXml, carrierXmlPathEducation);

			
			System.out.println("SessionID: " +sessionId);
			System.out.println("Expected Value: " +expected);
			System.out.println("Actual Value: " +actualEducationValue);
			System.out.println("Expected Value: " +expected);
			Assert.assertEquals(expected, actualEducationValue);
			reportAttributes.put(ExcelReportWriter.SESSION_ID, sessionId);
			reportAttributes.put(ExcelReportWriter.ACORD_VALUE, acordValue);
			reportAttributes.put(ExcelReportWriter.EXPECTED_VALUE, expected);
			reportAttributes.put(ExcelReportWriter.ACTUAL_VALUE, actualEducationValue);


			reportWriter.write(reportAttributes, "Education");
		}
	

}


