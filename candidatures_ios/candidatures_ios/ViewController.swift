//
//  ViewController.swift
//  candidatures_ios
//
//  Created by Ashley Rakotoarisoa on 18/05/2026.
//

import UIKit

class ViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
    }


    @IBAction func continuerVersSuite(_ sender: Any) {
        print("NAV:", navigationController as Any)
        
        let email = UserDefaults.standard.string(forKey: "userEmail")

        if email == nil || email == "" {
            // PAS connecté
            let vc = storyboard?.instantiateViewController(
                withIdentifier: "LoginViewController"
            ) as! LoginViewController
            navigationController?.pushViewController(vc, animated: true)
        } else {
            // CONNECTÉ
            let vc = storyboard?.instantiateViewController(
                withIdentifier: "MenuViewController"
            ) as! MenuViewController
            navigationController?.pushViewController(vc, animated: true)
        }
    }
}

