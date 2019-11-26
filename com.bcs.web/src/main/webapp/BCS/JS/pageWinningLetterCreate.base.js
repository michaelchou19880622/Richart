/**
 * 
 */
$(function(){
	var campaignId = $.urlParam("campaignId");
	var actionType = $.urlParam("actionType") || 'Create';
	console.info('actionType', actionType);
	var from = $.urlParam("from") || 'disable';
	console.info('from', from);
	
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
	var optionSelectChange_func = function(){
		var selectValue = $(this).find('option:selected').text();
		$(this).closest('.option').find('.optionLabel').html(selectValue);
	};
	
	$('.optionSelect').change(optionSelectChange_func);

	// 日期元件
//	$(".datepicker").datepicker({ 'dateFormat' : 'yy-mm-dd'});
	// Original: must after today
	$(".datepicker").datepicker({ 'minDate' : 0, 'dateFormat' : 'yy-mm-dd'});
	
	// 取消
	$('.btn_cancel').click(function(){

		var r = confirm("請確認是否取消");
		if (r) {
			// confirm true
		} else {
		    return;
		}

		if(from == 'disable'){
			window.location.replace(bcs.bcsContextPath + '/edit/shareCampaignListDisablePage');
		}
		else if(from == 'active'){
			window.location.replace(bcs.bcsContextPath + '/edit/shareCampaignListPage');
		}
		else if(from == 'api'){
			window.location.replace(bcs.bcsContextPath + '/edit/shareCampaignListApiPage');
		}
		else{
			window.location.replace(bcs.bcsContextPath + '/edit/shareCampaignListPage');
		}
	});
	
	var getWinningLetterData = function(){

		if (!validator.form()) {
			return;
		}
		
		if(actionType == 'Copy'){
			campaignId = null;
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
				winningLetterName : winningLetterName,
				startDateTime : winningLetterStartDateTime.format(dateFormat),
				endDateTime : winningLetterEndDateTime.format(dateFormat),
				winningLetterGifts : winningLetterGifts
			};
		
		return winningLetterData;
	};
	
	// 儲存按鍵
	$('.btn_save').click(function(){

		if(actionType == "Read"){
			location.reload();
			return;
		}
		
		if (!validator.form()) {
			return;
		}
		
		var postData = getWinningLetterData();
		
		console.info('postData', postData);
		
		if (!confirm(actionType == 'Create' ? '請確認是否建立' : '請確認是否儲存')) {
			return false;
		}
		
		alert('儲存測試123');

//		$('.LyMain').block($.BCS.blockMsgSave);
//		$.ajax({
//			type : "POST",
//			url : bcs.bcsContextPath + '/edit/createWinningLetter',
//            cache: false,
//            contentType: 'application/json',
//            processData: false,
//			data : JSON.stringify(postData)
//		}).success(function(response){
//			console.info(response);
//			alert( '儲存成功');
//
//			if(from == 'disable'){
//				window.location.replace(bcs.bcsContextPath + '/edit/shareCampaignListDisablePage');
//			}
//			else if(from == 'active'){
//				window.location.replace(bcs.bcsContextPath + '/edit/shareCampaignListPage');
//			}
//			else if(from == 'api'){
//				window.location.replace(bcs.bcsContextPath + '/edit/shareCampaignListApiPage');
//			}
//			else{
//				window.location.replace(bcs.bcsContextPath + '/edit/shareCampaignListPage');
//			}
//		}).fail(function(response){
//			console.info(response);
//			$.FailResponse(response);
//			$('.LyMain').unblock();
//		}).done(function(){
//			$('.LyMain').unblock();
//		});
	});
	
//	var loadDataFunc = function(){
//		var linePointSerialId = "";
//		var linePointShow = true;
//		
//		if (campaignId) {
//			
//			$.ajax({
//				type : "GET",
//				url : bcs.bcsContextPath + '/edit/getShareCampaign?campaignId=' + campaignId
//			}).success(function(response){
//				console.info("getShareCampaign's response:", response);
//				
//				// 活動標題
//				$('#winningLetterName').val(response.campaignName);
//
//				// 分享次數
//				$('#shareTimes').val(response.shareTimes);
//				
//				// 是否判斷加好友/綁定
//				if(response.judgement == 'DISABLE')
//					$('input[name="judgement"]')[0].checked = true;
//				else if (response.judgement == 'FOLLOW')
//					$('input[name="judgement"]')[1].checked = true;
//				else
//					$('input[name="judgement"]')[2].checked = true;
//				
//				// 是否自動送點
//				if(response.autoSendPoint == true || response.autoSendPoint == 'true'){
//					$('input[name="autoSendPoint"]')[0].checked = true;
//					linePointShow = true;
//				}else{ 
//					$('input[name="autoSendPoint"]')[1].checked = true;
//					linePointShow = false;
//				}
//				// 選擇發送Lint Points活動
//				linePointSerialId = response.linePointSerialId;
//				
//				// 活動圖片
//				$('#actionImgId').val(response.actionImgReferenceId);
//				if(response.actionImgReferenceId){
//					$('#actionImg').attr("src", bcs.bcsContextPath + '/getResource/IMAGE/' + response.actionImgReferenceId);
//				}
//				
//				// 活動圖片連結
//				$('#actionImgUrl').val(response.actionImgUrl);
//				
//				// 分享圖片
//				$('#shareImgId').val(response.shareImgReferenceId);
//				if(response.shareImgReferenceId){
//		            $('#shareImg').attr("src", bcs.bcsContextPath + '/getResource/IMAGE/' + response.shareImgReferenceId);
//				}
//
//				// 分享圖片連結
//				//$('#shareImgUrl').val(response.shareImgUrl);
//				
//				// 說明圖片
//				$('#descriptionImgId').val(response.descriptionImgReferenceId);
//				if(response.descriptionImgReferenceId){
//		            $('#descriptionImg').attr("src", bcs.bcsContextPath + '/getResource/IMAGE/' + response.descriptionImgReferenceId);
//				}
//
//				// 說明圖片連結
//				$('#descriptionImgUrl').val(response.descriptionImgUrl);
//				
//            	setElementDate('campaignStartTime', response.startTime);
//            	setElementDate('campaignEndTime', response.endTime);
//
//            	$('#campaignShareMsg').val(response.shareMsg);
//            	
//            	// 計算字數
//            	$('#winningLetterName').keyup();
//            	$('#campaignShareMsg').keyup();
//			}).fail(function(response){
//				console.info(response);
//				$.FailResponse(response);
//			}).done(function(){
//				getAutoLinePointMainList(linePointSerialId, linePointShow);
//			});
//
//			if(actionType == "Read"){
//				$('.btn_save').remove();
//				$('#campaignTitle').attr('disabled',true);
//				$('.MdBtnUpload').remove();
//				$('#campaignStartTime').attr('disabled',true);
//				$('#campaignStartTimeHour').attr('disabled',true);
//				$('#campaignStartTimeMinute').attr('disabled',true);
//				$('#campaignEndTime').attr('disabled',true);
//				$('#campaignEndTimeHour').attr('disabled',true);
//				$('#campaignEndTimeMinute').attr('disabled',true);
//				$('#campaignShareMsg').attr('disabled',true);
//			}
//		}else{
//			// From Create Page
//			$('input[name="judgement"]')[0].checked = true;
//			$('input[name="autoSendPoint"]')[0].checked = true;
//			getAutoLinePointMainList(linePointSerialId, linePointShow);
//		}
//	};
//
//	var getAutoLinePointMainList = function(linePointSerialId, linePointShow){
//		console.info("linePointShow:", linePointShow);
//		// linePointShow
//		if(linePointShow){
//			$('#LinePointView').show();
//		}else{
//			$('#LinePointView').hide();
//		}
//		
//		// Line Point Main List
//		var selectedValue = "";
//		console.info("linePointSerialId", linePointSerialId);
//		
//		$.ajax({
//			type : "GET",
//			url : bcs.bcsContextPath + '/market/getUndoneAutoLinePointMainList'
//		}).success(function(response){
//			console.info('getLinePointList response:' + JSON.stringify(response));
//
//			var mainList = document.getElementById("mainList");
//			$.each(response, function(i, o){		
//				console.info('getLinePointList o:' + JSON.stringify(o));
//				 var opt = document.createElement('option');
//				 opt.value = o.id;
//				 opt.innerHTML = o.serialId; // + ' (' + o.title + ')';	
//				 console.info("o.serialId", o.serialId);
//				 if(linePointSerialId != null && o.serialId == linePointSerialId){
//					 selectedValue = opt.value;
//					 console.info("selectedValue", selectedValue);
//				 }
//				mainList.appendChild(opt);
//			});
//			$('#mainList').val(selectedValue);
//			
//		}).fail(function(response){
//			console.info(response);
//			$.FailResponse(response);
//		}).done(function(){
//		});		
//	};
//	
//	var imgUploadFormat = {
//			actionImageUpload : {rightWidth : 'eq750'},
//			shareImageUpload : {rightWidth : 'eq750'},
//			descriptionImageUpload : {rightWidth : 'eq750'}
//	};
//	
//	// 上傳活動圖片
//	$('#actionImageUpload').on("change", function(event){
//
//		if(actionType == "Read"){
//			location.reload();
//			return;
//		}
//		
//		if (!validator.element(this)) {
//			return false;
//		}
//		
//		var input = event.currentTarget;
//		
//    	if (input.files && input.files[0]) {
//    		if(input.files[0].size < 1048576) {
//	    		var fileName = input.files[0].name;
//	    		console.info("fileName : " + fileName);
//	    		var form_data = new FormData();
//	    		
//	    		var ajaxUrl = bcs.bcsContextPath + "/edit/createResource?resourceType=IMAGE";
//	    		
//	    		var thisImgUploadFormat = imgUploadFormat[$(input).attr('name')];
//	    		if(thisImgUploadFormat){
//	    			ajaxUrl = bcs.bcsContextPath + "/edit/createResource?resourceType=IMAGE&rightContentType=" + $(input).attr('accept');
//	    			if(thisImgUploadFormat.rightWidth){
//	    				ajaxUrl += "&rightWidth=" + thisImgUploadFormat.rightWidth;
//	    			}
//	    			if(thisImgUploadFormat.rightHeight){
//	    				ajaxUrl += "&rightHeight=" + thisImgUploadFormat.rightHeight;
//	    			}
//	    			if(thisImgUploadFormat.rightSize){
//	    				ajaxUrl += "&rightSize=" + thisImgUploadFormat.rightSize;
//	    			}
//	    		}
//	    		
//	    		form_data.append("filePart",input.files[0]);
//	
//	    		$('.LyMain').block($.BCS.blockMsgUpload);
//	    		$.ajax({
//	                type: 'POST',
//	                url: ajaxUrl,
//	                cache: false,
//	                contentType: false,
//	                processData: false,
//	                data: form_data
//	    		}).success(function(response){
//	            	console.info(response);
//	            	
//	            	if(typeof response === 'string' && response.indexOf("WARING") == 0){
//	            		alert(response);
//	            		$(input).val('');
//	            		return;
//	            	}
//	            	
//	            	alert("上傳成功!");
//	            	$('#actionImgId').val(response.resourceId);
//	            	$('#actionImg').attr("src", bcs.bcsContextPath + '/getResource/IMAGE/' + response.resourceId);
//	    		}).fail(function(response){
//	    			console.info(response);
//	    			$.FailResponse(response);
//	    			$('.LyMain').unblock();
//	    		}).done(function(){
//	    			$('.LyMain').unblock();
//	    		});
//    		} else {
//    			alert("圖片大小不可大於 1MB！");
//    		}
//        } 
//	});
//	
//	// 上傳分享圖片
//	$('#shareImageUpload').on("change", function(event){
//
//		if(actionType == "Read"){
//			location.reload();
//			return;
//		}
//		
//		if (!validator.element(this)) {
//			return false;
//		}
//		
//		var input = event.currentTarget;
//		
//    	if (input.files && input.files[0]) {
//    		if(input.files[0].size < 1048576) {
//	    		var fileName = input.files[0].name;
//	    		console.info("fileName : " + fileName);
//	    		var form_data = new FormData();
//	    		
//	    		var ajaxUrl = bcs.bcsContextPath + "/edit/createResource?resourceType=IMAGE";
//	    		
//	    		var thisImgUploadFormat = imgUploadFormat[$(input).attr('name')];
//	    		if(thisImgUploadFormat){
//	    			ajaxUrl = bcs.bcsContextPath + "/edit/createResource?resourceType=IMAGE&rightContentType=" + $(input).attr('accept');
//	    			if(thisImgUploadFormat.rightWidth){
//	    				ajaxUrl += "&rightWidth=" + thisImgUploadFormat.rightWidth;
//	    			}
//	    			if(thisImgUploadFormat.rightHeight){
//	    				ajaxUrl += "&rightHeight=" + thisImgUploadFormat.rightHeight;
//	    			}
//	    			if(thisImgUploadFormat.rightSize){
//	    				ajaxUrl += "&rightSize=" + thisImgUploadFormat.rightSize;
//	    			}
//	    		}
//	    		
//	    		form_data.append("filePart",input.files[0]);
//	
//	    		$('.LyMain').block($.BCS.blockMsgUpload);
//	    		$.ajax({
//	                type: 'POST',
//	                url: ajaxUrl,
//	                cache: false,
//	                contentType: false,
//	                processData: false,
//	                data: form_data
//	    		}).success(function(response){
//	            	console.info(response);
//	            	
//	            	if(typeof response === 'string' && response.indexOf("WARING") == 0){
//	            		alert(response);
//	            		$(input).val('');
//	            		return;
//	            	}
//	            	
//	            	alert("上傳成功!");
//	            	$('#shareImgId').val(response.resourceId);
//	            	$('#shareImg').attr("src", bcs.bcsContextPath + '/getResource/IMAGE/' + response.resourceId);
//	    		}).fail(function(response){
//	    			console.info(response);
//	    			$.FailResponse(response);
//	    			$('.LyMain').unblock();
//	    		}).done(function(){
//	    			$('.LyMain').unblock();
//	    		});
//    		} else {
//    			alert("圖片大小不可大於 1MB！");
//    		}
//        } 
//	});
//	
//	// 上傳說明圖片
//	$('#descriptionImageUpload').on("change", function(event){
//
//		if(actionType == "Read"){
//			location.reload();
//			return;
//		}
//		
//		if (!validator.element(this)) {
//			return false;
//		}
//		
//		var input = event.currentTarget;
//		
//    	if (input.files && input.files[0]) {
//    		if(input.files[0].size < 1048576) {
//	    		var fileName = input.files[0].name;
//	    		console.info("fileName : " + fileName);
//	    		var form_data = new FormData();
//	    		
//	    		var ajaxUrl = bcs.bcsContextPath + "/edit/createResource?resourceType=IMAGE";
//	    		
//	    		var thisImgUploadFormat = imgUploadFormat[$(input).attr('name')];
//	    		if(thisImgUploadFormat){
//	    			ajaxUrl = bcs.bcsContextPath + "/edit/createResource?resourceType=IMAGE&rightContentType=" + $(input).attr('accept');
//	    			if(thisImgUploadFormat.rightWidth){
//	    				ajaxUrl += "&rightWidth=" + thisImgUploadFormat.rightWidth;
//	    			}
//	    			if(thisImgUploadFormat.rightHeight){
//	    				ajaxUrl += "&rightHeight=" + thisImgUploadFormat.rightHeight;
//	    			}
//	    			if(thisImgUploadFormat.rightSize){
//	    				ajaxUrl += "&rightSize=" + thisImgUploadFormat.rightSize;
//	    			}
//	    		}
//	    		
//	    		form_data.append("filePart",input.files[0]);
//	
//	    		$('.LyMain').block($.BCS.blockMsgUpload);
//	    		$.ajax({
//	                type: 'POST',
//	                url: ajaxUrl,
//	                cache: false,
//	                contentType: false,
//	                processData: false,
//	                data: form_data
//	    		}).success(function(response){
//	            	console.info(response);
//	            	
//	            	if(typeof response === 'string' && response.indexOf("WARING") == 0){
//	            		alert(response);
//	            		$(input).val('');
//	            		return;
//	            	}
//	            	
//	            	alert("上傳成功!");
//	            	$('#descriptionImgId').val(response.resourceId);
//	            	$('#descriptionImg').attr("src", bcs.bcsContextPath + '/getResource/IMAGE/' + response.resourceId);
//	    		}).fail(function(response){
//	    			console.info(response);
//	    			$.FailResponse(response);
//	    			$('.LyMain').unblock();
//	    		}).done(function(){
//	    			$('.LyMain').unblock();
//	    		});
//    		} else {
//    			alert("圖片大小不可大於 1MB！");
//    		}
//        } 
//	});

//	loadDataFunc();
});