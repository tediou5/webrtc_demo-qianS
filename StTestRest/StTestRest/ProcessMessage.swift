//
//  ProcessMessage.swift
//  test_for_fs
//
//  Created by qianS on 2019/12/16.
//  Copyright © 2019 qians. All rights reserved.
//

import Foundation

class ProcessMessage: NSObject {
    let id = String(UserDefaults.standard.integer(forKey: "id"))
    
    func receiveMsg(jsonMsg: NSDictionary){
        
        let ssid: Int = jsonMsg["ssid"] as! Int
        let cmd: Int = jsonMsg["cmd"] as! Int
        //let command: StWsMessage.Command = jsonMsg["command"] as! StWsMessage.Command
        let info: String = jsonMsg["info"] as! String
        let friendId: String = String(jsonMsg["src"] as! Int)
        //let friendId: String = String(friendID)
        //let stomp: OpenStomp
        
        switch cmd {//should add type.Allow cmd
            case StWsMessage.Command.SEND_DATA.rawValue:
                print("get SEND_DATA command")
                print(info)
                break
            case StWsMessage.Command.ECHO.rawValue:
                //print("get ECHO command")
                break
            case StWsMessage.Command.MAKE_CALL.rawValue:
                print("get MAKE_CALL command")
                print("============================AMKE_CALL============================")
                print(jsonMsg)
                print("=================================================================")
                //stomp.sendAllow(ssid: ssid, cmd: StWsMessage.Command.MAKE_CALL, info: info, friendId: friendId)
                doMakeCall(friendID: friendId)
                break
            case StWsMessage.Command.ACCEPT_CALL.rawValue:
                print("get ACCEPT_CALL command")
                doAcceptCall(friendID: friendId, user: id)
                break
            case StWsMessage.Command.APPLY_ADD_DEVICE.rawValue:
                print("get APPLY_ADD_DEVICE command")
                applyAddDevice(info: info)
                break
            case StWsMessage.Command.CALL_ANSWER.rawValue:
                print("get CALL_ANSWER command")
                doCallAnswer(sdp: info)
                break
            case StWsMessage.Command.CALL_BYE.rawValue:
                print("get CALL_BYE command")
                break
            case StWsMessage.Command.CALL_CANDIDATE.rawValue:
                print("get CALL_CANDIDATE command")
                doCallCandidate(info: info)
                break
            case StWsMessage.Command.CALL_CANDIDATE_RM.rawValue:
                print("get CALL_CANDIDATE_RM command")
                break
            case StWsMessage.Command.CALL_OFFER.rawValue:
                print("get CALL_OFFER command")
                doCallOffer(sdp: info)
                break
            case StWsMessage.Command.HANDUP_CALL.rawValue:
                print("get HANDUP_CALL command")
                break
            default:
                print("error")
        }
    }
    func doMakeCall(friendID: String) -> Void {
    }
    func applyAddDevice(info: String) -> Void {
        print("process add device apply")
    }
    
    func doAcceptCall(friendID: String, user: String) -> Void {
        print("process do call apply")
    }
    
    func doCallOffer(sdp: String) -> Void {
        print("process do send offer apply")
    }
    
    func doCallAnswer(sdp: String) -> Void {
        print("process do send answer apply")
    }
    
    func doCallCandidate(info: String) -> Void {
        print("process do send candidate apply")
    }
}
