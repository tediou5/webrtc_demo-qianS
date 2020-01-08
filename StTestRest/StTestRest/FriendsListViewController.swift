//
//  FriendsListViewController.swift
//  StTestRest
//
//  Created by qianS on 2020/1/8.
//  Copyright © 2020 qians. All rights reserved.
//

import UIKit

class FriendsListViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {
    
    var View: UIView!
    
    var friendsTable = UITableView()
    var friendsArr: [String] = []
    
    var loginBtn = UIButton()
    var leaveBtn = UIButton()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setUI()
        reFresh()
    }
    func reFresh() -> Void {
        let defaults = UserDefaults.standard
        let friends = defaults.dictionary(forKey: "friends")
        if (friends != nil) {
            self.friendsArr = Array(friends!.values) as! [String]
            self.friendsTable.reloadData()
        }
    }

    func setUI() -> Void {
        let width = self.view.bounds.size.width
        let height = self.view.bounds.size.height
        //TableView
        friendsTable.frame = CGRect(x: 0, y: 0, width: width, height: height)
        friendsTable.delegate = self
        friendsTable.dataSource = self
        self.view.addSubview(friendsTable)
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
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {}
}
