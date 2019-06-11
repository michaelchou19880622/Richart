$(function(){
		$('.btn_add').click(function(){
	 		window.location.replace(bcs.bcsContextPath + '/market/linePointReportPage');
		});

		var loadDataFunc = function(){
			// params
			var mainId = $.urlParam("mainId");
			console.info("mainId:", mainId);
			
			var url =  bcs.bcsContextPath + '/market/getLinePointScheduledDetailList/' + mainId

			$.ajax({
				type : "GET",
				url : url
			}).success(function(response){
				
				console.log(response);
				$('.dataTemplate').remove();
				console.info(response);
		
				$.each(response, function(i, o){
					var groupData = templateBody.clone(true); //增加一行
					console.info(groupData);
					groupData.find('.uid').html(o.uid);		
					$('#tableBody').append(groupData);
				});
				
			}).fail(function(response){
				console.info(response);
				$.FailResponse(response);
			}).done(function(){
			});
		};
		
		var templateBody = {};
		templateBody = $('.dataTemplate').clone(true);
		$('.dataTemplate').remove();
		
		loadDataFunc();
	});

 