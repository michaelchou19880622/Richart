/**
 * 
 */
$(function() {
	var keywordInput = document.getElementById('keywordInput');
	
	var keywordValue = "";
	
	/* To prevent form refresh from press 'Enter' key */
	$("form").submit(function() {
		return false;
	});
	
	/* < TextInput > 查詢 */	
	$('.searchInput_winningLetterName').keypress(function(event) {
		// Some browsers support 'which'(IE) others support 'keyCode' (Chrome ...etc)
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
		window.location.replace(bcs.bcsContextPath + '/admin/winningLetterListPage');
		return;
	}

	var pageStatus = $.urlParam("status") || 'Active';

	var templateBody = {};
	templateBody = $('.dataTemplate').clone(true);
	$('.dataTemplate').remove();

	var activeButton_li = document.getElementById('ActiveBtn_li');
	var disableButton_li = document.getElementById('DisableBtn_li');

	/* < Button > 查詢 */
	$('.btn_name_query').click(function() {

		keywordValue = keywordInput.value;
		
		loadDataFunc();
	});

	/* < Button > 匯出EXCEL */
	$('.btn_export').click(function() {
		window.location.replace(encodeURI(bcs.bcsContextPath + '/edit/exportToExcelForWinningLetter?name=' + keywordValue + '&status=' + pageStatus));
	});

	/* < Button > 狀態 = '生效' */
	$('.ActiveBtn').click(function() {
		window.location.replace(bcs.bcsContextPath + '/admin/winningLetterListPage?status=Active');
	});

	/* < Button > 狀態 = '取消' */
	$('.DisableBtn').click(function() {
		window.location.replace(bcs.bcsContextPath + '/admin/winningLetterListPage?status=Inactive');
	});

	/* < Function > 複製 */
	var func_copyWinningLetter = function() {
		var winningLetterId = $(this).attr('winningLetterId');

		window.location.replace(bcs.bcsContextPath + '/admin/winningLetterMainPage?id=' + winningLetterId + '&actionType=Copy&isExpired=False');
	};

	/* < Function > 刪除 */
	var func_deteleWinningLetter = function() {
		var winningLetterId = $(this).attr('winningLetterId');

		if (!confirm('請確認是否刪除')) {
			return false;
		}

		$('.LyMain').block($.BCS.blockWinningLetterDeleting);

		$.ajax({
			type : "DELETE",
			url : bcs.bcsContextPath + '/admin/deleteWinningLetter?winningLetterId=' + winningLetterId
		}).done(function(response) {
			alert("刪除成功");
			$('.LyMain').unblock();
			loadDataFunc();
		}).fail(function(response) {
			console.info(response);
			$.FailResponse(response);
			$('.LyMain').unblock();
		})
	};

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
	var func_countReplyPeople = function(dataTemplateBody, winningLetterId, winningLetterName) {

		$.ajax({
			type : "GET",
			url : bcs.bcsContextPath + '/edit/countWinningLetterReplyPeople?winningLetterId=' + winningLetterId
		}).done(
				function(response) {
					replyCount = $.BCS.formatNumber(response, 0);

					dataTemplateBody.find('.replyPeople a').attr('href', encodeURI(bcs.bcsContextPath + '/admin/winningLetterReplyListPage?wlId=' + winningLetterId + '&wlName=' + winningLetterName + '&wlReplyCount=' + replyCount)).html(replyCount);
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
	var model = document.getElementById("myModel");

	/* URL */
	var modelUrl = document.getElementById("modelUrl");

	/* When the user clicks anywhere outside of the model, close the model */
	window.onclick = function(event) {
		if (event.target == model) {
			model.style.display = "none";
		}
	}

	/* When the user click 'ESC', close the model */
	$(document).keyup(function(e) {
		if (e.key === "Escape") {
			if (model.style.display === "block") {
				model.style.display = "none";
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

			model.style.display = "block";
		} else {
			var winningLetterId = $(this).attr('winningLetterId');

			modelUrl.innerHTML = winningLetterTracingUrlPre + winningLetterId;
			modelUrl.style.color = "#0000FF";
			modelUrl.setAttribute('href', winningLetterTracingUrlPre + winningLetterId);

			model.style.display = "block";
		}
	};

	/* Load data */
	var loadDataFunc = function() {
		(pageStatus == 'Active') ? activeButton_li.classList.add("ExSelected") : disableButton_li.classList.add("ExSelected");

		$('.LyMain').block($.BCS.blockWinningLetterListLoading);

		$.ajax({
			type : "GET",
			url : encodeURI(bcs.bcsContextPath + '/edit/getWinningLetterList?name=' + keywordValue + '&status=' + pageStatus)
		}).done(function(response) {
			$('.dataTemplate').remove();

			$.each(response, function(i, o) {
				var dataTemplateBody = templateBody.clone(true);

				var isExpired = func_checkIsExpired(o.endTime);

				// 名稱
				dataTemplateBody.find('.winningLetterName a')
								.attr('href', bcs.bcsContextPath + '/admin/winningLetterMainPage?id=' + o.id + '&actionType=Edit' + '&isExpired=' + isExpired)
								.html(o.name);

				if (isExpired == 'True') {
					// 狀態文字:生效 or 取消
					dataTemplateBody.find('.winningLetterStatus span').html(func_parseStatus(o.status));

					// 狀態文字:已逾期
					dataTemplateBody.find('.winningLetterStatus span2').html("(已逾期)");
					dataTemplateBody.find('.winningLetterStatus span2').css('color', 'red');

					// <br2 />
					dataTemplateBody.find('.winningLetterStatus br2').remove();

					// 狀態按鈕:取消
					dataTemplateBody.find('.btn_status').remove();
				} else {
					// 狀態文字:生效 or 取消
					dataTemplateBody.find('.winningLetterStatus span').html(func_parseStatus(o.status));

					// 狀態文字:已逾期
					dataTemplateBody.find('.winningLetterStatus span2').remove();

					// 狀態按鈕:取消
					dataTemplateBody.find('.btn_status').val(func_parseButtonStatus(pageStatus));
					dataTemplateBody.find('.btn_status').attr('winningLetterId', o.id);
					dataTemplateBody.find('.btn_status').click(func_updateStatusButton);
				}

				if (o.stats == 'Inactive') {
					// 狀態按鈕:取消
					dataTemplateBody.find('.btn_status').remove();
				}

				// 填寫人數
				func_countReplyPeople(dataTemplateBody, o.id, o.name);

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

				// 刪除
				dataTemplateBody.find('.btn_detele').attr('winningLetterId', o.id).click(func_deteleWinningLetter);

				$('#tableBody').append(dataTemplateBody);

			});

			keywordInput.value = keywordValue;

			$('.LyMain').unblock();
		}).fail(function(response) {
			console.info(response);
			$.FailResponse(response);

			$('.LyMain').unblock();
		})
	};

	loadDataFunc();
});