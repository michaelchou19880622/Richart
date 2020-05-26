/**
 * 
 */
$(function(){
	
	var totalPageSize = document.getElementById('totalPageSize');
	
	var valTotalPageSize = 0;
	
	var currentPageIndex = document.getElementById('currentPageIndex');
	
	var valCurrentPageIndex = 1;
	
	var perPageSize = $(this).find('option:selected').text();
	
	var btn_copyFunc = function(){
		var groupId = $(this).attr('groupId');
		console.info('btn_copyFunc groupId:' + groupId);
 		window.location.replace(bcs.bcsContextPath + '/edit/hpiRichMenuCreateGroupPage?groupId=' + groupId + '&actionType=Copy');
	};
	
	var btn_deteleFunc = function(){
		var groupId = $(this).attr('groupId');
		console.info('btn_deteleFunc groupId:' + groupId);

		var r = confirm("請確認是否刪除");
		if (r) {
			
		} else {
		    return;
		}

		$('.LyMain').block($.BCS.blockRichmenuGroupListDeleting);
		$.ajax({
			type : "DELETE",
			url : bcs.bcsContextPath + '/admin/deleteSendGroup?groupId=' + groupId
		}).success(function(response){
			console.info(response);
			alert("刪除成功");
			loadDataFunc();
		}).fail(function(response){
			console.info(response);
			$.FailResponse(response);
			$('.LyMain').unblock();
		}).done(function(){
			$('.LyMain').unblock();
		});
	};
	
	/* < Button > 上一頁 */
	$('#btn_PreviousPage').click(function() {
		valCurrentPageIndex = (valCurrentPageIndex - 1 <= 0)? valCurrentPageIndex : valCurrentPageIndex - 1;
		
		loadDataFunc();
	});
	
	/* < Button > 下一頁 */
	$('#btn_NextPage').click(function() {
		valCurrentPageIndex = (valCurrentPageIndex + 1 > valTotalPageSize)? valCurrentPageIndex : valCurrentPageIndex + 1;
		
		loadDataFunc();
	});

	/* 更新每頁顯示數量下拉選單 */
	var func_optionSelectChanged = function(){
		var selectValue = $(this).find('option:selected').text();
		
		$(this).closest('.optionPageSize').find('.optionLabelPageSize').html(selectValue);
		
		perPageSize = selectValue;
		
		loadDataFunc();
	};

	$('.optionSelectPageSize').change(func_optionSelectChanged);

	var loadDataFunc = function(){
		
		$('.LyMain').block($.BCS.blockRichmenuGroupListLoading);
		
		$.ajax({
			type : "GET",
			url : encodeURI(bcs.bcsContextPath + '/market/getRichmenuSendGroupList?&page=' + (valCurrentPageIndex - 1) +'&size=' + perPageSize)
		}).success(function(response){
			$('.dataTemplate').remove();
			console.info(response);
			
			if (response.totalElements == 0) {
				totalPageSize.innerText = '-';
				currentPageIndex.innerText = '-';
				
				$('.LyMain').unblock();
				
				return;
			}
			
			valTotalPageSize = response.totalPages;
			valCurrentPageIndex = (response.number + 1);
			
			totalPageSize.innerText = valTotalPageSize;
			currentPageIndex.innerText = valCurrentPageIndex;

			if (valCurrentPageIndex > valTotalPageSize) {
				valCurrentPageIndex = valTotalPageSize;
				currentPageIndex.innerText = valCurrentPageIndex;
				
				loadDataFunc();
				
				return;
			}
	
			$.each(response.content, function(i, o){
				var groupData = templateBody.clone(true);

				groupData.find('.groupName a').attr('href', encodeURI(bcs.bcsContextPath + '/edit/hpiRichMenuCreateGroupPage?groupId=' + o.groupId + '&actionType=Edit'));
				groupData.find('.groupName a').html(o.groupTitle);
				
				var groupTypeDisplay;// = (o.groupType == 'CONDITIONS')? "依照設定條件" : "依照匯入名單";
				
				switch (o.groupType){
					case 'UID_LIST':
						groupTypeDisplay = '依照匯入名單';
						break;
					case 'CONDITIONS':
						groupTypeDisplay = '依照設定條件';
						break;
					case 'BINDSTATUS':
						groupTypeDisplay = '依照綁定狀態';
						break;
				}
				
				groupData.find('.groupType').html(groupTypeDisplay);
				
				if(o.modifyTime){
					groupData.find('.modifyTime').html(moment(o.modifyTime).format('YYYY-MM-DD HH:mm:ss'));
				}
				else{
					groupData.find('.modifyTime').html('-');
				}
				groupData.find('.modifyUser').html(o.modifyUser);
	
				groupData.find('.btn_copy').attr('groupId', o.groupId);
				groupData.find('.btn_copy').click(btn_copyFunc);
				
				if (bcs.user.admin) {
					groupData.find('.btn_detele').attr('groupId', o.groupId);
					groupData.find('.btn_detele').click(btn_deteleFunc);
				} else {
					groupData.find('.btn_detele').remove();
				}
				
				if(o.groupId < 0){
					groupData.find('.btn_copy').remove();
					groupData.find('.btn_detele').remove();
				}
	
				$('#tableBody').append(groupData);
			});
			
		}).fail(function(response){
			console.info(response);
			$.FailResponse(response);
			$('.LyMain').unblock();
		}).done(function(){
			$('.LyMain').unblock();
		});
	};

	var templateBody = {};
	templateBody = $('.dataTemplate').clone(true);
	$('.dataTemplate').remove();
	
	loadDataFunc();
});