'use strict'

var http = require('http');
var os = require('os');
var nodeStatic = require('node-static');
var USERCOUNT = 3;
//socket.io
var socketIO = require('socket.io');

var fileServer = new(nodeStatic.Server)();
var app = http.createServer(function(req, res) {
  fileServer.serve(req, res);
}).listen(8080);

var io = socketIO.listen(app);

//connection
io.sockets.on('connection', (socket)=> {

    socket.on('message', (room, data)=>{
        socket.to(room).emit('message',room, data);
    });

    socket.on('join', (room)=>{
        socket.join(room);
        var myRoom = io.sockets.adapter.rooms[room];
        var users = (myRoom)? Object.keys(myRoom.sockets).length : 0;
        logger.debug('the user number of room is: ' + users);

        if(users < USERCOUNT){
            socket.emit('joined', room, socket.id); //发给除自己之外的房间内的所有人
            if(users > 1){
                socket.to(room).emit('otherjoin', room, socket.id);
            }
        
        }else{
            socket.leave(room);
            socket.emit('full', room, socket.id);
        }
        //socket.emit('joined', room, socket.id); //发给自己
        //socket.broadcast.emit('joined', room, socket.id); //发给除自己之外的这个节点上的所有人
        //io.in(room).emit('joined', room, socket.id); //发给房间内的所有人
    });

    socket.on('leave', (room)=>{
        var myRoom = io.sockets.adapter.rooms[room];
        var users = (myRoom)? Object.keys(myRoom.sockets).length : 0;
        logger.debug('the user number of room is: ' + (users-1));
        //socket.emit('leaved', room, socket.id);
        //socket.broadcast.emit('leaved', room, socket.id);
        socket.to(room).emit('bye', room, socket.id);
        socket.emit('leaved', room, socket.id);
        //io.in(room).emit('leaved', room, socket.id);
    });

    socket.on('ipaddr', function() {
        var ifaces = os.networkInterfaces();
        for (var dev in ifaces) {
            ifaces[dev].forEach(function(details) {
              if (details.family === 'IPv4' && details.address !== '127.0.0.1') {
                socket.emit('ipaddr', details.address);
              }
           });
        }
    });
});





