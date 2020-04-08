/**
 * 
 */
$(function() {
	
	var selectedGroupType = 'UID_LIST';
	
	var btnUploadMid = document.getElementById("btnUploadMid");
	
	var uploadedUidFiles = [];
	
	/**
	 * 紀錄 最後按鈕
	 */
	var btnTarget = "";

	// 表單驗證
	var validator = $('#formSendGroup').validate({
		rules : {

			// 群組名稱
			'groupTitle' : {
				required : {
					param : true,
					depends : function(element) {
						if (btnTarget == "btn_richmenu_save") {
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
					param : true,
					depends : function(element) {
						if (btnTarget == "btn_richmenu_save") {
							return true;
						}
						return false;
					}
				},
				maxlength : 700
			}
		}
	});

	/**
	 * 為新增的一列群組條件加上驗證規則
	 */
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
					maxlength : 100
				});
				// 下拉選單
			} else {
				queryValue.rules("add", {
					required : true
				});
			}
		});
	};

	// 發送類型
	$('[name="groupType"]').click(function() {

		var confirmStr = "是否確定更改群組類型?\n確認更改後，將會清除群組條件。\n如需依照條件篩選，需要重新添加並設定。";
		
		var r = confirm(confirmStr);
		if (!r) {
			var previousSelected =  $("input[name=groupType]:checked");
			previousSelected.checked = true;
	        return false;
		}
		
		selectedGroupType = $('[name="groupType"]:checked').val();
		console.info('selectedGroupType = ', selectedGroupType);

		if (selectedGroupType == "UIDLIST") {
			$('.upload_mid').show;
			btnUploadMid.style.display = "block";
			
		} else if (selectedGroupType == "CONDITIONS") {
			$('.upload_mid').remove;
			btnUploadMid.style.display = "none";
		}

		$('.dataTemplate').remove();
		
		uploadedUidFiles = [];
		uploadedUidFiles.length = 0;
	});

	$('.btn_richmenu_save').click(function() {
		var queryDataDoms = $('.dataTemplate');

		if (queryDataDoms.length == 0) {
			alert('請設定群組條件');
			return;
		}

		btnTarget = "btn_richmenu_save";
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

		if (groupId && actionType == 'Edit') {
			msgAction = "Change"
		} else if (actionType == 'Copy') {
			groupId = null;
		}

		// Get Query Data
		var sendGroupDetail = [];
		$.each(queryDataDoms, function(i, o) {
			var dom = $(o);
			var queryData = {};

			if (dom.find('.labelField').is(':visible')) {
				queryData.queryField = 'UploadMid';
				queryData.queryOp = dom.find('.labelValue').attr('fileName');
				queryData.queryValue = dom.find('.labelValue').attr('referenceId') + ":" + dom.find('.labelValue').attr('count');
			} else {
				queryData.queryField = dom.find('.queryField').val();
				queryData.queryOp = dom.find('.queryOp').val();
				queryData.queryValue = dom.find('.queryValue:visible').val();
			}
			
			queryData.groupType = selectedGroupType;

			sendGroupDetail.push(queryData);
		});

		console.info('sendGroupDetail', sendGroupDetail);

		var postData = {};
		postData.groupId = groupId;
		postData.groupTitle = groupTitle;
		postData.groupDescription = groupDescription;
		postData.sendGroupDetail = sendGroupDetail;
		postData.groupType = selectedGroupType;

		console.info('postData', postData);

		/**
		 * Do Confirm Check
		 */
		var confirmStr = "請確認是否建立";
		if (msgAction == "Change") {
			confirmStr = "請確認是否儲存";
		}
		
		var r = confirm(confirmStr);
		if (!r) {
			return;
		}


		$('.LyMain').block($.BCS.blockRichmenuGroupListCreating);
		
		$.ajax({
			type : "POST",
			url : bcs.bcsContextPath + '/market/createSendGroup',
			cache : false,
			contentType : 'application/json',
			processData : false,
			data : JSON.stringify(postData)
		}).success(function(response) {
			console.info(response);
			alert('儲存成功');
			$('.LyMain').unblock();
			window.location.replace(bcs.bcsContextPath + '/edit/hpiRichMenuGroupListPage');
		}).fail(function(response) {
			console.info(response);
			$.FailResponse(response);
			$('.LyMain').unblock();
		}).done(function() {
			$('.LyMain').unblock();
		});
	});

	var getDetailFunc = function() {

		var queryDataDoms = $('.dataTemplate');

		var groupId = $.urlParam("groupId");

		var groupTitle = $('#groupTitle').val();
		console.info('groupTitle', groupTitle);

		var postData = {};
		postData.groupTitle = groupTitle;

		if (groupId < 0) { // 預設群組不需要設定
			postData.groupId = groupId;
		} else {
			if (queryDataDoms.length == 0) {
				alert('請設定群組條件');
				return;
			}

			btnTarget = "btn_richmenu_query";
			if (!validator.form()) {
				return;
			}
		}

		// Get Query Data
		var sendGroupDetail = [];
		$.each(queryDataDoms, function(i, o) {
			var dom = $(o);
			var queryData = {};

			if (dom.find('.labelField').is(':visible')) {
				queryData.queryField = 'UploadMid';
				queryData.queryOp = dom.find('.labelValue').attr('fileName');
				queryData.queryValue = dom.find('.labelValue').attr('referenceId') + ":" + dom.find('.labelValue').attr('count');
			} else {
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
	$('.btn_richmenu_exportMid').click(function() {
		var postData = getDetailFunc();
		console.info('postData', postData);

		if (!postData) {
			return;
		}

		$.ajax({
			type : "POST",
			url : bcs.bcsContextPath + '/market/createSendGroupMidExcelTemp',
			cache : false,
			contentType : 'application/json',
			processData : false,
			data : JSON.stringify(postData)
		}).success(function(response) {
			console.info(response);

			if (response.count > 0) {
				var url = bcs.bcsContextPath + '/market/exportToExcelForSendGroup?tempId=' + response.tempId;

				var downloadReport = $('#downloadReport');
				downloadReport.attr("src", url);
			} else {
				alert('查詢結果共 ' + response.count + ' 筆');
			}
		}).fail(function(response) {
			console.info(response);
			$.FailResponse(response);
		}).done(function() {
		});
	});

	$('#downloadReport').load(function() {
		// if the download link return a page
		// load event will be triggered
		$('.LyMain').unblock();
	});

	// 條件結果按鍵
	$('.btn_richmenu_query').click(function() {
		var postData = getDetailFunc();
		console.info('postData', postData);
		if (!postData) {
			return;
		}

		// 遮蓋效果UI
		$('.LyMain').block($.BCS.blockMsgResultQuerying);
		$.ajax({
			type : "POST",
			url : bcs.bcsContextPath + '/market/getSendGroupConditionResult',
			cache : false,
			contentType : 'application/json',
			processData : false,
			data : JSON.stringify(postData)
		}).success(function(response) {
			$('.LyMain').unblock();
			console.info('response = ', response);
			alert('查詢結果共 ' + response + ' 筆資料符合。');
		}).fail(function(response) {
			$('.LyMain').unblock();
			console.info('response = ', response);
			$.FailResponse(response);
		}).done(function() {
			$('.LyMain').unblock();
		});
	});

	$('.btn_richmenu_cancel').click(function() {

		var r = confirm("請確認是否取消");
		if (r) {
			// confirm true
		} else {
			return;
		}

		window.location.replace(bcs.bcsContextPath + '/edit/hpiRichMenuGroupListPage');
	});

	var btn_deteleFunc = function() {
		
		var conditionType = $(this).closest('tr').find('.labelField').html();
		console.info('conditionType = ', conditionType);
		
		var confirmStr = "是否確定刪除此" + ((conditionType === '匯入UID')? "匯入UID紀錄?" : "條件?");
		
		if (!confirm(confirmStr)) {
	        return;
		}
		
		if (conditionType === '匯入UID') {
			var deletedFileName = $(this).closest('tr').find('.labelOp').html();
			console.info('deletedFileName = ', deletedFileName);
			
			const index = uploadedUidFiles.indexOf(deletedFileName.trim());

			if (index > -1) {
				uploadedUidFiles.splice(index, 1);
			}
		}
		
		$(this).closest('tr').remove();
	};

	/**
	 * 選擇[欄位]要動態切換[條件]下拉選單的選項、[數值]元件
	 * 
	 * @param queryFieldSelect
	 *            [欄位]下拉選單
	 */
	var setGroupQueryComponent = function(queryFieldSelect) {
		if (!sendGroupCondition) {
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
				queryValueComponentSelectList.find('select').append('<option value="' + sendGroupQueryTag.queryFieldTagValue + '">' + sendGroupQueryTag.queryFieldTagDisplay + '</option>');
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

	var optionSelectChange_func = function() {
		var select = $(this);
		var selectValue = select.find('option:selected').text();
		select.closest('.optionForRichmenuCreateGroup').find('.optionLabel').html(selectValue);

		// 若是[欄位]下拉選單
		if (select.hasClass('queryField')) {
			setGroupQueryComponent(select);
		}
	};

	$('.add_rule').click(function() {
		var queryBody = templateBody.clone(true);
		queryBody.find('.btn_delete').click(btn_deteleFunc);
		queryBody.find('.optionSelect').change(optionSelectChange_func);
		queryBody.find(".datepicker").datepicker({
			'dateFormat' : 'yy-mm-dd'
		});
		$('#tableBody').append(queryBody);
		setValidationOnNewRow();
	});

	$('.upload_mid').click(function() {
		$('#upload_mid_btn').click();
	});

	$('#upload_mid_btn').on("change", function(ev) {

		var input = ev.currentTarget;
		
		if (input.files && input.files[0]) {
			var fileName = input.files[0].name;
			console.info('fileName = ', fileName);

			if (uploadedUidFiles.includes(fileName)) {
				alert('您選擇的檔案已存在，請重新選擇。');
				
			    this.value = '';
		        return;
			}
			
			uploadedUidFiles.push(fileName);
			
			var form_data = new FormData();
			form_data.append("filePart", input.files[0]);

			// 遮蓋效果UI
			$('.LyMain').block($.BCS.blockMsgUpload);
			$.ajax({
				type : 'POST',
				url : bcs.bcsContextPath + '/market/uploadRichmenuMidSendGroup',
				cache : false,
				contentType : false,
				processData : false,
				data : form_data
			}).success(function(response) {
				console.info('response = ', response);
				alert("匯入成功!");
				
				var queryBody = templateBody.clone(true);
				queryBody.find('.btn_delete').click(btn_deteleFunc);
				
				queryBody.find('.labelField').html("匯入UID");
				queryBody.find('.labelField').show();
				
				queryBody.find('.labelOp').html(fileName);
				queryBody.find('.labelOp').show();
				
				queryBody.find('.labelValue').html(response.count + " 筆 UID");
				queryBody.find('.labelValue').show();
				
				queryBody.find('.optionForRichmenuCreateGroup').remove();

				queryBody.find('.labelValue').attr('fileName', fileName);
				queryBody.find('.labelValue').attr('referenceId', response.referenceId);
				
				queryBody.find('.labelValue').attr('count', response.count);
				
				$('#tableBody').append(queryBody);
			}).fail(function(response) {
				console.info('response = ', response);
				
				if (response.status == 503)
					$.TimeoutFailResponse(response);
				else
					$.FailResponse(response);
				
				$('.LyMain').unblock();
			}).done(function() {
				$('.LyMain').unblock();
			});
		}
		
	    this.value = '';
	});

	var loadDataFunc = function() {

		$('.LyMain').block($.BCS.blockRichmenuGroupPageLoading);
		
		// 取得群組條件各個下拉選項值
		$.ajax({
			type : "GET",
			url : bcs.bcsContextPath + '/market/getSendGroupCondition'
		}).success(function(response) {
			console.info('GroupCondition = ', response);
			sendGroupCondition = response;

			$.each(sendGroupCondition, function(queryFieldId, queryFieldObject) {
				templateBody.find('.queryField').append('<option value="' + queryFieldId + '">' + queryFieldObject.queryFieldName + '</option>');
			});

			var groupId = $.urlParam("groupId");

			if (groupId) {

				$.ajax({
					type : "GET",
					url : bcs.bcsContextPath + '/market/getSendGroup?groupId=' + groupId
				}).success(function(response) {
					console.info(response);

					$('.dataTemplate').remove();

					$('#groupTitle').val(response.groupTitle);
					$('#groupDescription').val(response.groupDescription);
					
					if (response.groupType == 'CONDITIONS') {
						$('#groupType_UIDLIST').prop('checked', false);
						$('#groupType_CONDITIONS').prop('checked', true);
						
						btnUploadMid.style.display = "none";
					}
					else {
						$('#groupType_UIDLIST').prop('checked', true);
						$('#groupType_CONDITIONS').prop('checked', false);
						
						btnUploadMid.style.display = "block";
					}

					if (groupId > 0) {
						$.each(response.sendGroupDetail, function(i, o) {

							var queryBody = templateBody.clone(true);
							queryBody.find(".datepicker").datepicker({
								'dateFormat' : 'yy-mm-dd'
							});
							queryBody.find('.optionSelect').change(optionSelectChange_func);

							if ('UploadMid' == o.queryField) {

								var split = o.queryValue.split(':');

								queryBody.find('.labelField').html("UID匯入");
								queryBody.find('.labelField').show();
								queryBody.find('.labelOp').html(o.queryOp);
								queryBody.find('.labelOp').show();
								queryBody.find('.labelValue').html(split[1] + " 筆 UID");
								queryBody.find('.labelValue').show();
								queryBody.find('.optionForRichmenuCreateGroup').remove();

								queryBody.find('.labelValue').attr('fileName', o.queryOp);
								queryBody.find('.labelValue').attr('referenceId', split[0]);
								queryBody.find('.labelValue').attr('count', split[1]);
							} else {
								queryBody.find('.queryField').val(o.queryField).change();
								queryBody.find('.queryOp').val(o.queryOp).change();
								queryBody.find('.queryValue').val(o.queryValue).change();
							}

							queryBody.find('.btn_delete').click(btn_deteleFunc);

							$('#tableBody').append(queryBody);
							setValidationOnNewRow();
						});
					} else {
						$('#groupTitle').attr('disabled', true);
						$('#groupDescription').attr('disabled', true);

						$('.btn_richmenu_save').remove();
						$('#queryContent').remove();
					}

				}).fail(function(response) {
					console.info(response);
					$.FailResponse(response);
					$('.LyMain').unblock();
				}).done(function() {
					$('.LyMain').unblock();
				});

				var actionType = $.urlParam("actionType");

				if (actionType == "Edit") {
					$('.CHTtl').html('編輯發送群組');
				} else if (actionType == "Copy") {
					$('.CHTtl').html('複製發送群組');
				}
			} else {
				$('#groupType_UIDLIST').prop('checked', true);
				$('#groupType_CONDITIONS').prop('checked', false);
				
				selectedGroupType = 'UID_LIST';
				
				$('.LyMain').unblock();
			}
				
		}).fail(function(response) {
			console.info(response);
			$.FailResponse(response);
			$('.LyMain').unblock();
		}).done(function() {
		});
	};

	$.TimeoutFailResponse = function(response) {
		var str = "";
		if (response && response.status == 503) {
			str = "\n[" + "資料量過大導致超時，請再重新匯入。" + "]";
		}

		alert(str);
		$('.LyMain').unblock();
	}
	
	var templateBody = {};
	templateBody = $('.dataTemplate').clone(true);
	$('.dataTemplate').remove();

	loadDataFunc();
});