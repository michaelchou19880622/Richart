$(function() {
	window.addEventListener('load', function() {
		document.querySelector('input[id="filepondFront"]').addEventListener('change', function() {
			if (this.files && this.files[0]) {
				var img = document.querySelector('img[id="myImgFront"]'); // $('img')[0]
				img.src = URL.createObjectURL(this.files[0]); // set src to
				img.onload = imageIsLoaded;
			}
		});

		document.querySelector('input[id="filepondBack"]').addEventListener('change', function() {
			if (this.files && this.files[0]) {
				var img = document.querySelector('img[id="myImgBack"]'); // $('img')[0]
				img.src = URL.createObjectURL(this.files[0]); // set src to
				img.onload = imageIsLoaded;
			}
		});
	});

	var imgFront = document.getElementById("myImgFront");
	imgFront.onclick = function() {
		$('#filepondFront').trigger('click');
	}

	var imgBack = document.getElementById("myImgBack");
	imgBack.onclick = function() {
		$('#filepondBack').trigger('click');
	}

	function imageIsLoaded() {
		alert(this.src); // blob url
		// update width and height ...
	}
});