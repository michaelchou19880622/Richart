/**
 * 
 */
$(function(){

	var $loadDataFunc = {};
	var $loadTotalPagesFunc = {};
	var $queryObj = {};
	
	var paginationContainer = $('#pagination-container');
	var defaultOpts = {
	        totalPages: 10
	};

	$initPagination = function(loadDataFunc, loadTotalPagesFunc, queryObj){

		if(loadDataFunc){
			$loadDataFunc = loadDataFunc;
		}
		
		if(loadTotalPagesFunc){
			$loadTotalPagesFunc = loadTotalPagesFunc;
		}
		
		if(queryObj){
			$queryObj = queryObj;
		}
		console.info(paginationContainer);
		
		paginationContainer.pagination(defaultOpts);

		var currentPage = paginationContainer.pagination('getCurrentPage');
		var totalPages = $loadTotalPagesFunc($queryObj);

		paginationContainer.pagination('destroy');
		paginationContainer.pagination({
	        pages: totalPages,
	        displayedPages: 5,
	        edges: 1,
	        currentPage: currentPage,
	        cssStyle: 'light-theme',
	        prevText: '<',
	        nextText: '>',
	        onInit: function () {
	        	$loadDataFunc($queryObj, 0);
	        },
	        onPageClick: function (page, evt) {
	            $loadDataFunc($queryObj, page -1);
	        }
	    });
	};

	$('#queryByFlag').click(function(){
		
		$queryObj.queryFlag = $("#flagInput").val();
		
		if($(':radio[name="sortOption"]')){
			$queryObj.isAsc = $(':radio[name="sortOption"]:checked').val() == 'asc'? 'true' : 'false';
		}
		
		$initPagination($loadDataFunc, $loadTotalPagesFunc, $queryObj);
	});
	
	$('#flagInput').keypress(function(e){

		var code = (e.keyCode ? e.keyCode : e.which);

		if (code == 13){
			$('#queryByFlag').click();
			return false;
		}
	});
	
	$(':radio[name="sortOption"]').change(function(){
		$('#queryByFlag').click();
		return false;
	});
});