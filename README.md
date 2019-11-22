# README for webrtc demo
---------------------------------------------------
- 盛乾(Qian Sheng), qian.sheng@51feisuo.com
- Datae: 2019-11-6

# 环境
1. 首先升级macOS版本至最新版(macOS Catalina)
2. 安装最新版Xcode
3. 在macOS与Xcode中登陆Apple账号(两者须与手机中登陆的App Store账号匹配)
4. 将手机连接至mac
5. webRTC_IOS请在IOS12.4及以下版本运行，否则会出现运行错误

# webrtc client (WEB端)
## apprtc_web
- 本DEMO为Google appr.tc开源项目，请按照文件夹内README.md进行部署
- 本DEMO在Ubuntu14.04中运行，请预装好Jre, node.js, python2, Grunt, go, google-cloud-sdk
- 本DEMO中room_server使用8086端口，Collider使用8089端口，请确保这2个端口开放
- GOPATH请设置在go源文件外否则可能导致权限问题
1. 安装node以及python相关库
2. grunt build编译项目
3. 设置GOPATH
4. 按照apprtc/src/collider文件夹下README.md安装collider(信令服务器)
5. 在项目根目录下执行命令启动room_server和collider:
```
$ /home/qsheng/google_appengine/dev_appserver.py --host=0.0.0.0 --port=8086 --enable_host_checking=False ./out/app_engine
$ $GOPATH/bin/collidermain -port=8089 -tls=false 
```
6. 执行
## webrtc_server
1. 安装Node.js及npm环境
2. cd至目标文件夹下命令行执行: 
```
    $ npm install
    $ node server.js
```
3. 网页访问'localhost:8080'
4. [可选]如在局域网内非本机可通过'http://ip:8080'访问网页
5. 点选videoChat -> index.html
6. 输入房间号，进入房间
7. 双方都进入房间后点选Connect Sig Server开始连接
## codeLab: step-05
- 本DEMO为webrtc codeLab step-05
1. 安装Node.js及npm环境
2. cd至目标文件夹下命令行执行: 
```
    $ npm install
    $ node index.js
```
3. 网页访问'localhost:8080'
### 相关问题解(web)：
网页无法调用摄像头

这是由于使用http协议，浏览器认为不安全导致。可以将网页加入白名单来解决。
步骤：
- 在chrome地址栏输入：chrome://flags/#unsafely-treat-insecure-origin-as-secure
- 将Insecure origins treated as secure一栏改为Enable，并在框内输入http://ip:port (http://192.168.31.216:8080)
- 重启浏览器重新打开网页即可

# webrtc client(ios端)
## webrtc_ios
1. cd至/webrtc_ios文件夹下
2. 执行命令行
```
    $ pod install
```
3. 从apprtc.xcworkspace文件进入Xcode
4. 打开左侧Navigator，选中第一个project navigator(文件夹样式图标📁)，单击webRTC
5. 选定TARGETS -> webRTC -> Signing & Capabilities
6. 随意修改Bundle Identifier直至不报错
7. 勾选Automatically manage signing，在Team一栏选取Team
8. 在Xcode左上角的设备中选择自己的手机(webRTC > Generic IOS Device)
9. 运行工程
## apprtc(ios)
1. cd至/apprtc文件夹下
2. 执行命令行
```
    $ pod install
```
3. 从apprtc.xcworkspace文件进入Xcode
4. 打开左侧Navigator，选中第一个project navigator(文件夹样式图标📁)，单击apprtc
5. 选定TARGETS -> apprtc -> Signing & Capabilities
6. 随意修改Bundle Identifier直至不报错
7. 勾选Automatically manage signing，在Team一栏选取Team
8. 在Xcode左上角的设备中选择自己的手机(apprtc > Generic IOS Device)
9. 运行工程
## iPhone 手机配置
1. 打开设置 -> 通用 -> 描述文件和设备管理 -> 开发者APP 选择信任
2. 再次运行工程(或在手机桌面打开APP)

### 相关问题解决(ios)：
使用 'pod install' 出现错误：
```
-bash: /usr/local/bin/pod: /System/Library/Frameworks/Ruby.framework/Versions/2.0/usr/bin/ruby: bad interpreter: No such file or directory
```

升级macOS版本时，忘记更新cocoapods导致。
执行命令：
```
    $ sudo gem update --system
    $ sudo gem install cocoapods -n/usr/local/bin
```



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
