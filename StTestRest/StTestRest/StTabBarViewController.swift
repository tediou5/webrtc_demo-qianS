//
//  TagBarViewController.swift
//  LoginTest
//
//  Created by qians on 2020/1/8.
//  Copyright © 2020 qians. All rights reserved.
//

import UIKit

class StTabBarViewController: UITabBarController {
    
    let FriendsListView = FriendsListViewController()
    let SettingView = SettingViewController()
    let UserInfoView = UserInfoViewController()
    let ContactsView = ContactsViewController()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        print("this is tab bar view")
        createTabBar()
        setUpControllers()
    }
    
    func setUpControllers(){
        let contactsNC = UINavigationController.init(rootViewController: ContactsView)
        let friendsListNC = UINavigationController.init(rootViewController: FriendsListView)
        let settingNC = UINavigationController.init(rootViewController: SettingView)
        let userInfoNC = UINavigationController.init(rootViewController: UserInfoView)
        
        self.viewControllers = [contactsNC, friendsListNC, settingNC, userInfoNC]
    }
    
    func createTabBar() -> Void {
        self.tabBar.barTintColor = UIColor.white
        self.tabBar.unselectedItemTintColor = UIColor.black
        self.tabBar.backgroundColor = UIColor.gray
        self.selectedIndex = 0
        self.tabBar.isUserInteractionEnabled = true
        
        createNavigationController(vc: ContactsView, title: "Contacts")
        createNavigationController(vc: FriendsListView, title: "FriendsList")
        createNavigationController(vc: SettingView, title: "Setting")
        createNavigationController(vc: UserInfoView, title: "UserInfo")
    }
    
    func createNavigationController(vc: UIViewController, title: String) -> Void {
        
        vc.title = title
        //vc.view.backgroundColor = UIColor.black
        vc.tabBarItem = UITabBarItem(title: title,
                                     image: nil,
                                     selectedImage: nil)
        let nav = UINavigationController(rootViewController: vc)
        self.addChild(nav)
    }
}
