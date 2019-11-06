## 音视频采集API
1. 基本格式
var promise = navigator.mediaDevices.getUserMedia(constraints)

2. MediaStreamConstraints
dictonary MediaStreamConstraints {
	(boolean or MediaTrackConstraints) video = false;
	(boolean or MediaTrackConstraints) audio = false;
}

## adapter.js
1. 用处
用于适配不同浏览器的getUserMedia
2. 使用
https://webrtc.github.io/adapter/adapter-latest.js

## 视频采集约束
1. width                :宽
2. height               :高
3. aspectRatio      :比例 -> width/height（4:3 -> 1.333）
4. frameRate         :帧率
5. facingMode
    1> user             :前置摄像头
    2>environment :后置摄像头
    3>left                :前置左摄像头
    4>right              :前置右摄像头
6. resizeMode       :裁剪

## 音频采集约束

1. volume                  :音量(0 - 1.0)
2. sampleRate           :采样率
3. sampleSize           :采样大小(位声)
4. echoCancellation  :回音消除
5. autoGainControl   :自动增益
6. noiseSuppression :降噪
7. latency                  :延迟
8. channelCount       :单双声道
9. devicesID              :切换设备
10. groupID               :同一物理设备

## MediaStream获取视频约束

1. MediaStream.addTrack()           :添加轨
2. MediaStream.removeTrack()     : 移除轨
3. MeidaStream.getVideoTracks() :获取视频轨
4. MediaStream.getAudioTracks() :获取音频轨道
5. MediaStream.stop()                   :关闭stram
### MediaStream事件
    1. MediaStream.onaddtrack
    2. MediaStream.onremovetrack
    3. MediaStream.onended

## MediaRecorder媒体流录制
1. 基本格式
var mediaRecorder = new MediaRecorder(stream,options);

2. 参数说明
    1> stream
    2> options :限制选项
                       1. mimeType :录制格式
                                             video/mp4
                                             video/webm
                                             audio/webm
                                             video/webm;codecs=vp8
                                             video/webm;codecs=h264
                                             audio/webm;codecs=opus
                       2. audioBitsPerSecond :音频码率
                       3. videoBitsPerSecond :视频码率
                       4. bitsPerSecond          :整体码率

### MediaRecorder API
    1. MediaRecorder.start(timeslice)  :开始录制流媒体，timeslice是可选的，如果设置了会按时间片存储数据
    2. MediaRecorder.stop()            :停止录制，会触发包括最终Blob数据的dataavailable事件
    3. MediaRecorder.pause()           :暂停
    4. MediaRecorder.resume()          :恢复
    5. MediaRecorder.isTypeSupported() :检查格式是否支持

### MediaRecorder 事件
    1. MediaRecorder.ondataavailable :当数据有效时触发,每次记录一定时间数据（如未指定时间片，则记录整个数据时）会定期触发
    2. MediaRecorder.onerror :error是触发，录制会被停止
