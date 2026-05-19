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

    @IBOutlet weak var message_result: UILabel!
    @IBOutlet weak var mailReinit: UITextField!
    let baseURL = Bundle.main.object(forInfoDictionaryKey: "API_BASE_URL") as? String ?? ""
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }
    

    @IBAction func checkEmail(_ sender: Any) {
        message_result.text = ""
        guard let email = mailReinit.text, !email.isEmpty else {
                message_result.text = "Le champ du mail est obligatoire"
                message_result.textColor = .red
                return
            }

            // ⚠️ Remplace par ton IP locale
            var url = URL(string: baseURL+"/api/compte/find-by-email")!

            var request = URLRequest(url: url)

            request.httpMethod = "POST"

            request.setValue("application/json", forHTTPHeaderField: "Content-Type")

            // Corps JSON
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
                        print(decoded)

                        DispatchQueue.main.async {
                            if decoded.found {
                                self.message_result.text = "Compte trouvé: \(decoded.compte?.email ?? "")"
                                self.message_result.textColor = .green
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
