# Connect Server
NSURL * url = [[NSURL alloc] initWithString:addr];
manager = [[SocketManager alloc]
		initWithSocketURL:url
				config:@{
					@"log":@YES,
					@"forcePolling":@YES,
					@"forceWebsockets":@YES
					}];
socket = manager.defaultSocket;

# send message
if(socket.status == SocketIOStatusConnected){
	[socket emit:@"join"with:@[room]];
}

# listen message
[socketon:@"connect"
	callback:^(NSArray* data, SocketAckEmitter* ack){
		NSLog(@"socket connected");
}];
