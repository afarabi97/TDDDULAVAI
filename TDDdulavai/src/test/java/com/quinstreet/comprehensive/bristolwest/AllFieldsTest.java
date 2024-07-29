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

public class AllFieldsTest extends BaseTest {
	public static final String TRANSPORT_ID = "BristolWest";
	public static final String CARRIER_NAME="Bristol West";


	@BeforeMethod
	public void setUp() throws IOException {
		super.setUp();
		userName = "marsh@email.com";
		password = "R/&\\GD8)GAF/`;]<H90;ML_aMO3ZKN@Q\\J[`'`FB";
		replaceSid("src/test/resources/Comprehensive_Payloads", "1099107603");

	}

	private Object[][] createDataProvider(String filePath) {
		List<List<Object>> output = new ArrayList<>();
		List<String> states = Arrays.asList("UT");
		for (int i = 0; i < states.size(); i++) {
			String state = states.get(i);
			Input input = new Input(state,
					String.format("src/test/resources/Comprehensive_Payloads/%s_RC1.xml", state));
			Properties prop = read("src/test/resources/Carriers/BristolWest/AllTestsData/" + filePath + ".properties");
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

	@DataProvider(name = "nameSuffixTestData")
	public Object[][] nameSuffixTestData() {
		String nameSuffix = "NameSuffixData";
		return createDataProvider(nameSuffix);
	}

	@Test(dataProvider = "nameSuffixTestData")
	public void endToEndTesting(Input state, String acordValue, String expected) throws Exception {
		String xmlPath = "ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersDriver/GeneralPartyInfo/NameInfo/PersonName/NameSuffix";
		String carrierxmlPath = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/InsuredOrPrincipal/GeneralPartyInfo/NameInfo/PersonName/NameSuffix";

		Utilities.updateNode(state.getFilePath(), acordValue, xmlPath);
		String sessionId = postRC1Request(state,CARRIER_NAME);

		String requestXml = getCarrierRequestXmlFromDatabase(sessionId, TRANSPORT_ID);
		String actual = Utilities.getNodeValue(requestXml, carrierxmlPath);

		reportAttributes.put(ExcelReportWriter.SESSION_ID, sessionId);
		reportAttributes.put(ExcelReportWriter.ACORD_VALUE, acordValue);
		reportAttributes.put(ExcelReportWriter.EXPECTED_VALUE, expected);
		reportAttributes.put(ExcelReportWriter.ACTUAL_VALUE, actual);
		reportWriter.write(reportAttributes, "Name_Suffix");
		Assert.assertEquals(expected, actual);

	}

	
	@DataProvider(name = "genderTestData")
	public Object[][] genderTestData() {
		String genderData = "GenderData";
		return createDataProvider(genderData);
	}

	@Test(dataProvider = "genderTestData")
	public void genderTesting(Input state, String acordValue, String expected) throws Exception {
		String xmlPath = "ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersDriver[@id=\"D100\"]/DriverInfo/PersonInfo/GenderCd";
		String carrierxmlPath = "ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersDriver/DriverInfo/PersonInfo/GenderCd";

		Utilities.updateNode(state.getFilePath(), acordValue, xmlPath);
		String sessionId = postRC1Request(state,CARRIER_NAME);

		String requestXml = getCarrierRequestXmlFromDatabase(sessionId, TRANSPORT_ID);
		String actual = Utilities.getNodeValue(requestXml, carrierxmlPath);

		reportAttributes.put(ExcelReportWriter.SESSION_ID, sessionId);
		reportAttributes.put(ExcelReportWriter.ACORD_VALUE, acordValue);
		reportAttributes.put(ExcelReportWriter.EXPECTED_VALUE, expected);
		reportAttributes.put(ExcelReportWriter.ACTUAL_VALUE, actual);
		reportWriter.write(reportAttributes, "Gender");
		Assert.assertEquals(expected, actual);

	}
	
	@DataProvider(name = "maritalStatusTestData")
	public Object[][] maritalStatusTestData() {
		String maritalStatusData = "MaritalStatusData";
		return createDataProvider(maritalStatusData);
	}

	@Test(dataProvider = "maritalStatusTestData")
	public void maritalStatusTesting(Input state, String acordValue, String expected) throws Exception {
		String xmlPath = "ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersDriver[@id=\"D100\"]/DriverInfo/PersonInfo/MaritalStatusCd";
		String carrierxmlPath = "ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersDriver/DriverInfo/PersonInfo/MaritalStatusCd";

		Utilities.updateNode(state.getFilePath(), acordValue, xmlPath);
		String sessionId = postRC1Request(state,CARRIER_NAME);

		String requestXml = getCarrierRequestXmlFromDatabase(sessionId, TRANSPORT_ID);
		String actual = Utilities.getNodeValue(requestXml, carrierxmlPath);

		reportAttributes.put(ExcelReportWriter.SESSION_ID, sessionId);
		reportAttributes.put(ExcelReportWriter.ACORD_VALUE, acordValue);
		reportAttributes.put(ExcelReportWriter.EXPECTED_VALUE, expected);
		reportAttributes.put(ExcelReportWriter.ACTUAL_VALUE, actual);
		reportWriter.write(reportAttributes, "Marital_Status");
		Assert.assertEquals(expected, actual);

	}
	
	@DataProvider(name = "relationshipToApplicantTestData")
	public Object[][] relationshipToApplicantTestData() {
		String relationshipToApplicantData = "RelationshipToApplicantData";
		return createDataProvider(relationshipToApplicantData);
	}

	@Test(dataProvider = "relationshipToApplicantTestData")
	public void relationshipToApplicantTesting(Input state, String acordValue, String expected) throws Exception {
		String xmlPath = "ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersDriver[@id=\"D200\"]/PersDriverInfo/DriverRelationshipToApplicantCd";
		String carrierxmlPath = "ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersDriver[@id=\"Drv2\"]/PersDriverInfo/DriverRelationshipToApplicantCd";

		Utilities.updateNode(state.getFilePath(), acordValue, xmlPath);
		String sessionId = postRC1Request(state,CARRIER_NAME);

		String requestXml = getCarrierRequestXmlFromDatabase(sessionId, TRANSPORT_ID);
		String actual = Utilities.getNodeValue(requestXml, carrierxmlPath);

		reportAttributes.put(ExcelReportWriter.SESSION_ID, sessionId);
		reportAttributes.put(ExcelReportWriter.ACORD_VALUE, acordValue);
		reportAttributes.put(ExcelReportWriter.EXPECTED_VALUE, expected);
		reportAttributes.put(ExcelReportWriter.ACTUAL_VALUE, actual);
		reportWriter.write(reportAttributes, "Relationship_To_Applicant");
		Assert.assertEquals(expected, actual);

	}
	
	@DataProvider(name = "licenseStatusTestData")
	public Object[][] licenseStatusTestData() {
		String licenseStatusData = "LicenseStatusData";
		return createDataProvider(licenseStatusData);
	}

	@Test(dataProvider = "licenseStatusTestData")
	public void licenseStatusTesting(Input state, String acordValue, String expected) throws Exception {
		String xmlPath = "ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersDriver[@id=\"D100\"]/DriverInfo/License/LicenseStatusCd";
		String carrierxmlPath = "ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersDriver[@id=\"Drv1\"]/DriverInfo/License/LicenseStatusCd";

		Utilities.updateNode(state.getFilePath(), acordValue, xmlPath);
		String sessionId = postRC1Request(state,CARRIER_NAME);

		String requestXml = getCarrierRequestXmlFromDatabase(sessionId, TRANSPORT_ID);
		String actual = Utilities.getNodeValue(requestXml, carrierxmlPath);

		reportAttributes.put(ExcelReportWriter.SESSION_ID, sessionId);
		reportAttributes.put(ExcelReportWriter.ACORD_VALUE, acordValue);
		reportAttributes.put(ExcelReportWriter.EXPECTED_VALUE, expected);
		reportAttributes.put(ExcelReportWriter.ACTUAL_VALUE, actual);
		reportWriter.write(reportAttributes, "License_Status");
		Assert.assertEquals(expected, actual);

	}

	
	@DataProvider(name = "priorLengthWithPreviousInsurer")
	public Object[][] priorLengthWithPreviousInsurer() {
		String priorLength ="Prior_Length_With_Previous_Insurer";
		return createDataProvider(priorLength);
	}
	
	@Test(dataProvider = "priorLengthWithPreviousInsurer")
	public void priorLengthWithPreviousInsurerRC1Testing(Input state, String acordValue, String expected) throws Exception {
		String xmlPath = "ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersPolicy/OtherOrPriorPolicy[PolicyCd='Prior' and LOBCd='AUTOP']/LengthTimeWithPreviousInsurer[UnitMeasurementCd='ANN']/NumUnits";
		String carrierXmlPath="/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersPolicy/OtherOrPriorPolicy/LengthTimeWithPreviousInsurer/NumUnits";
	
			Utilities.updateNode(state.getFilePath(), acordValue, xmlPath);
			String sessionId = postRC1Request(state,CARRIER_NAME);

			String requestXml = getCarrierRequestXmlFromDatabase(sessionId,TRANSPORT_ID);
			String actual = Utilities.getNodeValue(requestXml, carrierXmlPath);

			
			reportAttributes.put(ExcelReportWriter.SESSION_ID, sessionId);
			reportAttributes.put(ExcelReportWriter.ACORD_VALUE, acordValue);
			reportAttributes.put(ExcelReportWriter.EXPECTED_VALUE, expected);
			reportAttributes.put(ExcelReportWriter.ACTUAL_VALUE, actual);
			reportWriter.write(reportAttributes, "Prior_Length");
			Assert.assertEquals(expected, actual);
		
	}
	
	@DataProvider(name = "residencetypeData")
	public Object[][] residencetypeData() {
		String occupations="residence_type";
		return createDataProvider(occupations);
	}
	@Test(dataProvider = "residencetypeData")
	public void residenceTypeTesting(Input state,String acordValue, String expected) throws Exception {
		
		String expectedResidenceOwnedValue=expected.split("/")[0];
	//	String expectedResidenceTypeValue=expected.split("/")[1];

		    String xmlPath="ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersPolicy/PersApplicationInfo/ResidenceTypeCd";
		
		    String carrierXmlPathResidenceOwned="ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersPolicy/PersApplicationInfo/ResidenceOwnedRentedCd";
		//   String carrierXmlPathResidenceType="ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersPolicy/PersApplicationInfo/ResidenceTypeCd";
		    
			Utilities.updateNode(state.getFilePath(),acordValue,xmlPath);
			String sessionId = postRC1Request(state,CARRIER_NAME);
			
			String requestXml = getCarrierRequestXmlFromDatabase(sessionId,TRANSPORT_ID);
			String actualResidenceOwnedValue = Utilities.getNodeValue(requestXml, carrierXmlPathResidenceOwned);
		//	String actualResidenceTypeValue = Utilities.getNodeValue(requestXml, carrierXmlPathResidenceType);

	
			reportAttributes.put(ExcelReportWriter.SESSION_ID, sessionId);
			reportAttributes.put(ExcelReportWriter.ACORD_VALUE, acordValue);
			reportAttributes.put(ExcelReportWriter.EXPECTED_RESIDENCE_OWNED_VALUE, expectedResidenceOwnedValue);
			reportAttributes.put(ExcelReportWriter.ACTUAL_RESIDENCE_OWNED_VALUE, actualResidenceOwnedValue);
			//reportAttributes.put(ExcelReportWriter.EXPECTED_RESIDENCE_TYPE_VALUE, expectedResidenceTypeValue);
			//reportAttributes.put(ExcelReportWriter.ACTUAL_RESIDENCE_TYPE_VALUE, actualResidenceTypeValue);

			reportWriter.write(reportAttributes, "Residence_Type");
			Assert.assertEquals(expectedResidenceOwnedValue, actualResidenceOwnedValue);
		//	Assert.assertEquals(expectedResidenceTypeValue, actualResidenceTypeValue);
		}
	
	@DataProvider(name = "ownershipOptionsData")
	public Object[][] ownershipOptionsData() {
		String ownership="Ownership";
		return createDataProvider(ownership);	}

	@Test(dataProvider = "ownershipOptionsData")
	public void vehicleOwnershipTesting(Input state,String acordValue, String expected) throws Exception {
		String xmlPath="/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/com.icom_VehicleOwnershipCd";
		String carrierXmlPath="ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/LeasedVehInd";
			Utilities.updateNode(state.getFilePath(),acordValue,xmlPath);
			String sessionId = postRC1Request(state,CARRIER_NAME);
			
			String requestXml = getCarrierRequestXmlFromDatabase(sessionId,TRANSPORT_ID);
			String actual = Utilities.getNodeValue(requestXml, carrierXmlPath);
			
			reportAttributes.put(ExcelReportWriter.SESSION_ID, sessionId);
			reportAttributes.put(ExcelReportWriter.ACORD_VALUE, acordValue);
			reportAttributes.put(ExcelReportWriter.EXPECTED_VALUE, expected);
			reportAttributes.put(ExcelReportWriter.ACTUAL_VALUE, actual);
			reportWriter.write(reportAttributes, "Vehicle_Ownership");
			Assert.assertEquals(expected, actual);
			
		}
	
}


