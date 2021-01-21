//
//  StAppClient.swift
//  StTestRest
//
//  Created by qians on 2020/1/7.
//  Copyright © 2020 Guilin Cao. All rights reserved.
//

import Foundation
import HandyJSON

class StAppClient: HandyJSON {
    var friends = [StAppFriend]()
    var id: StClientID!
    var name: String?
    var label: String?
    var desp: String?
    var flag: Int!
    var flag_ClientType: String?
    var flag_Online: Bool!
    
    required init() {}
}