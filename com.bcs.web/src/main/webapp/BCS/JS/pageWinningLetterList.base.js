/**
 * 
 */
$(function() {
	console.info('bcs.bcsContextPath:' + bcs.bcsContextPath);

	/* < Button > 匯出EXCEL */
	$('.btn_export').click(function() {
		window.location.replace(bcs.bcsContextPath + '/edit/shareCampaignCreatePage?actionType=Create&from=active');
	});

	/* < Button > 狀態 = '生效' */
	$('.ActiveBtn').click(function() {
		window.location.replace(bcs.bcsContextPath + '/edit/shareCampaignListPage');
	});

	/* < Button > 狀態 = '取消' */
	$('.DisableBtn').click(function() {
		window.location.replace(bcs.bcsContextPath + '/edit/shareCampaignListDisablePage');
	});

	/* < Function > 複製 */
	var func_copyWinningLetter = function() {
		var campaignId = $(this).attr('campaignId');
		console.info('btn_copyFunc campaignId:' + campaignId);
		window.location.replace(bcs.bcsContextPath + '/edit/shareCampaignCreatePage?campaignId=' + campaignId + '&actionType=Copy&from=active');
	};

	/* < Function > 刪除 */
	var func_deteleWinningLetter = function() {
		var campaignId = $(this).attr('campaignId');
		console.info('btn_deteleFunc campaignId:' + campaignId);

		if (!confirm('請確認是否刪除')) {
			return false;
		}

		$.ajax({
			type : "DELETE",
			url : bcs.bcsContextPath + '/admin/deleteShareCampaign?campaignId=' + campaignId
		}).success(function(response) {
			console.info(response);
			alert("刪除成功");
			loadDataFunc();
		}).fail(function(response) {
			console.info(response);
			$.FailResponse(response);
		}).done(function() {
		});
	};

	/* 更新狀態按鈕 */
	var redesignFunc = function() {
		var campaignId = $(this).attr('campaignId');
		console.info('redesignFunc campaignId:' + campaignId);

		if (!confirm('請確認是否取消')) {
			return false;
		}

		$.ajax({
			type : "DELETE",
			url : bcs.bcsContextPath + '/edit/redesignShareCampaign?campaignId=' + campaignId
		}).success(function(response) {
			console.info(response);
			alert("改變成功");
			loadDataFunc();
		}).fail(function(response) {
			console.info(response);
			$.FailResponse(response);
		}).done(function() {
		});
	};

	/* Initial */
	var loadDataFunc = function() {
		console.info("loadDataFunc start");

		$.ajax({
			type : "GET",
			url : bcs.bcsContextPath + '/edit/getShareCampaignList'
		}).done(function(response) {
			$('.dataTemplate').remove();
			console.info(response);

			$.each(response, function(i, o) {
				var queryBody = templateBody.clone(true);

				var msgContent = "";

				msgContent += o.campaignName;

				queryBody.find('.campaignTitle a').attr('href', bcs.bcsContextPath + '/edit/shareCampaignCreatePage?campaignId=' + o.campaignId + '&actionType=Edit&from=active').html(msgContent);

				queryBody.find('.campaignTime').html(moment(o.startTime).format('YYYY-MM-DD HH:mm:ss') + '<br> ~ ' + moment(o.endTime).format('YYYY-MM-DD HH:mm:ss'));

				queryBody.find('.modifyUser').html(moment(o.modifyTime).format('YYYY-MM-DD HH:mm:ss') + "<br>" + o.modifyUser);

				queryBody.find('.status span').html($.BCS.parseInteractiveStatus(o.status));

				queryBody.find('.campaignShareNumber a').attr('href', bcs.bcsContextPath + '/edit/shareCampaignReportPage?campaignId=' + o.campaignId);

				countUserRecord(queryBody, o.campaignId);

				queryBody.find('.btn_redeisgn').attr('campaignId', o.campaignId);
				queryBody.find('.btn_redeisgn').click(redesignFunc);

				queryBody.find('.judgement').html(o.judgement);

				if (o.autoSendPoint)
					queryBody.find('.autoSendPoint').html('是');
				else
					queryBody.find('.autoSendPoint').html('否');

				queryBody.find('.actionImgUrl').html(o.actionImgUrl);
				queryBody.find('.descriptionImgUrl').html(o.descriptionImgUrl);
				queryBody.find('.linePointSerialId').html(o.linePointSerialId);

				queryBody.find('.btn_copy').attr('campaignId', o.campaignId).click(func_copyWinningLetter);

				queryBody.find('.btn_detele').attr('campaignId', o.campaignId).click(func_deteleWinningLetter);

				queryBody.find('.btn_css').attr('campaignId', o.campaignId).attr('campaignName', o.campaignName).click(func_showUrlModel);

				$('#tableBody').append(queryBody);
			});
		}).fail(function(response) {
			console.info(response);
			$.FailResponse(response);
		})

		console.info("loadDataFunc end");
	};

	/* 計算填寫人數 */
	var countUserRecord = function(queryBody, campaignId) {

		$.ajax({
			type : "GET",
			url : bcs.bcsContextPath + '/edit/countShareUserRecord?campaignId=' + campaignId
		}).success(function(response) {

			console.info(response);
			queryBody.find('.campaignShareNumber a').html($.BCS.formatNumber(response, 0));

		}).fail(function(response) {
			console.info(response);
			$.FailResponse(response);
		}).done(function() {
		});
	}

	var templateBody = {};
	templateBody = $('.dataTemplate').clone(true);
	$('.dataTemplate').remove();

	/* Get parent URL */
	var winningLetterTracingUrlPre = $('#winninLetterTracingUrlPre').val();

	/* 彈出視窗Model */
	var modal = document.getElementById("myModal");

	/* URL */
	var modelUrl = document.getElementById("modelUrl");

	/* When the user clicks anywhere outside of the modal, close the model */
	window.onclick = function(event) {
		if (event.target == modal) {
			modal.style.display = "none";
		}
	}

	/* When the user ESC, close the model */
	$(document).keyup(function(e) {
		if (e.key === "Escape") {
			if (modal.style.display === "block") {
				modal.style.display = "none";
			}
		}
	});

	var func_showUrlModel = function() {
		var campaignId = $(this).attr('campaignId');
		
		modelUrl.innerHTML = winningLetterTracingUrlPre + campaignId;
		modelUrl.setAttribute('href', winningLetterTracingUrlPre + campaignId);

		modal.style.display = "block";
	};

	loadDataFunc();
});