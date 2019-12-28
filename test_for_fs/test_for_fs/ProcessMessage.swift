//
//  ProcessMessage.swift
//  test_for_fs
//
//  Created by qianS on 2019/12/16.
//  Copyright © 2019 qians. All rights reserved.
//

import Foundation

class ProcessMessage: NSObject {
    let doCmd = ProcessCommand()
    
    let id = String(UserDefaults.standard.integer(forKey: "id"))
    
    func join(friendID: String) -> Void {
        doCmd.doJoin(friendID)
    }
    func getMsg(jsonMsg: NSDictionary){
        
        let cmd: Int = jsonMsg["cmd"] as! Int
        let command: String = jsonMsg["command"] as! String
        let info: String = jsonMsg["info"] as! String
        let friendID: Int = jsonMsg["src"] as! Int
        let friendId: String = String(friendID)
        print("------------------------Process Command---------------------")
        print("cmd = \(command)")
        print("msg = \(jsonMsg)")
        
        switch cmd {//should add MAKE_CALL cmd
            case StWsMessage.Command.SEND_DATA.rawValue:
                print("get SEND_DATA command")
                print(info)
                break
            case StWsMessage.Command.ECHO.rawValue:
                print("get ECHO command")
                print(jsonMsg)
                break
            case StWsMessage.Command.MAKE_CALL.rawValue:
                print("get MAKE_CALL command")
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
                break
            case StWsMessage.Command.CALL_BYE.rawValue:
                print("get CALL_BYE command")
                break
            case StWsMessage.Command.CALL_CANDIDATE.rawValue:
                print("get CALL_CANDIDATE command")
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
        doCmd.doMakeCallCmd(friendID)
    }
    func applyAddDevice(info: String) -> Void {
        print("process add device apply")
        
        doCmd.doApplyAddCmd(info)
        print("--------------------------------------------")
        print(info)
        print("--------------------------------------------")
    }
    
    func doAcceptCall(friendID: String, user: String) -> Void {
        print("process do call apply")
//        let isCouldCall: Bool = UserDefaults.standard.bool(forKey: "isCouldCall")
//        print(isCouldCall)
//        if (isCouldCall){
//            let defaults = UserDefaults.standard
            //defaults.set(false, forKey: "isCouldCall")
            let doCmd = ProcessCommand()
            doCmd.doAcceptCallCmd(friendID, userID: user)
            
//        }else{
//            doCmd.doFull()
//        }
    }
    
    func doCallOffer(sdp: String) -> Void {
        print("process do send offer apply")
        doCmd.doCallOfferCmd(sdp)
        print("----------------------offer----------------------")
        print(sdp)
        print("-----------------------------------------------")
    }
    
    func doCallAnswer(sdp: String) -> Void {
        print("process do send offer apply")
        doCmd.doCallAnswerCmd(sdp)
        print("----------------------answer----------------------")
        print(sdp)
        print("-----------------------------------------------")
    }
    
    func doCallCandidate(info: String) -> Void {
        print("process do send candidate apply")
        doCmd.doCallAnswerCmd(info)
        print("----------------------candidate----------------------")
        print(info)
        print("-----------------------------------------------")
    }
}
