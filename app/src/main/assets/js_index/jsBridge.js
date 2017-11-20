//BASE64
(function (global, doc, undef) {
    var BASE64_MAPPING = [
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
            'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
            'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
            'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_',
        ];
    /**
     *ascii convert to binary
     */
    var _toBinary = function (ascii) {
        var binary = new Array();
        while (ascii > 0) {
            var b = ascii % 2;
            ascii = Math.floor(ascii / 2);
            binary.push(b);
        }
        /*
		var len = binary.length;
		if(6-len > 0){
			for(var i = 6-len ; i > 0 ; --i){
				binary.push(0);
			}
		}*/
        binary.reverse();
        return binary;
    };

    /**
     *binary convert to decimal
     */
    var _toDecimal = function (binary) {
        var dec = 0;
        var p = 0;
        for (var i = binary.length - 1; i >= 0; --i) {
            var b = binary[i];
            if (b == 1) {
                dec += Math.pow(2, p);
            }
            ++p;
        }
        return dec;
    };

    /**
     *unicode convert to utf-8
     */
    var _toUTF8Binary = function (c, binaryArray) {
        var mustLen = (8 - (c + 1)) + ((c - 1) * 6);
        var fatLen = binaryArray.length;
        var diff = mustLen - fatLen;
        while (--diff >= 0) {
            binaryArray.unshift(0);
        }
        var binary = [];
        var _c = c;
        while (--_c >= 0) {
            binary.push(1);
        }
        binary.push(0);
        var i = 0,
            len = 8 - (c + 1);
        for (; i < len; ++i) {
            binary.push(binaryArray[i]);
        }

        for (var j = 0; j < c - 1; ++j) {
            binary.push(1);
            binary.push(0);
            var sum = 6;
            while (--sum >= 0) {
                binary.push(binaryArray[i++]);
            }
        }
        return binary;
    };

    var __BASE64 = {
        /**
         *BASE64 Encode
         */
        encoder: function (str) {
            var base64_Index = [];
            var binaryArray = [];
            for (var i = 0, len = str.length; i < len; ++i) {
                var unicode = str.charCodeAt(i);
                var _tmpBinary = _toBinary(unicode);
                if (unicode < 0x80) {
                    var _tmpdiff = 8 - _tmpBinary.length;
                    while (--_tmpdiff >= 0) {
                        _tmpBinary.unshift(0);
                    }
                    binaryArray = binaryArray.concat(_tmpBinary);
                } else if (unicode >= 0x80 && unicode <= 0x7FF) {
                    binaryArray = binaryArray.concat(_toUTF8Binary(2, _tmpBinary));
                } else if (unicode >= 0x800 && unicode <= 0xFFFF) { //UTF-8 3byte
                    binaryArray = binaryArray.concat(_toUTF8Binary(3, _tmpBinary));
                } else if (unicode >= 0x10000 && unicode <= 0x1FFFFF) { //UTF-8 4byte
                    binaryArray = binaryArray.concat(_toUTF8Binary(4, _tmpBinary));
                } else if (unicode >= 0x200000 && unicode <= 0x3FFFFFF) { //UTF-8 5byte
                    binaryArray = binaryArray.concat(_toUTF8Binary(5, _tmpBinary));
                } else if (unicode >= 4000000 && unicode <= 0x7FFFFFFF) { //UTF-8 6byte
                    binaryArray = binaryArray.concat(_toUTF8Binary(6, _tmpBinary));
                }
            }

            var extra_Zero_Count = 0;
            for (var i = 0, len = binaryArray.length; i < len; i += 6) {
                var diff = (i + 6) - len;
                if (diff == 2) {
                    extra_Zero_Count = 2;
                } else if (diff == 4) {
                    extra_Zero_Count = 4;
                }
                //if(extra_Zero_Count > 0){
                //	len += extra_Zero_Count+1;
                //}
                var _tmpExtra_Zero_Count = extra_Zero_Count;
                while (--_tmpExtra_Zero_Count >= 0) {
                    binaryArray.push(0);
                }
                base64_Index.push(_toDecimal(binaryArray.slice(i, i + 6)));
            }

            var base64 = '';
            for (var i = 0, len = base64_Index.length; i < len; ++i) {
                base64 += BASE64_MAPPING[base64_Index[i]];
            }

            for (var i = 0, len = extra_Zero_Count / 2; i < len; ++i) {
                base64 += '=';
            }
            return base64;
        },
        /**
         *BASE64  Decode for UTF-8
         */
        decoder: function (_base64Str) {
            var _len = _base64Str.length;
            var extra_Zero_Count = 0;
            /**
             *计算在进行BASE64编码的时候，补了几个0
             */
            if (_base64Str.charAt(_len - 1) == '=') {
                //alert(_base64Str.charAt(_len-1));
                //alert(_base64Str.charAt(_len-2));
                if (_base64Str.charAt(_len - 2) == '=') { //两个等号说明补了4个0
                    extra_Zero_Count = 4;
                    _base64Str = _base64Str.substring(0, _len - 2);
                } else { //一个等号说明补了2个0
                    extra_Zero_Count = 2;
                    _base64Str = _base64Str.substring(0, _len - 1);
                }
            }

            var binaryArray = [];
            for (var i = 0, len = _base64Str.length; i < len; ++i) {
                var c = _base64Str.charAt(i);
                for (var j = 0, size = BASE64_MAPPING.length; j < size; ++j) {
                    if (c == BASE64_MAPPING[j]) {
                        var _tmp = _toBinary(j);
                        /*不足6位的补0*/
                        var _tmpLen = _tmp.length;
                        if (6 - _tmpLen > 0) {
                            for (var k = 6 - _tmpLen; k > 0; --k) {
                                _tmp.unshift(0);
                            }
                        }
                        binaryArray = binaryArray.concat(_tmp);
                        break;
                    }
                }
            }

            if (extra_Zero_Count > 0) {
                binaryArray = binaryArray.slice(0, binaryArray.length - extra_Zero_Count);
            }

            var unicode = [];
            var unicodeBinary = [];
            for (var i = 0, len = binaryArray.length; i < len;) {
                if (binaryArray[i] == 0) {
                    unicode = unicode.concat(_toDecimal(binaryArray.slice(i, i + 8)));
                    i += 8;
                } else {
                    var sum = 0;
                    while (i < len) {
                        if (binaryArray[i] == 1) {
                            ++sum;
                        } else {
                            break;
                        }
                        ++i;
                    }
                    unicodeBinary = unicodeBinary.concat(binaryArray.slice(i + 1, i + 8 - sum));
                    i += 8 - sum;
                    while (sum > 1) {
                        unicodeBinary = unicodeBinary.concat(binaryArray.slice(i + 2, i + 8));
                        i += 8;
                        --sum;
                    }
                    unicode = unicode.concat(_toDecimal(unicodeBinary));
                    unicodeBinary = [];
                }
            }
            return unicode;
        }
    };

    function urlsafe_b64encode(input) {
        return window.base64.encoder(input).replace(/\+/g, '-').replace('/', '_').replace(/=/g, '');
    }
    window.urlsafe_b64encode = urlsafe_b64encode;
    window.base64 = __BASE64;
})(window, document);
//bridge
;
(function (global, doc) {
    var pub = {
        readyStr: "CmrJSBridgeReady", //ready的事件名
        initStr: "CmrJSBridgeInit", //init的事件名
        schema: "cmread://", //默认协议
        /*此处涉及gulp替换两个地址，请勿改动如下两个地址*/
        oldIOS:"//wap.cmread.com/rbc/t/content/repository/ues/js/s109/b2cIos.js?t=20171114143040",//bridge老版地址
        oldAnd:"//wap.cmread.com/rbc/t/content/repository/ues/js/s109/b2cAndroid.js?t=20171114143040",
        /**
        @ method  _createElement | 创建一个iframe并传递地址
        @ param options | {Object} 根据options自动拼接url参数
            -method {String} 协议方法名如call/notifyResultToast
            -jsonData {Object} 传递的jsonData数据，需要对此参数进行单独加密
            -retMethod {String} 客户端接受消息后会执行的js方法，此方法需前端定义
            -cleanType {String} listen和broadcast时的清除功能。
        **/
        _createElement: function (options) {
            var src = pub.schema + options.method + "?";
            for (var i in options) {
                if (i != 'method') {
                    if (i == 'jsonData') {
                        var data = JSON.stringify(options.jsonData);
                        var json = urlsafe_b64encode(data);
                        src += "jsonData=" + json + "&";
                    } else {
                        options[i] ? src += i + "=" + options[i] + "&" : null;
                    }

                }
            }
            src = src.substring(0, src.length - 1);
            console.log(JSON.stringify(options) + "\n" + src);
            var iframe = document.createElement('iframe');
            iframe.style.display = 'none';
            iframe.src = src;
            iframe.onload = function () {
                setTimeout(function () {
                    iframe.remove();
                }, 0);
            };
            doc.getElementsByTagName('body')[0].appendChild(iframe);
            setTimeout(function(){
                iframe.remove();
            },1000);
        },
        /**
        @ method _invoke | 执行方法（_invoke）
        @ param  method {String} 协议方法名如notifyResultToast
        @ param data | {Object} 传递的jsonData数据
        **/
        _invoke: function (method, data) {
            this._createElement({
                method: "call/" + method,
                jsonData: data
            });
        },
        /**
        @ method _fetch  执行fetch方法（_fetch）
        @ param method {String} 协议方法名如getClientValue
        @ param data  {Object} 传递的jsonData数据,有回调
        @ param retMethod {String} 执行的回调方法
        **/
        _fetch: function (method, data) {
            this._createElement({
                method: "fetch/" + method,
                jsonData: data,
                retMethod: data.retMethod
            });
        },
        /**
        @ method _listen  执行fetch方法（_createElement）
        @ param key {String} 存储的键值
        @ param data  {Object} 传递的jsonData数据,有回调
        @ param cleanType {String} 清理数据机制
        **/
        _listen: function (key, data, cleanType) {
            this._createElement({
                method: "listen/channel",
                jsonData: data,
                key: key,
                cleanType: cleanType || ""
            });
        },
        /**
        @ method _listen  执行fetch方法（_createElement）
        @ param key {String} 存储的键值
        @ param data  {Object} 传递的jsonData数据,有回调
        @ param cleanType {String} 清理数据机制
        **/
        _broadcast: function (key, retMethod, cleanType) {
            this._createElement({
                method: "broadcast/channel",
                retMethod: retMethod,
                key: key,
                cleanType: cleanType || ""
            });
        }
    };
    var bridge = global.CmreadJsBridge || {
        isReady: false,
        version: "0.0.1",
        ua:navigator.userAgent.toLocaleLowerCase(),
        /**
        @ method  GetSys | 判断客户端为IOS还是安卓，返回"ios"或者"android"
        **/
        GetSys:function(){
            var sys="";
            /android|adr/.test(this.ua)>=0?sys="android":null;
            /iphone|ipad|ipod/.test(this.ua)?sys="ios":null;
            return sys;
        },
        /*
        @ method  GetAppVersion | 判断客户端版本号，获取到为0或者-1时代表获取版本号失败（一般是不在客户端内）
        */
        GetAppVersion:function(){
            if(this.ua.indexOf("_v")<0){
                return "0";
            }else{
                try{
                    return this.ua.split("_v")[1].split("(")[0];
                }catch(e){
                    return "-1";
                }
            }
        },
        isNewApp:function(){
            return true;
        },
        loadScript:function(src,callback){
            var scr=document.createElement('script');
            var timeStamp="";
            if(src.indexOf('?t=')<0){
                timeStamp="?t="+new Date().getTime();
            }
            scr.src=src+timeStamp;
            document.getElementsByTagName("head")[0].appendChild(scr);
            scr.onload=callback;
        },
        invoke: function (method, data) {
            pub._invoke(method, data);
        },
        fetch: function (method, data) {
            pub._fetch(method, data);
        },
        listen: function (key, data, cleanType) {
            pub._listen(key, data, cleanType);
        },
        broadcast: function (key, retMethod, cleanType) {
            pub._broadcast(key, retMethod, cleanType);
        },
        /**
        @ method  adaptation | 初始化判断客户端并兼容
        **/
        adaptation:function(){
            //alert(this.isNewApp()+""+this.ua);
            if(this.isNewApp()!=true){
                var self=this;
                switch (this.system){
                    case "android":
                        //console.log("这是安卓版本，此处build和dev后会删除毁掉方法里异步加载");
                        this.loadScript(pub.oldAnd,function(){
                            
                        });
                        break;
                    case "ios":
                        //console.log("这是IOS版本");
                        this.loadScript(pub.oldIOS,function(){
                            
                        });
                        break;
                }
            }
        },
        /**
        @ method  init | 初始化bridge
        **/
        init: function () {
            var self=this;
            this.appVersion=this.GetAppVersion();
            this.appVersionNum=this.appVersion.replace(/\./g,"");
            this.system=this.GetSys();
            this.adaptation();//兼容新老版本的b2c
            var _initEvent = document.createEvent("Event");
            _initEvent.initEvent(pub.initStr);
            _initEvent.bridge = this;
            doc.dispatchEvent(_initEvent);
        },
        ready: function () {
            this.isReady = true;
            var _readyEvent = document.createEvent('Event');
            _readyEvent.initEvent(pub.readyStr);
            _readyEvent.bridge = bridge;
            doc.dispatchEvent(_readyEvent);
        }
    };
    doc.addEventListener(pub.readyStr, function (e) {
        /*console.log("bridge ready");*/
    });
    doc.addEventListener(pub.initStr, function (e) {
        /*console.log("bridge init");*/
    });
    bridge.ready();
    //全局暴露
    global.CmreadJsBridge = bridge;

})(window, document);

