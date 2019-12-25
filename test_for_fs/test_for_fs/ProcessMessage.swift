//
//  ProcessMessage.swift
//  test_for_fs
//
//  Created by qianS on 2019/12/16.
//  Copyright © 2019 qians. All rights reserved.
//

import Foundation

class ProcessMessage: NSObject {
    func getMsg(jsonMsg: NSDictionary){
        
        let cmd: Int = jsonMsg["cmd"] as! Int
        let command: String = jsonMsg["command"] as! String
        let info: String = jsonMsg["info"] as! String
        print("------------------------Process Command---------------------")
        print("cmd = \(command)")
        
        switch cmd {
            case StWsMessage.Command.ECHO.rawValue:
                print("get ECHO command")
                break
            case StWsMessage.Command.ACCEPT_CALL.rawValue:
                print("get ACCEPT_CALL command")
                break
            case StWsMessage.Command.APPLY_ADD_DEVICE.rawValue:
                print("get APPLY_ADD_DEVICE command")
                applyAddDevice(info: info);
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
                break
            case StWsMessage.Command.HANDUP_CALL.rawValue:
                print("get HANDUP_CALL command")
                break
            default:
                print("error")
        }
    }
    
    func applyAddDevice(info: String) -> Void {
        print("process add device apply")
        let doCmd = ProcessCommand()
        doCmd.doApplyAddCmd(info)
        print("--------------------------------------------")
        print(info)
        print("--------------------------------------------")
    }

}
