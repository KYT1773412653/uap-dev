/**
 * 公共方法
 */
var Common = {
	favorMenusMax:10,//收藏菜单最大值
	Url: [{
		appCode: 'uap',
		restURL: "", //平台url地址
		menuURL: "" //平台菜单url地址
	}, {
		appCode: 'uap-bpm',
		restURL: "", //工作流url地址
		menuURL: "" //工作流菜单url地址
	}, {
		appCode: 'uap-schedule',
		restURL: "", //定时任务url地址
		menuURL: "" //定时任务菜单url地址
	}],
	/**
	 * 设置用户信息
	 * @param {Object} userInfo
	 */
	setUserInfo:function(userInfo){
		localStorage.setItem("UAP_USER",userInfo);
	},
	/**
	 * 获取登录用户信息
	 */
	getUserInfo:function(){
		if(localStorage.getItem("UAP_USER") == null){
			localStorage.clear();
			top.location.href = "/uap/ui/index.html";
			return;
		}
		var json = eval("(" + localStorage.getItem("UAP_USER") + ")");
		return json;
	},
	/**
	 * 获取token
	 */
	getToken:function(){
		if(Common.getUserInfo() == null){
			return null;
		}
		return Common.getUserInfo().token;
	},
	/**
	 * 获取当前点击的菜单id
	 */
	getCurrentMenu:function(){
		return localStorage.getItem("MENU_ID");
	},
	/**
	 * element 控件国际化
	 */
	elementLng:function(){
		if(localStorage.getItem("LANGUAGE") == "en"){
			ELEMENT.locale(ELEMENT.lang.en)
		}else if(localStorage.getItem("LANGUAGE") == "zh"){
			ELEMENT.locale(ELEMENT.lang.zhCN)
		}
	},
	/**
	 * 检验是否含有特殊字符
	 * @param {Object} s
	 */
	checkReg:function(s){
		var patrn = /[`~!@#$%^&*()_\-+=<>?:"{}|,.\/;'\\[\]·~！@#￥%……&*（）——\-+={}|《》？：“”【】、；‘’，。、]/im;  
	     if (!patrn.test(str)) {// 如果包含特殊字符返回false
	         return false;
	     }
	     return true;
	},
	/** 
	 *转换日期对象为日期字符串
	 * @param date 时间
	 * @param pattern 格式字符串,例如：yyyy-MM-dd hh:mm:ss, 默认为yyyy-MM-dd hh:mm:ss
	 * @return 符合要求的日期字符串
	 */
	getFormatDate:function(date, pattern) {
		//			console.log(date, pattern);
		try {
			if(date == undefined || date == null) {
				return "";
			}
			if(pattern == undefined || pattern == null) {
				pattern = "yyyy-MM-dd hh:mm:ss";
			}
			return date.format(pattern);
		} catch(e) {
			return date;
		}
	},
	/** 
	 * 转换long值为日期字符串
	 * @param longDate long值时间
	 * @param pattern 格式字符串,例如：yyyy-MM-dd hh:mm:ss
	 * @return 符合要求的日期字符串
	 */
	getFormatDateByLong:function(longDate, pattern) {
		try {
			if(longDate == undefined || longDate == null) {
				return "";
			}
			return this.getFormatDate(new Date(longDate), pattern);
		} catch(e) {
		}
		return "";
	},
	//获取接口访问url路径
	getRestURL: function(url) {
		var Url = "";
		var index = url.indexOf(":");
		var isTrue = false;
		for(var i = 0; i < Common.Url.length; i++) {
			var rowObjitem = Common.Url[i];
			if(url.substring(0, index) == rowObjitem.appCode) {
				isTrue = true;
				Url = rowObjitem.restURL;
				if(Url == "" || Url == undefined) {
					Url == "";
					for(var j = 0; j < Common.getUserInfo().apps.length; j++) {
						var rowObj = Common.getUserInfo().apps[j];
						if(rowObj.code == rowObjitem.appCode) {
							Url = rowObj.url;
							break;
						}
					}
				}
				break;
			}
		}
		if(!isTrue && Url == "") {
			for(var j = 0; j < Common.getUserInfo().apps.length; j++) {
				var Obj = Common.getUserInfo().apps[j];
				if(Obj.code == url.substring(0, index)) {
					Url = Obj.url;
					break;
				}
			}
		}
		Url = Url + url.substring(index + 1, url.length);
		return Url;
	},
	//获取菜单访问url路径
	getMenuURL: function(data) {
		var Url = "";
		var isTrue = false;
		for(var i = 0; i < Common.Url.length; i++) {
			var rowObjitem = Common.Url[i];
			if(data.app_code == rowObjitem.appCode) {
				isTrue = true;
				Url = rowObjitem.menuURL;
				if(Url == "" || Url == undefined) {
					Url = data.app_ui_url;
					break;
				}
				break;
			}
		}
		if(!isTrue && Url == "") {
			Url = data.app_ui_url;
		}
		return Url;
	},
}
//公用时间格式化
Date.prototype.format = function(format) {
	var o = {
		"M+": this.getMonth() + 1,
		"d+": this.getDate(),
		"h+": this.getHours(),
		"m+": this.getMinutes(),
		"s+": this.getSeconds(),
		"q+": Math.floor((this.getMonth() + 3) / 3),
		"S": this.getMilliseconds()
	}
	if(/(y+)/.test(format)) {
		format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
	}
	for(var k in o) {
		if(new RegExp("(" + k + ")").test(format)) {
			format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
		}
	}

	return format;
}
