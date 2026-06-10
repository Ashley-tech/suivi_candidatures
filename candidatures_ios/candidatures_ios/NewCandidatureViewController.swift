//
//  NewCandidatureViewController.swift
//  candidatures_ios
//
//  Created by Ashley Rakotoarisoa on 06/06/2026.
//

import UIKit

class NewCandidatureViewController: UIViewController, UIPickerViewDelegate, UIPickerViewDataSource {
    @IBOutlet weak var message_result: UILabel!
    @IBOutlet weak var cvs: UIPickerView!
    @IBOutlet weak var salaireMax: UITextField!
    @IBOutlet weak var salaireMin: UITextField!
    @IBOutlet weak var periode: UITextField!
    @IBOutlet weak var recruteur: UITextField!
    @IBOutlet weak var statut: UITextField!
    @IBOutlet weak var descrip: UITextView!
    @IBOutlet weak var dateCandidature: UIDatePicker!
    @IBOutlet weak var datePublic: UIDatePicker!
    @IBOutlet weak var entreprise: UITextField!
    @IBOutlet weak var type: UIPickerView!
    @IBOutlet weak var titre: UITextField!
    
    let types = ["(Pas de type défini)","Alternance","Stage","CDI","CDD","Freelance","Autre"]
    
        var listeCVs: [CVResponse] = []
    
    var compte = 0
    let baseURL = Bundle.main.object(forInfoDictionaryKey: "API_BASE_URL") as? String ?? ""
    var type_selected = ""
    var idCVselected: Int = 0
    
