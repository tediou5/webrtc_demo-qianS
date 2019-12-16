//
//  StWsMessage.swift
//  test_for_fs
//
//  Created by qians on 2019/12/11.
//  Copyright © 2019 qians. All rights reserved.
//
import Foundation

class StWsMessage: NSObject{
    
    static let ID_INVALID = -1;
    static let ID_SERVER  = 0;
    
    enum Command: Int{
        
        case NULL = 0x00
        case APPLY_ADD_DEVICE = 0x30
        case GRANT_ADD_DEVICE = 0x31
        case ECHO = 0x40
        case UPDATE_FRIEND = 0x41
        case MAKE_CALL = 0x42
        case ACCEPT_CALL = 0x43
        case HANDUP_CALL = 0x44
        case PING = 0x45
        case GET_CALL_INFO = 0x46
        case GET_DEV_INFO = 0x47
        case RECORD_CAPTURE = 0x48
        case REQ_MAX = 0x50

        case CALL_OFFER = 0x51
        case CALL_ANSWER = 0x52
        case CALL_CANDIDATE = 0x53
        case CALL_BYE = 0x54
        case CALL_CANDIDATE_RM = 0x55
        case SEND_DATA = 0x60

        case MAX = 0xFFFF
    }
    enum stType: String{
        case None = "None"
        case Request = "Request"
        case Allow = "Allow"
        case Deny = "Deny"
    }
    
    var ssid: Int
    var src: String
    var dst: String
    var len: Int
    var info: String
    
    // for debug & test
    var opt: String
    
    var cmd: Command
    var type: stType
    
//    init(AnyObject: msg){
//        
//    }
    
    init(ssid: Int, cmd: Command, type: stType, info: String, from: String, to: String) {
        self.ssid = ssid
        self.cmd = cmd
        self.type = type
        self.info = info
        self.src = from
        self.dst = to
        
        if info.isEmpty{
            self.info = ""
            self.len = 0
        }else{
            self.len = info.lengthOfBytes(using: String.Encoding.utf8)
        }
        self.opt = ""
        self.opt += "TODO"
    }

    convenience init(ssid: Int, info: String, from: String, to: String) {
        self.init(ssid: ssid, cmd: Command.NULL, type: stType.None, info: info, from: from, to: to)
    }
    
    convenience init(ssid: Int, cmd: Command, type: stType, info: String) {
        self.init(ssid: ssid, cmd: cmd, type: type, info: info, from: "0", to: "1")
    }
    
    func addNote(note: String) -> Void{
        self.opt += note
    }
    
    func toString() -> String {
        var dict: [String: Any] = [:]
        
        dict["ssid"] = ssid
        dict["cmd"]  = cmd.rawValue
        dict["type"] = type.rawValue
        dict["info"] = info
        dict["len"]  = len
        dict["opt"]  = opt
        dict["src"]  = src
        dict["dst"]  = dst
        do {
            let theJSONData = try JSONSerialization.data(withJSONObject: dict, options: [])
            let theJSONText = String(data: theJSONData, encoding: String.Encoding.utf8)
            
            return theJSONText!
        }catch {
            print("error serializing JSON: \(error)")
            return ""
        }
       
    }
    
    func getSsid() -> Int {
        return self.ssid
    }
    
    func setSsid(ssid: Int) -> Void {
        self.ssid = ssid
    }
    
    func getCmd() -> Command {
        return self.cmd
    }
    
    func setCmd(cmd: Command) -> Void {
        self.cmd = cmd
    }
    
    func getType() -> stType {
        return self.type
    }
    
    func setType(type: stType) -> Void {
        self.type = type
    }
    
    func getLen() -> Int {
        return self.len
    }
    
    func setLen(len: Int) -> Void {
        self.len = len
    }
    
    func getInfo() -> String {
        return self.info
    }
    
    func setInfo(info: String) -> Void {
        self.info = info
    }
    
    func getSrc() -> String {
        return self.src
    }
    
    func setSrc(src: String) -> Void {
        self.src = src
    }
    
    func getDst() -> String {
        return self.dst
    }
    
    func setDst(dst: String) -> Void {
        self.dst = dst
    }
    
    func getOpt() -> String {
        return self.opt
    }
    
    func setOpt(opt: String) -> Void {
        self.opt = opt
    }
}

