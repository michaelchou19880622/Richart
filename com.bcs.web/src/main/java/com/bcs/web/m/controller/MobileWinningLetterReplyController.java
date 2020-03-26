package com.bcs.web.m.controller;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.bcs.core.db.entity.ContentResource;
import com.bcs.core.db.entity.ContentResourceFile;
import com.bcs.core.db.entity.WinningLetter;
import com.bcs.core.db.entity.WinningLetterRecord;
import com.bcs.core.db.repository.ContentResourceRepository;
import com.bcs.core.db.service.ContentResourceFileService;
import com.bcs.core.db.service.ContentResourceService;
import com.bcs.core.enums.CONFIG_STR;
import com.bcs.core.exception.BcsNoticeException;
import com.bcs.core.resource.CoreConfigReader;
import com.bcs.core.richart.service.WinningLetterRecordService;
import com.bcs.core.richart.service.WinningLetterService;
import com.bcs.core.spring.ApplicationContextProvider;
import com.bcs.core.utils.DataSyncUtil;
import com.bcs.core.utils.ErrorRecord;
import com.bcs.core.utils.FileUtil;
import com.bcs.core.web.ui.page.enums.MobilePageEnum;

@Controller
@RequestMapping("/m")
public class MobileWinningLetterReplyController {

	/** Logger **/
	private static Logger logger = LoggerFactory.getLogger(MobileWinningLetterReplyController.class);

	@Autowired
	private WinningLetterService winningLetterService;

	@Autowired
	private WinningLetterRecordService winningLetterRecordService;

	@Autowired
	private ContentResourceService contentResourceService;
	
	@Autowired
	private ContentResourceFileService contentResourceFileService;
	
	/** WinningLetter Reply Page **/
	@RequestMapping(method = RequestMethod.GET, value = "/wl/winningLetterReplyPage")
	public String winningLetterReplyPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.info("winningLetterReplyPage");

