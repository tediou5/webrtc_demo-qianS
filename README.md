1> 环境
  1. 首先升级macOS版本至最新版(macOS Catalina)
  2. 在Apple developer官网下载Xcode最新版(11.2beta)
  3. 在macOS与Xcode中登陆Apple账号(两者须与手机中登陆的账号匹配)
  4. 将手机连接至mac
  ############可选############
  5. 将手机连接至Wi-Fi: op3800-2.4
  ###########################
  
2> Node.js
  1. 安装Node.js及npm环境
  2. cd至SkyRTC-demo-master文件夹下
  3. npm install安装相关库
  4. node server.js开启服务器
  5. 网页访问'localhost:3000#100',进入房间100(IOS端默认房间)
  
3> 工程
  1. cd至WebRTC_two文件夹下
  2. pod install安装相关库文件
  3. 从WebRTC_two.xcworkspace文件进入xcode
  4. 打开左侧Navigator，选中第一个project navigator(文件夹样式图标📁)，单击WebRTC_two
  5. 选定TARGETS -> WebRTC-two -> General -> Identity -> Bundle Identifier(安全码)
  6. 随意修改Bundle Identifier直至不报错
  7. 选定TARGETS -> WebRTC-two -> Signing & Capabilities
  8. 勾选Auto....，在Team一栏选取(Personal Team)
  9. 在Xcode左上角的设备中选择自己的手机(Generic IOS Device)
  10. 修改ChatViewController.m第199行connectServer，更改为本地IP
  11. 同样修改FriendListViewController.m第52行，ViewController.m第49行
  12. 运行工程
  
4> 手机
  1. 打开设置 -> 通用 -> 描述文件和设备管理 -> 开发者APP 选择信任
  2. 再次运行工程(或在手机桌面打开APP)
  3. 如果遇到显示本地流白屏，则点选挂断重新进入即可
  ---------------------------------------------------
  盛乾(Qian Sheng)
  qian.sheng@51feisuo.com
  2019-10-29

