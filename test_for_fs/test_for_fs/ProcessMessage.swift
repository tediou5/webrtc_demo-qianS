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
        
        //NSLog(@"test three");
        //NSMutableDictionary* WsMessage = [NSJSONSerialization JSONObjectWithData:jsonMsg options:NSJSONReadingMutableLeaves error:nil];
        //NSLog(@"test four");
        let cmd: Int = jsonMsg["cmd"] as! Int
        print("1")
        let command: String = jsonMsg["command"] as! String
        print("2")
        let src: NSNumber = jsonMsg["src"] as! NSNumber
        
        print("cmd = \(command)")
        
        //let wsCmd = StWsMessage.Command();
        
        switch cmd {
        case StWsMessage.Command.ACCEPT_CALL.rawValue:
            
            break
        case StWsMessage.Command.APPLY_ADD_DEVICE.rawValue:
            
            break
        case StWsMessage.Command.CALL_ANSWER.rawValue:
            
            break
        case StWsMessage.Command.CALL_BYE.rawValue:
            
            break
        case StWsMessage.Command.CALL_CANDIDATE.rawValue:
            
            break
        case StWsMessage.Command.CALL_CANDIDATE_RM.rawValue:
            
            break
        case StWsMessage.Command.CALL_OFFER.rawValue:
            
            break
        case StWsMessage.Command.HANDUP_CALL.rawValue:
            
            break
        default:
            print("error")
        }
    }

}
