package ExcelResults;

import java.util.Map;

public interface ReportWriter {
	void write(Map<String, String> attributes, String sheetName) throws Exception;

}


