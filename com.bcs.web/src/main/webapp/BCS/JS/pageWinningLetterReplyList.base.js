/**
 * 
 */
$(function() {
	/* To prevent form refresh from press 'Enter' key */
	$("form").submit(function() {
		return false;
	});
	
	var isInitial = true;
	
	var pageWinningLetterId = $.urlParam("wlId");
	
	var pageWinningLetterName = $.urlParam("wlName");
	
	var pageWinningLetterReplyCount = $.urlParam("wlReplyCount");

	title.innerText += ' ( 中獎回函名稱 = ' + decodeURI(pageWinningLetterName) + ',  填寫人數 = ' + pageWinningLetterReplyCount + ' )';
	
	var templateBody = {};
	templateBody = $('.dataTemplate').clone(true);
	$('.dataTemplate').remove();

	var keywordInput = document.getElementById('keywordInput');
	
	var btn_export_pdf = document.getElementById('btn_export_pdf');
	
	$('.searchInput_winnerName').keypress(function(event) {
		// Some browsers support 'which'(IE) others support 'keyCode' (Chrome ...etc)
		var keycode = (event.keyCode ? event.keyCode : event.which);
		
		if (keycode == '13') {
			/* To prevent page refresh from press 'Enter' key */
			event.preventDefault();
			
			loadDataFunc();
		}
	});
	
	/* < CheckBox > Select All */
	$('.cbxSelectAll').click(function() {
		
		func_toggleCheckbox(this);
	});
	
	/* < Button > 查詢 */
	$('.btn_name_query').click(function() {
		
		loadDataFunc();
	});
	
	/* < Button > PDF檔 */
	$('.btn_export').click(function() {
//		window.location.replace(bcs.bcsContextPath + '/edit/exportToExcelForWinningLetter?name=' + keywordInput.value +'&status=' + pageStatus);
		alert("功能開發中..");
	});
	
	/* < Button > Excel檔 */
	$('.btn_export').click(function() {
		window.location.replace(bcs.bcsContextPath + '/edit/exportToExcelForWinningLetter?name=' + keywordInput.value +'&status=' + pageStatus);
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
		
		for (let checkbox of checkboxes){
			checkbox.checked = srcCheckBox.checked;
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
		
		for (let checkbox of checkboxes){
			if (checkbox.checked) {
				numOfCheckedBox++;
			}
			
			if (!checkbox.checked) {
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
	
	/* 彈出視窗Model */
	var modal = document.getElementById("myModal");

	/* URL */
	var modelUrl = document.getElementById("modelUrl");

	/* When the user clicks anywhere outside of the modal, close the model */
	window.onclick = function(event) {
		if (event.target == modal) {
			modal.style.display = "none";
		}
	}

	/* When the user click 'ESC', close the model */
	$(document).keyup(function(e) {
		if (e.key === "Escape") {
			if (modal.style.display === "block") {
				modal.style.display = "none";
			}
		}
	});
	
	/* Defined the popup model for URL */
	var func_showUrlModel = function() {

		var isExpired = $(this).attr('isExpired');
		
		if (isExpired == 'True') {
			modelUrl.innerHTML = "*** 活動已逾期；若活動需延期，請在回覆到期時間截止前修改到期時間 *** ";
			modelUrl.style.color = "#FF0000";
			modelUrl.removeAttribute('href');

			modal.style.display = "block";
		} else {
			var winningLetterId = $(this).attr('winningLetterId');

			modelUrl.innerHTML = winningLetterTracingUrlPre + winningLetterId;
			modelUrl.style.color = "#0000FF";
			modelUrl.setAttribute('href', winningLetterTracingUrlPre + winningLetterId);

			modal.style.display = "block";
		}
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
			url : bcs.bcsContextPath + '/edit/getWinningLetterReplyList?winnerName=' + keywordInput.value + '&winningLetterId=' + pageWinningLetterId
		}).done(function(response) {
			$('.dataTemplate').remove();

			isInitial = true;

			$.each(response, function(i, o) {
				var dataTemplateBody = templateBody.clone(true);

				dataTemplateBody.find('.checkBox2').click(func_toggleChildCheckbox);
				
				$('.checkBox2').click(func_toggleChildCheckbox(this));

				// 中獎者名稱
				dataTemplateBody.find('.winnerName').html(o.name);

				// 連絡電話
				dataTemplateBody.find('.winnerContact').html(func_encryptedString(o.phonenumber));

				// 身分證字號
				dataTemplateBody.find('.winnerIdCardNumber').html(func_encryptedString(o.id_card_number));
				
				// 回覆時間
				dataTemplateBody.find('.recordTime').html(func_parseDateTime(o.recordTime));

				$('#tableBody').append(dataTemplateBody);

			});
			
			isInitial = false;
			
			keywordInput.value = keywordInput.value;

			$('.LyMain').unblock();
		}).fail(function(response) {
			console.info(response);
			$.FailResponse(response);

			$('.LyMain').unblock();
		})
	};

	loadDataFunc();
});