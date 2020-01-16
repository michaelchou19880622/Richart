$(function() {
	let urlParams = new URLSearchParams(window.location.search);

	var liffId;
	if (urlParams.has('liffId')) {
		liffId = urlParams.get('liffId');
	}
	
	var winningLetterId;
	if (urlParams.has('winningLetterId')) {
		winningLetterId = urlParams.get('winningLetterId');
	}
	console.info('winningLetterId = ' + winningLetterId);

	var winningLetterName;
	if (urlParams.has('winningLetterName')) {
		winningLetterName = urlParams.get('winningLetterName');
	}
	console.info('winningLetterName = ' + winningLetterName);

	var wlEndTime;
	if (urlParams.has('endTime')) {
		wlEndTime = urlParams.get('endTime');
	}
	console.info('wlEndTime = ' + wlEndTime);

	var gifts;
	if (urlParams.has('gifts')) {
		gifts = urlParams.get('gifts');
	}
	console.info('gifts = ' + gifts);

	var replacedWinningLetterName = document.getElementById("replacedWinningLetterName");
	replacedWinningLetterName.innerHTML = winningLetterName;
	
	var replacedEndTime = document.getElementById("replacedEndTime");
	str = (replacedEndTime.innerHTML || replacedEndTime.textContent);
	replacedEndTime.innerHTML = str.replace("${EndTime}", wlEndTime);

	var replacedWinningLetterGifts = document.getElementById("replacedWinningLetterGifts");
	replacedWinningLetterGifts.value = gifts;
	
	var userId = "";

	function initializeApp() {
		userId = liff.getContext().userId;
	}

	function initializeLiff() {
	    liff.init({
            liffId: liffId
        }).then(() => {
            initializeApp();
        }).catch((err) => {
    		userId = "";
        });
	}

	initializeLiff();

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

		// 身分證字號
		var data_IdCardNumber = winner_idCardNum.value;

		// 聯絡電話
		var data_PhoneNumber = winner_phoneNumber.value;

		// 戶籍地址
		var data_ResidentAddress = winner_residentAddress.value;

		// 通訊地址
		var data_MailingAddress = ((cb_address.checked) ? data_ResidentAddress : winner_mailingAddress.value);

		// 身分證影本正面
		var data_IdCardFront = myImgFront.src;

		// 身分證影本反面
		var data_IdCardBack = myImgBack.src;

		// 領獎申請人簽章
		var data_Signature = canvas.toDataURL();
		console.info("data_Signature = " + data_Signature);

		var winningLetterRecordData = {
			'uid' : userId,
			'winningLetterId' : winningLetterId,
			'name' : data_WinnerName,
			'idCardNumber' : data_IdCardNumber,
			'phoneNumber' : data_PhoneNumber,
			'residentAddress' : data_ResidentAddress,
			'mailingAddress' : data_MailingAddress
		};

		return winningLetterRecordData;
	};

	function dataURLtoFile(dataurl, filename) {
        var arr = dataurl.split(','), mime = arr[0].match(/:(.*?);/)[1],
            bstr = atob(arr[1]), n = bstr.length, u8arr = new Uint8Array(n);
        while (n--) {
            u8arr[n] = bstr.charCodeAt(n);
        }

        return new File([u8arr], filename, {type:mime});
    }
	
	function isCanvasBlank(canvas) {
	  return !canvas.getContext('2d')
	    .getImageData(0, 0, canvas.width, canvas.height).data
	    .some(channel => channel !== 0);
	}

	var imageSrcFront = null;
	var imageSrcBack = null;
	var imageSrcESignature = null;
	var winningLetterRecordId;
	
	function ajax_updateWinnerIdCardFront() {
		var formData_front = new FormData();
		formData_front.append("filePart", imageSrcFront);

		return new Promise((resolve, reject) => {
			$.ajax({
				type : "POST",
				url : encodeURI(bcs.mContextPath + "/wl/updateWinnerIdCard?winningLetterRecordId=" + winningLetterRecordId + "&type=f"),
	            cache: false,
	            contentType: false,
	            processData: false,
				data : formData_front,
				success : function(data) {
					resolve(data)
					ajax_updateWinnerIdCardBack();
				},
				error : function(error) {
					reject(error)
				}
			})
		})
	}
	
	function ajax_updateWinnerIdCardBack() {
		var formData_back = new FormData();
		formData_back.append("filePart", imageSrcBack);

		return new Promise((resolve, reject) => {
				$.ajax({
				type : "POST",
				url : encodeURI(bcs.mContextPath + "/wl/updateWinnerIdCard?winningLetterRecordId=" + winningLetterRecordId + "&type=b"),
	            cache: false,
	            contentType: false,
	            processData: false,
				data : formData_back,
				success : function(data) {
					resolve(data)
					ajax_updateWinnerESignature();
				},
				error : function(error) {
					reject(error)
				}
			})
		})
	}

	function ajax_updateWinnerESignature() {
		
		var isBlank = isCanvasBlank(canvas);
		
		imageSrcESignature = (isBlank)? null : dataURLtoFile(canvas.toDataURL('images/png'), "img.png");

		var formData_eSignature = new FormData();
		formData_eSignature.append("filePart", imageSrcESignature);

		return new Promise((resolve, reject) => {
				$.ajax({
				type : "POST",
				url : encodeURI(bcs.mContextPath + "/wl/updateWinnerESignature?winningLetterRecordId=" + winningLetterRecordId),
	            cache: false,
	            contentType: false,
	            processData: false,
				data : formData_eSignature,
				success : function(data) {
					resolve(data)
					
					alert("用戶資料已成功上傳，台新銀行將進行檢核回饋作業，謝謝。");

					$('.columnUploadImage').unblock();

					window.location.replace('https://richart.tw/TSDIB_RichartWeb/RC00/RC000000');
					
				},
				error : function(error) {
					reject(error)
				}
			})
		})
	}

	submitBtn.addEventListener("click", function(e) {
		
		// 中獎人姓名
		if (!winner_name.value) {
			alert("很抱歉，中獎人姓名不能為空。");
			return;
		}

		// 身分證字號
		if (!winner_idCardNum.value) {
			alert("很抱歉，身分證字號不能為空。");
			return;
		}

		// 聯絡電話
		if (!winner_phoneNumber.value) {
			alert("很抱歉，聯絡電話不能為空。");
			return;
		}

		// 戶籍地址
		if (!winner_residentAddress.value) {
			alert("很抱歉，戶籍地址不能為空。");
			return;
		}

		// 通訊地址
		if (!cb_address.checked && !winner_mailingAddress.value) {
			alert("很抱歉，通訊地址不能為空。");
			return;
		}

		// 身分證影本正面
		if (!myImgFront.src || !myImgFront.src.trim()) {
			alert("很抱歉，身分證影本正面未正確上傳。");
			return;
		}

		// 身分證影本反面
		if (!myImgBack.src || !myImgBack.src.trim()) {
			alert("很抱歉，身分證影本反面未正確上傳。");
			return;
		}
		
		$('.columnUploadImage').block($.BCS.blockWinnerInfoUploading);
		
		$.ajax({
			type : "GET",
			url : encodeURI(bcs.mContextPath + "/wl/getWinningLetter?winningLetterId=" + winningLetterId)
		}).done(function(response) {
			// 預留判斷中獎回函是否正在活動期間? 如果不是可以擋用戶不讓用戶填寫資料。 
			// ps. 暫時沒此需求...
			
			var endTime = response['endTime'];
			var isExpired = func_checkIsExpired(endTime);

			if (isExpired == 'True') {
				alert("很抱歉，此中獎回函填寫時間已過期。");
				window.location.replace('https://richart.tw/TSDIB_RichartWeb/RC00/RC000000');
				return;
			}
			
			/* Encode winner data to array */
			var postData = encodeWinningLetterRecordData();
			
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
				winningLetterRecordId = response;
				
				ajax_updateWinnerIdCardFront();
				
			}).fail(function(response) {
				console.info(response);
				alert('用戶資料上傳出現異常，請重新嘗試。如仍出現錯誤訊息，請聯繫相關人員。');

				$('.columnUploadImage').unblock();
			})

		}).fail(function(response) {
			alert(response.responseText);

			$('.columnUploadImage').unblock();
		});

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

	window.addEventListener('load', function() {
		document.querySelector('input[id="filepondFront"]').addEventListener('change', function() {
			if (this.files && this.files[0]) {
				if (!this.files[0].type.match(/image.*/)) {
					alert('很抱歉，您選擇的檔案格式暫不支持，請重新選擇。');
					return;
				}

				imageSrcFront = this.files[0];

				var img = document.querySelector('img[id="myImgFront"]');
				img.src = URL.createObjectURL(this.files[0]);

				img.onload = idCardIsLoaded;
			}
		});

		document.querySelector('input[id="filepondBack"]').addEventListener('change', function() {
			if (this.files && this.files[0]) {
				if (!this.files[0].type.match(/image.*/)) {
					alert('很抱歉，您選擇的檔案格式暫不支持，請重新選擇。');
					return;
				}

				imageSrcBack = this.files[0];

				var img = document.querySelector('img[id="myImgBack"]');
				img.src = URL.createObjectURL(this.files[0]);
				
				img.onload = idCardIsLoaded;
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

	function idCardIsLoaded() {
		alert("請確認身份證資訊皆有完整露出");
	}
});