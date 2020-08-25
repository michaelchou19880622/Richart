// Item Controller
const ItemCtrl = (function () {
	let data = {
		// 標籤列表資訊
		tagList: [],
		pageInfo: {
			current: 1,
			total: 0,
			size: 10
		},
		searchTagsList: [], 
		currentItem: null
	}
	// public
	return {
		// 取得當前選擇目標
		getCurrentItem: function () {
			return data.currentItem
		},
		/**
		 * 設定當前點擊或focus的標籤內容
		 * @param {Number} id 
		 * @param {String} uiSelector 
		 */
		setCurrentItem: function (id, uiSelector) {
			data.tagList.forEach(function (item) {
				if (item.id === id) {
					data.currentItem = item
					data.currentItem['htmlSelector'] = uiSelector
				}
			})
		},
		/**
		 * 儲存標籤資料
		 * @param {Object} tagsdata 
		 */
		saveTagsData: function (tagsdata) {
			// 列表資料
			data.tagList = tagsdata.content
			// 頁碼資料
			data.pageInfo = {
				current: tagsdata.number,
				total: tagsdata.totalPages,
				size: tagsdata.size
			}
		},
		/**
		 * 儲存搜尋的標籤資料
		 * @param {String} searchItem 搜尋的資料
		 */
		saveSearchTags: function (searchItem) {
			if (data.searchTagsList.indexOf(searchItem) ===  (-1)) {
				data.searchTagsList.push(searchItem)
			}
		},
		// 移除特定tags
		removeSearchTags: function (searchItem) {
			let idx = data.searchTagsList.findIndex(item => item == searchItem)
			data.searchTagsList.splice(idx, 1)
		},
		getSearchTags: function () {
			return data.searchTagsList
		},
		logData: function () {
			return data
		}
	}
})();

