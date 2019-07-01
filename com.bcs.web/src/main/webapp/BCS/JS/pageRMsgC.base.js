/**
 * 
 */
$(function(){
	var sendGroupCondition = null;
	var btnTarget = ""; // 紀錄 最後按鈕
	
	// ---------------------
//	$(".useStartTimeScheduler").click(function(e){
//		var selectedUseStartTimeScheduler = e.currentTarget.value;
//		
//		switch(selectedUseStartTimeScheduler){
//			case 'NO' :
//				$("#startTimeView").hide();
//				break;
//			case 'YES' :
//				$("#startTimeView").show();
//				break;
//		}
//	});
//	
//	$(".useEndTimeScheduler").click(function(e){
//		var selectedUseEndTimeScheduler = e.currentTarget.value;
//		
//		switch(selectedUseEndTimeScheduler){
//			case 'NO' :
//				$("#endTimeView").hide();
//				break;
//			case 'YES' :
//				$("#endTimeView").show();
//				break;
//		}
//	});
	
	// ----  Date Picker Component---- 
	
	// date format
	var dateFormat = "YYYY-MM-DD HH:mm:ss";
	$(".datepicker").datepicker({'dateFormat' : 'yy-mm-dd'});

	// 從欄位取得日期(型態是 Moment.js 的 date wraps)
	var getMomentByElement = function(elementId) {
		var yearMonthDay = $('#' + elementId).val();
		var hour = $('#' + elementId + 'Hour').val();
		var minute = $('#' + elementId + 'Minute').val();		
		var momentDate = moment(yearMonthDay + ' ' + hour + ':' + minute + ':00', dateFormat);
		return momentDate;
	}
	
	// 設定日期時間欄位值
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
	
	var optionSelectChange_func = function(){
		var selectValue = $(this).find('option:selected').text();
		$(this).closest('.option').find('.optionLabel').html(selectValue);
	};
	
	$('.changeConditionSelect').change(optionSelectChange_func);
	$('.optionSelect').change(optionSelectChange_func);
	
	// 表單驗證
	var validator = $('#formSendGroup').validate({
		rules : {
			
			// 群組名稱
			'groupTitle' : {
				required : {
			        param: true,
			        depends: function(element) {
						if(btnTarget == "btn_save"){
							return true;
						}
						return false;
			        }
				},
				maxlength : 50
			},
			
			// 群組說明
			'groupDescription' : {
				required : {
			        param: true,
			        depends: function(element) {
						if(btnTarget == "btn_save"){
							return true;
						}
						return false;
			        }
				},
				maxlength : 700
			},
			
			// 使用效期
			'richMenuStartUsingTime' : {
				required : true,
				dateYYYYMMDD : true
			},
			
			'richMenuStartUsingTimeHour' : {
				required : true
			},
			
			'richMenuStartUsingTimeMinute' : {
				required : true
			},
			
			'richMenuEndUsingTime' : {
				required : true,
				dateYYYYMMDD : true,
				compareDate : {
					compareType : 'after',
					dateFormat : dateFormat,
					getThisDateStringFunction : function() {
						var yearMonthDay = $('#richMenuEndUsingTime').val();
						var hour = $('#richMenuEndUsingTimeHour').val();
						var minute = $('#richMenuEndUsingTimeMinute').val();		
						return yearMonthDay + ' ' + hour + ':' + minute + ':00';
					},
					getAnotherDateStringFunction : function() {
						var yearMonthDay = $('#richMenuStartUsingTime').val();
						var hour = $('#richMenuStartUsingTimeHour').val();
						var minute = $('#richMenuStartUsingTimeMinute').val();		
						return yearMonthDay + ' ' + hour + ':' + minute + ':00';
					},
					thisDateName : '使用效期結束日期',
					anotherDateName : '使用效期開始日期'
				}
			},
			'richMenuEndUsingTimeHour' : {
				required : true
			},
			
			'richMenuEndUsingTimeMinute' : {
				required : true
			},
		}
	});
	
	// 為新增的一列群組條件加上驗證規則
	var setValidationOnNewRow = function() {
		var tableBody = $('#tableBody');
		var queryBody = tableBody.find('.dataTemplate:last');
		
		// 重新修改 element 的 name，避免多個 element 有重複的 name 而導致表單驗證錯誤的問題
		var rowIndex = tableBody.prop('rowIndex') || 0;
		rowIndex++;
		queryBody.find('.queryField, .queryOp').each(function(index, element) {
			var jqElement = $(this);
			jqElement.attr("name", jqElement.attr("name") + rowIndex);
		});
		queryBody.find('.queryValue').each(function(index, element) {
			var jqElement = $(this);
			jqElement.attr("name", jqElement.attr("name") + rowIndex + '_' + (index + 1));
		});
		
		tableBody.prop('rowIndex', rowIndex);
		
		// 對新增的欄位加上表單驗證
		// 群組條件-欄位
		queryBody.find('.queryField').rules("add", {
			required : true
		});
		
		// 群組條件-條件
		queryBody.find('.queryOp').rules("add", {
			required : true
		});
		
		// 群組條件-數值
		queryBody.find('.queryValue').each(function(index, element) {
			var queryValue = $(this);
			
			// 日期元件
			if (queryValue.is("input.datepicker")) {
				queryValue.rules("add", {
					required : true,
					dateYYYYMMDD : true
				});
				
			// 一般輸入框
			} else if (queryValue.is("input")) {
				queryValue.rules("add", {
					required : true,
					maxlength : 50
				});
			// 下拉選單
			} else {
				queryValue.rules("add", {
					required : true
				});
			}
		});
	};
	
	// do Save
	$('.btn_save').click(function(){
		var queryDataDoms = $('.dataTemplate');
		
		if(queryDataDoms.length == 0){
			alert('請設定群組條件');
			return;
		}
		
		btnTarget = "btn_save";
		if (!validator.form()) {
			return;
		}
		
		var groupTitle = $('#groupTitle').val();
		console.info('groupTitle', groupTitle);
		var groupDescription = $('#groupDescription').val();
		console.info('groupDescription', groupDescription);
		var groupId = $.urlParam("groupId");
		console.info('groupId', groupId);
		var actionType = $.urlParam("actionType");
		console.info('actionType', actionType);
		
		var msgAction = "Create";
				
		if(groupId && actionType == 'Edit'){
			msgAction = "Change"
		}
		else if(actionType == 'Copy'){
			groupId = null;
		}
		
		// Get Query Data
		var sendGroupDetail = [];
		$.each(queryDataDoms, function(i, o){
			var dom = $(o);
			var queryData = {};
			
			if(dom.find('.labelField').is(':visible')){
				queryData.queryField = 'UploadMid';
				queryData.queryOp = dom.find('.labelValue').attr('fileName');
				queryData.queryValue = dom.find('.labelValue').attr('referenceId') + ":" + dom.find('.labelValue').attr('count');
			}
			else{
				queryData.queryField = dom.find('.queryField').val();
				queryData.queryOp = dom.find('.queryOp').val();
				queryData.queryValue = dom.find('.queryValue:visible').val();
			}
			
			sendGroupDetail.push(queryData);
		});
		
		// mainList

		var mainList = document.getElementById("mainList");
		
		var groupName = mainList.options[mainList.selectedIndex].text;
		console.info("groupName:", groupName);
		if (groupName == '請選擇') {
			alert('請選擇圖文選單');
			return;
		}
		
		
		var groupIdStr = mainList.options[mainList.selectedIndex].value;
		var groupIdInt = parseInt(groupIdStr);
//		console.info("groupId's number: ", typeof groupIdInt === 'number' && isFinite(groupIdInt));
		
//        // useStartTimeSchedulers
//		var useStartTimeSchedulers = $('.useStartTimeScheduler');
//		var useStartTimeScheduler = useStartTimeSchedulers[0].checked;
//        // useEndTimeSchedulers
//		var useEndTimeSchedulers = $('.useEndTimeScheduler');
//		var useEndTimeScheduler = useEndTimeSchedulers[0].checked;
		
		var postData = {
			groupId: groupId,
			groupTitle: groupTitle,
			groupType: 'NORMAL', // Not Default
			groupDescription: groupDescription,
			sendGroupDetail: sendGroupDetail,
			richMenuGroupId: groupIdInt,
			richMenuGroupName: groupName,
            //useStartTimeScheduler: useStartTimeScheduler,
            //useEndTimeScheduler: useEndTimeScheduler,
		};
        
		// 使用效期
//        if(useStartTimeScheduler){
//        	var momentRichMenuStartUsingTime = getMomentByElement('richMenuStartUsingTime');
//        	postData.richMenuStartUsingTime = momentRichMenuStartUsingTime.format(dateFormat);
//        }
//        if(useEndTimeScheduler){
//        	var momentRichMenuEndUsingTime = getMomentByElement('richMenuEndUsingTime');
//        	postData.richMenuEndUsingTime = momentRichMenuEndUsingTime.format(dateFormat);
//        }
        
		console.info('postData', postData);
		
		/**
		 * Do Confirm Check
		 */
		var confirmStr = "請確認是否建立";
		if(msgAction  == "Change"){
			confirmStr = "請確認是否儲存";
		}
		var r = confirm(confirmStr);
		if (r) {
			// confirm true
		} else {
		    return;
		}

		$.ajax({
			type : "POST",
			url : bcs.bcsContextPath + '/market/createRichMenuSendGroup',
            cache: false,
            contentType: 'application/json',
            processData: false,
			data : JSON.stringify(postData)
		}).success(function(response){
			console.info(response);
			console.info('groupId:', response.groupId);
			setRichMenuLink(response.groupId);
		}).fail(function(response){
			console.info(response);
			$.FailResponse(response);
		}).done(function(){
			
		});
	});
	
	var setRichMenuLink = function(sendGroupId){
		var postData = {};
		postData.sendGroupId = sendGroupId;
		console.info('postData', postData);
		
		// 傳送資料
		$.ajax({
			type : "POST",
			url : bcs.bcsContextPath +'/edit/sendingRichMenu',
            cache: false,
            contentType: 'application/json',
            processData: false,
			data : JSON.stringify(postData)
		}).success(function(response){
			console.info(response);
			alert( '設定成功');
	 		window.location.replace(bcs.bcsContextPath + '/market/richMenuSendGroupListPage');
		}).fail(function(response){
			console.info(response);
			$.FailResponse(response);
		}).done(function(){
		});
	};
	
	var getDetailFunc = function(){

		var queryDataDoms = $('.dataTemplate');
		
		var groupId = $.urlParam("groupId");

		var groupTitle = $('#groupTitle').val();
		console.info('groupTitle', groupTitle);
		
		var postData = {};
		postData.groupTitle = groupTitle;
		postData.groupType = 'DELETABLE';
		
		if(groupId < 0){ // 預設群組不需要設定
			postData.groupId = groupId;
		}else{
			if(queryDataDoms.length == 0){
				alert('請設定群組條件');
				return;
			}

			btnTarget = "btn_query";
			if (!validator.form()) {
				return;
			}
		}
		
		// Get Query Data
		var sendGroupDetail = [];
		$.each(queryDataDoms, function(i, o){
			var dom = $(o);
			var queryData = {};
			
			if(dom.find('.labelField').is(':visible')){
				queryData.queryField = 'UploadMid';
				queryData.queryOp = dom.find('.labelValue').attr('fileName');
				queryData.queryValue = dom.find('.labelValue').attr('referenceId') + ":" + dom.find('.labelValue').attr('count');
			}
			else{
				queryData.queryField = dom.find('.queryField').val();
				queryData.queryOp = dom.find('.queryOp').val();
				queryData.queryValue = dom.find('.queryValue:visible').val();
			}
			
			sendGroupDetail.push(queryData);
		});
		
		postData.sendGroupDetail = sendGroupDetail;
		
		return postData;
	}

	// 匯出MID
	$('.btn_draft').click(function(){
		var postData = getDetailFunc();
		console.info('postData', postData);

		if(!postData){
			return;
		}
		
		$.ajax({
			type : "POST",
			url : bcs.bcsContextPath + '/market/createRichMenuSendGroupMidExcelTemp',
            cache: false,
            contentType: 'application/json',
            processData: false,
			data : JSON.stringify(postData)
		}).success(function(response){
			console.info(response);

			if(response.count > 0){
				var url =  bcs.bcsContextPath + '/market/exportToExcelForRichMenuSendGroup?tempId=' + response.tempId;
				
				var downloadReport = $('#downloadReport');
				downloadReport.attr("src", url);
			}
			else{
				alert('查詢結果共 ' + response.count + ' 筆');
			}
		}).fail(function(response){
			console.info(response);
			$.FailResponse(response);
		}).done(function(){
		});
	});

	$('#downloadReport').load(function () {
        //if the download link return a page
        //load event will be triggered
		$('.LyMain').unblock();
    });
	
	// 條件結果按鍵
	$('.btn_query').click(function(){
		var postData = getDetailFunc();
		console.info('postData', postData);
		if(!postData){
			return;
		}
		
		$.ajax({
			type : "POST",
			url : bcs.bcsContextPath + '/market/getRichMenuSendGroupConditionResult',
            cache: false,
            contentType: 'application/json',
            processData: false,
			data : JSON.stringify(postData)
		}).success(function(response){
			console.info(response);
			alert('查詢結果共 ' + response + ' 筆');
		}).fail(function(response){
			console.info(response);
			$.FailResponse(response);
		}).done(function(){
		});
	});
	
	$('.btn_cancel').click(function(){
		
		var r = confirm("請確認是否取消");
		if (r) {
			// confirm true
		} else {
		    return;
		}
		
 		window.location.replace(bcs.bcsContextPath + '/market/richMenuSendGroupListPage');
	});

	var btn_deteleFunc = function(){
		$(this).closest('tr').remove();
	};
	
	/**
	 * 選擇[欄位]要動態切換[條件]下拉選單的選項、[數值]元件
	 * @param queryFieldSelect [欄位]下拉選單
	 */
	var setGroupQueryComponent = function(queryFieldSelect){		
		if (!sendGroupCondition) {c
			return;
		}
		
		// 包含[欄位]下拉選單的 <tr/>
		var tr = queryFieldSelect.closest('tr');
		
		// [條件]下拉選單
		var queryOpSelect = tr.find('.queryOp');
		queryOpSelect.find('option[value!=""]').remove();
		queryOpSelect.change();
		
		// [數值]元件
		var queryValueComponent = tr.find('.queryValueComponent');
		
		// 移除表單驗證所加上的錯誤 css class
		queryValueComponent.find('.queryValue').removeClass('error').next('label.error').remove();
		
		var queryValueComponentSelectList = queryValueComponent.find('.queryValueComponentSelectList');
		queryValueComponentSelectList.hide().find('option[value!=""]').remove();
		var queryValueComponentInput = queryValueComponent.find('.queryValueComponentInput');
		queryValueComponentInput.hide().find(':text').val('');
		var queryValueComponentDatepicker = queryValueComponent.find('.queryValueComponentDatepicker');
		queryValueComponentDatepicker.hide().find(':text').val('');
		
		var queryFieldId = queryFieldSelect.val();
		
		if (!queryFieldId) {
			return;
		}
		
		// 設定[條件]下拉選單的選項
		$.each(sendGroupCondition[queryFieldId].queryFieldOp, function(index, value) {
			queryOpSelect.append('<option value="' + value + '">' + value + '</option>');
		});
		queryOpSelect.change();
		
		// 判斷要使用的[數值]元件
		switch (sendGroupCondition[queryFieldId].queryFieldSet) {
		case 'SelectList':
			$.each(sendGroupCondition[queryFieldId].sendGroupQueryTag, function(index, sendGroupQueryTag) {
				queryValueComponentSelectList
					.find('select')
					.append('<option value="' + sendGroupQueryTag.queryFieldTagValue + '">' 
							+ sendGroupQueryTag.queryFieldTagDisplay + '</option>');
			});
			queryValueComponentSelectList.show().find('select').change();
			break;
		case 'Input':
			queryValueComponentInput.show();
			break;
		case 'DatePicker':
			queryValueComponentDatepicker.show();
			break;
		default:
			break;
		}
	};
	
	var optionSelectChange_func = function(){
		var select = $(this);
		var selectValue = select.find('option:selected').text();
		select.closest('.option').find('.optionLabel').html(selectValue);
		
		// 若是[欄位]下拉選單
		if (select.hasClass('queryField')) {
			setGroupQueryComponent(select);
		}
	};
	
	$('.add_rule').click(function(){
		var queryBody = templateBody.clone(true);
		queryBody.find('.btn_delete').click(btn_deteleFunc);
		queryBody.find('.optionSelect').change(optionSelectChange_func);
		queryBody.find(".datepicker").datepicker({'dateFormat' : 'yy-mm-dd'});
		$('#tableBody').append(queryBody);
		setValidationOnNewRow();
	});
	
	$('.upload_mid').click(function(){
		$('#upload_mid_btn').click();
	});
	
	$('#upload_mid_btn').on("change", function(ev){

		var input = ev.currentTarget;
    	if (input.files && input.files[0]) {
    		var fileName = input.files[0].name;
    		console.info("fileName : " + fileName);
    		var form_data = new FormData();
    		
    		form_data.append("filePart",input.files[0]);
    		
    		
    		//遮蓋效果UI
    		$('.LyMain').block($.BCS.blockMsgUpload); 
    		$.ajax({
                type: 'POST',
                url: bcs.bcsContextPath + '/market/uploadMidRichMenuSendGroup?event=sendGroup',
                cache: false,
                contentType: false,
                processData: false,
                data: form_data
    		}).success(function(response){
            	console.info(response);
            	alert("匯入成功!");
        		var queryBody = templateBody.clone(true);
        		queryBody.find('.btn_delete').click(btn_deteleFunc);
        		queryBody.find('.labelField').html("UID匯入");
        		queryBody.find('.labelField').show();
        		queryBody.find('.labelOp').html(fileName);
        		queryBody.find('.labelOp').show();
        		queryBody.find('.labelValue').html(response.count + " 筆 UID");
        		queryBody.find('.labelValue').show();
        		queryBody.find('.option').remove();

        		queryBody.find('.labelValue').attr('fileName', fileName);
        		queryBody.find('.labelValue').attr('referenceId', response.referenceId);
        		queryBody.find('.labelValue').attr('count', response.count);
        		$('#tableBody').append(queryBody);
    		}).fail(function(response){
    			console.info(response);
    			$.FailResponse(response);
    			$('.LyMain').unblock();
    		}).done(function(){
    			$('.LyMain').unblock();
    		});
        } 
	});

	var loadDataFunc = function(){
		
		// 取得群組條件各個下拉選項值
		$.ajax({
			type : "GET",
			url : bcs.bcsContextPath + '/market/getRichMenuSendGroupCondition'
		}).success(function(response){
			console.info(response);
			sendGroupCondition = response;
			
			$.each(sendGroupCondition, function(queryFieldId, queryFieldObject){
				templateBody.find('.queryField').append(
						'<option value="' + queryFieldId + '">' + queryFieldObject.queryFieldName + '</option>');
			});
			
			var groupId = $.urlParam("groupId");
			
			if(groupId){
				
				$.ajax({
					type : "GET",
					url : bcs.bcsContextPath + '/market/getRichMenuSendGroup?groupId=' + groupId
				}).success(function(response){
					$('.dataTemplate').remove();
					console.info(response);
					
					// useStartTimeScheduler
//					if(response.useStartTimeScheduler == true || response.autoSendPoint == 'true'){
//						$('input[name="useStartTimeScheduler"]')[0].checked = true;
//						$('#startTimeView').show();
//					}else{ 
//						$('input[name="useStartTimeScheduler"]')[1].checked = true;
//						$('#startTimeView').hide();
//					}
//					// useEndTimeScheduler
//					if(response.useEndTimeScheduler == true || response.autoSendPoint == 'true'){
//						$('input[name="useEndTimeScheduler"]')[0].checked = true;
//						$('#endTimeView').show();
//					}else{ 
//						$('input[name="useEndTimeScheduler"]')[1].checked = true;
//						$('#endTimeView').hide();
//					}
//					
//					// StartUsingTime
//					setElementDate('richMenuStartUsingTime', response.richMenuStartUsingTime);
//	            	setElementDate('richMenuEndUsingTime', response.richMenuEndUsingTime);
					
					$('#groupTitle').val(response.groupTitle);
					$('#groupDescription').val(response.groupDescription);
					
					if(groupId > 0){
						$.each(response.sendGroupDetail, function(i, o){
	
							var queryBody = templateBody.clone(true);
							queryBody.find(".datepicker").datepicker({ 'dateFormat' : 'yy-mm-dd'});
							queryBody.find('.optionSelect').change(optionSelectChange_func);
							
							if('UploadMid' == o.queryField){

								var split = o.queryValue.split(':');
								
				        		queryBody.find('.labelField').html("UID匯入");
				        		queryBody.find('.labelField').show();
				        		queryBody.find('.labelOp').html(o.queryOp);
				        		queryBody.find('.labelOp').show();
				        		queryBody.find('.labelValue').html(split[1] + " 筆 UID");
				        		queryBody.find('.labelValue').show();
				        		queryBody.find('.option').remove();

				        		queryBody.find('.labelValue').attr('fileName', o.queryOp);
				        		queryBody.find('.labelValue').attr('referenceId', split[0]);
				        		queryBody.find('.labelValue').attr('count', split[1]);
							}
							else{
								queryBody.find('.queryField').val(o.queryField).change();
								queryBody.find('.queryOp').val(o.queryOp).change();
								queryBody.find('.queryValue').val(o.queryValue).change();
							}
	
							queryBody.find('.btn_delete').click(btn_deteleFunc);
							
							$('#tableBody').append(queryBody);
							setValidationOnNewRow();
						});
						
						if(response.groupType == 'DEFAULT'){
//							$('#startTimeTr').remove();
//							$('#endTimeTr').remove();
							$('#queryContent').remove();
							$('#groupTitle').attr('disabled',true);
						}
					}else{
						$('#groupTitle').attr('disabled',true);
						$('#groupDescription').attr('disabled',true);
						
						$('.btn_save').remove();
						$('#queryContent').remove();
					}
					
				}).fail(function(response){
					console.info(response);
					$.FailResponse(response);
				}).done(function(){
				});
				
				var actionType = $.urlParam("actionType");
				if(actionType == "Edit"){
					$('.CHTtl').html('編輯發送群組');
				}
				else if(actionType == "Copy"){
					$('.CHTtl').html('複製發送群組');
				}
			}else{
				// Create
//				$('input[name="useStartTimeScheduler"]')[0].checked = true;
//				$('input[name="useEndTimeScheduler"]')[0].checked = true;
			}
		}).fail(function(response){
			console.info(response);
			$.FailResponse(response);
		}).done(function(){
			getGroupList();
		});
	};

	var getGroupList = function(){
		$.ajax({
			type : "GET",
			url : bcs.bcsContextPath + '/edit/getRichMenuGroupList'
		}).success(function(response){
			console.info('getLinePointList response:' + JSON.stringify(response));

			var mainList = document.getElementById("mainList");
			$.each(response, function(i, o){		
				console.info('getLinePointList o:' + JSON.stringify(o));
				 var opt = document.createElement('option');
				 opt.value = o.richMenuGroupId;
				 opt.innerHTML = o.richMenuGroupName;	
				mainList.appendChild(opt);
			});
		}).fail(function(response){
			console.info(response);
			$.FailResponse(response);
		}).done(function(){
		});
	};
	
	var templateBody = {};
	templateBody = $('.dataTemplate').clone(true);
	$('.dataTemplate').remove();
	
	loadDataFunc();
});