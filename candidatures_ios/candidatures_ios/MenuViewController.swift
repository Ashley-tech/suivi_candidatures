//
//  MenuViewController.swift
//  candidatures_ios
//
//  Created by Ashley Rakotoarisoa on 21/05/2026.
//

import UIKit

struct CandidaturesResponse: Decodable {
    let candidatures: [Candidature]
}

class MenuViewController: UIViewController {
    @IBOutlet weak var welcomet: UILabel!
    @IBOutlet weak var messager: UILabel!
    var id : Int = 0
    let mail : String = UserDefaults.standard.string(forKey: "userEmail") ?? ""
    let baseURL = Bundle.main.object(forInfoDictionaryKey: "API_BASE_URL") as? String ?? ""
    override func viewDidLoad() {
        super.viewDidLoad()

        //print(mail)
        navigationItem.hidesBackButton = true
        
        chargerMenu()
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "versCandidatures" {
            let destination = segue.destination as! CandidaturesViewController
            destination.compte = id
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        chargerMenu()
    }
    
    func chargerMenu() {
        var url = URL(string: baseURL+"/api/compte/find-by-email")!

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
                    print(decoded)

                    if decoded.found {
                        self.welcomet.text = "Bienvenue, \(decoded.compte?.prenom ?? "") !"
                        self.id = decoded.compte?.id ?? 0
                        
                        url = URL(string: self.baseURL+"/api/compte/\(self.id)/candidatures")!
                        request = URLRequest(url: url)
                        request.httpMethod = "GET"
                        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
                        
                        URLSession.shared.dataTask(with: url) { data, response, error in
                            
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
                                    let decoded2 = try JSONDecoder().decode(
                                        [Candidature].self,
                                        from: data
                                    )

                                    print(decoded2)
                                    for i in 0..<decoded2.count {
                                        if decoded2[i].statut!.lowercased().contains("contrat signé") {
                                            self.messager.text = "Vous avez au moins une candidature, dont vous avez signé le contrat ! Félicitations ! Vous pouvez peut-être supprimer votre compte si vous n'en avez plus besoin, ou continuer à suivre vos autres candidatures."
                                            break;
                                        }
                                    }
                                } catch {
                                    print("Erreur JSON 2 : ",error.localizedDescription)
                                }
                            }
                        }.resume()
                    }
                } catch {
                    print("Erreur JSON :", error.localizedDescription)
                }
            }

        }.resume()
    }

    @IBAction func deconnecter(_ sender: Any) {
        let alert = UIAlertController(
            title: "Déconnexion",
            message: "Êtes-vous sûr de vouloir vous déconnecter ?",
            preferredStyle: .alert
        )

        // Bouton CONFIRMER
        let confirmAction = UIAlertAction(
            title: "Oui",
            style: .destructive
        ) { _ in
            UserDefaults.standard.removeObject(forKey: "userEmail")
            //UserDefaults.standard.removeObject(forKey: "userId")
            let vc = self.storyboard?.instantiateViewController(
                withIdentifier: "LoginViewController"
            ) as! LoginViewController
            self.navigationController?.setViewControllers([vc], animated: true)
        }
        
        // Bouton ANNULER
        let cancelAction = UIAlertAction(
            title: "Non",
            //style: .cancel
            style: .default
        )
        alert.addAction(confirmAction)
        alert.addAction(cancelAction)

        self.present(alert, animated: true)
    }
}
