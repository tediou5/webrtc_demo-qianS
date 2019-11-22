# README for apprtc_server demo
---------------------------------------------------
- 盛乾(Qian Sheng), qian.sheng@51feisuo.com
- Date: 2019-11-22

# 环境
1. 本DEMO为Google appr.tc开源项目，请按照apprtc文件夹内README.md进行部署
2. 本DEMO在Ubuntu14.04中运行，请预装好Jre, node.js, python2, Grunt, go, google-cloud-sdk
3. 本DEMO中room_server使用8086端口，Collider使用8089端口，请确保这2个端口开放
4. GOPATH请设置在go源文件外否则可能导致权限问题
5. 本DEMO对apprtc/src/app_engine下apprtc.py的get_wss_parameters函数进行了更改，将HTTPS修改为HTTP格式

# apprtc_server
1. cd 至../apprtc_server/apprtc下命令行执行
```
    $ npm install
    $ pip install -r requirements.txt
    $ grunt build
    $ grunt runPythonTests[可选]
```
2. 按照apprtc/src/collider文件夹下README.md安装collider(信令服务器)
3. 在项目根目录下执行命令启动room_server和collider:
```
$ /home/qsheng/google_appengine/dev_appserver.py --host=0.0.0.0 --port=8086 --enable_host_checking=False ./out/app_engine
$ $GOPATH/bin/collidermain -port=8089 -tls=false 
```
4. 网页打开“http://52.82.101.16:8086”
### 相关问题解决：
网页无法调用摄像头

这是由于使用http协议，浏览器认为不安全导致。可以将网页加入白名单来解决。
步骤：
- 在chrome地址栏输入：chrome://flags/#unsafely-treat-insecure-origin-as-secure
- 将Insecure origins treated as secure一栏改为Enable，并在框内输入http://ip:port (http://192.168.31.216:8080)
- 重启浏览器重新打开网页即可
