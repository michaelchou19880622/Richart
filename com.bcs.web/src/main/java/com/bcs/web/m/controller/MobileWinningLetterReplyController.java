package com.bcs.web.m.controller;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.bcs.core.db.entity.WinningLetter;
import com.bcs.core.db.entity.WinningLetterRecord;
import com.bcs.core.db.service.ContentResourceService;
import com.bcs.core.enums.CONFIG_STR;
import com.bcs.core.exception.BcsNoticeException;
import com.bcs.core.resource.CoreConfigReader;
import com.bcs.core.richart.service.WinningLetterRecordService;
import com.bcs.core.richart.service.WinningLetterService;
import com.bcs.core.utils.ErrorRecord;
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

		String liffAppId = null;

		// Check is winning letter inactived?
		if (winningLetter == null || !winningLetter.getStatus().equals(WinningLetter.STATUS_ACTIVE)) {
			liffAppId = CoreConfigReader.getString("winningLetterInactived.LiffAppId");
		} else {
			liffAppId = CoreConfigReader.getString("winningLetter.LiffAppId");
		}

		logger.info("liffAppId = {}", liffAppId);
		
		String liffId = liffAppId.split("\\?")[0].replace("line://app/", "");
		logger.info("liffId = {}", liffId);

		String liffUrl = liffAppId;
		
		//line://app/1550669403-KA59ja3L?liffId={liffId}&winningLetterId={winningLetterId}&winningLetterName={winningLetterName}&endTime={endTime}&gifts={gifts}
		
		liffUrl = liffUrl.replace("{liffId}", liffId);
		logger.info("1-1 liffUrl = {}", liffUrl);
				
		liffUrl = liffUrl.replace("{winningLetterId}", winningLetterId);
		logger.info("1-2 liffUrl = {}", liffUrl);
		
		liffUrl = liffUrl.replace("{winningLetterName}", winningLetterName);
		logger.info("1-3 liffUrl = {}", liffUrl);
		
		liffUrl = liffUrl.replace("{endTime}", endTime);
		logger.info("1-4 liffUrl = {}", liffUrl);
		
		liffUrl = liffUrl.replace("{gifts}", gifts);
		logger.info("1-5 liffUrl = {}", gifts);
		
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

		Long winningLetterRecordId = winningLetterRecordService.save(winningLetterRecordData);
		logger.info("winningLetterRecordId = {}", winningLetterRecordId);

		return new ResponseEntity<>(winningLetterRecordId, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/wl/updateWinnerIdCard")
	@ResponseBody
	public ResponseEntity<?> updateWinnerIdCard(HttpServletRequest request, HttpServletResponse response, @RequestPart MultipartFile filePart, @RequestParam String type,
			@RequestParam String winningLetterRecordId) throws IOException {
		logger.info("updateWinnerIdCard");

		try {
			if (type != null) {
				logger.info("type = {}", type);
			}

			if (winningLetterRecordId != null) {
				logger.info("winningLetterRecordId = {}", winningLetterRecordId);
			}

			if (filePart != null) {

				logger.info("filePart.toString() = {}", filePart.toString());
				logger.info("filePart.getOriginalFilename = {}", filePart.getOriginalFilename());
				logger.info("filePart.getContentType = {}", filePart.getContentType());
				logger.info("filePart.getSize = {}", filePart.getSize());

				ContentResource resource = contentResourceService.uploadFile(filePart, ContentResource.RESOURCE_TYPE_IMAGE, null);

				String filePath = CoreConfigReader.getString(CONFIG_STR.FilePath) + System.getProperty("file.separator") + "IMAGE";
				logger.info("filePath = {}", filePath);

				String srcFile = filePath + System.getProperty("file.separator") + resource.getResourceId();
				logger.info("srcFile = {}", srcFile);

				String waterMarkPath = CoreConfigReader.getString(CONFIG_STR.FilePath) + System.getProperty("file.separator") + "DEFAULT";

				File fileWatermark = new File(waterMarkPath + System.getProperty("file.separator") + "richart_use_only_watermark.png");
				logger.info("fileWatermark = {}", fileWatermark);

				addImageWatermark(fileWatermark, new File(srcFile), new File(srcFile));

				if (resource != null) {
					logger.info("resource.getResourceId() = {}", resource.getResourceId());

					WinningLetterRecord winningLetterRecordData = winningLetterRecordService.findById(Long.valueOf(winningLetterRecordId));

					if (winningLetterRecordData != null) {
						switch (type) {
						case "f":
							winningLetterRecordData.setIdCardCopyFront(resource.getResourceId());
							break;
						case "b":
							winningLetterRecordData.setIdCardCopyBack(resource.getResourceId());
							break;
						}

						Long savedWinningLetterRecordId = winningLetterRecordService.save(winningLetterRecordData);
						logger.info("savedWinningLetterRecordId = {}", savedWinningLetterRecordId);
					}
				}

				return new ResponseEntity<>(resource, HttpStatus.OK);
			} else {
//				throw new Exception("Update Winner Id Card Error");
				return new ResponseEntity<>("Update Winner Id Card Error", HttpStatus.INTERNAL_SERVER_ERROR);
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

	public void addImageWatermark(File watermarkImageFile, File sourceImageFile, File destImageFile) {
		try {
			BufferedImage sourceImage = ImageIO.read(sourceImageFile);
			BufferedImage watermarkImage = ImageIO.read(watermarkImageFile);

			// initializes necessary graphic properties
			Graphics2D g2d = (Graphics2D) sourceImage.getGraphics();
			AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f);
			g2d.setComposite(alphaChannel);

			// calculates the coordinate where the image is painted
			int topLeftX = (sourceImage.getWidth() - watermarkImage.getWidth()) / 2;
			int topLeftY = (sourceImage.getHeight() - watermarkImage.getHeight()) / 2;

			// paints the image watermark
			g2d.drawImage(watermarkImage, topLeftX, topLeftY, null);

			ImageIO.write(sourceImage, "png", destImageFile);
			g2d.dispose();

		} catch (IOException ex) {
			logger.info("ex = {}", ex);
		}
	}
}
