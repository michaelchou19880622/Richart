package com.bcs.core.richart.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.springframework.stereotype.Service;

@Service
public class ServiceExportWinnerInfoToPDF {

	/** Logger **/
	private static Logger logger = LoggerFactory.getLogger(ServiceExportWinnerInfoToPDF.class);

	private static final String SOURCE_FILE = "C:\\HpiCorp\\06_TestResources\\WinningLetterReplyTemplete\\docx\\WinningLetterReplyTempleteSource2.docx";
	private static final String OUTPUT_FILE = "C:\\HpiCorp\\06_TestResources\\WinningLetterReplyTemplete\\output\\WinningLetterReplyTemplete_Test2.docx";
	private static final String OUTPUT_PDF_FILE = "C:\\HpiCorp\\06_TestResources\\WinningLetterReplyTemplete\\output\\WinningLetterReplyTemplete_Test2.pdf";
	
	private static final String IMAGE_ID_CARD_FRONT = "C:\\BCS\\FILE\\IMAGE\\90b60bfa-df96-4a76-b009-4d0530b7b139";
	private static final String IMAGE_ID_CARD_BACK = "C:\\BCS\\FILE\\IMAGE\\4b3fcfd5-f2a3-4c83-8909-11c770374ca8";
	private static final String IMAGE_ESIGNATURE = "C:\\BCS\\FILE\\IMAGE\\edc54ab0-bde1-4691-8c34-24cd3807f261";

