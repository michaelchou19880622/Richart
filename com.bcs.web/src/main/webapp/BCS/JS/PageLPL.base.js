$(function() {
    $('.btn_add').click(function() {
        window.location.replace(bcs.bcsContextPath + '/market/linePointCreatePage');
    });
    //============================ 轉址 ==============================
    //	var btn_copyFunc = function(){
    //		var campaignCode = $(this).attr('campaignCode');
    //		console.info('btn_copyFunc campaignCode:' + campaignCode);
    // 		window.location.replace(bcs.bcsContextPath + '/market/sendGroupCreatePage?campaignCode=' + campaignCode + '&actionType=Copy');
    //	};

    var btn_deteleFunc = function() {
        var campaignId = $(this).attr('id');

        console.info('btn_deteleFunc campaignId:' + campaignId);

        var r = confirm("請確認是否刪除");
        if (r) {

        } else {
            return;
        }

        $.ajax({
            type: "DELETE",
            url: bcs.bcsContextPath + '/admin/deleteLinePointMain?campaignId=' + campaignId + '&listType=LineCampaignList'
        }).success(function(response) {
            console.info(response);
            alert("刪除成功");
            loadDataFunc();
        }).fail(function(response) {
            console.info(response);
            $.FailResponse(response);
        }).done(function() {});
    };

    var loadDataFunc = function() {

        $.ajax({
            type: "GET",
            url: bcs.bcsContextPath + '/market/getLinePointMainList'
        }).success(function(response) {

            console.log(response);
            $('.dataTemplate').remove();
            console.info(response);

            $.each(response, function(i, o) {
                var groupData = templateBody.clone(true); //增加一行
                console.info(groupData);
                // 				groupData.find('.campaignCode a').attr('href', bcs.bcsContextPath + '/getLinePointMainList?campaignCode=' + o.campaignCode);
                groupData.find('.campaignCode').html(o.serialId);
                groupData.find('.campaignName').html(o.title);
                if (o.modifyTime) {
                    groupData.find('.modifyTime').html(moment(o.modifyTime).format('YYYY-MM-DD HH:mm:ss'));
                } else {
                    groupData.find('.modifyTime').html('-');
                }

                groupData.find('.campaignName').html(o.title);
                groupData.find('.sendPoint').html(o.amount);
                groupData.find('.campaignPersonNum').html(o.totalCount);
                groupData.find('.setUpUser').html(o.modifyUser);

                if (bcs.user.admin) {
                    groupData.find('.btn_detele').attr('id', o.id);
                    groupData.find('.btn_detele').click(btn_deteleFunc);
                } else {
                    groupData.find('.btn_detele').remove();
                }

                //				if(o.serialId == "0"){ //假設有預設群組 <0
                //					groupData.find('.btn_copy').remove();
                //					groupData.find('.btn_detele').remove();
                //				}

                $('#tableBody').append(groupData);
            });

        }).fail(function(response) {
            console.info(response);
            $.FailResponse(response);
        }).done(function() {});
    };

    var templateBody = {};
    templateBody = $('.dataTemplate').clone(true);
    $('.dataTemplate').remove();
    loadDataFunc();
});