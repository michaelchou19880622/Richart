/**
 * 
 */
$(function() {
	console.info('bcs.bcsContextPath:' + bcs.bcsContextPath);

	var pageStatus = $.urlParam("status") || 'Active';
	console.info('pageStatus = ', pageStatus);
	
	var templateBody = {};
	templateBody = $('.dataTemplate').clone(true);
	$('.dataTemplate').remove();
	
	var activeButton = document.getElementById("ActiveBtn");
	var disableButton = document.getElementById("DisableBtn");

	/* < Button > 匯出EXCEL */
	$('.btn_export').click(function() {
//		window.location.replace(bcs.bcsContextPath + '/edit/shareCampaignCreatePage?actionType=Create&from=active');
	});

	/* < Button > 狀態 = '生效' */
	$('.ActiveBtn').click(function() {
		window.location.replace(bcs.bcsContextPath + '/admin/winningLetterListPage?status=Active');
		$(this).parent().addClass("btn btn-primary done xxx");
		activeButton.classList.add('CHLeftBtn ExSelected');
		activeButton.classList.remove('CHLeftBtn')
		
		disableButton.classList.add('CHLeftBtn');
		disableButton.classList.remove('CHLeftBtn ExSelected')
	});

	/* < Button > 狀態 = '取消' */
	$('.DisableBtn').click(function() {
		window.location.replace(bcs.bcsContextPath + '/admin/winningLetterListPage?status=Inactive');
		activeButton.classList.add('CHLeftBtn');
		activeButton.classList.remove('CHLeftBtn ExSelected')
		
		disableButton.classList.add('CHLeftBtn ExSelected');
		disableButton.classList.remove('CHLeftBtn')
	});

	/* < Function > 複製 */
	var func_copyWinningLetter = function() {
		var winningLetterId = $(this).attr('winningLetterId');
		console.info('func_copyWinningLetter winningLetterId = ' + winningLetterId);
		
		window.location.replace(bcs.bcsContextPath + '/admin/winningLetterMainPage?id=' + winningLetterId + '&actionType=Copy&isExpired=False');
	};

	/* < Function > 刪除 */
	var func_deteleWinningLetter = function() {
		var winningLetterId = $(this).attr('winningLetterId');
		console.info('func_deteleWinningLetter winningLetterId = ' + winningLetterId);

		if (!confirm('請確認是否刪除')) {
			return false;
		}

		$.ajax({
			type : "DELETE",
			url : bcs.bcsContextPath + '/admin/deleteWinningLetter?winningLetterId=' + winningLetterId
		}).done(function(response) {
			console.info(response);
			alert("刪除成功");
			loadDataFunc();
		}).fail(function(response) {
			console.info(response);
			$.FailResponse(response);
		})
	};

	/* 更新狀態按鈕 */
	var func_updateStatusButton = function() {
		var winningLetterId = $(this).attr('winningLetterId');
		console.info('redesignFunc winningLetterId:' + winningLetterId);

		if (!confirm((pageStatus == 'Active') ? '請確認是否取消' : '請確認是否生效')) {
			return false;
		}
		
		var ajax_Url = (pageStatus == 'Active') ? 'inactiveWinningLetter?' : 'activeWinningLetter?';

		$.ajax({
			type : "POST",
			url : bcs.bcsContextPath + '/admin/' + ajax_Url + 'winningLetterId=' + winningLetterId
		}).done(function(response) {
			console.info(response);
			alert("改變成功");
			loadDataFunc();
		}).fail(function(response) {
			console.info(response);
			$.FailResponse(response);
		})
	};

	/* 設定狀態顯示文字 */
	var func_parseStatus = function(winningLetterStatus) {
		
		if ("Active" == winningLetterStatus) {
			return "生效";
		} else if ("Inactive" == winningLetterStatus) {
			return "取消";
		}
	}

	/* 設定狀態按鈕文字 */
	var func_parseButtonStatus = function(pageStatus) {

		console.info('pageStatus = ' + pageStatus);
		
		if ("Active" == pageStatus) {
			return "取消";
		} else if ("Inactive" == pageStatus) {
			return "生效";
		}
	}
	
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
	
	/* 計算填寫人數 */
	var func_countReplyPeople = function(dataTemplateBody, winningLetterId) {

		$.ajax({
			type : "GET",
			url : bcs.bcsContextPath + '/edit/countWinningLetterReplyPeople?winningLetterId=' + winningLetterId
		}).done(function(response) {
			console.info(response);
			dataTemplateBody.find('.replyPeople a').html($.BCS.formatNumber(response, 0));

		}).fail(function(response) {
			console.info(response);
			$.FailResponse(response);
		})
	}
	
	/* 檢查活動時間是否到期 */
	var func_checkIsExpired = function(datetime) {
		
		var currentDateTime = new Date();
		
		if (currentDateTime > datetime) {
			return 'True';
		} else {
			return 'False';
		}
	}

	/* Get parent URL */
	var winningLetterTracingUrlPre = $('#winninLetterTracingUrlPre').val();

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
			modelUrl.innerHTML = "*** 活動已逾期 *** ";
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

	/* Load data */
	var loadDataFunc = function() {
		console.info("loadDataFunc start");

		$.ajax({
			type : "GET",
			url : bcs.bcsContextPath + '/edit/getWinningLetterList?status=' + pageStatus
		}).done(function(response) {
			$('.dataTemplate').remove();
			console.info(response);

			$.each(response, function(i, o) {
				var dataTemplateBody = templateBody.clone(true);

				var isExpired = func_checkIsExpired(o.endTime);

				// 名稱
				dataTemplateBody.find('.winningLetterName a')
								.attr('href', bcs.bcsContextPath + '/admin/winningLetterMainPage?id=' + o.id + '&actionType=Edit' + '&isExpired=' + isExpired)
								.html(o.name);

				// 狀態:生效
				dataTemplateBody.find('.winningLetterStatus span').html(func_parseStatus(o.status));

				// 按鈕:取消
				dataTemplateBody.find('.btn_status').val(func_parseButtonStatus(pageStatus));
				dataTemplateBody.find('.btn_status').attr('winningLetterId', o.id);
				dataTemplateBody.find('.btn_status').click(func_updateStatusButton);

				// 填寫人數
				func_countReplyPeople(dataTemplateBody, o.id);
//				dataTemplateBody.find('.replyPeople a').attr('href', bcs.bcsContextPath +'/edit/couponReportPage?couponId=' + o.winningLetterId);
				
				// 中獎回函網址
				dataTemplateBody.find('.btn_css').attr('winningLetterId', o.id);
				dataTemplateBody.find('.btn_css').attr('isExpired', isExpired);
				dataTemplateBody.find('.btn_css').click(func_showUrlModel);

				// 回覆到期時間
				dataTemplateBody.find('.winningLetterEndTime').html(func_parseDateTime(o.endTime));
				
				// 建立時間
				dataTemplateBody.find('.createTime').html(func_parseDateTime(o.createTime));
				
				// 建立人員
				dataTemplateBody.find('.createUser').html(func_parseUser(o.createUser));

				// 修改時間
				dataTemplateBody.find('.modifyTime').html(func_parseDateTime(o.modifyTime));
				
				// 修改人員
				dataTemplateBody.find('.modifyUser').html(func_parseUser(o.modifyUser));

				// 複製
				dataTemplateBody.find('.btn_copy').attr('winningLetterId', o.id).click(func_copyWinningLetter);
				
				//刪除
				dataTemplateBody.find('.btn_detele').attr('winningLetterId', o.id).click(func_deteleWinningLetter);

				$('#tableBody').append(dataTemplateBody);
			});
		}).fail(function(response) {
			console.info(response);
			$.FailResponse(response);
		})

		console.info("loadDataFunc end");
	};

	loadDataFunc();
});