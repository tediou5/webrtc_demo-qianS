//
//  ViewController.swift
//  LoginTest
//
//  Created by qians on 2020/1/6.
//  Copyright © 2020 qians. All rights reserved.
//

import UIKit

class ViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {

    var View: UIView!
    
    var nameLabel = UILabel()
    var passwdLabel = UILabel()
    
    var friendsTable = UITableView()
    var friendsArr: [String] = []
    
    var name = UITextField()
    var passwd = UITextField()
    
    var loginBtn = UIButton()
    var leaveBtn = UIButton()
    
    let AFNet = AFManager()
    let Stomp = StWsClinet()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setUI()
        self.Stomp.registerSocket()
    }

    func setUI() -> Void {
        let width = self.view.bounds.size.width
        let height = self.view.bounds.size.height
        //UILabel
        nameLabel.frame = CGRect(x: 20, y: 100, width: 90, height: 40)
        nameLabel.text = "name: "
        self.view.addSubview(nameLabel)
        
        passwdLabel.frame = CGRect(x: 20, y: 150, width: 90, height: 40)
        passwdLabel.text = "password: "
        self.view.addSubview(passwdLabel)
        //TableView
        friendsTable.frame = CGRect(x: 20, y: 300, width: width-40, height: height-350)
        friendsTable.delegate = self
        friendsTable.dataSource = self
        self.view.addSubview(friendsTable)
        
        //UITextField
        name.frame = CGRect(x: 100, y: 100, width: width-120, height: 40)
        name.text = "user01"
        name.textColor = UIColor.black
        name.layer.borderWidth = 1
        name.layer.borderColor = UIColor.gray.cgColor
        name.isEnabled = true
        self.view.addSubview(name)
        
        passwd.frame = CGRect(x: 100, y: 150, width: width-120, height: 40)
        passwd.text = "abcd1234"
        passwd.textColor = UIColor.black
        passwd.layer.borderWidth = 1
        passwd.layer.borderColor = UIColor.gray.cgColor
        passwd.isEnabled = true
        self.view.addSubview(passwd)
        
        //UIButton
        loginBtn.frame = CGRect(x: width-90, y: 200, width: 70, height: 40)
        loginBtn.setTitle("login", for: .normal)
        loginBtn.setTitleColor(UIColor.black, for: .normal)
        loginBtn.layer.borderWidth = 1
        loginBtn.layer.borderColor = UIColor.gray.cgColor
        loginBtn.backgroundColor = UIColor.gray
        loginBtn.isEnabled = true
        loginBtn.tag = 1
        loginBtn.addTarget(self, action: #selector(doLogin(btn:)), for: .touchUpInside)
        self.view.addSubview(loginBtn)
        
        leaveBtn.frame = CGRect(x: width-90, y: 250, width: 70, height: 40)
        leaveBtn.setTitle("leave", for: .normal)
        leaveBtn.setTitleColor(UIColor.black, for: .normal)
        leaveBtn.layer.borderWidth = 1
        leaveBtn.layer.borderColor = UIColor.gray.cgColor
        leaveBtn.backgroundColor = UIColor.gray
        leaveBtn.isEnabled = true
        leaveBtn.tag = 2
        leaveBtn.addTarget(self, action: #selector(doLeave(btn:)), for: .touchUpInside)
        self.view.addSubview(leaveBtn)
    }

    @objc func doLogin(btn: UIButton) {
        print("do login: \(btn.tag)")
        let group = DispatchGroup()
        group.enter()
        AFNet.login(name: self.name.text!, passwd: self.passwd.text!, group: group)
        group.notify(queue: DispatchQueue.main){
            if self.AFNet.getIsSuccess(){
                print("is login!")
                let defaults = UserDefaults.standard
                let friends = defaults.dictionary(forKey: "friends")
                self.friendsArr = Array(friends!.values) as! [String]
                self.friendsTable.reloadData()
                Thread.detachNewThread {
                    while(true){
                        self.Stomp.sendECHO()
                        Thread.sleep(forTimeInterval: 7)
                    }
                }
            }else{
                print("Please Check Account and Password")
            }
        }
    }
    @objc func doLeave(btn: UIButton) {
        print("do leave: \(btn.tag)")
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return friendsArr.count
    }
    // 行高
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 40.0
    }
    // cell
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = UITableViewCell.init(style: UITableViewCell.CellStyle.default, reuseIdentifier: "CellIdentifier")
        cell.textLabel?.text = friendsArr[indexPath.row]
        return cell
    }
    // cell点击事件处理
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        print("click cell")
    }
}

