/**
 * 
 */
$(function(){
	var groupId = 1;
	
	$('.btn_add').click(function(){
 		//window.location.replace('richMenuCreatePage');
		window.location.replace(bcs.bcsContextPath +'/edit/richMenuCreatePage?groupId=' + groupId);
	});
	$('.btn_cancel').click(function() {

//		var r = confirm("請確認是否取消");
//		if (r) {
//			// confirm true
//		} else {
//		    return;
//		}
//		
		window.location.replace(bcs.bcsContextPath + '/edit/richMenuGroupPage');
	});
	
//	$('.ActiveBtn').click(function(){
// 		window.location.replace(bcs.bcsContextPath +'/edit/richMenuListPage');
//	});
//	$('.DisableBtn').click(function(){
// 		window.location.replace(bcs.bcsContextPath +'/edit/richMenuListDeletePage');
//	});
	
	$('.exportReport').click(function(){
		var richMsgTr = $(this).closest(".richMsgTrTemplate");
		var richId = richMsgTr.find('.richId').val();
		
		var url =  bcs.bcsContextPath + '/edit/exportToExcelForRichMenuChangeReport?richId=' + richId;
		
		var downloadReport = $('#downloadReport');
		downloadReport.attr("src", url);
	});

	var loadDataFunc = function(){ //queryObj, page
		//var thisQueryFlag = queryObj.queryFlag? queryObj.queryFlag : '';
		//var thisIsAsc = queryObj.isAsc? queryObj.isAsc : 'false';
		
		$('.LyMain').block($.BCS.blockMsgRead);
		
		//console.info("url:", bcs.bcsContextPath + '/edit/getRichMenuList?queryFlag='+ encodeURIComponent(thisQueryFlag) + '&page=' + page + '&isAsc=' + thisIsAsc + '&status=ACTIVE');
		
		var link = bcs.bcsContextPath + '/edit/getRichMenuListByRichMenuGroupId/' + groupId;
		//var link = bcs.bcsContextPath + '/edit/getRichMenuList?queryFlag='+ encodeURIComponent(thisQueryFlag) + '&page=' + page + '&isAsc=' + thisIsAsc + '&status=ACTIVE';
		
		$.ajax({
			type : "GET",
			url : link
		}).success(function(response){
			console.log(response);
			$('.richMsgTrTemplate').remove();
			
			//for(key in response){
			$.each(response, function(i, o) {
				var richMsgTr = richMsgTrTemplate.clone(true);

//				var valueObj = response[key];
//				console.info('valueObj', valueObj);

				richMsgTr.find('.richId').val(o.richId);
				richMsgTr.find('.richMenuId').html(o.richMenuId);
				richMsgTr.find('.richMenuName').html(o.richMenuName);
				richMsgTr.find('.richMenuImgTitle img').attr('richId', o.richId);
				richMsgTr.find('.richMenuImgTitle img').attr('src', bcs.bcsContextPath + "/getResource/IMAGE/" + o.richImageId);
				richMsgTr.find('.richMenuImgTitle img').click(richMenuSelectEventFunc);
				richMsgTr.find('.richMenuImgTitle a').attr('href', bcs.bcsContextPath + '/edit/richMenuCreatePage?richId=' + o.richId + '&groupId=' + groupId + '&actionType=Edit');
//				var urls = [];
//				if(valueObj[1]){
//					urls = valueObj[1].split(",");
//				}
//				var titles = [];
//				if(valueObj[5]){
//					titles = valueObj[5].split(",");
//				}
//				var actions = [];
//				if(valueObj[8]){
//					actions = valueObj[8].split(",");
//				}
//				var sendMessages = [];
//				if(valueObj[9]){
//					sendMessages = valueObj[9].split(",");
//				}
//				var urlHtml = "";
//				if(urls != null && urls.length > 0){
//					for (var i = 0; i < urls.length; i++) {
//						var title = titles[i];
//						if(!title){
//							title = urls[i];
//						}
//						var action = actions[i];
//						if(!action){
//							action = "連結";
//							urlHtml += action + "-<a href='" + urls[i] + "' target='_blank'>" + title + "</a><br/>";
//						}
//						else{
//							if("web" == action){
//								action = "連結";
//								urlHtml += action + "-<a href='" + urls[i] + "' target='_blank'>" + title + "</a><br/>";
//							}
//							else if("postback" == action){
//								action = "隱藏";
//								urlHtml += action + "-" + sendMessages[i] + "<br/>";
//							}
//							else{
//								action = "文字";
//								urlHtml += action + "-" + sendMessages[i] + "<br/>";
//							}
//						}
//					}
//				}else{
//					for (var i = 0; i < actions.length; i++) {
//						if(actions[i] == "postback"){
//							var action = "隱藏";
//							urlHtml += action + "-" + sendMessages[0] + "<br/>";
//						}else{
//							var action = "文字";
//							urlHtml += action + "-" + sendMessages[0] + "<br/>";
//						}
//					}
//				}
//				richMsgTr.find('.richMenuImgUrl').html(urlHtml);
				
				var condition;
				if(o.condition == 'BINDED'){
					condition = "已綁定";
				}else{
					condition = "未綁定"
				}
				richMsgTr.find('.richMenuChangeCondition').html(condition);
				
				//var time = o.modifyTime.replace(/\.\d+$/, ''); // 刪去毫秒
				var time = moment(o.modifyTime).format('YYYY-MM-DD HH:mm:ss'); // new style
				richMsgTr.find('.richMsgCreateTime').html(time);
				richMsgTr.find('.richMsgCreateUser').html(o.modifyUser);
				
				// 使用期間
//				var startUsetime = o.richMenuStartUsingTime.replace(/\.\d+$/, ''); // 刪去毫秒
//				var endUsetime = o.richMenuStartUsingTime.replace(/\.\d+$/, ''); // 刪去毫秒				
				var startUsetime = moment(o.richMenuStartUsingTime).format('YYYY-MM-DD HH:mm:ss'); // new style
				var endUsetime = moment(o.richMenuStartUsingTime).format('YYYY-MM-DD HH:mm:ss'); // new style
				richMsgTr.find('.richMenuUseTime').html(startUsetime + " ~ " + endUsetime);
				
				richMsgTr.find('.richMenuStatus').html(o.status);
				
				$('#richMsgListTable').append(richMsgTr);
//			}
            });
			
			setCopyBtnEvent();
			setActiveBtnEvent();
			setDisableBtnEvent();
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
	
	var setCopyBtnEvent = function() {
		$('.btn_copy').click(function(e) {
			var copyConfirm = confirm("請確認是否複製");
			if (!copyConfirm) return; //點擊取消
			
			var richMsgTr = $(this).closest(".richMsgTrTemplate");
			var selectedContentRichMenuId = richMsgTr.find('.richId').val();
			
			window.location.replace(bcs.bcsContextPath + '/edit/richMenuCreatePage?richId=' + selectedContentRichMenuId + '&actionType=Copy');
		});
		
	}
	
	var setActiveBtnEvent = function() {
		$('.btn_redesign').click(function(e) {
			var deleteConfirm = confirm("請確認是否啟用");
			if (!deleteConfirm) return; //點擊取消
			
			var richMsgTr = $(this).closest(".richMsgTrTemplate");
			var selectedContentRichMenuId = richMsgTr.find('.richId').val();
			
			//console.info("btn_redesign", selectedContentRichMenuId);
			
			$('.LyMain').block($.BCS.blockMsgSave);
			$.ajax({
				type : "DELETE",
				url : bcs.bcsContextPath + '/edit/activeRichMenuStatus?richId=' + selectedContentRichMenuId
			}).success(function(response){
				alert("變更成功！");
				//$initPagination();
				window.location.replace(bcs.bcsContextPath + '/edit/richMenuListPage?groupId=' + groupId);
			}).fail(function(response){
				console.info(response);
				$.FailResponse(response);
				$('.LyMain').unblock();
			}).done(function(){
				$('.LyMain').unblock();
			});
		});
	}
	
	var setDisableBtnEvent = function() {
		$('.btn_disable').click(function(e) {
			var deleteConfirm = confirm("請確認是否停用");
			if (!deleteConfirm) return; //點擊取消
			
			var richMsgTr = $(this).closest(".richMsgTrTemplate");
			var selectedContentRichMenuId = richMsgTr.find('.richId').val();
			var selectedRichMenuId = richMsgTr.find('.richMenuId').val();
			
			$('.LyMain').block($.BCS.blockMsgSave);
			$.ajax({
				type : "DELETE",
				url : bcs.bcsContextPath + '/edit/stopRichMenu/' + selectedContentRichMenuId + '?richMenuId=' + selectedRichMenuId
			}).success(function(response){
				alert("停用成功！");
				//$initPagination();
				window.location.replace(bcs.bcsContextPath + '/edit/richMenuListPage?groupId=' + groupId);
			}).fail(function(response){
				console.info(response);
				$.FailResponse(response);
				$('.LyMain').unblock();
			}).done(function(){
				$('.LyMain').unblock();
			});
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
				//$initPagination();
				window.location.replace(bcs.bcsContextPath + '/edit/richMenuListPage?groupId=' + groupId);
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
	
//	var loadTotalPagesFunc = function(queryObj){
//		var thisQueryFlag = queryObj.queryFlag? queryObj.queryFlag : '';
//		var totalPages = -1;
//		console.info(bcs.bcsContextPath + '/edit/getRichMenuPageTotal?queryFlag=' + encodeURIComponent(thisQueryFlag));
//		
//		$.ajax({
//			//async:false,
//			type : "GET",
//			url : bcs.bcsContextPath + '/edit/getRichMenuPageTotal?queryFlag=' + encodeURIComponent(thisQueryFlag)
//		}).success(function(response){
//			
//			totalPages = response + 1;
//			
//		}).fail(function(response){
//			console.info(response);
//			$.FailResponse(response);
//		}).done(function(){
//		});
//		
//		return totalPages;
//	}

	initTemplate();
	loadDataFunc();
	//$initPagination(loadDataFunc, loadTotalPagesFunc);
});