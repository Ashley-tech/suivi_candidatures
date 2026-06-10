//
//  DetailsCandidatureViewController.swift
//  candidatures_ios
//
//  Created by Ashley Rakotoarisoa on 07/06/2026.
//

import UIKit

class DetailsCandidatureViewController: UIViewController {
    
    @IBOutlet weak var dp: UILabel!
    @IBOutlet weak var score_matched: UILabel!
    @IBOutlet weak var dc: UILabel!
    @IBOutlet weak var statut: UILabel!
    @IBOutlet weak var salaire: UILabel!
    @IBOutlet weak var periode: UILabel!
    @IBOutlet weak var tel_recruteur: UILabel!
    @IBOutlet weak var email_recruteur: UILabel!
    @IBOutlet weak var nom_recruteur: UILabel!
    @IBOutlet weak var payst: UILabel!
    @IBOutlet weak var villet: UILabel!
    @IBOutlet weak var cpt: UILabel!
    @IBOutlet weak var adresseT: UILabel!
    @IBOutlet weak var descript: UITextView!
    @IBOutlet weak var entreprise: UILabel!
    @IBOutlet weak var titleInter: UILabel!
    @IBOutlet weak var typeO: UILabel!
    @IBOutlet weak var smBtn: UIButton!
    var idCV = 0
    var candidatureAvecOffre: CandidatureAvecOffre?
    var compte = 0
    let baseURL = Bundle.main.object(forInfoDictionaryKey: "API_BASE_URL") as? String ?? ""
    var score = ""
    var type = ""
    var titre = ""
    var d = ""
    var ne = ""
    var adresse = ""
    var cadresse = ""
    var cp = ""
    var ville = ""
    var pays = ""
    var nr = ""
    var pr = ""
    var er = ""
    var tr = ""
    var st = ""
    var salaireMin: Double? = nil
    var salaireMax: Double? = nil
    var p = ""
    var dateCandidature = ""
    var datePublication = ""
    var offre = 0
    override func viewDidLoad() {
        super.viewDidLoad()
        
        descript.isEditable = false
        descript.isScrollEnabled = true
        
        chargerCandidature()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        chargerCandidature()
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "versModifCandidature" {
            let destination = segue.destination as! ModifyCandidatureViewController
            destination.descrip = d
            destination.titre = titre
            destination.type_selected = type
            destination.dateCandidature = dateCandidature
            destination.statut = st
            destination.datePublication = datePublication
            destination.idCVselected = idCV
            if pays != "" {
                destination.e = "\(ne)/\(adresse)/\(cadresse)/\(cp)/\(ville)/\(pays)"
            } else if ville != "" {
                destination.e = "\(ne)/\(adresse)/\(cadresse)/\(cp)/\(ville)"
            } else if cp != "" {
                destination.e = "\(ne)/\(adresse)/\(cadresse)/\(cp)"
            } else if cadresse != "" {
                destination.e = "\(ne)/\(adresse)/\(cadresse)"
            } else if adresse != "" {
                destination.e = "\(ne)/\(adresse)"
            } else if ne != "" {
                destination.e = ne
            }
            destination.periode = p
            if tr != "" {
                destination.r = "\(nr)/\(pr)/\(er)/\(tr)"
            } else if er != "" {
                destination.r = "\(nr)/\(pr)/\(er)"
            } else if pr != "" {
                destination.r = "\(nr)/\(pr)"
            } else if nr != "" {
                destination.r = nr
            }
            destination.sma = salaireMax != nil ? String(salaireMax!) : ""
            destination.smi = salaireMin != nil ? String(salaireMin!) : ""
            destination.compte = compte
            destination.offre = offre
            destination.id = candidatureAvecOffre?.candidature.id ?? 0
        }
    }
    
