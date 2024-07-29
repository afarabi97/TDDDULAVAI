package com.quinstreet.test;

import static com.jayway.restassured.RestAssured.given;


import static com.jayway.restassured.filter.log.LogDetail.ALL;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.BooleanUtils;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.filter.log.RequestLoggingFilter;
import com.jayway.restassured.filter.log.ResponseLoggingFilter;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.quinstreet.database.Database;
import com.quinstreet.model.Field;
import com.quinstreet.model.Option;
import com.quinstreet.model.UAPIRequest;
import com.quinstreet.model.UAPIResponse;

import ExcelResults.ExcelReportWriter;
import groovy.json.internal.Charsets;
import static org.hamcrest.core.StringContains.containsStringIgnoringCase;
import static org.junit.Assert.assertThat;


public class BaseTest {
	public static final String PRODUCTION_URL = "https://auto-insurance-quote.insurance.com";
	public static final String RC3_BASE_URI="https://api-tok-stage.insurance.com/AutoInsuranceServices";
	protected ObjectMapper objectMapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	protected Map<String, Object> testData;
	protected List<String> sectionsToBeExcluded = Arrays.asList("Vehicle Coverages", "Liability Coverages");
	protected String userName;
	protected String password;
	protected String actualRC2Response;
	protected static Database database = new Database();
	protected ExcelReportWriter reportWriter;
	protected Map<String, String> reportAttributes = new LinkedHashMap<String,String>();


	@SuppressWarnings("unchecked")
	@BeforeMethod
	public void setUp() throws IOException {
		userName = "matic_uapi@qrp.com";
		password = "P@s5w0rD";
		RestAssured.baseURI = "http://test.insurance.com";
		//RestAssured.baseURI = "https://stage.insurance.com";
		RestAssured.basePath = "/AutoInsuranceServices";
		RestAssured.filters(new RequestLoggingFilter(ALL,true,System.out), new ResponseLoggingFilter(ALL,true,System.out));
		replaceSid("src/test/resources/Regression_Payloads", "1099107532");
		replaceDate("src/test/resources/Regression_Payloads");
		replaceSid("src/test/resources/TestScenario_Payloads", "1099107532");
		replaceDate("src/test/resources/TestScenario_Payloads");
		replaceDate("src/test/resources/Comprehensive_Payloads");
		this.testData = objectMapper.readValue(new FileReader("src/main/resources/testData.json"), Map.class);
		reportAttributes.clear();
		reportWriter = new ExcelReportWriter();
	}
	
	@AfterMethod
	public void destroy() throws Exception {
		reportWriter.flush();
	}

