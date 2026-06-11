//
//  ProfileViewController.swift
//  candidatures_ios
//
//  Created by Ashley Rakotoarisoa on 25/05/2026.
//

import UIKit

class ProfileViewController: UIViewController {

    @IBOutlet weak var sa_text: UILabel!
    @IBOutlet weak var mel_text: UILabel!
    @IBOutlet weak var name_text: UILabel!
    @IBOutlet weak var naissance_text: UILabel!
    @IBOutlet weak var nationalite_text: UILabel!
    @IBOutlet weak var pwd_text: UILabel!
    @IBOutlet weak var adresse_text: UILabel!
    @IBOutlet weak var tel_text: UILabel!
    @IBOutlet weak var creation_text: UILabel!
    @IBOutlet weak var web_text: UILabel!
    @IBOutlet weak var cpt: UILabel!
    @IBOutlet weak var ville_text: UILabel!
    @IBOutlet weak var pays_text: UILabel!
    lazy var url = URL(string:"")!
    lazy var request : URLRequest = URLRequest(url: url)
    var id : Int = 0;
    var mail : String = UserDefaults.standard.string(forKey: "userEmail") ?? ""
    let baseURL = Bundle.main.object(forInfoDictionaryKey: "API_BASE_URL") as? String ?? ""
    var prenom : String = ""
    var nom: String = ""
    var sexe: String = ""
    var email: String = ""
    var mdp: String = ""
    var adresse_complete : String = ""
    var cvp: String = ""
    var nationalite = ""
    var web: String = ""
    var tel: String = ""
    var situation_actuelle = ""
    var dateNaissance: String = ""
    override func viewDidLoad() {
        super.viewDidLoad()
        navigationItem.hidesBackButton = true
        chargerProfil()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        chargerProfil()
    }
    
    @IBAction func profileReturned(segue: UIStoryboardSegue){
        
    }
    
