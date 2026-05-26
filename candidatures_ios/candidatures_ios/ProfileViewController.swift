//
//  ProfileViewController.swift
//  candidatures_ios
//
//  Created by Ashley Rakotoarisoa on 25/05/2026.
//

import UIKit

class ProfileViewController: UIViewController {

    @IBOutlet weak var mel_text: UILabel!
    @IBOutlet weak var name_text: UILabel!
    @IBOutlet weak var naissance_text: UILabel!
    @IBOutlet weak var nationalite_text: UILabel!
    @IBOutlet weak var pwd_text: UILabel!
    @IBOutlet weak var adresse_text: UILabel!
    @IBOutlet weak var tel_text: UILabel!
    @IBOutlet weak var creation_text: UILabel!
    @IBOutlet weak var web_text: UILabel!
    let mail : String = UserDefaults.standard.string(forKey: "userEmail") ?? ""
    let baseURL = Bundle.main.object(forInfoDictionaryKey: "API_BASE_URL") as? String ?? ""
    override func viewDidLoad() {
        super.viewDidLoad()

        var url = URL(string:baseURL+"/api/compte/find-by-email")!
        
        var request = URLRequest(url: url)

        request.httpMethod = "POST"

        request.setValue("application/json", forHTTPHeaderField: "Content-Type")

        // Corps JSON
        var body: [String: String] = [
            "email": mail
        ]

        request.httpBody = try? JSONSerialization.data(withJSONObject: body)

                URLSession.shared.dataTask(with: request) { data, response, error in
                    
                    DispatchQueue.main.async {
                        if let error = error {
                            print(error.localizedDescription)
                            return
                        }

                        guard let data = data else {
                            print("Aucune réponse serveur")
                            return
                        }

                        do {

                            let decoded = try JSONDecoder().decode(
                                CompteForgotResponse.self,
                                from: data
                            )

                            if decoded.found {

                                let prenom = decoded.compte?.prenom ?? ""
                                let nom = decoded.compte?.nom?.uppercased() ?? ""
                                let sexe = decoded.compte?.sexe ?? ""
                                let email = decoded.compte?.email ?? ""
                                let mdp = decoded.compte?.mdp ?? ""

                                // NOM
                                if sexe == "M" {
                                    self.name_text.text = "M. \(prenom) \(nom)"
                                } else if sexe == "F" {
                                    self.name_text.text = "Mme \(prenom) \(nom)"
                                } else {
                                    self.name_text.text = "\(prenom) \(nom)"
                                }

                                // MAIL
                                self.mel_text.text = "Mail : \(email)"

                                // PASSWORD
                                self.pwd_text.text =
                                    "Mot de passe : \(String(repeating: "*", count: mdp.count))"
                                self.nationalite_text.text = "Nationalité : \(decoded.compte?.nationalite ?? "")"
                            }

                        } catch {

                            print("Erreur JSON :", error.localizedDescription)
                        }
                    }
                }.resume()
    }

    @IBAction func supprimerCompte(_ sender: Any) {
    }
    @IBAction func deconnecter(_ sender: Any) {
    }
}
