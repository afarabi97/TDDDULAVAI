package ExcelResults;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReportWriter implements ReportWriter {
	public static final String SESSION_ID = "SessionId";
	public static final String ACORD_VALUE = "ACORD_VALUE";
	public static final String EXPECTED_VALUE = "Expected";
	public static final String ACTUAL_VALUE = "Actual";
	public static final String PAYMENT_PLAN = "Payment_Plan";
	public static final String PAYMENT_OPTION = "Payment_Option";
	public static final String DOWN_PAYMENT_METHOD = "Down_Payment_Method";
	public static final String INSURER_NAME = "Insurer_Name";
	public static final String EXPECTED_INDUSTRY_VALUE = "Expected_Inudstry_Value";
	public static final String EXPECTED_OCCUPATION_VALUE = "Expected_Occupation_Value";
	public static final String ACTUAL_INDUSTRY_VALUE = "Actual_Inudstry_Value";
	public static final String ACTUAL_OCCUPATION_VALUE = "Actual_Occupation_Value";
	public static final String BIND_STATUS = "Bind_Status";
	public static final String ACTUAL_VALUE_PER_PERSON="Actual_Bodily_Injury_Per_Person";
	public static final String ACTUAL_VALUE_PER_ACC="Actual_Bodily_Injury_Per_Acc";
	public static final String ACTUAL_VALUE_BI_COVERAGE="Bodily_Injury_Coverage";
	public static final String ACTUAL_VALUE_PD_PER_PERSON="Actual_Property_Damage";
	public static final String ACTUAL_VALUE_DEDUCTIBLE="ACTUAL_VALUE_DEDUCTIBLE";
	public static final String ACTUAL_VALUE_OPT1="ACTUAL_VALUE_OPT1";
	public static final String ACTUAL_VALUE_OPT2="ACTUAL_VALUE_OPT2";
	public static final String EXPECTED_RESIDENCE_OWNED_VALUE="EXPECTED_RESIDENCE_OWNED_VALUE";
	public static final String ACTUAL_RESIDENCE_OWNED_VALUE="ACTUAL_RESIDENCE_OWNED_VALUE";
	public static final String EXPECTED_RESIDENCE_TYPE_VALUE="EXPECTED_RESIDENCE_TYPE_VALUE";
	public static final String ACTUAL_RESIDENCE_TYPE_VALUE="ACTUAL_RESIDENCE_TYPE_VALUE";
	public static final String UM_ACTUAL_VALUE_PER_PERSON="Actual_UM_Per_Person";
	public static final String UM_ACTUAL_VALUE_PER_ACC="Actual_UM_Per_Acc";
	public static final String UIM_ACTUAL_VALUE_PER_PERSON="Actual_UIM_Per_Person";
	public static final String UIM_ACTUAL_VALUE_PER_ACC="Actual_UIM_Per_Acc";	
	public static final String UMPD_ACTUAL_VALUE="Actual_UMPD";
	public static final String ACTUAL_NON_STACKED_VALUE ="Actual_Non_Stacked_Value";
	public static final String ACTUAL_STACKED_VALUE ="Actual_Stacked_Value";	
	public static final String ACTUAL_DATE = "Actual_Date";


	

	protected Workbook workbook;

	public ExcelReportWriter() {
		try {
			FileInputStream file = new FileInputStream(new File(getFileLocation()));
			this.workbook = new XSSFWorkbook(file);
		} catch (Exception e) {
			this.workbook = new XSSFWorkbook();
		}
	}

	@Override
	public void write(Map<String, String> attributes, String sheetName) throws Exception {
		Sheet sheet = workbook.getSheet(sheetName);
		if (sheet == null) {
			sheet = workbook.createSheet(sheetName);
		}
		if (sheet.getRow(0) == null || sheet.getRow(0).getCell(0) == null) {
			Row header = sheet.createRow(0);
			CellStyle headerStyle = getHeaderStyle();
			int count = 0;
			for (String columnName : attributes.keySet()) {
				Cell headerCell = header.createCell(count);
				headerCell.setCellValue(columnName);
				headerCell.setCellStyle(headerStyle);
				count++;
			}
		}
		Row dataRow = sheet.createRow(sheet.getLastRowNum() + 1);
		int count = 0;
		for (String columnName : attributes.keySet()) {
			Cell headerCell = dataRow.createCell(count);
			headerCell.setCellValue(attributes.get(columnName));
			count++;
		}

	}
	
	private CellStyle getHeaderStyle() {
		CellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		XSSFFont font = ((XSSFWorkbook) workbook).createFont();
		font.setFontName("Calibri");
		font.setFontHeightInPoints((short) 12);
		font.setBold(true);
		headerStyle.setFont(font);

		return headerStyle;
	}

	public String getFileLocation() {
		File currDir = new File(".");
		String path = currDir.getAbsolutePath();
		String fileLocation = path.substring(0, path.length() - 1) + "temp.xlsx";
		return fileLocation;
	}

	public void flush() throws Exception {
		String fileLocation = getFileLocation();

		FileOutputStream outputStream = new FileOutputStream(fileLocation);
		workbook.write(outputStream);
		workbook.close();
		System.out.println(fileLocation);
	}

	public static void main(String args[]) throws Exception {
		ExcelReportWriter writer = new ExcelReportWriter();
		Map<String, String> attributes = new LinkedHashMap<String, String>();
		attributes.put(SESSION_ID, "test session id");
		attributes.put(EXPECTED_VALUE, "test expected id");
		attributes.put(ACTUAL_VALUE, "test actual id");
		writer.write(attributes, "coverage");
		writer.flush();
	}

}


