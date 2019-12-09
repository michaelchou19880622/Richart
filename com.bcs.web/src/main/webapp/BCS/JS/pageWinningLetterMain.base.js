/**
 * 
 */
$(function() {
	
	var isExpired = $.urlParam("isExpired") || 'False';
	console.info('isExpired = ', isExpired);

	var actionType = $.urlParam("actionType") || 'Create';
	console.info('actionType = ', actionType);

	var btn_create_save = document.getElementById('btn_create_save');
	btn_create_save.value = (actionType == 'Create' ? '建立' : '儲存');
	
	title.innerText = (actionType == 'Create' ? '建立中獎回函' : '編輯中獎回函')
	subTitle.innerText = (actionType == 'Create' ? '請輸入中獎回函內容。' : (isExpired == 'True' ? "該中獎回函已過期，無法重新進行編輯。" : "請重新修改中獎回函內容，修改完畢後請確認儲存。"))
	subTitle.style.color = (isExpired == 'True' ? "red" : "gray")
	
	var winningLetterName = document.getElementById('winningLetterName');
	var winningLetterGifts = document.getElementById('winningLetterGifts');
	
	var winningLetterNameValue = $.urlParam("winningLetterNameValue") || '';
	console.info('winningLetterNameValue = ', winningLetterNameValue);
	
	var winningLetterGiftsValue = $.urlParam("winningLetterGiftsValue") || '';
	console.info('winningLetterGiftsValue = ', winningLetterGiftsValue);

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
				dateYYYYMMDD : true,
				compareDate : {
					compareType : 'after',
					dateFormat : dateFormat,
					getThisDateStringFunction : function() {
						var yearMonthDay = $('#winningLetterEndTime').val();
						var hour = $('#winningLetterEndTimeHour').val();
						var minute = $('#winningLetterEndTimeMinute').val();
						return yearMonthDay + ' ' + hour + ':' + minute + ':00';
					},
					getAnotherDateStringFunction : function() {
						var yearMonthDay = $('#winningLetterStartTime').val();
						var hour = $('#winningLetterStartTimeHour').val();
						var minute = $('#winningLetterStartTimeMinute').val();
						return yearMonthDay + ' ' + hour + ':' + minute + ':00';
					},
					thisDateName : '回覆到期結束時間',
					anotherDateName : '回覆到期開始時間'
				}
			}
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
			console.info('btn_create_save.className = ', btn_create_save.className);

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

			if (!confirm(actionType == 'Create' ? '請確認是否建立' : '請確認是否儲存')) {
				return false;
			}
			
			var apiUrl = (actionType == 'Create')? "/api/createWinningLetter" : "/api/editWinningLetter";

			$('.LyMain').block($.BCS.blockWinningLetterCreating);
			$.ajax({
				type : "POST",
				url : bcs.bcsContextPath + apiUrl,
	            cache: false,
	            contentType: 'application/json',
	            processData: false,
				data : JSON.stringify(postData)
			}).done(function(response){
				console.info('response = ' + response);
				
				alert((actionType == 'Create')? '中獎回函建立完成' : '中獎回函已更新');
				$('.LyMain').unblock();

			}).fail(function(response){
				console.info('response = ' + response.responseText);
				
				alert(response.responseText);
				$('.LyMain').unblock();
			})
		});

		$("#btn_cancel").click(function() {

			var confirmRslt = confirm('是否確定取消?');
			
			if (!confirmRslt) {
				return;
			}
			
			// If confirmed, do cancle and return the previous page
			alert('取消確認123')
			
		});
	});

	var loadDataFunc = function() {
		console.info('loadDataFunc --- start');

		winningLetterName.disabled = (actionType == 'Edit')? true : false;
		winningLetterName.style.color = (actionType == 'Edit')? 'silver' : 'black';
		
		if (isExpired == 'True') {
			winningLetterGifts.disabled = true;
			btn_create_save.className = "btn_cancel";
			winningLetterGifts.style.color = 'silver';
		} else {
			winningLetterGifts.disabled = false;
			btn_create_save.className = "btn_save";
			winningLetterGifts.style.color = 'black';
		}
		
		winningLetterName.value = winningLetterNameValue;
		winningLetterGifts.value = winningLetterGiftsValue;
		
		console.info('loadDataFunc --- end');
	};

	loadDataFunc();
});