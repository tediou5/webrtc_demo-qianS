# 环境
  1. 首先升级macOS版本至最新版(macOS Catalina)
  2. 在Apple developer官网下载Xcode最新版(11.2beta)
  3. 在macOS与Xcode中登陆Apple账号(两者须与手机中登陆的账号匹配)
  4. 将手机连接至mac
  ############可选############
  5. 将手机连接至Wi-Fi: op3800-2.4
  ###########################
  
  # Node.js
  1. 安装Node.js及npm环境
  2. cd至SkyRTC-demo-master文件夹下
  3. npm install安装相关库
  4. node server.js开启服务器
  5. 网页访问'localhost:3000#100',进入房间100(IOS端默认房间)
  
  # 工程
  1. cd至WebRTC_new文件夹下
  2. pod install安装相关库文件
  3. 从WebRTC_new.xcworkspace文件进入xcode
  4. 打开左侧Navigator，选中第一个project navigator(文件夹样式图标📁)，单击WebRTC_new
  5. 选定TARGETS -> WebRTC-new -> General -> Identity -> Bundle Identifier(安全码)
  6. 随意修改Bundle Identifier直至不报错
  7. 选定TARGETS -> WebRTC-new -> Signing & Capabilities
  8. 勾选Auto....，在Team一栏选取(Personal Team)
  9. 在Xcode左上角的设备中选择自己的手机(Generic IOS Device)
  10. 修改ChatViewController.m第199行connectServer，更改为本地IP
  11. 同样修改FriendListViewController.m第52行，ViewController.m第49行
  12. 运行工程
  
  # 手机
  1. 打开设置 -> 通用 -> 描述文件和设备管理 -> 开发者APP 选择信任
  2. 再次运行工程(或在手机桌面打开APP)
  3. 如果遇到显示本地流白屏，则点选挂断重新进入即可
  
  # step-05
  1. 本DEMO为webRTC codeLab step-05
  2. cd至step-05文件夹下
  3. npm install安装Node.js相关库
  4. node index.js启动服务器
  5. 浏览器输入‘localhost:8080’打开网页
  6. 如无法打开摄像头请参照相关问题解决（2）
  
  ###########################
  相关问题解决：
  1. 使用pod install出现-bash: /usr/local/bin/pod: /System/Library/Frameworks/Ruby.framework/Versions/2.0/usr/bin/ruby: bad interpreter: No such file or directory
  这是Mac升级系统导致，当你的Mac系统升级为 high siera的时候，别忘记更新cocoapods。
  执行命令：
  $ sudo gem update --system
  $ sudo gem install cocoapods -n/usr/local/bin
  
  2. 网页无法调用摄像头
  这是由于使用http协议，浏览器认为不安全导致。可以将网页加入白名单来解决。
  步骤：
  在chrome地址栏输入：chrome://flags/#unsafely-treat-insecure-origin-as-secure
  将Insecure origins treated as secure一栏改为Enable，并在框内输入：http://192.168.31.216:8080（op3800-2.4）
  重启浏览器重新打开网页即可
  
  ---------------------------------------------------
  盛乾(Qian Sheng)
  qian.sheng@51feisuo.com
  2019-11-1

