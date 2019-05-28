/**
 * 
 */
$(function(){
	$('.btn_add').click(function(){
 		window.location.replace('richMenuCreatePage');
	});
	$('.ActiveBtn').click(function(){
 		window.location.replace(bcs.bcsContextPath +'/edit/richMenuListPage');
	});
	$('.DisableBtn').click(function(){
 		window.location.replace(bcs.bcsContextPath +'/edit/richMenuListDeletePage');
	});
	
	$('.exportReport').click(function(){
		var richMsgTr = $(this).closest(".richMsgTrTemplate");
		var richId = richMsgTr.find('.richId').val();
		
		var url =  bcs.bcsContextPath + '/edit/exportToExcelForRichMenuChangeReport?richId=' + richId;
		
		var downloadReport = $('#downloadReport');
		downloadReport.attr("src", url);
	});

	var loadDataFunc = function(queryObj, page){
		var thisQueryFlag = queryObj.queryFlag? queryObj.queryFlag : '';
		var thisIsAsc = queryObj.isAsc? queryObj.isAsc : 'false';
		
		$('.LyMain').block($.BCS.blockMsgRead);
		
		$.ajax({
			type : "GET",
			url : bcs.bcsContextPath + '/edit/getRichMenuList?queryFlag='+ encodeURIComponent(thisQueryFlag) + '&page=' + page + '&isAsc=' + thisIsAsc + '&status=DISABLE'
		}).success(function(response){
			console.log(response);
			$('.richMsgTrTemplate').remove();
			
			for(key in response){
				var richMsgTr = richMsgTrTemplate.clone(true);

				var valueObj = response[key];
				console.info('valueObj', valueObj);

				richMsgTr.find('.richId').val(key);
				richMsgTr.find('.richMenuId').val(valueObj[12]);
				richMsgTr.find('.richMenuName').html(valueObj[0]);
				richMsgTr.find('.richMenuImgTitle img').attr('richId', key);
				richMsgTr.find('.richMenuImgTitle img').attr('src', bcs.bcsContextPath + "/getResource/IMAGE/" + valueObj[4]);
				richMsgTr.find('.richMenuImgTitle img').click(richMenuSelectEventFunc);
				richMsgTr.find('.richMenuImgTitle a').attr('href', bcs.bcsContextPath + '/edit/richMenuCreatePage?richId=' + key + '&actionType=Edit');
				var urls = [];
				if(valueObj[1]){
					urls = valueObj[1].split(",");
				}
				var titles = [];
				if(valueObj[5]){
					titles = valueObj[5].split(",");
				}
				var actions = [];
				if(valueObj[8]){
					actions = valueObj[8].split(",");
				}
				var sendMessages = [];
				if(valueObj[9]){
					sendMessages = valueObj[9].split(",");
				}
				var urlHtml = "";
				if(urls != null && urls.length > 0){
					for (var i = 0; i < urls.length; i++) {
						var title = titles[i];
						if(!title){
							title = urls[i];
						}
						var action = actions[i];
						if(!action){
							action = "連結";
							urlHtml += action + "-<a href='" + urls[i] + "' target='_blank'>" + title + "</a><br/>";
						}
						else{
							if("web" == action){
								action = "連結";
								urlHtml += action + "-<a href='" + urls[i] + "' target='_blank'>" + title + "</a><br/>";
							}
							else if("postback" == action){
								action = "隱藏";
								urlHtml += action + "-" + sendMessages[i] + "<br/>";
							}
							else{
								action = "文字";
								urlHtml += action + "-" + sendMessages[i] + "<br/>";
							}
						}
					}
				}
				else{
					for (var i = 0; i < actions.length; i++) {
						if(actions[i] == "postback"){
							var action = "隱藏";
							urlHtml += action + "-" + sendMessages[0] + "<br/>";
						}else{
							var action = "文字";
							urlHtml += action + "-" + sendMessages[0] + "<br/>";
						}
					}
				}
				richMsgTr.find('.richMenuImgUrl').html(urlHtml);
				
				var condition;
				if(valueObj[7] == 'BINDED'){
					condition = "已綁定";
				}else{
					condition = "未綁定"
				}
				richMsgTr.find('.richMenuChangeCondition').html(condition);
				
				var time = valueObj[2].replace(/\.\d+$/, ''); // 刪去毫秒
				richMsgTr.find('.richMsgCreateTime').html(time);
				richMsgTr.find('.richMsgCreateUser').html(valueObj[3]);
				
				// 使用期間
				var startUsetime = valueObj[13].replace(/\.\d+$/, ''); // 刪去毫秒
				var endUsetime = valueObj[14].replace(/\.\d+$/, ''); // 刪去毫秒
				richMsgTr.find('.richMenuUseTime').html(startUsetime + " ~ " + endUsetime);
				
				$('#richMsgListTable').append(richMsgTr);
			}
			
			setActiveBtnEvent();
			setCopyBtnEvent();
			setDeleteBtnEvent();
		}).fail(function(response){
			console.info(response);
			$.FailResponse(response);
			$('.LyMain').unblock();
		}).done(function(){
			$('.LyMain').unblock();
		});
	};
	
	var richMenuSelectEventFunc = function(){
		var richId = $(this).attr('richId');
 		window.location.replace(bcs.bcsContextPath + '/edit/richMenuCreatePage?richId=' + richId + '&actionType=Edit');
	};
	
	// 啟用圖文選單
	var setActiveBtnEvent = function() {
		$('.btn_redeisgn').click(function(e) {
			var deleteConfirm = confirm("請確認是否啟用");
			if (!deleteConfirm) return; //點擊取消
			
			var richMsgTr = $(this).closest(".richMsgTrTemplate");
			var selectedContentRichMenuId = richMsgTr.find('.richId').val();
			
			$('.LyMain').block($.BCS.blockMsgSave);
			$.ajax({
				type : "DELETE",
				url : bcs.bcsContextPath + '/edit/activeRichMenuStatus?richId=' + selectedContentRichMenuId
			}).success(function(response){
				alert("變更成功！");
				$initPagination();
			}).fail(function(response){
				console.info(response);
				$.FailResponse(response);
				$('.LyMain').unblock();
			}).done(function(){
				$('.LyMain').unblock();
			});
		});
		
	}
	
	var setCopyBtnEvent = function() {
		$('.btn_copy').click(function(e) {
			var copyConfirm = confirm("請確認是否複製");
			if (!copyConfirm) return; //點擊取消
			
			var richMsgTr = $(this).closest(".richMsgTrTemplate");
			var selectedContentRichMenuId = richMsgTr.find('.richId').val();
			
			window.location.replace(bcs.bcsContextPath + '/edit/richMenuCreatePage?richId=' + selectedContentRichMenuId + '&actionType=Copy');
		});
		
	}
	
	var setDeleteBtnEvent = function() {
		$('.btn_detele').click(function(e) {
			var deleteConfirm = confirm("請確認是否刪除");
			if (!deleteConfirm) return; //點擊取消
			
			var richMsgTr = $(this).closest(".richMsgTrTemplate");
			var selectedContentRichMenuId = richMsgTr.find('.richId').val();
			
			$('.LyMain').block($.BCS.blockMsgSave);
			$.ajax({
				type : "DELETE",
				url : bcs.bcsContextPath + '/admin/deleteRichMenu/' + selectedContentRichMenuId
			}).success(function(response){
				alert("刪除成功！");
				$initPagination();
			}).fail(function(response){
				console.info(response);
				$.FailResponse(response);
				$('.LyMain').unblock();
			}).done(function(){
				$('.LyMain').unblock();
			});
		});
		
	}

	var richMsgTrTemplate = {};
	var initTemplate = function(){
		richMsgTrTemplate = $('.richMsgTrTemplate').clone(true);
		$('.richMsgTrTemplate').remove();
	}
	
	var loadTotalPagesFunc = function(queryObj){
		
		var thisQueryFlag = queryObj.queryFlag? queryObj.queryFlag : '';
		
		var totalPages = -1;
		
		$.ajax({
			//async:false,
			type : "GET",
			url : bcs.bcsContextPath + '/edit/getRichMenuPageTotal?queryFlag=' + encodeURIComponent(thisQueryFlag)
		}).success(function(response){
			
			totalPages = response + 1;
			
		}).fail(function(response){
			console.info(response);
			$.FailResponse(response);
		}).done(function(){
		});
		
		return totalPages;
	}

	initTemplate();
	$initPagination(loadDataFunc, loadTotalPagesFunc);
});