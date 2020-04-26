$(function(){
//	var clonedDOM = null;
	var startDate = null, endDate = null;
	var hasData = false;
	var APIStart = null;
	
	$(".datepicker").datepicker({
		maxDate : 0,
		dateFormat : 'yy-mm-dd',
		changeMonth: true
	});
	
	$('.query').click(function(){
		if(dataValidate()) {
			$('.dataTemplate').remove();
			
			startDate = $('#startDate').val();
			endDate = $('#endDate').val();

			APIStart = Date.now();
			getEffects(startDate, endDate);
		}
	});
	
	initial();
	
	function initial() {
		console.log('Push API 成效列表');
		
//		clonedDOM = $('.dataTemplate').clone(true);
		$('.dataTemplate').remove();
		
		startDate = moment(new Date()).format('YYYY-MM-DD');
		endDate = moment(new Date()).format('YYYY-MM-DD');
		
		$('#startDate').val(startDate);
		$('#endDate').val(endDate);
		
		APIStart = Date.now();
		getEffects(startDate, endDate);
	}
	
	function getEffects(startDate, endDate) {
		$.ajax({
			type : "GET",
			url : bcs.bcsContextPath + '/edit/getPushApiEffects?startDate=' + startDate + '&endDate=' + endDate
		}).success(function(response){
			if(response.length === 0) {
				hasData = false;
				
				$('<tr class="dataTemplate"><td colspan="5">此日期區間無任何資料</td></tr>').appendTo($('#tableBody'));
			} else {
				console.log("GetEffects() API Duration : ", Date.now() - APIStart);
				
				hasData = true;
				
				const start = Date.now();
				var tbl = document.getElementById("tableBody");
				
				var exportUrl = '../edit/exportToExcelForPushApiEffectDetail?createTime=';
				
				response.forEach(function(element){
					var row = document.createElement("tr");					
					row.classList.add("dataTemplate");
				     // Create a <td> element and a text node, make the text
				      // node the contents of the <td>, and put the <td> at
				      // the end of the table row
					/* 為了效能考量 , 放棄使用DOM進行迴圈的操作. */
				    for (var column = 0; column < 5; column++) {
					
				      var cell = document.createElement("td");
				      var cellText;
				      if ( column == 0) {
				    	    cellText = document.createElement("div");				    	    
				    	    cellText.innerHTML = "<a href=" + exportUrl + encodeURI(moment(element.createTime).format('YYYY-MM-DD HH:mm:ss.SSS')) +">" + moment(element.createTime).format('YYYY-MM-DD HH:mm:ss') + "</a>"
				      }
				      else if ( column == 1) 
				    	  cellText = document.createTextNode(element.sendType);
				      else if ( column == 2) 
				    	  cellText = document.createTextNode(element.department);
				      else if ( column == 3) 
				    	  cellText = document.createTextNode(element.successCount);
				      else if ( column == 4) 
				    	  cellText = document.createTextNode(element.failCount);
				      cell.appendChild(cellText);				      
					  row.appendChild(cell);
				    }
					tbl.appendChild(row);					
/*					
					var rowDOM = clonedDOM.clone(true);
					rowDOM.find('.createDate').html('<a>' + moment(element.createTime).format('YYYY-MM-DD HH:mm:ss') + '</a>').end().find('a').attr('href', exportUrl + element.createTime);
					rowDOM.find('.product').text(element.department);
					rowDOM.find('.successCount').text(element.successCount);
					rowDOM.find('.failCount').text(element.failCount);
					rowDOM.find('.sendType').text(element.sendType);
					
					rowDOM.appendTo($('#tableBody'));
*/					
				});
				console.log("Create HTML Duration : ", Date.now() - start);			
				
			}
			
			setExportButtonSource(startDate, endDate);
		}).fail(function(response){
			console.info(response);
		}).done(function(){
		});
	}
	
	function setExportButtonSource(startDate, endDate) {
		if(hasData) {
			var exportUrl = '../edit/exportToExcelForPushApiEffects?startDate='+ startDate + '&endDate=' + endDate;
			
			$('.btn_add.exportToExcel').attr('href', exportUrl);
		} else {
			$('.btn_add.exportToExcel').attr('href', '#');
		}
	}
	
	function dataValidate() {
		var startDate = $('#startDate').val();
		var endDate = $('#endDate').val();
		
		if(!startDate) {
			alert('請填寫起始日期！');
			return false;
		}
		if(!$('#endDate').val()) {
			alert('請填寫結束日期！');
			return false;
		}
		if(moment(startDate).isAfter(moment(endDate))) {
			alert('起始日期不可大於結束日期！');
			return false;
		}
		
		return true;
	}
});