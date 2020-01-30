/**
 * 
 */
$(function() {
	/* Get URL Referrer */
	var urlRef = $('#urlReferrer').val();
	console.info('urlRef = ', urlRef);

	if (urlRef == null || urlRef.length == 0) {
		alert("對不起，您不能直接更改URL來訪問網頁，你的操作非法。");
		window.location.replace(bcs.bcsContextPath + '/admin/winningLetterMainPage');
		return
	}
	
	var winningLetterId = $.urlParam("id");
	console.info('winningLetterId = ', winningLetterId);

	var isExpired = $.urlParam("isExpired") || 'False';
	console.info('isExpired = ', isExpired);

	var actionType = $.urlParam("actionType") || 'Create';
	console.info('actionType = ', actionType);

	var btn_create_save = document.getElementById('btn_create_save');
	btn_create_save.value = ((actionType == 'Edit') ? '儲存' : '建立');
	
	var desc_btn = document.getElementById('desc_btn');
	
	if (isExpired == 'True') {
		btn_create_save.remove();
		desc_btn.remove();
	}

	var btn_cancel = document.getElementById('btn_cancel');
	btn_cancel.style.visibility = ((actionType == 'Create') ? 'hidden' : 'visible');
	btn_cancel.value = ((isExpired == 'True') ? '返回' : '取消');

	title.innerText = ((actionType == 'Create') ? '建立中獎回函' : ((actionType == 'Copy') ? '建立中獎回函 ( 複製 )' : '編輯中獎回函'))
	subTitle.innerText = ((actionType == 'Create' || actionType == 'Copy') ? '請輸入中獎回函內容。' : ((isExpired == 'True') ? "該中獎回函活動已逾期，無法重新進行編輯。" : "請重新修改中獎回函內容 ( 「名稱」無法修改 )，若活動需延期，請在填寫時間截止前修改「結束時間」，修改完畢後請確認儲存。"))
	subTitle.innerText = ((actionType != 'Copy') ? subTitle.innerText : "請輸入中獎回函內容。")
	subTitle.style.color = ((actionType == 'Copy') ? "gray" : ((isExpired == 'True') ? "red" : "gray"))

	var winningLetterName = document.getElementById('winningLetterName');
	var winningLetterGifts = document.getElementById('winningLetterGifts');
	
	var optionLabelTimePicker = document.getElementsByClassName('optionLabel');

	var dateFormat = "YYYY-MM-DD HH:mm:ss";

	// 表單驗證
	var validator = $('#formContentWinningLetter').validate({
		rules : {

			// 中獎回函名稱
			'winningLetterName' : {
				required : true,
				maxlength : 50
			},

			// 中獎贈品
			'winningLetterGifts' : {
				required : true,
				maxlength : 100
			},

			// 回覆到期時間-開始日期
			'winningLetterStartTime' : {
				required : true,
				dateYYYYMMDD : true
			},

			// 回覆到期時間-結束日期
			'winningLetterEndTime' : {
				required : true,
				dateYYYYMMDD : true
			}

//			// 回覆到期時間-結束日期
//			'winningLetterEndTime' : {
//				required : true,
//				dateYYYYMMDD : true,
//				compareDate : {
//					compareType : 'after',
//					dateFormat : dateFormat,
//					getThisDateStringFunction : function() {
//						var yearMonthDay = $('#winningLetterEndTime').val();
//						var hour = $('#winningLetterEndTimeHour').val();
//						var minute = $('#winningLetterEndTimeMinute').val();
//						return yearMonthDay + ' ' + hour + ':' + minute + ':00';
//					},
//					getAnotherDateStringFunction : function() {
//						var yearMonthDay = $('#winningLetterStartTime').val();
//						var hour = $('#winningLetterStartTimeHour').val();
//						var minute = $('#winningLetterStartTimeMinute').val();
//						return yearMonthDay + ' ' + hour + ':' + minute + ':00';
//					},
//					thisDateName : '填寫結束時間',
//					anotherDateName : '填寫開始時間'
//				}
//			}
		}
	});

	// 計算輸入框輸入字數
	var countTextForTextInput = function() {
		$('#winningLetterName').keyup(function() {
			var txtLength = $(this).val().length;
			var tr = $(this).closest("tr");
			var inputCount = tr.find(".MdTxtInputCount");
			var countText = inputCount.text();
			inputCount.text(countText.replace(/\d+\//, txtLength + '/'));
		});

		$("#winningLetterGifts").keyup(function(e) {
			var txtLength = $(this).val().length;
			var tr = $(this).closest("tr");
			var inputCount = tr.find(".MdTxtInputCount");
			var countText = inputCount.text();
			inputCount.text(countText.replace(/\d+\//, txtLength + '/'));
		});

	};

	countTextForTextInput();

	/**
	 * 從欄位取得日期(型態是 Moment.js 的 date wraps)
	 */
	var getDateTimeByElement = function(elementId) {
		var yearMonthDay = $('#' + elementId).val();
		var hour = $('#' + elementId + 'Hour').val();
		var minute = $('#' + elementId + 'Minute').val();
		var dateTime = moment(yearMonthDay + ' ' + hour + ':' + minute + ':00', dateFormat);
		return dateTime;
	}

	/**
	 * 設定日期時間欄位值
	 */
	var setElementDate = function(elementId, timestamp) {
		if (!timestamp) {
			return;
		}

		var momentDate = moment(timestamp);
		$('#' + elementId).val(momentDate.format('YYYY-MM-DD'));

		var hour = momentDate.hour();
		$('#' + elementId + 'Hour').val(hour < 10 ? '0' + hour : hour).change();

		var minute = momentDate.minute();
		$('#' + elementId + 'Minute').val(minute < 10 ? '0' + minute : minute).change();
	}

	// 下拉選項
	var optionSelectChange_func = function() {
		var selectValue = $(this).find('option:selected').text();
		$(this).closest('.option').find('.optionLabel').html(selectValue);
	};

	$('.optionSelect').change(optionSelectChange_func);

	// 日期元件
	// $(".datepicker").datepicker({ 'dateFormat' : 'yy-mm-dd'});
	// Original: must after today
	$(".datepicker").datepicker({
		'minDate' : 0,
		'dateFormat' : 'yy-mm-dd'
	});

	/* 設定日期時間欄位值 */
	var func_setElementDateTime = function(elementId, timestamp) {
		if (!timestamp) {
			return;
		}

		var momentDate = moment(timestamp);
		$('#' + elementId).val(momentDate.format('YYYY-MM-DD'));

		var hour = momentDate.hour();
		$('#' + elementId + 'Hour').val(hour < 10 ? '0' + hour : hour).change();

		var minute = momentDate.minute();
		$('#' + elementId + 'Minute').val(minute < 10 ? '0' + minute : minute).change();
	}

	/* 設定日期時間欄位是否禁用 */
	var func_setElementDateTimeDisabled = function(elementId, isDisable) {
		console.info('elementId = ', elementId, ', isDisable = ', isDisable);
		
		document.getElementById(elementId).disabled = isDisable;
		document.getElementById(elementId).style.color = (isDisable)? '#a1a7b5' : 'black';
		
		document.getElementById(elementId + 'Hour').disabled = isDisable;
		document.getElementById(elementId + 'Hour').style.color = (isDisable)? '#a1a7b5' : 'black';
		
		document.getElementById(elementId + 'Minute').disabled = isDisable;
		document.getElementById(elementId + 'Minute').style.color = (isDisable)? '#a1a7b5' : 'black';
	}

	/* 設定時間 */
	var func_parseDateTime = function(datetime) {
		if (null == datetime) {
			return "-";
		} 
		
		return moment(datetime).format('YYYY-MM-DD HH:mm');
	}

	/* 檢查活動時間是否有效 */
	var func_checkIsActiveTimeValid = function(datetime) {
		console.info("datetime = ", datetime);
		
		var currentDateTime = new Date();
		console.info("currentDateTime = ", currentDateTime);
		
		currentDateTime = func_parseDateTime(currentDateTime);
		console.info("formated currentDateTime = ", currentDateTime);
				
		if (moment(currentDateTime).format(dateFormat) > datetime) {
			console.info("Active time is invalid");
			return 'False';
		} else {
			console.info("Active time is valid");
			return 'True';
		}
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

	var getWinningLetterData = function() {

		if (!validator.form()) {
			return;
		}

		// 中獎回函名稱
		var winningLetterName = $('#winningLetterName').val();

		// 回覆到期時間 - 開始日期+時間
		var winningLetterStartDateTime = getDateTimeByElement('winningLetterStartTime');

		// 回覆到期時間 - 結束日期+時間
		var winningLetterEndDateTime = getDateTimeByElement('winningLetterEndTime');

		// 中獎贈品
		var winningLetterGifts = $('#winningLetterGifts').val();

		var winningLetterData = {
			'name' : winningLetterName,
			'startTime' : winningLetterStartDateTime.format(dateFormat),
			'endTime' : winningLetterEndDateTime.format(dateFormat),
			'gift' : winningLetterGifts,
			'status' : "Active"
		};

		return winningLetterData;
	};

	$(document).ready(function() {
		$("#btn_create_save").click(function() {
			if (isExpired == 'True') {
				alert("The winning letter is expired, can not edit anymore.");
				return;
			}
			
			if (!validator.form()) {
				return;
			}

			var postData = getWinningLetterData();
			console.info('postData = ', postData);

			console.info('name = ', postData['name']);
			console.info('startTime = ', postData['startTime']);
			console.info('endTime = ', postData['endTime']);
			console.info('gift = ', postData['gift']);
			console.info('status = ', postData['status']);

			console.info('JSON.stringify(postData) = ', JSON.stringify(postData));
			
			var isActiveStartTimeValid = func_checkIsActiveTimeValid(postData['startTime']);
			if (isActiveStartTimeValid == 'False') {
				alert("時間設定錯誤，中獎回函填寫「開始時間」必須大於「當前時間」。");
				return false;
			}
			
			if (postData['endTime'] <= postData['startTime']) {
				alert("時間設定錯誤，中獎回函填寫「結束時間」必須大於「開始時間」。");
				return false;
			}

			if (!confirm((actionType == 'Create' || actionType == 'Copy') ? '請確認是否建立' : '請確認是否儲存')) {
				return false;
			}

			var apiUrl = (actionType == 'Create' || actionType == 'Copy') ? "/api/createWinningLetter" : "/api/editWinningLetter";

			if (actionType == 'Create' || actionType == 'Copy'){
				$('.LyMain').block($.BCS.blockWinningLetterCreating);
			}
			else{
				$('.LyMain').block($.BCS.blockWinningLetterUpdating);
			}
			
			$.ajax({
				type : "POST",
				url : bcs.bcsContextPath + apiUrl,
				cache : false,
				contentType : 'application/json',
				processData : false,
				data : JSON.stringify(postData)
			}).done(function(response) {
				console.info('response = ' + response);

				alert((actionType == 'Create' || actionType == 'Copy') ? '中獎回函建立完成' : '中獎回函已更新');
				$('.LyMain').unblock();

				window.location.replace(bcs.bcsContextPath + '/admin/winningLetterListPage');

			}).fail(function(response) {
				console.info('response = ' + response.responseText);

				alert(response.responseText);
				$('.LyMain').unblock();
			})
		});

		$("#btn_cancel").click(function() {

			var confirmRslt = confirm('是否確定' + btn_cancel.value + '?');

			if (!confirmRslt) {
				return;
			}

			// If confirmed, do cancle and return the previous page
			window.location.replace(bcs.bcsContextPath + '/admin/winningLetterListPage');

		});
	});

	var loadDataFunc = function() {
		console.info('loadDataFunc --- start');

		winningLetterName.disabled = (actionType == 'Edit') ? true : false;
		winningLetterName.style.color = (actionType == 'Edit') ? 'silver' : 'black';
		

		if (isExpired == 'True') {
			winningLetterGifts.disabled = true;
			btn_create_save.style.visibility = 'hidden';
			winningLetterGifts.style.color = 'silver';
			func_setElementDateTimeDisabled('winningLetterStartTime', true);
			func_setElementDateTimeDisabled('winningLetterEndTime', true);
			
			for (i = 0, len = optionLabelTimePicker.length; i < len; i++) {
				optionLabelTimePicker[i].style.color = '#a1a7b5';
			}
		} else {
			winningLetterGifts.disabled = false;
			btn_create_save.style.visibility = 'visible';
			winningLetterGifts.style.color = 'black';
			func_setElementDateTimeDisabled('winningLetterStartTime', false);
			func_setElementDateTimeDisabled('winningLetterEndTime', false);
			
			for (i = 0, len = optionLabelTimePicker.length; i < len; i++) {
				optionLabelTimePicker[i].style.color = 'black';
			}
		}
		
		if (winningLetterId) {

			$('.LyMain').block($.BCS.blockWinningLetterLoading);
			$.ajax({
				type : "GET",
				url : bcs.bcsContextPath + "/edit/getWinningLetter?winningLetterId=" + winningLetterId
			}).done(function(response) {
				console.info(response);

				winningLetterName.value = response.name;
				winningLetterGifts.value = response.gift;

				func_setElementDateTime('winningLetterStartTime', response.startTime);
				func_setElementDateTime('winningLetterEndTime', response.endTime);

				$('#winningLetterName').keyup();
				$('#winningLetterGifts').keyup();
				$('.LyMain').unblock();

			}).fail(function(response) {
				console.info(response);
				alert(response.responseText);
				$('.LyMain').unblock();
			})
		}

		console.info('loadDataFunc --- end');
	};
	
	
	loadDataFunc();
});