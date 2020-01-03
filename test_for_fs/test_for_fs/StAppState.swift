//
//  StAppState.swift
//  test_for_fs
//
//  Created by qians on 2020/1/3.
//  Copyright © 2020 qians. All rights reserved.
//

import Foundation
enum StAppState: Int {
    case OFFLINE = 0x00
    case IDLE = 0x40
    case CALL_OUT = 0x41
    case CALL_IN = 0x42
    case CALL_OFFER = 0x43
    case CALL_ANSWER = 0x44
    case CALL_ONGOING = 0x45
    case CALL_BYE = 0x46
}
