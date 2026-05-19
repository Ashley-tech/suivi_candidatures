//
//  LoginViewController.swift
//  candidatures_ios
//
//  Created by Ashley Rakotoarisoa on 18/05/2026.
//

import UIKit

struct LoginResponse: Codable {
    let message: String
    let success: Bool
    let code: Int
    let compte: Compte?
}



class LoginViewController: UIViewController {

    @IBOutlet weak var message_result: UILabel!
    @IBOutlet weak var pwdField: UITextField!
    @IBOutlet weak var login: UITextField!
    let baseURL = Bundle.main.object(forInfoDictionaryKey: "API_BASE_URL") as? String ?? ""
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        pwdField.isSecureTextEntry = true

        print(baseURL)
    }
    
    func regexCheck(_ regex: String, _ str: String) -> Bool {
        return str.range(of: regex, options: .regularExpression) != nil
    }
    
    @IBAction func tenterConnexion(_ sender: Any) {
        message_result.text = ""
        guard
            let email = login.text, !email.isEmpty,
            let mdp = pwdField.text, !mdp.isEmpty
        else {
            message_result.text = "Tous les champs sont obligatoires"
            message_result.textColor = .red
            return
        }
        let regexMail = #"^[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$"#
        guard regexCheck(regexMail, email)
        else {
            message_result.text = "Votre mail ne respecte pas la norme classique"
            message_result.textColor = .red
            return
        }
            // ⚠️ Remplace par ton IP locale
            let url = URL(string: baseURL+"/api/login")!

            var request = URLRequest(url: url)

            request.httpMethod = "POST"

            request.setValue("application/json", forHTTPHeaderField: "Content-Type")

            // Corps JSON
            let body: [String: String] = [
                "email": email,
                "mdp": mdp
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
                        let decoded = try JSONDecoder().decode(LoginResponse.self, from: data)
                        print(decoded)

                        DispatchQueue.main.async {
                            if decoded.success == true {
                                self.message_result.text = "Connexion réussie"
                                self.message_result.textColor = .green
                            } else {
                                self.message_result.text = "L'adresse et le mot de passe ne correspondent à aucun compte"
                                self.message_result.textColor = .red
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
