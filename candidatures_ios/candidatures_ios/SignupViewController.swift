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
    
    func regexCheck(_ regex: String, _ str: String) -> Bool {
        return str.range(of: regex, options: .regularExpression) != nil
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
        let formatter = DateFormatter()
        
        formatter.dateFormat = "yyyy-MM-dd"
        
        let formattedDate = formatter.string(from: birthP.date)
        
        print(formattedDate)
        
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
        
        guard mailF.text == mailRec.text else {
            message_result.text = "Les 2 adresses mails saisis sont différents"
            message_result.textColor = .red
            return
        }
        let regexMail = #"^[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$"#
        guard regexCheck(regexMail, mailF.text ?? "") else {
            message_result.text = "Votre mail que vous avez saisi ne respecte pas la norme classique"
            message_result.textColor = .red
            return
        }
        guard mdp.text == mdpr.text else {
            message_result.text = "Les 2 mots de passe saisis sont différents"
            message_result.textColor = .red
            return
        }
        guard mdp.text?.count ?? 0 >= 8 else {
            message_result.text = "Le mot de passe ne contient pas au moins 8 caractères"
            message_result.textColor = .red
            return
        }
        var part_champ = adresseComplete.text?.components(separatedBy: "/")
        guard part_champ?.count ?? 0 <= 2 else {
            message_result.text = "Merci de respecter le format [Adresse]/[Complément] car il y a trop d'arguments : \(String(describing: part_champ?.count)) > 2"
            message_result.textColor = .red
            return
        }
        var adresse = ""
        var comp = ""
        if part_champ?.count == 2 {
            comp = part_champ?[1] ?? ""
            adresse = part_champ?[0] ?? ""
        } else if part_champ?.count == 1 {
            adresse = part_champ?[0] ?? ""
            comp = ""
        } else {
            adresse = ""
            comp = ""
        }
        part_champ = cvp.text?.components(separatedBy: "/")
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
        if telT.text != "" {
            guard regexCheck(regexTel, telT.text ?? "") else {
                message_result.text = "Le numéro de téléphone que vous avez saisi doit contenir exactment 10 chiffres"
                message_result.textColor = .red
                return
            }
        }
        if webF.text != "" {
            if (!regexCheck(regex1Web, webF.text!) && !regexCheck(regex2Web, webF.text!) && !regexCheck(regex3Web, webF.text!)) {
                message_result.text = "L'adresse web saisi doit commencer par http:// https:// ou www."
                message_result.textColor = .red
                return
            }
        }
        var url = URL(string: baseURL+"/api/compte/find-by-email")
        var request = URLRequest(url: url!)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        let email = mailF.text!//?
            //.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""

        print("EMAIL =", email)

        let body: [String: String] = [
            "email": email
        ]

        request.httpBody = try? JSONSerialization.data(withJSONObject: body)

        print(String(data: request.httpBody!, encoding: .utf8)!)
        
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
                    print(decoded)

                    DispatchQueue.main.async {
                        if decoded.found {
                            self.message_result.text = "Un compte avec cette adresse email existe déjà. Veuillez utiliser une adresse email différente."
                            self.message_result.textColor = .red
                        }else{
                            url = URL(string: self.baseURL+"/api/comptes")!

                            request = URLRequest(url: url!)

                            request.httpMethod = "POST"

                            request.setValue("application/json", forHTTPHeaderField: "Content-Type")

                            // Corps JSON
                            var body: [String: String] = [
                                "sexe":self.sex_selected,
                                "nom":self.lnF.text!,
                                "prenom":self.fnF.text!,
                                "email":self.mailF.text!,
                                "date_naissance":formattedDate,
                                "mdp":self.mdp.text!,
                                "nationalite":self.nationaliteField.text!,
                                "titre":self.titreSA.text!,
                                "adresse":adresse,
                                "adresse_comp":comp,
                                "cp":cp,
                                "ville":ville,
                                "pays":pays,
                                "numero":self.telT.text!,
                                "website":self.webF.text!
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
                                                self.message_result.text = "Inscription réussie ! Vous allez recevoir un email de confirmation."
                                                self.message_result.textColor = .green
                                                url = URL(string: self.baseURL + "/api/test-mail")!

                                                var mailRequest = URLRequest(url: url!)
                                                mailRequest.httpMethod = "POST"
                                                mailRequest.setValue("application/json", forHTTPHeaderField: "Content-Type")

                                                let mailBody: [String: String] = [
                                                    "email": self.mailF.text!,
                                                    "subject": "Confirmation de votre inscription",
                                                    "content": """
                                                    Bonjour \(self.fnF.text ?? ""),<br /><br />
                                                    Votre compte a bien été créé sur le site de suivi des candidatures.<br /><br />
                                                    Cordialement,<br />
                                                    L'équipe de suivi des candidatures
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
                                                
                                                
                                                DispatchQueue.main.asyncAfter(deadline: .now() + 1.0) {
                                                    self.message_result.text = "Inscription réussie."
                                                    self.navigationController?.popViewController(animated: true)
                                                }
                                            } else {
                                                self.message_result.text = "Nous n'avons pas pu vous inscrire. Veuillez réessayer ultérieurement"
                                                self.message_result.textColor = .red
                                            }
                                        }

                                    } catch {
                                        DispatchQueue.main.async {
                                            self.message_result.text = "Erreur parsing JSON 2"
                                            self.message_result.textColor = .red
                                        }
                                    }
                                }

                            }.resume()
                        }
                    }
                }catch{
                    DispatchQueue.main.async {
                        self.message_result.text = "Erreur parsing JSON"
                        self.message_result.textColor = .red
                    }
                }
            }
        }.resume()
    }
}