	/** Export winner info to PDF **/
	public void exportWinnerInfoToPDF(String exportPath, String fileName, String winningLetterRecordId) throws Exception {

		// check record in database

		Map<String, String> mapReplacedText = new HashMap<>();
		mapReplacedText.put("${WinningLetterName}", "Richart好禮自由配機會中獎回覆函");
		mapReplacedText.put("${EndTime}", "2020/01/31 ");
		mapReplacedText.put("${WinnerName}", "王小明");
		mapReplacedText.put("${WinnerIdCardNum}", "S123456789");
		mapReplacedText.put("${WinningLetterGifts}", "LINE Points 3,000點 (價值NT$3,465)");
		mapReplacedText.put("${WinnerPhoneNumber}", "0987654321");
		mapReplacedText.put("${WinnerResidentAddress}", "台北市內湖區堤頂大道二段999號");
		mapReplacedText.put("${WinnerMailingAddress}", "台北市內湖區堤頂大道二段123號");
		mapReplacedText.put("${idCardFront}", "@@@");
		mapReplacedText.put("${idCardBack}", "@@@");
		mapReplacedText.put("${eSignature}", "@@@");

		try {
			CustomXWPFDocument doc = new CustomXWPFDocument(new FileInputStream(SOURCE_FILE));

			List<XWPFTable> list_Tables = doc.getTables();
			for (int tableIndex = 0; tableIndex < list_Tables.size(); tableIndex++) {
				XWPFTable table = list_Tables.get(tableIndex);

				List<XWPFTableRow> list_TableRows = table.getRows();
				for (int tableRowIndex = 0; tableRowIndex < list_TableRows.size(); tableRowIndex++) {
					XWPFTableRow tableRow = list_TableRows.get(tableRowIndex);

					List<XWPFTableCell> list_TableCells = tableRow.getTableCells();
					for (int tableCellIndex = 0; tableCellIndex < list_TableCells.size(); tableCellIndex++) {
						XWPFTableCell tableCell = list_TableCells.get(tableCellIndex);

						if (tableCell.getParagraphs().size() > 0) {

							List<XWPFParagraph> list_TableCell_Paragraphs = tableCell.getParagraphs();
							for (int paragraphIndex = 0; paragraphIndex < list_TableCell_Paragraphs.size(); paragraphIndex++) {
								XWPFParagraph paragraph = list_TableCell_Paragraphs.get(paragraphIndex);

								for (int runIndex = 0; runIndex < paragraph.getRuns().size(); runIndex++) {
									XWPFRun run = paragraph.getRuns().get(runIndex);

									List<CTDrawing> drawings = run.getCTR().getDrawingList();
									logger.info("1-1 drawings.size() = {}", drawings.size());

									for (int drawingIndex = 0; drawingIndex < drawings.size(); drawingIndex++) {
										CTDrawing drawing = drawings.get(drawingIndex);
										
										List<CTInline> list_InLine = drawing.getInlineList();
										logger.info("1-1 list_InLine.size() = {}", list_InLine.size());
										for (int inLineIndex = 0; inLineIndex < list_InLine.size(); inLineIndex++) {
											CTInline ctInline = list_InLine.get(inLineIndex);
											
											CTPositiveSize2D ps2d = ctInline.getExtent();
											
											String blipId = null;
											
											long docPrId = ctInline.getDocPr().getId();
											logger.info("1-1 docPrId = {}", docPrId);
											
											switch (String.valueOf(docPrId)) {
											default:
												continue;
											case "2":
												blipId = addPictureData(IMAGE_ID_CARD_FRONT, doc);
												break;
											case "3":
												blipId = addPictureData(IMAGE_ID_CARD_BACK, doc);
												break;
											case "6":
												blipId = addPictureData(IMAGE_ESIGNATURE, doc);
												break;
											}
											
											doc.createPicture(run.getCTR(), blipId, doc.getNextPicNameNumber(XWPFDocument.PICTURE_TYPE_PNG), ps2d.getCx(), ps2d.getCy());
//												run.getCTR().removeDrawing(drawingIndex);
											
											drawing.removeInline(inLineIndex);
											
											logger.info("1-1 TEST, tcInLineIndex = {}", inLineIndex);
										}
									}

									String runText = run.getText(0);

									if (StringUtils.isBlank(runText)) {
										continue;
									}

									if (!runText.startsWith("${") || !runText.endsWith("}")) {
										continue;
									}

									logger.info(String.format("table[%d].tableRow[%d].tableCell[%d].paragraph[%d].run[%d] = %s", tableIndex, tableRowIndex, tableCellIndex, paragraphIndex, runIndex,
											runText));

									Set<Map.Entry<String, String>> textSets = mapReplacedText.entrySet();
									for (Map.Entry<String, String> textSet : textSets) {
										String key = textSet.getKey();
										if (runText.indexOf(key) != -1) {
											run.setText(textSet.getValue(), 0);
										}
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

										List<XWPFParagraph> list_TableCell_TableCell_Paragraphs = tcTableCell.getParagraphs();
										for (int tcParagraphIndex = 0; tcParagraphIndex < list_TableCell_TableCell_Paragraphs.size(); tcParagraphIndex++) {
											XWPFParagraph tcParagraph = list_TableCell_TableCell_Paragraphs.get(tcParagraphIndex);

											for (int tcRunIndex = 0; tcRunIndex < tcParagraph.getRuns().size(); tcRunIndex++) {
												XWPFRun tcRun = tcParagraph.getRuns().get(tcRunIndex);
												
												List<CTDrawing> drawings = tcRun.getCTR().getDrawingList();
												logger.info("2-1 drawings.size() = {}", drawings.size());

												for (int tcRunDrawingIndex = 0; tcRunDrawingIndex < drawings.size(); tcRunDrawingIndex++) {
													CTDrawing drawing = drawings.get(tcRunDrawingIndex);
													
													List<CTInline> list_InLine = drawing.getInlineList();
													logger.info("2-1 list_InLine.size() = {}", list_InLine.size());
													for (int tcInLineIndex = 0; tcInLineIndex < list_InLine.size(); tcInLineIndex++) {
														CTInline ctInline = list_InLine.get(tcInLineIndex);
														
														CTPositiveSize2D ps2d = ctInline.getExtent();
														
														String blipId = null;
														
														long docPrId = ctInline.getDocPr().getId();
														logger.info("2-1 docPrId = {}", docPrId);
														
														switch (String.valueOf(docPrId)) {
														default:
															break;
														case "2":
															blipId = addPictureData(IMAGE_ID_CARD_FRONT, doc);
															break;
														case "3":
															blipId = addPictureData(IMAGE_ID_CARD_BACK, doc);
															break;
														case "6":
															blipId = addPictureData(IMAGE_ESIGNATURE, doc);
															break;
														}
														
														doc.createPicture(tcRun.getCTR(), blipId, doc.getNextPicNameNumber(XWPFDocument.PICTURE_TYPE_PNG), ps2d.getCx(), ps2d.getCy());
//															tcRun.getCTR().removeDrawing(tcRunDrawingIndex);
														
														drawing.removeInline(tcInLineIndex);

														logger.info("2-1 TEST, tcInLineIndex = {}", tcInLineIndex);
														break;
													}

													break;
												}

												String tcRunText = tcRun.getText(0);

												if (StringUtils.isBlank(tcRunText)) {
													continue;
												}

												if (!tcRunText.startsWith("${") || !tcRunText.endsWith("}")) {
													continue;
												}

												logger.info(String.format("table[%d].tableRow[%d].tableCell[%d].paragraph[%d].run[%d] = %s", tcTableIndex, tcTableRowIndex, tcTableCellIndex,
														tcParagraphIndex, tcRunIndex, tcRunText));

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

			doc.write(new FileOutputStream(OUTPUT_FILE));
			doc.close();

			wordConverterToPdf(new FileInputStream(OUTPUT_FILE), new FileOutputStream(OUTPUT_PDF_FILE));

		} catch (Exception e) {
			logger.error("Exception : {}", e);
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

	private static String addPictureData(String image, CustomXWPFDocument doc) throws FileNotFoundException, InvalidFormatException {
		InputStream images = null;
		try {
			images = new FileInputStream(image);
			return doc.addPictureData(images, XWPFDocument.PICTURE_TYPE_PNG);
		} finally {
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
	 
	        //CTGraphicalObjectData graphicData = inline.addNewGraphic().addNewGraphicData();
	        XmlToken xmlToken = null;
	        try {
	            xmlToken = XmlToken.Factory.parse(picXml);
	        } catch(XmlException xe) {
	            xe.printStackTrace();
	        }
	        inline.set(xmlToken);
	        //graphicData.set(xmlToken);
	 
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
	}
}
