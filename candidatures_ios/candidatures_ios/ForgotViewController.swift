//
//  ForgotViewController.swift
//  candidatures_ios
//
//  Created by Ashley Rakotoarisoa on 18/05/2026.
//

import UIKit

struct CompteForgotResponse: Codable {
    let found: Bool
    let compte: Compte?
}

class ForgotViewController: UIViewController {

    @IBOutlet weak var loadingIcon: UIActivityIndicatorView!
    @IBOutlet weak var message_result: UILabel!
    @IBOutlet weak var mailReinit: UITextField!
    let baseURL = Bundle.main.object(forInfoDictionaryKey: "API_BASE_URL") as? String ?? ""
    override func viewDidLoad() {
        super.viewDidLoad()

        navigationItem.hidesBackButton = true
        loadingIcon.isHidden = true
    }
    
    func regexCheck(_ regex: String, _ str: String) -> Bool {
        return str.range(of: regex, options: .regularExpression) != nil
    }
    @IBAction func checkEmail(_ sender: Any) {
        message_result.text = ""
        guard let email = mailReinit.text, !email.isEmpty else {
                message_result.text = "Le champ du mail est obligatoire"
                message_result.textColor = .red
                return
            }

        loadingIcon.isHidden = false
        loadingIcon.startAnimating()
            // ⚠️ Remplace par ton IP locale
            var url = URL(string: baseURL+"/api/compte/find-by-email")!
            var request = URLRequest(url: url)
            request.httpMethod = "POST"
            request.setValue("application/json", forHTTPHeaderField: "Content-Type")

            // Corps JSON
            var body: [String: String] = [
                "email": email
            ]

            request.httpBody = try? JSONSerialization.data(withJSONObject: body)

            URLSession.shared.dataTask(with: request) { data, response, error in
                DispatchQueue.main.async {
                    if let error = error {
                        self.loadingIcon.stopAnimating()
                        self.loadingIcon.isHidden = true
                        self.message_result.text = error.localizedDescription
                        self.message_result.textColor = .red
                        return
                    }

                    guard let data = data else {
                        self.loadingIcon.stopAnimating()
                        self.loadingIcon.isHidden = true
                        self.message_result.text = "Aucune réponse serveur"
                        self.message_result.textColor = .red
                        return
                    }

                    do {
                        let decoded = try JSONDecoder().decode(CompteForgotResponse.self, from: data)
                        print(decoded)

                        DispatchQueue.main.async {
                            self.loadingIcon.stopAnimating()
                            self.loadingIcon.isHidden = true
                            if decoded.found {
                                self.message_result.text = "Email trouvé ! Un mail va vous être envoyé avec les instructions pour réinitialiser votre mot de passe. Vous serz obligé de basculer sur le site web pour changer votre mot de passe."
                                self.message_result.textColor = .green
                                url = URL(string: self.baseURL + "/api/test-mail")!

                                var mailRequest = URLRequest(url: url)
                                mailRequest.httpMethod = "POST"
                                mailRequest.setValue("application/json", forHTTPHeaderField: "Content-Type")

                                let mailBody: [String: String] = [
                                    "email": self.mailReinit.text!,
                                    "subject": "Réinitialisation de votre mot de pase",
                                    "content": "Bonjour,<br /><br />Voici le <a href='http://127.0.0.1:8000/\(decoded.compte!.id)/new_password'>lien</a> pour réinitialiser votre mot de passe.<br /><br />Cordialement,<br />L'équipe de suivi des candidatures"
                                    
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
                                self.message_result.text = "Aucun compte trouvé"
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
    
}
