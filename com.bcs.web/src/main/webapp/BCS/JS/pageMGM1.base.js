/**
 * 
 */
$(function(){
    /* Get URL Referrer */
    var urlRef = $('#urlReferrer').val();

    if (urlRef == null || urlRef.length == 0) {
        alert("對不起，您不能直接更改URL來訪問網頁，你的操作非法。");
        window.location.replace(bcs.bcsContextPath + '/edit/shareCampaignCreatePage');
        return
    }
	
	var campaignId = $.urlParam("campaignId");
	var actionType = $.urlParam("actionType") || 'Create';
	console.info('actionType', actionType);
	
	var from = $.urlParam("from") || 'disable';
	console.info('from', from);
	
	var isEditable = $('#isEditable').val();
	console.info('isEditable', isEditable);
	
    if (isEditable == 'false') {
        $('#campaignTimeNotification').show();
        $('#shareTimesNotification').show();
        $('#judgementNotification').show();
        $('#autoSendPointNotification').show();
    	
        $('input[name=campaignStartTime]').attr("disabled",true);
        $('select[name=campaignStartTimeHour]').attr("disabled",true);
        $('select[name=campaignStartTimeMinute]').attr("disabled",true);
        
        $('input[name=campaignEndTime]').attr("disabled",true);
        $('select[name=campaignEndTimeHour]').attr("disabled",true);
        $('select[name=campaignEndTimeMinute]').attr("disabled",true);
        
        $('input[name=judgement]').attr("disabled",true);
        $('input[name=shareTimes]').attr("disabled",true);
        $('input[name=autoSendPoint]').attr("disabled",true);
        
        $('select[name=mainList]').attr("disabled",true);
    } else {
        $('#campaignTimeNotification').hide();
        $('#shareTimesNotification').hide();
        $('#judgementNotification').hide();
        $('#autoSendPointNotification').hide();
        
        $('input[name=campaignStartTime]').attr("disabled",false);
        $('select[name=campaignStartTimeHour]').attr("disabled",false);
        $('select[name=campaignStartTimeMinute]').attr("disabled",false);
        
        $('input[name=campaignEndTime]').attr("disabled",false);
        $('select[name=campaignEndTimeHour]').attr("disabled",false);
        $('select[name=campaignEndTimeMinute]').attr("disabled",false);
        
        $('input[name=judgement]').attr('disabled', false);
        $('input[name=shareTimes]').attr("disabled",false);
        $('input[name=autoSendPoint]').attr("disabled",false);
        
        $('select[name=mainList]').attr("disabled",false);
    }
	
	var dateFormat = "YYYY-MM-DD HH:mm:ss";
	
	// 表單驗證
	var validator = $('#formContentCampaign').validate({
		rules : {
			
			// 優惠券標題
			'campaignTitle' : {
				required : true,
				maxlength : 50
			},
			
			// 分享次數
			'shareTimes' : {
				required : true,
				maxlength : 5,
				digits:true
			},
			
			// 活動圖片
			'actionImageUpload' : {
				required : '#actionImgId:blank'
			},
			
			// 分享圖片
			'shareImageUpload' : {
				required : '#shareImgId:blank'
			},
			
			// 說明圖片
			'descriptionImageUpload' : {
				required : '#descriptionImgId:blank'
			},

			// 活動時間
			'campaignStartTime' : {
				required : true,
				dateYYYYMMDD : true
			},
			
			'campaignStartTimeHour' : {
				required : true
			},
			
			'campaignStartTimeMinute' : {
				required : true
			},
			
			'campaignEndTime' : {
				required : true,
				dateYYYYMMDD : true,
				compareDate : {
					compareType : 'after',
					dateFormat : dateFormat,
					getThisDateStringFunction : function() {
						var yearMonthDay = $('#campaignEndTime').val();
						var hour = $('#campaignEndTimeHour').val();
						var minute = $('#campaignEndTimeMinute').val();		
						return yearMonthDay + ' ' + hour + ':' + minute + ':00';
					},
					getAnotherDateStringFunction : function() {
						var yearMonthDay = $('#campaignStartTime').val();
						var hour = $('#campaignStartTimeHour').val();
						var minute = $('#campaignStartTimeMinute').val();		
						return yearMonthDay + ' ' + hour + ':' + minute + ':00';
					},
					thisDateName : '使用效期結束日期',
					anotherDateName : '使用效期開始日期'
				}
			},
			
			'campaignEndTimeHour' : {
				required : true
			},
			
			'campaignEndTimeMinute' : {
				required : true
			}
		}
	});
	
	// 綁訂計算字數函式到輸入框
	var bindCountTextFunctionToInput = function() {
		$('#campaignTitle').keyup(function() {
			var txtLength = $(this).val().length;
			var tr = $(this).closest("tr");
			var inputCount = tr.find(".MdTxtInputCount");
			var countText = inputCount.text();
			inputCount.text(countText.replace(/\d+\//, txtLength + '/'));
		});
		
		$("#campaignShareMsg").keyup(function(e) {
			var txtLength = $(this).val().length;
			var tr = $(this).closest("tr");
			var inputCount = tr.find(".floatRight");
			var countText = inputCount.text();
			inputCount.text(countText.replace(/\d+\//, txtLength + '/'));
		});

	};
	bindCountTextFunctionToInput();
	
	/**
	 * 從欄位取得日期(型態是 Moment.js 的 date wraps)
	 */
	var getMomentByElement = function(elementId) {
		var yearMonthDay = $('#' + elementId).val();
		var hour = $('#' + elementId + 'Hour').val();
		var minute = $('#' + elementId + 'Minute').val();		
		var momentDate = moment(yearMonthDay + ' ' + hour + ':' + minute + ':00', dateFormat);
		return momentDate;
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
	
	//----------選擇autoSendPoint類型-----------
	$(".autoSendPoint").click(function(e){
		var selectedAutoSendPoint = e.currentTarget.value;
		
		//var curfewView = $("#curfewView");
		switch(selectedAutoSendPoint){
			case 'OFF' :
				$("#LinePointView").hide();
				break;
			case 'ON' :
				$("#LinePointView").show();
				break;
		}
	});
	
	$('.optionSelect').change(optionSelectChange_func);

	// 日期元件
	$(".datepicker").datepicker({ 'dateFormat' : 'yy-mm-dd'});
	// Original: must after today
	//$(".datepicker").datepicker({ 'minDate' : 0, 'dateFormat' : 'yy-mm-dd'});
	
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
	
	var getDateFromUI = function(){

		if (!validator.form()) {
			return;
		}
		
		var campaignId = $.urlParam("campaignId");
		console.info('campaignId', campaignId);
		
		if(actionType == 'Copy'){
			campaignId = null;
		}
		
		// MGM標題
		var campaignTitle = $('#campaignTitle').val();

		// 分享次數
		var shareTimes = $('#shareTimes').val();
		
		// 活動圖片
		var actionImgId = $('#actionImgId').val();
		
		// 分享圖片
		var shareImgId = $('#shareImgId').val();
		
		// 說明圖片
		var descriptionImgId = $('#descriptionImgId').val();
		
		// 活動時間
		var momentCampaignStartTime = getMomentByElement('campaignStartTime');
		var momentCampaignEndTime = getMomentByElement('campaignEndTime');

		// 分享訊息
		var campaignShareMsg = $('#campaignShareMsg').val();

		// -----
		
		
		var actionImgUrl = $('#actionImgUrl').val(); 
		//var shareImgUrl = $('#shareImgUrl').val(); 
		var descriptionImgUrl = $('#descriptionImgUrl').val();
		
		var mainList = document.getElementById("mainList");
		var linePointSerialId = mainList.options[mainList.selectedIndex].text;
		console.info("linePointSerialId", linePointSerialId);
		
		var judgements = $('.judgement');
		var judgement = "";
		if(judgements[0].checked){
			judgement = "DISABLE";
		}else if(judgements[1].checked){
			judgement = "FOLLOW";
		}else if(judgements[2].checked){
			judgement = "BINDED";
		}
		
		var autoSendPoints = $('.autoSendPoint');
		//console.info("a0:", autoSendPoints[0].checked);
		//console.info("a1:", autoSendPoints[1].checked);
		var autoSendPoint = autoSendPoints[0].checked;
		
		console.info("judgement", judgement);
		console.info("autoSendPoint", autoSendPoint);
		
		var shareCampaign = {
				campaignId : campaignId,
				campaignName : campaignTitle,
				shareTimes : shareTimes,
				actionImgReferenceId : actionImgId,
				shareImgReferenceId : shareImgId,
				descriptionImgReferenceId : descriptionImgId,
				startTime : momentCampaignStartTime.format(dateFormat),
				endTime : momentCampaignEndTime.format(dateFormat),
				judgement : judgement,
				autoSendPoint : autoSendPoint,
				actionImgUrl : actionImgUrl,
				//shareImgUrl : shareImgUrl,
				descriptionImgUrl : descriptionImgUrl,
				linePointSerialId : linePointSerialId,
				shareMsg : campaignShareMsg
			};
		return shareCampaign;
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
		
		var postData = getDateFromUI();
		
		console.info('postData', postData);
		console.info('postData.judgement', postData.judgement);
		console.info('postData.autoSendPoint', postData.autoSendPoint);
		
		if (!confirm(actionType == 'Create' ? '請確認是否建立' : '請確認是否儲存')) {
			return false;
		}

		$('.LyMain').block($.BCS.blockMsgSave);
		$.ajax({
			type : "POST",
			url : bcs.bcsContextPath + '/edit/saveShareCampaign',
            cache: false,
            contentType: 'application/json',
            processData: false,
			data : JSON.stringify(postData)
		}).success(function(response){
			console.info(response);
			alert( '儲存成功');

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
		}).fail(function(response){
			console.info(response);
			$.FailResponse(response);
			$('.LyMain').unblock();
		}).done(function(){
			$('.LyMain').unblock();
		});
	});
	
	var loadDataFunc = function(){
		var linePointSerialId = "";
		var linePointShow = true;
		
		if (campaignId) {
			
			$.ajax({
				type : "GET",
				url : bcs.bcsContextPath + '/edit/getShareCampaign?campaignId=' + campaignId
			}).success(function(response){
//				console.info("getShareCampaign's response:", response);
				
				// 活動標題
				$('#campaignTitle').val(response.campaignName);

				// 分享次數
				$('#shareTimes').val(response.shareTimes);
				
				// 是否判斷加好友/綁定
				if(response.judgement == 'DISABLE')
					$('input[name="judgement"]')[0].checked = true;
				else if (response.judgement == 'FOLLOW')
					$('input[name="judgement"]')[1].checked = true;
				else
					$('input[name="judgement"]')[2].checked = true;
				
				// 是否自動送點
				if(response.autoSendPoint == true || response.autoSendPoint == 'true'){
					$('input[name="autoSendPoint"]')[0].checked = true;
					linePointShow = true;
				}else{ 
					$('input[name="autoSendPoint"]')[1].checked = true;
					linePointShow = false;
				}
				// 選擇發送Lint Points活動
				linePointSerialId = response.linePointSerialId;
				
				// 活動圖片
				$('#actionImgId').val(response.actionImgReferenceId);
				if(response.actionImgReferenceId){
					$('#actionImg').attr("src", bcs.bcsContextPath + '/getResource/IMAGE/' + response.actionImgReferenceId);
				}
				
				// 活動圖片連結
				$('#actionImgUrl').val(response.actionImgUrl);
				
				// 分享圖片
				$('#shareImgId').val(response.shareImgReferenceId);
				if(response.shareImgReferenceId){
		            $('#shareImg').attr("src", bcs.bcsContextPath + '/getResource/IMAGE/' + response.shareImgReferenceId);
				}

				// 分享圖片連結
				//$('#shareImgUrl').val(response.shareImgUrl);
				
				// 說明圖片
				$('#descriptionImgId').val(response.descriptionImgReferenceId);
				if(response.descriptionImgReferenceId){
		            $('#descriptionImg').attr("src", bcs.bcsContextPath + '/getResource/IMAGE/' + response.descriptionImgReferenceId);
				}

				// 說明圖片連結
				$('#descriptionImgUrl').val(response.descriptionImgUrl);
				
            	setElementDate('campaignStartTime', response.startTime);
            	setElementDate('campaignEndTime', response.endTime);

            	$('#campaignShareMsg').val(response.shareMsg);
            	
            	// 計算字數
            	$('#campaignTitle').keyup();
            	$('#campaignShareMsg').keyup();
			}).fail(function(response){
				console.info(response);
				$.FailResponse(response);
			}).done(function(){
				getAutoLinePointMainList(linePointSerialId, linePointShow);
			});

			if(actionType == "Read"){
				$('.btn_save').remove();
				$('#campaignTitle').attr('disabled',true);
				$('.MdBtnUpload').remove();
				$('#campaignStartTime').attr('disabled',true);
				$('#campaignStartTimeHour').attr('disabled',true);
				$('#campaignStartTimeMinute').attr('disabled',true);
				$('#campaignEndTime').attr('disabled',true);
				$('#campaignEndTimeHour').attr('disabled',true);
				$('#campaignEndTimeMinute').attr('disabled',true);
				$('#campaignShareMsg').attr('disabled',true);
			}
		}else{
			// From Create Page
			$('input[name="judgement"]')[0].checked = true;
			$('input[name="autoSendPoint"]')[0].checked = true;
			getAutoLinePointMainList(linePointSerialId, linePointShow);
		}
	};

	var getAutoLinePointMainList = function(linePointSerialId, linePointShow){
		console.info("linePointShow:", linePointShow);
		// linePointShow
		if(linePointShow){
			$('#LinePointView').show();
		}else{
			$('#LinePointView').hide();
		}
		
		// Line Point Main List
		var selectedValue = "";
		console.info("linePointSerialId", linePointSerialId);
		
		$.ajax({
			type : "GET",
			url : bcs.bcsContextPath + '/market/getAutoLinePointMainList'
		}).success(function(response){
//			console.info('getLinePointList response:' + JSON.stringify(response));

			var mainList = document.getElementById("mainList");
			$.each(response, function(i, o){		
//				console.info('getLinePointList o:' + JSON.stringify(o));
				 var opt = document.createElement('option');
				 opt.value = o.id;
				 opt.innerHTML = o.serialId; // + ' (' + o.title + ')';	
//				 console.info("o.serialId", o.serialId);
				 if(linePointSerialId != null && o.serialId == linePointSerialId){
					 selectedValue = opt.value;
//					 console.info("selectedValue", selectedValue);
				 }
				mainList.appendChild(opt);
			});
			$('#mainList').val(selectedValue);
			
		}).fail(function(response){
			console.info(response);
			$.FailResponse(response);
		}).done(function(){
		});		
	};
	
	var imgUploadFormat = {
			actionImageUpload : {rightWidth : 'eq750'},
			shareImageUpload : {rightWidth : 'eq750'},
			descriptionImageUpload : {rightWidth : 'eq750'}
	};
	
	// 上傳活動圖片
	$('#actionImageUpload').on("change", function(event){

		if(actionType == "Read"){
			location.reload();
			return;
		}
		
		if (!validator.element(this)) {
			return false;
		}
		
		var input = event.currentTarget;
		
    	if (input.files && input.files[0]) {
    		if(input.files[0].size < 1048576) {
	    		var fileName = input.files[0].name;
	    		console.info("fileName : " + fileName);
	    		var form_data = new FormData();
	    		
	    		var ajaxUrl = bcs.bcsContextPath + "/edit/createResource?resourceType=IMAGE";
	    		
	    		var thisImgUploadFormat = imgUploadFormat[$(input).attr('name')];
	    		if(thisImgUploadFormat){
	    			ajaxUrl = bcs.bcsContextPath + "/edit/createResource?resourceType=IMAGE&rightContentType=" + $(input).attr('accept');
	    			if(thisImgUploadFormat.rightWidth){
	    				ajaxUrl += "&rightWidth=" + thisImgUploadFormat.rightWidth;
	    			}
	    			if(thisImgUploadFormat.rightHeight){
	    				ajaxUrl += "&rightHeight=" + thisImgUploadFormat.rightHeight;
	    			}
	    			if(thisImgUploadFormat.rightSize){
	    				ajaxUrl += "&rightSize=" + thisImgUploadFormat.rightSize;
	    			}
	    		}
	    		
	    		form_data.append("filePart",input.files[0]);
	
	    		$('.LyMain').block($.BCS.blockMsgUpload);
	    		$.ajax({
	                type: 'POST',
	                url: ajaxUrl,
	                cache: false,
	                contentType: false,
	                processData: false,
	                data: form_data
	    		}).success(function(response){
	            	console.info(response);
	            	
	            	if(typeof response === 'string' && response.indexOf("WARING") == 0){
	            		alert(response);
	            		$(input).val('');
	            		return;
	            	}
	            	
	            	alert("上傳成功!");
	            	$('#actionImgId').val(response.resourceId);
	            	$('#actionImg').attr("src", bcs.bcsContextPath + '/getResource/IMAGE/' + response.resourceId);
	    		}).fail(function(response){
	    			console.info(response);
	    			$.FailResponse(response);
	    			$('.LyMain').unblock();
	    		}).done(function(){
	    			$('.LyMain').unblock();
	    		});
    		} else {
    			alert("圖片大小不可大於 1MB！");
    		}
        } 
	});
	
	// 上傳分享圖片
	$('#shareImageUpload').on("change", function(event){

		if(actionType == "Read"){
			location.reload();
			return;
		}
		
		if (!validator.element(this)) {
			return false;
		}
		
		var input = event.currentTarget;
		
    	if (input.files && input.files[0]) {
    		if(input.files[0].size < 1048576) {
	    		var fileName = input.files[0].name;
	    		console.info("fileName : " + fileName);
	    		var form_data = new FormData();
	    		
	    		var ajaxUrl = bcs.bcsContextPath + "/edit/createResource?resourceType=IMAGE";
	    		
	    		var thisImgUploadFormat = imgUploadFormat[$(input).attr('name')];
	    		if(thisImgUploadFormat){
	    			ajaxUrl = bcs.bcsContextPath + "/edit/createResource?resourceType=IMAGE&rightContentType=" + $(input).attr('accept');
	    			if(thisImgUploadFormat.rightWidth){
	    				ajaxUrl += "&rightWidth=" + thisImgUploadFormat.rightWidth;
	    			}
	    			if(thisImgUploadFormat.rightHeight){
	    				ajaxUrl += "&rightHeight=" + thisImgUploadFormat.rightHeight;
	    			}
	    			if(thisImgUploadFormat.rightSize){
	    				ajaxUrl += "&rightSize=" + thisImgUploadFormat.rightSize;
	    			}
	    		}
	    		
	    		form_data.append("filePart",input.files[0]);
	
	    		$('.LyMain').block($.BCS.blockMsgUpload);
	    		$.ajax({
	                type: 'POST',
	                url: ajaxUrl,
	                cache: false,
	                contentType: false,
	                processData: false,
	                data: form_data
	    		}).success(function(response){
	            	console.info(response);
	            	
	            	if(typeof response === 'string' && response.indexOf("WARING") == 0){
	            		alert(response);
	            		$(input).val('');
	            		return;
	            	}
	            	
	            	alert("上傳成功!");
	            	$('#shareImgId').val(response.resourceId);
	            	$('#shareImg').attr("src", bcs.bcsContextPath + '/getResource/IMAGE/' + response.resourceId);
	    		}).fail(function(response){
	    			console.info(response);
	    			$.FailResponse(response);
	    			$('.LyMain').unblock();
	    		}).done(function(){
	    			$('.LyMain').unblock();
	    		});
    		} else {
    			alert("圖片大小不可大於 1MB！");
    		}
        } 
	});
	
	// 上傳說明圖片
	$('#descriptionImageUpload').on("change", function(event){

		if(actionType == "Read"){
			location.reload();
			return;
		}
		
		if (!validator.element(this)) {
			return false;
		}
		
		var input = event.currentTarget;
		
    	if (input.files && input.files[0]) {
    		if(input.files[0].size < 1048576) {
	    		var fileName = input.files[0].name;
	    		console.info("fileName : " + fileName);
	    		var form_data = new FormData();
	    		
	    		var ajaxUrl = bcs.bcsContextPath + "/edit/createResource?resourceType=IMAGE";
	    		
	    		var thisImgUploadFormat = imgUploadFormat[$(input).attr('name')];
	    		if(thisImgUploadFormat){
	    			ajaxUrl = bcs.bcsContextPath + "/edit/createResource?resourceType=IMAGE&rightContentType=" + $(input).attr('accept');
	    			if(thisImgUploadFormat.rightWidth){
	    				ajaxUrl += "&rightWidth=" + thisImgUploadFormat.rightWidth;
	    			}
	    			if(thisImgUploadFormat.rightHeight){
	    				ajaxUrl += "&rightHeight=" + thisImgUploadFormat.rightHeight;
	    			}
	    			if(thisImgUploadFormat.rightSize){
	    				ajaxUrl += "&rightSize=" + thisImgUploadFormat.rightSize;
	    			}
	    		}
	    		
	    		form_data.append("filePart",input.files[0]);
	
	    		$('.LyMain').block($.BCS.blockMsgUpload);
	    		$.ajax({
	                type: 'POST',
	                url: ajaxUrl,
	                cache: false,
	                contentType: false,
	                processData: false,
	                data: form_data
	    		}).success(function(response){
	            	console.info(response);
	            	
	            	if(typeof response === 'string' && response.indexOf("WARING") == 0){
	            		alert(response);
	            		$(input).val('');
	            		return;
	            	}
	            	
	            	alert("上傳成功!");
	            	$('#descriptionImgId').val(response.resourceId);
	            	$('#descriptionImg').attr("src", bcs.bcsContextPath + '/getResource/IMAGE/' + response.resourceId);
	    		}).fail(function(response){
	    			console.info(response);
	    			$.FailResponse(response);
	    			$('.LyMain').unblock();
	    		}).done(function(){
	    			$('.LyMain').unblock();
	    		});
    		} else {
    			alert("圖片大小不可大於 1MB！");
    		}
        } 
	});

	loadDataFunc();
});