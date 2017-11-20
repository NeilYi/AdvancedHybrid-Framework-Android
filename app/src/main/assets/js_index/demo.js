/*var $ = function (selector) {
    return document.querySelector(selector);
}*/
cmrsdk.documentReady();
cmrsdk.startAdWebPage=function(json){
    json=json||{};
    CmreadJsBridge.invoke('startAdWebPage', json);
}
var fun = {
    items:[
            {id:"startVoiceSearchpage1",desc:"startVoiceSearchpage | 启动语音搜索new"},
            {id:"startShakepage",desc:"startShakepage | 启动摇一摇"},
            {id:"StartMarkContentDialog",desc:"StartMarkContentDialog | 评分"},
            {id:"sendCommonSMS",desc:"sendCommonSMS | 发送通用短信"},
            {id:"startMMClient",desc:"startMMClient | 启动MM客户端"},
            {id:"startLocation",desc:"startLocation | 获取位置信息",clickfun:function(){
                cmrsdk.startLocation({callback:function(key,value){
                    cmrsdk.toast(key+",结果:"+value);
                }});
            }},
            {id:"subscribeCatalog",desc:"subscribeCatalog | IOS启动包月 | IOS独有",clickfun:function(){
                cmrsdk.subscribeCatalog({catalogId:'590002281',subCircle:'12',isRecharge:'0',subType:'99',productId:'com.Cmcc.Cmread.Vip.Full.360',isPlatCatalogId:'1'});
            }},
            {id:"refreshBookShelf",desc:"refreshBookShelf | 批量刷新书架",clickfun:function(){
                cmrsdk.refreshBookShelf();
            }},
            {id:"c_startChatPage",desc:"c_startChatPage | 启动聊天界面",clickfun:function(){
                cmrsdk.c_startChatPage({sendMsisdn:"15958166197",avatar:"http://wap.cmread.com/hbc/images/avatars/defaultmigu.jpg",nickName:"你猜我无聊不",token:"ab644c33a3d1fc5d61c76d8a8e315370"});
            }},
            {id:"c_refresh_member_page",desc:"c_refresh_member_page | 全站包订购后刷新会员页",clickfun:function(){
                cmrsdk.c_refresh_member_page();
            }},
            {id:"c_startAccountAndSafe",desc:"c_startAccountAndSafe | 唤起账号安全页",clickfun:function(){
                cmrsdk.c_startAccountAndSafe();
            }},
            {id:"playVideo",desc:"playVideo | 客户端播放视频",clickfun:function(){
                cmrsdk.playVideo({"urlId":"http://mgcdn.migucloud.com/vi0/ftp/miguread/CLOUD1000166750/54/0eakSOB4cF7apynX8vo4MP4sim54.mp4?duration=109&owner=198&quality=54×tamp=20170711094616&title=sanshengsanshi_sc54.mp4&vid=0eakSOB4cF7apynX8vo4¶1=yyy¶2=xxx","videoTitle":"刘亦菲杨洋主演电影《三生三世十里桃花》预告"});
            }},
            {id:"startInfoCenter",desc:"startInfoCenter | 启动消息中心",clickfun:function(){
                cmrsdk.startInfoCenter();
            }},
            {id:"getClientValue",desc:"getClientValue | 旧版获取网络参数",clickfun:function(){
                cmrsdk.getClientValue('networkType');
            }},
            {id:"modifyPersonInfo",desc:"modifyPersonInfo | 修改个人信息",clickfun:function(){
                cmrsdk.modifyPersonInfo({infoUrl:"http://wap.cmread.com/rbc/p/cps_test.jsp"});
            }},
            {id:"personActionResponse",desc:"personActionResponse | 个人主页用户操作",clickfun:function(){
                cmrsdk.personActionResponse({actionUrl:"http://wap.cmread.com/rbc/p/cps_test.jsp",actionType:"1"});
            }},
            {id:"restoreAppStoreCatalogProduct",desc:"restoreAppStoreCatalogProduct | IOS恢复购买",clickfun:function(){
                cmrsdk.restoreAppStoreCatalogProduct();
            }},
            {id:"saveReadGene",desc:"saveReadGene | 阅读基因界面",clickfun:function(){
                cmrsdk.saveReadGene();
            }},
            {id:"clientLogin",desc:"clientLogin | 客户端登录",clickfun:function(){
                cmrsdk.clientLogin();
            }},
            {id:"stealBookSuccess",desc:"stealBookSuccess | 偷书成功后给客户端",clickfun:function(){
                cmrsdk.stealBookSuccess({
                    contentType: '1',
                    contentID: '413227718',
                    contentName: '随身带着外星人',
                    bigLogo: 'http://wap.cmread.com/rbc/cover_file/7718/413227718/20151222134710/cover75100.jpg',
                    chapterID: '413227971',
                    chapterName: '第0001章 被开除了',
                    isSerial: '0',
                    jsMethodName:'ishaven',
                    chargeMode: '1',
                    authorName: '华丽转身',
                    bookLevel: '1',
                    isPrePackFinished: '2',
                    c_downloadAttribute: '1',
                    remainDay:"10"
                });
            }},
            {
             id:"c_startCommonWebPage",desc:"c_startCommonWebPage | 二级B页面展示",clickfun:function(){
                 cmrsdk.c_startCommonWebPage({
                     URL:"http://wap.cmread.com/hbc/t/exchangecardbene.jsp"
                 })
             }
            },
            {
                id:"cityRefresh",desc:"cityRefresh | 二级B页面刷新当前页面 ",clickfun:function(){
                    cmrsdk.cityRefresh()
                }
            },
            {
                id:"notifyAlert",desc:"notifyAlert | 弹出C侧一个按钮的Dialog对话框",clickfun:function(){
                    cmrsdk.notifyAlert({
                        responseInfo:"可以",
                        buttonText:"可以"
                    })
                }
            },
            {
                id:"notifyConfirm",desc:"notifyConfirm | 弹出C侧两个按钮的Dialog对话框",clickfun:function(){
                    cmrsdk.notifyConfirm({
                        responseInfo:"可以",
                        positiveText:"可以",
                        nagativeText:"不可以"
                    })
                }
            },
            {
                id:"notifyPopup",desc:"notifyPopup | 弹出C侧PopupWidiw控件 ",clickfun:function(){
                    cmrsdk.notifyPopup({
                        responseInfo:"可以",
                        closeSelf:"true"
                    })
                }
            },
            {
               id:"startEventWebPage",desc:"startEventWebPage | 跳转没有Titlebar的webview页面",clickfun:function(){
                   cmrsdk.startEventWebPage({
                       URL:"http://wap.cmread.com/hbc/t/exchangecardbene.jsp"
                   })
               }
            },
            {
                id:"startMagazineReader",desc:"startMagazineReader | 唤起杂志阅读页",clickfun:function(){
                    cmrsdk.startMagazineReader({
                        contentID:"641265174",
                        offset:"0",
                        contentName:"爱尚生活2017年7月5期",
                        bigLogo:"http://wap.cmread.com/mbc/cover_file/5174/641265174/20170731094949/180240.jpg"
                    })
                }
            },
            {
                id:"startNetSetting",desc:"startNetSetting | 跳转手机设置界面 ",clickfun:function(){
                    cmrsdk.startNetSetting()
                }
            },
            {id:"startNoteDetailPage",desc:"startNoteDetailPage | 跳转笔记详情页面",clickfun:function(){
                cmrsdk.startNoteDetailPage({"noteId":'666666',"msisdn":'15715849960'});
            }},
            {id:"startRecentlyReadMoreActivity",desc:"startRecentlyReadMoreActivity | 打开最近阅读页面",clickfun:function(){
                cmrsdk.startRecentlyReadMoreActivity();
            }},
            {
                id:"startExPageNew",desc:"startExPageNew | IOS添加参数后的测试 ",clickfun:function(){
                    cmrsdk.startExPage({url:'http://s.migu.cn/',noCustomRequest:"1"});
                }
            },
            {
                id:"startMiguCoinsRechargePage",desc:"startMiguCoinsRechargePage | 包月资费页启动咪咕币 ",clickfun:function(){
                    cmrsdk.startMiguCoinsRechargePage();
                }
            },
            {
                id:"startDownload_mh",desc:"startDownload | 漫画下载测试 ",clickfun:function(){
                    cmrsdk.startCatalog({contentID:"392360544",contentType:2,contentName:"豪门甜心",chargeMode:2,bigLogo:"http://cdn.cmread.com/coverFile/392360544/5527c1c8a1e2dfbe056a5f40dc9516058a8736d8b3d2/360x480.jpg",bookLevel:"4"})
                }
            },
            {
                id:"getClientValue3",desc:"getClientValue | 获取网络状态 ",clickfun:function(){
                    cmrsdk.getClientValue('adJson');
                }
            },
            {
                id:"startExPage_new1",desc:"startExPage | 添加了noLoadingPage的参数0",clickfun:function(){
                    cmrsdk.startExPage({url:'http://s.migu.cn/',noCustomRequest:"1",noLoadingPage:"0"});
                }
            },
            {
                id:"startExPage_new2",desc:"startExPage | 添加了noLoadingPage的参数1",clickfun:function(){
                    cmrsdk.startExPage({url:'http://s.migu.cn/',noCustomRequest:"1",noLoadingPage:"1"});
                }
            },
            {
                id:"toastAndRefresh",desc:"toastAndRefresh | 同时执行toast和刷新",clickfun:function(){
                    cmrsdk.toast('点击了toast');
                    setTimeout(function(){
                        cmrsdk.refresh();
                    },30);
                }
            },
            {
                id:"c_publishCommentForBs",desc:"c_publishCommentForBs | 书城的评论上传",clickfun:function(){
                    cmrsdk.c_publishCommentForBs({
                        c_pageId:"1025652",
                        type:"2"
                    },function(resdata){
                        console.log(resdata);
                    })
                }
            },
            {
                id:"startInterestSettingPage",desc:"startInterestSettingPage | 跳到偏好设置页面",clickfun:function(){
                    cmrsdk.startInterestSettingPage();
                }
            },
            {
                id:"closePageNew",desc:"closePageNew | 添加参数的closepage",clickfun:function(){
                    cmrsdk.closePage({
                        url:"http://wap.cmread.com/r"
                    });
                }
            },
            {
                id:"startAdWebPage",desc:"startAdWebPage | startAdWebPage跳转1",clickfun:function(){
                    cmrsdk.startAdWebPage({
                        URL:"https://dmp-data.vip.com/deeplink/showSpecial?sid=ja5vde&tra_from=tra%3Ady7wh7ma%3A%3A%3A%3A"
                    });
                }
            },
            {
                id:"startAdWebPage2",desc:"startAdWebPage | startAdWebPage跳转2",clickfun:function(){
                    cmrsdk.startAdWebPage({
                        URL:"https://floor.ews.m.jaeapp.com/bestseller/jump.html"
                    });
                }
            },
            {
                id:"startAdWebPage3",desc:"startAdWebPage | startAdWebPage跳转3",clickfun:function(){
                    cmrsdk.startAdWebPage({
                        URL:"https://h5.m.jd.com/dev/22Px9BahzVFBKsj3Fxz5GpXa4dKn/index.html?ad_od=1"
                    });
                }
            },
            {
                id:"startAdWebPage5",desc:"startAdWebPage | startAdWebPage跳转4",clickfun:function(){
                    cmrsdk.startAdWebPage({
                        URL:"vipshop://showSpecial?tra_from=tra%3Ady7wh7ma%3A%3A%3A%3A&cm=M801006M&sid=ja5vde"
                    });
                }
            },
            {
                id:"startAdWebPage4",desc:"startAdWebPage | 自定义跳转",clickfun:function(){
                    var url=$(".addUrl").val()||"vipshop://showSpecial?tra_from=tra%3Ady7wh7ma%3A%3A%3A%3A&cm=M801006M&sid=ja5vde";
                    if(url==""||!url){
                        cmrsdk.toast('startAdWebPage的url不能为空');
                        return;
                    }
                    cmrsdk.startAdWebPage({
                        URL:url
                    });
                }
            },
            {
                id:"c_searchBoxSync",desc:"c_searchBoxSync | 打开搜索",clickfun:function(){
                    cmrsdk.c_searchBoxSync({
                        txt:'三生三世'
                    });
                }
            }
        ],
    createItem:function(){
        var self=this,
        str="";
        self.items.forEach(function(v,k){
            str+='<div class="test-item">\
                <a href="javascript:void(0)" class="'+v.desc[0]+'_item global_item" id="'+v.id+'">'+v.desc+'</a>\
            </div>';
            if(v.clickfun){
                $("#"+v.id).live('click',function(){
                    v.clickfun();
                });
            }
        });
        $('.part-auto').append(str);
    },
    bindEvents: function () {
        /*toast*/
        $(".toast-btn").on('click', function () {
            var msg = $('.toast-input').value || "提示语为空";
            cmrsdk.toast(msg);
        });
        /*登录成功js回调*/
        $("#logincallback").on("click", function () {
            cmrsdk.switchLogin(function () {
                var ste = "jsuufe" + 'fff';
                alert('回调：登录成功' + ste);
            });
        });
        /*换气阅读页*/
        $("#startReader").on("click", function () {
            cmrsdk.startReader({
                contentID: "447093151",
                chapterID: '447093158',
                contentType: 1,
                contentName: "绝品透视高手",
                bigLogo: "http://wap.cmread.com/rbc/cover_file/3151/447093151/20161202174252/cover180240.jpg",
                chargeMode: 1,
                authorName: "九殇",
                offset: 0,
                bookLevel:1,

            });
        });
        /*唤起目录*/
        $("#startCatalog").on("click", function () {
            cmrsdk.startCatalog({
                contentID: 409285694,
                contentType: 1,
                contentName: "星河霸血",
                chargeMode: 2,
                bigLogo: "http://wap.cmread.com/rbc/cover_file/5694/409285694/20150812093332/cover75100.jpg",
                authorName: "王袍",
                bookLevel: 1
            });
        });
        /*启动速配略图阅读页*/
        $("#startPicReader").on("click", function () {
            cmrsdk.startPicReader({
                contentID: "447093151",
                chapterID: "447093158",
                contentName: "星河霸血",
                count: 3,
                imageUrl: ';PicUrl:http://wap.cmread.com/rbc/cover_file/5694/409285694/20150812093332/cover75100.jpg;PicUrl:http://wap.cmread.com/rbc/cover_file/5694/409285694/20150812093332/cover75100.jpg;PicUrl:http://wap.cmread.com/rbc/cover_file/5694/409285694/20150812093332/cover75100.jpg',
                offset: 2,
                bigLogo: "http://wap.cmread.com/rbc/cover_file/5694/409285694/20150812093332/cover75100.jpg"

            });
        });
        /*启动速配略图阅读页*/
        $("#startTTSReader").on("click", function () {
            cmrsdk.startTTSReader({
                contentID: '407792837',
                chapterID: '407792839',
                contentType: 1,
                offset: 0,
                contentName: '御仙无双',
                bigLogo: 'http://wap.cmread.com/rbc/cover_file/2837/407792837/20150617184254/cover180240.jpg',
                chargeMode: 0,
                authorName: '小生不才',
                isCompare: true
            });
        });
        /*登录鉴权*/
        $("#authenticate").on("click", function () {
            cmrsdk.authenticate(
                function(){
                    alert("成功")
                }
            );
        });
        /*刷新*/
        $("#viewRrefresh").on("click", function () {
            cmrsdk.refresh();
        });
        /*刷新*/
        $("#goBack").on("click", function () {
            cmrsdk.goBack();
        });
        /*加入书架*/
        $("#addToBookshelf").on("click", function () {
            /*addToBookshelf('1','413227718','随身带着外星人','http://wap.cmread.com/rbc/cover_file/7718/413227718/20151222134710/cover75100.jpg', '413227971','第0001章 被开除了','0','1','4','华丽转身','1','2')*/
            cmrsdk.addShelf({
                contentType: '1',
                contentID: '413227718',
                contentName: '随身带着外星人',
                bigLogo: 'http://wap.cmread.com/rbc/cover_file/7718/413227718/20151222134710/cover75100.jpg',
                chapterID: '413227971',
                chapterName: '第0001章 被开除了',
                isSerial: '0',
                jsMethodName: function () {
                    cmrsdk.toast('加入书架成功回调');
                },
                chargeMode: '1',
                authorName: '华丽转身',
                bookLevel: '1',
                isPrePackFinished: '2',
                c_downloadAttribute: '1'
            });
        });
        /*批量下载图书分册*/
        $("#batchDownloadFascicle").on("click", function () {
            cmrsdk.batchDownload({
                contentID:'409448521',
                contentName:'神秘老公不离婚',
                chargeMode:2, bigLogo:'/rbc/cover_file/8521/409448521/20150817200323/神秘老公不离婚-大封面_meitu_3_meitu_1_meitu_2.jpg',
                authorName:'聿辰',
                isSerial:'0',
                isPrePackFinished:'0'
            });
        });
        /*下载*/
        $("#startDownload").on("click", function () {

            cmrsdk.startDownload({
                url: '/rbc/f/dlc?ln=15120_450282_92297267_4_L2&amp;nid=41119636&amp;dlp=1&amp;cm=M8030001&amp;purl=%2Frbc%2Fl%2Fv.jsp%3Fnid%3D41119636%26cm%3DM8030001%26bid%3D453058445%26t1%3D17254&amp;bid=453058445&amp;t1=17254&amp;dlt=0&amp;vt=3&amp;cid=453058470',
                contentID: '453058445',
                contentType: 1,
                contentName: '人的脑洞略大于整个宇宙',
                chapterID: '453058448',
                chapterName: '人的脑洞略大于整个宇宙',
                chargeMode: 1,
                bigLogo: '/rbc/cover_file/8445/453058445/20170301154405/cover180240.jpg',
                authorName: '【美】丹·刘易斯；陈亚萍译',
                canBatchDownload: 'false',
                jsMethodName: function () {
                    cmrsdk.toast('下载完成这里是成功回调');
                },//ios必填
                isPrePackFinished: '',
                bookLevel: '1',
                isSerial:"0"
            });

        });
        $("#startExPage").on("click", function () {
            cmrsdk.startExPage({url:'http://s.migu.cn/'});
        });
        $("#shareContent").on("click", function () {

            cmrsdk.share({
                title:"速8正品，激情扫货",
                description:"车速太快，容易出轨！激情太多，这里释放!",
                bigLogo:"http://s.migu.cn/stN/s8/imgs/shareImg.jpeg",
                URL:"/rbc/t/cps_test.jsp",
                type:1,
                setClientvalue:function(key,value){
                    cmrsdk.toast("key:"+key+",value:"+value);
                }
            });
        });
        $("#shareContentF").on("click", function () {
            cmrsdk.share({
                title:"速8正品，激情扫货",
                description:"车速太快，容易出轨！激情太多，这里释放!",
                bigLogo:"http://s.migu.cn/stN/s8/imgs/shareImg.jpeg",
                URL:"http://s.migu.cn/s8",
                type:2
            });
        });
        $("#shareContentEx").on("click", function () {
            cmrsdk.shareEx({
                shareType:"4",
                detailType:"2",
                contentType:"10",
                title:"速8正品，激情扫货",
                description:"车速太快，容易出轨！激情太多，这里释放!",
                bigLogo:"http://wap.cmread.com/rbc/p/content/repository/ues/image/s109/20170422231841094.jpg",
                imgUrl:"http://wap.cmread.com/rbc/p/content/repository/ues/image/s109/20170422231841094.jpg",
                URL:"http://wap.cmread.com/rbc/t/cs_share_pop.jsp"
            });
        });
        //关闭页面
        $("#closePage").on("click", function () {
            cmrsdk.closePage();
        });
        //频道跳转
        $("#jumpCatalog").on("click", function () {
            cmrsdk.jumpCatalog();
        });
        //注销
        $("#logout").on("click", function () {
            cmrsdk.logout();
        });
        //页面应用防火墙
        $("#startIEForDownload").on("click", function () {
            cmrsdk.startIEForDownload({
                url:"http://wap.cmread.com/"
            });
        });
        //启动绑定页
        $("#startBindPaymentNumber").on("click", function () {
            cmrsdk.startBindPaymentNumber();
        });
        //解除绑定页
        $("#startUnbindPaymentNumber").on("click", function () {
            cmrsdk.startUnbindPaymentNumber();
        });
        //启动修改密码页
        $("#startChangePassword").on("click", function () {
            cmrsdk.startChangePassword();
        });
        //启动设置密保
        $("#startSetSecurityQuestion").on("click", function () {
            cmrsdk.startSetSecurityQuestion();
        });
        //启动个人笔记
        $("#startUserNotes").on("click", function () {
            cmrsdk.startUserNotes();
        });
        //启动二级页面
        $("#startSimplePage").on("click", function () {
            cmrsdk.startSimplePage({url:"http://wap.cmread.com"});
        });
        //启动语音搜索
        $("#startVoiceSearch").on("click", function () {
            cmrsdk.startVoiceSearch({url:""});
        });
        //启动充值 startRechargePage
        $("#startRecharge").on("click", function () {
            /*cmrsdk.startRecharge({url:"http://wap.cmread.com/rbc/t/rechargePage.jsp;jsessionid=53FF430E477F27173E39005317B85944.8ngG2p9UE.2.0?ln=759_553025__0_&vt=3"});*/
            cmrsdk.startRecharge({url:"http://wap.cmread.com/rbc/t/cps_test.jsp;jsessionid=53FF430E477F27173E39005317B85944.8ngG2p9UE.2.0?ln=759_553025__0_&vt=3"});

        });
        //关闭充值 startRechargePage
        $("#closeRecharge").on("click", function () {
            cmrsdk.closeRecharge({fsrc:1});
        });
        //启动摇一摇 startShake
        $("#startShake").on("click", function () {
            cmrsdk.startShake({});
        });
        //关闭摇一摇结果框 closeShakeResultDialog
        $("#closeShakeResultDialog").on("click", function () {
            cmrsdk.closeShakeResultDialog();
        });
        //启动摇一摇结果详情页（IOS无） startShakeResultDetailPage
        $("#startShakeResultDetailPage").on("click", function () {
            cmrsdk.startShakeResultDetailPage({URL:"http://wap.cmread.com"});
        });
        //回到书架
        $("#BackToShelf").on("click", function () {
            cmrsdk.BackToShelf();
        });
        //分享客户端
        $("#shareApp").on("click", function () {
            cmrsdk.shareApp({
                shareType:"4",
                detailType:"1",
                title:"欢迎使用咪咕阅读",
                bigLogo:"",
                URL:"http://wap.cmread.com/rbc/p/zonghesy.jsp"
            });
        });
        /*startSMSReceiver 充值二次确认*/
        $("#startSMSReceiver").on("click", function () {
            cmrsdk.startSMSReceiver({callingNum:"15067152679", calledNum:"15700078519", featureStr:"test"});
        });
        /*EnCliAndDown 分享回流*/
        $("#EnCliAndDown").on("click", function () {
            cmrsdk.EnCliAndDown({forwardUrl:"http://wap.cmread.com", bookDownUrlStr:"http://wap.cmread.com", BookIDStr:"450580860"});
        });
        /*sendSMSForUNICOMChargeAck 2.1.25	客户端增加联通充值的发短信方法*/
        $("#sendSMSForUNICOMChargeAck").on("click", function () {
            cmrsdk.sendSMSForUNICOMChargeAck({receiver:"15067152679",content:"sendSMSForUNICOMChargeAck测试短信发送"});
        });
        /*payToAlipay 客户端传送给支付宝sdk方法*/
        $("#payToAlipay").on("click", function () {
            cmrsdk.payToAlipay({
                subject:"xxx",
                total_fee:100,
                body:"xxx",
                ordered:"xxx",
                result_url:"xxx",
                orderId:"xxx",
                "notify_url":""
            });
        });
        /*getAbstractParams 摘要页滑动阅读页(IOS无)*/
        $("#getAbstractParams").on("click", function () {
            cmrsdk.getAbstractParams({
                contentID: "447093151",
                chapterID: '447093158',
                contentType: 1,
                contentName: "绝品透视高手",
                bigLogo: "http://wap.cmread.com/rbc/cover_file/3151/447093151/20161202174252/cover180240.jpg",
                authorName: "九殇",
                offset: 0
            });
        });
        /*payToAlipay 客户端传送给支付宝sdk方法*/
        $("#refreshPersonal").on("click", function () {
            cmrsdk.refreshPersonal();
        });
        /*startCommonMainPage 启动听书/漫画/杂志-图片首页*/
        $("#startCommonMainPage").on("click", function () {
            cmrsdk.startCommonMainPage({
                channelTag:"2",
                url:"http://wap.cmread.com/rbc/p/zonghesy.jsp"
            });
        });
        /*startMoreWonderfulPage 启动不一样的精彩页面(IOS无)*/
        $("#startMoreWonderfulPage").on("click", function () {
            cmrsdk.startMoreWonderfulPage();
        });
        /*startStealBookPage 启动不一样的精彩页面(IOS无)*/
        $("#startStealBookPage").on("click", function () {
            cmrsdk.startStealBookPage();
        });
        /*startOfferWall 个人页面中启动客户端支付绑定页面（IOS无）*/
        $("#startOfferWall").on("click", function () {
            cmrsdk.startOfferWall();
        });
        /*payToWX 启动微信支付（IOS无）*/
        $("#payToWX").on("click", function () {
            cmrsdk.payToWX({
                appid:"xx",
                noncestr:"xx",
                partnerid:"xx",
                prepayid:"xx",
                timestamp:"xx",
                sign:"xx",
                result_url:"xx"
            });
        });
        /*startBindAccount 我、设置、资费等页面 启动随机用户绑定手机号码页面*/
        $("#startBindAccount").on("click", function () {
            cmrsdk.startBindAccount();
        });
        /*startEventWebPage 配合前端页面，优化特殊的webview样式(IOS无)*/
        $("#startEventWebPage").on("click", function () {
            cmrsdk.startEventWebPage({
                URL:"http://wap.cmread.com/"
            });
        });
        /*sendSMSForChargeAck 发送三网融合确认短信（IOS无)*/
        $("#sendSMSForChargeAck").on("click", function () {
            cmrsdk.sendSMSForChargeAck({
                receiver:"15067152679",
                content:"sendSMSForChargeAck 测试"
            });
        });
        /*notifyDownload 在咪咕星页面,单机包下载管理（IOS无)*/
        $("#notifyDownload").on("click", function () {
            cmrsdk.notifyDownload({
                state:"xx", productID:"xxxx", url:"xxx", packageName:"xxx"
            });
        });
        /*sendDonwloadList 在咪咕星页面,单机包下载管理（IOS无)*/
        $("#sendDonwloadList").on("click", function () {
            cmrsdk.sendDonwloadList({
                downloadList:[{
                    productID:"productID",
                    packageName:"packageName"
                }]
            });
        });
        /*notifyFcode 通知F码（IOS无)*/
        $("#notifyFcode").on("click", function () {
            cmrsdk.notifyFcode({
                Fcode:"Fcode",
                contentId:"contentId"
            });
        });
        /*closeExPage 关闭充值/悦读中国等外部页面并刷新之前的页面（IOS无）*/
        $("#closeExPage").on("click", function () {
            cmrsdk.closeExPage();
        });
        /*getCatalogPayResult 通知包月订购状态（IOS无）*/
        $("#getCatalogPayResult").on("click", function () {
            cmrsdk.getCatalogPayResult({resultCode:"0"});
        });
        /*startBindAlipay 通知包月订购状态（IOS无）*/
        $("#startBindAlipay").on("click", function () {
            cmrsdk.startBindAlipay();
        });
        /*getBindNumberState 非移用户获取绑定支付号码状态（IOS无）*/
        $("#getBindNumberState").on("click", function () {
            cmrsdk.getBindNumberState({uid:"15067152679"});
        });
        /*c_sendApkDownloadList 在应用墙,通知客户端需下载单机包列表（IOS无）*/
        $("#c_sendApkDownloadList").on("click", function () {
            cmrsdk.c_sendApkDownloadList({
                c_apkDownloadList:[
                    {c_packageName:"app1",c_versionCode:"c_versionCode"},
                    {c_packageName:"app2",c_versionCode:"c_versionCode2"}
                ]
            });
        });
        /*c_notifyApkDownload 在应用墙,通知客户端需下载单机包列表（IOS无）*/
        $("#c_notifyApkDownload").on("click", function () {
            cmrsdk.c_notifyApkDownload({c_state:"0",c_packageName:"安卓测试包"});
        });
        /*relateAccountSuccess 登录成功后进行的动作..（IOS无）*/
        $("#relateAccountSuccess").on("click", function () {
            cmrsdk.relateAccountSuccess({successAction:"http://wap.cmread.com", relatedMobile:"15067152679"});
        });
        /*startMobileRelateAccount 启动账号关联页面*/
        $("#startMobileRelateAccount").on("click", function () {
            cmrsdk.startMobileRelateAccount({sceneId:"102456",successAction:""});
        });
        /*c_queryWeChatAppId 启动查询微信appid是否支持微信微支付（IOS无）*/
        $("#c_queryWeChatAppId").on("click", function () {
            cmrsdk.c_queryWeChatAppId();

        });
        /*c_startBindWeChatPay 启动微信绑定签约状态（IOS无）*/
        $("#c_startBindWeChatPay").on("click", function () {
            cmrsdk.c_startBindWeChatPay({c_url:"http://wap.cmread.com",callback:function(key,value){
                cmrsdk.toast("c_startBindWeChatPay:"+value);
            }});
        });
        /*c_startMiguAccountUpgrade 启动SDK账号升级界面页面（IOS无）*/
        $("#c_startMiguAccountUpgrade").on("click", function () {
            cmrsdk.c_startMiguAccountUpgrade();
        });
        /*startMiguCoinsPage 新增启动我的咪咕币页面（IOS无）*/
        $("#startMiguCoinsPage").on("click", function () {
            cmrsdk.startMiguCoinsPage();
        });
        /*miguAdExposured 新增广告曝光和点击（IOS无）*/
        $("#miguAdExposured").on("click", function () {
            cmrsdk.miguAdExposured();
        });
        /*miguAdOperation 前端对广告页进行点击、曝光等操作（IOS无）*/
        $("#miguAdOperation").on("click", function () {
            cmrsdk.miguAdOperation();
        });
        /*c_publishComment 前端活动评论页面，调用客户端写评论）*/
        $("#c_publishComment").on("click", function () {
            cmrsdk.c_publishComment({
                pageId:"456358726"
            });
        });
        /*startMiguClient 客户端用户策反活动优化（IOS无）*/
        $("#startMiguClient").on("click", function () {
            cmrsdk.startMiguClient({invokeSource:"test"});
        });
        /*startResetPassword   重置密码*/
        $("#startResetPassword").on("click", function () {
            cmrsdk.startResetPassword();
        });
        /*clientRecharg   客户端充值*/
        $("#clientRecharge").on("click", function () {
            cmrsdk.clientRecharge();
        });
        /*startStoreCharge   重置密码*/
        $("#startStoreCharge").on("click", function () {
            cmrsdk.startStoreCharge({money:'6'});
        });
        /*isDownload  是否已下载*/
        $("#isDownload").on("click", function () {
            cmrsdk.isDownload({
                contentId:"440040745",
                contentType:1,
                chapterID:"440041168",
                jsMethodName:'isDownBack',
                retMethod:'isDownBack'
            });
        });
        window.isDownBack=function(param){
            cmrsdk.toast('isDownBack');
        }
        b2cFun.isDownBack=function(param){
            cmrsdk.toast('b2cfun.isDownBack');
        }
        /*isAddToBookshelf   是否已在书架*/
        $("#isAddToBookshelf").on("click", function () {
            cmrsdk.isAddToBookshelf({
                contentId:"440040745",
                contentType:1,
                chapterID:"440041168",
                jsMethodName:"isaddShelfBack"
            });
        });
        window.isaddShelfBack=function(param){
            cmrsdk.toast('isAddToBookshelf');
        }
        /*goBookStore  去书城*/
        $("#goBookStore").on("click", function () {
            cmrsdk.goBookStore();
        });
        /*pushDataWhenLoaded  把进阅读页要用的数据传给C,此方法与进阅读页参数完全一致*/
        $("#pushDataWhenLoaded").on("click", function () {
            cmrsdk.pushDataWhenLoaded({
                contentID: "447093151",
                chapterID: '447093158',
                contentType: 1,
                contentName: "绝品透视高手",
                bigLogo: "http://wap.cmread.com/rbc/cover_file/3151/447093151/20161202174252/cover180240.jpg",
                authorName: "九殇",
                offset: 0,
                chargeMode:"1",
                bookLevel:"1"
            });
        });
        /*startAudioBookDetailPage  加载听书详情页面*/
        $("#startAudioBookDetailPage").on("click", function () {
            cmrsdk.startAudioBookDetailPage({url:"http://wap.cmread.com/rbc/660485191/index.htm?amp;nid=398114494&ln=1105_552932_116860940_3_3&srsc=1&vt=3&page=1&vt=3"});
        });
        /*readGeneChange  阅读基因推荐*/
        $("#readGeneChange").on("click", function () {
            cmrsdk.readGeneChange();
        });
        /*unsubscribeCatalog  包月退订*/
        $("#unsubscribeCatalog").on("click", function () {
            cmrsdk.unsubscribeCatalog();
        });
        /*goBack2Location  浏览器返回重定向*/
        $("#goBack2Location").on("click", function () {
            cmrsdk.goBack2Location({
                url:"http://wap.cmread.com/rbc/t/cps_test.jsp"
            });
        });
        /*JumpMiguReadClient  跳转咪咕阅读详情页*/
        $("#JumpMiguReadClient").on("click", function () {
            cmrsdk.JumpMiguReadClient();
        });
        $("#startVoiceSearchpage1").on('click',function(){
            cmrsdk.startVoiceSearchpage({
                url:"http://wap.cmread.com/rbc/t/t_search.jsp"
            });
        });
        /*
        * @method startShake 启动摇一摇
        * @*param dURL 活动详情页url
        * @*param rURL 摇一摇跳转到摇一摇结果页
        * @*param pURL 摇一摇背景图
        * @*param sURL 手机晃动图片
        */
        $("#startShakepage").on('click',function(){
            cmrsdk.startShake({
                dURL:"http://wap.cmread.com/rbc/t/t_search.jsp",
                rURL:"http://wap.cmread.com/rbc/t/t_search.jsp",
                pURL:"http://wap.cmread.com/hd/images/avatars/female1.jpg",
                sURL:"http://wap.cmread.com/hd/p/content/repository/ues/image/s180/grade2icon.png"
            });
        });
        $("#StartMarkContentDialog").on('click',function(){
            cmrsdk.StartMarkContentDialog({contentId:"441048419"});
        });
        $("#sendCommonSMS").on('click',function(){
            cmrsdk.sendCommonSMS({receiver:"15067152679",content:"测试短信发送"});
        });
        $("#startMMClient").on('click',function(){
            cmrsdk.startMMClient({URL:"http://wap.cmread.com/",callerid:"xxx",phone:"15067152679"});
        });

    },
    init: function () {
        $('body').append('<input type="text" style="width:100%;height:40px;border:1px solid #eee;" value="vipshop://showSpecial?tra_from=tra%3Ady7wh7ma%3A%3A%3A%3A&cm=M801006M&sid=ja5vde" class="addUrl" />');
        this.createItem();
        this.bindEvents();
        new nav({
            className:".test-title",
            wrap:".nav-ul",
            item:".nav-a",
            onelen:"100"
        });
    }
}
function nav(conf){
    this.conf=conf;
    this.init.apply(this);
}
nav.prototype={
    bindEvents:function(){
        var cn=this.conf.item,
            wrap=this.conf.wrap,
            self=this;
        $(cn).on("click",function(){
            var index=$(this).parent().index();
            var target=$(this).attr('data-target'),
            sctop=$("#"+target).offset().top-40;
            setTimeout(function(){self.chooseX(index)},0);
            $(window).scrollTop(sctop);
        });
        var winhei=$(window).height();
        $(window).scroll(function(){
            var sctop=$(window).scrollTop();
            self.conf.heights.forEach(function(v,k){
                if(sctop>v-50){
                    self.chooseX(k);
                }
            });
            if($('body').height()-winhei==sctop){
                self.chooseX(self.conf.len-1);
            }
        });
        $(".cmr-rightnav .right-item").on('tap',function(){
            var data=$(this).attr('data-val');
            $(".cmr-rightnav .right-item").removeClass('active');
            $(this).addClass('active');
            if(data=="+"){
                $('.global_item').css('display','block');
            }else{
                $('.global_item').css('display','none');
                $('.'+data+"_item").css('display','block');
            }

        });

    },
    chooseX:function(index){
         var cn=this.conf.item,
            wrap=this.conf.wrap,
            self=this;
        $(cn).removeClass('active');
        $(cn).eq(index).addClass('active');
        $(wrap).parent().scrollLeft(self.conf.onelen*(index-1));
    },
    /*刷新按钮*/
    addRefresh:function(){
        var str='<a class="cmr-refresh" onclick="cmrsdk.refresh()" href="javascript:;">刷新</a>';
        $('body').append(str);
    },
    addRightNav:function(){
        var Letters=['+','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'];
        var str='<nav class="cmr-rightnav">';
        Letters.forEach(function(v,k){
            str+='<a href="javascript:;" data-val="'+v+'" class="right-item">'+v+'</a>'
        });
        str+='</nav>';
        $('body').append(str);
    },
    addStyle:function(){
        var style='<style>.cmr-refresh{width:50px;height:50px;background:#04be02;color:#fff;position:fixed;right:30px;bottom:10px;line-height:50px;border-radius:50%;box-shadow:0 0 15px #999;text-align:center;font-size:12px;z-index:98}.cmr-rightnav{height:100%;width:20px;background:rgba(255,255,255,0.8);position:fixed;right:0;bottom:0;z-index:99;}.right-item{height:20px;line-height:20px;text-align:center;color:#333;display:block;border:1px solid #eaeaea;border-bottom:0;}.right-item:last-child{border-bottom:1px solid #eaeaea}.right-item.active{background:#04be02;color:#fff;}</style>';
        $('head').append(style);
    },
    getInitNav:function(){
        var cn=this.conf.className,
            wp=this.conf.wrap,
            self=this;
        this.conf.len=$(cn).size();
        var str='';
        this.conf.heights=[];
        $(cn).forEach(function(v,k){
            var target=$(v).attr('id'),
                name=$(v).attr('data-title');
            if(target==""){
                return;
            }
            str+='<li class="nav-item">\
                <a data-target="'+target+'" href="javascript:void(0);" class="nav-a">'+name+'</a>\
            </li>';
            self.conf.heights.push($("#"+target).offset().top);
        });
        $(wp).width(this.conf.len*this.conf.onelen).html(str);
        $(self.conf.item).eq(0).addClass('active');
    },
    init:function(){
        this.addStyle();
        this.getInitNav();
        this.addRightNav();
        this.bindEvents();
        this.addRefresh();
    }
}
fun.init();
var ua = CmreadJsBridge.GetSys();
$("#UA")[0].value = navigator.userAgent;
var version = CmreadJsBridge.GetAppVersion();
$("#version")[0].value = version;
function setClientValue(key,value){
    cmrsdk.toast("setClientValue测试:"+key+",value:"+value);
};
function c2b_setBindNumberState(key,value){
     cmrsdk.toast("c2b_setBindNumberState:"+key+",value:"+value)
};
function pushRes(key,value){
    cmrsdk.toast("pushRes:"+key+",value:"+value)
};
function nzmAutoEnter(){
    cmrsdk.toast("nzmAutoEnter:"+key+",value:"+value)
}
