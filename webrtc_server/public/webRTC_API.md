# RTCPeerConnection
## 基本格式
pc = new RTCPeerConnection([configuration]);

## 媒体协商
### createOffer
aPromise = myPeerConnection.createOffer([options]);

### createAnswer
aPromise = myPeerConnection.createAnswer([options]);

### setLocalDescription
aPromise = myPc.setLocalDescription(sessionDescription);

### setRemoteDescription
aPromise = myPc.setRemoteDescription(sessionDescription);

## Stream/Track
### addTrack
rtpSender = myPc.addTrack(Track,stream...);
- track: 添加到RTCPeerConnection中的媒体轨
- stream: 指定track所在的stram

### removeTrack
myPc.removeTrack(rtpSender);

### 重要事件
- onnegotiationneeded: 需要协商
- onicecandidate: 收到一个ICE候选者
- ontrack: 当远端数据来时触发，pc2.ontrack = getRemoteStream;
```
    function getRemoteStream(e){
        remoteVideo.srcObject = e.streams[0];
    }
```

## 传输相关方法


## 统计相关方法

# 注意事项
- 在进行协商之前必须先添加流，如果没有流，那么协商时只会进行底层的网络协商，不会接收流。
