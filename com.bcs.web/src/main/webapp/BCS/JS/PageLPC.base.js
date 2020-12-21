$(function() {

	var sendType = 'MANUAL';
	$(".sendType").click(function(e){
		sendType = e.currentTarget.value;
		console.info("selectedSendType:", sendType);
	});
	
    $('.btn_save').click(function() {
        btnTarget = "btn_save";
        var campaignName = $('#campaignTitle').val();
        var campaignCode = $('#campaignCode').val();
        var sendPoint = $('#points').val();
        var campaignPersonNum = $('#campaignPersonNum').val();
        if (!campaignName || !campaignCode || !sendPoint || !campaignPersonNum) {
            alert("欄位不可為空");
            return;
        }

        var postData = {};
        postData.title = campaignName;
        postData.serialId = campaignCode;
        postData.amount = sendPoint;
        postData.totalCount = campaignPersonNum;
        postData.sendType = sendType;
        postData.status = "IDLE";
        postData.successfulCount = 0;
        postData.failedCount = 0;
        
        console.info('postData', postData);

        $.ajax({
            type: "POST",
            url: bcs.bcsContextPath + '/market/createLinePointMain',
            cache: false,
            contentType: 'application/json',
            processData: false,
            data: JSON.stringify(postData)

        }).success(
            function(response) {
                console.info(response);
                alert('儲存成功');
                window.location.replace(bcs.bcsContextPath + '/market/linePointListPage');
            }).fail(function(response) {
            console.info(response);
            alert(response.responseText);
        })
    });
});