    func chargerCandidature() {
        guard let url = URL(
            string: "\(baseURL)/api/candidature/\(candidatureAvecOffre?.candidature.id ?? 0)"
        ) else {
            return
        }

        URLSession.shared.dataTask(with: url) { data, response, error in
            if let error = error {
                print(error.localizedDescription)
                return
            }
            
            guard let data = data else {
                return
            }
            
            do {
                let decoded = try JSONDecoder().decode(
                    Candidature.self,
                    from: data
                )
                DispatchQueue.main.async {
                    self.st = decoded.statut!
                    self.score = decoded.score_matching ?? ""
                    self.dateCandidature = decoded.date_candidature
                    self.idCV = decoded.cv
                    
                    self.statut.text = "Statut : \(self.st)"
                    self.score_matched.text = "Score : \(self.score ?? "")"
                    if self.score != "" {
                        self.smBtn.setTitle("Recalculer le score de matching", for: .normal)
                    } else {
                        self.smBtn.setTitle("Calculer le score de matching", for: .normal)

                    }
                    self.dc.text = "Date de candidature : \(self.dateCandidature)"
                }
                let offreId = decoded.offre

                guard let url = URL(
                    string: "\(self.baseURL)/api/offres/\(offreId)"
                ) else {
                    return
                }

                URLSession.shared.dataTask(with: url) { data, response, error in
                    if let error = error {
                        print(error.localizedDescription)
                        return
                    }
                    
                    guard let data = data else {
                        return
                    }
                    
                    do {
                        let decoded0 = try JSONDecoder().decode(
                            Offre.self,
                            from: data
                        )
                        DispatchQueue.main.async {
                            self.type = decoded0.type ?? ""
                            self.titre = decoded0.titre
                            self.d = decoded0.description ?? ""
                            self.ne = decoded0.nom_entreprise ?? ""
                            self.adresse = decoded0.adresse_entreprise ?? ""
                            self.cadresse = decoded0.adresse_comp_entreprise ?? ""
                            self.cp = decoded0.cp_entreprise ?? ""
                            self.ville = decoded0.ville_entreprise ?? ""
                            self.pays = decoded0.pays_entreprise ?? ""
                            self.p = decoded0.periode ?? ""
                            self.nr = decoded0.nom_recruteur ?? ""
                            self.pr = decoded0.prenom_recruteur ?? ""
                            self.er = decoded0.email_entreprise ?? ""
                            self.tr = decoded0.tel_entreprise ?? ""
                            self.salaireMin = decoded0.salaire_min
                            self.salaireMax = decoded0.salaire_max
                            self.datePublication = decoded0.date_publication ?? ""
                            
                            self.titleInter.text = "Détails de votre candidature pour l'offre : \n\(self.titre)"
                            self.typeO.text = "Type de l'offre : \(self.type)"
                            self.descript.text = self.d
                            self.entreprise.text = "Entreprise : \(self.ne)"
                            if (self.cadresse != "") {
                                self.adresseT.text = "Adresse : \(self.adresse) - \(self.cadresse)"
                            } else {
                                self.adresseT.text = "Adresse : \(self.adresse)"
                            }
                            self.cpt.text = "Code postal : \(self.cp)"
                            self.villet.text = "Ville : \(self.ville)"
                            self.payst.text = "Pays : \(self.pays)"
                            self.nom_recruteur.text = "Recruteur : \(self.pr) \(self.nr.uppercased())"
                            self.tel_recruteur.text = "Téléphone du recruteur : \(self.tr)"
                            self.email_recruteur.text = "E-mail du recruteur : \(self.er)"
                            self.periode.text = "Période : \(self.p)"
                            if self.salaireMax != nil && self.salaireMin != nil {
                                self.salaire.text = "Salaire : Entre \(self.salaireMin) et \(self.salaireMax)"
                            } else if self.salaireMax == nil && self.salaireMin != nil {
                                self.salaire.text = "Salaire : A partir de \(self.salaireMin)"
                            } else if self.salaireMax != nil && self.salaireMin == nil {
                                self.salaire.text = "Salaite = Jusqu'à \(self.salaireMax)"
                            }
                            self.dp.text = "Date de publication : \(self.datePublication)"
                        }
                    }catch {
                        print(error)
                    }
                }.resume()
            }catch{
                print(error)
            }
        }.resume()
    }
    
