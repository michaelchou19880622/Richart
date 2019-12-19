$(function() {
	'use strict';

	// Check for the various File API support.
	if (window.File && window.FileReader && window.FileList && window.Blob) {
		console.log('OK');
	} else {
		alert('The File APIs are not fully supported in this browser.');
	}

	var reader;
	var files;
	var dropZone = document.getElementById('dropZone');
	var outputTag = document.getElementById('output');

	function clickHandler(e) {
		$("input[id='my_file']").click();
	}

	/** Event handlers for ReadFile. * */
	// FileReader abort Handler
	function abortHandler(e) {
		alert('File read Canceled');
	}

	// FileReader Error Handler
	function errorHandler(e) {
		switch (e.target.error.code) {
		case e.target.error.NOT_FOUND_ERR:
			alert('File Not Found!');
			break;
		case e.target.error.NOT_READABLE_ERR:
			alert('File is not readable');
			break;
		case e.target.error.ABORT_ERR:
			break; // noop
		default:
			alert('An error occurred reading this file.');
		}
	}

	// Event after loading a file completed (Append thumbnail.)
	function loadHandler(theFile) {

		return function(e) {
			var newFile = document.createElement('div');
			var picture = document.createElement('pictureDUF');
			var img = document.createElement('div');
			img.style.backgroundImage = 'url(' + e.target.result + ')';
			img.title = escape(theFile.name);
			img.className = 'thumbDUF';

			picture.appendChild(img);
			newFile.appendChild(picture);
			newFile.className = 'fileDUF';

			outputTag.insertBefore(newFile, null);
		}
	}

	// Main function for ReadFile and appending thumbnails.
	function appendThumbnail(f) {
		reader = new FileReader();
		reader.onerror = errorHandler;
		reader.onabort = abortHandler;
		reader.onload = loadHandler(f);
		reader.readAsDataURL(f);
	}

	/**
	 * Main Event Handler to deal with the whole drop & upload process.
	 */
	function handleFileSelect(e) {
		e.stopPropagation();
		e.preventDefault();

		dropZone.classList.remove('dragover');

		files = e.dataTransfer.files;

		// Go through each file.
		for (var i = 0, f; f = files[i]; i++) {
			// Only process image files.
			if (!f.type.match('image.*'))
				continue;
			appendThumbnail(f);

		} // END for

	} // END handleFileSelect

	/**
	 * functions associating "drag" event.
	 */
	function handleDragEnter(e) {
		e.stopPropagation();
		e.preventDefault();
		this.classList.add('dragover');
	}
	function handleDragLeave(e) {
		e.stopPropagation();
		e.preventDefault();
		this.classList.remove('dragover');
	}
	function handleDragOver(e) {
		e.stopPropagation();
		e.preventDefault();
		e.dataTransfer.dropEffect = 'copy'; // Explicitly show this is a copy.
	}

	/**
	 * Setup the event listeners.
	 */
	dropZone.addEventListener('dragenter', handleDragEnter, false)
	dropZone.addEventListener('dragleave', handleDragLeave, false)
	dropZone.addEventListener('dragover', handleDragOver, false);
	dropZone.addEventListener('drop', handleFileSelect, false);
	dropZone.addEventListener('click', clickHandler, false);

});