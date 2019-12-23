//
//  OpenStomp.swift
//  test_for_fs
//
//  Created by qians on 2019/11/28.
//  Copyright © 2019 qians. All rights reserved.
//

import Foundation
import StompClientLib

//import "ProcessCommand.h"


class OpenStomp: NSObject, StompClientLibDelegate{

    var id = ""
    //var friendId = ""
    
    var socketClient = StompClientLib()
    var baseTopic = "/u/"
    var topic = ""
    var token = "[user_ws_token] TODO: request from server when login;"
    
    //let url = NSURL(string: "http://localhost:9001")!
    let url = NSURL(string: "ws://192.168.11.123:9001/ep-st-websocket/websocket")!
    //let url = NSURL(string: "ws://192.168.0.105:9001/ep-st-websocket/websocket")
    //let url = NSURL(string: "ws://localhost:9001/ep-st-websocket/websocket")
    //var url = NSURL(string: "http://server.teclub.cn:8080/ep-st-websocket/websocket")!
    //var url = NSURL(string: "ws://192.168.11.123:8080/im/websocket")!
    
    
    @objc func registerSocket() -> Void{
        print("webSocket is Connection:\(url)")
        if (socketClient.isConnected()){
            DisConnect()
            socketClient.reconnect(request: NSURLRequest(url: url as! URL) , delegate: self as StompClientLibDelegate)
            print("reconnect")
        }
        else{
            socketClient.openSocketWithURLRequest(request: NSURLRequest(url: url as! URL) , delegate: self as StompClientLibDelegate)
        }
        print("i can do something over here")
    }
    
    @objc public func sendECHO() -> Void{
        if (socketClient.isConnected()){
            let stompHeaders:[String: String] = ["content-type": "application/json", "auth": token]
            let mess = StWsMessage(ssid: 0, cmd: StWsMessage.Command.ECHO, type: StWsMessage.stType.Request, info: "ECHO Message", from: id, to: "1").toString()
            socketClient.sendMessage(message: mess, toDestination: "/app/server", withHeaders: stompHeaders, withReceipt: nil)
        }
        else {
            print("Please Connect Websocket First!")
        }
    }
    
    @objc public func sendoffer(sdp: String, friendId: String) -> Void{
        if (socketClient.isConnected()){
            let stompHeaders:[String: String] = ["content-type": "application/json", "auth": token]
            let mess = StWsMessage(ssid: 0, cmd: StWsMessage.Command.CALL_OFFER, type: StWsMessage.stType.Request, info: "Send Offer", from: id, to: friendId).toString()
            socketClient.sendMessage(message: mess, toDestination: "/app/server", withHeaders: stompHeaders, withReceipt: nil)
        }
        else {
            print("Please Connect Websocket First!")
        }
    }
    
    func stompClientDidConnect(client: StompClientLib!) {
        let ID = UserDefaults.standard.integer(forKey: "id")
        print("ID = \(ID)")
        id = String(ID)
        
        topic = baseTopic + id
        
        print("Socket is Connected : \(topic)")
        socketClient.subscribe(destination: topic)
        //if (socketClient.isProxy()){
        //    print("Socket is Proxy")
        //}
        // Auto Disconnect after 3 sec
        //socketClient.autoDisconnect(time: 3)
        // Reconnect after 4 sec
        //print(url)
        if (!socketClient.isConnected()){
            socketClient.reconnect(request: NSURLRequest(url: url as! URL) , delegate: self as StompClientLibDelegate, time: 4.0)
        }
        else {
            print("is connected")
        }
    }
    
    @objc public func DisConnect() -> Void{
        socketClient.disconnect()
        if (!socketClient.isConnected()) {
            print("已断开！")
        }
    }
    
    @objc public func IsConnected() -> Void{
        if(socketClient.isConnected()){
            print("stomp is connected!")
        }else{
            print("ERROR: please connect again!")
        }
    }
    
    func stompClientDidDisconnect(client: StompClientLib!) {
        print("Socket is Disconnected")
    }
    
    func stompClient(client: StompClientLib!, didReceiveMessageWithJSONBody jsonBody: AnyObject?, withHeader header: [String : String]?, withDestination destination: String) {
        print("DESTIONATION : \(destination)")
        print("I should Process JSON message at here")
        
        //let jsonData:Data = jsonBody?.anyObject(using: .utf8)!
        
        let pMsg = ProcessMessage()
        let jsonMsg = jsonBody as? NSDictionary
        //print("cmd = \(String(describing: jsonMsg?["cmd"]))")
        //print("command = \(String(describing: jsonMsg?["command"]))")
        pMsg.getMsg(jsonMsg: jsonMsg!)
        //print("JSON BODY : \(String(describing: jsonBody))")
    }
    
    func stompClient(client: StompClientLib!, didReceiveMessageWithJSONBody jsonBody: AnyObject?, akaStringBody stringBody: String?, withHeader header: [String : String]?, withDestination destination: String) {
        print("DESTIONATION : \(destination)")
        print("JSON BODY : \(String(describing: jsonBody))")
        print("STRING BODY : \(stringBody ?? "nil")")
    }
    
    func stompClientJSONBody(client: StompClientLib!, didReceiveMessageWithJSONBody jsonBody: String?, withHeader header: [String : String]?, withDestination destination: String) {
        print("DESTIONATION : \(destination)")
        print("String JSON BODY : \(String(describing: jsonBody))")
    }
    
    func serverDidSendReceipt(client: StompClientLib!, withReceiptId receiptId: String) {
        print("Receipt : \(receiptId)")
    }
    
    func serverDidSendError(client: StompClientLib!, withErrorMessage description: String, detailedErrorMessage message: String?) {
        print("Error : \(String(describing: message))")
    }
    
    func serverDidSendPing() {
        print("Server Ping")
    }
}
