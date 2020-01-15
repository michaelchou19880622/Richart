/**
 * 
 */
$(function() {
	var keywordInput = document.getElementById('keywordInput');
	
	var keywordValue = "";
	
	var totalPageSize = document.getElementById('totalPageSize');
	
	var valTotalPageSize = 0;
	
	var currentPageIndex = document.getElementById('currentPageIndex');
	
	var valCurrentPageIndex = 1;
	
	var perPageSize = $(this).find('option:selected').text();
	
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

	/* 更新每頁顯示數量下拉選單 */
	var func_optionSelectChanged = function(){
		var selectValue = $(this).find('option:selected').text();
		
		$(this).closest('.optionPageSize').find('.optionLabelPageSize').html(selectValue);
		
		perPageSize = selectValue;
		
		loadDataFunc();
	};

	$('.optionSelectPageSize').change(func_optionSelectChanged);
	
	/* Get URL Referrer */
	var urlRef = $('#urlReferrer').val();

	if (urlRef == null || urlRef.length == 0) {
		alert("對不起，您不能直接更改URL來訪問網頁，你的操作非法。");
		window.location.replace(bcs.bcsContextPath + '/admin/winningLetterListPage?status=Active');
		return
	}
	
	/* Get URL PDF Export Path */
	var pdfExportPath = $('#pdfExportPath').val();
	console.info('pdfExportPath = ', pdfExportPath);
	
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
	
	/* < Button > 上一頁 */
	$('#btn_PreviousPage').click(function() {
		valCurrentPageIndex = (valCurrentPageIndex - 1 <= 0)? valCurrentPageIndex : valCurrentPageIndex - 1;

//		btn_export_pdf.style.visibility = 'hidden';
		
		loadDataFunc();
	});
	
	/* < Button > 下一頁 */
	$('#btn_NextPage').click(function() {
		valCurrentPageIndex = (valCurrentPageIndex + 1 > valTotalPageSize)? valCurrentPageIndex : valCurrentPageIndex + 1;
		
//		btn_export_pdf.style.visibility = 'hidden';
		
		loadDataFunc();
	});
	
	/* < Button > 查詢 */
	$('.btn_name_query').click(function() {
		
		isInitial = false;
		
		keywordValue = keywordInput.value;

		document.getElementById("cbxSelectAll").checked = false;
		
//		btn_export_pdf.style.visibility = 'hidden';
		
		loadDataFunc();
	});
	
	/* < Button > PDF檔 */
	$('.btn_export_pdf').click(function() {

		var checkedWinningLetterRecordIds = [];
		var pdfFiles = "";
		
		$('.LyMain').block($.BCS.blockWinningLetterRecordExporting);
		
		checkboxes = document.getElementsByName('checkBoxChilds');

		// Support on IE browser
		for (var i = 0, n = checkboxes.length; i < n; i++) {
			
			if (checkboxes[i].checked == false) {
				continue;
			}
			
			winningLetterRecordId = checkboxes[i].getAttribute('wlrid');
			
			checkedWinningLetterRecordIds.push(winningLetterRecordId);
			
//			break;
		}
		
//		window.location.replace(bcs.bcsContextPath + '/edit/exportWinnerInfoToPDF?wlrId=' + winningLetterRecordId);
		
		$.ajax({
			type : "POST",
            cache: false,
            contentType: "application/json; charset=utf-8",
            dataType: "json",
			url : encodeURI(bcs.bcsContextPath + '/edit/exportWinnerInfoListToPDF'),
			data: JSON.stringify(checkedWinningLetterRecordIds)
		}).done(function(response) {
			response.forEach(function(fileName){
				pdfFiles = pdfFiles + fileName + '\n';
			});
			
			alert("下列檔案已匯出至 \" "+ pdfExportPath + " \"\n" + pdfFiles);

			$('.LyMain').unblock();
			
//			$('.LyMain').unblock();
		}).fail(function(response) {
			console.info(response);
			$.FailResponse(response);

			$('.LyMain').unblock();
		})

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
			btn_export_pdf.style.visibility = 'visible';
//			btn_export_pdf.style.visibility = (checkboxes.length == 1) ? 'visible' : "hidden";
		} 
//		else if (checkboxes.length == 1){
//			btn_export_pdf.style.visibility = 'hidden';
//		}
	}

	/* Toggle child checkboxes to select/unselect the parent checkbox */
	var func_toggleChildCheckbox = function() {
		if (isInitial) {
			return;
		}
		
		parentCheckbox = document.getElementById("cbxSelectAll");
		
		checkboxes = document.getElementsByName('checkBoxChilds');
		console.info('checkboxes.length = ' + checkboxes.length);
		
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

//		btn_export_pdf.style.visibility = 'hidden';
	}
	
	/* 彈出視窗 Image Model */
	var model = document.getElementById("myModel");

	/* Model Image 1 */
	var modelImage1 = document.getElementById("model_image1");
	modelImage1.style.height = '200px';
	modelImage1.style.width = '400px';

	/* Model Image 2 */
	var modelImage2 = document.getElementById("model_image2");
	modelImage2.style.height = '200px';
	modelImage2.style.width = '400px';

	/* When the user clicks anywhere outside of the model, close the model */
	window.onclick = function(event) {
		if (event.target == model) {
			model.style.display = "none";
		}
	}
	
	window.addEventListener('click', function(e) {   
		if (document.getElementById('myModel').contains(e.target)){
			model.style.display = "none";
		}
	});

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
		modelImage1.src = $(this).attr('img1');
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
		
		keywordInput.value = keywordValue;
		
		document.getElementById("cbxSelectAll").checked = false;
		
		btn_export_pdf.style.visibility = 'hidden';
		
		console.info('bcs.bcsContextPath = ' + bcs.bcsContextPath);
		
		$.ajax({
			type : "GET",
//			url : encodeURI(bcs.bcsContextPath + '/edit/getWinningLetterReplyList?winnerName=' + keywordValue + '&winningLetterId=' + pageWinningLetterId)
			url : encodeURI(bcs.bcsContextPath + '/edit/getWinningLetterReplyList?winnerName=' + keywordValue + '&winningLetterId=' + pageWinningLetterId + '&page=' + (valCurrentPageIndex - 1) +'&size=' + perPageSize)
		}).done(function(response) {
			$('.dataTemplate').remove();

			if (response.totalElements == 0) {
				totalPageSize.innerText = '-';
				currentPageIndex.innerText = '-';

				$('.LyMain').unblock();
				
				return;
			}
			
			valTotalPageSize = response.totalPages;
			valCurrentPageIndex = (response.number + 1);
			
			totalPageSize.innerText = valTotalPageSize;
			currentPageIndex.innerText = valCurrentPageIndex;

			if (valCurrentPageIndex > valTotalPageSize) {
				valCurrentPageIndex = valTotalPageSize;
				currentPageIndex.innerText = valCurrentPageIndex;
				
				loadDataFunc();
				
				return;
			}

			isInitial = true;

			$.each(response.content, function(i, o) {
				var dataTemplateBody = templateBody.clone(true);

				dataTemplateBody.find('.checkBox2').click(func_toggleChildCheckbox);
				dataTemplateBody.find('.checkBox2').attr('wlrId', o.id)
				dataTemplateBody.find('.checkBox2').attr('id', o.id)
				
//				$('.checkBox2').click(func_toggleChildCheckbox(this));

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