    func chargerProfil() {
        url = URL(string:baseURL+"/api/compte/find-by-email")!
        
        request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        let email = UserDefaults.standard.string(forKey: "userEmail") ?? ""
        
        // Corps JSON
        var body: [String: String] = [
            "email": email
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
                                self.id = decoded.compte?.id ?? 0
                                self.prenom = decoded.compte?.prenom ?? ""
                                self.nom = decoded.compte?.nom ?? ""
                                self.sexe = decoded.compte?.sexe ?? ""
                                self.email = decoded.compte?.email ?? ""
                                self.mdp = decoded.compte?.mdp ?? ""
                                self.dateNaissance = decoded.compte?.date_naissance ?? ""

                                // NOM
                                if self.sexe == "M" {
                                    self.name_text.text = "M. \(self.prenom) \(self.nom.uppercased())"
                                } else if self.sexe == "F" {
                                    self.name_text.text = "Mme \(self.prenom) \(self.nom.uppercased())"
                                } else {
                                    self.name_text.text = "\(self.prenom) \(self.nom.uppercased())"
                                }
                                
                                self.naissance_text.text = "Date de naissance : \(self.dateNaissance)"

                                // MAIL
                                self.mel_text.text = "Mail : \(self.email)"

                                // PASSWORD
                                self.pwd_text.text =
                                "Mot de passe : \(String(repeating: "*", count: self.mdp.count))"
                                self.nationalite = decoded.compte?.nationalite ?? ""
                                self.nationalite_text.text = "Nationalité : \(self.nationalite)"
                                self.situation_actuelle = decoded.compte?.titre ?? ""
                                self.sa_text.text = "Votre situation actuelle : \(self.situation_actuelle)"
                                let adresse = decoded.compte?.adresse ?? ""
                                let adresseComp = decoded.compte?.adresse_comp ?? ""
                                if !adresse.isEmpty {
                                    self.adresse_text.text = "Adresse : \(adresse)"
                                    if !adresseComp.isEmpty {
                                        self.adresse_text.text = "Adresse : \(adresse) - \(adresseComp)"
                                    }
                                } else {
                                    self.adresse_text.text = "Adresse : \(adresse)"
                                }
                                if !adresseComp.isEmpty {
                                    self.adresse_complete = "\(adresse)/\(adresseComp)"
                                } else {
                                    if !adresse.isEmpty {
                                        self.adresse_complete = adresse
                                    } else {
                                        self.adresse_complete = ""
                                    }
                                }
                                self.cpt.text = "Code postal : \(decoded.compte?.cp ?? "")"
                                self.ville_text.text = "Ville : \(decoded.compte?.ville ?? "")"
                                self.pays_text.text = "Pays :\(decoded.compte?.pays ?? "")"
                                if (!(decoded.compte?.pays ?? "").isEmpty) {
                                    self.cvp = "\(decoded.compte?.cp ?? "")/\(decoded.compte?.ville ?? "")/\(decoded.compte?.pays ?? "")"
                                } else if (!(decoded.compte?.ville ?? "").isEmpty) {
                                    self.cvp = "\(decoded.compte?.cp ?? "")/\(decoded.compte?.ville ?? "")"
                                } else {
                                    self.cvp = "\(decoded.compte?.cp ?? "")"
                                }
                                self.tel_text.text = "Téléphone : \(decoded.compte?.numero ?? "")"
                                self.web = decoded.compte?.website ?? ""
                                self.web_text.text = "Site web : \(self.web)"
                            }
                        } catch {
                            print("Erreur JSON :", error.localizedDescription)
                        }
                    }
                }.resume()
    }

    @IBAction func supprimerCompte(_ sender: Any) {
        let alert = UIAlertController(
            title: "Suppression du compte",
            message: "Êtes-vous sûr de vouloir supprimer votre compte ? (Cette action sera irréversible)",
            preferredStyle: .alert
        )

        // Bouton CONFIRMER
        let confirmAction = UIAlertAction(
            title: "Oui",
            style: .destructive
        ) { _ in
            self.url = URL(string:self.baseURL+"/api/compte/\(self.id)/candidatures")!
            
            self.request = URLRequest(url: self.url)

            self.request.httpMethod = "GET"

            self.request.setValue("application/json", forHTTPHeaderField: "Content-Type")
            
            URLSession.shared.dataTask(with: self.request) { data, response, error in
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
                            [CandidatureResponse].self,
                            from: data
                        )
                        for i in 0..<decoded.count {
                            let idt = decoded[i].id
                            var urlt = URL(string:self.baseURL+"/api/candidature/\(idt)")
                            var rt = URLRequest(url:urlt!)
                            rt.httpMethod = "DELETE"
                            rt.setValue("application/json", forHTTPHeaderField: "Content-Type")
                            
                            URLSession.shared.dataTask(with: rt) { data, response, error in
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
                                                DeleteResponse.self,
                                                from: data
                                            )
                                    }catch{
                                        print("Erreur de suppression de la candidature n°\(idt): "+error.localizedDescription)
                                        return
                                    }
                                }
                            }.resume()
                            
                        }
                    }catch {
                        print("Erreur JSON : "+error.localizedDescription)
                        return
                    }
                }
            }.resume()
            
            self.url = URL(string:self.baseURL+"/api/compte/\(self.id)/cvs")!
            
            self.request = URLRequest(url: self.url)

            self.request.httpMethod = "GET"

            self.request.setValue("application/json", forHTTPHeaderField: "Content-Type")
            
            URLSession.shared.dataTask(with: self.request) { data, response, error in
                DispatchQueue.main.async {
                    if let error = error {
                        print(error.localizedDescription)
                        return
                    }
                    
                    guard let data3 = data else {
                        print("Aucune réponse serveur")
                        return
                    }
                    
                    do {
                        let decoded3 = try JSONDecoder().decode(
                            [CVResponse].self,
                            from: data3
                        )
                        for i in 0..<decoded3.count {
                            let idt0 = decoded3[i].id
                            var urlt = URL(string:self.baseURL+"/api/cv/\(idt0)")
                            var rt = URLRequest(url:urlt!)
                            rt.httpMethod = "DELETE"
                            rt.setValue("application/json", forHTTPHeaderField: "Content-Type")
                            
                            URLSession.shared.dataTask(with: rt) { data, response, error in
                                DispatchQueue.main.async {
                                    if let error = error {
                                        print(error.localizedDescription)
                                        return
                                    }
                                    
                                    guard let data0 = data else {
                                        print("Aucune réponse serveur")
                                        return
                                    }
                                    do {
                                        let decoded3 = try JSONDecoder().decode(
                                                DeleteResponse.self,
                                                from: data0
                                            )
                                    }catch{
                                        print("Erreur de la suppression du CV n°\(idt0) : "+error.localizedDescription)
                                        return
                                    }
                                }
                            }.resume()
                        }
                    }catch {
                        print("Erreur JSON : "+error.localizedDescription)
                        return
                    }
                }
            }.resume()
            
            self.url = URL(string:self.baseURL+"/api/compte/\(self.id)")!
            
            self.request = URLRequest(url: self.url)

            self.request.httpMethod = "DELETE"

            self.request.setValue("application/json", forHTTPHeaderField: "Content-Type")
            
            URLSession.shared.dataTask(with: self.request) { data, response, error in
                DispatchQueue.main.async {
                    if let error = error {
                        print(error.localizedDescription)
                        return
                    }
                    
                    guard let data5 = data else {
                        print("Aucune réponse serveur")
                        return
                    }
                    
                    do {
                        let decoded5 = try JSONDecoder().decode(
                                DeleteResponse.self,
                                from: data5
                            )
                    }catch{
                        print("Erreur de la suppression de votre compte : "+error.localizedDescription)
                        return
                    }
                }
            }.resume()
            
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
    
    override func prepare(for segue: UIStoryboardSegue, sender sneder: Any?){
        if (segue.identifier ==  "versModifProfil") {
            let destination = segue.destination as! ModifyProfileViewController

            destination.prenom = self.prenom
            destination.nom = self.nom
            destination.email = self.email
            destination.sex_selected = self.sexe
            destination.adresse = self.adresse_complete
            destination.cvpt = self.cvp
            destination.nationalite = self.nationalite
            destination.web = self.web
            destination.tel = self.tel
            destination.situation_actuelle = self.situation_actuelle
            destination.dateNaissance = self.dateNaissance
            destination.oldPassword = self.mdp
            destination.id = self.id
        } else if segue.identifier == "versCVs"{
            let destination = segue.destination as! CVsViewController
            destination.compte = self.id
        }
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
