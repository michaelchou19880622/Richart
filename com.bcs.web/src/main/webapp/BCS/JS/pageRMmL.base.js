/**
 * 
 */
$(function(){
	// global variables
	var groupId = 0;
	var richMsgTrTemplate = {};

	// to Group Page
	$('.btn_cancel').click(function() {
		window.location.replace(bcs.bcsContextPath + '/edit/richMenuGroupListPage');
	});
	
	// to Create Page (new)
	$('#createRichMenu').click(function(){
		window.location.replace(bcs.bcsContextPath +'/edit/richMenuCreatePage?groupId=' + groupId);
	});
		
	// to Create Page (edit)
	var richMenuSelectEventFunc = function(){
		var richId = $(this).attr('richId');
 		window.location.replace(bcs.bcsContextPath + '/edit/richMenuCreatePage?richId=' + richId + '&groupId=' + groupId + '&actionType=Edit');
	};
	
	// to Create Page (read only)
	var richMenuSelectReadOnlyFunc = function(){
		var richId = $(this).attr('richId');
 		window.location.replace(bcs.bcsContextPath + '/edit/richMenuCreatePage?richId=' + richId + '&groupId=' + groupId + '&actionType=Edit&readOnly=true');
	}
	
	// to Create Page (clone)
	var setCopyBtnEvent = function() {
		$('.btn_clone').click(function(e) {
			var copyConfirm = confirm("請確認是否複製");
			if (!copyConfirm) return; //點擊取消
			
			var richMsgTr = $(this).closest(".richMsgTrTemplate");
			var richId = richMsgTr.find('.richId').val();
			
			window.location.replace(bcs.bcsContextPath + '/edit/richMenuCreatePage?richId=' + richId + '&groupId=' + groupId + '&actionType=Copy');
		});
		
	}
	
	// updateSendGroup
	$('#updateSendGroup').click(function(){
		$('.LyMain').block($.BCS.blockMsgSave);
		$.ajax({
			type : 'GET',
			url : bcs.bcsContextPath + '/edit/updateRichMenuSendGroupByRichMenuGroupId?richMenuGroupId=' + groupId
		}).success(function(response){
			alert("聯繫成功！");
			window.location.replace(bcs.bcsContextPath + '/edit/richMenuMemberListPage?groupId=' + groupId);
		}).fail(function(response){
			console.info(response);
			$.FailResponse(response);
			$('.LyMain').unblock();
		}).done(function(){
			$('.LyMain').unblock();
		});
	});
	
	var setActiveBtnEvent = function() {
		$('.btn_redesign').click(function(e) {
			var deleteConfirm = confirm("請確認是否啟用");
			if (!deleteConfirm) return; //點擊取消
			
			var richMsgTr = $(this).closest(".richMsgTrTemplate");
			var selectedContentRichMenuId = richMsgTr.find('.richId').val();
			console.info("richMsgTr:", richMsgTr);
			console.info("selectedContentRichMenuId:", selectedContentRichMenuId);
			//console.info("btn_redesign", selectedContentRichMenuId);
			
			$('.LyMain').block($.BCS.blockMsgSave);
			$.ajax({
				type : "DELETE",
				url : bcs.bcsContextPath + '/edit/activeRichMenuStatus?richId=' + selectedContentRichMenuId
			}).success(function(response){
				alert("變更成功！");
				//$initPagination();
				window.location.replace(bcs.bcsContextPath + '/edit/richMenuMemberListPage?groupId=' + groupId);
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
			console.info(selectedContentRichMenuId);
			console.info(selectedRichMenuId);
			
			var url = bcs.bcsContextPath + '/edit/stopRichMenu/' + selectedContentRichMenuId;
			console.info("url:", url);
			
			$('.LyMain').block($.BCS.blockMsgSave);
			$.ajax({
				type : "DELETE",
				url : url
			}).success(function(response){
				alert("停用成功！");
				//$initPagination();
				window.location.replace(bcs.bcsContextPath + '/edit/richMenuMemberListPage?groupId=' + groupId);
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
		$('.btn_delete').click(function(e) {
			var deleteConfirm = confirm("請確認是否刪除，一旦刪除此圖文選單，其他連接到此圖文選單的連結將需重新設置！");
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
				window.location.replace(bcs.bcsContextPath + '/edit/richMenuMemberListPage?groupId=' + groupId);
			}).fail(function(response){
				console.info(response);
				$.FailResponse(response);
				$('.LyMain').unblock();
			}).done(function(){
				$('.LyMain').unblock();
			});
		});
		
	}
	
	// do Export Report
//	$('.exportReport').click(function(){
//		// find Parent
//		var richMsgTr = $(this).closest(".richMsgTrTemplate");
//		
//		// find Brother for value
//		var richId = richMsgTr.find('.richId').val();
//		
//		// set 
//		var url =  bcs.bcsContextPath + '/edit/exportToExcelForRichMenuChangeReport?richId=' + richId;
//		var downloadReport = $('#downloadReport');
//		downloadReport.attr("src", url);
//	});

	
	// initialize
	var loadDataFunc = function(){
		$('.LyMain').block($.BCS.blockMsgRead);
		var link = bcs.bcsContextPath + '/edit/getRichMenuListByRichMenuGroupId/' + groupId;
		$.ajax({
			type : "GET",
			url : link
		}).success(function(response){
			console.log(response);
			$('.richMsgTrTemplate').remove();
			
			$.each(response, function(i, o) {
				
				var richMsgTr = richMsgTrTemplate.clone(true);
				richMsgTr.find('.id').html(o.richId);
				richMsgTr.find('.richId').val(o.richId);
				richMsgTr.find('.richMenuId').html(o.richMenuId);
				richMsgTr.find('.richMenuName').html(o.richMenuName);
				richMsgTr.find('.richMenuImgTitle img').attr('richId', o.richId);
				richMsgTr.find('.richMenuImgTitle img').attr('src', bcs.bcsContextPath + "/getResource/IMAGE/" + o.richImageId);
				if(o.status == 'DISABLE'){
					richMsgTr.find('.richMenuImgTitle img').click(richMenuSelectEventFunc);
					richMsgTr.find('.richMenuImgTitle a').attr('href', bcs.bcsContextPath + '/edit/richMenuCreatePage?richId=' + o.richId + '&groupId=' + groupId + '&actionType=Edit');
				}else{
					richMsgTr.find('.richMenuImgTitle img').click(richMenuSelectReadOnlyFunc);
					richMsgTr.find('.richMenuImgTitle a').attr('href', bcs.bcsContextPath + '/edit/richMenuCreatePage?richId=' + o.richId + '&groupId=' + groupId + '&actionType=Edit&readOnly=true');
				}
				
				var level = "";
				if(o.level == 'MAIN'){
					level = '首頁';
				}else{
					level = '非首頁';
				}
				richMsgTr.find('.richMenuLevel').html(level);
				
				var time = moment(o.modifyTime).format('YYYY-MM-DD HH:mm:ss');
				richMsgTr.find('.richMsgCreateTime').html(time);
				richMsgTr.find('.richMsgCreateUser').html(o.modifyUser);
						
//				var startUsetime = moment(o.richMenuStartUsingTime).format('YYYY-MM-DD HH:mm:ss'); // new style
//				var endUsetime = moment(o.richMenuStartUsingTime).format('YYYY-MM-DD HH:mm:ss'); // new style
//				richMsgTr.find('.richMenuUseTime').html(startUsetime + " ~ " + endUsetime);
				
				richMsgTr.find('.richMenuStatus').html(o.status);
				
				$('#richMsgListTable').append(richMsgTr);
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
	
	var initTemplate = function(){
		groupId = $.urlParam("groupId");
		richMsgTrTemplate = $('.richMsgTrTemplate').clone(true);
		$('.richMsgTrTemplate').remove();
	}

	initTemplate();
	loadDataFunc();
});