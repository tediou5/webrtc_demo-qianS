//
//  StDevInfo.swift
//  test_for_fs
//
//  Created by qians on 2020/1/3.
//  Copyright © 2020 qians. All rights reserved.
//
import UIKit
import Foundation
class StDevInfo: NSObject {
    enum stType: String {
        case LOW = "LOW"
        case MID = "MID"
        case HIGH = "HIGH"
    }
    var brand: String
    var model: String
    var type: stType
    var codecs: String
    
    override init() {
        brand = "Apple"
        model = UIDevice.current.model
        codecs = UIDevice.current.systemVersion
        type = stType.MID
    }
}
