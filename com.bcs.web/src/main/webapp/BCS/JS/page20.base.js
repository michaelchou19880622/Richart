/**
 * 
 */
$(function(){
	var page = 0;
	var paramPage = $.urlParam("page");
	var templateBody = {};
	if(paramPage){
		page = paramPage;
		page--;
	}
	
	$('.LeftBtn').click(function(){
		if(page > 0){
			page--;
			loadDataFunc();
		}
	});
	
	$('.RightBtn').click(function(){
		page++;
		loadDataFunc();
	});
	
	$('.exportToExcel').click(function(){
		if(!validateTimeRange()){
			return false;
		}
		var startDate = $('#campaignStartTime').val();
		var endDate = $('#campaignEndTime').val();
		var dataStartDate = $('#dataStartTime').val();
		var dataEndDate = $('#dataEndTime').val();
		var queryFlag = $("#queryFlag").val();
		var orderBy = $('#orderBy').val();
		var postData = {};
		postData.startDate = startDate;
		postData.endDate = endDate;
		postData.dataStartDate = startDate;
		postData.dataEndDate = dataEndDate;
		postData.queryFlag = queryFlag;
		postData.orderBy = orderBy;
		var url = bcs.bcsContextPath + '/edit/exportLinkClickReportListNew';
	    downloadFromAjaxPost(url, JSON.stringify(postData));
	});
	
	function downloadFromAjaxPost(url, params) {
	    var xhr = new XMLHttpRequest();
	    xhr.open('POST', url, true);
	    xhr.responseType = 'arraybuffer';
	    $('.LyMain').block($.BCS.blockMsgRead);
	    xhr.onload = function() {
	        if (this.status === 200) {
	            var filename = "";
	            var disposition = xhr.getResponseHeader('Content-Disposition');
	            if (disposition && disposition.indexOf('attachment') !== -1) {
	                var filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
	                var matches = filenameRegex.exec(disposition);
	                if (matches != null && matches[1]) filename = matches[1].replace(/['"]/g, '');
	            }
	            var type = xhr.getResponseHeader('Content-Type');
	            var blob = new Blob([this.response], { type: type });
	            if (typeof window.navigator.msSaveBlob !== 'undefined') {
	                // IE workaround for "HTML7007: One or more blob URLs were revoked by closing the blob for which they were created. These URLs will no longer resolve as the data backing the URL has been freed."
	                window.navigator.msSaveBlob(blob, filename);
	            } else {
	                var URL = window.URL || window.webkitURL;
	                var downloadUrl = URL.createObjectURL(blob);
	                if (filename) {
	                    // use HTML5 a[download] attribute to specify filename
	                    var a = document.createElement("a");
	                    // safari doesn't support this yet
	                    if (typeof a.download === 'undefined') {
	                        window.location = downloadUrl;
	                    } else {
	                        a.href = downloadUrl;
	                        a.download = filename;
	                        document.body.appendChild(a);
	                        a.click();
	                    }
	                } else {
	                    window.location = downloadUrl;
	                }
	                setTimeout(function() { URL.revokeObjectURL(downloadUrl); }, 100); // cleanup
	            }
	        }
	        $('.LyMain').unblock();
	    };
	    xhr.setRequestHeader('Content-type', 'application/json');
	    xhr.send(params);
	}
	
	var validateTimeRange = function() {
		var startDate = moment($('#campaignStartTime').val(), "YYYY-MM-DD");
		var endDate = moment($('#campaignEndTime').val(), "YYYY-MM-DD");
		var dataStartDate = moment($('#dataStartTime').val(), "YYYY-MM-DD");
		var dataEndDate = moment($('#dataEndTime').val(), "YYYY-MM-DD");
		if (!startDate.isValid()) {
			alert("請選擇建立連結起始日期");
			return false;
		}
		if (!endDate.isValid()) {
			alert("請選擇建立連結結束日期");
			return false;
		}
		if (startDate.isAfter(endDate)) {
			alert("建立連結起始日不能大於結束日");
			return false;
		}
		var n = parseInt((new Date(endDate) - new Date(startDate)) / 86400000);
		if (n > 30) {
			alert("僅限查詢建立連結一個月區間內資料");
			return false;
		}
		if (!dataStartDate.isValid()) {
			alert("請選擇資料記錄起始日期");
			return false;
		}
		if (!dataEndDate.isValid()) {
			alert("請選擇資料記錄結束日期");
			return false;
		}
		if (dataStartDate.isAfter(dataEndDate)) {
			alert("資料記錄起始日不能大於結束日");
			return false;
		}
		n = parseInt((new Date(dataEndDate) - new Date(dataStartDate)) / 86400000);
		if (n > 30) {
			alert("僅限查詢資料記錄一個月區間內資料");
			return false;
		}
		return true;
	}
	
	//選取日期元件
	$(".datepicker").datepicker({
		'maxDate' : 0, //最多只能選至前一天
		'dateFormat' : 'yy-mm-dd'
	});
	
	$('.query').click(function(){
		page = 0;
		loadDataFunc();
	});

	var loadDataFunc = function(){
		var startDate = $("#campaignStartTime").val();
		var endDate = $("#campaignEndTime").val();
		var dataStartDate = $("#dataStartTime").val();
		var dataEndDate = $("#dataEndTime").val();
		var queryFlag = $("#queryFlag").val();
		var orderBy = $("#orderBy").val();
		console.info("startDate", startDate);
		console.info("endDate", endDate);
		console.info("dataStartDate", dataStartDate);
		console.info("dataEndDate", dataEndDate);
		console.info("queryFlag", queryFlag);
		console.info("orderBy", queryFlag);
		console.info("page", page);
		if(!validateTimeRange()){
			return;
		}
		var postData = {};
		postData.queryFlag = queryFlag;
		postData.page = page;
		postData.pageSize = 20;
		postData.startDate = startDate;
		postData.endDate = endDate;
		postData.dataStartDate = startDate;
		postData.dataEndDate = dataEndDate;
		postData.orderBy = orderBy;
		$('.LyMain').block($.BCS.blockMsgRead);
		$('#pageText').html(page+1);
		$.ajax({
			type : "POST",
			url : bcs.bcsContextPath + '/edit/getLinkClickReportListNew',
			cache: false,
            contentType: 'application/json',
            processData: false,
			data : JSON.stringify(postData)
		}).success(function(response){
			$('.dataTemplate').remove();
			console.info(response);
			var contentLinkTracingList = response.ContentLinkTracingList;
			var tracingUrlPre = response.TracingUrlPre;
			var recordNumber = 0;
			$.each(contentLinkTracingList, function(i, o){
				var groupData = templateBody.clone(true);
				groupData.find('.tracingLink').html(tracingUrlPre + o.tracingLink);
				groupData.find('.linkTitle').html(o.linkTitle);
				groupData.find('.linkUrl').html(o.linkUrl);
				var linkFlag = moment(o.linkTime).format("YYYY/MM/DD");
				for (var i = 0; i < o.flags.length; i++) {
					linkFlag += "<br/><br/>" + o.flags[i];
				}
				groupData.find('.linkFlag').html(linkFlag);
				var linkId = encodeURIComponent(o.linkId);
				var linkUrl = encodeURIComponent(o.linkUrl);
				groupData.find('.totalCount a').attr('href', bcs.bcsContextPath + '/admin/reportLinkClickDetailPage?linkUrl=' + linkUrl + "&linkId=" + linkId + "&startDate=" + dataStartDate + "&endDate=" + dataEndDate)
				groupData.find('.totalCount a').html($.BCS.formatNumber(o.totalCount,0));
				groupData.find('.userCount a').attr('href', bcs.bcsContextPath + '/admin/reportLinkClickDetailPage?linkUrl=' + linkUrl  + "&linkId=" + linkId + "&startDate=" + dataStartDate + "&endDate=" + dataEndDate)
				groupData.find('.userCount a').html($.BCS.formatNumber(o.userCount,0));
				$('#tableBody').append(groupData);
				recordNumber += 1;
			});
			$('#recordNumberText').html(recordNumber);
			
		}).fail(function(response){
			console.info(response);
			$.FailResponse(response);
			$('.LyMain').unblock();
		}).done(function(){
			$('.LyMain').unblock();
		});
	};
	
	var initTemplate = function(){
		$("#queryFlag").val("");
		$("#orderBy").val("0");
		templateBody = $('.dataTemplate').clone(true);
		$('.dataTemplate').remove();
		var nowDate = moment(); //取得現在時間
		var campaignStartTime = moment().dates(nowDate.dates() - 6);
		var campaignEndTime = moment().dates(nowDate.dates());
		var dataStartTime = moment().dates(nowDate.dates() - 6);
		var dataEndTime = moment().dates(nowDate.dates());
		$('#campaignStartTime').val(campaignStartTime.format('YYYY-MM-DD'));
		$('#campaignEndTime').val(campaignEndTime.format('YYYY-MM-DD'));
		$('#dataStartTime').val(dataStartTime.format('YYYY-MM-DD'));
		$('#dataEndTime').val(dataEndTime.format('YYYY-MM-DD'));
	}
	initTemplate();
	loadDataFunc("");
});