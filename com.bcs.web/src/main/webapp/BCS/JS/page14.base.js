/**
 * 
 */
$(function(){
	// 日期元件
	$(".datepicker").datepicker({
		'dateFormat' : 'yy-mm-dd'
	});
	
	$('.btn_add').click(function(){
 		window.location.replace(bcs.bcsContextPath +'/edit/keywordResponseCreatePage?from=keywordResponsePage');
	});
	
	// 下拉選項
	var optionSelectChange_func = function(){
		var selectValue = $(this).find('option:selected').text();
		$(this).closest('.option').find('.optionLabel').html(selectValue);
	};
	
	$('.optionSelect').change(optionSelectChange_func);
	
	//清空查詢
	$('.clear').click(function(){
		$('#keywordInput').val('');
		$('#pushDate').val('');
		$('#keywordSelector').val('').change();
	});
	
	$('.query').click(function(){
		loadDataFunc();
	});
	
	// 刪除按鈕
	var btn_deteleFunc = function(){
		var iMsgId = $(this).attr('iMsgId');

		var r = confirm("請確認是否刪除");
		if (r) {
			
		} else {
		    return;
		}
		
		$.ajax({
			type : "DELETE",
			url : bcs.bcsContextPath + '/admin/deleteInteractiveMsg?iMsgId=' + iMsgId
		}).success(function(response){
			console.info(response);
			alert("刪除成功");
			loadDataFunc();
		}).fail(function(response){
			console.info(response);
			$.FailResponse(response);
		}).done(function(){
		});
	};
	
	// 複製按鈕
	var btn_copyFunc = function(){
		var iMsgId = $(this).attr('iMsgId');
		console.info('btn_copyFunc iMsgId:' + iMsgId);
 		window.location.replace(bcs.bcsContextPath + '/edit/keywordResponseCreatePage?iMsgId=' + iMsgId + '&actionType=Copy&from=keywordResponsePage');
	};
	
	// 改變狀態按鈕
	var redesignFunc = function(){
		var iMsgId = $(this).attr('iMsgId');
		console.info('redesignFunc iMsgId:' + iMsgId);
		
		$.ajax({
			type : "DELETE",
			url : bcs.bcsContextPath + '/edit/redesignInteractiveMsg?iMsgId=' + iMsgId
		}).success(function(response){
			console.info(response);
			alert("取消成功");
			loadDataFunc();
		}).fail(function(response){
			console.info(response);
			$.FailResponse(response);
		}).done(function(){
		});
	};
	
	// 改變狀態按鈕
	var activateFunc = function(){
		var iMsgId = $(this).attr('iMsgId');
		console.info('activateFunc iMsgId:' + iMsgId);
		
		$.ajax({
			type : "GET",
			url : bcs.bcsContextPath + '/edit/activateInteractiveMsg?iMsgId=' + iMsgId
		}).success(function(response){
			console.info(response);
			alert("生效成功");
			loadDataFunc();
		}).fail(function(response){
			console.info(response);
			$.FailResponse(response);
		}).done(function(){
		});
	};

	// 取得資料列表
	var loadDataFunc = function(){
		$('.LyMain').block($.BCS.blockMsgRead);
		var keywordSelector = $('#keywordSelector').val();
		$('#keywordSelector').val(keywordSelector).change();
		var groupSetting = {};
		
		$.ajax({
			type : "GET",
			url : bcs.bcsContextPath + '/market/getSendGroupList'
		}).success(function(response){
			$.each(response, function(i, o){
				
				groupSetting["GROUPID"+ o.groupId] = o.groupTitle;
			});
		}).fail(function(response){
			console.info(response);
			$.FailResponse(response);
		}).done(function(){
		});
		
		var postData = {};
		postData.type = 'KEYWORD';
		postData.status = $('#keywordSelector').val();
		postData.keywordInput = $('#keywordInput').val();
		postData.pushDate = $('#pushDate').val();
		$.ajax({
			type : "POST",
			url : bcs.bcsContextPath +'/edit/getInteractiveMsgList',
			cache: false,
            contentType: 'application/json',
            processData: false,
			data : JSON.stringify(postData)
		}).success(function(response){
			$('.dataTemplate').remove();
			$.BCS.ResourceMap = response.ResourceMap;
			
			for(key in response.MsgMain){
				var msgData = templateBody.clone(true);
				
				var keyObj = JSON.parse(key);
				
				var valueObj = response.MsgMain[key];
				
				var iMsgId = keyObj.iMsgId;
								
				// 設定內容
				var msgContent = "";
				var msgTypeStr = "-";
				$.each(valueObj, function(i, o){
					msgTypeStr += $.BCS.parseMsgType(o.msgType) + "-";
					if(o.text){
						msgContent += o.text + "-<br/>";
					}
					else if('STICKER' == o.msgType){
						msgContent += '<img src="' + bcs.bcsResourcePath + '/images/Stickers/' + o.referenceId + '_key.png" alt="Type2" ><br/>';
					}
					else if('IMAGE' == o.msgType){
						msgContent += '<img src="' + bcs.bcsContextPath + '/getResource/IMAGE/' + o.referenceId + '" alt="Type2" style="width:100px"><br/>';
					}
					else if('RICH_MSG' == o.msgType){
						var richMsg = $.BCS.ResourceMap[o.referenceId];
						if(richMsg){
							var imageId = richMsg.richImageId;
							msgContent += '<img src="' + bcs.bcsContextPath + '/getResource/IMAGE/' + imageId + '" alt="Type2" style="width:100px"><br/>';
						}
					}
					else if('TEMPLATE' == o.msgType){
						var templateMsg = $.BCS.ResourceMap[o.referenceId];
						if(templateMsg){
							var imageId = templateMsg.templateImageId;

							var templateType = templateMsg.templateType;
							if(templateType == 'confirm'){
								templateType = '確認樣板';
							}
							else if(templateType == 'buttons'){
								templateType = '按鈕樣板';
							}
							else if(templateType == 'carousel'){
								templateType = '滑動樣板';
							}
							
							msgContent += '<p>樣板類型 : '+ templateType + '</p>';
							if(templateMsg.templateType != 'confirm'){
								msgContent += '<img src="' + bcs.bcsContextPath + '/getResource/IMAGE/' + imageId + '" alt="Type2" style="width:100px"><br/>';
								msgContent += '<p>標題 : '+ templateMsg.templateTitle + '</p>';
							}else{
								msgContent += '<p>內容  : '+ templateMsg.templateText + '</p>';
							}
						}
					}
				});
				
				if(keyObj.msgTag){
					msgTypeStr += "<br/>類別:" + keyObj.msgTag;
				}
				
				var keywordShow = keyObj.mainKeyword;
				
				// 設定順位
				var interactiveIntexStr = $.BCS.parseInteractiveIndex(keyObj.interactiveIndex);
				if(interactiveIntexStr){
					keywordShow = interactiveIntexStr + "<br/>" + keywordShow;
				}

				// 重新產生 追加關鍵字
				var otherKeywords = $.BCS.ResourceMap['iMsgId-' + iMsgId];
				if(otherKeywords){
					if(otherKeywords.length > 0){
						keywordShow += "<br/><br/>追加 : ";
					}
					
					$.each(otherKeywords, function(i, o){
						var otherKeyword = o.otherKeyword;
						keywordShow += otherKeyword + ", ";
					});
				}
				
				// 設定生效時間
				var timeType = "";
				if(keyObj.interactiveTimeType){

					var startTimeStr = "";
					var endTimeStr = "";
					if(keyObj.interactiveTimeType == "TYPE_DAY"){
						
						timeType = "一天區間<br/>";

						var startTime = moment(keyObj.interactiveStartTime);
						var startDay = startTime.format("HH:mm");
						startTimeStr = startDay;
						
						var endTime = moment(keyObj.interactiveEndTime);
						var endDay = endTime.format("HH:mm");
						endTimeStr = endDay;
					}
					else if(keyObj.interactiveTimeType == "TYPE_RANGE"){

						timeType = "時間區間<br/>";
						
						var startTime = moment(keyObj.interactiveStartTime);
						var startDay = startTime.format("YYYY-MM-DD HH:mm");
						startTimeStr = startDay;
						
						var endTime = moment(keyObj.interactiveEndTime);
						var endDay = endTime.format("YYYY-MM-DD HH:mm");
						endTimeStr = endDay;
					}
					
					timeType +=startTimeStr + "<br/>" + endTimeStr;
				}
				else{
					timeType = "未設定";
				}

				msgData.find('.mainKeyword a').attr('href', bcs.bcsContextPath +'/edit/keywordResponseCreatePage?iMsgId=' + iMsgId + '&actionType=Edit&from=keywordResponsePage');
				msgData.find('.mainKeyword a').html(keywordShow);
				msgData.find('.userStatus').html($.BCS.parseUserStatus(keyObj.userStatus));

				var otherRoleStr = "";
				var groupSettingStr = groupSetting[keyObj.otherRole];
				if(groupSettingStr){
					otherRoleStr = groupSettingStr;
				}
				msgData.find('.otherRole').html(otherRoleStr);
				
				msgData.find('.msgContent').html(msgContent);

				msgData.find('.timeType').html(timeType);
				
				msgData.find('.msgType').html(msgTypeStr);
				
				msgData.find('.sendCount a').attr('href', bcs.bcsContextPath +'/edit/keywordAndInteractiveReportPage?iMsgId=' + iMsgId + '&userStatus=' + keyObj.userStatus + '&reportType=Keyword');
				msgData.find('.sendCount a').html($.BCS.formatNumber(keyObj.sendCount,0));

				msgData.find('.interactiveStatus span').html($.BCS.parseInteractiveStatus(keyObj.interactiveStatus));
				
				msgData.find('.modifyTime').html($.formatTime(new Date(keyObj.modifyTime)));

				msgData.find('.modifyUser').html(response.AdminUser[keyObj.modifyUser]);

				if (keyObj.interactiveStatus == "ACTIVE") {
					msgData.find('.btn_activate').hide();
					msgData.find('.btn_redeisgn').click(redesignFunc);
					msgData.find('.btn_redeisgn').attr('iMsgId', iMsgId);
				}
				else if (keyObj.interactiveStatus == "DISABLE") {
					msgData.find('.btn_redeisgn').hide();
					msgData.find('.btn_activate').click(activateFunc);
					msgData.find('.btn_activate').attr('iMsgId', iMsgId);
				}
				else {
					msgData.find('.btn_activate').hide();
					msgData.find('.btn_redeisgn').hide();
				}
				
				msgData.find('.btn_copy').attr('iMsgId', iMsgId);
				msgData.find('.btn_copy').click(btn_copyFunc);

				if (bcs.user.admin) {
					msgData.find('.btn_detele').attr('iMsgId', iMsgId);
					msgData.find('.btn_detele').click(btn_deteleFunc);
				} else {
					msgData.find('.btn_detele').remove();
				}

				$('#tableBody').append(msgData);
			}
			
		}).fail(function(response){
			console.info(response);
			$.FailResponse(response);
			$('.LyMain').unblock();
		}).done(function(){
			$('.LyMain').unblock();
		});
	};

	$('.btn_upload_keywords').click(function(){
		$('#upload_keywords').click();
	});
	
	$('#upload_keywords').on("change", function(ev){

		var input = ev.currentTarget;
    	if (input.files && input.files[0]) {
    		var fileName = input.files[0].name;
    		console.info("fileName : " + fileName);
    		var form_data = new FormData();
    		
    		form_data.append("filePart",input.files[0]);

    		$('.LyMain').block($.BCS.blockMsgUpload);
    		$.ajax({
                type: 'POST',
                url: bcs.bcsContextPath + '/edit/uploadMainKeywordList',
                cache: false,
                contentType: false,
                processData: false,
                data: form_data
    		}).success(function(response){
            	console.info(response);
            	alert("匯入成功!");
 				window.location.replace(bcs.bcsContextPath + '/edit/keywordResponseDisablePage');
    		}).fail(function(response){
    			console.info(response);
    			$.FailResponse(response);
    			$('.LyMain').unblock();
    		}).done(function(){
    			$('.LyMain').unblock();
    		});
        } 
	});
	
	var templateBody = {};
	
	var initTemplate = function(){

		templateBody = $('.dataTemplate').clone(true);
		$('.dataTemplate').remove();
	}

	initTemplate();
	loadDataFunc();
});