    override func viewDidLoad() {
        super.viewDidLoad()

        type.delegate = self
        type.dataSource = self
        cvs.delegate = self
        cvs.dataSource = self
        
        salaireMin.keyboardType = .numberPad
        salaireMax.keyboardType = .numberPad
        
        dateCandidature.datePickerMode = .date
        dateCandidature.preferredDatePickerStyle = .compact
        
        datePublic.datePickerMode = .date
        datePublic.preferredDatePickerStyle = .compact
        
        let url = URL(string: "\(baseURL)/api/compte/\(compte)/cvs")!

        var request = URLRequest(url: url)
        request.httpMethod = "GET"

        URLSession.shared.dataTask(with: request) { data, response, error in
            guard let data = data else {
                return
            }

            do {
                let decoded = try JSONDecoder().decode(
                    [CVResponse].self,
                    from: data
                )

                var cvsAvecVide = decoded

                cvsAvecVide.insert(
                    CVResponse(
                        id: 0,
                        nom: "(Aucun CV sélectionné)",
                        download_url: "",
                        mime_type: "",
                        date_upload: "",
                        visible: 1
                    ),
                    at: 0
                )

                DispatchQueue.main.async {
                    self.listeCVs = cvsAvecVide
                    self.idCVselected = 0
                    self.cvs.reloadAllComponents()
                    self.cvs.selectRow(0, inComponent: 0, animated: false)
                }
            } catch {
                print("Erreur JSON :", error)
            }
        }.resume()
        
        type_selected = types[0]
    }
    
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
            return 1
        }

        func pickerView(
            _ pickerView: UIPickerView,
            numberOfRowsInComponent component: Int
        ) -> Int {
            if pickerView == type {
                return types.count
            }

            if pickerView == cvs {
                return listeCVs.count
            }

            return 0
        }

    func pickerView(
        _ pickerView: UIPickerView,
        titleForRow row: Int,
        forComponent component: Int
    ) -> String? {

        if pickerView == type {
            guard row < types.count else { return nil }
            return types[row]
        }

        if pickerView === cvs {
            let cv = listeCVs[row]

            if cv.id == 0 {
                return cv.nom
            }

            return "\(cv.id). \(cv.nom)"
        }

        return nil
    }
    
    @IBAction func ajouterCandidature(_ sender: Any) {
        if (titre.text?.isEmpty ?? true) || idCVselected == 0 {
            message_result.text = "Vous devez saisir un titre et sélectionner un CV"
            message_result.textColor = .red
            return
        }
        var part_champ = entreprise.text?.components(separatedBy: "/")
        guard let parts = part_champ else {
            return
        }
        guard parts.count <= 6 else {
            message_result.text = "Merci de respecter le format de l'entreprise [Nom]/[Adresse]/[Complément d'adresse]/[Code postal]/[Ville]/[Pays] car il y a trop d'arguments : \(parts.count) > 6"
            message_result.textColor = .red
            return
        }

        let e    = parts.count > 0 ? parts[0] : ""
        let adr  = parts.count > 1 ? parts[1] : ""
        let cadr = parts.count > 2 ? parts[2] : ""
        let cp   = parts.count > 3 ? parts[3] : ""
        let v    = parts.count > 4 ? parts[4] : ""
        let p    = parts.count > 5 ? parts[5] : ""
        let regexTel = #"^\d{10}$"#
        let regexCp = #"^\d{5}$"#
        
        if !cp.isEmpty && !regexCheck(regexCp, cp) {
            message_result.textColor = .red
            message_result.text = "Le code postal de l'entreprise saisi doit exactement avoir 5 chiffres."
            return
        }
        let formatter = DateFormatter()
        
        formatter.dateFormat = "yyyy-MM-dd"
        
        let formattedDateC = formatter.string(from: dateCandidature.date)
        let formattedDateP = formatter.string(from: datePublic.date)
        
        part_champ = recruteur.text?.components(separatedBy: "/")
        guard let parts = part_champ else {
            return
        }
        guard parts.count <= 4 else {
            message_result.text = "Merci de respecter le format du recruteur  [Nom]/[Prénom]/[E-mail]/[Téléphone] car il y a trop d'arguments : \(parts.count) > 4"
            message_result.textColor = .red
            return
        }
        let nr = parts.count > 0 ? parts[0] : ""
        let pr = parts.count > 1 ? parts[1] : ""
        let er = parts.count > 2 ? parts[2] : ""
        let tr = parts.count > 3 ? parts[3] : ""
        let st = statut.text != "" ? statut.text! : "En attente"
        let regexMail = #"^[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$"#
        
        if !er.isEmpty {
            guard regexCheck(regexMail, er) else {
                message_result.text = "Le mail du recruteur que vous avez saisi ne respecte pas la norme classique"
                message_result.textColor = .red
                return
            }
        }
        
        if !tr.isEmpty && !regexCheck(regexTel, tr){
            message_result.text = "Le numéro de téléphone du recruteur doit être composé d'exactement 10 chiffres"
            message_result.textColor = .red
            return
        }
        
        let regexSalaire = #"^\d+(?:[.,]\d{1,2})?$"#
        
        if let salaire = salaireMin.text,
           !salaire.isEmpty,
           !regexCheck(regexSalaire, salaire) {
            message_result.text = "Le salaire minimum doit être un nombre avec au maximum 2 décimales."
            message_result.textColor = .red
            return
        }
        
        if let salaire = salaireMax.text,
           !salaire.isEmpty,
           !regexCheck(regexSalaire, salaire) {
            message_result.text = "Le salaire maximum doit être un nombre avec au maximum 2 décimales."
            message_result.textColor = .red
            return
        }
        
        if let salaireMinText = salaireMin.text,
           let salaireMaxText = salaireMax.text,
           !salaireMinText.isEmpty,
           !salaireMaxText.isEmpty,
           let salaireMinValue = Float(salaireMinText.replacingOccurrences(of: ",", with: ".")),
           let salaireMaxValue = Float(salaireMaxText.replacingOccurrences(of: ",", with: ".")) {

            if salaireMaxValue < salaireMinValue {
                message_result.text = "Le salaire maximum doit être supérieur ou égal au salaire minimum."
                message_result.textColor = .red
                return
            }
        }
        
        if formattedDateC < formattedDateP {
            message_result.text = "La date de publication de l'offre doit être antérieure ou égale à la date de candidature."
            message_result.textColor = .red
            return
        }
        
        var url = URL(string: baseURL+"/api/offres")
        var request = URLRequest(url: url!)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        var body: [String:Any] = [
            "type": type_selected,
            "titre": titre.text ?? "",
            "description": descrip.text ?? "",
            "nom_entreprise": e,
            "adresse_entreprise": adr,
            "adresse_comp_entreprise": cadr,
            "cp_entreprise": cp,
            "ville_entreprise": v,
            "pays_entreprise": p,
            "nom_recruteur": nr,
            "prenom_recruteur": pr,
            "email_entreprise": er,
            "tel_entreprise":tr,
            "periode":periode.text ?? "",
            "salaire_min": salaireMin.text ?? "",
            "salaire_max": salaireMax.text ?? "",
            "statut": st,
            "date_publication": formattedDateP
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
                    let decoded = try JSONDecoder().decode(OffreResponse.self, from: data)
                    print(decoded)
                    
                    let offre = decoded.offre_id!
                    
                    url = URL(string: self.baseURL+"/api/candidatures")
                    request = URLRequest(url: url!)
                    request.httpMethod = "POST"
                    request.setValue("application/json", forHTTPHeaderField: "Content-Type")
                    body = [
                        "offre": offre,
                        "compte": self.compte,
                        "date_candidature": formattedDateC,
                        "statut": st,
                        "cv": self.idCVselected,
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
                                let decoded0 = try JSONDecoder().decode(CandidatureResponse.self, from: data)
                                print(decoded0)
                                    self.message_result.text = "Candidature ajoutée avec succès !"
                                self.message_result.textColor = .green;
                                DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
                                        self.navigationController?.popViewController(animated: true)
                                    }
                            }catch{
                                print(error)
                                DispatchQueue.main.async {
                                    self.message_result.text = "Erreur parsing JSON"
                                    self.message_result.textColor = .red
                                }
                            }
                        }
                    }.resume()
                }catch{
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
    
    func pickerView(
        _ pickerView: UIPickerView,
        didSelectRow row: Int,
        inComponent component: Int
    ) {
        if pickerView == type {
            guard row < types.count else { return }
            type_selected = row > 0 ? types[row] : ""
            print ("Type de contrat : \(type_selected)")
        }

        if pickerView == cvs {
            guard row < listeCVs.count else { return }
            idCVselected = listeCVs[row].id
            print("ID du CV selectionné : \(idCVselected)")
        }
    }
}
