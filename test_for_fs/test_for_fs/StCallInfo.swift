//
//  CallData.swift
//  test_for_fs
//
//  Created by qians on 2020/1/3.
//  Copyright © 2020 qians. All rights reserved.
//

import Foundation


class StCallInfo: NSObject {
    var uuid: String
    var incoming: Bool
    
    var clientA: StClientID
    var clientB: StClientID
    
    var devInfoA: StDevInfo
    var devInfoB: StDevInfo
    
    var msStart: CLong
    var sdp: RTCSessionDescription
    
    func getClientA() -> StClientID {
        return clientA
    }
    func getClientB() -> StClientID {
        return clientB
    }
    
    func getRemoteID() -> StClientID {
        if incoming {
            return clientA
        }else{
            return clientB
        }
    }
    func getRemoteDev() -> StDevInfo {
        if incoming {
            return devInfoA
        }else{
            return devInfoB
        }
    }
    func getLocalDev() -> StDevInfo {
        if incoming {
            return devInfoB
        }else{
            return devInfoA
        }
    }
    
    func getDevInfoA() -> StDevInfo {
        return devInfoA
    }
    func getDevInfoB() -> StDevInfo {
        return devInfoB
    }
    
    func setDevInfoA(DevInfoA: StDevInfo) -> Void {
        devInfoA = DevInfoA
    }
    func setDevInfoB(DevInfoB: StDevInfo) -> Void {
        devInfoB = DevInfoB
    }
    
    class CallDate: NSObject {
        var leastUUID: CLong
        var mostUUID: CLong
        var aID: CLong
        var bID: CLong
        var msStart: CLong
        var aDev: String
        var bDev: String
        
        var stateValue: Int
        
        override init(){}
        
        func getLeastUUID() -> CLong {
            return leastUUID
        }
        func setLeastUUID(LeastUUID: CLong) -> Void {
            leastUUID = LeastUUID
        }
        func getMostUUID() -> CLong {
            return mostUUID
        }
        func setMostUUID(MostUUID: CLong) -> Void {
            mostUUID = MostUUID
        }
        
        func getAID() -> CLong {
            return aID
        }
        func setAID(aId: CLong) -> Void {
            aID = aId
        }
        func getBID() -> CLong {
            return bID
        }
        func setBID(bId: CLong) -> Void {
            bID = bId
        }
        
        func getMsStart() -> CLong {
            return msStart
        }
        func setMsStart(MsStart: CLong) -> Void {
            msStart = MsStart
        }
        
        func getStateValue() -> Int {
            return stateValue
        }
        func setStateValue(StateValue: Int) -> Void {
            stateValue = StateValue
        }
        
        func getADev() -> String {
            return aDev
        }
        func setBDev(ADev: String) -> Void {
            aDev = ADev
        }
        func getBDev() -> String {
            return bDev
        }
        func setBDev(BDev: String) -> Void {
            bDev = BDev
        }
    }
}
