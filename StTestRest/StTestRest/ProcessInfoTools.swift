//
//  ProcessInfoTools.swift
//  StTestRest
//
//  Created by qians on 2020/1/7.
//  Copyright © 2020 Guilin Cao. All rights reserved.
//

import Foundation
class ProcessInfoTools: NSObject {
    
    
    public static func saveFriendsFromArr(friends: Array<StAppFriend>) -> Void {
        //let friend: StAppFriend
        
        if(friends.count != 0){
            //var firendsDic = Dictionary<String, String>()
            var friendsDic = [String: String]()
            for friend in friends{
                let name = friend.name as String
                let ID = friend.id!.id as Int
                let Id = String(ID)
                friendsDic[Id] = name
            }
            let userDefault = UserDefaults.standard
            userDefault.setValue(friendsDic, forKey: "friends")
        }
        
    }
    
    public static func saveToken(token: String) -> Void {
        let userDefault = UserDefaults.standard
        userDefault.setValue(token, forKey: "token")
    }
    
    public static func saveClientInfo(client: StAppClient) -> Void{
        let ID = client.id.id as Int
        let Id = String(ID)
        let name = client.name
        print("id = \(Id)")
        print("name = \(name)")
        let userDefault = UserDefaults.standard
        userDefault.setValue(Id, forKey: "id")
        userDefault.setValue(name, forKey: "name")
    }
}
