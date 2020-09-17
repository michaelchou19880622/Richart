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
				var inactiveBgImage = cdnpath + 'Mobile/images/winningLetterReplyInactiveBackground.jpg';
				document.getElementsByTagName('body')[0].style.backgroundImage = 'url(' + inactiveBgImage + ')';
		});
	}
});