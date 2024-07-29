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

public class CoveragesTests extends BaseTest {
	
	public static final String TRANSPORT_ID="BristolWest";
	public static final String CARRIER_NAME="Bristol West";

	@BeforeMethod
	public void setUp() throws IOException {
		super.setUp();
		userName = "marsh@email.com";
		password = "R/&\\GD8)GAF/`;]<H90;ML_aMO3ZKN@Q\\J[`'`FB";
		replaceSid("src/test/resources/Comprehensive_Payloads", "1099107603");

	}

	@DataProvider(name = "bodilyInjury")
	public Object[][] bodilyInjury() {
		String bodilyInjury = "BI";
		return createDataProvider(bodilyInjury);
	}

	private Object[][] createDataProvider(String coverageFileName) {
		List<List<Object>> output = new ArrayList<>();
		List<String> states = Arrays.asList("UT");
		for (int i = 0; i < states.size(); i++) {
			String state = states.get(i);
			Input input = new Input(state,
					String.format("src/test/resources/Comprehensive_Payloads/%s_RC1.xml", state));
			Properties prop = read(
					"src/test/resources/Carriers/BristolWest/Coverages/"+state+"/"+ coverageFileName + ".properties");
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

	@Test(dataProvider = "bodilyInjury")
	public void bodilyInjuryCoveageRC1Testing(Input state, String acordValue, String carrierrequestValue)
			throws Exception {
		System.out.println(state.getState() + " " + acordValue + " " + carrierrequestValue);
		String payloadxmlPathPerPerson = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'BI']/Limit[LimitAppliesToCd = 'PerPerson']/FormatInteger";
		String payloadxmlPathPerAcc = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'BI']/Limit[LimitAppliesToCd = 'PerAcc']/FormatInteger";
		String payloadxmlPathCoverage = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'PD']/Limit[LimitAppliesToCd = 'Coverage']/FormatInteger";