/*
*****************************CmreadJsSdk*********************************
* @version:v0.0.1
* @retMethod相关约定：前端定义此方法，客户端在相关操作时调用此方法，并给出回调参数如：
    {
      "code":0,
      "message":null,
      "result":object
    }
    a. 客户端定义code的类型和意义
    b. 如果 执行失败，result 为null
    c. result永远为对象，如果结果是bool，number等，也需转成对象
**************************************************************************
*/
;
(function(global, doc) {
	global.b2cFun = global.b2cFun || {};
	var timeStamp = new Date().getTime();
	var sdk = {
		/*
		 * @method isNeed
		 * @desc 检查必填参数是否为空
		 * @*param contentArr {Object} 必填参数的数组
		 * @*param json {Object} 整个参数对象
		 * @return 返回true获取false
		 */
		isNeed: function(contentArr, json) {
			var pass = true,
				newArr = [];
			contentArr.forEach(function(v, k) {
				if (json[v] == null || json[v] == undefined || json[v] === '') {
					newArr.push(v);
					pass = false;
				}
			});
			if (pass == false) {
				alert(newArr.join(',') + "参数不能为空");
			}
			return pass;
		},
		/*
		 * @method 拼接完整路径 IOS旧版b2c方法中提取出来的方法 【仅供旧版使用】
		 * @param URL
		 */
		populateUrl: function(URL) {
			if (URL && URL.indexOf("http") != 0) {
				return document.location.protocol + '//' + document.location.host + URL;
			} else {
				return URL;
			}
		},
		/*
		 * @method ios_bCallC ios通用JS方法  (ios旧版b2c方法通用方法)【仅供旧版使用】
		 */
		ios_bCallC: function(action, json, token) {
			var url = 'cmread-objc://' + action + '.' + token + '#' + json;
			//window.location ="http://www.baidu.com";
			window.location = url;
		},
		/*
		 * @method extend
		 * @desc 将两个对象合并
		 * @*param o {Object} 老对象
		 * @*param n {Object} 新对象
		 * @return 返回合并后的对象
		 */
		extend: function(o, n) {
			for (var p in n) {
				if (n.hasOwnProperty(p) && (!o.hasOwnProperty(p)))
					o[p] = n[p];
			}
			return o;
		},
		/*下面开始自定义方法*/
		/*
		 * @method toast
		 * @desc toast提醒
		 * @*param msg {String} toast文案
		 * @param isClose {Boolean} 弹出Toast后是否关闭当前页
		 */
		toast: function(msg, isClose) {
			if (!msg) {
				console.warn('toast:提示文案必填');
				return;
			}
			/*if(CmreadJsBridge&&CmreadJsBridge.ua.indexOf('cmread')<0){
	            try{
	                coToast&&coToast.show(msg);
	            }catch(e){
					console.log({"toast":msg});
	            }
	            return;
	        }*/
			CmreadJsBridge.invoke("notifyResultToast", {
				responseInfo: msg,
				closeSelf: isClose
			});
		},
		/*
		 * @method switchLogin 切换客户端账号
		 * @param action {String||Function} 登录成功后进行的动作，如果是URL，则跳转，如果是”javascript:”开头，则会调用该页面提供的javascript方法
		 */
		switchLogin: function(action) {
			action = action || '';
			if (typeof action == 'function') {
				action = "javascript:(" + action.toString() + ")()";
			}
			CmreadJsBridge.invoke('switchAccountLogin', {
				successAction: action
			});
		},
		/*
		 * @method cleanChannel 清空listen存储在客户端的值
		 * @param *key 需要清空的键值
		 */
		cleanChannel: function(key) {
			CmreadJsBridge.listen(key, {}, 2);
		},
		/*
		 * @method getClientValue 使用fetch方法获取客户端相关信息
		 * @param key {String} 前端与客户端约定的key值
		 * @param retMethod {String} 前端与客户端约定的方法，前端定义此方法，客户端在fetch时调用此方法
		 * @param callback 回调执行的操作。会传递jsondata回来
		 */
		getClientValue: function(key, callback) {
			/*因此方法需要在页面进入时就调用，故兼容放在在此写入*/
			if (CmreadJsBridge.isNewApp() != true) {
				if (key == null) {
					alert('key参数是必填参数');
					return;
				}
				switch (CmreadJsBridge.system) {
					case "android":
						var action = 'getClientValue';
						var jsonStr = null;
						jsonStr = '{"key":"' + key + '"}';
						cmread.callBackClient(action, jsonStr, "");
						break;
					case "ios":
						var action = 'getClientValue';
						var myJSONObject = new Object();
						myJSONObject.key = key;
						var jsonStr = JSON.stringify(myJSONObject);
						this.ios_bCallC(action, jsonStr, "");
						break;
				}
			} else {
				/*新接口*/
				var retMethod = retMethod || 'fetch_' + timeStamp; //如果不指定就用时间戳
				callback = callback || global.setClientValue;
				global.b2cFun[retMethod] = callback;
				CmreadJsBridge.invoke('getClientValue', {
					key: key,
					retMethod: retMethod
				});
			}
		},
		/*
		 * @method pushChannel 使用listen方法在当前key下追加值
		 * @param key {String} broadcast时的唯一标识
		 * @param value {Object} 需要存储的数据
		 */
		pushChannel: function(key, value) {
			CmreadJsBridge.listen(key, value);
		},
		/*
		 * @method resetChannel 使用listen方法在当前key下清空并替换值
		 * @param key {String} broadcast时的唯一标识
		 * @param value {Object} 需要存储的数据
		 */
		resetChannel: function(key, value) {
			CmreadJsBridge.listen(key, value, '1');
		},
		/*
		 * @method getChannelOnce broadcast时清空并返回当前key下的值
		 * @param key {String} broadcast时的唯一标识
		 * @param callback
		 */
		getChannelOnce: function(key, callback) {
			var retMethod = 'broadcast_' + key;
			b2cFun[retMethod] = callback || function(jsonData) {};
			CmreadJsBridge.broadcast(key, retMethod, '1');
		},
		/*
		* @method getChannel 使用broadcast方法从客户端取出数据
		* @*param key {String} listen中存储的键值，根据此取出数据
		* @*param retMethod {String} 前端定义此方法，客户端执行此方法并将结果传至参数中
		* @param cleanType {String}
		    a.  0或者不传，什么都不做
		    b. 1，发送broadcast后，清空数据
		*/
		getChannel: function(key, callback) {
			var retMethod = 'broadcast_' + key;
			b2cFun[retMethod] = callback || function(jsonData) {};
			CmreadJsBridge.broadcast(key, retMethod, '0');
		},
		/*
		 * @method startReader 唤起客户端阅读页
		 * @*param contentID 内容id
		 * @*param chapterID 章节ID，contendType=6 电台，章节id同内容ID
		 * @*param contentType 内容类型, 1图书, 2漫画, 3杂志, 5听书6、电台,7、点播电台
		 * @*param offset 内容相对章首的偏移量（图书时为字数，漫画/杂志为页码，听书为时长单位为秒）
		 * @*param contentName 书名或电台名
		 * @*param bigLogo 封面图片url（请获取尺寸为180*240的图片）或电台logo的url
		 * @param recentlyTime 最近的阅读时间
		 * @param isCompare 是否需要根据时间戳进行比较
		            true : 需要与客户端数据库比较
		            false: 不需要与客户端数据库比较
		 * @*param chargeMode 收费类型（0：免费1：按本计费2：按章计费）
		 * @*param authorName 作家名
		 * @param chapterName 章节名，内容类型为听书时必填
		 * @param bookLevel 图书等级，1 免费带广告、2、3、4 10、ugc图书
		 */
		startReader: function(json) {
			var needArr = ['contentID', 'chapterID', 'contentType', 'contentName', 'bigLogo', 'chargeMode', 'offset']; //必填项
			if (this.isNeed(needArr, json) != true) {
				return; //非空判断
			}
			CmreadJsBridge.invoke('startCommonReader', json);
		},
		/*
		 * @method startCatalog 唤起目录页
		 * @*param contentID 内容id
		 * @*param contentType 内容类型（1图书，2漫画，5听书）
		 * @*param contentName 内容名称
		 * @*param chargeMode 收费类型（0：免费1：按本计费2：按章计费）
		 * @param bigLogo 封面图片url（请获取尺寸为180*240的图片）
		 * @param authorName 作家名
		 * @param speakerName 听书主播名，进入听书目录页时必选
		 * @param description 听书内容长简介，进入听书目录页时必选
		 * @param bookLevel 图书等级，1 免费带广告、2、3、4
		 ***booklevel为了兼容老IOS接口必传***
		 */
		startCatalog: function(json) {
			var needArr = ['contentID', 'contentType', 'contentName', 'chargeMode'];
			if (this.isNeed(needArr, json) != true) {
				return; //非空判断
			}
			CmreadJsBridge.invoke('startChapterList', json);
		},
		/*
		 * @method startPicReader 启动带缩略图的阅读页(IOS无此方法)
		 * @*param contentID 内容id
		 * @*param chapterID 章节ID
		 * @*param contentName 书名
		 * @*param bigLogo 封面图片url（请获取尺寸为180*240的图片）
		 * @*param count 缩略图张数
		 * @*param imageUrl  缩略图对应url（图片url之间使用：“;PicUrl:”做分割）
		 * @param offset 图片第几张
		 */
		startPicReader: function(json) {

			var needArr = ['contentID', 'chapterID', 'contentName', 'bigLogo', 'count', 'imageUrl'];
			if (this.isNeed(needArr, json) != true) {
				return; //非空判断
			}
			CmreadJsBridge.invoke('startPictureShortReader', json);
		},
		/*
		 * @method startTTSReader 以TTS朗读方式启动图书阅读页
		 * @*param contentID 内容id
		 * @*param chapterID 章节ID
		 * @param contentType 暂无处理
		 * @*param offset 内容相对章首的偏移量（字数）
		 * @*param contentName 书名
		 * @*param bigLogo 封面图片url（请获取尺寸为180*240的图片）
		 * @*param chargeMode 收费类型（0：免费1：按本计费2：按章计费）
		 * @param authorName 作者
		 * @param chapterName  章节名，内容类型为听书时必填
		 * @param description 为了兼容老版ios方法
		 * @param recentlyTime 为了兼容老版ios方法
		 * @param isCompare 为了兼容老版ios方法
		 contentID, chapterID, contentType, offset,
		    contentName, bigLogo, chargeMode, authorName, chapterName, description, recentlyTime, isCompare
		 */
		startTTSReader: function(json) {
			var needArr = ['contentID', 'chapterID', 'offset', 'contentName', 'bigLogo', 'chargeMode'];
			if (this.isNeed(needArr, json) != true) {
				return; //非空判断
			}
			CmreadJsBridge.invoke('startTTSReader', json);
		},
		/*
		 * @method authenticate 登录鉴权
		 * @*param action 登录成功后进行的动作，如果是URL，则跳转，如果是”function”则执行
		 */
		authenticate: function(action) {
			action = action || '';
			if (typeof action == 'function') {
				action = "javascript:(" + action.toString() + ")()";
			}
			CmreadJsBridge.invoke('authenticate', {
				successAction: action
			});
		},
		/*
		 * @method startDownload 下载(兼容老版安卓和ios)
		 * @param URL {string} 下载地址
		 * @*param contentID {String} 下载的内容id
		 * @*param contentType {Interger} 下载内容类型（1：图书2：漫画3：杂志 5：听书 7：手机报）
		 * @*param contentName {String} 内容名称
		 * @param chapterID {String} 下载章节ID （漫画，杂志，听书下载时必选，）
		 * @param chapterName {String} 下载章节名称 （漫画，杂志，听书下载时必选，）
		 * @*param chargeMode {Integer} 收费类型（0：免费1：按本计费2：按章计费）
		 * @*param bigLogo {String} 封面图片（下载时请获取尺寸为180*240的图片）
		 * @param authorName {String} 作家名
		 * @param speakerName {String} 听书主播名，听书下载时必选
		 * @param description {String} 听书内容长简介，听书下载时必选
		 * @param isSerial {String} contentType字段为”1”时，该字段有效是否为连载书：0连载书1非连载书(旧版ios必填)
		 * @*param canBatchDownload {Boolean} 是否允许批量下载连载未完本的分册内容
		 * @*param jsMethodName {function} 客户端回调用此方法
		 * @*param bookLevel {String} 图书等级，1 免费带广告、2、3、4
		 * @*param isPrePackFinished {String} isSerial字段为”0”时，该字段有效：连载书是否预打包完成：1已打包0 未打包 旧版IOS必填。
		 URL, contentID, contentType, contentName, chapterID, chapterName, chargeMode, bigLogo, authorName, speakerName, description, isSerial, canBatchDownload, jsMethodName, bookLevel, isPrePackFinished
		 *
		 */
		startDownload: function(json) {
			var needArr = ['contentID', 'contentType', 'contentName', 'chargeMode', 'chargeMode', 'bigLogo', 'canBatchDownload', 'bookLevel']
			if (this.isNeed(needArr, json) != true) {
				return; //非空判断
			}
			var funname = 'download' + new Date().getTime();
			b2cFun[funname] = json.jsMethodName;
			json.jsMethodName = "b2cFun['" + funname + "']";
			var action = "download";
			if (CmreadJsBridge.system == "ios") {
				action = "downloadContent";
			}
			CmreadJsBridge.invoke(action, json);
		},
		/*
		 * @method refresh 刷新
		 */
		refresh: function() {
			CmreadJsBridge.invoke('viewRefresh', {});
		},
		/*
		 * @method goBack 后退
		 */
		goBack: function() {
			CmreadJsBridge.invoke('goBack', {});
		},
		/*
		 * @method addshelf 加入书架
		 * @*param contentType 要加入书架的书的类型：1、图书 2、漫画 3、杂志 5、听书
		 * @*param contentID 要加入书架的书的contentid
		 * @*param contentName 要加入书架的书的第一章的ID
		 * @*param chapterID 要加入书架的书的书名
		 * @param chapterName 要加入书架的书的第一章的名字
		 * @*param bigLogo 要加入书架的书的封面URL
		 * @param isSerial contentType字段为”1”时，该字段有效 是否为连载书：0连载书 1非连载书
		 * @param isPrePackFinished isSerial字段为”0”时，该字段有效：| 连载书是否预打包完成：1已打包 0 未打包
		 * @param bookLevel 要加入书架图书的等级：1 看广告免费 2 会员级别免费 3 会员级别免费 4 所有用户都需要点播付费其他(如传空或其他值) 客户端不作处理，非书籍类型，前端传参为空，客户端默认不处理
		 * @*param c_downloadAttribute 0：不可下载 1：连载书下载 2：完本书下载
		 * @*param chargeMode 收费类型（0：免费1：按本计费2：按章计费）
		 * @param authorName 作家名
		 * @*param jsMethodName {Function} 加入书架成功后的回调
		 * @param description 书籍简介
		 */
		addShelf: function(json) {
			var needArr = ['contentType', 'contentID', 'contentName', 'chapterID', 'bigLogo', 'chargeMode'];
			if (this.isNeed(needArr, json) != true) {
				return; //非空判断
			}
			var oname = 'addShelf_' + new Date().getTime();
			b2cFun[oname] = json.jsMethodName;
			json.jsMethodName = 'b2cFun[\"' + oname + '\"]';
			CmreadJsBridge.invoke('addToBookshelf', json);
		},
		/*
		 * @method batchDownload 批量下载图书分册(IOS没有该方法)
		 * @*param contentID 内容名称
		 * @*param contentName 下载章节ID（漫画，杂志，听书下载时必选，）
		 * @*param chargeMode {Number} 收费类型（0：免费1：按本计费2：按章计费）
		 * @*param bigLogo 封面图片（下载时请获取尺寸为180*240的图片）
		 * @param authorName 作家名
		 * @param isSerial contentType字段为”1”时，该字段有效
		 * @param isPrePackFinished 暂未处理
		 contentID, contentName, chargeMode, bigLogo, authorName, isSerial, isPrePackFinished
		 */
		batchDownload: function(json) {
			var needArr = ['contentID', 'contentName', 'chargeMode', 'bigLogo'];
			if (this.isNeed(needArr, json) != true) {
				return; //非空判断
			}
			CmreadJsBridge.invoke('batchDownloadFascicle', json);
		},
		/*
		 * @method startExPage 打开外站链接
		 * @*param url 内容名称
		 * @param needCache true：加载缓存 false：不加载缓存
		 * @param pullRefresh 禁止下拉刷新 0：支持下拉刷新 1：不支持下拉刷新
		 url, needCache, pullRefresh（IOS）
		 */
		startExPage: function(json) {
			var needArr = ['url'];
			if (this.isNeed(needArr, json) != true) {
				return; //非空判断
			}
			json.url=this.populateUrl(json.url);
			if (CmreadJsBridge.system == "android") {
				json.URL = json.url;
				delete json.url;
			}
			CmreadJsBridge.invoke('startExPage', json || {});
		},
		/*
		 * @method share 分享简单版
		 */
		share: function(json) {
			var needArr = ['title', 'URL', 'bigLogo', 'description', 'type'];
			if (this.isNeed(needArr, json) != true) {
				return; //非空判断
			}
			if (json.jsMethodName) {
				b2cFun['call_share'] == json.jsMethodName;
			}
			CmreadJsBridge.invoke('shareContent', json || {});
		},
		/*
		 * @method shareEx 分享强化版
		 */
		shareEx: function(json) {
			var needArr = ['contentType'];
			if (this.isNeed(needArr, json) != true) {
				return; //非空判断
			}
			if (json.detailType != null) {

			} else if (json.shareType == 4) {
				alert('shareType为4时，detailType是必填参数')
				return;
			}
			if (json.description != null) {} else if (json.shareType != 2) {
				alert('shareType不为2时，description是必填参数')
				return;
			}
			if (json.contentType == 10 && json.URL == null) {
				alert('contentType为10时，URL是必填参数')
				return;
			}
			if (json.contentType == 8 && (json.code == null || json.contentId == null)) {
				if (json.code == null) {
					alert('contentType为8时，code是必填参数')
				} else {
					alert('contentType为8时，contentId是单机包id，是必填参数')
				}

				return;
			}

			if (json.contentType == 9 && json.code == null) {
				alert('contentType为9时，code是必填参数')
				return;
			}
			if (json.contentType == 13 && json.imgUrl == null) {
				alert('contentType为13时，imgUrl是必填参数')
				return;
			}
			CmreadJsBridge.invoke('shareContentEx', json || {});
		},
		/*
		 * @method logout 注销
		 */
		logout: function(json) {
			CmreadJsBridge.invoke('logout', json || {});
		},
		/*
		 * @method startIEForDownload 页面应用防火墙
		 * @param url 需要跳转的页面地址
		 */
		startIEForDownload: function(json) {
			var needArr = ['url'];
			if (this.isNeed(needArr, json) != true) {
				return; //非空判断
			}
			json.url=this.populateUrl(json.url);
			json.URL = json.url; //安卓的url是大写的，两个url都传
			CmreadJsBridge.invoke('startIEForDownload', json || {});
		},
		/*
		 * @method startBindPaymentNumber 启动绑定页，IOS无
		 */
		startBindPaymentNumber: function(json) {
			CmreadJsBridge.invoke('startBindPaymentNumber', json || {});
		},
		/*
		 * @method startBindPaymentNumber 解除支付绑定，IOS无
		 */
		startUnbindPaymentNumber: function(json) {
			CmreadJsBridge.invoke('startUnbindPaymentNumber', json || {});
		},
		/*
		 *startChangePassword 启动修改密码页面
		 */
		startChangePassword: function(json) {
			CmreadJsBridge.invoke('startChangePassword', json || {});
		},
		/*
		 *startSetSecurityQuestion 启动设置密保页面
		 */
		startSetSecurityQuestion: function(json) {
			CmreadJsBridge.invoke('startSetSecurityQuestion', json || {});
		},
		/*startUserNotes 安卓启动个人笔记*/
		startUserNotes: function(json) {
			CmreadJsBridge.invoke('startUserNotes', json || {});
		},
		/*startSimplePage 启动二级页面（IOS无） 旧版不需要参数*/
		startSimplePage: function(json) {
			var needArr = ['url'];
			if (this.isNeed(needArr, json) != true) {
				return; //非空判断
			}
			json.url=this.populateUrl(json.url);
			json.URL = json.url;
			delete json.url;
			CmreadJsBridge.invoke('startSimplePage', json || {});
		},
		/*
		 * @method startVoiceSearch 启动语音搜索
		 * @param url 搜索结果页的URL
		 */
		/*startVoiceSearch:function(){
		    this.toast('新版方法未集成');
		},*/
		/*
		 * @method startRechargePage 启动充值页
		 * @param url 带fsrc参数的充值页URL
		 */
		startRecharge: function(json) {
			json = json || {};
			var method = "startRechargePage";
			switch (CmreadJsBridge.system) {
				case "android":
					var needArr = ['url'];
					if (this.isNeed(needArr, json) != true) {
						return; //非空判断
					}
					json.url=this.populateUrl(json.url);
					json.URL = json.url;
					delete json.url;
					break;
				case "ios":
					method = "clientRecharge";
					break;
			}
			CmreadJsBridge.invoke(method, json);
		},
		/*
		 * @method closeRechargePage 关闭充值页
		 * @param fsrc 个人中心传--0，包月详情页传--2
		 */
		closeRecharge: function(json) {
			var needArr = ['fsrc'];
			if (this.isNeed(needArr, json) != true) {
				return; //非空判断
			}
			CmreadJsBridge.invoke('closeRechargePage', json || {});
		},
		/*
		 * @method startShake 启动摇一摇
		 * @*param dURL 活动详情页url
		 * @*param rURL 摇一摇跳转到摇一摇结果页
		 * @*param pURL 摇一摇背景图
		 * @*param sURL 手机晃动图片
		 */
		startShake: function(json) {
			var needArr = ['dURL', 'rURL', 'pURL', 'sURL'];
			if (this.isNeed(needArr, json) != true) {
				return; //非空判断
			}
			CmreadJsBridge.invoke('startShakepage', json || {});
		},
		/*
		 * @method closeShakeResultDialog 闭摇一摇结果框（IOS无）
		 */
		closeShakeResultDialog: function(json) {
			CmreadJsBridge.invoke('closeShakeResultDialog', json || {});
		},
		/*
		 * @method startShakeResultDetailPage 启动摇一摇结果详情页（IOS无)
		 * @*param URL 需要访问的页面链接
		 */
		startShakeResultDetailPage: function(json) {
			CmreadJsBridge.invoke('startShakeResultDetailPage', json || {});
		},
		/*
		 * @method BackToShelf 回到书架（IOS无)
		 */
		BackToShelf: function(json) {
			this.toast('方法已删除');
		},
		/*
		 * @method shareApp 分享客户端（IOS无)
		 * @*param shareType 分享类型
		 * @param detailType 1微信好友 2微信朋友圈 仅sharetype=4时有效
		 * @param title 分享标题
		 * @param description 分享描述
		 * @param bigLogo 分享logo
		 * @param URL 分享URL
		 * @param extend 可选参数，格式以类似p1=v1&p2=v2格式表示，其中的序号最大支持扩展到9
		 */
		shareApp: function(json) {
			json = json || {};
			var needArr = ['shareType'];
			if (this.isNeed(needArr, json) != true) {
				return; //非空判断
			}
			if (json.detailType != null) {} else if (json.shareType == 4) {
				alert('shareType为4时，detailType是必填参数')
				return;
			}
			CmreadJsBridge.invoke('shareApp', json || {});
		},
		/*
		 * @param startSMSReceiver启动三网融合充值二次确认页短信拦截
		 * @*param callingNum 拦截的主叫号码
		 * @*param calledNum 拦截的被叫号码
		 * @*param featureStr 拦截特征字符串
		 */
		startSMSReceiver: function(json) {
			json = json || {};
			var needArr = ['callingNum', 'calledNum', 'featureStr'];
			if (this.isNeed(needArr, json) != true) {
				return; //非空判断
			}
			CmreadJsBridge.invoke('startSMSReceiver', json);
		},
		/*
		 * @param EnCliAndDown
		 * @*param forwardUrl
		 * @*param bookDownUrlStr
		 * @*param BookIDStr
		 */
		EnCliAndDown: function(json) {
			json = json || {};
			var needArr = ['forwardUrl', 'bookDownUrlStr', 'BookIDStr'];
			if (this.isNeed(needArr, json) != true) {
				return; //非空判断
			}
			CmreadJsBridge.invoke('EnCliAndDown', json);
		},
		/*
		 * @method sendSMSForUNICOMChargeAck	客户端增加联通充值的发短信方法
		 * @*param receiver 接收短信的号码
		 * @*param content 短信内容
		 */
		sendSMSForUNICOMChargeAck: function(json) {
			json = json || {};
			var needArr = ['receiver', 'content'];
			if (this.isNeed(needArr, json) != true) {
				return; //非空判断
			}
			CmreadJsBridge.invoke('sendSMSForUNICOMChargeAck', json);
		},
		/*
		 * @method payToAlipay	客户端传送给支付宝SDK方法（IOS无）
		 * @param subject 商品名称
		 * @param total_fee 金额
		 * @param body 商品详情
		 * @param notify_url 自动跳转商户地址
		 * @param result_url 结果页地址
		 * @param orderId  订单号
		 */
		payToAlipay: function(json) {
			json = json || {};
			var needArr = ['subject', 'total_fee', 'body', 'result_url', 'orderId'];
			if (this.isNeed(needArr, json) != true) {
				return; //非空判断
			}
			CmreadJsBridge.invoke('payToAlipay', json);
		},
		/*
		 * @method getAbstractParams 摘要页滑动阅读页
		 * @*param contentID 内容ID
		 * @*param chapterID 章节ID
		 * @*param contentType 内容类型 , 1图书, 2漫画, 3杂志, 5听书6、电台
		 * @param offset 内容相对章首的偏移量（图书时为字数，漫画/杂志为页码，听书为时长单位为秒)
		 * @*param contentName 书名或电台名
		 * @*param bigLogo 封面图片url（请获取尺寸为180*240的图片）或电台logo的url
		 * @param recentlyTime 最近的阅读时间
		 * @param isCompare 是否需要根据时间戳进行比较 true | false
		 * @param authorName 作家名
		 */
		getAbstractParams: function(json) {
			json = json || {};
			var needArr = ['contentID', 'chapterID', 'contentType', 'offset', 'contentName', 'bigLogo'];
			if (this.isNeed(needArr, json) != true) {
				return; //非空判断
			}
			switch (CmreadJsBridge.system) {
				case "android":
					CmreadJsBridge.invoke('getAbstractParams', json);
					break;
				case "ios":
					CmreadJsBridge.invoke('pushDataWhenLoaded', json);
					break;
			}
		},
		/*
		 * @method refreshPersonal	刷新个人中心昵称
		 */
		refreshPersonal: function(json) {
			json = json || {};
			CmreadJsBridge.invoke('refreshPersonal', json);
		},
		/*
		 * @method startCommonMainPage 启动不一样的精彩页面
		 * @*param channelTag 内容类型:2漫画,5听书 6书单
		 * @param url 需要访问的页面链接（IOS必填）
		 */
		startCommonMainPage: function(json) {
			json = json || {};
			var needArr = ['channelTag'];
			if (this.isNeed(needArr, json) != true) {
				return; //非空判断
			}
			CmreadJsBridge.invoke('startCommonMainPage', json);
		},
		/*
		 * @method startMoreWonderfulPage 启动不一样的精彩页面(IOS无)
		 * @*param channelTag 内容类型:2漫画,5听书 6书单
		 * @param url 需要访问的页面链接（IOS必填）
		 */
		startMoreWonderfulPage: function(json) {
			json = json || {};
			CmreadJsBridge.invoke('startMoreWonderfulPage', json);
		},
		/*
		 * @method startStealBookPage 启动偷书页面（IOS无）
		 */
		startStealBookPage:function(json){
 			json = json || {};
 			CmreadJsBridge.invoke('startStealBookPage', json);
 		},
		/*
		 * @method startOfferWall 个人页面中启动客户端支付绑定页面（IOS无）
		 */
		startOfferWall: function(json) {
			json = json || {};
			CmreadJsBridge.invoke('startOfferWall', json);
		},
		/*
		 * @method payToWX  启动微信支付（IOS无）
		 * @*param appid 开放平台账号
		 * @*param noncestr 随机字符串
		 * @*param partnerid 注册时分配的财付通账号
		 * @*param prepayid 预支付订单号
		 * @*param timestamp 时间戳
		 * @*param sign  签名
		 * @*param result_url  回调地址
		 */
		payToWX: function(json) {
			json = json || {};
			var needArr = ['appid', 'noncestr', 'partnerid', 'prepayid', 'timestamp', 'sign', 'result_url'];
			if (this.isNeed(needArr, json) != true) {
				return; //非空判断
			}
			CmreadJsBridge.invoke('payToWX', json);
		},
		/*
		 * @method startBindAccount 我、设置、资费等页面 启动随机用户绑定手机号码页面
		 */
		startBindAccount: function(json) {
			json = json || {};
			CmreadJsBridge.invoke('startBindAccount', json);
		},
		/*
		 * @method startEventWebPage  配合前端页面，优化特殊的webview样式(IOS无)
		 * @param jumpURL 跳转地址
		 */
		startEventWebPage: function(json) {
			json = json || {};
			var needArr = ['URL'];
			if (this.isNeed(needArr, json) != true) {
				return; //非空判断
			}
			json.URL=this.populateUrl(json.URL);
			CmreadJsBridge.invoke('startEventWebPage', json);
		},
		/*
		 * @method sendSMSForChargeAck  发送三网融合确认短信   （IOS无)
		 * @*param receiver 接受短信的号码
		 * @*param content 短信内容
		 * @*param operator 运营商标志 cmcc、ctcc、cucc（联通）
		 * @*param orderId 订单号，用于关联统计的唯一标识
		 */
		sendSMSForChargeAck: function(json) {
			json = json || {};
			var needArr = ['receiver', 'content'];
			if (this.isNeed(needArr, json) != true) {
				return; //非空判断
			}
			CmreadJsBridge.invoke('sendSMSForChargeAck', json);
		},
		/*
		* @method notifyDownload  在咪咕星页面,单机包下载管理  （IOS无)
		* @*param state 0：开始下载1：删除正在下载的单机包或删;已下载的单机包2：暂停下载3：继续下载

		* @*param productID 单机包id
		* @param url 单机包地址
		* @*param packageName 单机包包名，开始下载和继续下载时必传，用来检查本地单机包安装状态
		*/
		notifyDownload: function(json) {
			json = json || {};
			var needArr = ['state', 'productID'];
			if (this.isNeed(needArr, json) != true) {
				return; //非空判断
			}
			if (((json.state.indexOf("0") == 0) || (json.state.indexOf("3") == 0)) && json.url == null) {
				alert('当启动或继续下载时需要传url');
				return;
			}
			if (((json.state.indexOf("0") == 0) || (json.state.indexOf("3") == 0)) && json.packageName == null) {
				alert('当启动或继续下载时需要传packageName');
				return;
			}
			CmreadJsBridge.invoke('notifyDownload', json);
		},
		/*
		 * @method sendDonwloadList  2.4.17 在咪咕星页面,通知客户端需下载单机包列表（IOS无）
		 * @*param downloadListJson
		 */
		sendDonwloadList: function(json) {
			json = json || {};
			CmreadJsBridge.invoke('sendDonwloadList', json);
		},
		/*
		 * @method notifyFcode  通知F码（IOS无）
		 * @*param Fcode F码
		 * @*param contentId productID:单机包id
		 */
		notifyFcode: function(json) {
			json = json || {};
			var needArr = ['Fcode', 'contentId'];
			if (this.isNeed(needArr, json) != true) {
				return; //非空判断
			}
			CmreadJsBridge.invoke('notifyFcode', json);
		},
		/*
		 * @method closeExPage  关闭充值/悦读中国等外部页面并刷新之前的页面（IOS无）
		 */
		closeExPage: function(json) {
			json = json || {};
			CmreadJsBridge.invoke('closeExPage', json);
		},
		/*
		 * @method getCatalogPayResult  通知包月订购状态（IOS无）
		 * @*param resultCode 0：订购成功 -1：订购失败
		 */
		getCatalogPayResult: function(json) {
			json = json || {};
			var needArr = ['resultCode'];
			if (this.isNeed(needArr, json) != true) {
				return; //非空判断
			}
			CmreadJsBridge.invoke('getCatalogPayResult', json);
		},
		/*
		 * @method startBindAlipay 向客户端唤起签约支付宝钱包功能（IOS无）
		 */
		startBindAlipay: function(json) {
			json = json || {};
			CmreadJsBridge.invoke('startBindAlipay', json);
		},
		/*
		 * @method getBindNumberState 非移用户获取绑定支付号码状态（IOS无）
		 * @*param uid 用户id
		 */
		getBindNumberState: function(json) {
			json = json || {};
			var needArr = ['uid'];
			if (this.isNeed(needArr, json) != true) {
				return; //非空判断
			}
			CmreadJsBridge.invoke('getBindNumberState', json);
		},
		/*
		 * @method c_sendApkDownloadList 在应用墙,通知客户端需下载单机包列表（IOS无）
		 * @*param c_downloadListJson 请参考老版调用方法
		 */
		c_sendApkDownloadList: function(json) {
			json = json || {};
			CmreadJsBridge.invoke('c_sendApkDownloadList', json);
		},
		/*
		 * @method c_notifyApkDownload 在应用墙,单机包下载管理（IOS无）
		 * @*param c_state0：开始下载1：删除正在下载的应用或删除已下载的应用2：暂停下载3：继续下载4：安装客户端5：打开客户端6:升级客户端
		 * @param c_url 应用地址
		 * @*param c_packageName 应用包名
		 */
		c_notifyApkDownload: function(json) {
			json = json || {};
			var needArr = ['c_state', 'c_packageName'];
			if (this.isNeed(needArr, json) != true) {
				return; //非空判断
			}
			CmreadJsBridge.invoke('c_notifyApkDownload', json);
		},
		/*
		 * @method relateAccountSuccess 登录成功后进行的动作,二级账号成功关联的手机号码（IOS无）
		 * @param successAction 成功后执行的js方法名？？？
		 * @*param relatedMobile 关联的手机号 请参考旧版
		 */
		relateAccountSuccess: function(json) {
			json = json || {};
			var needArr = ['relatedMobile', 'c_packageName'];
			if (this.isNeed(needArr, json) != true) {
				return; //非空判断
			}
			CmreadJsBridge.invoke('relateAccountSuccess', json);
		},
		/*
		 * @method startMobileRelateAccount 启动账号关联页面
		 * @*param sceneId 场景id
		 * @param successAction 关联成功后的进入地址[可空]
		 */
		startMobileRelateAccount: function(json) {
			json = json || {};
			var needArr = ['sceneId'];
			if (this.isNeed(needArr, json) != true) {
				return; //非空判断
			}
			CmreadJsBridge.invoke('startMobileRelateAccount', json);
		},
		/*
		 * @method c_queryWeChatAppId 启动查询微信appid是否支持微信微支付（IOS无）
		 * @param callback 获取成功后客户端的回调
		 */
		c_queryWeChatAppId: function(json) {
			json = json || {};
			/*因此方法需要在页面进入时就调用，故兼容放在在此写入*/
			if (CmreadJsBridge.isNewApp() != true) {
				var action = 'c_queryWeChatAppId';
				var jsonStr = '{}';
				try {
					cmread.callBackClient(action, jsonStr, json.token || "");
				} catch (e) {
					console.log(e.message);
				}

			} else {
				CmreadJsBridge.invoke('c_queryWeChatAppId', json);
				if (json.callback) {
					this.checkSetClientValue(json.callback);
				}
			}

		},
		/*
		 * @method c_startBindWeChatPay 启动微信绑定签约状态（IOS无）
		 * @*param c_url  签约地址
		 * @param function 获取成功后客户端的回调
		 */
		c_startBindWeChatPay: function(json) {
			json = json || {};
			var needArr = ['url'];
			if (this.isNeed(needArr, json) != true) {
				return; //非空判断
			}
			CmreadJsBridge.invoke('c_startBindWeChatPay', json);
			if (json.callback) {
				this.checkSetClientValue(json.callback);
			}
		},
		/*
		 * @method c_startMiguAccountUpgrade 启动SDK账号升级界面页面（IOS无）
		 */
		c_startMiguAccountUpgrade: function(json) {
			json = json || {};
			CmreadJsBridge.invoke('c_startMiguAccountUpgrade', json);
		},
		/*
		 * @method startMiguCoinsPage  新增启动我的咪咕币页面（IOS无）
		 */
		startMiguCoinsPage: function(json) {
			json = json || {};
			CmreadJsBridge.invoke('startMiguCoinsPage', json);
		},
		/*
		 * @method miguAdExposured  新增广告曝光和点击（IOS无）安卓不再使用
		 * @*param key 请参考旧版调用方式
		 * @*param adUnitId
		 */
		/*miguAdExposured:function(json){
		    this.toast('新版方法未集成');
		},*/
		/*
		 * @method miguAdOperation  前端对广告页进行点击、曝光等操作（IOS无）安卓不再使用
		 * @*param operateId 请参考旧版调用方式
		 * @*param adUnitId
		 */
		/*miguAdOperation:function(json){
		    this.toast('新版方法未集成');
		},*/
		/*
		 * @method closePage 关闭页面
		 */
		closePage: function(json) {
			var json = json || {};
			CmreadJsBridge.invoke('closePage', json);
		},
		/*
		 * @method c_publishComment  客户端图片评论
		 * @*param pageId 评论页面Id
		 */
		c_publishComment: function(json) {
			json = json || {};
			var needArr = ['pageId'];
			if (this.isNeed(needArr, json) != true) {
				return; //非空判断
			}
			CmreadJsBridge.invoke('c_publishComment', json);
		},
		/*
		 * @method startMiguClient   客户端用户策反活动优化（IOS无）
		 * @*param invokeSource 评论页面Id
		 */
		startMiguClient: function(json) {
 			json = json || {};
 			CmreadJsBridge.invoke('startMiguClient', json);
 		},
		/*
		 * @method startResetPassword   重置密码
		 */
		startResetPassword: function(json) {
			json = json || {};
			CmreadJsBridge.invoke('startResetPassword', json);
		},
		/*
		 * @method clientRecharge 客户端充值 同startRecharge
		 */
		clientRecharge: function(json) {
			json = json || {};
			this.startRecharge(json);
		},
		/*
		 * @method startStoreCharge 跳转到苹果充值页
		 * @param money 充值金额
		 */
		startStoreCharge: function(json) {
			json = json || {};
			var needArr = ['money'];
			if (this.isNeed(needArr, json) != true) {
				return; //非空判断
			}
			CmreadJsBridge.invoke('startStoreCharge', json);
		},
		/*
		 * @method isDownload 是否已下载
		 * @*param jsMethodName 由此JS方法来设置下载按钮是否置灰
		 * @*param contentId 内容ID,
		 * @*param contentType 内容类型,
		 * @*param chapterID 章节id
		 * @param callback  Function 可直接传递回调方法
		 */
		isDownload: function(json) {
			json = json || {};
			/*新老客户端兼容*/
			if (CmreadJsBridge.isNewApp() != true) {
				var action = 'isDownload';
				if (json.jsMethodName && typeof json.jsMethodName == "function") {
					var oname = 'isdownload_' + new Date().getTime();
					b2cFun[oname] = json.jsMethodName;
					json.jsMethodName = 'b2cFun[\"' + oname + '\"]';
				}
				json = JSON.stringify(json);
				this.ios_bCallC(action, json, json.token || "");
			} else {
				var needArr = ['contentId', 'contentType'];
				if (this.isNeed(needArr, json) != true) {
					return; //非空判断
				}
				if (!json.retMthod) {
					json.retMethod = json.jsMethodName; //为了兼容旧版的jsMethod参数
				}
				//给客户端传一个方法
				if (json.retMthod && typeof json.retMthod == "function") {
					var oname = 'isdownload_' + new Date().getTime();
					b2cFun[oname] = json.retMethod;
					json.retMethod = oname;
				} else {
					b2cFun[json.retMethod] = window[json.retMethod] || function() {};
				}
				CmreadJsBridge.fetch('isDownload', json);
			}
		},
		/*
		 * @method isAddToBookshelf 是否已在书架【因需要一开始就加载故兼容方案写在此】
		 * @param jsMethodName 由此JS方法来设置下载按钮是否置灰
		 * @param contentId 内容ID,
		 * @param contentType 内容类型,
		 * @param chapterID 章节id
		 * @param callback  Function 可直接传递回调方法
		 */
		isAddToBookshelf: function(json) {
			json = json || {};
			/*新老客户端兼容*/
			if (CmreadJsBridge.isNewApp() != true) {
				var action = 'isAddToBookshelf';
				if (json.jsMethodName && typeof json.jsMethodName == "function") {
					var oname = 'isaddtoShelf_' + new Date().getTime();
					b2cFun[oname] = json.jsMethodName;
					json.jsMethodName = 'b2cFun[\"' + oname + '\"]';
				}
				json = JSON.stringify(json);
				this.ios_bCallC(action, json, json.token || "");
			} else {
				var needArr = ['contentId', 'contentType', 'jsMethodName'];
				if (this.isNeed(needArr, json) != true) {
					return; //非空判断
				}
				if (!json.retMthod) {
					json.retMethod = json.jsMethodName; //为了兼容旧版的jsMethod参数
				}
				//给客户端传一个方法
				if (json.retMthod && typeof json.retMthod == "function") {
					var oname = 'isaddshelf_' + new Date().getTime();
					b2cFun[oname] = json.retMethod;
					json.retMethod = oname;
				} else {
					b2cFun[json.retMethod] = window[json.retMethod] || function() {};
				}
				CmreadJsBridge.fetch('isAddToBookshelf', json);
			}
		},
		/*
		 * @method goBookStore 去书城
		 */
		goBookStore: function(json) {
			json = json || {};
			CmreadJsBridge.invoke('goBookStore', json);
		},
		/*
		* @method pushDataWhenLoaded 详情页加载完成时，调些方法，把进阅读页要用的数据传给C,此方法与进阅读页参数完全一致[兼容，旧版IOS方法，因必须在页面加载完成时调用]
		* @params contentID, chapterID, contentType, offset,
		    contentName, bigLogo, chargeMode, authorName, chapterName, description, bookLevel, recentlyTime, isCompare, token
		*/
		pushDataWhenLoaded: function(json) {
			json = json || {};
			if (CmreadJsBridge.isNewApp() != true) {
				/*旧版客户端*/
				var needArr = ['contentID', 'chapterID', 'contentType', 'offset', 'contentName', 'bigLogo', 'chargeMode', 'bookLevel'];
				if (this.isNeed(needArr, json) != true) {
					return; //非空判断
				}
				json.bigLogo = this.populateUrl(json.bigLogo);
				var action = 'pushDataWhenLoaded';
				json = JSON.stringify(json);
				this.ios_bCallC(action, json, json.token || "");
			} else {
				this.getAbstractParams(json);
			}
		},
		/*
		 * @method startAudioBookDetailPage 加载听书详情页面
		 * @*url 详情页地址
		 */
		startAudioBookDetailPage: function(json) {
			json = json || {};
			var needArr = ['url'];
			if (this.isNeed(needArr, json) != true) {
				return; //非空判断
			}
			CmreadJsBridge.invoke('startAudioBookDetailPage', json);
		},
		/*
		 * @method readGeneChange 阅读基因推荐
		 */
		readGeneChange: function(json) {
			json = json || {};
			CmreadJsBridge.invoke('readGeneChange', json);
		},
		/*
		 *@method subscribeCatalog
		 */
		subscribeCatalog: function(json) {
			json = json || {};
			var needArr = ['catalogId', 'subCircle', 'isRecharge', 'subType', 'productId', 'isPlatCatalogId'];
			if (this.isNeed(needArr, json) != true) {
				return; //非空判断
			}
			CmreadJsBridge.invoke('subscribeCatalog', json);
		},
		/*
		 * @method unsubscribeCatalog 包月退订
		 */
		unsubscribeCatalog: function(json) {
			json = json || {};
			CmreadJsBridge.invoke('unsubscribeCatalog', json);
		},
		/*
		 * @method goBack2Location 浏览器返回重定向 充值并打赏
		 * @param url 重定向地址
		 */
		/*goBack2Location:function(json){
		    json=json||{};
		    var needArr=['url'];
		    if (this.isNeed(needArr,json) != true) {
		        return;//非空判断
		    }
		    CmreadJsBridge.invoke('goBack2Location', json);
		},*/
		/*
		 * @method JumpMiguReadClient 客户端用户策反活动优化
		 * @param invokeSource
		 */
		/*JumpMiguReadClient:function(json){
		    json=json||{};
		    var needArr=['invokeSource'];
		    if (this.isNeed(needArr,json) != true) {
		        return;//非空判断
		    }
		    CmreadJsBridge.invoke('JumpMiguReadClient', json);
		},*/
		/*
		 *@method refreshBookShelf 前端批量加入书架成功后告知客户端刷新书架
		 */
		refreshBookShelf: function(json) {
			json = json || {};
			CmreadJsBridge.invoke('refreshBookShelf', json);
		},
		/*
		 *@method c_startmiguunionpay 页面触发一级支付sdk
		 */
		c_startmiguunionpay: function(json) {
			json = json || {};
			CmreadJsBridge.invoke('c_startmiguunionpay', json);
		},
		/*
		 *@method startVoiceSearch 启动语音搜索
		 */
		startVoiceSearchpage: function(json) {
			json = json || {};
			var needArr = ['url'];
			if (this.isNeed(needArr, json) != true) {
				return; //非空判断
			}
			json.url=this.populateUrl(json.url);
			CmreadJsBridge.invoke('startVoiceSearchpage', json);
		},
		/*
		 * @method StartMarkContentDialog 评分 2014年5月23号新增
		 * @param contentId 个人中心传--0，包月详情页传--2
		 */
		StartMarkContentDialog: function(json) {
			var needArr = ['contentId'];
			if (this.isNeed(needArr, json) != true) {
				return; //非空判断
			}
			CmreadJsBridge.invoke('StartMarkContentDialog', json || {});
		},
		/*
		 * @method sendCommonSMS 客户端增加短信发送能力发送通用短信
		 * @param receiver 接受人
		 * @param content 内容
		 */
		sendCommonSMS: function(json) {
			var needArr = ['receiver', 'content'];
			if (this.isNeed(needArr, json) != true) {
				return; //非空判断
			}
			CmreadJsBridge.invoke('sendCommonSMS', json || {});
		},
		/*
		 * @method startMMClient Start MM Client
		 * @param URL
		 * @param callerid
		 * @param phone
		 */
		startMMClient: function(json) {
			var needArr = ['URL', 'callerid', 'phone'];
			if (this.isNeed(needArr, json) != true) {
				return; //非空判断
			}
			CmreadJsBridge.invoke('startMMClient', json || {});
		},
		/*
		 * @method c_startChatPage 启动聊天窗口
		 * @param URL
		 * @param callerid
		 * @param phone
		 */
		c_startChatPage: function(json) {
			json = json || {};
			var needArr = ['sendMsisdn', 'avatar', 'nickName'];
			if (this.isNeed(needArr, json) != true) {
				return; //非空判断
			}
			CmreadJsBridge.invoke('c_startChatPage', json);
		},
		/*
		 * @method documentReady B页面加载完成后执行此方法可让客户端提前展示B页面
		 */
		documentReady: function(json) {
			json = json || {};
			if (CmreadJsBridge.isNewApp() != true) {
				if(CmreadJsBridge.system=="ios"){
					var action = 'documentReady';
					var jsonStr = '{}';
					this.ios_bCallC(action,jsonStr,'');
				}
			}else{
				CmreadJsBridge.invoke('documentReady', json);
			}
		},
		/*
		 * @method startLocation 点击偷书入口后，开始定位
		 */
		startLocation: function(json) {
			json = json || {};
			if (CmreadJsBridge.isNewApp() != true) {
				if(CmreadJsBridge.system=="android"){
					cmread.callBackClient('startLocation', {}, json.token||"");
				}else{
					this.ios_bCallC('startLocation', {}, json.token||"");
				}
			}else{
				var retMethod = json.retMethod || 'fetch_' + timeStamp;
				json.retMethod = retMethod;
				var callback = json.callback || global.setClientValue;
				global.b2cFun[retMethod] = callback;
				CmreadJsBridge.invoke('startLocation', json);
			}
		},
		/*
		 * @method checkSetClientValue 当需要客户端回掉此方法时检测是否已存在setClientValue方法
		 * @param callback 成功后执行的回调方法
		 */
		checkSetClientValue: function(callback) {
			if (!global.setClientValue || typeof setClientValue != "function") {
				global.setClientValue = function(key, value) {
					callback(key, value);
					global.setClientValue = undefined;
				}
				/*如果客户端1s内没有调用setClientValue，则直接清空*/
				setTimeout(function() {
					global.setClientValue = undefined;
				}, 2000);
			} else {
				/*this.toast('当前页面已存在一个以上setClientValue方法,callback不生效，请合并至一起');*/
			}
		},
		/*
		 * @method c_startAccountAndSafe 唤起账号安全页
		 */
		c_startAccountAndSafe: function(json) {
			json = json || {};
			var action = "c_startAccountAndSafe";
			switch (CmreadJsBridge.system) {
				case "ios":
					action = "startAccountAndSafe";
					break;
				case "android":
					action = "c_startAccountAndSafe";
					break;
			}
			CmreadJsBridge.invoke(action, json);
		},
		getScrollableArea: function(json) {
			json = json || {};
			if (CmreadJsBridge.isNewApp() != true) {
				/*旧版客户端*/
				if(CmreadJsBridge.system!="ios"){
					try{
						cmread.callBackClient('getScrollableArea',json,'');
					}catch(e){
						console.log(e.message);
					}

				}
			}else{
				/*新版客户端*/
				if(CmreadJsBridge.system!="ios"){
					try{
						json?json=JSON.parse(json):null;
					}catch(e){};
					CmreadJsBridge.invoke('getScrollableArea', json);
				}
			}

		},
		/*启动消息中心*/
		startInfoCenter: function(json) {
			json = json || {};
			CmreadJsBridge.invoke('startInfoCenter', json);
		},
		/*客户端播放视频*/
		playVideo: function(json) {
			json = json || {};
			var needArr = ['urlId', 'videoTitle'];
			if (this.isNeed(needArr, json) != true) {
				return; //非空判断
			}
			CmreadJsBridge.invoke('playVideo', json);
		},
		/*
		 * @method modifyPersonInfo 修改个人信息
		 * @*param actionType url链接
		 */
		modifyPersonInfo: function(json) {
			json = json || {};
			var needArr = ['infoUrl'];
			if (this.isNeed(needArr, json) != true) {
				return; //非空判断
			}
			CmreadJsBridge.invoke('modifyPersonInfo', json);
		},
		/*
		 * @method personActionResponse 响应个人页面中的用户操作
		 * @*param actionType 操作类型
		 * @param actionUrl url链接（默认为空，当actionType为默认、每日签到、我的订购、我的书券、充值、领取优惠、流量管理、意见反馈时才有内容）
		 */
		personActionResponse: function(json) {
			json = json || {};
			var needArr = ['actionType', 'actionUrl'];
			if (this.isNeed(needArr, json) != true) {
				return; //非空判断
			}
			CmreadJsBridge.invoke('personActionResponse', json);
		},
		/*
		 * @method stealBookSuccess 响应个人页面中的用户操作
		 */
		stealBookSuccess: function(json) {
			json = json || {};
			var needArr = ['contentID', 'contentName', 'contentType', 'bigLogo', 'authorName', 'bookLevel', 'chapterID', 'chapterName', 'remainDay', 'isSerial'];
			if (this.isNeed(needArr, json) != true) {
				return; //非空判断
			}
			CmreadJsBridge.invoke('stealBookSuccess', json);
		},
		saveReadGene: function(json) {
			json = json || {};
			CmreadJsBridge.invoke('saveReadGene', json);
		},
		/*全站包订购成功后，给客户端一个通知刷新会员页 7.0以上支持*/
		c_refresh_member_page: function(json) {
			json = json || {};
			CmreadJsBridge.invoke('c_refresh_member_page', json);
		},
		/*
		 * @method IOS恢复够买
		 */
		restoreAppStoreCatalogProduct: function(json) {
			json = json || {};
			CmreadJsBridge.invoke('restoreAppStoreCatalogProduct', json);
		},
		/*
		 * @method IOS登录
		 */
		clientLogin: function(json) {
			json = json || {};
			CmreadJsBridge.invoke('clientLogin', json);
		},
		/*
		 * @method 普通二级B页面展示
		 */
		c_startCommonWebPage: function(json) {
			json = json || {};
			var needArr = ['URL'];
			if (this.isNeed(needArr, json) != true) {
				return
			}
			CmreadJsBridge.invoke("c_startCommonWebPage", json)
		},
		/*
		 * @method 广播通知普通二级B页面刷新当前页面
		 */
		cityRefresh: function(json) {
			json = json || {};
			CmreadJsBridge.invoke('cityRefresh', json);
		},
		/*
		 * @method 弹出C侧一个按钮的Dialog对话框
		 */
		notifyAlert: function(json) {
			json = json || {};
			var needArr = ["responseInfo", "buttonText"];
			if (this.isNeed(needArr, json) != true) {
				return
			}
			CmreadJsBridge.invoke('notifyAlert', json);
		},
		/*
		 * @method 弹出C侧两个按钮的Dialog对话框
		 */
		notifyConfirm: function(json) {
			json = json || {};
			var needArr = ["responseInfo", "positiveText", "nagativeText"];
			if (this.isNeed(needArr, json) != true) {
				return
			}
			CmreadJsBridge.invoke("notifyConfirm", json)
		},
		/*
		 * @method 弹出C侧PopupWidiw控件
		 */
		notifyPopup: function(json) {
			json = json || {};
			var needArr = ["responseInfo", "closeSelf"];
			if (this.isNeed(needArr, json) != true) {
				return
			}
			CmreadJsBridge.invoke("notifyPopup", json)
		},
		/*
		 * @method 跳转没有Titlebar的webview页面
		 */
		startEventWebPage: function(json) {
			json = json || {};
			var needArr = ['URL'];
			if (this.isNeed(needArr, json) != true) {
				return
			}
			json.URL=this.populateUrl(json.URL);
			CmreadJsBridge.invoke("startEventWebPage", json)
		},
		/*
		 * @method 唤起杂志阅读页
		 */
		startMagazineReader: function(json) {
			json = json || {};
			var needArr = ["contentID", "offset", "contentName", "bigLogo"];
			if (this.isNeed(needArr, json) != true) {
				return
			}
			CmreadJsBridge.invoke("startMagazineReader", json)
		},
		/*
		 * @method 跳转手机设置界面
		 */
		startNetSetting: function(json) {
			json = json || {};
			CmreadJsBridge.invoke("startNetSetting", json)
		},
		/*
		 * @method 一键充值+订购或投票
		 */
		continueTasksAfterCharge: function(json) {
			json = json || {};
			CmreadJsBridge.invoke("continueTasksAfterCharge", json)
		},
		/*
		* @method 跳转笔记详情页面
		 @*param noteId笔记ID、
		 @*param msisdn笔记作者阅读号
		*/
		startNoteDetailPage: function(json) {
			json = json || {};
			var needArr = ["noteId", "msisdn"];
			if (this.isNeed(needArr, json) != true) {
				return
			}
			CmreadJsBridge.invoke("startNoteDetailPage", json)
		},
		/*@method 打开最近阅读页面*/
		startRecentlyReadMoreActivity: function(json) {
			json = json || {};
			CmreadJsBridge.invoke("startRecentlyReadMoreActivity", json)
		},
		/*BR004351 咪咕币*/
		startMiguCoinsRechargePage:function(json){
			json = json || {};
			CmreadJsBridge.invoke("startMiguCoinsRechargePage", json);
		},
		/*
		 * @method c_publishCommentBs  客户端图片评论 20171013新增接口针对咪咕书城
		 * @*param pageId 评论页面Id
		 * @*param type “1”:活动页评论内容上传：“2”：咪咕书城评论图片上传
		 */
		c_publishCommentForBs: function(json,callback) {
			/*新接口*/
			var retMethod = retMethod || 'fetch_' + timeStamp; //如果不指定就用时间戳
			callback = callback || function(){console.log('未指定c_publishCommentForBs的callback');};
			global.b2cFun[retMethod] = callback;
			json.retMethod=retMethod;
			CmreadJsBridge.fetch('c_publishComment', json||{});
		},
		/*
		 * @method startInterestSettingPage  前端可以跳到偏好设置页面
		 */
		startInterestSettingPage:function(json){
			json = json || {};
			CmreadJsBridge.invoke("startInterestSettingPage", json);
		},
		/*
		* @method c_searchBoxSync 传入纠错字符串
		*/
		c_searchBoxSync:function(json){
			json = json || {};
			CmreadJsBridge.invoke("c_searchBoxSync", json);
		}
	};
	CmreadJsBridge.init();
	global.cmrsdk = sdk;
})(window, document);
