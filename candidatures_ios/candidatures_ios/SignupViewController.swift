//
//  SignupViewController.swift
//  candidatures_ios
//
//  Created by Ashley Rakotoarisoa on 18/05/2026.
//

import UIKit

class SignupViewController: UIViewController, UIPickerViewDelegate, UIPickerViewDataSource{
    @IBOutlet weak var mailRec: UITextField!
    @IBOutlet weak var mailF: UITextField!
    
    @IBOutlet weak var fnF: UITextField!
    @IBOutlet weak var lnF: UITextField!
    @IBOutlet weak var mdpr: UITextField!
    @IBOutlet weak var mdp: UITextField!
    @IBOutlet weak var sexe_picker: UIPickerView!
    @IBOutlet weak var adresseComplete: UITextField!
    @IBOutlet weak var telT: UITextField!
    @IBOutlet weak var message_result: UILabel!
    @IBOutlet weak var webF: UITextField!
    @IBOutlet weak var cvp: UITextField!
    @IBOutlet weak var titreSA: UITextField!
    @IBOutlet weak var nationaliteField: UITextField!
    @IBOutlet weak var birthP: UIDatePicker!
    let items = ["Sexe","Homme","Femme"]
    let baseURL = Bundle.main.object(forInfoDictionaryKey: "API_BASE_URL") as? String ?? ""
    var sex_selected: String = ""
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        sexe_picker.delegate = self
        sexe_picker.dataSource = self
        mdp.isSecureTextEntry = true
        mdpr.isSecureTextEntry = true
        birthP.datePickerMode = .date
        birthP.preferredDatePickerStyle = .compact

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

    @IBAction func validerInscription(_ sender: Any) {
        message_result.text = ""
        if (fnF.text?.isEmpty ?? true)
        || (lnF.text?.isEmpty ?? true)
        || (mailF.text?.isEmpty ?? true)
        || (mailRec.text?.isEmpty ?? true)
        || (mdp.text?.isEmpty ?? true)
        || (mdpr.text?.isEmpty ?? true) {

            message_result.text = "Les champs avec * sont obligatoires"
            message_result.textColor = UIColor.red
            return
        }
    }
}