	protected static Properties read(String path) {
		try (InputStream input = new FileInputStream(path)) {
			Properties prop = new Properties();
			// load a properties file
			prop.load(input);
			return prop;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public void replaceDate(String dirName) {
		File parentDir = new File(dirName);
		Collection<File> children = FileUtils.listFilesAndDirs(parentDir, TrueFileFilter.INSTANCE,
				DirectoryFileFilter.DIRECTORY);
		for (File child : children) {
			if (child.isDirectory()) {
				if (!parentDir.equals(child)) {
					replaceDate(child.getAbsolutePath());
				}
			} else {
				try {
					replaceFileString(child.getAbsolutePath(), "<EffectiveDt>(.+?)</EffectiveDt>",
							"<EffectiveDt>" + getCurrentDateForRC1Request() + "</EffectiveDt>");
					replaceFileString(child.getAbsolutePath(), "<ExpirationDt>(.+?)</ExpirationDt>",
							"<ExpirationDt>" + getCurrentDateForRC1Request() + "</ExpirationDt>");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected void replaceSid(String dirName, String newSid) {
		File parent = new File(dirName);
		Collection<File> children = FileUtils.listFilesAndDirs(parent, TrueFileFilter.INSTANCE,
				DirectoryFileFilter.DIRECTORY);
		for (File child : children) {
			if (child.isDirectory()) {
				if (!parent.equals(child)) {
					replaceSid(child.getAbsolutePath(), newSid);
				}
			} else {
				try {
					replaceFileString(child.getAbsolutePath(), "<CustPermId>(.+?)</CustPermId>",
							"<CustPermId>" + newSid + "</CustPermId>");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private String getCurrentDateForRC1Request() {
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.format(new java.util.Date());
	}

	public void replaceFileString(String fileName, String oldString, String newString) throws IOException {
		FileInputStream fis = new FileInputStream(fileName);
		String content = IOUtils.toString(fis, Charset.defaultCharset());
		content = content.replaceAll(oldString, newString);
		FileOutputStream fos = new FileOutputStream(fileName);
		IOUtils.write(content, new FileOutputStream(fileName), Charset.defaultCharset());
		fis.close();
		fos.close();
	}

	@DataProvider(name = "singleStateRegression")
	public Object[][] singleStateRegression() {
		List<String> states = Arrays.asList("OH");
		return generateRegressionData(states, "Regression_Payloads");
	}

	@DataProvider(name = "multipleStatesRegression")
	public Object[][] multipleStatesRegression() {
		List<String> states = Arrays.asList("CT", "FL", "MD","IN","SC","TX","GA");
		return generateRegressionData(states, "Regression_Payloads");
	}

	protected Object[][] generateRegressionData(List<String> states, String filePath) {
		Object[][] data = new Object[states.size()][];
		for (int i = 0; i < states.size(); i++) {
			String state = states.get(i);
			data[i] = new Input[] {
					new Input(state, String.format("src/test/resources/" + filePath + "/%s_RC1.xml", state)) };
		}
		return data;
	}

	@DataProvider(name = "multipleStatesTestScenario")
	public Object[][] multipleStatesTestScenario() {
		List<String> states = Arrays.asList("AZ", "CO", "CT", "FL", "GA","IL","IN","MD","NJ","OH","OR","PA","SC","TX","VA");
		return generateTestScenarioData(states);
	}

	@DataProvider(name = "singleStateTestScenario")
	public Object[][] singleStateTestScenario() {
		List<String> states = Arrays.asList("UT");
		return generateTestScenarioData(states);
	}

	private Object[][] generateTestScenarioData(List<String> states) {
		List<Input> inputList = new ArrayList<>();

		for (String state : states) {
			File dir = new File(String.format("src/test/resources/TestScenario_Payloads/%s", state));
			File[] directoryListing = dir.listFiles();
			for (File child : directoryListing) {
				inputList.add(new Input(state, child.getAbsolutePath()));
			}
		}
		Object[][] data = new Object[inputList.size()][];
		for (int i = 0; i < inputList.size(); i++) {
			data[i] = new Object[] { inputList.get(i) };
		}
		return data;
	}

	protected void addCoverageStartDate(String todayDate, List<Field> fields) {
		for (int i = 0; i < fields.size(); i++) {
			if (fields.get(i).getFieldName().equals("QTE_CoverageStartDate")) {
				fields.get(i).setFieldValue(todayDate);
			}
		}
	}

	protected void addCurrentPolicyExpirationDate(String todayDate, List<Field> fields) {
		for (int i = 0; i < fields.size(); i++) {
			if (fields.get(i).getFieldName().equals("QTE_CurrentPolicyExpirationDate")) {
				fields.get(i).setFieldValue(todayDate);
			}
		}
	}

	protected List<Field> buildRequest(UAPIResponse res, Map<String, Object> testData, UAPIRequest request) {
		List<Field> fields = new ArrayList<>();
		request.setFields(fields);
		for (Field field : res.getFields()) {
			if (BooleanUtils.isTrue(field.isVisible()) && BooleanUtils.isTrue(field.getIsRequired())
					&& !sectionsToBeExcluded.contains(field.getSubSection())) {
				Field reqField = new Field();
				reqField.setFieldName(field.getFieldName());
				if (field.getValues() != null && !field.getValues().isEmpty()
						&& !testData.containsKey(field.getFieldName())) {
					Optional<Option> optionOptional = field.getValues().stream().filter(option -> option.isVisible())
							.findFirst();
					if (optionOptional.isPresent()) {
						reqField.setFieldValue(optionOptional.get().getInternalValue());
					}
				} else {
					reqField.setFieldValue((String) testData.get(field.getFieldName()));
				}
				fields.add(reqField);
			}
		}
		return fields;

	}

	protected UAPIResponse getPageFields(String sessionId, String pageName, int carrierId) {
		//@formatter:off
		return 
				given()
				.queryParam("pageName", pageName)
				.queryParam("sessionId", sessionId)
				.queryParam("carrierId", carrierId)
				.urlEncodingEnabled(true)
				.auth().preemptive()
				.basic(userName, password)
				.get("/Page/GetPageFields")
				.then()
				.assertThat()
				.statusCode(200)
				.extract().
				as(UAPIResponse.class);
		//@formatter:on
	}

	protected String postRC1Request(Input state,String carrierName ) throws IOException {
		String request = FileUtils.readFileToString(new File(state.getFilePath()), Charset.defaultCharset());
		//@formatter:off
		Response response = 
				given()
				.contentType(ContentType.XML)
				.accept(ContentType.XML)
				.body(request)
				.when()
				.post("/Api/Publisher/GetRatesForExternalPublisher");
		//@formatter:on
	String sessionId = response.xmlPath().get("ACORD.SignonRs.SessionId");
		response.then().statusCode(200);
		
	/*	  String carrierStatus = response.xmlPath() .get(
		  "**.findAll{it.CarrierName=='"+carrierName+"'}.MsgStatus.MsgStatusCd");
		  assertThat(carrierStatus, containsStringIgnoringCase("Success"));
		  String carrierMessageStatusDesc = response.xmlPath().get("**.findAll{it.CarrierName=='"+carrierName+"'}.MsgStatus.MsgStatusDesc");
		 
			reportAttributes.put("CarrierStatus", carrierStatus);
			reportAttributes.put("CarrierStatusMessage", carrierMessageStatusDesc);

		//	 assertThat(carrierStatus, containsStringIgnoringCase("Success")); */
		return sessionId;

	}

	protected String postRC1RequestSafeco(Input state) throws IOException {
		String request = FileUtils.readFileToString(new File(state.getFilePath()), Charset.defaultCharset());
		//@formatter:off
		Response response = 
				given()
				.contentType(ContentType.XML)
				.accept(ContentType.XML)
				.body(request)
				.when()
				.post("/Api/Publisher/GetRatesForExternalPublisher");
		//@formatter:on
		String sessionId = response.xmlPath().get("ACORD.SignonRs.SessionId");
		response.then().statusCode(200);
		String safecoStatus = response.xmlPath().get("**.findAll{it.CarrierName=='Safeco'}.MsgStatus.MsgStatusCd");
		String safecoMessageStatusDesc = response.xmlPath()
				.get("**.findAll{it.CarrierName=='Safeco'}.MsgStatus.MsgStatusDesc");

		reportAttributes.put("SafecoStatus", safecoStatus);
		reportAttributes.put("SafecoStatusMessage", safecoMessageStatusDesc);

		 assertThat(safecoStatus, containsStringIgnoringCase("Success")); 
		return sessionId;

	}
	
	protected String postRC1RequestBW(Input state) throws IOException {
		String request = FileUtils.readFileToString(new File(state.getFilePath()), Charset.defaultCharset());
		//@formatter:off
		Response response = 
				given()
				.contentType(ContentType.XML)
				.accept(ContentType.XML)
				.body(request)
				.when()
				.post("/Api/Publisher/GetRatesForExternalPublisher");
		//@formatter:on
		String sessionId = response.xmlPath().get("ACORD.SignonRs.SessionId");
		response.then().statusCode(200);
		String BWStatus = response.xmlPath().get("**.findAll{it.CarrierName=='Bristol West'}.MsgStatus.MsgStatusCd");
		String BWMessageStatusDesc = response.xmlPath()
				.get("**.findAll{it.CarrierName=='Bristol West'}.MsgStatus.MsgStatusDesc");

		reportAttributes.put("BWStatus", BWStatus);
		reportAttributes.put("BWStatusMessage", BWMessageStatusDesc);

		 assertThat(BWStatus, containsStringIgnoringCase("Success")); 
		return sessionId;

	}

	protected void submitRC3Request(String todayDate, String sessionId, UAPIResponse res1)
			throws UnsupportedEncodingException {
		UAPIRequest request = new UAPIRequest();
		request.setPageName("BindCallDetails");
		List<Field> fields = buildRequest(res1, testData, request);
		addCoverageStartDate(todayDate, fields);
		addCurrentPolicyExpirationDate(todayDate, fields);
		addFieldValue("QTE_BillPlan", "OnePay_CreditCard_100", fields);
		addFieldValue("QTE_WhoCanPay", "John Terry", fields);
		addFieldValue("QTE_CreditCardNumber", "4012888888881881", fields);
		addFieldValue("QTE_CreditCardZip", "94404", fields);
		addFieldValue("QTE_CreditCardExpirationDate", "022023", fields);

		String encodedSessionID = URLEncoder.encode(sessionId, Charsets.UTF_8.name());
		//@formatter:off
		given()
		.urlEncodingEnabled(false)
		.auth().preemptive()
		.basic(userName, password)
		.contentType(ContentType.JSON)
		.body(request)
		.pathParam("sessionId", encodedSessionID)
		.post(RC3_BASE_URI+"/AutoInsuranceServices/Publisher/GetRC3RateForExternalPublisher/{sessionId}")
		.then()
		.assertThat()
		.statusCode(200);
		//@formatter:on
	}

	protected void addFieldValue(String fieldName, String fieldValue, List<Field> fields) {
		boolean fieldAdded = false;
		for (int i = 0; i < fields.size(); i++) {
			if (fields.get(i).getFieldName().equals(fieldName)) {
				fields.get(i).setFieldValue(fieldValue);
				fieldAdded = true;
			}
		}
		if (!fieldAdded) {
			Field field = new Field();
			field.setFieldName(fieldName);
			field.setFieldValue(fieldValue);
			fields.add(field);
		}
	}

	protected void getRC2Rates(String sessionId) throws UnsupportedEncodingException {
		String encodedSessionID = URLEncoder.encode(sessionId, Charsets.UTF_8.name());
		//@formatter:off
		Response res = 
				given()
				.urlEncodingEnabled(false)
				.auth().preemptive()
				.basic(userName, password)
				.contentType(ContentType.JSON)
				.pathParam("sessionId", encodedSessionID)
				.get("/Publisher/GetRC2RateForExternalPublisher/{sessionId}");
		//@formatter:on
		res.then().statusCode(200);
		actualRC2Response = res.xmlPath().get("ACORD.InsuranceSvcRs.PersAutoPolicyQuoteInqRs.MsgStatus.MsgStatusCd");

	    assertThat(actualRC2Response, containsStringIgnoringCase("Success")); 
	}

	protected void submitRC2Answers(String todayDate, String sessionId, UAPIResponse res)
			throws UnsupportedEncodingException {
		UAPIRequest request = new UAPIRequest();
		request.setPageName("RateCall2Details");
		List<Field> fields = buildRequest(res, testData, request);
		addCoverageStartDate(todayDate, fields);
		addCurrentPolicyExpirationDate(todayDate, fields);
		/*
		 * addFieldValue("QTE_SafecoNonAutoPolicies", "HO", fields);
		 * addFieldValue("QTE_CAR_RightTrackMobileNo", "2257566981", fields);
		 * addFieldValue("QTE_VEH_0_RideSharingCoverage", "Y", fields);
		 * addFieldValue("QTE_CAR_DelayMVR", "N", fields);
		 */

		String encodedSessionID = URLEncoder.encode(sessionId, Charsets.UTF_8.name());
		//@formatter:off
		given()
		.urlEncodingEnabled(false)
		.auth().preemptive()
		.basic(userName, password)
		.contentType(ContentType.JSON)
		.body(request)
		.pathParam("sessionId", encodedSessionID)
		.patch("/Page/SubmitPage/{sessionId}")
		.then()
		.assertThat()
		.statusCode(200);
		//@formatter:on
		
		
	}

	protected String getCurrentDate() {
		DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		return formatter.format(new java.util.Date());
	}

	protected String getCarrierRequestXmlFromDatabase(String sessionId, String transportid) {
		return database.getRequestXML(sessionId,transportid);

	}
	protected String getCurrentDateForRC2Request() {
		DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		return formatter.format(new java.util.Date());
	}

}

