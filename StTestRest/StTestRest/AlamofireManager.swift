//
//  AlamofireManager.swift
//  StTestRest
//
//  Created by qians on 2020/1/7.
//  Copyright © 2020 Guilin Cao. All rights reserved.
//

import Foundation
import Alamofire

class AFManager: NSObject {
    //self.baseUrl = @"http://localhost:9001";
    let baseUrl = "http://192.168.11.123:8080";//fs_office
    //let baseUrl = "http://192.168.31.216:8080";//op3800
    let applyAddApi = "/api/v1/rtc/contact/applyAdd";
    let authcodeApi = "/api/v1/rtc/common/authcode/";
    let contactsApi = "/api/v1/rtc/user/contacts/";
    let grantAddApi = "/api/v1/rtc/contact/grantAdd";
    let loginApi = "/api/v1/rtc/user/login/name";
    let logoutApi = "";
    let searchApi = "/api/v1/rtc/contact/search";
    let signInApi = "/api/v1/rtc/user/signin";
    let signOutApi = "";
    let deleteDeviceApi = "/api/v1/rtc/contact/delete";
    
    var isSuccess: Bool
    
    override init() {
        isSuccess = false
    }
    
    func login(name: String, passwd: String, group: DispatchGroup) -> Void {
        let Url = self.baseUrl + self.loginApi
        let parameters: Dictionary = ["name": name, "passwd": passwd, "type": "1"]
        var request = URLRequest(url: URL(string: Url)!)
        request.httpMethod = HTTPMethod.post.rawValue
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        do {
            let theJSONData = try JSONSerialization.data(withJSONObject: parameters, options: JSONSerialization.WritingOptions())
            request.httpBody = theJSONData
            Alamofire.request(request).responseString{(response) in
                switch response.result{
                case .success:
                    if let loginResponse = StLoginResponse.deserialize(from: response.result.value){
                        if  (loginResponse.token != nil) {
                            ProcessInfoTools.saveToken(token: loginResponse.token)
                            ProcessInfoTools.saveFriendsFromArr(friends: loginResponse.client.friends)
                            self.isSuccess = true
                        }else{
                            self.isSuccess = false
                        }
                    }
                    break
                    
                case .failure(let error):
                    print(error)
                    self.isSuccess = false
                    break
                }
                group.leave()
            }
        } catch {
            print("error serializing JSON: \(error)")
        }
    }
    
    func logout(phoneNum: String, authCode: String, group: DispatchGroup) -> Void {
        //TODO
    }
    
    func signIn(uid: String, group: DispatchGroup) -> Void {
        //TODO
    }
    
    func getAuthCode(phoneNum: String, group: DispatchGroup) -> Void {
        //TODO
    }
    
    func getContacts(uid: String, group: DispatchGroup) -> Void {
        //TODO
    }
    
    func getContacts(uid: String) -> Void {
        //TODO
    }
    
    func search(sid: String, keyword: String, group: DispatchGroup) -> Void {
        //TODO
    }
    
    func applyAddDevice(cid: String, sid: String, tid: String, group: DispatchGroup) -> Void {
        //TODO
    }
    
    func grantAddDevice(uid: String, sid: String, tid: String, type: String, group: DispatchGroup) -> Void {
        //TODO
    }
    
    func deleteDevice(uid: String, cid: String, group: DispatchGroup) -> Void {
        //TODO
    }
    
    func getIsSuccess() -> Bool {
        return self.isSuccess
    }
}
