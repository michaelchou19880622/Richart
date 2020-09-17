$(function() {
	
	ajax_getCDNPath();	
	
	function ajax_getCDNPath() {
		$.ajax({
				type : "GET",
				url : encodeURI(bcs.mContextPath + "/getCDNPath"),
	            cache: false,
	            contentType: 'application/json',
	            processData: false,
		}).done(function(response) {
				var cdnpath = response;
				var expiredBgImage = cdnpath + 'Mobile/images/winningLetterReplyExpiredBackground.jpg';
				document.getElementsByTagName('body')[0].style.backgroundImage = 'url(' + expiredBgImage + ')';
		});
	}
});