		return MobilePageEnum.WinningLetterReplyPage.toString();
	}

	/** WinningLetter Reply Page For Inactived **/
	@RequestMapping(method = RequestMethod.GET, value = "/wl/winningLetterReplyPageInactived")
	public String winningLetterReplyPageInactived(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.info("winningLetterReplyPageInactived");

		return MobilePageEnum.WinningLetterReplyPageInactived.toString();
	}

	/** WinningLetter Reply Page For Expired **/
	@RequestMapping(method = RequestMethod.GET, value = "/wl/winningLetterReplyPageExpired")
	public String winningLetterReplyPageExpired(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.info("winningLetterReplyPageExpired");

		return MobilePageEnum.WinningLetterReplyPageExpired.toString();
	}

	/** Get winning letter data **/
	@RequestMapping(method = RequestMethod.GET, value = "/wl/getWinningLetter")
	@ResponseBody
	public ResponseEntity<?> getWinningLetter(HttpServletRequest request, HttpServletResponse response, Model model, @RequestParam String winningLetterId) throws IOException {
		logger.info("getWinningLetter");

		String urlReferrer = request.getHeader("referer");
		logger.info("urlReferrer = {}", urlReferrer);

		model.addAttribute("urlReferrer", urlReferrer);

		logger.info("winningLetterId = {}", winningLetterId);

		try {
			if (winningLetterId != null) {
				WinningLetter winningLetter = winningLetterService.findById(Long.valueOf(winningLetterId));
				logger.info("winningLetter = {}", winningLetter);

				if (winningLetter != null) {
					return new ResponseEntity<>(winningLetter, HttpStatus.OK);
				}
			}

			throw new Exception("Could not find the winning letter by id : " + winningLetterId);
		} catch (Exception e) {
			logger.info("Exception : ", e);

			if (e instanceof BcsNoticeException) {
				return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_IMPLEMENTED);
			} else {
				return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	/** Link WinningLetter **/
	@RequestMapping(method = RequestMethod.GET, value = "/wl/{winningLetterId}")
	public ModelAndView winningLetterUrlLinkWithId(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable String winningLetterId) throws Exception {
		logger.info("winningLetterUrlLinkWithId");
		logger.info("winningLetterId = {}", winningLetterId);

		WinningLetter winningLetter = winningLetterService.findById(Long.valueOf(winningLetterId));
		
		String winningLetterName = winningLetter.getName();
		logger.info("winningLetterName = {}", winningLetterName);

		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd ");
		
		String endTime = sdFormat.format(winningLetter.getEndTime());
		logger.info("endTime = {}", endTime);
		
		String gifts = winningLetter.getGift();
		logger.info("gifts = {}", gifts);
		
		boolean isExpired = checkIsExpired(winningLetter.getEndTime());
		logger.info("isExpired = {}", isExpired);

		String liffAppId = null;

		// Check is winning letter inactived?
		if (winningLetter == null || !winningLetter.getStatus().equals(WinningLetter.STATUS_ACTIVE) || isExpired) {
			liffAppId = (isExpired)? CoreConfigReader.getString("winningLetterExpired.LiffAppId") : CoreConfigReader.getString("winningLetterInactived.LiffAppId");
		} else {
			liffAppId = CoreConfigReader.getString("winningLetter.LiffAppId");
		}

		logger.info("liffAppId = {}", liffAppId);
		
		String liffId = liffAppId.split("\\?")[0].replace("line://app/", "");
		logger.info("liffId = {}", liffId);

		String liffUrl = liffAppId;
		
		liffUrl = liffUrl.replace("{liffId}", liffId);
		logger.info("1-1 liffUrl.replace(\"{liffId}\") = {}", liffUrl);
				
		liffUrl = liffUrl.replace("{winningLetterId}", winningLetterId);
		logger.info("1-2 liffUrl.replace(\"{winningLetterId}\") = {}", liffUrl);
		
		liffUrl = liffUrl.replace("{winningLetterName}", URLEncoder.encode(winningLetterName, "UTF-8"));
		logger.info("1-3 liffUrl.replace(\"{winningLetterName}\") = {}", liffUrl);
		
		liffUrl = liffUrl.replace("{endTime}", endTime);
		logger.info("1-4 liffUrl.replace(\"{endTime}\") = {}", liffUrl);
		
		liffUrl = liffUrl.replace("{gifts}", URLEncoder.encode(gifts, "UTF-8"));
		logger.info("1-5 liffUrl.replace(\"{gifts}\") = {}", liffUrl);
		
		liffUrl = liffUrl.replaceAll("\\+", "%20");
		logger.info("1-6 liffUrl.replaceAll(\"\\\\+\") = {}", liffUrl);
		
		return new ModelAndView("redirect:" + liffUrl);
	}

	/** Update Winning Letter Record Data **/
	@RequestMapping(method = RequestMethod.POST, value = "/wl/updateWinningLetterRecord", consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<?> updateWinningLetterRecord(HttpServletRequest request, HttpServletResponse response, Model model, @RequestBody WinningLetterRecord winningLetterRecord) throws Exception {

		logger.info("updateWinningLetterRecord");

		logger.info("winningLetterRecord = {}", winningLetterRecord.toString());

		String urlReferrer = request.getHeader("referer");
		logger.info("urlReferrer = {}", urlReferrer);

		model.addAttribute("urlReferrer", urlReferrer);

		Date currentDateTime = new Date();
		logger.info("currentDateTime = {}", currentDateTime);

		logger.info("RequestBody : winningLetterRecord = {}", winningLetterRecord);
		
		WinningLetterRecord winningLetterRecordData = winningLetterRecordService.findByIdCardNumberAndWinningLetterId(winningLetterRecord.getIdCardNumber(), winningLetterRecord.getWinningLetterId());
		logger.info("winningLetterRecordData = {}", winningLetterRecordData);
		
		if (winningLetterRecordData == null) {
			winningLetterRecordData = new WinningLetterRecord();
		}

		winningLetterRecordData.setIdCardNumber(winningLetterRecord.getIdCardNumber());
		winningLetterRecordData.setUid(winningLetterRecord.getUid());
		winningLetterRecordData.setWinningLetterId(winningLetterRecord.getWinningLetterId());
		winningLetterRecordData.setName(winningLetterRecord.getName());
		winningLetterRecordData.setPhoneNumber(winningLetterRecord.getPhoneNumber());
		winningLetterRecordData.setResidentAddress(winningLetterRecord.getResidentAddress());
		winningLetterRecordData.setMailingAddress(winningLetterRecord.getMailingAddress());
		winningLetterRecordData.setRecordTime(currentDateTime);
		logger.info("winningLetterRecordData = {}", winningLetterRecordData);

		Long winningLetterRecordId = winningLetterRecordService.save(winningLetterRecordData);
		logger.info("winningLetterRecordId = {}", winningLetterRecordId);

		return new ResponseEntity<>(winningLetterRecordId, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/wl/updateWinnerIdCard")
	@ResponseBody
	public ResponseEntity<?> updateWinnerIdCard(HttpServletRequest request, HttpServletResponse response, @RequestParam String type,
			@RequestParam String winningLetterRecordId, @RequestParam String resourceId) throws IOException {
		logger.info("updateWinnerIdCard");

		try {
			if (type != null) {
				logger.info("type = {}", type);
			}

			if (winningLetterRecordId != null) {
				logger.info("winningLetterRecordId = {}", winningLetterRecordId);
			}

			if (resourceId != null) {
				logger.info("resourceId = {}", resourceId);
			}
			
			WinningLetterRecord winningLetterRecordData = winningLetterRecordService.findById(Long.valueOf(winningLetterRecordId));

			if (winningLetterRecordData != null) {
				switch (type) {
				case "f":
					winningLetterRecordData.setIdCardCopyFront(resourceId);
					break;
				case "b":
					winningLetterRecordData.setIdCardCopyBack(resourceId);
					break;
				}

				Long savedWinningLetterRecordId = winningLetterRecordService.save(winningLetterRecordData);
				logger.info("savedWinningLetterRecordId = {}", savedWinningLetterRecordId);
			}

			return new ResponseEntity<>(resourceId, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(ErrorRecord.recordError(e));

			if (e instanceof BcsNoticeException) {
				return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_IMPLEMENTED);
			} else {
				return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/wl/updateWinnerESignature")
	@ResponseBody
	public ResponseEntity<?> updateWinnerESignature(HttpServletRequest request, HttpServletResponse response, @RequestPart MultipartFile filePart, @RequestParam String winningLetterRecordId)
			throws IOException {
		logger.info("updateWinnerESignature");

		try {
			if (winningLetterRecordId != null) {
				logger.info("winningLetterRecordId = {}", winningLetterRecordId);
			}

			if (filePart != null) {

				logger.info("filePart.toString() = {}", filePart.toString());
				logger.info("filePart.getOriginalFilename = {}", filePart.getOriginalFilename());
				logger.info("filePart.getContentType = {}", filePart.getContentType());
				logger.info("filePart.getSize = {}", filePart.getSize());

				ContentResource resource = contentResourceService.uploadFile(filePart, ContentResource.RESOURCE_TYPE_IMAGE, null);

				if (resource != null) {
					logger.info("resource.getResourceId() = {}", resource.getResourceId());

					WinningLetterRecord winningLetterRecordData = winningLetterRecordService.findById(Long.valueOf(winningLetterRecordId));

					if (winningLetterRecordData != null) {

						winningLetterRecordData.seteSignature(resource.getResourceId());

						Long savedWinningLetterRecordId = winningLetterRecordService.save(winningLetterRecordData);
						logger.info("savedWinningLetterRecordId = {}", savedWinningLetterRecordId);
					}
				}

				return new ResponseEntity<>(resource, HttpStatus.OK);
			} else {
//				throw new Exception("Update Winner E-Signature Error");
				return new ResponseEntity<>("Update Winner E-Signature Error", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			logger.error(ErrorRecord.recordError(e));

			if (e instanceof BcsNoticeException) {
				return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_IMPLEMENTED);
			} else {
				return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	public void addTextWatermark(String text, File sourceImageFile, File destImageFile) {
		try {
			BufferedImage sourceImage = ImageIO.read(sourceImageFile);
			Graphics2D g2d = (Graphics2D) sourceImage.getGraphics();

			// initializes necessary graphic properties
			AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f);
			g2d.setComposite(alphaChannel);
			g2d.setColor(Color.RED);
			g2d.setFont(new Font("標楷體", Font.BOLD, 38));
			FontMetrics fontMetrics = g2d.getFontMetrics();
			Rectangle2D rect = fontMetrics.getStringBounds(text, g2d);

			// calculates the coordinate where the String is painted
			int centerX = (sourceImage.getWidth() - (int) rect.getWidth()) / 2;
			int centerY = sourceImage.getHeight() / 2;

			// paints the textual watermark
			g2d.drawString(text, centerX, centerY);

			ImageIO.write(sourceImage, "png", destImageFile);
			g2d.dispose();
		} catch (IOException ex) {
			logger.info("ex = {}", ex);
		}
	}
	
	private static BufferedImage resizeImage(BufferedImage originalImage, int type, int width, int height) {
		
		Image toolkitImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		int tkImageWidth = toolkitImage.getWidth(null);
		int tkImageHeight = toolkitImage.getHeight(null);
		
		BufferedImage resizedImage = new BufferedImage(tkImageWidth, tkImageHeight, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(toolkitImage, 0, 0, width, height, null);
		g.dispose();

		return resizedImage;
	}
	
	public void addImageWatermark(File watermarkImageFile, File sourceImageFile, File destImageFile) {
		try {
			
			BufferedImage sourceImage = ImageIO.read(sourceImageFile);
			logger.info("sourceImage.getWidth() = {}", sourceImage.getWidth());
			logger.info("sourceImage.getHeight() = {}", sourceImage.getHeight());
			
			BufferedImage watermarkImage = ImageIO.read(watermarkImageFile);
			logger.info("watermarkImage.getWidth() = {}", watermarkImage.getWidth());
			logger.info("watermarkImage.getHeight() = {}", watermarkImage.getHeight());
			
			int type = sourceImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : sourceImage.getType();
			
			BufferedImage scaledSourceImage = resizeImage(sourceImage, type, 494, 293);
			logger.info("scaledSourceImage.getWidth() = {}", scaledSourceImage.getWidth());
			logger.info("scaledSourceImage.getHeight() = {}", scaledSourceImage.getHeight());
			
			// initializes necessary graphic properties
			Graphics2D g2d = (Graphics2D) scaledSourceImage.getGraphics();
			AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.95f);
			g2d.setComposite(alphaChannel);
			
			// calculates the coordinate where the image is painted
			int topLeftX = (scaledSourceImage.getWidth() - watermarkImage.getWidth()) / 2;
			int topLeftY = (scaledSourceImage.getHeight() - watermarkImage.getHeight()) / 2;

			// paints the image watermark
			g2d.drawImage(watermarkImage, topLeftX, topLeftY, null);

			ImageIO.write(scaledSourceImage, "png", destImageFile);
			g2d.dispose();

		} catch (IOException ex) {
			logger.info("ex = {}", ex);
		}
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/wl/addWatermark")
	@ResponseBody
	public ResponseEntity<?> addWatermark(HttpServletRequest request, HttpServletResponse response, @RequestPart MultipartFile filePart)
			throws IOException {

		synchronized (this) {
		
			logger.info("addWatermark");
	
			try {
				if (filePart != null) {
	
					logger.info("filePart.toString() = {}", filePart.toString());
					logger.info("filePart.getOriginalFilename = {}", filePart.getOriginalFilename());
					logger.info("filePart.getContentType = {}", filePart.getContentType());
					logger.info("filePart.getSize = {}", filePart.getSize());
	
					ContentResource resource = contentResourceService.uploadFile(filePart, ContentResource.RESOURCE_TYPE_IMAGE, null, false);
					
					if (resource != null) {
						logger.info("resource.getResourceId() = {}", resource.getResourceId());
						
						String filePath = CoreConfigReader.getString(CONFIG_STR.FilePath) + System.getProperty("file.separator") + "IMAGE";
						logger.info("filePath = {}", filePath);
	
						String srcFile = filePath + System.getProperty("file.separator") + resource.getResourceId();
						logger.info("srcFile = {}", srcFile);
	
						String waterMarkPath = CoreConfigReader.getString(CONFIG_STR.FilePath) + System.getProperty("file.separator") + "DEFAULT";
	
						File fileWatermark = new File(waterMarkPath + System.getProperty("file.separator") + "richart_use_only_watermark.png");
						logger.info("fileWatermark = {}", fileWatermark);
	
						File fileSrcImage = new File(srcFile);
						
						addImageWatermark(fileWatermark, fileSrcImage, fileSrcImage);
						
						ContentResourceFile contentResourceFile = contentResourceFileService.findOne(resource.getResourceId());
					
						if (contentResourceFile != null) { // For Debug Used
							logger.info("1-1 contentResourceFile.getResourceId() = {}", contentResourceFile.getResourceId());
							logger.info("1-1 contentResourceFile.getModifyTime() = {}", contentResourceFile.getModifyTime());
						}
						else if (contentResourceFile == null) {
							contentResourceFile = new ContentResourceFile(resource.getResourceId());
						}

				        byte[] bFile = new byte[(int) fileSrcImage.length()];
				        FileInputStream fileInputStream = new FileInputStream(fileSrcImage);
				        fileInputStream.read(bFile);
				        fileInputStream.close();
				        
				        contentResourceFile.setFileData(bFile);
				        contentResourceFile.setModifyTime(new Date());
						
				        contentResourceFile = contentResourceFileService.saveAndGetReturn(contentResourceFile);
				        
				    	if (contentResourceFile != null) { // For Debug Used
							logger.info("1-2 contentResourceFile.getResourceId() = {}", contentResourceFile.getResourceId());
							logger.info("1-2 contentResourceFile.getModifyTime() = {}", contentResourceFile.getModifyTime());
						}

						DataSyncUtil.settingReSync(ContentResourceService.RESOURCE_SYNC);
						
						contentResourceService.syncResourceFile();
					}
	
					return new ResponseEntity<>(resource.getResourceId(), HttpStatus.OK);
				} else {
					return new ResponseEntity<>("Add watermark error", HttpStatus.INTERNAL_SERVER_ERROR);
				}
			} catch (Exception e) {
				logger.error(ErrorRecord.recordError(e));
	
				if (e instanceof BcsNoticeException) {
					return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_IMPLEMENTED);
				} else {
					return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
		}
	}
	
	public boolean checkIsExpired(Date endDateTime) {

		boolean isExpired = false;
		
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			Date curDate = simpleDateFormat.parse(simpleDateFormat.format(new Date()));

			logger.info("curDate = {}", curDate);

			logger.info("endDateTime = {}", endDateTime);

			isExpired = (curDate.compareTo(endDateTime) >= 0);

		} catch (ParseException e) {
			logger.error("ParseException = {}", e);
		}

		logger.info("isExpired = {}", isExpired);

		return isExpired;
	}
}