    @IBAction func supprimerCandidature(_ sender: Any) {
        let alert = UIAlertController(
            title: "Suppression",
            message: "Voulez-vous supprimer la candidature n°\(candidatureAvecOffre?.candidature.id ?? 0) ?",
            preferredStyle: .alert
        )

        alert.addAction(
            UIAlertAction(
                title: "Oui",
                style: .destructive
            ) { _ in
                let url = URL(
                    string: "\(self.baseURL)/api/candidature/\(self.candidatureAvecOffre?.candidature.id ?? 0)"
                )!

                var request = URLRequest(url: url)

                request.httpMethod = "DELETE"

                URLSession.shared.dataTask(with: request) { data, response, error in
                    DispatchQueue.main.async {
                        guard let data = data else {
                            return
                        }

                        do {
                            let decoded = try JSONDecoder().decode(
                                DeleteResponse.self,
                                from: data
                            )

                            print(decoded)

                            self.navigationController?.popViewController(animated: true)

                        } catch {
                            print("Erreur JSON :", error)
                        }
                    }
                }.resume()
            }
        )
        
        alert.addAction(
            UIAlertAction(
                title: "Non",
                style: .default
            )
        )
        present(alert, animated: true)
        
    }
    @IBAction func calculerScoreMatching(_ sender: Any) {
        if score == nil || score == "" {
            let url = URL(
                string: "\(baseURL)/api/candidature/\(candidatureAvecOffre?.candidature.id ?? 0)/save-score")
            var request = URLRequest(url: url!)
                request.httpMethod = "PATCH"
                request.setValue("application/json", forHTTPHeaderField: "Content-Type")

            URLSession.shared.dataTask(with: request) { data, response, error in
                if let error = error {
                    print(error.localizedDescription)
                    return
                }
                
                guard let data = data else {
                    return
                }
                
                do {
                    let decoded = try JSONDecoder().decode(NewScore.self, from: data)
                    DispatchQueue.main.async {
                        self.score_matched.text = "Score : \(decoded.score)"
                        self.smBtn.setTitle("Recalculer le score de matching", for: .normal)
                    }
                }catch{
                    print(error)
                }
            }.resume()
        } else {
            let alert = UIAlertController(
                title: "Calcul du score matching",
                message: "Êtes-vous sûr de vouloir recommencer le matching score ?",
                preferredStyle: .alert
            )

            alert.addAction(
                UIAlertAction(
                    title: "Oui",
                    style: .destructive
                ) { _ in
                    let url = URL(
                        string: "\(self.baseURL)/api/candidature/\(self.candidatureAvecOffre?.candidature.id ?? 0)/save-score")
                    var request = URLRequest(url: url!)
                        request.httpMethod = "PATCH"
                        request.setValue("application/json", forHTTPHeaderField: "Content-Type")

                    URLSession.shared.dataTask(with: request) { data, response, error in
                        if let error = error {
                            print(error.localizedDescription)
                            return
                        }
                        
                        guard let data = data else {
                            return
                        }
                        
                        do {
                            let decoded = try JSONDecoder().decode(NewScore.self, from: data)
                            DispatchQueue.main.async {
                                self.score_matched.text = "Score : \(decoded.score)"
                                self.smBtn.setTitle("Recalculer le score de matching", for: .normal)
                            }
                        }catch{
                            print(error)
                        }
                    }.resume()
                }
            )
            
            alert.addAction(
                UIAlertAction(
                    title: "Non",
                    style: .default
                )
            )
            present(alert, animated: true)
        }
    }
}
