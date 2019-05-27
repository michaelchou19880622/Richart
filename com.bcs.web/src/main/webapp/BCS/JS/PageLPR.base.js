$(function(){
		$('.btn_add').click(function(){
	 		window.location.replace(bcs.bcsContextPath + '/market/linePointPushPage');
		});

		var loadDataFunc = function(){
			
			$.ajax({
				type : "GET",
				url : bcs.bcsContextPath + '/market/getLinePointMainList'
			}).success(function(response){
				
				console.log(response);
				$('.dataTemplate').remove();
				console.info(response);
		
				$.each(response, function(i, o){
					var groupData = templateBody.clone(true); //增加一行
					console.info(groupData);
//	 				groupData.find('.campaignCode a').attr('href', bcs.bcsContextPath + '/getLinePointMainList?campaignCode=' + o.campaignCode);
					groupData.find('.serialId').html(o.serialId);
					groupData.find('.title').html(o.title);
					if(o.modifyTime){
						groupData.find('.modifyTime').html(moment(o.modifyTime).format('YYYY-MM-DD HH:mm:ss'));
					}else{
						groupData.find('.modifyTime').html('-');
					}
					
					groupData.find('.amount').html(o.amount);
					groupData.find('.totalCount').html(o.totalCount);
				 	groupData.find('.successfulCount').html(o.successfulCount);
				 	groupData.find('.failedCount').html(o.failedCount);
				 	groupData.find('.modifyUser').html(o.modifyUser);
				 	
					
//					if (bcs.user.admin) {
//						groupData.find('.btn_detele').attr('eventId', o.eventId);
//						groupData.find('.btn_detele').click(btn_deteleFunc);
//					} else {
//						groupData.find('.btn_detele').remove();
//					}
//					
//					if(o.campaignCode < 0){ //假設有預設群組
//						groupData.find('.btn_copy').remove();
//						groupData.find('.btn_detele').remove();
//					}
		
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

 