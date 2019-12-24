$(function() {

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

	var encodeUserData = function() {

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

		var userData = {
			'name' : data_WinnerName,
			'id_card_number' : data_IdCardNumber,
			'phonenumber' : data_PhoneNumber,
			'resident_address' : data_ResidentAddress,
			'mailing_address' : data_MailingAddress,
			'id_card_copy_front' : data_IdCardFront,
			'id_card_copy_back' : data_IdCardBack,
			'e_signature' : data_Signature
		};

		return userData;
	};

	submitBtn.addEventListener("click", function(e) {
		$.ajax({
			type : "GET",
			url : encodeURI(bcs.mContextPath + "/wl/getWinningLetter?winningLetterId=" + winningLetterId)
		}).done(function(response) {
			alert(response);

			var endTime = response['endTime'];
			var isExpired = func_checkIsExpired(endTime);
			alert("endTime = " + endTime + ", isExpired = " + isExpired);

			if (isExpired) {
				alert("資料審核失敗，此中獎回函填寫已過期。");
				return;
			}

			var postData = encodeUserData();
			alert('postData = ' + postData);

			alert('JSON.stringify(postData) = ' + JSON.stringify(postData));

			// $.ajax({
			// type : "POST",
			// url : bcs.bcsContextPath + apiUrl,
			// cache : false,
			// contentType : 'application/json',
			// processData : false,
			// data : JSON.stringify(postData)
			// }).done(function(response) {
			// console.info('response = ' + response);
			//
			// alert((actionType == 'Create' || actionType == 'Copy') ?
			// '中獎回函建立完成' :
			// '中獎回函已更新');
			// $('.LyMain').unblock();
			//
			// window.location.replace(bcs.bcsContextPath +
			// '/admin/winningLetterListPage');
			//
			// }).fail(function(response) {
			// console.info('response = ' + response.responseText);
			//
			// alert(response.responseText);
			// $('.LyMain').unblock();
			// })

		}).fail(function(response) {
			alert(response.responseText);
		})

	}, false);

});