		String carrierrequestxmlPathPerPerson = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'BI']/Limit[LimitAppliesToCd = 'PerPerson']/FormatInteger";
		String carrierrequestxmlPathPerAccident = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'BI']/Limit[LimitAppliesToCd = 'PerAccident']/FormatInteger";
		String carrierrequestxmlPathCoverage = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'PD']/Limit[LimitAppliesToCd = 'PerAccident']/FormatInteger";

		if (acordValue.indexOf("/") != -1) {
			String expectedPerPerson = carrierrequestValue.split("/")[0];
			String expectedPerAcc = carrierrequestValue.split("/")[1];
			String expectedCoverage = carrierrequestValue.split("/")[2];

			System.out.println(acordValue.split("/")[0]);
			System.out.println(acordValue.split("/")[1]);
			System.out.println(acordValue.split("/")[2]);

			Utilities.updateNode(state.getFilePath(), acordValue.split("/")[0], payloadxmlPathPerPerson);
			Utilities.updateNode(state.getFilePath(), acordValue.split("/")[1], payloadxmlPathPerAcc);
			Utilities.updateNode(state.getFilePath(), acordValue.split("/")[2], payloadxmlPathCoverage);

			String sessionId = postRC1Request(state,CARRIER_NAME);

			String requestXml = getCarrierRequestXmlFromDatabase(sessionId,TRANSPORT_ID);
			String actualPerPerson = Utilities.getNodeValue(requestXml, carrierrequestxmlPathPerPerson);
			String actualPerAcc = Utilities.getNodeValue(requestXml, carrierrequestxmlPathPerAccident);
			String actualCoverage = Utilities.getNodeValue(requestXml, carrierrequestxmlPathCoverage);
			
			reportAttributes.put(ExcelReportWriter.SESSION_ID, sessionId);
			reportAttributes.put(ExcelReportWriter.ACORD_VALUE, acordValue);
			reportAttributes.put(ExcelReportWriter.EXPECTED_VALUE, carrierrequestValue);
			reportAttributes.put(ExcelReportWriter.ACTUAL_VALUE_PER_PERSON, actualPerPerson);
			reportAttributes.put(ExcelReportWriter.ACTUAL_VALUE_PER_ACC, actualPerAcc);
			reportAttributes.put(ExcelReportWriter.ACTUAL_VALUE_PD_PER_PERSON, actualCoverage);

			reportWriter.write(reportAttributes, "Bodily Injury");

			Assert.assertEquals(expectedPerPerson, actualPerPerson);
			Assert.assertEquals(expectedPerAcc, actualPerAcc);
			Assert.assertEquals(expectedCoverage, actualCoverage);

		}
	}

	@DataProvider(name = "medicalPayments")
	public Object[][] medicalPayments() {
		String medicalPayments = "MedicalPayments";
		return createDataProvider(medicalPayments);
	}

	@Test(dataProvider = "medicalPayments")
	public void medicalCoverageRC1Testing(Input state, String acordValue, String expectedCarrierRequestValue)
			throws Exception {
		String payloadxmlPathMedicalCoverage = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd='MEDPM']/Limit[LimitAppliesToCd='PerPerson']/FormatInteger";
		String carrierrequestxmlPathMedicalCoverage = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd='MEDPM']/Limit/FormatInteger";

		Utilities.updateNode(state.getFilePath(), acordValue, payloadxmlPathMedicalCoverage);

		String sessionId = postRC1Request(state,CARRIER_NAME);
		String requestXml = getCarrierRequestXmlFromDatabase(sessionId,TRANSPORT_ID);
		String actualMedicalCoverage = Utilities.getNodeValue(requestXml, carrierrequestxmlPathMedicalCoverage);

		reportAttributes.put(ExcelReportWriter.SESSION_ID, sessionId);
		reportAttributes.put(ExcelReportWriter.ACORD_VALUE, acordValue);
		reportAttributes.put(ExcelReportWriter.EXPECTED_VALUE, expectedCarrierRequestValue);
		reportAttributes.put(ExcelReportWriter.ACTUAL_VALUE, actualMedicalCoverage);
		
		reportWriter.write(reportAttributes, "Medical Coverage");

		Assert.assertEquals(expectedCarrierRequestValue, actualMedicalCoverage);
	}

	@DataProvider(name = "collisionData")
	public Object[][] collisionData() {
		String collision = "Collision";
		return createDataProvider(collision);
	}

	@Test(dataProvider = "collisionData")
	public void collisionCoverageRC1Testing(Input state, String acordValue, String expectedCarrierRequestValue)
			throws Exception {
		String payloadxmlPathCollisionCoverage = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd='COLL']/Deductible/FormatInteger";
		String payloadxmlPathCollisionStandardOption = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd='COLL']/Option[OptionTypeCd='Opt1']/OptionCd";
		String carrierrequestxmlPathCollisionCoverage = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd='COLL']/Deductible/FormatInteger";
		//Added this for comprehensive coverage - Broad for MI state
		//String carrierrequestxmlPathCollisionCoverage = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd='BFC']/Deductible/FormatInteger";

	    if ("MI".contains(state.getState())) {
			Utilities.updateNode(state.getFilePath(), acordValue.split("/")[0], payloadxmlPathCollisionCoverage);
			Utilities.updateNode(state.getFilePath(), acordValue.split("/")[1], payloadxmlPathCollisionStandardOption);
		} else {
			Utilities.updateNode(state.getFilePath(), acordValue, payloadxmlPathCollisionCoverage);
		}

		String sessionId = postRC1Request(state,CARRIER_NAME);

		String requestXml = getCarrierRequestXmlFromDatabase(sessionId,TRANSPORT_ID);
		String actualCollisionCoverage = Utilities.getNodeValue(requestXml, carrierrequestxmlPathCollisionCoverage);

		reportAttributes.put(ExcelReportWriter.SESSION_ID, sessionId);
		reportAttributes.put(ExcelReportWriter.ACORD_VALUE, acordValue);
		reportAttributes.put(ExcelReportWriter.EXPECTED_VALUE, expectedCarrierRequestValue);
		reportAttributes.put(ExcelReportWriter.ACTUAL_VALUE, actualCollisionCoverage);
		
		reportWriter.write(reportAttributes, "Collision Coverage");
		Assert.assertEquals(expectedCarrierRequestValue, actualCollisionCoverage);
	}

	@DataProvider(name = "comprehensiveData")
	public Object[][] comprehensiveData() {
		String comprehensive = "Comprehensive";
		return createDataProvider(comprehensive);
	}

	@Test(dataProvider = "comprehensiveData")
	public void comprehensiveCoverageRC1Testing(Input state, String acordValue, String expectedCarrierRequestValue)
			throws Exception {
		String payloadxmlPathComprehensiveCoverage = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd='COMP']/Deductible/FormatInteger";
		String carrierrequestxmlPathComprehensiveCoverage = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd='COMP']/Deductible/FormatInteger";
		String carrierrequestxmlPathOptionComprehensiveCoverage = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd='OTC']/Deductible/FormatInteger";
		
		Utilities.updateNode(state.getFilePath(), acordValue, payloadxmlPathComprehensiveCoverage);

		String sessionId = postRC1Request(state,CARRIER_NAME);

		String requestXml = getCarrierRequestXmlFromDatabase(sessionId,TRANSPORT_ID);
		String actualComprehensiveCoverage = "";
		if("MI,PA".contains(state.getState())){
			actualComprehensiveCoverage = Utilities.getNodeValue(requestXml, carrierrequestxmlPathOptionComprehensiveCoverage);
		}else {
			actualComprehensiveCoverage = Utilities.getNodeValue(requestXml, carrierrequestxmlPathComprehensiveCoverage);
		}

		reportAttributes.put(ExcelReportWriter.SESSION_ID, sessionId);
		reportAttributes.put(ExcelReportWriter.ACORD_VALUE, acordValue);
		reportAttributes.put(ExcelReportWriter.EXPECTED_VALUE, expectedCarrierRequestValue);
		reportAttributes.put(ExcelReportWriter.ACTUAL_VALUE, actualComprehensiveCoverage);
		
		reportWriter.write(reportAttributes, "Comprehensive Coverage");

		Assert.assertEquals(expectedCarrierRequestValue, actualComprehensiveCoverage);
	}

	@DataProvider(name = "rentalCoverageData")
	public Object[][] rentalCoverageData() {
		String rental = "Rental";
		return createDataProvider(rental);
	}

	@Test(dataProvider = "rentalCoverageData")
	public void rentalCoverageRC1Testing(Input state, String acordValue, String expectedCarrierRequestValue)
			throws Exception {
		String payloadxmlPathRentalCoveragePerDay = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd='RREIM']/Limit[LimitAppliesToCd='PerDay']/FormatInteger";
		String payloadxmlPathRentalCoveragePerDayMaxAmount = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd='RREIM']/Limit[LimitAppliesToCd='MaxAmount']/FormatInteger";

		String carrierrequestxmlPathRentalCoverage = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd='TRNEX']/Limit/FormatInteger";

		if("PA,MT".contains(state.getState()))	
		{

			carrierrequestxmlPathRentalCoverage = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd='EXTR']/Limit/FormatInteger"; 
		}

		if (acordValue.indexOf("/") != -1) {
			Utilities.updateNode(state.getFilePath(), acordValue.split("/")[0], payloadxmlPathRentalCoveragePerDay);
			Utilities.updateNode(state.getFilePath(), acordValue.split("/")[1], payloadxmlPathRentalCoveragePerDayMaxAmount);

			String sessionId = postRC1Request(state,CARRIER_NAME);

			String requestXml = getCarrierRequestXmlFromDatabase(sessionId,TRANSPORT_ID);
			String actual = Utilities.getNodeValue(requestXml, carrierrequestxmlPathRentalCoverage);

			reportAttributes.put(ExcelReportWriter.SESSION_ID, sessionId);
			reportAttributes.put(ExcelReportWriter.ACORD_VALUE, acordValue);
			reportAttributes.put(ExcelReportWriter.EXPECTED_VALUE, expectedCarrierRequestValue);
			reportAttributes.put(ExcelReportWriter.ACTUAL_VALUE, actual);
			
			reportWriter.write(reportAttributes, "Rental Coverage");

			Assert.assertEquals(expectedCarrierRequestValue, actual);
		}

	}
	
	
	@DataProvider(name = "towingCoverageData")
	public Object[][] towingCoverageData() {
		String towing = "Towing";
		return createDataProvider(towing);
	}
	
	@Test(dataProvider = "towingCoverageData")
	public void towingCoverageRC1Testing(Input state, String acordValue, String expectedCarrierRequestValue)
			throws Exception {
		String payloadxmlPathTowingCoveragePerDay = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd='TL']/Limit[LimitAppliesToCd='Coverage']/FormatText";

		String carrierrequestxmlPathTowingCoverage = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd='TL']/Limit/FormatInteger";

			Utilities.updateNode(state.getFilePath(), acordValue, payloadxmlPathTowingCoveragePerDay);

			String sessionId = postRC1Request(state,CARRIER_NAME);

			String requestXml = getCarrierRequestXmlFromDatabase(sessionId,TRANSPORT_ID);
			String actual = Utilities.getNodeValue(requestXml, carrierrequestxmlPathTowingCoverage);


			reportAttributes.put(ExcelReportWriter.SESSION_ID, sessionId);
			reportAttributes.put(ExcelReportWriter.ACORD_VALUE, acordValue);
			reportAttributes.put(ExcelReportWriter.EXPECTED_VALUE, expectedCarrierRequestValue);
			reportAttributes.put(ExcelReportWriter.ACTUAL_VALUE, actual);
			
			reportWriter.write(reportAttributes, "Towing Coverage");

			Assert.assertEquals(expectedCarrierRequestValue, actual);
		}

	@DataProvider(name = "propertyDamageCoverageData")
	public Object[][] propertyDamageCoverageData() {
		String propertyDamage = "PropertyDamage";
		return createDataProvider(propertyDamage);
	}
	
	@Test(dataProvider = "propertyDamageCoverageData")
	public void propertyDamageCoverageRC1Testing(Input state, String acordValue, String expectedCarrierRequestValue)
			throws Exception {
		String payloadxmlPathPropertyDamageCoverage = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd=\"UMPD\"]/Limit[LimitAppliesToCd=\"Coverage\"]/FormatInteger";

		String carrierrequestxmlPathPropertyDamageCoverage = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd=\"UMPD\"]/Limit/FormatInteger";

			Utilities.updateNode(state.getFilePath(), acordValue, payloadxmlPathPropertyDamageCoverage);

			String sessionId = postRC1Request(state,CARRIER_NAME);

			String requestXml = getCarrierRequestXmlFromDatabase(sessionId,TRANSPORT_ID);
			String actual = Utilities.getNodeValue(requestXml, carrierrequestxmlPathPropertyDamageCoverage);


			reportAttributes.put(ExcelReportWriter.SESSION_ID, sessionId);
			reportAttributes.put(ExcelReportWriter.ACORD_VALUE, acordValue);
			reportAttributes.put(ExcelReportWriter.EXPECTED_VALUE, expectedCarrierRequestValue);
			reportAttributes.put(ExcelReportWriter.ACTUAL_VALUE, actual);
			
			reportWriter.write(reportAttributes, "PropertyDamage Coverage");

			Assert.assertEquals(expectedCarrierRequestValue, actual);
		}

	@DataProvider(name = "UMCoverageData")
	public Object[][] UMCoverageData() {
		String UM = "UM";
		return createDataProvider(UM);
	}
	
	@Test(dataProvider = "UMCoverageData")
	public void UMCoveageRC1Testing(Input state, String acordValue, String expectedCarrierRequestValue)
			throws Exception {
		System.out.println(state.getState() + " " + acordValue + " " + expectedCarrierRequestValue);
		String payloadxmlPathPerPerson = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'UM']/Limit[LimitAppliesToCd ='PerPerson']/FormatInteger";
		String payloadxmlPathPerAcc = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'UM']/Limit[LimitAppliesToCd ='PerAcc']/FormatInteger";

		String carrierrequestxmlPathUMPerPerson = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'UM']/Limit[LimitAppliesToCd ='PerPerson']/FormatInteger";
		String carrierrequestxmlPathUMPerAcc = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'UM']/Limit[LimitAppliesToCd ='PerAcc']/FormatInteger";
		String carrierrequestxmlPathUIMPerPerson = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'UIM']/Limit[LimitAppliesToCd ='PerPerson']/FormatInteger";
		String carrierrequestxmlPathUIMPerAccident = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'UIM']/Limit[LimitAppliesToCd ='PerAcc']/FormatInteger";
		String carrierrequestxmlPathUMPerAccident = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'UM']/Limit[LimitAppliesToCd ='PerAccident']/FormatInteger";
		String carrierrequestxmlPathUNDUMPerPerson = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'UNDUM']/Limit[LimitAppliesToCd ='PerPerson']/FormatInteger";
		String carrierrequestxmlPathUNDUMPerAccident = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'UNDUM']/Limit[LimitAppliesToCd ='PerAccident']/FormatInteger";
		String carrierrequestxmlPathUNDUMPerAcc = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'UNDUM']/Limit[LimitAppliesToCd ='PerAcc']/FormatInteger";
		if (acordValue.indexOf("/") != -1) {
			String expectedPerPerson = expectedCarrierRequestValue.split("/")[0];
			String expectedPerAcc = expectedCarrierRequestValue.split("/")[1];

			Utilities.updateNode(state.getFilePath(), acordValue.split("/")[0], payloadxmlPathPerPerson);
			Utilities.updateNode(state.getFilePath(), acordValue.split("/")[1], payloadxmlPathPerAcc);

			String sessionId = postRC1Request(state,CARRIER_NAME);

			String requestXml = getCarrierRequestXmlFromDatabase(sessionId,TRANSPORT_ID);
			String actualUMPerPerson = Utilities.getNodeValue(requestXml, carrierrequestxmlPathUMPerPerson);
			String actualUMPerAcc="";
			String actualUIMPerPerson = "";
			String actualUIMPerAcc ="";
			String actualUNDUMPerPerson = "";
			String actualUNDUMPerAccident="";
			if("NE,IL".contains(state.getState())) {
				actualUNDUMPerPerson = Utilities.getNodeValue(requestXml, carrierrequestxmlPathUNDUMPerPerson);
				if("NE".contains(state.getState())) {
				actualUMPerAcc = Utilities.getNodeValue(requestXml, carrierrequestxmlPathUMPerAccident);
				actualUNDUMPerAccident = Utilities.getNodeValue(requestXml, carrierrequestxmlPathUNDUMPerAccident);
				}
				if("IL".contains(state.getState())) {
					actualUMPerAcc = Utilities.getNodeValue(requestXml, carrierrequestxmlPathUMPerAcc);
					actualUNDUMPerAccident = Utilities.getNodeValue(requestXml, carrierrequestxmlPathUNDUMPerAcc);
					}
				}else
				actualUMPerAcc = Utilities.getNodeValue(requestXml, carrierrequestxmlPathUMPerAcc);
			
			if("MI".contains(state.getState())){
			actualUIMPerPerson = Utilities.getNodeValue(requestXml, carrierrequestxmlPathUIMPerPerson);
			actualUIMPerAcc = Utilities.getNodeValue(requestXml, carrierrequestxmlPathUIMPerAccident);
			}
			reportAttributes.put(ExcelReportWriter.SESSION_ID, sessionId);
			reportAttributes.put(ExcelReportWriter.ACORD_VALUE, acordValue);
			reportAttributes.put(ExcelReportWriter.EXPECTED_VALUE, expectedCarrierRequestValue);
			reportAttributes.put(ExcelReportWriter.ACTUAL_VALUE_PER_PERSON, actualUMPerPerson);
            if("NE,IL".contains(state.getState())) {
            	reportAttributes.put(ExcelReportWriter.ACTUAL_VALUE_PER_ACC, actualUMPerAcc);
            	reportAttributes.put(ExcelReportWriter.UIM_ACTUAL_VALUE_PER_PERSON, actualUNDUMPerPerson);
            	reportAttributes.put(ExcelReportWriter.UIM_ACTUAL_VALUE_PER_ACC, actualUNDUMPerAccident);
            }else
			reportAttributes.put(ExcelReportWriter.ACTUAL_VALUE_PER_ACC, actualUMPerAcc);
			if("MI".contains(state.getState())){
			reportAttributes.put(ExcelReportWriter.UIM_ACTUAL_VALUE_PER_PERSON, actualUIMPerPerson);
			reportAttributes.put(ExcelReportWriter.UIM_ACTUAL_VALUE_PER_ACC, actualUIMPerAcc);
			}
			
			reportWriter.write(reportAttributes, "UM Coverage");

			Assert.assertEquals(expectedPerPerson, actualUMPerPerson);
			Assert.assertEquals(expectedPerAcc, actualUMPerAcc);


		}
	}
	
	@DataProvider(name = "UIMCoverageData")
	public Object[][] UIMCoverageData() {
		String UIM = "UIM";
		return createDataProvider(UIM);
	}
	
	@Test(dataProvider = "UIMCoverageData")
	public void UIMCoveageRC1Testing(Input state, String acordValue, String expectedCarrierRequestValue)
			throws Exception {
		System.out.println(state.getState() + " " + acordValue + " " + expectedCarrierRequestValue);
		String payloadxmlUMPathPerPerson = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'UM']/Limit[LimitAppliesToCd ='PerPerson']/FormatInteger";
		String payloadxmlUMPathPerAcc = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'UM']/Limit[LimitAppliesToCd ='PerAcc']/FormatInteger";
		String payloadxmlUIMPathPerPerson = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'UNDUM']/Limit[LimitAppliesToCd ='PerPerson']/FormatInteger";
		String payloadxmlUIMPathPerAcc = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'UNDUM']/Limit[LimitAppliesToCd ='PerAcc']/FormatInteger";

		String carrierrequestxmlPathUMPerPerson = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'UM']/Limit[LimitAppliesToCd ='PerPerson']/FormatInteger";
		String carrierrequestxmlPathUMPerAccident = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'UM']/Limit[LimitAppliesToCd ='PerAcc']/FormatInteger";
		String carrierrequestxmlPathUIMPerPerson = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'UNDUM']/Limit[LimitAppliesToCd ='PerPerson']/FormatInteger";
		String carrierrequestxmlPathUIMPerAccident = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'UNDUM']/Limit[LimitAppliesToCd ='PerAcc']/FormatInteger";

		if (state.getState() == "PA")
		{
			 carrierrequestxmlPathUMPerPerson = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'UM']/Limit[LimitAppliesToCd ='PerPerson']/FormatInteger";
			 carrierrequestxmlPathUMPerAccident = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'UM']/Limit[LimitAppliesToCd ='PerAcc']/FormatInteger";
			 carrierrequestxmlPathUIMPerPerson = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'UIM']/Limit[LimitAppliesToCd ='PerPerson']/FormatInteger";
			 carrierrequestxmlPathUIMPerAccident = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'UIM']/Limit[LimitAppliesToCd ='PerAcc']/FormatInteger";
		}
		
		if (acordValue.indexOf("/") != -1) {
			String expectedUMPerPerson = expectedCarrierRequestValue.split("/")[0];
			String expectedUMPerAcc = expectedCarrierRequestValue.split("/")[1];
			String expectedUIMPerPerson= expectedCarrierRequestValue.split("/")[2];
			String expectedUIMPerAcc = expectedCarrierRequestValue.split("/")[3];

			Utilities.updateNode(state.getFilePath(), acordValue.split("/")[0], payloadxmlUMPathPerPerson);
			Utilities.updateNode(state.getFilePath(), acordValue.split("/")[1], payloadxmlUMPathPerAcc);
			Utilities.updateNode(state.getFilePath(), acordValue.split("/")[2], payloadxmlUIMPathPerPerson);
			Utilities.updateNode(state.getFilePath(), acordValue.split("/")[3], payloadxmlUIMPathPerAcc);

			String sessionId = postRC1Request(state,CARRIER_NAME);

			String requestXml = getCarrierRequestXmlFromDatabase(sessionId,TRANSPORT_ID);
			String actualUMPerPerson = Utilities.getNodeValue(requestXml, carrierrequestxmlPathUMPerPerson);
			String actualUMPerAcc = Utilities.getNodeValue(requestXml, carrierrequestxmlPathUMPerAccident);
			String actualUIMPerPerson = Utilities.getNodeValue(requestXml, carrierrequestxmlPathUIMPerPerson);
			String actualUIMPerAcc = Utilities.getNodeValue(requestXml, carrierrequestxmlPathUIMPerAccident);
			

			reportAttributes.put(ExcelReportWriter.SESSION_ID, sessionId);
			reportAttributes.put(ExcelReportWriter.ACORD_VALUE, acordValue);
			reportAttributes.put(ExcelReportWriter.EXPECTED_VALUE, expectedCarrierRequestValue);
			reportAttributes.put(ExcelReportWriter.UM_ACTUAL_VALUE_PER_PERSON, actualUMPerPerson);
			reportAttributes.put(ExcelReportWriter.UM_ACTUAL_VALUE_PER_ACC, actualUMPerAcc);
			reportAttributes.put(ExcelReportWriter.UIM_ACTUAL_VALUE_PER_PERSON, actualUMPerPerson);
			reportAttributes.put(ExcelReportWriter.UIM_ACTUAL_VALUE_PER_ACC, actualUMPerAcc);
			
			reportWriter.write(reportAttributes, "UIM Coverage");

			Assert.assertEquals(expectedUMPerPerson, actualUMPerPerson);
			Assert.assertEquals(expectedUMPerAcc, actualUMPerAcc);
			Assert.assertEquals(expectedUIMPerPerson, actualUIMPerPerson);
			Assert.assertEquals(expectedUIMPerAcc, actualUIMPerAcc);


		}
	}
	
	@DataProvider(name = "UM_UMPDCoverageData")
	public Object[][] UM_UMPDCoverageData() {
		String umumpd = "UM_UMPD";
		return createDataProvider(umumpd);
	}
	
	@Test(dataProvider = "UM_UMPDCoverageData")
	public void UMUMPDCoveageRC1Testing(Input state, String acordValue, String expectedCarrierRequestValue)
			throws Exception {
		System.out.println(state.getState() + " " + acordValue + " " + expectedCarrierRequestValue);
		String payloadxmlUMPathPerPerson = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'UM']/Limit[LimitAppliesToCd ='PerPerson']/FormatInteger";
		String payloadxmlUMPathPerAcc = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'UM']/Limit[LimitAppliesToCd ='PerAcc']/FormatInteger";
		String payloadxmlPathCoverage = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'UMPD']/Limit[LimitAppliesToCd ='Coverage']/FormatInteger";

		String carrierrequestxmlPathUMPerPerson = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'UM']/Limit[LimitAppliesToCd ='PerPerson']/FormatInteger";
		String carrierrequestxmlPathUMPerAcc = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'UM']/Limit[LimitAppliesToCd ='PerAcc']/FormatInteger";
		String carrierrequestxmlPathUMPerAccident = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'UM']/Limit[LimitAppliesToCd = 'PerAccident']/FormatInteger";
		
		String carrierrequestxmlPathUIMPerPerson = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'UNDUM']/Limit[LimitAppliesToCd ='PerPerson']/FormatInteger";
		String carrierrequestxmlPathUIMPerAcc = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'UNDUM']/Limit[LimitAppliesToCd ='PerAcc']/FormatInteger";
		String carrierrequestxmlPathUIMPerAccident = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'UNDUM']/Limit[LimitAppliesToCd ='PerAccident']/FormatInteger";

		String carrierrequestxmlPathUMPD = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'UMPD']/Limit/FormatInteger";
		String carrierrequestxmlPathUMPDCoverage = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'UMPD']/Limit[LimitAppliesToCd = 'Coverage']/FormatInteger";
		String carrierrequestxmlPathUMPDPerAccident = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'UMPD']/Limit[LimitAppliesToCd ='PerAccident']/FormatInteger";
		
		if (acordValue.indexOf("/") != -1) {
			String expectedUMPerPerson = expectedCarrierRequestValue.split("/")[0];
			String expectedUMPerAcc = expectedCarrierRequestValue.split("/")[1];
			String expectedUMPD = "";
			if("MS,AR,NM".contains(state.getState())){
				expectedUMPD = expectedCarrierRequestValue.split("/")[4];
			} else
				expectedUMPD = expectedCarrierRequestValue.split("/")[2];

			Utilities.updateNode(state.getFilePath(), acordValue.split("/")[0], payloadxmlUMPathPerPerson);
			Utilities.updateNode(state.getFilePath(), acordValue.split("/")[1], payloadxmlUMPathPerAcc);
			Utilities.updateNode(state.getFilePath(), acordValue.split("/")[2], payloadxmlPathCoverage);

			String sessionId = postRC1Request(state,CARRIER_NAME);

			String requestXml = getCarrierRequestXmlFromDatabase(sessionId,TRANSPORT_ID);
			String actualUMPerPerson = Utilities.getNodeValue(requestXml, carrierrequestxmlPathUMPerPerson);
			String actualUMPerAcc = "";
			String actualUIMPerPerson = "";
			String actualUIMPerAcc = "";
			String actualUMPD = "";
			
			if("MS,NM".contains(state.getState())){
				actualUMPerAcc = Utilities.getNodeValue(requestXml, carrierrequestxmlPathUMPerAccident);
				actualUIMPerPerson = Utilities.getNodeValue(requestXml, carrierrequestxmlPathUIMPerPerson);
				actualUIMPerAcc = Utilities.getNodeValue(requestXml, carrierrequestxmlPathUIMPerAccident);
				actualUMPD = Utilities.getNodeValue(requestXml, carrierrequestxmlPathUMPD);
			} else {
				actualUMPerAcc = Utilities.getNodeValue(requestXml, carrierrequestxmlPathUMPerAcc);
				if("AR".contains(state.getState())){
					actualUIMPerPerson = Utilities.getNodeValue(requestXml, carrierrequestxmlPathUIMPerPerson);
					actualUIMPerAcc = Utilities.getNodeValue(requestXml, carrierrequestxmlPathUIMPerAcc);
					actualUMPD = Utilities.getNodeValue(requestXml, carrierrequestxmlPathUMPDPerAccident);
				} else
					actualUMPD = Utilities.getNodeValue(requestXml, carrierrequestxmlPathUMPDCoverage);
			}

			reportAttributes.put(ExcelReportWriter.SESSION_ID, sessionId);
			reportAttributes.put(ExcelReportWriter.ACORD_VALUE, acordValue);
			reportAttributes.put(ExcelReportWriter.EXPECTED_VALUE, expectedCarrierRequestValue);
			reportAttributes.put(ExcelReportWriter.UM_ACTUAL_VALUE_PER_PERSON, actualUMPerPerson);
			reportAttributes.put(ExcelReportWriter.UM_ACTUAL_VALUE_PER_ACC, actualUMPerAcc);
			reportAttributes.put(ExcelReportWriter.UMPD_ACTUAL_VALUE, actualUMPD);
			if("MS,AR,NM".contains(state.getState())){
				reportAttributes.put(ExcelReportWriter.UIM_ACTUAL_VALUE_PER_PERSON, actualUIMPerPerson);
				reportAttributes.put(ExcelReportWriter.UIM_ACTUAL_VALUE_PER_ACC, actualUIMPerAcc);
			}
			reportWriter.write(reportAttributes, "UIM Coverage");

			Assert.assertEquals(expectedUMPerPerson, actualUMPerPerson);
			Assert.assertEquals(expectedUMPerAcc, actualUMPerAcc);
			Assert.assertEquals(expectedUMPD, actualUMPD);


		}
	}
	
	@DataProvider(name = "pipCoverageData")
	public Object[][] pipCoverageData() {
		String pipCoverage = "PIP";
		return createDataProvider(pipCoverage);
	}
	@Test(dataProvider = "pipCoverageData")
	public void pipCoverageRC1Testing(Input state, String acordValue, String expectedCarrierRequestValue)
			throws Exception {
		String payloadxmlPathpipCoveragePerPerson = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd='PIP']/Limit[LimitAppliesToCd='PerPerson']/FormatInteger";
		String payloadxmlPathpipCoverageDeductible = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd='PIP']/Deductible/FormatInteger";
		String payloadxmlPathpipCoverageOpt1 = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd='PIP']/Option[OptionTypeCd='Opt1']/OptionCd";
		String payloadxmlPathpipCoverageOpt2 = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd='PIP']/Option[OptionTypeCd='Opt2']/OptionCd";

		String carrierrequestxmlpipCoveragePerPerson = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd='PIP']/Limit[LimitAppliesToCd='PerPerson']/FormatInteger";
		String carrierrequestxmlpipCoverageBPIP = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd='BPIP']/Deductible/FormatInteger";
		String carrierrequestxmlpipCoverageOpt1 = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd='BPIP']/Option[OptionTypeCd='Opt1']/OptionCd";
		String carrierrequestxmlpipCoverageOpt2 = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd='BPIP']/Option[2]/OptionCd";


		String expectedPerPerson = expectedCarrierRequestValue.split("/")[0];
		String expectedDeductible = expectedCarrierRequestValue.split("/")[1];
		String expectedCoverageOpt1 = expectedCarrierRequestValue.split("/")[2];	
		String expectedCoverageOpt2 = expectedCarrierRequestValue.split("/")[3];	

		
		Utilities.updateNode(state.getFilePath(), acordValue.split("/")[0], payloadxmlPathpipCoveragePerPerson);
		Utilities.updateNode(state.getFilePath(), acordValue.split("/")[1], payloadxmlPathpipCoverageDeductible);
		Utilities.updateNode(state.getFilePath(), acordValue.split("/")[2], payloadxmlPathpipCoverageOpt1);
		Utilities.updateNode(state.getFilePath(), acordValue.split("/")[3], payloadxmlPathpipCoverageOpt2);


			String sessionId = postRC1Request(state,CARRIER_NAME);

			String requestXml = getCarrierRequestXmlFromDatabase(sessionId,TRANSPORT_ID);
			String actualPerPerson = Utilities.getNodeValue(requestXml, carrierrequestxmlpipCoveragePerPerson);
			String actualDeductible = Utilities.getNodeValue(requestXml, carrierrequestxmlpipCoverageBPIP);
			String actualOpt1 = Utilities.getNodeValue(requestXml, carrierrequestxmlpipCoverageOpt1);
			String actualOpt2 = Utilities.getNodeValue(requestXml, carrierrequestxmlpipCoverageOpt2);
			
			reportAttributes.put(ExcelReportWriter.SESSION_ID, sessionId);
			reportAttributes.put(ExcelReportWriter.ACORD_VALUE, acordValue);
			reportAttributes.put(ExcelReportWriter.EXPECTED_VALUE, expectedCarrierRequestValue);
			reportAttributes.put(ExcelReportWriter.ACTUAL_VALUE_PER_PERSON, actualPerPerson);
			reportAttributes.put(ExcelReportWriter.ACTUAL_VALUE_DEDUCTIBLE, actualDeductible);
			reportAttributes.put(ExcelReportWriter.ACTUAL_VALUE_OPT1, actualOpt1);
			reportAttributes.put(ExcelReportWriter.ACTUAL_VALUE_OPT2, actualOpt2);
			
			reportWriter.write(reportAttributes, "PIP Coverage");

			Assert.assertEquals(expectedPerPerson, actualPerPerson);
			Assert.assertEquals(expectedDeductible, actualDeductible);
			Assert.assertEquals(expectedCoverageOpt1, actualOpt1);
			Assert.assertEquals(expectedCoverageOpt2, actualOpt2);

		}
	
	
	@DataProvider(name = "customizationCoverageData")
	public Object[][] customizationCoverageData() {
		String customizationCoverage = "CustomizationCoverage";
		return createDataProvider(customizationCoverage);
	}
	@Test(dataProvider = "customizationCoverageData")
	public void customizationCoverageRC1Testing(Input state, String acordValue, String expectedCarrierRequestValue)
			throws Exception {
		String payloadxmlPathCustomizationCoverage = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd='CUSTE']/Limit[LimitAppliesToCd='Coverage']/FormatInteger";

		String carrierrequestxmlPathCustomizationCoverage = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd='CAC']/Limit/FormatInteger";

			Utilities.updateNode(state.getFilePath(), acordValue, payloadxmlPathCustomizationCoverage);

			String sessionId = postRC1Request(state,CARRIER_NAME);

			String requestXml = getCarrierRequestXmlFromDatabase(sessionId,TRANSPORT_ID);
			String actual = Utilities.getNodeValue(requestXml, carrierrequestxmlPathCustomizationCoverage);

			reportAttributes.put(ExcelReportWriter.SESSION_ID, sessionId);
			reportAttributes.put(ExcelReportWriter.ACORD_VALUE, acordValue);
			reportAttributes.put(ExcelReportWriter.EXPECTED_VALUE, expectedCarrierRequestValue);
			reportAttributes.put(ExcelReportWriter.ACTUAL_VALUE, actual);
			
			reportWriter.write(reportAttributes, "Customization Coverage");

			Assert.assertEquals(expectedCarrierRequestValue, actual);
		}
	
	@DataProvider(name = "UMCSLCoverageData")
	public Object[][] UMCSLCoverageData() {
		String UMCSL = "UMCSL";
		return createDataProvider(UMCSL);
	}
	@Test(dataProvider = "UMCSLCoverageData")
	public void UMCSLCoverageDataCoverageRC1Testing(Input state, String acordValue, String expectedCarrierRequestValue)
			throws Exception {
		String payloadxmlPathUMCSLCoverage = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd='UMCSL']/Option[OptionTypeCd='Opt1']/OptionCd";

		String carrierrequestxmlPathUMCSLCoverage = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd='UMCSL']/Option[OptionTypeCd='Opt1']/OptionCd";

			Utilities.updateNode(state.getFilePath(), acordValue, payloadxmlPathUMCSLCoverage);

			String sessionId = postRC1Request(state,CARRIER_NAME);

			String requestXml = getCarrierRequestXmlFromDatabase(sessionId,TRANSPORT_ID);
			String actual = Utilities.getNodeValue(requestXml, carrierrequestxmlPathUMCSLCoverage);

			System.out.println("SessionID: " + sessionId);
			reportAttributes.put(ExcelReportWriter.ACORD_VALUE, acordValue);
			System.out.println("Expected Value Custmization Coverage: " + expectedCarrierRequestValue);
			System.out.println("Actual Value Custmization Coverage: " + actual);

			Assert.assertEquals(expectedCarrierRequestValue, actual);
		}
	
	@DataProvider(name = "UMPDCoverageData")
	public Object[][] UMPDCoverageData() {
		String UMPD = "UMPD";
		return createDataProvider(UMPD);
	}
	@Test(dataProvider = "UMPDCoverageData")
	public void UMPDCoverageDataCoverageRC1Testing(Input state, String acordValue, String expectedCarrierRequestValue)
			throws Exception {

		
		String payloadxmlPathPerPerson = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'UM']/Limit[LimitAppliesToCd = 'PerPerson']/FormatInteger";
		String payloadxmlPathPerAcc = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'UM']/Limit[LimitAppliesToCd = 'PerAcc']/FormatInteger";
		String payloadxmlPathCoverage = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd= 'UMPD']/Limit[LimitAppliesToCd=\"Coverage\"]/FormatInteger";

		String carrierrequestxmlPathPerPerson = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'UM']/Limit[LimitAppliesToCd = 'PerPerson']/FormatInteger";
		String carrierrequestxmlPathPerAccident = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'UM']/Limit[LimitAppliesToCd = 'PerAccident']/FormatInteger";
		String carrierrequestxmlPathCoverage = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'UMPD']/Limit[LimitAppliesToCd = 'Coverage']/FormatInteger";

			if (state.getState() == "WA")
		{

			  carrierrequestxmlPathPerPerson = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'UNDUM']/Limit[LimitAppliesToCd = 'PerPerson']/FormatInteger";
			  carrierrequestxmlPathPerAccident = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'UNDUM']/Limit[LimitAppliesToCd = 'PerAccident']/FormatInteger";
			  carrierrequestxmlPathCoverage = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'UNDPD']/Limit[LimitAppliesToCd ='PerAccident']/FormatInteger";	
			  
			
		}

		if (acordValue.indexOf("/") != -1) {
			String expectedPerPerson = expectedCarrierRequestValue.split("/")[0];
			String expectedPerAcc = expectedCarrierRequestValue.split("/")[1];
			String expectedCoverage = expectedCarrierRequestValue.split("/")[2];

			System.out.println(acordValue.split("/")[0]);
			System.out.println(acordValue.split("/")[1]);
			System.out.println(acordValue.split("/")[2]);

			Utilities.updateNode(state.getFilePath(), acordValue.split("/")[0], payloadxmlPathPerPerson);
			Utilities.updateNode(state.getFilePath(), acordValue.split("/")[1], payloadxmlPathPerAcc);
			Utilities.updateNode(state.getFilePath(), acordValue.split("/")[2], payloadxmlPathCoverage);

			String sessionId = postRC1Request(state,CARRIER_NAME);

			String requestXml = getCarrierRequestXmlFromDatabase(sessionId,TRANSPORT_ID);
			String actualPerPerson = Utilities.getNodeValue(requestXml, carrierrequestxmlPathPerPerson);
			String actualPerAcc = Utilities.getNodeValue(requestXml, carrierrequestxmlPathPerAccident);
			String actualCoverage = Utilities.getNodeValue(requestXml, carrierrequestxmlPathCoverage);

			System.out.println("SessionID: " + sessionId);
			reportAttributes.put(ExcelReportWriter.ACORD_VALUE, acordValue);
			System.out.println("Expected Value Per Person: " + expectedPerPerson);
			System.out.println("Expected Value Per Acc: " + expectedPerAcc);
			System.out.println("Expected Value Coverage: " + expectedCoverage);

			System.out.println("Actual Value Per Person: " + actualPerPerson);
			System.out.println("Actual Value Per Acc: " + actualPerAcc);
			System.out.println("Actual Value Coverage: " + actualCoverage);

			Assert.assertEquals(expectedPerPerson, actualPerPerson);
			Assert.assertEquals(expectedPerAcc, actualPerAcc);
			Assert.assertEquals(expectedCoverage, actualCoverage);

		}
		
	}	
		@DataProvider(name = "PIPCoverageData")
		public Object[][] PIPCoverageData() {
			String PIP = "PIP";
			return createDataProvider(PIP);
		}
		@Test(dataProvider = "PIPCoverageData")
		public void PIPCoverageDataCoverageRC1Testing(Input state, String acordValue, String expectedCarrierRequestValue)
				throws Exception {

			
			String payloadxmlPathPerPerson = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd='PIP']/Limit[LimitAppliesToCd='PerPerson']/FormatInteger";
			String payloadxmlPathIncEconLoss = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd='PIP']/ ]/Limit[LimitAppliesToCd = 'IncEconLoss']/FormatInteger";
		    String payloadxmlPathpipMedicalCoverage = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd='PIP']/Limit[LimitAppliesToCd='Medical']/FormatInteger";

			String carrierrequestxmlPathPerPerson = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd='PIP']/Limit[LimitAppliesToCd='PerPerson']/FormatInteger";
			String carrierrequestxmlPathIncEconLoss = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd='PIP']/Option/OptionCd";
		    String expectedPerPerson="";
		    String expectedIncEconLoss="";
			if (acordValue.indexOf("/") != -1) {
			expectedPerPerson = expectedCarrierRequestValue.split("/")[0];
			expectedIncEconLoss = expectedCarrierRequestValue.split("/")[1];

				System.out.println(acordValue.split("/")[0]);
				System.out.println(acordValue.split("/")[1]);

				Utilities.updateNode(state.getFilePath(), acordValue.split("/")[0], payloadxmlPathPerPerson);
				Utilities.updateNode(state.getFilePath(), acordValue.split("/")[1], payloadxmlPathIncEconLoss);

				String sessionId = postRC1Request(state,CARRIER_NAME);

				String requestXml = getCarrierRequestXmlFromDatabase(sessionId,TRANSPORT_ID);
				String actualPerPerson = Utilities.getNodeValue(requestXml, carrierrequestxmlPathPerPerson);
				String actualIncEconLoss = Utilities.getNodeValue(requestXml, carrierrequestxmlPathIncEconLoss);

				System.out.println("SessionID: " + sessionId);
				reportAttributes.put(ExcelReportWriter.ACORD_VALUE, acordValue);
				System.out.println("Expected Value Per Person: " + expectedPerPerson);
				System.out.println("Expected Value Per Acc: " + expectedIncEconLoss);

				System.out.println("Actual Value Per Person: " + actualPerPerson);
				System.out.println("Actual Value Per Acc: " + actualIncEconLoss);

				Assert.assertEquals(expectedPerPerson, actualPerPerson);
				Assert.assertEquals(expectedIncEconLoss, actualIncEconLoss);
		}else {
			if("MI".contains(state.getState())){
			Utilities.updateNode(state.getFilePath(), acordValue, payloadxmlPathpipMedicalCoverage);
			String sessionId = postRC1Request(state,CARRIER_NAME);
			String requestXml = getCarrierRequestXmlFromDatabase(sessionId, TRANSPORT_ID);
			String actualMedicalCoverage = Utilities.getNodeValue(requestXml, carrierrequestxmlPathPerPerson);
			reportAttributes.put(ExcelReportWriter.SESSION_ID, sessionId);
			reportAttributes.put(ExcelReportWriter.ACORD_VALUE, acordValue);
			reportAttributes.put(ExcelReportWriter.EXPECTED_VALUE, expectedCarrierRequestValue);
			reportAttributes.put(ExcelReportWriter.ACTUAL_VALUE, actualMedicalCoverage);
			reportWriter.write(reportAttributes, "Medical Coverage");
			Assert.assertEquals(expectedCarrierRequestValue, actualMedicalCoverage);
			}
		  }

		
	}

	
	@DataProvider(name = "AccidentalDeathCoverageData")
		public Object[][] AccidentalDeath() 
		{
			String AccidentalDeathCoverage = "AccidentalDeathCoverage";
			return createDataProvider(AccidentalDeathCoverage);
		}
		@Test(dataProvider = "AccidentalDeathCoverageData")
		public void AccidentalDeathCoverageRC1Testing(Input state, String acordValue, String expectedCarrierRequestValue)
				throws Exception 
		{
			String payloadxmlPathAccidentaldeathCoverage = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'ADB']/Limit[LimitAppliesToCd = 'PerPerson']/FormatInteger";
																																						    
			String carrierrequestxmlPathAccidentaldeathCoverage = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd='ACCD']/Limit/FormatInteger";
			
				Utilities.updateNode(state.getFilePath(), acordValue, payloadxmlPathAccidentaldeathCoverage);

				String sessionId = postRC1Request(state,CARRIER_NAME);
				

				String requestXml = getCarrierRequestXmlFromDatabase(sessionId,TRANSPORT_ID);
				String actual = Utilities.getNodeValue(requestXml, carrierrequestxmlPathAccidentaldeathCoverage);

				
				reportAttributes.put(ExcelReportWriter.SESSION_ID, sessionId);
				reportAttributes.put(ExcelReportWriter.ACORD_VALUE, acordValue);
				reportAttributes.put(ExcelReportWriter.EXPECTED_VALUE, expectedCarrierRequestValue);
				reportAttributes.put(ExcelReportWriter.ACTUAL_VALUE, actual);
				
				reportWriter.write(reportAttributes, "AccidentalDeath Coverage");

				Assert.assertEquals(expectedCarrierRequestValue, actual);
			}
		
		
		@DataProvider(name = "CmbFirstPartyBenefitsData")
		public Object[][] CmbFirstPartyBenefitsData() {
			String CFPB = "CFPB";
			return createDataProvider(CFPB);
		}
		@Test(dataProvider = "CmbFirstPartyBenefitsData")
		public void CmbFirstPartyBenefitsCoverageRC1Testing(Input state, String acordValue, String expectedCarrierRequestValue)
				throws Exception {
			String payloadxmlPathCFPBCoverage = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd='CFPB']/Limit[LimitAppliesToCd='TotalBen']/FormatInteger";
																																									    
			String carrierrequestxmlPathCFPBCoverage = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd='COMB']/Limit/FormatInteger";
			
			
				Utilities.updateNode(state.getFilePath(), acordValue, payloadxmlPathCFPBCoverage);

				String sessionId = postRC1Request(state,CARRIER_NAME);
				
				String requestXml = getCarrierRequestXmlFromDatabase(sessionId,TRANSPORT_ID);
				String actual = Utilities.getNodeValue(requestXml, carrierrequestxmlPathCFPBCoverage);

				reportAttributes.put(ExcelReportWriter.SESSION_ID, sessionId);
				reportAttributes.put(ExcelReportWriter.ACORD_VALUE, acordValue);
				reportAttributes.put(ExcelReportWriter.EXPECTED_VALUE, expectedCarrierRequestValue);
				reportAttributes.put(ExcelReportWriter.ACTUAL_VALUE, actual);
				
				reportWriter.write(reportAttributes, "CFPB Coverage");

				Assert.assertEquals(expectedCarrierRequestValue, actual);
			}
		
		

		@DataProvider(name = "IncomeLossCoverageData")
		public Object[][] IncomeLossCoverage() 
		{
			String IncomeLossCoverage = "IncomeLossCoverage";
			return createDataProvider(IncomeLossCoverage);
		}
		@Test(dataProvider = "IncomeLossCoverageData")
		public void IncomeLossCoverageRC1Testing(Input state, String acordValue, String expectedCarrierRequestValue)
				throws Exception 
		{
			String payloadxmlPathIncomeLossCoveragepermonth = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'WLB']/Limit[LimitAppliesToCd = 'Monthly']/FormatInteger";
			String payloadxmlPathIncomeLossCoverageworkloss = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'WLB']/Limit[LimitAppliesToCd = 'WorkLoss']/FormatInteger";
			
			String carrierrequestxmlPathIncomeLossCoverage = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd='INCL']/Limit/FormatInteger";
																						
			if (acordValue.indexOf("/") != -1) 
			{
			
				Utilities.updateNode(state.getFilePath(), acordValue.split("/")[0], payloadxmlPathIncomeLossCoveragepermonth);
				Utilities.updateNode(state.getFilePath(), acordValue.split("/")[1], payloadxmlPathIncomeLossCoverageworkloss);

			
				String sessionId = postRC1Request(state,CARRIER_NAME);			
				String requestXml = getCarrierRequestXmlFromDatabase(sessionId,TRANSPORT_ID);
				String actual = Utilities.getNodeValue(requestXml, carrierrequestxmlPathIncomeLossCoverage);

				reportAttributes.put(ExcelReportWriter.SESSION_ID, sessionId);
				reportAttributes.put(ExcelReportWriter.ACORD_VALUE, acordValue);
				reportAttributes.put(ExcelReportWriter.EXPECTED_VALUE, expectedCarrierRequestValue);
				reportAttributes.put(ExcelReportWriter.ACTUAL_VALUE, actual);
				
				reportWriter.write(reportAttributes, "IncomeLossCoverage");

				Assert.assertEquals(expectedCarrierRequestValue, actual);
			}
		
		}
		
		

		@DataProvider(name = "TortOptionData")
		public Object[][] TortOptionData() {
			String TortOption = "TortOption";
			return createDataProvider(TortOption);
		}
		@Test(dataProvider = "TortOptionData")
		public void TortOptionCoverageRC1Testing(Input state, String acordValue, String expectedCarrierRequestValue)
				throws Exception {
			String payloadxmlPathTortCoverage = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd='TORT']/Option[OptionTypeCd='Opt1']/OptionCd";
		    
			String carrierrequestxmlPathTortCoverage = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'TORT']/Option/OptionCd";
		
			
				Utilities.updateNode(state.getFilePath(), acordValue, payloadxmlPathTortCoverage);

				String sessionId = postRC1Request(state,CARRIER_NAME);
				
				String requestXml = getCarrierRequestXmlFromDatabase(sessionId,TRANSPORT_ID);
				String actual = Utilities.getNodeValue(requestXml, carrierrequestxmlPathTortCoverage);

			
				reportAttributes.put(ExcelReportWriter.SESSION_ID, sessionId);
				reportAttributes.put(ExcelReportWriter.ACORD_VALUE, acordValue);
				reportAttributes.put(ExcelReportWriter.EXPECTED_VALUE, expectedCarrierRequestValue);
				reportAttributes.put(ExcelReportWriter.ACTUAL_VALUE, actual);
				
				reportWriter.write(reportAttributes, "Tort Coverage");

				Assert.assertEquals(expectedCarrierRequestValue, actual);
			}
		

		@DataProvider(name = "FuneralCoveragedata")
		public Object[][] FuneralCoveragedata() {
			String FuneralCoverage = "FuneralCoverage";
			return createDataProvider(FuneralCoverage);
		}
		@Test(dataProvider = "FuneralCoveragedata")
		public void FuneralcoverageCoverageRC1Testing(Input state, String acordValue, String expectedCarrierRequestValue)
				throws Exception {
			String payloadxmlPathFuneralCoverage =  "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'FEB']/Limit[LimitAppliesToCd = 'PerPerson']/FormatInteger";
			String carrierrequestxmlPathFuneralCoverage = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'FUNB']/Limit/FormatInteger";
			
				
				Utilities.updateNode(state.getFilePath(), acordValue, payloadxmlPathFuneralCoverage);

				String sessionId = postRC1Request(state,CARRIER_NAME);
				
				String requestXml = getCarrierRequestXmlFromDatabase(sessionId,TRANSPORT_ID);
				String actual = Utilities.getNodeValue(requestXml, carrierrequestxmlPathFuneralCoverage);

				reportAttributes.put(ExcelReportWriter.SESSION_ID, sessionId);
				reportAttributes.put(ExcelReportWriter.ACORD_VALUE, acordValue);
				reportAttributes.put(ExcelReportWriter.EXPECTED_VALUE, expectedCarrierRequestValue);
				reportAttributes.put(ExcelReportWriter.ACTUAL_VALUE, actual);
				
				reportWriter.write(reportAttributes, "Funeral Coverage");

				Assert.assertEquals(expectedCarrierRequestValue, actual);
			}

			
		@DataProvider(name = "UMStackCoveragedata")
		public Object[][] UMStackCoveragedata() {
			String UMstack = "UMstack";
			return createDataProvider(UMstack);
		}
		@Test(dataProvider = "UMStackCoveragedata")
		public void UmstackcoverageRC1Testing(Input state, String acordValue, String expectedCarrierRequestValue)
				throws Exception {
			String payloadxmlPathUMstackCoverage = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'UM']/Option[OptionTypeCd = 'Opt1']/OptionCd";
			String carrierrequestxmlPathUMstackCoverage = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd='UM']/Option[OptionTypeCd='Option 1']/OptionCd";
				
				Utilities.updateNode(state.getFilePath(), acordValue, payloadxmlPathUMstackCoverage);

				String sessionId = postRC1Request(state,CARRIER_NAME);
				
				String requestXml = getCarrierRequestXmlFromDatabase(sessionId,TRANSPORT_ID);
				String actual = Utilities.getNodeValue(requestXml, carrierrequestxmlPathUMstackCoverage);
				
				reportAttributes.put(ExcelReportWriter.SESSION_ID, sessionId);
				reportAttributes.put(ExcelReportWriter.ACORD_VALUE, acordValue);
				reportAttributes.put(ExcelReportWriter.EXPECTED_VALUE, expectedCarrierRequestValue);
				reportAttributes.put(ExcelReportWriter.ACTUAL_VALUE, actual);
				
				reportWriter.write(reportAttributes, "UMstack Coverage");

				Assert.assertEquals(expectedCarrierRequestValue, actual);
			}
		
		
		@DataProvider(name = "EXMedicalCoveragedata")
		public Object[][] EXMedicalCoveragedata() {
			String EXMedical = "EXMedical";
			return createDataProvider(EXMedical);
		}
		@Test(dataProvider = "EXMedicalCoveragedata")
		public void EXMedicalCoverageRC1Testing(Input state, String acordValue, String expectedCarrierRequestValue)
				throws Exception {
			String payloadxmlPathExmedicalCoverage = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd='EXMED']/Limit[LimitAppliesToCd='PerPerson']/FormatInteger";
			String carrierrequestxmlPathExmedicalCoverage = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'EMP']/Limit/FormatInteger";;
				
				Utilities.updateNode(state.getFilePath(), acordValue, payloadxmlPathExmedicalCoverage);

				String sessionId = postRC1Request(state,CARRIER_NAME);
				
				String requestXml = getCarrierRequestXmlFromDatabase(sessionId,TRANSPORT_ID);
				String actual = Utilities.getNodeValue(requestXml, carrierrequestxmlPathExmedicalCoverage);
			
				reportAttributes.put(ExcelReportWriter.SESSION_ID, sessionId);
				reportAttributes.put(ExcelReportWriter.ACORD_VALUE, acordValue);
				reportAttributes.put(ExcelReportWriter.EXPECTED_VALUE, expectedCarrierRequestValue);
				reportAttributes.put(ExcelReportWriter.ACTUAL_VALUE, actual);
				
				reportWriter.write(reportAttributes, "EXmedical Coverage");

				Assert.assertEquals(expectedCarrierRequestValue, actual);
			}

		
		@DataProvider(name = "MedicalExpense")
		public Object[][] medicalexpense() {
			String MedicalExpenses = "MedicalExpenses";
			return createDataProvider(MedicalExpenses);
		}

		@Test(dataProvider = "MedicalExpense")
		public void medicalexpenseRC1Testing(Input state, String acordValue, String expectedCarrierRequestValue)
				throws Exception {
			String payloadxmlPathMedicalExpCoverage = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd='MEDEX']/Limit[LimitAppliesToCd='PerPerson']/FormatInteger";
			String carrierrequestxmlPathMedicalExpCoverage = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd='MEDEX']/Limit/FormatInteger";
					
			Utilities.updateNode(state.getFilePath(), acordValue, payloadxmlPathMedicalExpCoverage);

			String sessionId = postRC1Request(state,CARRIER_NAME);
			String requestXml = getCarrierRequestXmlFromDatabase(sessionId,TRANSPORT_ID);
			String actualMedicalCoverage = Utilities.getNodeValue(requestXml, carrierrequestxmlPathMedicalExpCoverage);

			reportAttributes.put(ExcelReportWriter.SESSION_ID, sessionId);
			reportAttributes.put(ExcelReportWriter.ACORD_VALUE, acordValue);
			reportAttributes.put(ExcelReportWriter.EXPECTED_VALUE, expectedCarrierRequestValue);
			reportAttributes.put(ExcelReportWriter.ACTUAL_VALUE, actualMedicalCoverage);

			reportWriter.write(reportAttributes, "MedicalExp Coverage");

			Assert.assertEquals(expectedCarrierRequestValue, actualMedicalCoverage);
		}

		
		@DataProvider(name = "BICSLData")
		public Object[][] BICSLData() {
			String BICSL ="BICSL";
			return createDataProvider(BICSL);
		}
		@Test(dataProvider = "BICSLData")
		public void BICSLRC1Testing(Input state, String acordValue, String expected) throws Exception {
			String xmlPath = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'CSL']/Limit[LimitAppliesToCd = 'CSL']/FormatInteger";
			String carrierxmlPathPerPerson = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'BI']/Limit[LimitAppliesToCd='PerPerson']/FormatInteger";
			String carrierxmlPathPerAcc = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'BI']/Limit[LimitAppliesToCd='PerAccident']/FormatInteger";
			String carrierxmlPathPD = "/ACORD/InsuranceSvcRq/PersAutoPolicyQuoteInqRq/PersAutoLineBusiness/PersVeh/Coverage[CoverageCd = 'PD']/Limit[LimitAppliesToCd='PerAccident']/FormatInteger";


					String expectedPerPerson = expected.split("/")[0];
					String expectedPerAcc = expected.split("/")[1];
					String expectedPDPerAcc = expected.split("/")[2];

					Utilities.updateNode(state.getFilePath(), acordValue, xmlPath);
					String sessionId = postRC1Request(state,CARRIER_NAME);
					String requestXml = getCarrierRequestXmlFromDatabase(sessionId,TRANSPORT_ID);
					String actualPerPerson = Utilities.getNodeValue(requestXml, carrierxmlPathPerPerson);
					String actualPerAcc = Utilities.getNodeValue(requestXml, carrierxmlPathPerAcc);
					String actualPDPerAcc = Utilities.getNodeValue(requestXml, carrierxmlPathPD);

					Assert.assertEquals(expectedPerPerson, actualPerPerson);
					Assert.assertEquals(expectedPerAcc, actualPerAcc);
					Assert.assertEquals(expectedPDPerAcc, actualPDPerAcc);
					
					reportAttributes.put(ExcelReportWriter.SESSION_ID, sessionId);
					reportAttributes.put(ExcelReportWriter.ACORD_VALUE, acordValue);
					reportAttributes.put(ExcelReportWriter.EXPECTED_VALUE, expected);
					reportAttributes.put(ExcelReportWriter.ACTUAL_VALUE_PER_PERSON, actualPerPerson);
					reportAttributes.put(ExcelReportWriter.ACTUAL_VALUE_PER_ACC, actualPerAcc);
					reportAttributes.put(ExcelReportWriter.ACTUAL_VALUE_PD_PER_PERSON, actualPDPerAcc);
					reportWriter.write(reportAttributes, "BI_CSL Coverage");


		}
		
	
}


