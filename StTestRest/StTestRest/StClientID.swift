//
//  StClientID.swift
//  test_for_fs
//
//  Created by qians on 2020/1/3.
//  Copyright © 2020 qians. All rights reserved.
//

import Foundation
import HandyJSON

class StClientID: HandyJSON {
    
    var id: Int!
    var hex: String!
    
    init(Id: CLong, Hex: String) {
        id = Id
        hex = Hex
    }
    required init() {}
}
