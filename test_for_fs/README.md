# README for webrtc demo
---------------------------------------------------
- 盛乾(Qian Sheng), qian.sheng@51feisuo.com
- Datae: 2019-11-6

# 环境
1. 首先升级macOS版本至最新版(macOS Catalina)
2. 安装最新版Xcode
3. 在macOS与Xcode中登陆Apple账号(两者须与手机中登陆的App Store账号匹配)
4. 将手机连接至mac
# test_for_fs(ios)
## 配置运行
1. cd至文件夹下
2. 执行命令行
```
    $ pod install
```
3. 从.xcworkspace文件进入Xcode
4. 打开左侧Navigator，选中第一个project navigator(文件夹样式图标📁)，单击test_for_fs
5. 选定TARGETS -> test_for_fs -> Signing & Capabilities
6. 随意修改Bundle Identifier直至不报错
7. 勾选Automatically manage signing，在Team一栏选取Team
8. 在Xcode左上角的设备中选择自己的手机(webRTC > Generic IOS Device)
9. 运行工程
## 媒体协商流程
1. 发起通话：MAKE_CALL(REQUEST)  | join(deledate)
2. 接受通话：MAKE_CALL(ALLOW) -> ACCEPT_CALL(request) | otherjoin(deledate) -> setLocalOffer -> CALL_OFFER(REQUEST)
3. 发送Answer：CALL_OFFER(ALLOW) -> CALL_ANSWER(REQUEST)
4. 发送Candidate：CALL_ANSWER(ALLOW) -> CALL_CANDIDATE(REQUSET)
- 每次在收到远端发送来的offer或者answer时都会触发didGenerateIceCandidate方法，该方法会执行[peerConnection addIceCandidate:candidate]方法，将收到的candidate添加进peerConnection中，该动作会在整个媒体协商过程中多次异步调用
## CallView执行顺序
1. init
2. viewDidLoad
3. createFactory
4. caputerLocalMedia
5. join(otherjoin)
6. create PeerConnection
7. 开始媒体协商流程
## RTC注意事项
1. createPeerConnection时如果设置不对，会返回nil。注意IceServer的设置，如果TURN服务器有密码请在初始化时将账户密码也写入。有可能会因为STUN服务器没有配置证书而不能正确初始化peerConnection，可以尝试设置在初始化IceServer时`tlsCertPolicy:RTCTlsCertPolicyInsecureNoCheck`
## TODO
1. 每次收到一个Request时应该回复一个Allow或者Deny
2. 实现信令中的leave和full
3. 页面布局更改值导航栏形式：friendsList(login -> friends)，addFriendsApplyList，callApplyList，user(userInfo, log out, sign in, sign out)
4. 未完成功能实现(sign out，log out)
5. 清理代码，去除测试用代码，和一些无用的参数
6. 优化关于服务器传来消息的读取
7. 实现与Android端互联
## iPhone 手机配置
1. 打开设置 -> 通用 -> 描述文件和设备管理 -> 开发者APP 选择信任
2. 再次运行工程(或在手机桌面打开APP)
### 相关问题解决(ios)：
1. 使用 'pod install' 出现错误：
```
-bash: /usr/local/bin/pod: /System/Library/Frameworks/Ruby.framework/Versions/2.0/usr/bin/ruby: bad interpreter: No such file or directory
```
2. 升级macOS版本时，忘记更新cocoapods导致。
执行命令：
```
    $ sudo gem update --system
    $ sudo gem install cocoapods -n/usr/local/bin
```
## 问题记录
1. 像AFManager，OpenStomp， ProcessMessage这种网络类，和会有delegate的处理类，应该保证始终持有的是一个且同一个，不然会导致网络断开、delegate调用不正确等问题
2. 页面间传参可使用delegate，方便
3. 从服务器获取的数据，注意查看数据类型，例如id的数据类型可能为NSNumber类型
4. AFNet中的Url应该使用“http://...”而不是“ws://...”，这2个是不同的协议，不应该混用
5. 关于低版本SceneDelegate问题，可在AppDelegate.m中写入`@synthesize window = _window;`解决
# Reference
1. https://juejin.im/post/5d25ab155188250a5d3c54e4
2. https://juejin.im/post/5cd2964c51882540a019c3e8
3. http://www.cocoachina.com/articles/18837
4. https://juejin.im/post/5d26ce8ae51d45777540fe3f
5. https://juejin.im/post/5d26dc78f265da1bae392509
6. https://juejin.im/post/5d26dd79f265da1bb13f5c28
7. https://juejin.im/post/5ccfe977e51d456e2a64b362
8. https://juejin.im/post/5dab1a8cf265da5bbd71cc01
9. https://rtcdeveloper.com/t/topic/435
10. https://webrtc.org.cn/how-webrtc-works-1/
11. https://webrtc.org.cn/video-process/
12. https://webrtc.org.cn/getusermedia-video-constraints/
13. https://juejin.im/post/5c12022b5188257e2a7b4a4c
14. https://juejin.im/post/5b3038ed6fb9a00e9d1b60cf
15. https://juejin.im/post/5c0f7c16f265da616d540880
16. https://juejin.im/post/5daec5b6f265da5b932e7f7e
