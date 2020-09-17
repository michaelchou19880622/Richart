$(document).ready(function() {
    
    var section_h = $(".coupon_alert").height();
    var obj_h = $(".coupon_alert > strong").height();
    
    var pos_top = Math.ceil((section_h - obj_h)/2)-60;
    
    $(".coupon_alert > strong").css("top",pos_top);

    ajax_getCDNPath();

	function ajax_getCDNPath() {
		$.ajax({
				type : "GET",
				url : '../m/getCDNPath',
	            cache: false,
	            contentType: 'application/json',
	            processData: false,
		}).done(function(response) {
				var cdnpath = response;
				var usedCouponBgImage = cdnpath + 'Mobile/Coupon/images/coupon_used_icon.png';
                var backgroundCSS = 'url(' + usedCouponBgImage + ')' + '59% 0 no-repeat';
                $(".coupon_alert > strong").css("background", backgroundCSS);

				var kvBgImage = cdnpath + 'Mobile/Coupon/images/kv.png';
				var kvClass = document.getElementsByClassName('kv')[0];
				var img = kvClass.getElementsByTagName('img')[0];
				img.src = kvBgImage;
		});
	}

});