package com.bcs.core.report.export;

import java.io.FileOutputStream;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.springframework.stereotype.Service;

import com.bcs.core.utils.ErrorRecord;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ExportToExcelForMid {
	
	/** Logger */
	private static Logger logger = LogManager.getLogger(ExportToExcelForMid.class);
	
	private SXSSFSheet[] sheetArray;
	
	private static int maxRowNumber = 1000000;
	private static int maxSheetNumber = 25;
	/**
	 * 匯出EXCEL
	 */
	public void exportToExcel(String exportPath, String fileName, String excelName, String title, String time, List<List<String>> data) throws Exception {
		this.exportToExcel(exportPath, fileName, excelName, title, time, null, data);
	}
	
	public void exportToExcel(String exportPath, String fileName, String excelName, String title, String time, List<String> titles, List<List<String>> data) throws Exception {
		try {
//			Workbook wb = new XSSFWorkbook(); //→xls // new XSSFWorkbook()→xlsx
			
			SXSSFWorkbook workbook = new SXSSFWorkbook();
			
			//先支援到最多2500W筆資料, 但實際記憶體需要更大才能支援.
			sheetArray = new SXSSFSheet[maxSheetNumber];
			/**
			 * exportMidToExcel
			 */
			this.exportMidToExcel(workbook, excelName, title, time, titles, data);
						
			// Save
			FileOutputStream out = new FileOutputStream(exportPath + System.getProperty("file.separator") + fileName);
			workbook.write(out);
			out.close();
			workbook.dispose();
			workbook.close();
		} catch (Exception e) {
    		logger.error(ErrorRecord.recordError(e));
		}
	}
	
	/**
	 * Export MID To Excel
	 * @param wb
	 * @param excelName
	 * @param data
	 * @throws Exception
	 */
	public void exportMidToExcel(SXSSFWorkbook workbook, String excelName, String title, String time, List<List<String>> data) throws Exception{
		this.exportMidToExcel(workbook, excelName, title, time, null, data);
	}
	
	public void exportMidToExcel(SXSSFWorkbook workbook, String excelName, String title, String time, List<String> titles, List<List<String>> data) throws Exception{

    	log.info("Received a request to begin exportMidToExcel");
		int index = 0;

		int sheetNumber = 1;
        int rowNumber = 0;
        SXSSFSheet sheet = getExportMidToExcelSheet(workbook, excelName, sheetNumber);		
		if(StringUtils.isNotBlank(title)){
			Row row = sheet.createRow(index); // declare a row object reference
			row.createCell(0).setCellValue(title);
			
			if(StringUtils.isNotBlank(time)){
				row.createCell(1).setCellValue(time);
			}
			index++;
		}
		
		if(titles != null && titles.size() > 0){
			Row row = sheet.createRow(index); // declare a row object reference
			for(int cell = 0; cell < titles.size(); cell++){
				String cellData = titles.get(cell);

				Integer count = 0;
				try{
					count = data.get(cell).size();
					cellData += ":" + count;
				}
				catch(Exception e){}
				
				if(cellData != null){
					row.createCell(cell).setCellValue(cellData.toString());	
				}
				else{
					row.createCell(cell).setCellValue("-");	
				}
			}
			index++;
		}

		for(int rowCount = 0; rowCount < data.size(); rowCount++){
			List<String> cellDatas = data.get(rowCount);
			//Re-initial when new column begin.
			sheetNumber = 1;
			sheet = getExportMidToExcelSheet(workbook, excelName, sheetNumber);
			log.info("ExportMidToExcel begins to deal with New Cell Datas , cellDatas.size():{}" ,  cellDatas.size());	            	        			

			for(int cell = 0; cell < cellDatas.size(); cell++){
	            if ((cell + index) >= maxRowNumber) { // RowLimit = 1048576
	            	sheetNumber = ((cell + index) / maxRowNumber) + 1 ; 
                    sheet = getExportMidToExcelSheet(workbook, excelName, sheetNumber);
                }			
            	rowNumber = (cell + index)%maxRowNumber ;
	            
				Row row = sheet.getRow(rowNumber);
				if(row == null){
					row = sheet.createRow(rowNumber);
				}

				String cellData = cellDatas.get(cell);
				if(StringUtils.isNotBlank(cellData)){
					row.createCell(rowCount).setCellValue(cellData);	
				}
				else{
					row.createCell(rowCount).setCellValue("-");	
				}
			}
		}
    	log.info("Received a request to end exportMidToExcel");				
	}
	
    private SXSSFSheet getExportMidToExcelSheet(SXSSFWorkbook workbook, String excelName, Integer sheetNumber) {
        try {
        	if (sheetNumber >= 1 && sheetNumber <= maxSheetNumber)  {
        		if (sheetArray[sheetNumber-1] == null) {
        			sheetArray[sheetNumber-1] = workbook.createSheet(excelName + sheetNumber);
					// 修正關鍵字無法匯出的問題, 會重複針對Row進行讀寫, 需將windowsSize設為unlimted
        			sheetArray[sheetNumber-1].setRandomAccessWindowSize(-1);
					log.info("ExportMidToExcel get new sheet , sheetNumber:{}" ,  sheetNumber);	            	        			
        		}
				return sheetArray[sheetNumber-1];        		
        	}
        	return null;
        } catch (Exception e) {
            log.error(ErrorRecord.recordError(e));
        }
        return sheetArray[sheetNumber-1];
    }		
}
