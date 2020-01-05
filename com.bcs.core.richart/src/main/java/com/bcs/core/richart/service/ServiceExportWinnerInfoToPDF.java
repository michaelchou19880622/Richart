package com.bcs.core.richart.service;

import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ServiceExportWinnerInfoToPDF {

	/** Logger **/
	private static Logger logger = LoggerFactory.getLogger(ServiceExportWinnerInfoToPDF.class);

	private static final String SOURCE_FILE = "C:\\HpiCorp\\06_TestResources\\WinningLetterReplyTemplete\\WinningLetterReplyTempleteSourceNew.docx";
	private static final String OUTPUT_FILE = "C:\\HpiCorp\\06_TestResources\\WinningLetterReplyTemplete\\WinningLetterReplyTemplete_Test.docx";

	/** Export winner info to PDF **/
	public void exportWinnerInfoToPDF(String exportPath, String fileName, String winningLetterRecordId) throws Exception {
		try {
			XWPFDocument doc = new XWPFDocument(OPCPackage.open(SOURCE_FILE));
			
			Iterator<IBodyElement> iter = doc.getBodyElementsIterator();
			while (iter.hasNext()) {
				IBodyElement elem = iter.next();
				logger.info("elem.getElementType() = {}", elem.getElementType());
				
				if (elem instanceof XWPFParagraph) {
					logger.info("elem instanceof XWPFParagraph");
					List<IBodyElement> lst_ParagraphBody = elem.getBody().getBodyElements();
					logger.info("lst_ParagraphBody.size() = {}", lst_ParagraphBody.size());
					
					for (IBodyElement iParagraphBodyElement : lst_ParagraphBody) {
						logger.info("iBodyElement.getElementType() = {}", iParagraphBodyElement.getElementType());
					}
				} else if (elem instanceof XWPFTable) {
					logger.info("elem instanceof XWPFTable");
					List<IBodyElement> lst_TableBody = elem.getBody().getBodyElements();
					logger.info("lst_TableBody.size() = {}", lst_TableBody.size());
					
					for (IBodyElement iTableBodyElement : lst_TableBody) {
						logger.info("iBodyElement.getElementType() = {}", iTableBodyElement.getElementType());
						
//						iTableBodyElement.getBody().getBodyElements()
					}
					
				} else if (elem instanceof XWPFTableRow) {
					logger.info("elem instanceof XWPFTableRow");
				}  else if (elem instanceof XWPFTableCell) {
					logger.info("elem instanceof XWPFTableCell");
				} else if (elem instanceof XWPFRun) {
					logger.info("elem instanceof XWPFRun");
				}
			}

//			logger.info("1-1 doc.getTables().size() = {}", doc.getTables().size());
//			logger.info("1-1 doc.getParagraphs().size() = {}", doc.getParagraphs().size());
//			logger.info("1-1 doc.getBodyElements().size() = {}", doc.getBodyElements().size());
			
			
//			for (XWPFParagraph p : doc.getParagraphs()) {
//				List<XWPFRun> runs = p.getRuns();
//				if (runs != null) {
//					for (XWPFRun r : runs) {
//						String text = r.getText(0);
//						logger.info("1-1 text = {}", text);
//						if (text != null && text.contains("${WinningLetterName}")) {
//							text = text.replace("${WinningLetterName}", "測試中獎回函123456");
//							r.setText(text, 0);
//						}
//						else if (text != null && text.contains("${EndTime}")) {
//							text = text.replace("${EndTime}", "2020/01/05");
//							r.setText(text, 0);
//						}
//					}
//				}
//			}
			
			
			
			
			for (XWPFTable tbl : doc.getTables()) {
				logger.info("2-1 doc.getTables().size() = {}", doc.getTables().size());
				for (XWPFTableRow row : tbl.getRows()) {
					logger.info("2-1 row.getTableCells().size() = {}", row.getTableCells().size());
					for (XWPFTableCell cell : row.getTableCells()) {
						for (XWPFParagraph p : cell.getParagraphs()) {
							for (XWPFRun r : p.getRuns()) {
								String text = r.getText(0);
								logger.info("2-1 text = {}", text);
								if (text != null && text.contains("${WinningLetterName}")) {
									text = text.replace("${WinningLetterName}", "測試中獎回函123456");
									r.setText(text, 0);
								}
								else if (text != null && text.contains("${EndTime}")) {
									text = text.replace("${EndTime}", "2020/01/05");
									r.setText(text, 0);
								}
							}
						}
					}
				}
			}
			
			doc.write(new FileOutputStream(OUTPUT_FILE));
			doc.close();

//			Map<String, String> map = new HashMap<>();
//	        map.put("WinningLetterName", "Richart好禮自由配機會中獎回覆函");
//	        map.put("EndTime", "2020/01/31");
//	        map.put("WinnerName", "王小明");
//	        map.put("WinnerIdCardNumber", "S123456789");
//			
//			XWPFDocument document = new XWPFDocument(OPCPackage.open(SOURCE_FILE));
//			
//			// 替换的文本对象
//			changeText(document, map);
//			
//			// 替换的表格对象
//			changeTable(document, map);
//
//			File file = new File(OUTPUT_FILE);
//			FileOutputStream stream = new FileOutputStream(file);
//			document.write(stream);
//			stream.close();

//			/**
//			 * if uploaded doc then use HWPF else if uploaded Docx file use XWPFDocument
//			 */
//			XWPFDocument doc = new XWPFDocument(OPCPackage.open(SOURCE_FILE));
//
//			logger.info("doc.getTables().size() = {}", doc.getTables().size());
//
//			Iterator<XWPFTable> it = doc.getTablesIterator();
//
//			while (it.hasNext()) {
//				XWPFTable xwpfTable = it.next();
//				logger.info("xwpfTable.getText() = {}", xwpfTable.getText());
//
//				List<XWPFTableRow> listRows = xwpfTable.getRows();
//				logger.info("listRows.size() = {}", listRows.size());
//
//				for (XWPFTableRow row : listRows) {
//					List<XWPFTableCell> cells = row.getTableCells();
//					logger.info("cells.size() = {}", cells.size());
//
//					for (XWPFTableCell cell : cells) {
//						List<XWPFParagraph> paragraphListTable = cell.getParagraphs();
//						logger.info("paragraphListTable.size() = {}", paragraphListTable.size());
//					}
//				}
//			}
//
//			for (XWPFTable tbl : doc.getTables()) {
//
//				logger.info("tbl.getRows().size() = {}", tbl.getRows().size());
//				for (XWPFTableRow row : tbl.getRows()) {
//
//					logger.info("row.getTableCells().size() = {}", row.getTableCells().size());
//					for (XWPFTableCell cell : row.getTableCells()) {
//						logger.info("cell.getText() = {}", cell.getText());
//
//						for (XWPFParagraph p : cell.getParagraphs()) {
//							logger.info("p.getText() = {}", p.getText());
//
//							for (XWPFRun r : p.getRuns()) {
//								logger.info("r.text() = {}", r.text());
//
//								String text = r.getText(0);
//								logger.info("text = {}", text);
////								if (text != null && text.contains("$$key$$")) {
////									text = text.replace("$$key$$", "abcd");
////									r.setText(text, 0);
////								}
//							}
//						}
//					}
//				}
//			}
//
//			doc.write(new FileOutputStream(OUTPUT_FILE));
//			doc.close();

		} catch (Exception e) {
			logger.error("Exception : {}", e);
		}
	}

	/**
	 * 替换段落文本
	 * 
	 * @param document docx解析对象
	 * @param textMap  需要替换的信息集合
	 */
	public static void changeText(XWPFDocument document, Map<String, String> textMap) {
		List<XWPFParagraph> paragraphs = document.getParagraphs();
		logger.info("paragraphs.size() = {}", paragraphs.size());

		for (XWPFParagraph paragraph : paragraphs) {
			String text = paragraph.getText();
			logger.info("text = {}", text);
			if (checkText(text)) {
				List<XWPFRun> runs = paragraph.getRuns();
				logger.info("runs = {}", runs);
				for (XWPFRun run : runs) {
					run.setText(changeValue(run.toString(), textMap), 0);
				}
			}
		}
	}

	/**
	 * 匹配传入信息集合与模板
	 * 
	 * @param value   模板需要替换的区域
	 * @param textMap 传入信息集合
	 * @return 模板需要替换区域信息集合对应值
	 */
	private static String changeValue(String value, Map<String, String> textMap) {
		Set<Map.Entry<String, String>> textSets = textMap.entrySet();
		for (Map.Entry<String, String> textSet : textSets) {
			// 匹配模板与替换值 格式${key}
			String key = "${" + textSet.getKey() + "}";
			if (value.indexOf(key) != -1) {
				value = textSet.getValue();
			}
		}
		// 模板未匹配到区域替换为空
		if (checkText(value)) {
			value = "-----";
		}
		return value;
	}

	/**
	 * 检索替换位置"$"
	 * 
	 * @param text
	 * @return
	 */
	private static boolean checkText(String text) {
		boolean check = false;
		if (text.indexOf("$") != -1) {
			check = true;
		}
		return check;
	}

	/**
	 * 替换表格对象方法
	 * 
	 * @param document  docx解析对象
	 * @param textMap   需要替换的信息集合
	 * @param tableList 需要插入的表格信息集合
	 */
	public static void changeTable(XWPFDocument document, Map<String, String> textMap) {
		List<XWPFTable> tables = document.getTables();
		for (int i = 0; i < tables.size(); i++) {
			// 只处理行数大于等于2的表格，且不循环表头
			XWPFTable table = tables.get(i);
			if (table.getRows().size() > 1) {
				// 判断表格是需要替换还是需要插入，判断逻辑有$为替换，表格无$为插入
				if (checkText(table.getText())) {
					List<XWPFTableRow> rows = table.getRows();
					eachTable(rows, textMap);
				}
			}
		}
	}

	/**
	 * 遍历表格
	 * 
	 * @param rows    表格行对象
	 * @param textMap 需要替换的信息集合
	 */
	private static void eachTable(List<XWPFTableRow> rows, Map<String, String> textMap) {
		for (XWPFTableRow row : rows) {
			List<XWPFTableCell> cells = row.getTableCells();
			for (XWPFTableCell cell : cells) {
				// 判断单元格里是否有需要替换的内容
				if (checkText(cell.getText())) {
					List<XWPFParagraph> paragraphs = cell.getParagraphs();
					for (XWPFParagraph paragraph : paragraphs) {
						List<XWPFRun> runs = paragraph.getRuns();
						for (XWPFRun run : runs) {
							run.setText(changeValue(run.toString(), textMap), 0);
						}
					}
				}
			}
		}
	}

//	private HWPFDocument replaceText(HWPFDocument doc, String findText, String replaceText) {
//		Range r = doc.getRange();
//		for (int i = 0; i < r.numSections(); ++i) {
//			Section s = r.getSection(i);
//			for (int j = 0; j < s.numParagraphs(); j++) {
//				Paragraph p = s.getParagraph(j);
//				for (int k = 0; k < p.numCharacterRuns(); k++) {
//					CharacterRun run = p.getCharacterRun(k);
//					String text = run.text();
//					if (text.contains(findText)) {
//						run.replaceText(findText, replaceText);
//					}
//				}
//			}
//		}
//		return doc;
//	}
//
//	private HWPFDocument openDocument(String file) throws Exception {
//		URL res = getClass().getClassLoader().getResource(file);
//		HWPFDocument document = null;
//		if (res != null) {
//			document = new HWPFDocument(new POIFSFileSystem(new File(res.getPath())));
//		}
//		return document;
//	}
//
//	private void saveDocument(HWPFDocument doc, String file) {
//		try (FileOutputStream out = new FileOutputStream(file)) {
//			doc.write(out);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
}
