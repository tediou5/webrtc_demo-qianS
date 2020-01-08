//
//  File.swift
//  StTestRest
//
//  Created by qians on 2020/1/7.
//  Copyright © 2020 Guilin Cao. All rights reserved.
//

import Foundation
import HandyJSON

class StLoginResponse: HandyJSON {
    var client: StAppClient!
    var token: String!
    
    required init() {}
}

//class StAppClient: HandyJSON {
//    var friends = [StAppFriend]()
//    var id: StClientID!
//    var name: String?
//    var label: String?
//    var desp: String?
//    var flag: Int!
//    var flag_ClientType: String?
//    var flag_Online: Bool!
//    
//    required init() {}
//}

//class StAppFriend: HandyJSON {
//    var id: StClientID!
//    var name: String?
//    var label: String?
//    var desp: String?
//    var flag: Int?
//    var flag_ClientType: String?
//    var flag_Online: Bool!
//    var phone: String?
//    var role: String!
//    
//    required init() {}
//}

//class StClientID: HandyJSON {
//    
//    var id: CLong?
//    var hex: String?
//    
//    init(Id: CLong, Hex: String) {
//        id = Id
//        hex = Hex
//    }
//    required init() {}
//}
