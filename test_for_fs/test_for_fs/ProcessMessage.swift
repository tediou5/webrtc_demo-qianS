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
    
    func getMsg(pCmd: ProcessCommand, jsonMsg: NSDictionary){
        
        let cmd: Int = jsonMsg["cmd"] as! Int
        //let command: String = jsonMsg["command"] as! String
        let info: String = jsonMsg["info"] as! String
        let friendID: Int = jsonMsg["src"] as! Int
        let friendId: String = String(friendID)
        
        switch cmd {//should add MAKE_CALL cmd
            case StWsMessage.Command.SEND_DATA.rawValue:
                print("get SEND_DATA command")
                print(info)
                break
            case StWsMessage.Command.ECHO.rawValue:
                //print("get ECHO command")
                //print(jsonMsg)
                break
            case StWsMessage.Command.MAKE_CALL.rawValue:
                print("get MAKE_CALL command")
                doMakeCall(pCmd: pCmd, friendID: friendId)
                break
            case StWsMessage.Command.ACCEPT_CALL.rawValue:
                print("get ACCEPT_CALL command")
                doAcceptCall(pCmd: pCmd, friendID: friendId, user: id)
                break
            case StWsMessage.Command.APPLY_ADD_DEVICE.rawValue:
                print("get APPLY_ADD_DEVICE command")
                applyAddDevice(pCmd: pCmd, info: info)
                break
            case StWsMessage.Command.CALL_ANSWER.rawValue:
                print("get CALL_ANSWER command")
                doCallAnswer(pCmd: pCmd, sdp: info)
                break
            case StWsMessage.Command.CALL_BYE.rawValue:
                print("get CALL_BYE command")
                break
            case StWsMessage.Command.CALL_CANDIDATE.rawValue:
                print("get CALL_CANDIDATE command")
                doCallCandidate(pCmd: pCmd, info: info)
                break
            case StWsMessage.Command.CALL_CANDIDATE_RM.rawValue:
                print("get CALL_CANDIDATE_RM command")
                break
            case StWsMessage.Command.CALL_OFFER.rawValue:
                print("get CALL_OFFER command")
                doCallOffer(pCmd: pCmd, sdp: info)
                break
            case StWsMessage.Command.HANDUP_CALL.rawValue:
                print("get HANDUP_CALL command")
                break
            default:
                print("error")
        }
    }
    func doMakeCall(pCmd: ProcessCommand, friendID: String) -> Void {
        pCmd.doMakeCallCmd(pCmd, friendID: friendID)
    }
    func applyAddDevice(pCmd: ProcessCommand, info: String) -> Void {
        print("process add device apply")
        pCmd.doApplyAddCmd(info)
    }
    
    func doAcceptCall(pCmd: ProcessCommand, friendID: String, user: String) -> Void {
        print("process do call apply")
//        let isCouldCall: Bool = UserDefaults.standard.bool(forKey: "isCouldCall")
//        print(isCouldCall)
//        if (isCouldCall){
//            let defaults = UserDefaults.standard
            //defaults.set(false, forKey: "isCouldCall")
        pCmd.doAcceptCallCmd(pCmd, friendID: friendID, userID: user)
            
//        }else{
//            doCmd.doFull()
//        }
    }
    
    func doCallOffer(pCmd: ProcessCommand, sdp: String) -> Void {
        print("process do send offer apply")
        pCmd.doCallOfferCmd(pCmd, sdp: sdp)
//        print("----------------------offer----------------------")
//        print(sdp)
//        print("-----------------------------------------------")
    }
    
    func doCallAnswer(pCmd: ProcessCommand, sdp: String) -> Void {
        print("process do send answer apply")
        pCmd.doCallAnswerCmd(pCmd, sdp: sdp)
//        print("----------------------answer----------------------")
//        print(sdp)
//        print("-----------------------------------------------")
    }
    
    func doCallCandidate(pCmd: ProcessCommand, info: String) -> Void {
        print("process do send candidate apply")
        pCmd.doCallCandidateCmd(pCmd, info: info)
//        print("----------------------candidate----------------------")
//        print(info)
//        print("-----------------------------------------------")
    }
}
