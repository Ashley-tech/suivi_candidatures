//
//  ModifyProfileViewController.swift
//  candidatures_ios
//
//  Created by Ashley Rakotoarisoa on 26/05/2026.
//

import UIKit

class ModifyProfileViewController: UIViewController, UIPickerViewDelegate, UIPickerViewDataSource {
    
    @IBOutlet weak var message_result: UILabel!
    @IBOutlet weak var maim: UITextField!
    var sex_selected: String = ""
    @IBOutlet weak var mdpr: UITextField!
    @IBOutlet weak var mdp: UITextField!
    @IBOutlet weak var lnM: UITextField!
    @IBOutlet weak var birthM: UIDatePicker!
    @IBOutlet weak var fnM: UITextField!
    @IBOutlet weak var nationalitem: UITextField!
    @IBOutlet weak var sam: UITextField!
    @IBOutlet weak var adressem: UITextField!
    @IBOutlet weak var webm: UITextField!
    @IBOutlet weak var telM: UITextField!
    @IBOutlet weak var sexe_picker: UIPickerView!
    @IBOutlet weak var cvpm: UITextField!
    let items = ["Sexe","Homme","Femme"]
    var prenom = ""
    var nom = ""
    var email = ""
    var cvpt = ""
    var web = ""
    var nationalite = ""
    var adresse = ""
    var situation_actuelle = ""
    var tel = ""
    var dateNaissance = ""
    var oldPassword = ""
    var id = 0
    var newPassword = ""
    let baseURL = Bundle.main.object(forInfoDictionaryKey: "API_BASE_URL") as? String ?? ""
    var mail : String = UserDefaults.standard.string(forKey: "userEmail") ?? ""
    override func viewDidLoad() {
        super.viewDidLoad()

        sexe_picker.delegate = self
        sexe_picker.dataSource = self
        mdp.isSecureTextEntry = true
        mdpr.isSecureTextEntry = true
        birthM.datePickerMode = .date
        birthM.preferredDatePickerStyle = .compact
        // Do any additional setup after loading the view.
        adressem.text = adresse
        lnM.text = nom
        fnM.text = prenom
        telM.text = tel
        maim.text = email
        webm.text = web
        cvpm.text = cvpt
        sam.text = situation_actuelle
        nationalitem.text = nationalite
        message_result.text = ""
        if sex_selected == "M" {
            sexe_picker.selectRow(1, inComponent: 0, animated: false)
        } else if sex_selected == "F" {
            sexe_picker.selectRow(2, inComponent: 0, animated: false)
        } else {
            sexe_picker.selectRow(0, inComponent: 0, animated: false)
        }
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"

        if let date = formatter.date(from: dateNaissance) {
            birthM.date = date
        }
        print("id : \(id)")
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
    
    @IBAction func validerModifications(_ sender: Any) {
        message_result.text = ""
        let formatter = DateFormatter()
        
        formatter.dateFormat = "yyyy-MM-dd"
        
        let formattedDate = formatter.string(from: birthM.date)
        
        if (fnM.text?.isEmpty ?? true)
            || (lnM.text?.isEmpty ?? true)
            || (maim.text?.isEmpty ?? true) {
            
            message_result.text = "Les champs avec * sont obligatoires"
            message_result.textColor = UIColor.red
            return
        }
        
        let regexMail = #"^[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$"#
        guard regexCheck(regexMail, maim.text ?? "") else {
            message_result.text = "Votre mail que vous avez saisi ne respecte pas la norme classique"
            message_result.textColor = .red
            return
        }
        
        if !(mdp.text ?? "").isEmpty || !(mdpr.text ?? "").isEmpty {
            guard mdp.text == mdpr.text else {
                message_result.text = "Les 2 nouveaux mots de passe saisis sont différents"
                message_result.textColor = .red
                return
            }
            
            guard mdp.text?.count ?? 0 >= 8 else {
                message_result.text = "Le mot de passe ne contient pas au moins 8 caractères"
                message_result.textColor = .red
                return
            }
        }
        var part_champ = adressem.text?.components(separatedBy: "/")
        guard part_champ?.count ?? 0 <= 2 else {
            message_result.text = "Merci de respecter le format [Adresse]/[Complément] car il y a trop d'arguments : \(String(describing: part_champ?.count)) > 2"
            message_result.textColor = .red
            return
        }
        var adr = ""
        var comp = ""
        if part_champ?.count == 2 {
            comp = part_champ?[1] ?? ""
            adr = part_champ?[0] ?? ""
        } else if part_champ?.count == 1 {
            adr = part_champ?[0] ?? ""
            comp = ""
        } else {
            adr = ""
            comp = ""
        }
        part_champ = cvpm.text?.components(separatedBy: "/")
        guard part_champ?.count ?? 0 <= 3 else {
            message_result.text = "Merci de respecter le format [Code postal]/[Ville]/[Pays] car il y a trop d'arguments : \(String(describing: part_champ?.count)) > 3"
            message_result.textColor = .red
            return
        }
        var cp = ""
        var ville = ""
        var pays = ""
        let regexTel = #"^\d{10}$"#
        let regexCp = #"^\d{5}$"#
        let regex1Web = #"^(https?://)?([\w-]+(\.[\w-]+)+)(/[\w-]*)*/?$"#
        let regex2Web = #"^[\w-]+(\.[\w-]+)+$"#
        let regex3Web = #"^(https?://)?([\w-]+(\.[\w-]+)+)(/[\w-]*)*/?$"#
        switch part_champ?.count {
            case 3:
                cp = part_champ![0]
                ville = part_champ![1]
                pays = part_champ![2]
                break
            case 2:
                cp = part_champ![0]
                ville = part_champ![1]
                pays = ""
                break
            case 1:
                cp = part_champ![0]
                ville = ""
                pays = ""
                break
            default:
                cp = ""; ville=""; pays = "";
        }
        if cp != ""{
            guard regexCheck(regexCp, cp) else {
                message_result.text = "Le code postal que vous avez saisi doit contenir exactment 5 chiffres"
                message_result.textColor = .red
                return
            }
        }
        if telM.text != "" {
            guard regexCheck(regexTel, telM.text ?? "") else {
                message_result.text = "Le numéro de téléphone que vous avez saisi doit contenir exactment 10 chiffres"
                message_result.textColor = .red
                return
            }
        }
        if webm.text != "" {
            if (!regexCheck(regex1Web, webm.text!) && !regexCheck(regex2Web, webm.text!) && !regexCheck(regex3Web, webm.text!)) {
                message_result.text = "L'adresse web saisi doit commencer par http:// https:// ou www."
                message_result.textColor = .red
                return
            }
        }
        var url = URL(string: baseURL+"/api/compte/find-by-email")
        var request = URLRequest(url: url!)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        let email = maim.text!

        let body: [String: String] = [
            "email": email
        ]

        request.httpBody = try? JSONSerialization.data(withJSONObject: body)
        
        URLSession.shared.dataTask(with: request) { data, response, error in
            DispatchQueue.main.async {
                if let error = error {
                    self.message_result.text = error.localizedDescription
                    self.message_result.textColor = .red
                    return
                }
                
                guard let data = data else {
                    self.message_result.text = "Aucune réponse serveur"
                    self.message_result.textColor = .red
                    return
                }
                
                do {
                    let decoded = try JSONDecoder().decode(CompteForgotResponse.self, from: data)
                    print(self.email)
                    print(self.maim.text!)

                    DispatchQueue.main.async {
                        if self.maim.text! != self.email && decoded.found {
                            self.message_result.text = "Un compte avec cette adresse email existe déjà. Veuillez utiliser une adresse email différente."
                            self.message_result.textColor = .red
                        } else {
                            url = URL(string: self.baseURL+"/api/compte/"+String(self.id))!

                            request = URLRequest(url: url!)
                            request.httpMethod = "PATCH"
                            request.setValue("application/json", forHTTPHeaderField: "Content-Type")
                            if self.mdp.text! != "" {
                                self.newPassword = self.mdp.text!
                            } else {
                                self.newPassword = self.oldPassword
                            }

                            // Corps JSON
                            var body: [String: String] = [
                                "sexe":self.sex_selected,
                                "nom":self.lnM.text!,
                                "prenom":self.fnM.text!,
                                "email":self.maim.text!,
                                "date_naissance":formattedDate,
                                "mdp":self.newPassword,
                                "nationalite":self.nationalitem.text!,
                                "titre":self.sam.text!,
                                "adresse":adr,
                                "adresse_comp":comp,
                                "cp":cp,
                                "ville":ville,
                                "pays":pays,
                                "numero":self.telM.text!,
                                "website":self.webm.text!
                            ]

                            request.httpBody = try? JSONSerialization.data(withJSONObject: body)
                            
                            URLSession.shared.dataTask(with: request) { data, response, error in
                                DispatchQueue.main.async {
                                    if let error = error {
                                        self.message_result.text = error.localizedDescription
                                        self.message_result.textColor = .red
                                        return
                                    }
                                    
                                    guard let data = data else {
                                        self.message_result.text = "Aucune réponse serveur"
                                        self.message_result.textColor = .red
                                        return
                                    }
                                    
                                    do {
                                        let decoded1 = try JSONDecoder().decode(SignupResponse.self, from: data)
                                        print(decoded1)
                                        
                                        DispatchQueue.main.async {
                                            if decoded1.success == true {
                                                self.message_result.textColor = .green
                                                if (self.email != self.maim.text!) {
                                                    self.message_result.text = "Compte modifié avec succès ! Comme vous avez modifié votre adresse mail, vous allez recevoir un mail à cette nouvelle adresse mail."
                                                    
                                                    UserDefaults.standard.set(self.maim.text!, forKey: "userEmail")
                                                    
                                                    url = URL(string: self.baseURL + "/api/test-mail")!

                                                    var mailRequest = URLRequest(url: url!)
                                                    mailRequest.httpMethod = "POST"
                                                    mailRequest.setValue("application/json", forHTTPHeaderField: "Content-Type")

                                                    let mailBody: [String: String] = [
                                                        "email": self.maim.text!,
                                                        "subject": "Modification de votre adresse email",
                                                        "content": """
                                                        Bonjour,<br /><br />Votre compte vient de subir la modification de votre adresse mail.<br />Adresse mail : <s>\(self.mail)</s>  \(self.maim.text!).<br /><br />Cordialement, <br />L'équipe de suivi des candidatures
                                                        """
                                                    ]

                                                    mailRequest.httpBody = try? JSONSerialization.data(withJSONObject: mailBody)
                                                    
                                                    URLSession.shared.dataTask(with: mailRequest) { data, response, error in

                                                        DispatchQueue.main.async {

                                                            if let error = error {
                                                                print("MAIL ERROR:", error.localizedDescription)
                                                                return
                                                            }

                                                            print("Mail envoyé ✔️")
                                                        }
                                                    }.resume()
                                                } else {
                                                    self.message_result.text = "Compte modifié avec succès !"
                                                }
                                                DispatchQueue.main.asyncAfter(deadline: .now() + 1.0) {
                                                    self.navigationController?.popViewController(animated: true)
                                                }
                                            } else {
                                                self.message_result.text = "Nous n'avons pas pu vous inscrire. Veuillez réessayer ultérieurement"
                                                self.message_result.textColor = .red
                                            }
                                        }
                                    }catch{
                                        DispatchQueue.main.async {
                                            self.message_result.text = "Erreur parsing JSON 2"
                                            self.message_result.textColor = .red
                                        }
                                    }
                                }
                            }.resume()
                        }
                    }
                } catch {
                    DispatchQueue.main.async {
                        self.message_result.text = "Erreur parsing JSON"
                        self.message_result.textColor = .red
                    }
                }
            }
        }.resume()
    }
    
    func regexCheck(_ regex: String, _ str: String) -> Bool {
        return str.range(of: regex, options: .regularExpression) != nil
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
