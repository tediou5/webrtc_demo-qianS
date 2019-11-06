# Socket.IO发送消息
### 给本次连接发送消息
socket.emit()

### 给某房间所有人发消息
io.in(room).emit()

### 除本连接外，给某房间内所有人发消息
socket.to(room).emit()

### 除本连接外，给所有人发消息
socket.broadcast.emit()

# Socket.IO客户端处理消息
## 发送action命令
    ### 无数据
    S: socket.emit('action');
    C: socket.on('action', function(){...});

    ### 有数据
    S: socket.emit('action', data);
    C: socket.on('action', function(data){...});
    
    ### 发送一个action命令，在emit方法中包含回掉函数
    S: socket.emit('action', data, function(arg1,arg2){...});
    C: socket.on('action', function(data,fn){fn('a','b');});
