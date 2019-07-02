/**
 * 
 */
$(function(){
	
	// ---- Global Variables ---- 
	
	var richId = "";
	var groupId = "";
	var richType = "";
	var actionType = "";
	var readOnly = "";
	var btnTarget = ""; // last clicked button
	
	// every richType's XY
	var framesTypePointXY = [
	        // 全版
 			[{startX : 0, startY : 0, endX : 833, endY : 843}, {startX : 833, startY : 0, endX : 1666, endY : 843}, {startX : 1666, startY : 0, endX : 2500, endY : 843}, 
 			 {startX : 0, startY : 843, endX : 833, endY : 1686}, {startX : 833, startY : 843, endX : 1666, endY : 1686}, {startX : 1666, startY : 843, endX : 2500, endY : 1686}],
 			[{startX : 0, startY : 0, endX : 1250, endY : 843}, {startX : 1250, startY : 0, endX : 2500, endY : 843},
 			 {startX : 0, startY : 843, endX : 1250, endY : 1686}, {startX : 1250, startY : 843, endX : 2500, endY : 1686}], 
 			[{startX : 0, startY : 0, endX : 2500, endY : 843}, {startX : 0, startY : 843, endX : 833, endY : 1686},
 			 {startX : 833, startY : 843, endX : 1666, endY : 1686}, {startX : 1666, startY : 843, endX : 2500, endY : 1686}], 
 			[{startX : 0, startY : 0, endX : 1666, endY : 1686}, {startX : 1666, startY : 0, endX :2500, endY : 843},
 			 {startX : 1666, startY : 843, endX : 2500, endY : 1686}],
 			[{startX : 0, startY : 0, endX : 2500, endY : 843}, {startX : 0, startY : 843, endX : 2500, endY : 1686}],
 			[{startX : 0, startY : 0, endX : 1250, endY : 1686}, {startX : 1250, startY : 0, endX : 2500, endY : 1686}],
 			[{startX : 0, startY : 0, endX : 2500, endY : 1686}],
 			// 半版
 			[{startX : 0, startY : 0, endX : 833, endY : 843}, {startX : 833, startY : 0, endX : 1666, endY : 843}, {startX : 1666, startY : 0, endX : 2500, endY : 843}],
 			[{startX : 0, startY : 0, endX : 1250, endY : 843}, {startX : 1250, startY : 0, endX : 2500, endY : 843}],
 			[{startX : 0, startY : 0, endX : 2500, endY : 843}]
 	];
	

	// ----  Date Picker Component---- 
	
	// date format
//	var dateFormat = "YYYY-MM-DD HH:mm:ss";
//	$(".datepicker").datepicker({'dateFormat' : 'yy-mm-dd'});
//
//	// 從欄位取得日期(型態是 Moment.js 的 date wraps)
//	var getMomentByElement = function(elementId) {
//		var yearMonthDay = $('#' + elementId).val();
//		var hour = $('#' + elementId + 'Hour').val();
//		var minute = $('#' + elementId + 'Minute').val();		
//		var momentDate = moment(yearMonthDay + ' ' + hour + ':' + minute + ':00', dateFormat);
//		return momentDate;
//	}
//	
//	// 設定日期時間欄位值
//	var setElementDate = function(elementId, timestamp) {
//		if (!timestamp) {
//			return;
//		}
//		var momentDate = moment(timestamp);
//		$('#' + elementId).val(momentDate.format('YYYY-MM-DD'));
//		
//		var hour = momentDate.hour();
//		$('#' + elementId + 'Hour').val(hour < 10 ? '0' + hour : hour).change();
//		
//		var minute = momentDate.minute();
//		$('#' + elementId + 'Minute').val(minute < 10 ? '0' + minute : minute).change();		
//	}

	
	// option select change function
	var optionSelectChange_func = function(){
		var selectValue = $(this).find('option:selected').text();
		$(this).closest('.option').find('.optionLabel').html(selectValue);
	};
	
	$('.changeConditionSelect').change(optionSelectChange_func);
	$('.optionSelect').change(optionSelectChange_func);
	
	
	// ---- Table Validate ----
	
	var validator = $('#ContentValidateForm').validate({
		rules : {	
			// 圖文選單名稱
			'richMenuName' : {
				required : {
			        param: true,
			        depends: function(element) {
						if(btnTarget == "btn_save"){
							return true;
						}
						return false;
			        }
				},
				maxlength : {
			        param: 100
				}
			},
			// 圖文選單標題
			'richMenuTitle' : {
				required : {
			        param: true,
			        depends: function(element) {
						if(btnTarget == "btn_save"){
							return true;
						}
						return false;
			        }
				},
				maxlength : {
			        param: 100
				}
			},
			
			// 切換條件
			'changeConditionSelect' : {
				required : {
			        param: true,
			        depends: function(element) {
						if(btnTarget == "btn_save"){
							return true;
						}
						return false;
			        }
				}
			},
			// 主要圖片
			'titleImage' : {
				required : '.imgId:blank'
			},
//			// 使用效期
//			'richMenuStartUsingTime' : {
//				required : true,
//				dateYYYYMMDD : true
//			},
//			
//			'richMenuStartUsingTimeHour' : {
//				required : true
//			},
//			
//			'richMenuStartUsingTimeMinute' : {
//				required : true
//			},
//			
//			'richMenuEndUsingTime' : {
//				required : true,
//				dateYYYYMMDD : true,
//				compareDate : {
//					compareType : 'after',
//					dateFormat : dateFormat,
//					getThisDateStringFunction : function() {
//						var yearMonthDay = $('#richMenuEndUsingTime').val();
//						var hour = $('#richMenuEndUsingTimeHour').val();
//						var minute = $('#richMenuEndUsingTimeMinute').val();		
//						return yearMonthDay + ' ' + hour + ':' + minute + ':00';
//					},
//					getAnotherDateStringFunction : function() {
//						var yearMonthDay = $('#richMenuStartUsingTime').val();
//						var hour = $('#richMenuStartUsingTimeHour').val();
//						var minute = $('#richMenuStartUsingTimeMinute').val();		
//						return yearMonthDay + ' ' + hour + ':' + minute + ':00';
//					},
//					thisDateName : '使用效期結束日期',
//					anotherDateName : '使用效期開始日期'
//				}
//			},
//			'richMenuEndUsingTimeHour' : {
//				required : true
//			},
//			
//			'richMenuEndUsingTimeMinute' : {
//				required : true
//			}
		}
	});
	

	// ---- Button Components ----

	// do Cancel
	$('input[name="cancel"]').click(function() {
		var r = confirm("請確認是否取消");
		if(r){
			window.location.replace(bcs.bcsContextPath + '/edit/richMenuMemberListPage?groupId=' + groupId);
		}else{
		    return;
		}
	});
	
	// do ActionType Click
	var actionTypeClick = function(){

		var actionType = $(this).val();
		console.info('actionType', actionType);

		var richMsgUrlPageTr = $(this).closest(".richMsgUrlPageTr");
		
		if(actionType == "sendMessage"){
			richMsgUrlPageTr.find('.webTd').css('display', 'none');
			richMsgUrlPageTr.find('.webTd .MdFRM01Input input').removeClass();
			richMsgUrlPageTr.find('.postbackTd').css('display', 'none');
			richMsgUrlPageTr.find('.postbackTd .MdFRM01Input input').removeClass();
			
			richMsgUrlPageTr.find('.sendMessageTd').css('display', '');
			richMsgUrlPageTr.find('.sendMessageTd input').addClass('richMsgUrl');
			richMsgUrlPageTr.next().find('.linkInput').css('display', 'none');
		}
		else if(actionType == "web"){
			richMsgUrlPageTr.find('.sendMessageTd').css('display', 'none');
			richMsgUrlPageTr.find('.sendMessageTd .MdFRM01Input input').removeClass();
			richMsgUrlPageTr.find('.postbackTd').css('display', 'none');
			richMsgUrlPageTr.find('.postbackTd .MdFRM01Input input').removeClass();
			
			richMsgUrlPageTr.find('.webTd').css('display', '');
			richMsgUrlPageTr.find('.webTd .MdFRM01Input input').addClass('richMsgUrl');
			richMsgUrlPageTr.next().find('.linkInput').css('display', '');
		}
		else if(actionType == "postback"){
			richMsgUrlPageTr.find('.sendMessageTd').css('display', 'none');
			richMsgUrlPageTr.find('.sendMessageTd .MdFRM01Input input').removeClass();
			richMsgUrlPageTr.find('.webTd').css('display', 'none');
			richMsgUrlPageTr.find('.webTd .MdFRM01Input input').removeClass();
			
			richMsgUrlPageTr.find('.postbackTd').css('display', '');
			richMsgUrlPageTr.find('.postbackTd .MdFRM01Input input').addClass('richMsgUrl');
			richMsgUrlPageTr.next().find('.linkInput').css('display', 'none');
		}
	}
	
	// do ActionType Radio Click
	var setActionTypeRadioEvent = function(){
		$('.actionType').click(actionTypeClick);
	}
	
	// initialize label
	var buildLinkTagContentFlag = function(element) {
		return $.BCS.contentFlagComponent(element, 'LINK', {
			placeholder : '請輸入註記'
		});
	};
	
	var richMsgUrlPageTemplate = {};
	var richMsgUrlTxtTemplate = {};
	var urlTrTemplate = {};
	
	// initialize
	var initTemplate = function(){
		richId = $.urlParam("richId"); //從列表頁導過來的參數
		groupId = $.urlParam("groupId"); //從列表頁導過來的參數
		actionType = $.urlParam("actionType"); //從列表頁導過來的參數
		readOnly = $.urlParam("readOnly"); //從列表頁導過來的參數
		
		richMsgUrlPageTemplate = $(".richMsgUrlPageTr").clone();
		richMsgUrlTxtTemplate = $(".richMsgUrlTxtTr").clone();
		urlTrTemplate = $(".urlDialogTr").clone();
		
		$(".richMsgUrlPageTr").remove();
		$(".richMsgUrlTxtTr").remove();
		$(".urlDialogTr").remove();
		
		$('#richMenuTitle').val("");
		$('.imgId').val("");
    	$('.mdFRM03Img').find('img').attr('src', "");
		
		console.info('richMsgUrlPageTemplate', richMsgUrlPageTemplate);
		console.info('richMsgUrlTxtTemplate', richMsgUrlTxtTemplate);
		console.info('urlTrTemplate', urlTrTemplate);
		
		if(richId != null && richId != ""){
//			$('.btn_save').remove();
		}else{
			var templateFrameType = $("input[name='templateFrameType']");
			templateFrameType[0].checked = true;
			$("input[name='templateFrameType']:checked").trigger("click");
		}
		
		if(readOnly){
			$('#save').remove();
		}
		
		$('#customizeTypeBtn').hide();
		$('#removeUrl').css({"margin-left": "3px"});
		$('#addUrl').css({"margin-left": "3px"});
		$('#customizeFrameTypeLimit').css({"width": "625px", "border": "1px solid", "position": "relative"});
		$('#savePosition').css({"float": "right", "margin-top": "10px"});
	};
	
	var imgSize;
	var changeText = function(type){
		imgSize = type;
		if(type == 'HALF'){			
			$('.MdTxtNotice01').html('上傳一張依據設計指南所製作的圖片。可支援的規格為<font size="3" color="red">2500(固定) x 843(固定)</font></th>向量，可支援檔案格式為jpg、png。');
		}else if(type == 'FULL'){
			$('.MdTxtNotice01').html('上傳一張依據設計指南所製作的圖片。可支援的規格為<font size="3" color="red">2500(固定) x 1686(固定)</font></th>向量，可支援檔案格式為jpg、png。');
		}
	}
	

	
	var getDataByRichId = function() {
//		richId = $.urlParam("richId"); //從列表頁導過來的參數
//		groupId = $.urlParam("groupId"); //從列表頁導過來的參數
//		actionType = $.urlParam("actionType"); //從列表頁導過來的參數
		
		console.info("richId", richId);
		console.info("groupId", groupId);
		console.info("actionType", actionType);
		
		if (richId != null && richId != "") {
			$.ajax({
                type: 'GET',
                url: bcs.bcsContextPath + "/edit/getRichMenu/" + richId,
    		}).success(function(response){
				var valueObj = response[richId];
				console.info('valueObj', valueObj);
				
				richType = valueObj[0];
				$.each($('input[name="templateFrameType"]'), function(i, v) {
					if (v.value == richType) {
						v.checked = true;
					}
				});
				
				menuSize = valueObj[18]
				changeText(menuSize);

				// 圖文選單名稱				
				$('#richMenuName').val(valueObj[15]);
				$('#richMenuTitle').val(valueObj[1]);
				$('.imgId').val(valueObj[2]);
				$('.mdFRM03Img').find('img').attr('src', bcs.bcsContextPath + "/getResource/IMAGE/" + valueObj[2]);
				$('.mdFRM03Img').find('img').css('width', '100%');
				
				// 使用期間
				//setElementDate('richMenuStartUsingTime', valueObj[19]);
            	//setElementDate('richMenuEndUsingTime', valueObj[20]);
				
				var urls = [""];
				if(valueObj[3]){
					urls = valueObj[3].split(",");
				}
				var actionTypeList = ["web"];
				console.info("actionTypeList:", actionTypeList); // ["sendMessage", "sendMessage", "postback"]
				
				if(valueObj[14]){
					actionTypeList = valueObj[14].split(",");
				}
				var linkTitles = [""];
				if(valueObj[4]){
					linkTitles = valueObj[4].split(",");
				}
				
				linkNumbers = urls.length;
				
				
				changeRichTypeImg(richType); //變更type圖示
				if (richType == "11" || richType == "12") {
					$('#customizeTypeBtn').show();
				} else {
					frameTypePointXY = framesTypePointXY[Number(richType) - 1];
				}
				generateRichMsgUrl(); 
				
				var richMsgUrlPageTrs = $('.richMsgUrlPageTr');
				for(var i=0; i < linkNumbers; i++) {
					var actionTypes = $(richMsgUrlPageTrs[i]).find('.actionType');
					$.each(actionTypes, function(j, o){
						if($(o).val() == actionTypeList[i]){
							$(o).click();
						}
					});
				}
				
				originalImgHeight = valueObj[10];
				originalImgWidth = valueObj[11];
				
				var richMsgUrls = $('.richMsgUrl');
				var richMsgUrlsTxt = $('.richMsgLinkTxt');
				var richMsgUrlTxtTr = $('.richMsgUrlTxtTr');
				var multiStartX = valueObj[6].split(",");
				var multiStartY = valueObj[7].split(",");
				var multiEndX = valueObj[8].split(",");
				var multiEndY = valueObj[9].split(",");
				var linkIdList = valueObj[12].split(",");
				for (var i=0; i<linkNumbers; i++) {
					if(actionTypeList[i] == "sendMessage"){
						richMsgUrls[i].value = linkIdList[i];
					}
					else if(actionTypeList[i] == "postback"){
						richMsgUrls[i].value = linkIdList[i];
					}
					else{
						richMsgUrls[i].value = urls[i];
					}
					richMsgUrlsTxt[i].value = linkTitles[i];
					
					// 註記標籤元件
					var linkTagContentFlag = richMsgUrlTxtTr.eq(i).data('linkTagContentFlag');
					linkTagContentFlag.findContentFlagList(linkIdList[i]);
					
					if (richType == "11" || richType == "12") {
						//設定draggable
						var letter = String.fromCharCode(65 + i);
						setDraggable(letter);
					}
				}
				
				// 圖文選單顯示設定
				$.each($('input[name="richMenuShowStatus"]'), function(i, v) {
					if (v.value == valueObj[16]) {
						v.checked = true;
					}
				});
				
				// 圖文選單切換條件
				var level = valueObj[17];
				$('.changeConditionSelect').val(level);
				$('.changeConditionSelect').change();
				
				//觸發輸入文字計數
				$('#richMenuName').trigger("keyup");
				$('#richMenuTitle').trigger("keyup");
				$('.richMsgLinkTxt').trigger("keyup");
				
				//設定每個draggable的座標與大小
				$.each($('.urlDraggable'), function(i, v) {
					var width = (multiEndX[i] - multiStartX[i]) / 4;
					var heigth = (multiEndY[i] - multiStartY[i]) / 4;
					$(this).css({"width": width, "height": heigth, "top": (multiStartY[i] / 4)+"px", "left": (multiStartX[i] / 4)+"px"})
				});
				
				//設定dialog的圖片大小
				var width = Number(valueObj[11]);
				var height = Number(valueObj[10]);
    			setImgHeightAndWidth(valueObj[2], width, height);
    		}).fail(function(response){
    			console.info(response);
    			$.FailResponse(response);
    		}).done(function(){
    			getGoToList();
    			console.info(".richMsgUrl:", $('.richMsgUrl'));
    		});
		} else {
			actionType = "Create";
		}
	}
	
	var getGoToList = function(){
		$.ajax({
			type : 'GET',
			url : bcs.bcsContextPath + '/edit/getRichMenuListByRichMenuGroupId/' + groupId 
		}).success(function(response){
			console.info('getRichMenuListByRichMenuGroupId response:' + JSON.stringify(response));
			
			var goToLists = $('.goToList');
			$.each(goToLists, function(k, v){
				var goToList = goToLists[k];
				
//				var selectedValue = "";
				var count = 0;
				$.each(response, function(i, o){		
					console.info('goToList o:' + JSON.stringify(o));
					 var opt = document.createElement('option');
					 //opt.richId = o.richId;
					 //opt.attr('richId', o.richId);
					 
					 opt.value = ++count;
					 opt.innerHTML = o.richMenuName;
					 
					 var input = document.createElement('input');
					 input.setAttribute('type', 'text');
					 input.setAttribute('name', 'richId');
					 input.setAttribute('value', o.richId);
					 //input.innerHTML = o.richId;
					 opt.appendChild(input);
					 
					 //opt.appendChild 
					 // o.richId
					 
					 //console.info("o.serialId", o.serialId);
//					 if(richId != null && richId == o.richId){
//						 selectedValue = o.richMenuName;
//						 console.info("selectedValue", selectedValue);
//					 }
					goToList.appendChild(opt);
				});
//				$('#goToList').val(selectedValue);
								
			});
		}).fail(function(response){
			console.info(response);
			$.FailResponse(response);
		}).done(function(){
			setGoToList();
		});		
	};
	
	var setGoToList = function(){
		var postbackTds = $('.postbackTd');
		console.info('postbackTd:', $('.postbackTd'));
		
		$.each(postbackTds, function(k, v){
			// var url1 = postbackTds[k].find();
			var goToPaths = $(postbackTds[k]).find('.goToPath');
			console.info("goToPaths:", goToPaths);
			if(goToPaths.length > 0) {
				var goToPath = goToPaths[0].value;
				console.info("goToPath:", goToPath);
				
				var goToList = $(postbackTds[k]).find('#goToList')[0];
				
				//$(postbackTds[k]).find('#goToList').val(1);
				
				console.info("goToList:", goToList);
				var index = 0;
				$.each(goToList, function(i, o){		
					console.info('goToList o:' + o);
					 //var opt = document.createElement('option');
					 console.info('o.value:', o.value);
					 console.info('o.innerHTML:', o.innerHTML);
					 
					 var search = o.innerHTML.search(goToPath);
					 console.info('search:', search);
					 if(search > -1){
						 console.info("get index:", index);
						 $(postbackTds[k]).find('#goToList').val(index);
					 }
					 index++;
				});
				
			}
		});
	}
	//點擊圖文訊息類別後變更設定連結的圖示
	var linkNumbers = 0; //連結數
	var menuSize = 'FULL'; //RichMenu Size
	var frameTypePointXY;
	$("input[name='templateFrameType']").click(function(e) {
		var selectedRichType = e.currentTarget.value; //選擇的連結類型
		console.info("selectedRichType:", selectedRichType);
		
		$('#customizeTypeBtn').hide();
		$('.urlDraggable').remove();
		
		if (richType == selectedRichType) {
			initTemplate();
			getDataByRichId(); //點擊的type與導頁過來的一樣，則取回原先資料
			return;
		}
		
		if(selectedRichType == 11 || selectedRichType == 12){
			// do nothing
		}else{
			getGoToList();
		}
		
		
		changeRichTypeImg(selectedRichType);
		
		switch (selectedRichType) {
			case '01':
				linkNumbers = 6;
				menuSize = 'FULL';
				break;
			case '02':
				linkNumbers = 4;
				menuSize = 'FULL';
				break;
			case '03':
				linkNumbers = 4;
				menuSize = 'FULL';
				break;
			case '04':
				linkNumbers = 3;
				menuSize = 'FULL';
				break;
			case '05':
				linkNumbers = 2;
				menuSize = 'FULL';
				break;
			case '06':
				linkNumbers = 2;
				menuSize = 'FULL';
				break;
			case '07':
				linkNumbers = 1;
				menuSize = 'FULL';
				break;
			case '08':
				linkNumbers = 3;
				menuSize = 'HALF';
				break;
			case '09':
				linkNumbers = 2;
				menuSize = 'HALF';
				break;
			case '10':
				linkNumbers = 1;
				menuSize = 'HALF';
				break;
			case '11':
				linkNumbers = 0;
				menuSize = 'FULL';
				break;
			case '12':
				linkNumbers = 0;
				menuSize = 'HALF';
				break;
			default:
				break;
		}

		// 版型不同時，清除舊有圖片
		if(menuSize != imgSize){
			$('.imgId').val("");
        	$('.mdFRM03Img').find('img').attr('src', ""); 
        	$('.mdFRM03Img').find('img').css('width', 0);
		}
		
		changeText(menuSize);
		
		$(".richMsgUrlPageTr").remove();
		$(".richMsgUrlTxtTr").remove();
		if (selectedRichType != "11" && selectedRichType != "12") {
			frameTypePointXY = framesTypePointXY[Number(selectedRichType) - 1];
			generateRichMsgUrl();
		} else {
			$('#customizeTypeBtn').show();
			$('#addUrl').trigger("click");
		}
	});
	
	var radios = $("input[name='templateFrameType']");
	$.each(radios, function(i, o){
		$(o).closest('.typeMenu').find('img').click(function(){
			$(o).click();
		});
	})
	
	//變更點擊的type圖示
	var changeRichTypeImg = function(richType) {
		var imgHtml = "連結<img src='" +  bcs.bcsResourcePath + "/images/richmenu_type_" + richType + ".png' alt='Type" + Number(richType) + "'>";
		$("#richMsgUrlTh").html(imgHtml);
	}
	
	var totalUrlCount = 0;
	//動態產生輸入url的tr
	var generateRichMsgUrl = function() {
		$("#richMsgUrlTh").prop("rowspan", linkNumbers * 2 + 1);
		
		var validateNameSet = [];
		var appendHtml = "";
		var existUrlNumbers = $('.richMsgUrlPageTr').length; //畫面已存在的連結數
		for (var i=existUrlNumbers; i<linkNumbers; i++) {
			totalUrlCount ++;
			
			var richMsgUrlPage = richMsgUrlPageTemplate;
			var richMsgUrlTxt = richMsgUrlTxtTemplate;
			
			var letter = String.fromCharCode(65 + i);
			richMsgUrlPage.find(".typeSideTxt").html(letter);

			var actionTarget = 'ActionType' + totalUrlCount;
			richMsgUrlPage.find(".actionType").attr('name', actionTarget);

			var nameTarget = 'RichMsg' + totalUrlCount;
			richMsgUrlPage.find('.richMsgUrl').attr('name', nameTarget);
			validateNameSet.push(nameTarget);
			console.info("nameTarget:", nameTarget);
			
			appendHtml += '<tr class="richMsgUrlPageTr">' + richMsgUrlPage.html() + '</tr>';
			appendHtml += '<tr class="richMsgUrlTxtTr">' + richMsgUrlTxt.html() + '</tr>';
		}
		
		var jqAppendHtml = $(appendHtml);
		
		// 初始化註記標籤元件並儲存到 tr.richMsgUrlTxtTr
		jqAppendHtml.filter('.richMsgUrlTxtTr').each(function(index, element) {
			var jqElement = $(element);
			jqElement.data('linkTagContentFlag', buildLinkTagContentFlag(jqElement.find('.tagInput')));
		});
		
		$("#richMsgTable").append(jqAppendHtml);
		richMsgUrlTxtKeyupEvent();
		setUrlBtnEvent();
		setActionTypeRadioEvent();
		
		$.each(validateNameSet, function(i, o){

			$('#ContentValidateForm').find('[name="' + o + '"]').rules("add", {
				required : {
			        param: true,
			        depends: function(element) {
						if(btnTarget == "btn_save"){
							return true;
						}
						return false;
			        }
				}
			});
		})
	};
	
	//設定連結文字的input事件
	var richMsgUrlTxtKeyupEvent = function() {
		$('#richMenuName').keyup(function() {
			var txtLength = $(this).val().length;
			var richMsgUrlTxtTr = $(this).closest("tr");
			richMsgUrlTxtTr.find(".MdTxtInputCount").html(txtLength + "/300");
		});
		
		$('#richMenuTitle').keyup(function() {
			var txtLength = $(this).val().length;
			var richMsgUrlTxtTr = $(this).closest("tr");
			richMsgUrlTxtTr.find(".MdTxtInputCount").html(txtLength + "/14");
		});
		
		$(".richMsgLinkTxt").keyup(function(e) {
			var txtLength = $(this).val().length;
			var richMsgUrlTxtTr = $(this).closest("tr");
			richMsgUrlTxtTr.find(".MdTxtInputCount").html(txtLength + "/100");
		});
	};
	
	var clickedUrlInput;
	var clickedUrlTitle;
	//設定showDialog的按鈕
	var setUrlBtnEvent = function() {
		$(".showDialogBtn").click(function() {
			$('#urlDialog').dialog('open');
			$('#urlSelection').css('display','');
			clickedUrlInput = $(this).closest('tr').find('.richMsgUrl'); //點擊showDialogBtn的input
			clickedUrlTitle = $(this).closest('tr').next().find('.richMsgLinkTxt'); //點擊showDialogBtn的input
		});
	}
	
	//取得所有連結
	var getUrlList = function() {
		$.ajax({
            type: 'GET',
            url: bcs.bcsContextPath + "/edit/getLinkUrlList",
		}).success(function(response){
			var appendHtml = "";
			for (var i in response) {
				var urlTr = urlTrTemplate;
				urlTr.find('.urls').html(response[i].linkTitle);
				urlTr.find('.urls').attr('url', response[i].linkUrl);
				
				appendHtml += '<tr class="urlDialogTr" style="cursor: pointer">' + urlTr.html() + '</tr>';
			}
			
			$('#urlDialogTable').append(appendHtml);
			setUrlDialogTrClickEvent();
			setDialogTableStyle();
		}).fail(function(response){
			console.info(response);
			$.FailResponse(response);
		}).done(function(){
		});
	}
	
	//設定Dialog顯示列的點擊事件
	var setUrlDialogTrClickEvent = function() {
		$('.urlDialogTr').click(function(e) {
			$('#urlDialog').dialog('close');
			$('#urlSelection').css('display','none');
			clickedUrlInput[0].value = $(this).find('.urls').attr('url');
			clickedUrlTitle[0].value = $(this).find('.urls').html();
		});
	};
	
	//設定table的css
	var setDialogTableStyle = function() {
		var tableStyle = {
			"margin": "0 auto",
			"font-size": "1.2em",
			"margin-bottom": "15px",
			"width": "100%",
	 		"background": "#fff",
	   		"border-collapse": "collapse",
	   		"border-spacing": "0"
		};
		
		var thStyle = {
			"background": "#8b8b8b",
	 		"color": "#fff",
			"font-weight": "bold",
			"text-align": "center",
		 	"padding": "12px 30px",
		  	"padding-left": "42px"
		};
		
		var tdStyle = {
			"cursor": "pointer",
			"text-align": "center",
		  	"padding": "15px 10px",
		  	"border-bottom": "1px solid #e5e5e5v"
		};
		
		$('#urlDialogTable').css(tableStyle);
		$('#urlDialogTable th').css(thStyle);
		$('#urlDialogTable td').css(tdStyle);
	}
	
	var originalImgHeight = 0;
	var originalImgWidth = 0;
	//上傳圖片
	// 寬高格式：[eq1000, ge1000, le1000, -1]  eq等於、ge以上、le以下。 -1為不驗證
	// 大小格式：[1, -1]  單位MB，驗證是否小於該值。 -1為不驗證
	var imgUploadFormat = {
			titleImage : {rightWidth : 'eq2500', rightHeight : 'eq1686', rightSize : '1'}
	};
	var imgUploadFormat_HALF = {
			titleImage : {rightWidth : 'eq2500', rightHeight : 'eq843', rightSize : '1'}
	};
	//上傳圖片
	$("#titleImage").on("change", function(e) {
		var input = e.currentTarget;
    	if (input.files && input.files[0]) {
    		var fileName = input.files[0].name;
    		console.info("fileName : " + fileName);
    		var form_data = new FormData();
    		
    		var thisImgUploadFormat;
    		if(menuSize == 'FULL'){
    			thisImgUploadFormat	= imgUploadFormat[$(input).attr('name')];
    		}else{
    			thisImgUploadFormat	= imgUploadFormat_HALF[$(input).attr('name')];
    		}
    		thisImgUploadFormat.rightContentType = $(input).attr('accept');
    		
    		form_data.append("filePart",input.files[0]);

    		$('.LyMain').block($.BCS.blockMsgUpload);
    		$.ajax({
                type: 'POST',
                url: bcs.bcsContextPath + "/edit/createResource?resourceType=IMAGE&rightContentType=" + thisImgUploadFormat.rightContentType + "&rightWidth=" + thisImgUploadFormat.rightWidth + "&rightHeight=" + thisImgUploadFormat.rightHeight + "&rightSize=" + thisImgUploadFormat.rightSize + "&isCreateResizeImage=true",
                cache: false,
                contentType: false,
                processData: false,
                data: form_data
    		}).success(function(response){
            	console.info(response);
            	
            	if(typeof response === 'string' && response.startsWith("WARING")){
            		alert(response);
            		$(input).val('');
            		return;
            	}
            	
            	alert("上傳成功!");
            	$('.imgId').val(response.resourceId);
            	$('.mdFRM03Img').find('img').attr('src', bcs.bcsContextPath + '/getResource/IMAGE/' + response.resourceId);
            	$('.mdFRM03Img').find('img').css('width', '100%');
            	originalImgWidth = response.resourceWidth;
            	originalImgHeight = response.resourceHeight;
            	setImgHeightAndWidth(response.resourceId, originalImgWidth, originalImgHeight)
    		}).fail(function(response){
    			console.info(response);
    			$.FailResponse(response);
    			$('.LyMain').unblock();
    		}).done(function(){
    			$('.LyMain').unblock();
    		});
        } 
	});
	
	//設定dialog的圖片長與寬
	var setImgHeightAndWidth = function(imgId, width, height) {
		//圖片大小除以4方便在畫面上的顯示
		width = width / 4;
		height = height / 4;
		$('#customizeFrameTypeLimit').css({
			"width": width, 
			"height": height
		});
		
		$('#customizeImg').css({
			"background-image": "url(" + bcs.bcsContextPath + '/getResource/IMAGE/' + imgId + ")", 
			"background-size": "contain", //圖大於div，fit大小
//			"background-size": "cover", //圖小於div，fit大小
			"background-repeat": "no-repeat",
			"opacity": "0.4", //透明度
			"width": width, 
			"height": height
		});
		
		$('.urlDraggable').resizable({
			handles: "all", //所有邊都可以縮放
			maxHeight: height - 2, //有邊框的關係，所以再減2
			maxWidth: 625,
			minHeight: 30,
			minWidth: 30
	    });
		
		$('.heightScale').remove();
		var appendHtml = "";
		for (var i=50; i<=height; i=i+50) {
			appendHtml += "<label class='heightScale' style='position: absolute; left: 625px; top: " + (i-10) + "px'>-" + (i*4) + "</label>";
		}
		appendHtml += "<label class='heightScale' style='position: absolute; left: 625px; top: " + (height-10) + "px'>-" + (height*4) + "</label>";
		$('#customizeFrameTypeLimit').append(appendHtml);
	}
	
	//將座標資訊轉為逗點分隔
	var getMutliPoint = function(pointsArray) {
		var mulitStartX = pointsArray[0].startX;
		var mulitStartY = pointsArray[0].startY;
		var mulitEndX = pointsArray[0].endX;
		var mulitEndY = pointsArray[0].endY;
		for (var i=1; i<pointsArray.length; i++) {
			mulitStartX = mulitStartX + "," + pointsArray[i].startX;
			mulitStartY = mulitStartY + "," + pointsArray[i].startY;
			mulitEndX = mulitEndX + "," + pointsArray[i].endX;
			mulitEndY = mulitEndY + "," + pointsArray[i].endY;
		}
		var multiPoint = [mulitStartX, mulitStartY, mulitEndX, mulitEndY];
		return multiPoint;
	}
	
	//將產生圖片預覽的資料轉換成陣列
	var parseDataToArray = function() {
		var previewRichMsgImageData = [];
		
		previewRichMsgImageData.push($("input[name='templateFrameType']:checked").val());
		previewRichMsgImageData.push($.BCS.escapeHtml($('#richMenuTitle').val()));
		previewRichMsgImageData.push($('.imgId').val());
		
		var richMsgUrls = "";
		$.each($('.richMsgUrlPageTr').find('.richMsgUrl'), function(i ,v) {
			if (richMsgUrls == "") {
				richMsgUrls = $.BCS.escapeHtml($(this).val());
			} else {
				richMsgUrls = richMsgUrls + "," + $.BCS.escapeHtml($(this).val());
			}
		});
		previewRichMsgImageData.push(richMsgUrls);
		
		var richMsgLinkTitles = "";
		$.each($('.richMsgUrlTxtTr').find('.richMsgLinkTxt'), function(i ,v) {
			var title = $(this).val();
			if(!title){
				title = "-";
			}
			
			if (richMsgLinkTitles == "") {
				richMsgLinkTitles = $.BCS.escapeHtml(title);
			} else {
				richMsgLinkTitles = richMsgLinkTitles + "," + $.BCS.escapeHtml(title);
			}
		});
		previewRichMsgImageData.push(richMsgLinkTitles);
		previewRichMsgImageData.push("");
		
		var multiPoint = [];
		//選擇自訂框架
		if ($("input[name='templateFrameType']:checked").val() == "11" || $("input[name='templateFrameType']:checked").val() == "12") {
			var draggablePositions = getUrlDraggablePosition();
			multiPoint = getMutliPoint(draggablePositions)
		} else {
			multiPoint = getMutliPoint(frameTypePointXY)
		}
		previewRichMsgImageData.push(multiPoint[0]);
		previewRichMsgImageData.push(multiPoint[1]);
		previewRichMsgImageData.push(multiPoint[2]);
		previewRichMsgImageData.push(multiPoint[3]);
		
		//圖片高
		previewRichMsgImageData.push(originalImgHeight);
		//圖片寬
		previewRichMsgImageData.push(originalImgWidth);
		
		return previewRichMsgImageData;
	}
	
	var validateInput = function(){
		var richMsgUrls = $('.richMsgUrl');
		for (var i=0; i<linkNumbers; i++) {
			var actionType = $(richMsgUrls[i]).closest('.richMsgUrlPageTr').find('.actionType:checked').val();
			console.info('actionType', actionType);
			
			if(actionType == 'sendMessage'){
				if(!richMsgUrls[i].value){
					alert("必須輸入文字訊息！");
					return false;
				}
			}
			else if(actionType == 'web'){
			
				if (!richMsgUrls[i].value.lastIndexOf('http://', 0)==0 
						&& !richMsgUrls[i].value.lastIndexOf('https://', 0)==0
						&& !richMsgUrls[i].value.lastIndexOf('BcsPage:', 0)==0) {
					alert("URL必須包含http或是BcsPage字樣！");
					return false;
				}
			}else if(actionType == 'postback'){
				if(!richMsgUrls[i].value){
					alert("必須輸入文字訊息！");
					return false;
				}
			}
		}
		return true;
	}
	
	$('#save').click(function() {

		btnTarget = "btn_save";
		if (!validator.form()) {
			return;
		}
		
		if ($("#richMenuName").val() == "") {
			alert("請輸入圖文選單名稱！");
			return;
		}
		
		if ($("#richMenuTitle").val() == "") {
			alert("請輸入圖文選單標題！");
			return;
		}
		
		if ($('.imgId').val() == "") {
			alert("請上傳背景圖像！");
			return;
		}
		
		var richMsgUrls = $('.richMsgUrl');
		var validate = true;
		$.each(richMsgUrls, function(i , o){
			if(validate){
				if (!o.value) {
					alert("部份資料尚未輸入！");
					validate = false;
					return
				}

				var actionType = $(o).closest('.richMsgUrlPageTr').find('.actionType:checked').val();
				console.info('actionType', actionType);
				
				if(actionType == 'sendMessage'){
					if(!o.value){
						alert("必須輸入文字訊息！");
						validate = false;
						return
					}
				}
				else if(actionType == 'web'){
					if (!o.value.lastIndexOf('http://', 0) == 0 
							&& !o.value.lastIndexOf('https://', 0) == 0
							&& !o.value.lastIndexOf('BcsPage:', 0) == 0) {
						alert("URL必須包含http或是BcsPage字樣！");
						validate = false;
						return
					}
				}else{
					if(!o.value){
						alert("必須輸入文字訊息！");
						validate = false;
						return
					}
				}
			}
		});
		if(!validate){
			return;
		}
		
		var saveConfirm = confirm("請確認是否儲存");
		if (!saveConfirm) return; //點擊取消
		
		var richMsgImgUrls = [];
		
		var richMsgUrls = $('.richMsgUrlPageTr').find('.richMsgUrl');
		var richMsgUrlTxtTr = $('.richMsgUrlTxtTr');
		var richMsgLinkTitles = richMsgUrlTxtTr.find('.richMsgLinkTxt');
		var richMsgLetters = $('.typeSideTxt');

		
		//選擇自訂框架
		if ($("input[name='templateFrameType']:checked").val() == "11" || $("input[name='templateFrameType']:checked").val() == "12" ) {
			var draggablePositions = getUrlDraggablePosition();
			for (var i in draggablePositions) {
				
				var actionType1 = $(richMsgUrls[i]).closest('.richMsgUrlPageTr').find('.actionType:checked').val();
				var linkUrl = richMsgUrls[i].value;
				if(actionType1 == 'postback'){
					var goToList = $(richMsgUrls[i]).closest('.richMsgUrlPageTr').find('.goToList')[0];
					console.info("goToList", goToList);
					//var path = goToList.options[goToList.selectedIndex].value;
					//console.info("path", path);
					//linkUrl = path;
					var column = goToList.options[goToList.selectedIndex]; //find("input[name='richId']");
					console.info('column:', column);
					console.info('column.innerText:', column.innerText);
					if(column.innerText == '請選擇'){
						linkUrl = 'NULL';
					}else{
						var child = $(column).find("input[name='richId']")[0];
						console.info('child:', child.value);
						linkUrl = child.value;
					}
				}
				console.info("i=", i);
				console.info("actionType1:", actionType1);
				console.info("linkUrl:", linkUrl);
				
				if (draggablePositions[i].endX > originalImgWidth || draggablePositions[i].endY > originalImgHeight
						|| draggablePositions[i].startX < 0 || draggablePositions[i].startY < 0) {
					alert("自訂連結區塊超出圖片範圍，請再次確認");
					return;
				}
				
				richMsgImgUrls.push({
					richDetailLetter : richMsgLetters[i].innerText,
					startPointX : draggablePositions[i].startX,
					startPointY : draggablePositions[i].startY,
					endPointX : draggablePositions[i].endX,
					endPointY : draggablePositions[i].endY,
					linkUrl : linkUrl,
					linkTitle : richMsgLinkTitles[i].value,
					linkTagList : richMsgUrlTxtTr.eq(i).data('linkTagContentFlag').getContentFlagList(),
					actionType : $(richMsgUrls[i]).closest('.richMsgUrlPageTr').find('.actionType:checked').val()
				});
			}
		} else {
			$.each(richMsgUrls, function(i, v) {
				var actionType1 = $(richMsgUrls[i]).closest('.richMsgUrlPageTr').find('.actionType:checked').val();
				var linkUrl = richMsgUrls[i].value;
				if(actionType1 == 'postback'){
					var goToList = $(richMsgUrls[i]).closest('.richMsgUrlPageTr').find('.goToList')[0];
					console.info("goToList", goToList);
					//var path = goToList.options[goToList.selectedIndex].value;
					//var path = goToList.options[goToList.selectedIndex].innerText;
					//var child = goToList.options[goToList.selectedIndex].find
					// $("input[name='templateFrameType']");
					var column = goToList.options[goToList.selectedIndex]; //find("input[name='richId']");
					console.info('column:', column);
					console.info('column.innerText:', column.innerText);
					if(column.innerText == '請選擇'){
						linkUrl = 'NULL';
					}else{
						var child = $(column).find("input[name='richId']")[0];
						console.info('child:', child.value);
						linkUrl = child.value;
					}
					
				}
				console.info("i=", i);
				console.info("actionType1:", actionType1);
				console.info("linkUrl:", linkUrl);
				
				richMsgImgUrls.push({
					richDetailLetter : richMsgLetters[i].innerText,
					startPointX : frameTypePointXY[i].startX,
					startPointY : frameTypePointXY[i].startY,
					endPointX : frameTypePointXY[i].endX,
					endPointY : frameTypePointXY[i].endY,
					linkUrl : linkUrl,
					linkTitle : richMsgLinkTitles[i].value,
					linkTagList : richMsgUrlTxtTr.eq(i).data('linkTagContentFlag').getContentFlagList(),
					actionType : $(richMsgUrls[i]).closest('.richMsgUrlPageTr').find('.actionType:checked').val()
				});
			});
		}
		
        // 使用效期
        //var momentRichMenuStartUsingTime = getMomentByElement('richMenuStartUsingTime');
        //var momentRichMenuEndUsingTime = getMomentByElement('richMenuEndUsingTime');
		
		postData = {
			richType : $('input[name="templateFrameType"]:checked').val(),
			richMenuName : $('#richMenuName').val(),
			richMenuTitle : $('#richMenuTitle').val(),
			richImageId : $('.imgId').val(),
			richMenuImgUrls : richMsgImgUrls,
			richMenuShowStatus : $('input[name="richMenuShowStatus"]:checked').val(),
			changeCondition : $('.changeConditionSelect').val(),
			menuSize : menuSize,
            //richMenuStartUsingTime : momentRichMenuStartUsingTime.format(dateFormat),
            //richMenuEndUsingTime : momentRichMenuEndUsingTime.format(dateFormat),
			richMenuGroupId : groupId
		}
		console.info(postData);
		
		$('.LyMain').block($.BCS.blockMsgSave);
		$.ajax({
			type : "POST",
			url : bcs.bcsContextPath + '/edit/createRichMenu?actionType=' + actionType + '&richId=' + richId,
            cache: false,
            contentType: 'application/json',
            processData: false,
			data : JSON.stringify(postData)
		}).success(function(response){
			console.info(response);
			if (actionType == "Edit") {
				alert("儲存圖文訊息成功！");
			} else {
				alert("建立圖文訊息成功！");
			}
			window.location.replace(bcs.bcsContextPath + '/edit/richMenuMemberListPage?groupId=' + groupId);
		}).fail(function(response){
			console.info(response);
			$.FailResponse(response);
			$('.LyMain').unblock();
		}).done(function(){
			$('.LyMain').unblock();
		});
	});
	
	//取得各個連結區塊的座標
	var getUrlDraggablePosition = function() {
		var draggablePositions = [];
		$.each($(".urlDraggable"), function() {
			//div長寬為原圖的四分之一，因此儲存座標值需要x4
			var startX = Number($(this).css("left").replace("px", "").replace("auto", "0")) * 4;
			var startY = Number($(this).css("top").replace("px", "").replace("auto", "0")) * 4;
			var height = Number($(this).height()) * 4;
			var width = Number($(this).width()) * 4;
			
			var draggablePosition = {
					startX : startX,
					endX : startX + width,
					startY : startY,
					endY : startY + height
			}
			draggablePositions.push(draggablePosition);
		});
		return draggablePositions;
	}
	
	$('#urlDialog').dialog({
    	autoOpen: false, //初始化不會是open
    	resizable: false, //不可縮放
    	modal: true, //畫面遮罩
    	draggable: false, //不可拖曳
    	minWidth : 500,
    	position: { my: "top", at: "top", of: window  }
    });
	$('#customizeDialog').dialog({
		autoOpen: false, //初始化不會是open
		resizable: false, //不可縮放
		modal: true, //畫面遮罩
		draggable: false, //不可拖曳
		minWidth : 700,
    	position: { my: "top", at: "top", of: window  }
	});

	//增加自訂連結
	$('#addUrl').click(function() {
		if (linkNumbers >= 20) {
			alert("至多20個連結！");
			return;
		}
		linkNumbers++;
		generateRichMsgUrl();
		getGoToList();
		var letter = String.fromCharCode(64 + linkNumbers);
		setDraggable(letter);
	});
	
	//設定連結區塊
	var setDraggable = function(letter) {
		var urlDraggable = "<div class='urlDraggable' style='position: absolute; left: 0px; top: 0px; width: 50px; height: 50px; border: 1px solid'><p style='position: absolute; top: 50%; left: 50%; margin-top: -16px; margin-left: -7px; font-size:24px'>" + letter + "</p></div>"
		$('#customizeFrameTypeLimit').append(urlDraggable);
		
		$('.urlDraggable').last().draggable({
			containment: "#customizeFrameTypeLimit", 
			scroll: false //不會出現卷軸
		});
		
		var maxHeight = $('#customizeFrameTypeLimit').height() - 2; //有邊框的關係，所以再減2
		$('.urlDraggable').last().resizable({
			handles: "all", //所有邊都可以縮放
			maxHeight: maxHeight,
			maxWidth: 623,
			minHeight: 30,
			minWidth: 30
	    });
	}
	
	//刪除一個自訂連結
	$('#removeUrl').click(function() {
		if (linkNumbers == 1) {
			alert("至少要有一個連結！");
			return;
		}
		linkNumbers--;
		$('.richMsgUrlPageTr').last().remove();
		$('.richMsgUrlTxtTr').last().remove();
		$('.urlDraggable').last().remove();
	});
	
	//顯示連結拖曳的Dialog
	$('#showCustomizeDialog').click(function() {
		
		btnTarget = "showCustomizeDialog";
		if (!validator.form()) {
			return;
		}
		
		if ($('.imgId').val() == "") {
			alert("請上傳背景圖像！");
			return;
		}
		$('#customizeDialog').dialog('open');
	});
	
	//customizeDialog的確認鈕
	$('#savePosition').click(function() {
		var draggablePositions = getUrlDraggablePosition();
		for (var i in draggablePositions) {
			if (draggablePositions[i].endX > originalImgWidth || draggablePositions[i].endY > originalImgHeight
					|| draggablePositions[i].startX < 0 || draggablePositions[i].startY < 0) {
				alert("自訂連結區塊超出圖片範圍，請再次確認");
				return;
			}
		}
		
		$('#customizeDialog').dialog('close');
	});
	

	
	initTemplate();
	getDataByRichId();
	getUrlList();
});