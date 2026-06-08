//
//  CandidaturesViewController.swift
//  candidatures_ios
//
//  Created by Ashley Rakotoarisoa on 26/05/2026.
//

import UIKit

struct CandidatureAvecOffre {
    let candidature: Candidature
    let offre: Offre
}

class CandidaturesViewController: UIViewController, UITableViewDelegate, UITableViewDataSource, UIDocumentPickerDelegate {
    let baseURL = Bundle.main.object(forInfoDictionaryKey: "API_BASE_URL") as? String ?? ""
    var compte = 0
    var candidatures : [CandidatureAvecOffre] = []
    var candidatureSelectionnee: CandidatureAvecOffre?
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return candidatures.count
    }
    
    func supprimerCandidature(
        _ candidature: Candidature,
        indexPath: IndexPath
    ) {
        let alert = UIAlertController(
            title: "Suppression",
            message: "Voulez-vous supprimer la candidature n°\(candidature.id) ?",
            preferredStyle: .alert
        )

        alert.addAction(
            UIAlertAction(
                title: "Oui",
                style: .destructive
            ) { _ in
                let url = URL(
                    string: "\(self.baseURL)/api/candidature/\(candidature.id)"
                )!

                var request = URLRequest(url: url)

                request.httpMethod = "DELETE"

                URLSession.shared.dataTask(
                    with: request
                ) { data, response, error in

                    DispatchQueue.main.async {
                        self.candidatures.remove(at: indexPath.row)
                        self.candidaturesList.deleteRows(
                            at: [indexPath],
                            with: .automatic
                        )
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
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "versNouvelleCandidature" {
            let destination = segue.destination as! NewCandidatureViewController
            destination.compte = compte
        }
        
        if segue.identifier == "versDetailsCandidature" {
                let destination = segue.destination as! DetailsCandidatureViewController
            destination.compte = compte
                destination.candidatureAvecOffre = candidatureSelectionnee
        }
    }
    
    func tableView(
        _ tableView: UITableView,
        cellForRowAt indexPath: IndexPath
    ) -> UITableViewCell {

        let cell = tableView.dequeueReusableCell(
            withIdentifier: "CandidatureCell",
            for: indexPath
        ) as! CandidatureCell

        let item = candidatures[indexPath.row]

        cell.titreLabel.text = item.offre.titre
        cell.dateLabel.text = item.candidature.date_candidature
        cell.statutLabel.text = item.candidature.statut
        
        cell.onDelete = { [weak self] in
            self?.supprimerCandidature(item.candidature, indexPath: indexPath)
        }
        
        cell.onDetails = { [weak self] in
            self?.candidatureSelectionnee = item
            self?.performSegue(
                withIdentifier: "versDetailsCandidature",
                sender: nil
            )
        }

        return cell
    }
    
    @IBOutlet weak var candidaturesList: UITableView!
    override func viewDidLoad() {
        super.viewDidLoad()

        candidaturesList.delegate = self
        candidaturesList.dataSource = self
        candidaturesList.rowHeight = 100
        
        chargerCandidatures()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        chargerCandidatures()
    }
    
    func chargerCandidatures() {
        guard let url = URL(
            string: "\(baseURL)/api/compte/\(compte)/candidatures"
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
                let candidaturesRecues = try JSONDecoder().decode(
                    [Candidature].self,
                    from: data
                )

                let group = DispatchGroup()
                var resultats: [CandidatureAvecOffre] = []
                for candidature in candidaturesRecues {
                    group.enter()
                    guard let offreURL = URL(
                        string: "\(self.baseURL)/api/offres/\(candidature.offre)"
                    ) else {
                        group.leave()
                        continue
                    }

                    URLSession.shared.dataTask(with: offreURL) {
                        data,
                        response,
                        error in

                        defer {
                            group.leave()
                        }

                        if let error = error {
                            print(error.localizedDescription)
                            return
                        }

                        guard let data = data else {
                            return
                        }
                        do {
                            let offre = try JSONDecoder().decode(
                                Offre.self,
                                from: data
                            )

                            resultats.append(
                                CandidatureAvecOffre(
                                    candidature: candidature,
                                    offre: offre
                                )
                            )
                        } catch {
                            print(error)
                        }
                    }.resume()
                }

                group.notify(queue: .main) {
                    self.candidatures = resultats
                    self.candidaturesList.reloadData()
                }
            } catch {
                print(error)
            }
        }.resume()
    }
}
