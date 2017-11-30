# AdvancedHybrid-Framework-Android

该项目适合那些H5和原生控件混合的Hybride应用，这类应用有一个特点，大部分页面通过前端H5页面实现，难免会遇到B页面和Native交互的场景，通常安卓端和IOS端页面和Native层间的交互使用的两套不同架构，前端开发同时需要维护两套JS交互框架，增加了代码的维护成本和可扩展性，故提出一个安卓和IOS统一的JS交互框架显得尤为必要，需要另辟蹊径，去寻找既安全，又能实现兼容Android和IOS各个版本的方案，JSBridge交互框架应运而生。

同时，该项目也对webview本身进行了重构和优化，包括代码层级的划分、webview生命周期的控制、性能和安全方面的提升以及对webview缓存策略的梳理。为了方便三方集成使用，项目中把webview、下拉刷新(https://github.com/liaohuqiu/android-Ultra-Pull-To-Refresh)、错误页面、超时处理、加载样式、页面地址过滤等通用逻辑封装在一个抽象Fragment里面，集成后只需集成对应Fragment，并实现对应接口回调即可，这样今后涉及到相关代码块可实现一处修改，多处地方均完成修改。

# 价值评估
（1）实现对扩展开放，对修改封闭，方便webview界面相关的功能扩展和集中管理，实现高内聚低耦合

（2）提升Webview的性能和安全

（3）梳理Webview缓存策略，实现基于HTTP标准协议的缓存策略

（4）为后续H5功能开发提供支持

（5）提供安全、可靠的H5和Native交互框架JSBridge，并支持带回传数据等四种交互类型

# 什么是JsBridge
全新的JSBridge框架替代了WebView的自带的JavascriptInterface接口注入，作为客户端本地java层和前端js层互通的框架，使得我们的开发更加灵活和安全。IOS端和Android按照约定好的URL格式使用同一套规则的B+C交互框架，极大减少维护成本，提高开发效率，同时安卓端避免了使用系统自带JavascriptInterface接口，根本上解决了4.0.x系统上WebView存在的接口隐患与手机挂马利用的安全漏洞。

# JSBridge实践
（一）URL定义：

cmread://call/xxx?jsonData=xxx

cmread://fetch/xxx?retMethod=xxx&jsonData=xxx

cmread://listen/channel?key=xxx&jsonData=xxx&cleanType=1

cmread://broadcast/channel?key=xxx&retMethod=xxx&cleanType=1

（二）Fetch类型URL：前端调用客户端方法，客户端本地处理完成后把结果值返回给前端，返回值方法由前端指定，URL中包含返回方法和json数据，两者分开。
cmread://fetch/xxxx?retMethod=xxx&jsonData=xxxxxxxx

（三）对于Fetch类型URL的返回值数据，格式为JSON字符串，定义为：
{
      “code”:0,
      "message":null,
      "result":object
}
a. 客户端定义code的类型和意义
b. 如果 执行失败，result为null
c. result永远为对象，如果结果是bool，number等，也需转成对象

（四）Listen类型的URL，前端调用客户端方法，请求客户端存储数据，用于跨页面调用，需配合Broadcast类型的URL使用
示例：cmread://listen/channel?key=xxx&jsonData=xxx&cleanType=1
channel为常量字符串
cleanType定义
a. 0或者不传，追加 【按照加入顺序加入】
b. 1 清空原有的数据后，加入
c. 2 只清空，不加入

（五）Broadcast类型的URL。前端调用客户端方法，获取前端通过Listen类型方法存储在客户端本地的数据
示例：cmread://broadcast/channel?key=xxx&retMethod=xxx&cleanType=1
channel为常量字符串
cleanType的定义
a. 0或者不传，什么都不做
b. 1，发送broadcast后，清空数据

（六）Call类型的URL，前端调用客户端的方法，使用客户端提供的Native层能力

（七）编码
jsonData采用URLSAFE的Base64编码，对传输的数据进行编码加密，防止信息泄露。客户端提供一个样例字符串，并且给出编码后的字符串，用于算法比对

# 使用说明
1.可按需直接使用JSBridgeWebView或是AdvancedWebView类，JSBridgeWebView继承了AdvancedWebView，AdvancedWebView集成自系统webview控件。JSBridgeWebView封装了B+C交互的处理，而AdvancedWebView则对webview系统控件做了优化和扩展，提升安全和性能。

2.errorview 和 loadingview 可以自定义

3.Fragment的使用

public class CommonWebFragment extends WebFragment {
    @Override
    public void onPageStarted(String url, Bitmap favicon) {

    }

    @Override
    public void onPageFinished(String url) {

    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
        showErrorView();
    }

    @Override
    public void onExternalPageRequest(String url, boolean isHostNameForbiddon) {
        showErrorView();
    }

    @Override
    protected AdvancedWebView initWebView() {
        Activity act = getActivity();
        if (act != null) {
            return new CommonWebView(act);
        }
        return null;
    }

    @Override
    protected void configureWebView(AdvancedWebView webView) {
        if (null == webView) {
            return;
        }

        webView.addPermittedHostname("");   // 设置禁止的host地址
        webView.setVerticalScrollBarEnabled(false);
        setPullRefreshEnable(false, true);  // 是否禁止下拉刷新功能
    }

    @Override
    protected boolean overrideUrlLoading(WebView view, String url) {
        if (url.startsWith("http") || url.startsWith("https")) {
            loadUrl(url, false);
        } else {
            Activity activity = getActivity();
            if (activity != null && !activity.isFinishing()) {
                try {
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    activity.startActivity(intent);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }
}