// 畫面
const UICtrl = (function () {
	const UISelectors = {
		addTagBtn: '.addTagBtn',
		searchTagInput: '.tagSearch__input',
		searchTagBtn: '.tagSearch__btn',
		searchTagsArea: '.p-searchArea',
		searchTagsAreaItem: '.p-searchArea__item',
		itemList: '.tagTable tbody',
		itemListInfo: '.tagTable--info',
		listItems: '.tagTable tbody tr',
		deleteItems: '.delete-item',
		editItems: '.edit-item',
		modalCancel: '.popupClose',
		modalCreateTag: '#popupCreateTags',
		//
		deleteModalBtn: '.deleteTagBtn',
		deleteModalConfirm: '#deleteConfirmModal',
		// 頁碼
		currentPageIndex: '#currentPageIndex',
		totalPageSize: '#totalPageSize',
		pagePrevious: '#btn_PreviousPage',
		pageNext: '#btn_NextPage'
	}
	return {
		/**
		 * 顯示標籤列表的item
		 * @param {Object} item
		 */
		showListItem: function (item) {
			document.querySelector(UISelectors.itemList).innerHTML = `<tr><th>標籤</th><th>標籤說明</th><th>編修人員</th><th>編修時間</th><th>功能</th></tr>`
			item.forEach(item => {
				// Create tr element
				const tr = document.createElement('tr');

				// Add ID
				tr.id = `item-${item.id}`

				// Add HTML
				tr.innerHTML = `
					<td><span class="edit-item">${item.tagName}</span></td>
					<td>${item.tagDescription}</td>
					<td>${item.modifyUser}</td>
					<td>${moment(item.modifyTime).format('YYYY-MM-DD HH:mm')}</td>
					<td>
						<input class="btn_detele mb-0 delete-item" style="cursor: pointer;" type="button" value="刪除">
					</td>	`;
				// Insert item
				document.querySelector(UISelectors.itemList).insertAdjacentElement('beforeend', tr)
			})
		},
		// 呈現頁碼
		showPagination (currentIdx, totalPage) {
			document.querySelector(UISelectors.currentPageIndex).innerHTML = currentIdx + 1
			document.querySelector(UISelectors.totalPageSize).innerHTML = totalPage
		},
		/**
		 * 顯示特定class modal, 並顯示文字至modal
		 * @param {String} uiSelector 選擇器名稱
		 * @param {String} content 文字
		 */
		showInfoModal: function (uiSelector, content) {
			console.log(content)
			document.querySelector(uiSelector).classList.remove('hidden')
			document.querySelector(`${uiSelector} .rwModal__content`).innerHTML = content
		},
		/**
		 * 調整特定選擇器顯示狀態
		 * @param {String} uiSelector 選擇器名稱
		 * @param {Boolean} isShow 關閉或開啟
		 */
		switchSelectorShowStatus: function (uiSeletctor, isShow) {
			if (isShow) {
				console.log(uiSeletctor)
				document.querySelector(uiSeletctor).classList.remove('hidden')
			} else {
				console.log(uiSeletctor)
				document.querySelector(uiSeletctor).classList.add('hidden')
			}
		},
		// 移除提示訊息
		tagTableInfo: function (isShow) {
			if (isShow) {
				document.querySelector(UISelectors.itemListInfo).style.display = 'block'
			} else {
				document.querySelector(UISelectors.itemListInfo).style.display = 'none'
			}
		},
		// 顯示標籤
		showSearchTags: function(name, number) {
			const li = document.createElement('li')
			li.classList.add('p-searchArea__item', 'mr-1')
			// 設定tags資訊在自定義data上
			li.setAttribute('data-searchTags', `tags${number}`)
			// 組 li 裡面資訊
			li.innerHTML = `
				<span class="p-searchArea__item__name">${name}</span>
				<span class="p-searchArea__item__delete">x</span>`
			// Insert item
			document.querySelector(UISelectors.searchTagsArea).insertAdjacentElement('beforeend', li)
		},
		removeSearchTags: function(dataName, dataVal) {
			document.querySelector(`.p-searchArea__item[data-${dataName}=${dataVal}]`).remove()
		},
		// 取得UI選擇器
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
		// Get UI Selectors
		// const UISelectors = UICtrl.getSelectors();

		// 點擊列表
		document.querySelector(UISelectors.itemList).addEventListener('click', itemListClick)

		// 點擊標籤搜尋的按鈕
		document.querySelector(UISelectors.searchTagBtn).addEventListener('click', searchBtnClick)

		// 點擊新增標籤按鈕
		document.querySelector(UISelectors.addTagBtn).addEventListener('click', function(e) {
			UICtrl.switchSelectorShowStatus(UISelectors.modalCreateTag, true)
		})
		// 點擊搜尋框列表
		document.querySelector(UISelectors.searchTagsArea).addEventListener('click', searchListClick)

		// 切換頁碼
		
		// 點擊modal刪除標籤按鈕
		document.querySelector(UISelectors.deleteModalBtn).addEventListener('click', itemDeleteSubmit)

		// 關閉新增/編輯modal
		document.querySelector(UISelectors.modalCancel).addEventListener('click', function () {
			UICtrl.switchSelectorShowStatus(UISelectors.modalCreateTag, false)
		})
	}

	/**
	 * [API]: 取得列表資料
	 * @param {Number} page 頁碼
	 * @param {String} search 搜尋標籤
	 */
	const getTagsList = function ({page, search, searchIdx} = {page: 1, search: '', searchIdx: 0}) {
		console.log(page)
		$('.LyMain').block($.BCS.blockMsgRead)
		let url = 'http://localhost:8080/bcs/BCS/JS/__mock/tagList.json'
		// let url = bcs.bcsContextPath + '/edit/getTagListData?search={search}&page={page}&size={size}&sort={sort}&direction={direction}'
		$.ajax({
			url,
			type: 'GET',
			contentType: 'application/json'
		}).success(function (res) {
			console.log('searchItem', search)
			// 顯示UI列表
			if (res.content.length) {
				UICtrl.showListItem(res.content)
				UICtrl.tagTableInfo(false)
			}
			// 儲存資料
			ItemCtrl.saveTagsData(res)
			console.log(res.current)
			UICtrl.showPagination(res.number, res.totalPages)
			// set Tags 列表
			if (search) {
				ItemCtrl.saveSearchTags(search, searchIdx)
				// 顯示 tags 在搜尋列
				UICtrl.showSearchTags(search, searchIdx)
				// 清空搜尋框
				document.querySelector(UISelectors.searchTagInput).value = ''
			}
		}).done(function () {
			$('.LyMain').unblock()
		})
	}
	// [API]
	const itemCreateSubmit = function (e) {
		// 新增標籤
		console.log('itemCreateSubmit')
	}
	// [API]
	const itemUpdateSubmit = function (e) {
		// 新增標籤
		console.log('itemCreateSubmit')
	}
	// [API] 刪除
	const itemDeleteSubmit = function (e) {
		console.log(ItemCtrl.getCurrentItem().id)
		// 關閉modal
		// @todo
		// $('.LyMain').block($.BCS.blockMsgRead)
		// let id = ItemCtrl.getCurrentItem().id
		//
		document.querySelector(UISelectors.deleteModalConfirm).classList.add('hidden')
		e.preventDefault();
	}
	// 前往編輯頁面
	const editListClick = function (id) {
		console.log('edit', id)
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

			if (e.target.classList.contains('edit-item')) {
			// 	// 開啟popup視窗
			// 	console.log('edit')
			} else if (e.target.classList.contains('delete-item')) {
				// 設定 currentItem
				ItemCtrl.setCurrentItem(id, listSelectorId)
				// Show Modal 
				let content = `確認刪除標籤 <p class="mt-2 mb-2">【 ${ItemCtrl.getCurrentItem().tagName} 】</p>`
				// 顯示刪除確認訊息
				UICtrl.showInfoModal(UISelectors.deleteModalConfirm, content)
			}
		} catch (error) {
			console.log('something wrong!')
		}
	}
	// 點擊搜尋框
	const searchBtnClick = function (e) {
		// 取得輸入標籤輸入框的資料
		let searchInput = document.querySelector(UISelectors.searchTagInput)
		// 取得頁面當前搜尋的所有標籤
		let getSearchTagsList = ItemCtrl.getSearchTags()
		let currentSearchTagLeng = getSearchTagsList.length
		// 檢查是否搜尋框是否有值，且沒有被搜尋過，若是新增，否清空搜尋框
		if (searchInput.value && getSearchTagsList.indexOf(searchInput.value) === (-1)) {
			getTagsList({page: 1, search: searchInput.value, searchIdx: currentSearchTagLeng})
		} else {
			searchInput.value = ''
		}
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
			getTagsList()
		}

		e.preventDefault()
		console.log(ItemCtrl.getSearchTags())
	}

	return {
		init: function () {
			// loading
			$('.LyMain').block($.BCS.blockMsgRead);

			// Load event listener
			loadEventListener()

			// 呼叫API取得標籤資料
			getTagsList()
		}
	}
})(ItemCtrl, UICtrl)

// Dom Ready後 call init
document.addEventListener("DOMContentLoaded", function () {
	App.init()
});