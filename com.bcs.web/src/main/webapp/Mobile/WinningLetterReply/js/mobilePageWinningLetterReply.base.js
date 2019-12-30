$(function() {

	var userId = "";
	
	function initializeApp() {
		userId = liff.getContext().userId;
		alert("[LIFF INITIAL] Complete : uid = " + userId);
	}
	
	function initializeLiff() {
	    liff
	        .init({
	            liffId: "1550669403-KA59ja3L"
	        })
	        .then(() => {
	            initializeApp();
	        })
	        .catch((err) => {
	    		alert("[LIFF INITIAL] Error : " + err.toString());
	    		userId = "";
	        });
	}
	
	initializeLiff();

	let urlParams = new URLSearchParams(window.location.search);

	var winningLetterId;

	if (urlParams.has('winningLetterId')) {

		winningLetterId = urlParams.get('winningLetterId');
		alert("中獎回函編號 : " + winningLetterId);
	}

	var winner_name = document.getElementById("winner_name");
	var winner_idCardNum = document.getElementById("winner_idCardNum");
	var winner_phoneNumber = document.getElementById("winner_phoneNumber");
	var winner_residentAddress = document.getElementById("winner_residentAddress");
	var winner_mailingAddress = document.getElementById("winner_mailingAddress");
	var cb_address = document.getElementById("cb_address");
	var myImgFront = document.getElementById("myImgFront");
	var myImgBack = document.getElementById("myImgBack");

	window.requestAnimFrame = (function(callback) {
		return window.requestAnimationFrame || window.webkitRequestAnimationFrame || window.mozRequestAnimationFrame || window.oRequestAnimationFrame || window.msRequestAnimaitonFrame
				|| function(callback) {
					window.setTimeout(callback, 1000 / 60);
				};
	})();

	var canvas = document.getElementById("sig-canvas");
	canvas.width = window.innerWidth * 97 / 100;

	var ctx = canvas.getContext("2d");
	ctx.strokeStyle = "#222222";
	ctx.lineWidth = 4;

	window.onresize = function(event) {
		ctx.canvas.width = window.innerWidth * 97 / 100;
		ctx.strokeStyle = "#222222";
		ctx.lineWidth = 4;
	};

	var drawing = false;
	var mousePos = {
		x : 0,
		y : 0
	};
	var lastPos = mousePos;

	canvas.addEventListener("mousedown", function(e) {
		e.preventDefault();
		drawing = true;
		lastPos = getMousePos(canvas, e);
	}, false);

	canvas.addEventListener("mousemove", function(e) {
		mousePos = getMousePos(canvas, e);
	}, false);

	canvas.addEventListener("mouseup", function(e) {
		drawing = false;
	}, false);

	// Add touch event support for mobile
	canvas.addEventListener("touchstart", function(e) {
		e.preventDefault();
		mousePos = getTouchPos(canvas, e);
		var touch = e.touches[0];
		var me = new MouseEvent("mousedown", {
			clientX : touch.clientX,
			clientY : touch.clientY
		});
		canvas.dispatchEvent(me);
	}, false);

	canvas.addEventListener("touchmove", function(e) {
		var touch = e.touches[0];
		var me = new MouseEvent("mousemove", {
			clientX : touch.clientX,
			clientY : touch.clientY
		});
		canvas.dispatchEvent(me);
	}, false);

	canvas.addEventListener("touchend", function(e) {
		var me = new MouseEvent("mouseup", {});
		canvas.dispatchEvent(me);
	}, false);

	function getMousePos(canvasDom, mouseEvent) {
		var rect = canvasDom.getBoundingClientRect();
		return {
			x : mouseEvent.clientX - rect.left,
			y : mouseEvent.clientY - rect.top
		}
	}

	function getTouchPos(canvasDom, touchEvent) {
		var rect = canvasDom.getBoundingClientRect();
		return {
			x : touchEvent.touches[0].clientX - rect.left,
			y : touchEvent.touches[0].clientY - rect.top
		}
	}

	function renderCanvas() {
		if (drawing) {
			ctx.moveTo(lastPos.x, lastPos.y);
			ctx.lineTo(mousePos.x, mousePos.y);
			ctx.stroke();
		}

		lastPos = mousePos;
	}

	(function drawLoop() {
		requestAnimFrame(drawLoop);
		renderCanvas();
	})();

	function clearCanvas() {
		canvas.width = canvas.width;
		ctx.strokeStyle = "#222222";
		ctx.lineWidth = 4;
	}

	var clearBtn = document.getElementById("sig-clearBtn");
	var submitBtn = document.getElementById("sig-submitBtn");

	clearBtn.addEventListener("click", function(e) {
		clearCanvas();
		ctx.strokeStyle = "#222222";
		ctx.lineWidth = 4;
	}, false);

	/* 檢查活動時間是否到期 */
	var func_checkIsExpired = function(datetime) {

		var currentDateTime = new Date();

		if (currentDateTime > datetime) {
			return 'True';
		} else {
			return 'False';
		}
	}
	
	function getArrayBuffer(file) {
		return new Promise((resolve, reject) => {
			const reader = new FileReader();
			reader.addEventListener('load', () => {
				resolve(reader.result);
			});
			reader.readAsArrayBuffer(file);
		})
	}

	var encodeWinningLetterRecordData = function() {

		// 中獎人姓名
		var data_WinnerName = winner_name.value;
		console.info("data_IdCardNumber = " + data_IdCardNumber);

		// 身分證字號
		var data_IdCardNumber = winner_idCardNum.value;
		console.info("data_IdCardNumber = " + data_IdCardNumber);

		// 聯絡電話
		var data_PhoneNumber = winner_phoneNumber.value;
		console.info("data_PhoneNumber = " + data_PhoneNumber);

		// 戶籍地址
		var data_ResidentAddress = winner_residentAddress.value;
		console.info("data_ResidentAddress = " + data_ResidentAddress);

		// 通訊地址
		var data_MailingAddress = ((cb_address.checked) ? data_ResidentAddress : winner_mailingAddress.value);
		console.info("data_MailingAddress = " + data_MailingAddress);

		// 身分證影本正面
		var data_IdCardFront = myImgFront.src;
		console.info("data_IdCardFront = " + data_IdCardFront);

		// 身分證影本反面
		var data_IdCardBack = myImgBack.src;
		console.info("data_IdCardBack = " + data_IdCardBack);

		// 領獎申請人簽章
		var data_Signature = canvas.toDataURL();
		console.info("data_Signature = " + data_Signature);

// var winningLetterRecordData = {
// 'uid' : userId,
// 'winningLetterId' : winningLetterId,
// 'name' : data_WinnerName,
// 'id_card_number' : data_IdCardNumber,
// 'phonenumber' : data_PhoneNumber,
// 'resident_address' : data_ResidentAddress,
// 'mailing_address' : data_MailingAddress,
// 'id_card_copy_front' : data_IdCardFront,
// 'id_card_copy_back' : data_IdCardBack,
// 'e_signature' : data_Signature
// };
		
		var winningLetterRecordData = {
			'uid' : userId,
			'winningLetterId' : winningLetterId,
			'name' : data_WinnerName,
			'id_card_number' : data_IdCardNumber,
			'phonenumber' : data_PhoneNumber,
			'resident_address' : data_ResidentAddress,
			'mailing_address' : data_MailingAddress
		};

		return winningLetterRecordData;
	};

	submitBtn.addEventListener("click", function(e) {
		$.ajax({
			type : "GET",
			url : encodeURI(bcs.mContextPath + "/wl/getWinningLetter?winningLetterId=" + winningLetterId)
		}).done(function(response) {
			var endTime = response['endTime'];
			var isExpired = func_checkIsExpired(endTime);
			alert("endTime = " + endTime + ", isExpired = " + isExpired);

			if (isExpired == 'True') {
				alert("很抱歉，此中獎回函填寫時間已過期。");
				window.location.replace('https://richart.tw/TSDIB_RichartWeb/RC00/RC000000');
				return;
			}

			/* Encode winner data to array */
			var postData = encodeWinningLetterRecordData();
			alert('JSON.stringify(postData) = ' + JSON.stringify(postData));
			
			$('.LyMain').block($.BCS.blockMsgSave);
			
			/* Upload winning letter record data */
			$.ajax({
				type : "POST",
				url : encodeURI(bcs.mContextPath + "/wl/updateWinningLetterRecord"),
	            cache: false,
	            contentType: 'application/json',
	            processData: false,
				data : JSON.stringify(postData)
			}).done(function(response){
				console.info(response);
				alert(response);
				alert('資料上傳成功');

			}).fail(function(response) {
				console.info(response);
				alert('資料上傳失敗');
				
				$('.LyMain').unblock();
			})
			
			/*
			 * Upload image ( Id card copy front ) to backend api to convert and
			 * store to database
			 */
			if (imageSrcFront != null){
				var formData_front = new FormData();
				formData_front.append("filePart", imageSrcFront);
				
				$.ajax({
					type : "POST",
					url : encodeURI(bcs.mContextPath + "/wl/updateWinnerIdCard"),
		            cache: false,
	                contentType: false,
	                processData: false,
					data : formData_front
				}).done(function(response){
					console.info(response);
					
					alert('image front test ok');

				}).fail(function(response){
					console.info(response);
					
					alert('image front test fail');
					
					$('.LyMain').unblock();
				})
			}

			/*
			 * Upload image ( Id card copy back ) to backend api to convert and
			 * store to database
			 */
			if (imageSrcBack != null) {
				var formData_back = new FormData();
				formData_back.append("filePart", imageSrcBack);
				
				$.ajax({
					type : "POST",
					url : encodeURI(bcs.mContextPath + "/wl/updateWinnerIdCard"),
		            cache: false,
	                contentType: false,
	                processData: false,
					data : formData_back
				}).done(function(response){
					console.info(response);
					
					alert('image back test ok');

				}).fail(function(response){
					console.info(response);
					
					alert('image back test fail');
					
					$('.LyMain').unblock();
				})
			}

		}).fail(function(response) {
			alert(response.responseText);
		})

	}, false);

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
	
	var imageSrcFront = null;
	var imageSrcBack = null;

	window.addEventListener('load', function() {
		document.querySelector('input[id="filepondFront"]').addEventListener('change', function() {
			if (this.files && this.files[0]) {
				alert("this.files[0].name = " + this.files[0].name);
				
				if (!this.files[0].type.match(/image.*/)) {
					alert('Sorry, only images are allowed');
					return;
				}
				
				imageSrcFront = this.files[0];
				
				var img = document.querySelector('img[id="myImgFront"]');
				img.src = URL.createObjectURL(this.files[0]);
				img.onload = imageIsLoaded("正面");
			}
		});

		document.querySelector('input[id="filepondBack"]').addEventListener('change', function() {
			if (this.files && this.files[0]) {
				alert("this.files[0].name = " + this.files[0].name);

				if (!this.files[0].type.match(/image.*/)) {
					alert('Sorry, only images are allowed');
					return;
				}
				
				imageSrcBack = this.files[0];
				
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