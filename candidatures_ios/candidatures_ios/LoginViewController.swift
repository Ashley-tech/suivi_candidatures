//
//  LoginViewController.swift
//  candidatures_ios
//
//  Created by Ashley Rakotoarisoa on 18/05/2026.
//

import UIKit

class LoginViewController: UIViewController {

    @IBOutlet weak var message_result: UILabel!
    @IBOutlet weak var pwdField: UITextField!
    @IBOutlet weak var login: UITextField!
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        pwdField.isSecureTextEntry = true
    }
    @IBAction func tenterConnexion(_ sender: Any) {
    }
    
    @IBAction func displayPwd(_ sender: UIButton) {

        let existingText = pwdField.text
        let isSecure = !pwdField.isSecureTextEntry

        pwdField.resignFirstResponder()

        pwdField.isSecureTextEntry = isSecure

        pwdField.becomeFirstResponder()

        // IMPORTANT : remettre le texte APRÈS
        // le retour du focus
        if let text = existingText {
            pwdField.text = ""
            pwdField.insertText(text)
        }

        print("TEXT:", pwdField.text ?? "nil")
    }

}
