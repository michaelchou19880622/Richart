/**
 * 
 */
$(function(){
	var loadByTime = false;
	var page = 0;
	var paramPage = $.urlParam("page");
	if(paramPage){
		page = paramPage;
		page--;
	}
	
	$('.LeftBtn').click(function(){
		if(page > 0){
			page--;
			if (loadByTime) {
				loadDataByTime();
			}
			else {
				loadDataFunc($("#queryByFlag").val());
			}
		}
	});
	
	$('.RightBtn').click(function(){
		page++;
		if (loadByTime) {
			loadDataByTime();
		}
		else {
			loadDataFunc($("#queryByFlag").val());
		}
	});

	$('.query').click(function(){
		var queryFlag = $("#queryByFlag").val();
		page = 0;
		loadByTime = false;
		loadDataFunc(queryFlag);
	});
	
	var loadDataFunc = function(queryFlag){
		$('.LyMain').block($.BCS.blockMsgRead);
		
		console.info("queryFlag", queryFlag);
		console.info("page", page);

		$('#pageText').html(page+1);
		
		var postData = {};
		postData.flag = queryFlag;
		postData.page = page;
		
		$.ajax({
			type : "POST",
			url : bcs.bcsContextPath + '/edit/getLinkUrlReportList',
			cache: false,
            contentType: 'application/json',
            processData: false,
			data : JSON.stringify(postData)
		}).success(function(response){
			$('.dataTemplate').remove();
			console.info(response);
			var contentLinkTracingList = response.ContentLinkTracingList;
			var tracingUrlPre = response.TracingUrlPre;
			$.each(contentLinkTracingList, function(i, o){
				var groupData = templateBody.clone(true);
				groupData.find('.linkTitle').html(o.linkTitle);
				groupData.find('.linkUrl').html(o.linkUrl);
				groupData.find('.tracingLink').html(tracingUrlPre + o.tracingLink);
				var linkFlag = moment(o.linkTime).format("YYYY/MM/DD") + "<br/><br/>";
				linkFlag += o.linkFlag;
				groupData.find('.linkFlag').html(linkFlag);
				
				var linkUrl = encodeURIComponent(o.linkUrl);
				console.info(linkUrl);

				var linkId = o.linkId;
				console.info(linkId);
				
				groupData.find('.totalCount a').attr('href', bcs.bcsContextPath +'/admin/reportLinkClickDetailPage?linkUrl=' + linkUrl + '&linkId=' + linkId)
				groupData.find('.totalCount a').html($.BCS.formatNumber(o.totalCount,0));
				
				groupData.find('.userCount a').attr('href', bcs.bcsContextPath +'/admin/reportLinkClickDetailPage?linkUrl=' + linkUrl + '&linkId=' + linkId)
				groupData.find('.userCount a').html($.BCS.formatNumber(o.userCount,0));
	
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
	
//	loadDataFunc("");
	
	var nowDate = moment(); //取得現在時間
	var lastWeek = moment().dates(nowDate.dates() - 6); //取得前7天(上一週)的時間
	$('#campaignStartTime').val(lastWeek.format('YYYY-MM-DD'));
	$('#campaignEndTime').val(nowDate.format('YYYY-MM-DD'));

	page = 0;
	loadByTime = true;
	loadDataByTime();
	
	
	//選取日期元件
	$(".datepicker").datepicker({ 'dateFormat' : 'yy-mm-dd'});
	
	$('.querydate').click(function(){
		page = 0;
		loadByTime = true;
		loadDataByTime();
	});
	
	function loadDataByTime() {
		var campaignStartTime =  moment($('#campaignStartTime').val(), "YYYY-MM-DD");
		var campaignEndTime =  moment($('#campaignEndTime').val(), "YYYY-MM-DD");
		console.info('campaignStartTime = ', campaignStartTime);
		console.info('campaignEndTime = ', campaignEndTime);
		
		var startTime = $("#campaignStartTime").val();
		var endTime = $("#campaignEndTime").val();
		console.info('startTime = ', startTime);
		console.info('endTime = ', endTime);
		
		//需要有日期
		if(startTime == '' || endTime == ''){
			alert('請輸入日期區間');
		}else if (campaignStartTime.isAfter(campaignEndTime)){
			alert("起始日不能大於結束日");
		}else{
			$('#pageText').html(page+1);
			$('.LyMain').block($.BCS.blockMsgRead);
			$.ajax({
				type : "GET",
				url : bcs.bcsContextPath + '/edit/getLinkUrlfromTime?startTime=' + startTime + '&endTime=' + endTime +'&page=' + page
			}).success(function(response){
				$('.dataTemplate').remove();
				console.info(response);
				var contentLinkTracingList = response.ContentLinkTracingList;
				var tracingUrlPre = response.TracingUrlPre;
				$.each(contentLinkTracingList, function(i, o){
					var groupData = templateBody.clone(true);
					
					groupData.find('.linkTitle').html(o.linkTitle);
					groupData.find('.linkUrl').html(o.linkUrl);
					groupData.find('.tracingLink').html(tracingUrlPre + o.tracingLink);
					var linkFlag = moment(o.linkTime).format("YYYY/MM/DD") + "<br/><br/>";
					$.each(o.flags, function(i, o){
						linkFlag += o + "/";
					});
					groupData.find('.linkFlag').html(linkFlag);
					
					var linkUrl = encodeURIComponent(o.linkUrl);
					console.info(linkUrl);

					var linkId = o.linkId;
					console.info(linkId);
					
					groupData.find('.totalCount a').attr('href', bcs.bcsContextPath +'/admin/reportLinkClickDetailPage?linkUrl=' + linkUrl + '&linkId=' + linkId)
					groupData.find('.totalCount a').html($.BCS.formatNumber(o.totalCount,0));
					
					groupData.find('.userCount a').attr('href', bcs.bcsContextPath +'/admin/reportLinkClickDetailPage?linkUrl=' + linkUrl + '&linkId=' + linkId)
					groupData.find('.userCount a').html($.BCS.formatNumber(o.userCount,0));
		
					$('#tableBody').append(groupData);
				});
				
			}).fail(function(response){
				console.info(response);
				$.FailResponse(response);
				$('.LyMain').unblock();
			}).done(function(){
				$('.LyMain').unblock();
			});
		}
	}
	
	$('.exportToExcel').click(function(){
		var url =  bcs.bcsContextPath + '/edit/exportToExcelForInterface';
		var downloadReport = $('#downloadReport');
		downloadReport.attr("src", url);
		
	});
	
	$('.exportSummary').click(function(){
		var url =  bcs.bcsContextPath + '/edit/exportToExcelForSummaryUid';
		var downloadReportAllUid = $('#downloadReportAllUid');
		downloadReportAllUid.attr("src", url);
		
	});
});