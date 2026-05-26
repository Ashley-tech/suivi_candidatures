//
//  ModifyProfileViewController.swift
//  candidatures_ios
//
//  Created by Ashley Rakotoarisoa on 26/05/2026.
//

import UIKit

class ModifyProfileViewController: UIViewController, UIPickerViewDelegate, UIPickerViewDataSource {
    
    var sex_selected: String = ""
    @IBOutlet weak var mdpr: UITextField!
    @IBOutlet weak var mdp: UITextField!
    @IBOutlet weak var sexe_picker: UIPickerView!
    let items = ["Sexe","Homme","Femme"]
    override func viewDidLoad() {
        super.viewDidLoad()

        sexe_picker.delegate = self
        sexe_picker.dataSource = self
        mdp.isSecureTextEntry = true
        mdpr.isSecureTextEntry = true
        
        // Do any additional setup after loading the view.
    }
    
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
            return 1
        }

        func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
            return items.count
        }

        func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
            return items[row]
        }

        func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
            if (row == 0){
                sex_selected = ""
            } else if (row == 1){
                sex_selected = "M"
            } else {
                sex_selected = "F"
            }
            print("Sélection :", sex_selected)
        }
    
    @IBAction func displayPwd(_ sender: Any) {
        let existingText = mdp.text
        let isSecure = !mdp.isSecureTextEntry
        
        mdp.resignFirstResponder()
        
        mdp.isSecureTextEntry = isSecure
        
        mdp.becomeFirstResponder()
        
        // IMPORTANT : remettre le texte APRÈS
        // le retour du focus
        if let text = existingText {
            mdp.text = ""
            mdp.insertText(text)
        }
    }
    
    @IBAction func displayPwdReconf(_ sender: Any) {
        let existingText = mdpr.text
        let isSecure = !mdpr.isSecureTextEntry
        
        mdpr.resignFirstResponder()
        
        mdpr.isSecureTextEntry = isSecure
        
        mdpr.becomeFirstResponder()
        
        // IMPORTANT : remettre le texte APRÈS
        // le retour du focus
        if let text = existingText {
            mdpr.text = ""
            mdpr.insertText(text)
        }
    }

}
