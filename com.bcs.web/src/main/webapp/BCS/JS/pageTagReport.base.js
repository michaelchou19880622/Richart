// Item Controller
const ItemCtrl = (function () {
	let data = {
    currentItem: {},
    seatchItems: [],
    reportList: [],
    mainPageInfo: {
      current: 1,
			total: 0,
			size: 10
    }
	}
	// public
	return {
    saveMainReportData: function (mainData) {
      let {content, number, totalPages, size} = mainData
      // 列表資料
      data.reportList = content
      // 頁碼資料
      data.mainPageInfo = {
        current: number,
        total: totalPages,
        size
      }
    },
		logData: function () {
			return data
		}
	}
})();

// 畫面
const UICtrl = (function () {
	const UISelectors = {
    // 主要報表資訊
    itemList: '#mainReport tbody',
		itemListInfo: '#mainReport--info',
		listItems: '#mainReport tbody tr',
		// 搜尋區域
		searchField: {
			selectorItem: '.radioGroup li',
			selectorArea: '.p-searchArea',
			searchBtn: '.reportSearch__btn',
			datepicker: '.datepicker',
			dependsSecotorItemUI: '.dependsDateType > div',
			keywordInput: '.reportSearch__input'
		},
    mainPagination: {
      btnPrevious: '#btn_mainPreviousPage',
      btnNext: '#btn_mainNextPage',
      current: '#mainCurrentPage',
      totalSize: '#totalPageSize'
    },
    modal: {
      reportTagged: '#popupReportTagged',
      reportTaggedClose: '#reportTaggedClose',
      reportDiagram: '#popupReportDiagram',
      reportDiagramClose: '#reportDiagramClose',
    }
    //

	}
	return {
    /**
		 * 顯示標籤報表的item
		 * @param {Object} item
		 */
    showListItem(item) {
      item.forEach(item => {
        let {id, name, count} = item
				// Create tr element
				const tr = document.createElement('tr');
				// Add ID
				tr.id = `item-${id}`
				// Add HTML
				tr.innerHTML = `
					<td>${name}</td>
					<td>${count}</td>
					<td class="cursor"><i class="fa fa-line-chart diagram-item" aria-hidden="true"></i></td>
					<td class="cursor"><i class="fa fa-users tagged-item" aria-hidden="true"></i></td>
					<td>
					  <input class="btn-save pointer" type="button" value="上傳UID">
					</td>	`;
				// Insert item
				document.querySelector(UISelectors.itemList).insertAdjacentElement('beforeend', tr)
			})
    },
    // 呈現頁碼
    showPagination (currentIdx, totalPage, uiSelectorObj) {
      document.querySelector(UISelectors[uiSelectorObj]['current']).innerHTML = currentIdx + 1
			document.querySelector(UISelectors[uiSelectorObj]['totalSize']).innerHTML = totalPage
    },
    // 移除提示訊息
		mainTableInfo: function (isShow) {
			if (isShow) {
				document.querySelector(UISelectors.itemListInfo).style.display = 'block'
			} else {
				document.querySelector(UISelectors.itemListInfo).style.display = 'none'
			}
    },
		/**
		 * 調整特定選擇器顯示狀態
		 * @param {String} uiSelector 選擇器名稱
		 * @param {Boolean} isShow 關閉或開啟
		 */
		switchSelectorShowStatus: function (uiSeletctor, isShow) {
			if (isShow) {
				document.querySelector(uiSeletctor).classList.remove('hidden')
			} else {
				document.querySelector(uiSeletctor).classList.add('hidden')
			}
		},
		closeAllDependsDateType () {
			document.querySelectorAll(UISelectors.searchField.dependsSecotorItemUI).forEach(item => {
				item.classList.add('hidden');
			})
		},
		getSelectors: function () {
			return UISelectors
		}
	}
})();

