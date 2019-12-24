$(function() {

	var cb_address = document.getElementById('cb_address');

	var winner_mailingAddress = document.getElementById('winner_mailingAddress');

	var MdFRMWLInput_mAddress = document.getElementById('MdFRMWLInput_mAddress');

	cb_address.onclick = function() {

		winner_mailingAddress.disabled = cb_address.checked;

		if (cb_address.checked) {
			winner_mailingAddress.style.color = 'black';
			MdFRMWLInput_mAddress.style.backgroundColor = '#dddddd';
		} else {
			winner_mailingAddress.style.color = 'black';
			MdFRMWLInput_mAddress.style.backgroundColor = 'white';
		}
	}

	window.addEventListener('load', function() {
		document.querySelector('input[id="filepondFront"]').addEventListener('change', function() {
			if (this.files && this.files[0]) {
				var img = document.querySelector('img[id="myImgFront"]');
				img.src = URL.createObjectURL(this.files[0]);
				img.onload = imageIsLoaded("正面");
			}
		});

		document.querySelector('input[id="filepondBack"]').addEventListener('change', function() {
			if (this.files && this.files[0]) {
				var img = document.querySelector('img[id="myImgBack"]');
				img.src = URL.createObjectURL(this.files[0]);
				img.onload = imageIsLoaded("反面");
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

	function imageIsLoaded(type) {
		alert("身分證影本" + type + "上傳成功");
	}
});