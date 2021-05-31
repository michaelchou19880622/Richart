$(function() {
	var originalTr = {};
	// do Create
    $('#createGroup').click(function() {
        var groupName = $('#groupName').val();
        if (!groupName) {
            alert("群組名稱不可為空");
            return;
        }

        var postData = {};
        postData.richMenuGroupName = groupName;
        console.info('postData', postData);

        $.ajax({
            type: "POST",
            url: bcs.bcsContextPath + '/edit/createRichMenuGroup',
            cache: false,
            contentType: 'application/json',
            processData: false,
            data: JSON.stringify(postData)
        }).success(function(response) {
            console.info(response);
            if(response == 'Duplication'){
            	alert('群組名稱不可與其他群組重複');
            }else{
            	alert('成功建立');
                window.location.replace(bcs.bcsContextPath + '/edit/richMenuGroupListPage');
            }
        }).fail(function(response) {
            console.info(response);
            alert(response);
            $.FailResponse(response);
        })
    });
    
	// do Search
    $('#searchGroup').click(function() {
        var richMenuGroupName = $('#searchName').val();
        if (!richMenuGroupName) {
            alert("群組名稱不可為空");
            return;
        }

        //var postData = {};
        //postData.richMenuGroupName = groupName;
        console.info('richMenuGroupName', richMenuGroupName);
		var url = bcs.bcsContextPath + '/edit/richMenuGroupListPage?richMenuGroupName=' + richMenuGroupName;
		if ($.BCS.validateURL(url)) {
			window.location.replace(encodeURI(url));
		} else {
			alert('An attempt was made to open a webpage of foreign domain. No allowed!');
		}
//
//        $.ajax({
//            type: "POST",
//            url: bcs.bcsContextPath + '/edit/createRichMenuGroup',
//            cache: false,
//            contentType: 'application/json',
//            processData: false,
//            data: richMenuGroupName //JSON.stringify(postData)
//        }).success(function(response) {
//            console.info(response);
//            if(response == 'Duplication'){
//            	alert('群組名稱不可與其他群組重複');
//            }else{
//            	alert('成功建立');
//                window.location.replace(bcs.bcsContextPath + '/edit/richMenuGroupListPage');
//            }
//        }).fail(function(response) {
//            console.info(response);
//            alert(response);
//            $.FailResponse(response);
//        })
    });
    
    
    // do Delete
    var btn_deteleFunc = function() {
        var groupId = $(this).attr('id');
        console.info('btn_deteleFunc groupId:' + groupId);

        var r = confirm("請確認是否刪除");
        if (r) {
        } else {
            return;
        }

        $.ajax({
            type: "DELETE",
            url: bcs.bcsContextPath + '/admin/deleteRichMenuGroup/' + groupId 
        }).success(function(response) {
            console.info(response);
            alert("成功刪除");
            window.location.replace(bcs.bcsContextPath + '/edit/richMenuGroupListPage');
        }).fail(function(response) {
            console.info(response);
            $.FailResponse(response);
        }).done(function() {});
    };
    
	// Initialize Page
	var initPage = function(){
		// clone & remove
	    originalTr = $('.templateTr').clone(true);
	    $('.templateTr').remove();
	};
	
    var loadDataFunc = function() {
		// block
		$('.LyMain').block($.BCS.blockMsgRead);
		
		// get Parameter
		var richMenuGroupName = $.urlParam("richMenuGroupName");
		console.info("urlParam richMenuGroupName:", richMenuGroupName);
		// get data
		if(richMenuGroupName){
			getListData('/edit/searchRichMenuGroupList?richMenuGroupName=' + richMenuGroupName);
		}else{
			getListData('/edit/getRichMenuGroupList');
		}
		
    };
    
    var getListData = function(url){
        $.ajax({
            type: "GET",
            url: bcs.bcsContextPath + url
        }).success(function(response) {
            console.info("response:", response);
            $.each(response, function(i, o) {
                var templateTr = originalTr.clone(true); //增加一行
                console.info("templateTr:", templateTr);
                
                templateTr.find('.name').html(o.richMenuGroupName);
                templateTr.find('.title a').attr('href', bcs.bcsContextPath + '/edit/richMenuMemberListPage?groupId=' + o.richMenuGroupId);

                templateTr.find('.modifyTime').html(moment(o.modifyTime).format('YYYY-MM-DD HH:mm:ss'));
                templateTr.find('.modifyUser').html(o.modifyUser);

                if (bcs.user.admin) {
                    templateTr.find('.btn_detele').attr('id', o.richMenuGroupId);
                    templateTr.find('.btn_detele').click(btn_deteleFunc);
                } else {
                    templateTr.find('.btn_detele').remove();
                }
                
                // Append to Table
                $('.templateTbody').append(templateTr);
            });
        }).fail(function(response) {
            console.info(response);
            $.FailResponse(response);
        }).done(function() {
        	$('.LyMain').unblock();
        });		
	};
	
    loadDataFunc();
    initPage();
});