// App Controller
const App = (function (ItemCtrl, UICtrl) {
  // Get UI Selectors
  const UISelectors = UICtrl.getSelectors();
	// Load event listeners
	const loadEventListener = function () {
    // 點擊列表
    document.querySelector(UISelectors.itemList).addEventListener('click', itemListClick)

		// 點擊搜尋條件選擇器
		document.querySelectorAll(UISelectors.searchField.selectorItem).forEach(item => {
			item.addEventListener('click', function(e) {
				let dateType = e.target.dataset['datetype']
				console.log(`${UISelectors.searchField.selectorItem}[data-dependstype=${dateType}]`)
				console.log(document.querySelector(`${UISelectors.searchField.selectorItem}[data-dependstype=${dateType}]`))
				let dependsDateType = document.querySelector(`${UISelectors.searchField.dependsSecotorItemUI}[data-dependstype=${dateType}]`)
				// 清除所有與篩選器有對應關係的UI
				UICtrl.closeAllDependsDateType()
				// 非所有選擇items皆有對應UI
				if(dependsDateType) { dependsDateType.classList.remove('hidden') }
			})
		})
		// 日期元件
		$(UISelectors.searchField.datepicker).datepicker({
			'minDate': 0,
			'dateFormat': 'yy-mm-dd'
		})
    // 關閉modal
    document.querySelector(UISelectors.modal.reportTaggedClose).addEventListener('click', function() {
      UICtrl.switchSelectorShowStatus(UISelectors.modal.reportTagged, false)
    })
    document.querySelector(UISelectors.modal.reportDiagramClose).addEventListener('click', function() {
      UICtrl.switchSelectorShowStatus(UISelectors.modal.reportDiagram, false)
    })
	}
	/**
	 * [API]: 取得列表資料
	 * @param {Number} page 頁碼
	 * @param {String} search 搜尋標籤
	 */
	const getReportList = function ({page, search, searchIdx} = {page: 1, search: '', searchIdx: 0}) {
		$('.LyMain').block($.BCS.blockMsgRead)
		let url = 'http://localhost:8080/bcs/BCS/JS/__mock/reportList.json'
		// let url = bcs.bcsContextPath + '/edit/getTagListData?search={search}&page={page}&size={size}&sort={sort}&direction={direction}'
		$.ajax({
			url,
			type: 'GET',
			contentType: 'application/json'
		}).success(function (res) {
      let {content, number, totalPages} = res
			// 顯示UI列表
			if (content.length) {
        // 呈現資料
        UICtrl.showListItem(content)
        // 關閉說明與否
				UICtrl.mainTableInfo(false)
			}
			// 儲存資料
			ItemCtrl.saveMainReportData(res)
			UICtrl.showPagination(number, totalPages, 'mainPagination')
		}).done(function () {
			$('.LyMain').unblock()
		})
	}
	// 點擊列表
	const itemListClick = function (e) {
		// Get UI Selectors
		const UISelectors = UICtrl.getSelectors();
		try {
			// Get list item id (item-0)
			const listSelectorId = e.target.parentNode.parentNode.id

			// Break into an array
			const listIdArr = listSelectorId.split('-')

			// Get the actual id 
			const id = parseInt(listIdArr[1])

			if (e.target.classList.contains('tagged-item')) {
			  // 	開啟popup視窗
			  UICtrl.switchSelectorShowStatus(UISelectors.modal.reportTagged, true)
			} else if (e.target.classList.contains('diagram-item')) {
        UICtrl.switchSelectorShowStatus(UISelectors.modal.reportDiagram, true)
				// 設定 currentItem
				// ItemCtrl.setCurrentItem(id, listSelectorId)
			}
		} catch (error) {
			console.log('something wrong!')
		}
	}
	// 搜尋日期選擇
	const searchSelecortItemsClick = function (e) {

	}
	// 點擊搜尋清單
	const searchListClick = function (e) {
		if (e.target.classList.contains('p-searchArea__item__delete')) {
			const tagTarget = e.target.parentNode.dataset['searchtags']
			const tagName = e.target.previousElementSibling.value
			// 移除畫面
			UICtrl.removeSearchTags('searchtags', tagTarget)
			// 移除資料
			ItemCtrl.removeSearchTags(tagName)
			// 重新呼叫列表
			getReportList()
		}
		e.preventDefault()
  }
  
  // 暫時
  const showChart = function (e) {
    var ctx = document.getElementById('myChart').getContext('2d');
    var chart = new Chart(ctx, {
      // The type of chart we want to create
      type: 'line',
      // The data for our dataset
      data: {
        labels: ['2020/01', '2020/02', '2020/03', '2020/04', '2020/05', '2020/06', '2020/07'],
        datasets: [{
            label: '標籤人數',
            borderColor: 'rgb(255, 99, 132)',
            data: [0, 10, 5, 2, 20, 30, 45]
        }]
      },
      // Configuration options go here
      options: {}
    });
  }

	return {
		init: function () {
			// loading
			$('.LyMain').block($.BCS.blockMsgRead);
			// Load event listener
			loadEventListener()
			// 呼叫API取得標籤資料
      getReportList()

      showChart()
		}
	}
})(ItemCtrl, UICtrl)

// Dom Ready後 call init
document.addEventListener("DOMContentLoaded", function () {
	App.init()
});