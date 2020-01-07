/**
 * 
 */
$(function() {
	var keywordInput = document.getElementById('keywordInput');
	
	var keywordValue = "";
	
	var pageIndex = 1;
	
	/* To prevent form refresh from press 'Enter' key */
	$("form").submit(function() {
		return false;
	});
	
	/* < TextInput > 查詢 */	
	$('.searchInput_winnerName').keypress(function(event) {
		// Some browsers support 'which'(IE) others support 'keyCode' (Chrome
		// ...etc)
		var keycode = (event.keyCode ? event.keyCode : event.which);
		
		if (keycode == 13) {
			/* To prevent page refresh from press 'Enter' key */
			event.preventDefault();
			
			keywordValue = keywordInput.value;
			
			loadDataFunc();
		}
	});
	
	/* Get URL Referrer */
	var urlRef = $('#urlReferrer').val();
	console.info('urlRef = ', urlRef);

	if (urlRef == null || urlRef.length == 0) {
		alert("對不起，您不能直接更改URL來訪問網頁，你的操作非法。");
		window.location.replace(bcs.bcsContextPath + '/admin/winningLetterListPage?status=Active');
		return
	}
	
	var isInitial = true;
	
	var winningLetterRecordId = -1;
	
	var pageWinningLetterId = $.urlParam("wlId");
	
	var pageWinningLetterName = $.urlParam("wlName");
	
	var pageWinningLetterReplyCount = $.urlParam("wlReplyCount");

	title.innerText += ' ( 中獎回函名稱 = ' + decodeURI(pageWinningLetterName) + ',  填寫人數 = ' + pageWinningLetterReplyCount + ' )';
	
	var templateBody = {};
	templateBody = $('.dataTemplate').clone(true);
	$('.dataTemplate').remove();

	var keywordInput = document.getElementById('keywordInput');
	
	var btn_export_pdf = document.getElementById('btn_export_pdf');
	
	/* < CheckBox > Select All */
	$('.cbxSelectAll').click(function() {
		
		func_toggleCheckbox(this);
	});
	
	/* < Button > 查詢 */
	$('.btn_name_query').click(function() {
		
		keywordValue = keywordInput.value;
		
		loadDataFunc();
	});
	
	/* < Button > PDF檔 */
	$('.btn_export_pdf').click(function() {
		
		checkboxes = document.getElementsByName('checkBoxChilds');

		// Support on IE browser
		for (var i = 0, n = checkboxes.length; i < n; i++) {
			console.info("checkboxes[" + i + "].checked = " + checkboxes[i].checked);
			
			if (checkboxes[i].checked == false) {
				continue;
			}
			
			winningLetterRecordId = checkboxes[i].getAttribute('wlrid');
			console.info("checked winningLetterRecordId = " + winningLetterRecordId);
			
			break;
		}
		
		window.location.replace(bcs.bcsContextPath + '/edit/exportWinnerInfoToPDF?wlrId=' + winningLetterRecordId);

	});
	
	/* < Button > Excel檔 */
	$('.btn_export_excel').click(function() {
		window.location.replace(bcs.bcsContextPath + '/edit/exportToExcelForWinnerReplyList?winningLetterId=' + pageWinningLetterId);
	});

	/* 更新狀態按鈕 */
	var func_updateStatusButton = function() {
		var winningLetterId = $(this).attr('winningLetterId');

		if (!confirm((pageStatus == 'Active') ? '請確認是否取消' : '請確認是否生效')) {
			return false;
		}

		$('.LyMain').block($.BCS.blockWinningLetterStatusUpdating);
		
		var ajax_Url = (pageStatus == 'Active') ? 'inactiveWinningLetter?' : 'activeWinningLetter?';

		$.ajax({
			type : "POST",
			url : bcs.bcsContextPath + '/admin/' + ajax_Url + 'winningLetterId=' + winningLetterId
		}).done(function(response) {
			alert("狀態已更新");
			$('.LyMain').unblock();
			loadDataFunc();
		}).fail(function(response) {
			console.info(response);
			$.FailResponse(response);
			$('.LyMain').unblock();
		})
	};

	/* 設定時間 */
	var func_parseDateTime = function(datetime) {
		if (null == datetime) {
			return "-";
		} 
		
		return moment(datetime).format('YYYY-MM-DD HH:mm');
	}
	
	/* 設定人員 */
	var func_parseUser = function(user) {
		if (null == user) {
			return "-";
		} 
		
		return user;
	}

	/* Toggle parent checkboxe to select/unselect all child checkboxs */
	var func_toggleCheckbox = function(srcCheckBox) {
		checkboxes = document.getElementsByName('checkBoxChilds');

		// Not support on IE browser
//		for (let checkbox of checkboxes) {
//			checkbox.checked = srcCheckBox.checked;
//		}

		// Support on IE browser
		for (var i = 0, n = checkboxes.length; i < n; i++) {
			checkboxes[i].checked = srcCheckBox.checked;
		}
		
		if (srcCheckBox.checked) {
			btn_export_pdf.style.visibility = (checkboxes.length == 1) ? 'visible' : "hidden";
		} else if (checkboxes.length == 1){
			btn_export_pdf.style.visibility = 'hidden';
		}
	}

	/* Toggle child checkboxes to select/unselect the parent checkbox */
	var func_toggleChildCheckbox = function() {
		if (isInitial) {
			return;
		}
		
		parentCheckbox = document.getElementById("cbxSelectAll");
		
		checkboxes = document.getElementsByName('checkBoxChilds');
		
		if (checkboxes.length == 1) {
			checkbox = checkboxes[0];
			
			parentCheckbox.checked = checkbox.checked;
			
			btn_export_pdf.style.visibility = (checkbox.checked) ? 'visible' : 'hidden';
			
			return
		}
		
		var numOfCheckedBox = 0;
		
		// Not support on IE browser
//		for (let checkbox of checkboxes) {
//			if (checkbox.checked) {
//				numOfCheckedBox++;
//			}
//					
//			if (!checkbox.checked) {
//				parentCheckbox.checked = false;
//				continue;
//			}
//		}

		// Support on IE browser
		for (var i = 0, n = checkboxes.length; i < n; i++) {
			if (checkboxes[i].checked) {
				numOfCheckedBox++;
			}
			
			if (!checkboxes[i].checked) {
				parentCheckbox.checked = false;
				continue;
			}
		}
		
		if (numOfCheckedBox == 0){
			btn_export_pdf.style.visibility = 'hidden';
			return;
		}
		
		if (numOfCheckedBox == checkboxes.length) {
			parentCheckbox.checked = true;
		}

		if (numOfCheckedBox == 1) {
			btn_export_pdf.style.visibility = 'visible';
			return;
		}

		btn_export_pdf.style.visibility = 'hidden';
	}
	
	/* 彈出視窗 Image Model */
	var model = document.getElementById("myModel");

	/* Model Image 1 */
	var modelImage1 = document.getElementById("model_image1");

	/* Model Image 2 */
	var modelImage2 = document.getElementById("model_image2");

	/* When the user clicks anywhere outside of the model, close the model */
	window.onclick = function(event) {
		if (event.target == model) {
			model.style.display = "none";
		}
	}

	/* When the user click 'ESC', close the model */
	$(document).keyup(function(e) {
		// Some browsers support 'which'(IE) others support 'keyCode' (Chrome
		// ...etc)
		var keycode = (e.keyCode ? e.keyCode : e.which);
		
		if (keycode == 27) {
			if (model.style.display === "block") {
				model.style.display = "none";
			}
		}
	});
	
	/* Defined the popup model for URL */
	var func_showIdCardModel = function() {
		modelImage1.style.height = '200px';
		modelImage1.style.width = '400px';
		modelImage1.src = $(this).attr('img1');
		
		modelImage2.style.height = '200px';
		modelImage2.style.width = '400px';
		modelImage2.src = $(this).attr('img2');

		model.style.display = "block";
	};
	
	
	/* 彈出視窗 E-Signature Model */
	var modelSignature = document.getElementById("mySignModel");

	/* Model Image */
	var signatureImage = document.getElementById("signature_image");

	/* When the user clicks anywhere outside of the model, close the model */
	window.onclick = function(event) {
		if (event.target == modelSignature) {
			modelSignature.style.display = "none";
		}
	}

	/* When the user click 'ESC', close the model */
	$(document).keyup(function(e) {
		// Some browsers support 'which'(IE) others support 'keyCode' (Chrome
		// ...etc)
		var keycode = (e.keyCode ? e.keyCode : e.which);
		
		if (keycode == 27) {
			if (modelSignature.style.display === "block") {
				modelSignature.style.display = "none";
			}
		}
	});
	
	/* Defined the popup model for URL */
	var func_showSignatureModel = function() {
		signatureImage.src = $(this).attr('img1');

		modelSignature.style.display = "block";
	};
	
	
	/* Defined the popup model for URL */
	var func_encryptedString = function(srcString) {
		return srcString.replace(/^(.{4})(?:\d+)(.{4})$/,"$1******$2");
	};
	
	/* Load data */
	var loadDataFunc = function() {
		
		$('.LyMain').block($.BCS.blockWinningLetterListLoading);

		$.ajax({
			type : "GET",
//			url : encodeURI(bcs.bcsContextPath + '/edit/getWinningLetterReplyList/' + pageIndex + '?winnerName=' + keywordValue + '&winningLetterId=' + pageWinningLetterId)
			url : encodeURI(bcs.bcsContextPath + '/edit/getWinningLetterReplyList?winnerName=' + keywordValue + '&winningLetterId=' + pageWinningLetterId)
		}).done(function(response) {
			$('.dataTemplate').remove();

			isInitial = true;

			$.each(response, function(i, o) {
				var dataTemplateBody = templateBody.clone(true);

				dataTemplateBody.find('.checkBox2').click(func_toggleChildCheckbox);
				dataTemplateBody.find('.checkBox2').attr('wlrId', o.id)
				
				$('.checkBox2').click(func_toggleChildCheckbox(this));

				// 中獎者名稱
				dataTemplateBody.find('.winnerName').html(o.name);

				// 連絡電話
				dataTemplateBody.find('.winnerContact').html(func_encryptedString(o.phonenumber));

				// 身分證字號
				dataTemplateBody.find('.winnerIdCardNumber').html(func_encryptedString(o.id_card_number));
				
				// 檢視身分證正反面
				dataTemplateBody.find('.btn_check_id_card').attr('img1', bcs.bcsContextPath + "/getResource/IMAGE/" + o.id_card_copy_front);
				dataTemplateBody.find('.btn_check_id_card').attr('img2', bcs.bcsContextPath + "/getResource/IMAGE/" + o.id_card_copy_back);
				dataTemplateBody.find('.btn_check_id_card').click(func_showIdCardModel);
				
				// 檢視簽名檔
				dataTemplateBody.find('.btn_check_e_signature').attr('img1', bcs.bcsContextPath + "/getResource/IMAGE/" + o.e_signature);
				dataTemplateBody.find('.btn_check_e_signature').click(func_showSignatureModel);
				
				// 回覆時間
				dataTemplateBody.find('.recordTime').html(func_parseDateTime(o.recordTime));

				$('#tableBody').append(dataTemplateBody);

			});
			
			isInitial = false;
			
			keywordInput.value = keywordValue;
			
			document.getElementById("cbxSelectAll").checked = false;

			$('.LyMain').unblock();
		}).fail(function(response) {
			console.info(response);
			$.FailResponse(response);

			$('.LyMain').unblock();
		})
	};

	loadDataFunc();
});