package com.bcs.core.richart.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlToken;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPositiveSize2D;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTInline;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDrawing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bcs.core.db.entity.WinningLetter;
import com.bcs.core.db.entity.WinningLetterRecord;
import com.bcs.core.db.repository.WinningLetterRecordRepository;
import com.bcs.core.db.repository.WinningLetterRepository;
import com.bcs.core.resource.CoreConfigReader;

@Service
public class ServiceExportWinnerInfoToPDF {

	/** Logger **/
	private static Logger logger = LoggerFactory.getLogger(ServiceExportWinnerInfoToPDF.class);

	@Autowired
	private WinningLetterRepository winningLetterRepository;
	
	@Autowired
	private WinningLetterRecordRepository winningLetterRecordRepository;

	/** Export winner info to PDF **/
	public String exportWinnerInfoToPDF(String exportPath, String winningLetterRecordId) throws Exception {

		synchronized (this) {
			logger.info("exportPath = {}", exportPath);
			logger.info("winningLetterRecordId = {}", winningLetterRecordId);
	
			// check record in database
			WinningLetterRecord winningLetterRecord = winningLetterRecordRepository.findById(Long.valueOf(winningLetterRecordId));
			logger.info("winningLetterRecord = {}", winningLetterRecord.toString());
			
			WinningLetter winningLetter = winningLetterRepository.findById(Long.valueOf(winningLetterRecord.getWinningLetterId()));
			logger.info("winningLetter = {}", winningLetter.toString());
			
			String outputFilePDF = String.format("%s\\%s_%s.pdf", exportPath, winningLetter.getName(), winningLetterRecord.getName());
			logger.info("outputFilePDF = {}", outputFilePDF);
	
			String outputTemplete = String.format("%s\\%s_%s.docx", exportPath, winningLetter.getName(), winningLetterRecord.getName());
			logger.info("outputTemplete = {}", outputTemplete);
	
			SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd");
			
			Map<String, String> mapReplacedText = new HashMap<>();
			mapReplacedText.put("${WinningLetterName}", winningLetter.getName());
			mapReplacedText.put("${EndTime}", sdFormat.format(winningLetter.getEndTime()));
			mapReplacedText.put("${WinnerName}", winningLetterRecord.getName());
			mapReplacedText.put("${WinnerIdCardNum}", winningLetterRecord.getIdCardNumber());
			mapReplacedText.put("${WinningLetterGifts}", winningLetter.getGift());
			mapReplacedText.put("${WinnerPhoneNumber}", winningLetterRecord.getPhoneNumber());
			mapReplacedText.put("${WinnerResidentAddress}", winningLetterRecord.getResidentAddress());
			mapReplacedText.put("${WinnerMailingAddress}", winningLetterRecord.getMailingAddress());
	
			String defaultSourcePath = CoreConfigReader.getString("file.path") + System.getProperty("file.separator") + "Default";
			logger.info("defaultSourcePath = {}", defaultSourcePath);
			
			String pdfTemplete = defaultSourcePath + System.getProperty("file.separator") + "WinningLetterReplyTempleteSource.docx";
			logger.info("pdfTemplete = {}", pdfTemplete);
			
			String imageSourcePath = CoreConfigReader.getString("file.path") + System.getProperty("file.separator") + "IMAGE";
			logger.info("imageSourcePath = {}", imageSourcePath);
			
			String idCardCopyFront = imageSourcePath + System.getProperty("file.separator") + winningLetterRecord.getIdCardCopyFront();
			logger.info("idCardCopyFront = {}", idCardCopyFront);
			
			String idCardCopyBack = imageSourcePath + System.getProperty("file.separator") + winningLetterRecord.getIdCardCopyBack();
			logger.info("idCardCopyBack = {}", idCardCopyBack);
			
			String eSignature = imageSourcePath + System.getProperty("file.separator") + winningLetterRecord.geteSignature();
			logger.info("eSignature = {}", eSignature);
	
			try {
				CustomXWPFDocument doc = new CustomXWPFDocument(new FileInputStream(pdfTemplete));
	
				List<XWPFTable> list_Tables = doc.getTables();
				for (int tableIndex = 0; tableIndex < list_Tables.size(); tableIndex++) {
					XWPFTable table = list_Tables.get(tableIndex);
	
					List<XWPFTableRow> list_TableRows = table.getRows();
					for (int tableRowIndex = 0; tableRowIndex < list_TableRows.size(); tableRowIndex++) {
						XWPFTableRow tableRow = list_TableRows.get(tableRowIndex);
	
						List<XWPFTableCell> list_TableCells = tableRow.getTableCells();
						for (int tableCellIndex = 0; tableCellIndex < list_TableCells.size(); tableCellIndex++) {
							XWPFTableCell tableCell = list_TableCells.get(tableCellIndex);
	
							List<XWPFParagraph> list_Paragraphs = tableCell.getParagraphs();
							for (int paragraphIndex = 0; paragraphIndex < list_Paragraphs.size(); paragraphIndex++) {
								XWPFParagraph paragraph = list_Paragraphs.get(paragraphIndex);
	
								List<XWPFRun> list_Runs = paragraph.getRuns();
								for (int runIndex = 0; runIndex < list_Runs.size(); runIndex++) {
									XWPFRun run = list_Runs.get(runIndex);
	
									List<CTDrawing> drawings = run.getCTR().getDrawingList();
									if (drawings.size() > 0) {
										CTDrawing drawing = drawings.get(0);
										
										List<CTInline> list_Inlines = drawing.getInlineList();
										if (list_Inlines.size() > 0) {
											CTInline ctInline = list_Inlines.get(0);
											CTPositiveSize2D ps2d = ctInline.getExtent();
	
											String blipId = null;
	
											long docPrId = ctInline.getDocPr().getId();
											logger.info("1-1 docPrId = {}", docPrId);
	
											switch (String.valueOf(docPrId)) {
											default:
												break;
											case "2":
												blipId = addPictureData(idCardCopyFront, doc);
	
												doc.createPicture(run.getCTR(), blipId, doc.getNextPicNameNumber(XWPFDocument.PICTURE_TYPE_PNG), ps2d.getCx(), ps2d.getCy());
												run.getCTR().removeDrawing(0);
												break;
											case "3":
												blipId = addPictureData(idCardCopyBack, doc);
	
												doc.createPicture(run.getCTR(), blipId, doc.getNextPicNameNumber(XWPFDocument.PICTURE_TYPE_PNG), ps2d.getCx(), ps2d.getCy());
												run.getCTR().removeDrawing(0);
												break;
											case "4":
												blipId = addPictureData(eSignature, doc);
	
												doc.createPicture(run.getCTR(), blipId, doc.getNextPicNameNumber(XWPFDocument.PICTURE_TYPE_PNG), ps2d.getCx(), ps2d.getCy());
												run.getCTR().removeDrawing(0);
												break;
											}

											logger.info("1-2 blipId = {}", blipId);
										}
										
										break;
									}
	
									String runText = run.getText(0);
	
									if (StringUtils.isBlank(runText)) {
										continue;
									}
	
									if (!runText.startsWith("${") || !runText.endsWith("}")) {
										continue;
									}
	
//									logger.info(String.format("table[%d].tableRow[%d].tableCell[%d].paragraph[%d].run[%d] = %s", tableIndex, tableRowIndex, tableCellIndex, paragraphIndex, runIndex,
//											runText));
	
									Set<Map.Entry<String, String>> textSets = mapReplacedText.entrySet();
									for (Map.Entry<String, String> textSet : textSets) {
										String key = textSet.getKey();
										if (runText.indexOf(key) != -1) {
											run.setText(textSet.getValue(), 0);
										}
									}
								}
							}
	
							if (tableCell.getTables().size() > 0) {
	
								List<XWPFTable> list_TableCell_Tables = tableCell.getTables();
								for (int tcTableIndex = 0; tcTableIndex < list_TableCell_Tables.size(); tcTableIndex++) {
									XWPFTable tcTable = list_TableCell_Tables.get(tcTableIndex);
	
									List<XWPFTableRow> list_TableCell_TableRows = tcTable.getRows();
									for (int tcTableRowIndex = 0; tcTableRowIndex < list_TableCell_TableRows.size(); tcTableRowIndex++) {
										XWPFTableRow tcTableRows = list_TableCell_TableRows.get(tcTableRowIndex);
	
										List<XWPFTableCell> list_TableCell_TableCells = tcTableRows.getTableCells();
										for (int tcTableCellIndex = 0; tcTableCellIndex < list_TableCell_TableCells.size(); tcTableCellIndex++) {
											XWPFTableCell tcTableCell = list_TableCell_TableCells.get(tcTableCellIndex);
	
											List<XWPFParagraph> list_TableCell_Paragraphs = tcTableCell.getParagraphs();
											for (int tcParagraphIndex = 0; tcParagraphIndex < list_TableCell_Paragraphs.size(); tcParagraphIndex++) {
												XWPFParagraph tcParagraph = list_TableCell_Paragraphs.get(tcParagraphIndex);
	
												List<XWPFRun> list_TableCell_Runs = tcParagraph.getRuns();
												for (int tcRunIndex = 0; tcRunIndex < list_TableCell_Runs.size(); tcRunIndex++) {
													XWPFRun tcRun = list_TableCell_Runs.get(tcRunIndex);
	
													List<CTDrawing> tcDrawings = tcRun.getCTR().getDrawingList();
													if (tcDrawings.size() > 0) {
														CTDrawing tcDrawing = tcDrawings.get(0);
														
														List<CTInline> list_tcInlines = tcDrawing.getInlineList();
														if (list_tcInlines.size() > 0) {
															CTInline tcCtInline = list_tcInlines.get(0);
															CTPositiveSize2D tcPs2d = tcCtInline.getExtent();
	
															String blipId = null;
	
															long docPrId = tcCtInline.getDocPr().getId();
															logger.info("2-1 docPrId = {}", docPrId);
	
															switch (String.valueOf(docPrId)) {
															default:
																break;
															case "2":
																blipId = addPictureData(idCardCopyFront, doc);
	
																doc.createPicture(tcRun.getCTR(), blipId, doc.getNextPicNameNumber(XWPFDocument.PICTURE_TYPE_PNG), tcPs2d.getCx(), tcPs2d.getCy());
																tcRun.getCTR().removeDrawing(0);
																break;
															case "3":
																blipId = addPictureData(idCardCopyBack, doc);
	
																doc.createPicture(tcRun.getCTR(), blipId, doc.getNextPicNameNumber(XWPFDocument.PICTURE_TYPE_PNG), tcPs2d.getCx(), tcPs2d.getCy());
																tcRun.getCTR().removeDrawing(0);
																break;
															case "4":
																blipId = addPictureData(eSignature, doc);
	
																doc.createPicture(tcRun.getCTR(), blipId, doc.getNextPicNameNumber(XWPFDocument.PICTURE_TYPE_PNG), tcPs2d.getCx(), tcPs2d.getCy());
																tcRun.getCTR().removeDrawing(0);
																break;
															}

															logger.info("2-2 blipId = {}", blipId);
															
															break;
														}
													}
	
													String tcRunText = tcRun.getText(0);
	
													if (StringUtils.isBlank(tcRunText)) {
														continue;
													}
	
													if (!tcRunText.startsWith("${") || !tcRunText.endsWith("}")) {
														continue;
													}
	
//													logger.info(String.format("table[%d].tableRow[%d].tableCell[%d].paragraph[%d].run[%d] = %s", tcTableIndex, tcTableRowIndex, tcTableCellIndex,
//															tcParagraphIndex, tcRunIndex, tcRunText));
	
													Set<Map.Entry<String, String>> textSets = mapReplacedText.entrySet();
													for (Map.Entry<String, String> textSet : textSets) {
														String key = textSet.getKey();
														if (tcRunText.indexOf(key) != -1) {
															tcRun.setText(textSet.getValue(), 0);
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
	
				doc.write(new FileOutputStream(outputTemplete));
				doc.close();
	
				wordConverterToPdf(new FileInputStream(outputTemplete), new FileOutputStream(outputFilePDF));
				
				File srcOutputFile = new File(outputTemplete);
				if (srcOutputFile.exists()) {
					srcOutputFile.delete();
				}
	
			} catch (Exception e) {
				logger.error("Exception : {}", e);
			}
			
			logger.info("outputFilePDF = " + FileUtils.getFile(outputFilePDF).getName());
			
			return FileUtils.getFile(outputFilePDF).getName();
		}
	}

	/**
	 * File(.docx) convert to file(.pdf)
	 * 
	 * @param source .docx file
	 * @param target .pdf file
	 * @throws Exception
	 */
	public static void wordConverterToPdf(InputStream source, OutputStream target) throws Exception {
		XWPFDocument doc = new XWPFDocument(source);
		PdfOptions options = null;
		PdfConverter.getInstance().convert(doc, target, options);
	}

	private static String addPictureData(String image, CustomXWPFDocument doc) throws InvalidFormatException {
		
		InputStream images = null;
		
		try {
			
			if (!FileUtils.getFile(image).exists()) {
				image = CoreConfigReader.getString("file.default.image.path");
			} 
			logger.info("image = ", image);
				
			images = new FileInputStream(image);
			
			return doc.addPictureData(images, XWPFDocument.PICTURE_TYPE_PNG);
		} catch (FileNotFoundException e) {
			logger.info("FileNotFoundException = ", e);

			return null;
			
		}finally {
		}
	}

	public class CustomXWPFDocument extends XWPFDocument {
		public CustomXWPFDocument() {
			super();
		}

		public CustomXWPFDocument(OPCPackage opcPackage) throws IOException {
			super(opcPackage);
		}

		public CustomXWPFDocument(InputStream in) throws IOException {
			super(in);
		}

		public void createPicture(CTR ctr, String blipId, int id, long width, long height) {
			CTInline inline = ctr.addNewDrawing().addNewInline();

			String picXml = "" +
	                "<a:graphic xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\">" +
	                "   <a:graphicData uri=\"http://schemas.openxmlformats.org/drawingml/2006/picture\">" +
	                "      <pic:pic xmlns:pic=\"http://schemas.openxmlformats.org/drawingml/2006/picture\">" +
	                "         <pic:nvPicPr>" +
	                "            <pic:cNvPr id=\"" + id + "\" name=\"Generated\"/>" +
	                "            <pic:cNvPicPr/>" +
	                "         </pic:nvPicPr>" +
	                "         <pic:blipFill>" +
	                "            <a:blip r:embed=\"" + blipId + "\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\"/>" +
	                "            <a:stretch>" +
	                "               <a:fillRect/>" +
	                "            </a:stretch>" +
	                "         </pic:blipFill>" +
	                "         <pic:spPr>" +
	                "            <a:xfrm>" +
	                "               <a:off x=\"0\" y=\"0\"/>" +
	                "               <a:ext cx=\"" + width + "\" cy=\"" + height + "\"/>" +
	                "            </a:xfrm>" +
	                "            <a:prstGeom prst=\"rect\">" +
	                "               <a:avLst/>" +
	                "            </a:prstGeom>" +
	                "         </pic:spPr>" +
	                "      </pic:pic>" +
	                "   </a:graphicData>" +
	                "</a:graphic>";
	 
	        XmlToken xmlToken = null;
	        
			try {
				xmlToken = XmlToken.Factory.parse(picXml);
			} catch (XmlException xe) {
				xe.printStackTrace();
			}
			inline.set(xmlToken);

			inline.setDistT(0);
			inline.setDistB(0);
			inline.setDistL(0);
			inline.setDistR(0);

			CTPositiveSize2D extent = inline.addNewExtent();
			extent.setCx(width);
			extent.setCy(height);

			CTNonVisualDrawingProps docPr = inline.addNewDocPr();
			docPr.setId(id);
			docPr.setName("Picture " + id);
			docPr.setDescr("Generated");
		}
		 
	    public void createPicture(String blipId, int id, int width, int height) {
	        createPicture(createParagraph().createRun().getCTR(), blipId, id, width, height);
	    }
	}
}
