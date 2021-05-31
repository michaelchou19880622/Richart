$(function(){
	$('.btn_add').click(function(){
 		window.location.href='gameCreatePage';
	});
	
	var loadDataFunc = function(){
		$('.LyMain').block($.BCS.blockMsgRead);
		
		$.ajax({
			type : "GET",
			url : bcs.bcsContextPath + '/edit/getGameList'
		}).success(function(response){
			console.info("response:", response);
			
			$('.gameTrTemplate').remove();

			for(var i = 0; i<response.length; i++){
				var gameTr = gameTrTemplate.clone(true);
				
				gameTr.find('.gameId').val(response[i].gameId);
				gameTr.find('.gameType').html(response[i].gameType);
				gameTr.find('.name').html(response[i].gameName);
				gameTr.find('.gameContent').html(response[i].gameContent);
				gameTr.find('.gameName a').attr('href', bcs.bcsContextPath + '/edit/gameCreatePage/' + response[i].gameType + '?gameId=' + response[i].gameId + '&actionType=Edit');
				var time = response[i].modifyTime.replace(/\.\d+$/, ''); // 刪去毫秒
				gameTr.find('.gameCreateTime').html(time);
				gameTr.find('.gameCreateUser').html(response[i].modifyUserName);
				
				$('#gameListTable').append(gameTr);
			}			
		}).fail(function(response){
			console.info(response);
			$.FailResponse(response);
			$('.LyMain').unblock();
		}).done(function(){
			$('.LyMain').unblock();
		});
	};
	
	$('.btn_detele').click(function(e) {
		var deleteConfirm = confirm("請確認是否刪除");
		if (!deleteConfirm) return; //點擊取消
		
		var gameTr = $(this).closest(".gameTrTemplate");
		var selectedGameId = gameTr.find('.gameId').val();
		$.ajax({
			type : "DELETE",
			url : bcs.bcsContextPath + '/admin/deleteGame/' + selectedGameId
		}).success(function(response){
			alert("刪除成功！");
			loadDataFunc();
		}).fail(function(response){
			console.info(response);
			$.FailResponse(response);
		}).done(function(){
		});
	});
	
	$('.btn_copy').click(function(e) {
		var gameTr = $(this).closest(".gameTrTemplate");
		var selectedGameId = gameTr.find('.gameId').val();
		var selectedGameType = gameTr.find('.gameType').html();
		var url = bcs.bcsContextPath + '/edit/gameCreatePage/' + selectedGameType + '?gameId=' + selectedGameId + '&actionType=Copy';
		if ($.BCS.validateURL(url)) {
			window.location.href=encodeURI(url);
		} else {
			alert('An attempt was made to open a webpage of foreign domain. No allowed!');
		}
	});
	
	$('.btn_winner_list').click(function(e) {
		var gameTr = $(this).closest(".gameTrTemplate");
		var selectedGameId = gameTr.find('.gameId').val();
		var url = bcs.bcsContextPath + '/edit/winnerListPage?gameId=' + selectedGameId;
		if ($.BCS.validateURL(url)) {
			window.location.href=encodeURI(url);
		} else {
			alert('An attempt was made to open a webpage of foreign domain. No allowed!');
		}
	});
	
	$('.btn_prize_list').click(function(e) {
		var gameTr = $(this).closest(".gameTrTemplate");
		var selectedGameId = gameTr.find('.gameId').val();
		var url = bcs.bcsContextPath + '/edit/prizeListPage?gameId=' + selectedGameId;
		if ($.BCS.validateURL(url)) {
			window.location.href=encodeURI(url);
		} else {
			alert('An attempt was made to open a webpage of foreign domain. No allowed!');
		}
	});
	
	$('.btn_gen_qrcode').click(function(){
		var selectedGameId = $(this).closest('.gameTrTemplate').find('.gameId').val();
		console.info('selectedGameId',selectedGameId);
		$('#qrcode_image').attr("src", bcs.bcsContextPath + "/game/createGameQRcode/" + selectedGameId);
		$('.qrcode_dialog').dialog('open');
	});
	
	var gameTrTemplate = {};
	var initPage = function(){
		gameTrTemplate = $('.gameTrTemplate').clone(true);
		$('.gameTrTemplate').remove();
	}
	
	$('.qrcode_dialog').dialog({
    	autoOpen: false,
		resizable: false, //不可縮放
		modal: true, //畫面遮罩
		draggable: false, //不可拖曳
		width: 'auto',
    	position: { my: "top", at: "top", of: window  }
    });
	
	initPage();
	loadDataFunc();
});