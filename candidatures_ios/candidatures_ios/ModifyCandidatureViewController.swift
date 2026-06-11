//
//  ModifyCandidatureViewController.swift
//  candidatures_ios
//
//  Created by Ashley Rakotoarisoa on 07/06/2026.
//

import UIKit

class ModifyCandidatureViewController: UIViewController, UIPickerViewDelegate, UIPickerViewDataSource {
    
    var type_selected = ""
    var titre = ""
    var descrip = ""
    var e = ""
    var dateCandidature = ""
    var datePublication = ""
    var sma = ""
    var smi = ""
    var periode = ""
    var r = ""
    var statut = ""
    var compte = 0
    var offre = 0
    var id = 0
    let types = ["(Pas de type défini)","Alternance","Stage","CDI","CDD","Freelance","Autre"]
    let baseURL = Bundle.main.object(forInfoDictionaryKey: "API_BASE_URL") as? String ?? ""
    
    @IBOutlet weak var message_result: UILabel!
    @IBOutlet weak var statutm: UITextField!
    @IBOutlet weak var em: UITextField!
    @IBOutlet weak var descriptionm: UITextView!
    @IBOutlet weak var titlem: UITextField!
    @IBOutlet weak var recruteur: UITextField!
    @IBOutlet weak var dcm: UIDatePicker!
    @IBOutlet weak var salaireMax: UITextField!
    @IBOutlet weak var salaireMin: UITextField!
    @IBOutlet weak var periodem: UITextField!
    @IBOutlet weak var cvs: UIPickerView!
    @IBOutlet weak var dpm: UIDatePicker!
    @IBOutlet weak var typem: UIPickerView!
    var idCVselected: Int = 0
    var listeCVs: [CVResponse] = []
    
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
            return 1
        }

        func pickerView(
            _ pickerView: UIPickerView,
            numberOfRowsInComponent component: Int
        ) -> Int {
            if pickerView == typem {
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

        if pickerView == typem {
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
    
    func pickerView(
        _ pickerView: UIPickerView,
        didSelectRow row: Int,
        inComponent component: Int
    ) {
        if pickerView == typem {
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
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        typem.delegate = self
        typem.dataSource = self
        cvs.delegate = self
        cvs.dataSource = self
        navigationItem.hidesBackButton = true
        
        salaireMin.keyboardType = .numberPad
        salaireMax.keyboardType = .numberPad

        dcm.datePickerMode = .date
        dcm.preferredDatePickerStyle = .compact
        
        dpm.datePickerMode = .date
        dpm.preferredDatePickerStyle = .compact
        
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"

        if let date = formatter.date(from: dateCandidature) {
            dcm.date = date
        }
        if let date = formatter.date(from: datePublication) {
            dpm.date = date
        }
        em.text = e
        recruteur.text = r
        descriptionm.text = descrip
        titlem.text = titre
        statutm.text = statut
        periodem.text = periode
        salaireMax.text = sma
        salaireMin.text = smi
        if type_selected == "Alternance"{
            typem.selectRow(1, inComponent: 0, animated: false)
        } else if type_selected == "CDI" {
            typem.selectRow(3, inComponent: 0, animated: false)
        } else if type_selected == "CDD" {
            typem.selectRow(4, inComponent: 0, animated: false)
        } else if type_selected == "Stage" {
            typem.selectRow(2, inComponent: 0, animated: false)
        } else if type_selected == "Freelance" {
            typem.selectRow(5, inComponent: 0, animated: false)
        } else if type_selected == "Autre" {
            typem.selectRow(6, inComponent: 0, animated: false)
        } else {
            typem.selectRow(0, inComponent: 0, animated: false)
        }
        
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
                    self.cvs.reloadAllComponents()
                    let indexc = self.listeCVs.firstIndex(where: { $0.id == self.idCVselected })
                    if let index = indexc {
                        let cv = self.listeCVs[index]
                        self.cvs.selectRow(index, inComponent: 0, animated: false)
                    }
                }
            } catch {
                print("Erreur JSON :", error)
            }
        }.resume()
        
    }
    
    func regexCheck(_ regex: String, _ str: String) -> Bool {
        return str.range(of: regex, options: .regularExpression) != nil
    }
    
    @IBAction func modifierCandidature(_ sender: Any) {
        if (titlem.text?.isEmpty ?? true) || idCVselected == 0 {
            message_result.text = "Vous devez saisir un titre et sélectionner un CV"
            message_result.textColor = .red
            return
        }
        var part_champ = em.text?.components(separatedBy: "/")
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
        
        let formattedDateC = formatter.string(from: dcm.date)
        let formattedDateP = formatter.string(from: dpm.date)
        
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
        let st = statutm.text != "" ? statutm.text! : "En attente"
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
        
        print("ID candidature :", self.id)
        print("ID offre :", self.offre)
        print("ID compte :", self.compte)

        var url = URL(string: baseURL+"/api/offres/\(self.offre)")
        var request = URLRequest(url: url!)
        request.httpMethod = "PATCH"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        var body: [String:Any] = [
            "type": type_selected,
            "titre": titlem.text ?? "",
            "description": descriptionm.text ?? "",
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
            "periode":periodem.text ?? "",
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
                    let decoded = try JSONDecoder().decode(UpdateOffreResponse.self, from: data)
                    print(decoded)
                    
                    url = URL(string: self.baseURL+"/api/candidature/\(self.id)")
                    request = URLRequest(url: url!)
                    request.httpMethod = "PATCH"
                    request.setValue("application/json", forHTTPHeaderField: "Content-Type")
                    body = [
                        "offre": self.offre,
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
                                let decoded0 = try JSONDecoder().decode(UpdateCandidatureResponse.self, from: data)
                                print(decoded0)
                                    self.message_result.text = "Candidature modifiée avec succès !"
                                self.message_result.textColor = .green;
                                DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
                                        self.navigationController?.popViewController(animated: true)
                                    }
                            }catch{
                                print(error)
                                DispatchQueue.main.async {
                                    self.message_result.text = "Erreur parsing JSON 2"
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
                    print(error)
                }
            }
        }.resume()
    }
    
}
