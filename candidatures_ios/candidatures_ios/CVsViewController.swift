//
//  CVsViewController.swift
//  candidatures_ios
//
//  Created by Ashley Rakotoarisoa on 04/06/2026.
//

import UIKit

class CVsViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
    @IBOutlet weak var cvlist: UITableView!
    var cvs: [CVResponse] = []
    let baseURL = Bundle.main.object(forInfoDictionaryKey: "API_BASE_URL") as? String ?? ""
    var compte = 0
    override func viewDidLoad() {
        super.viewDidLoad()

        cvlist.delegate = self
        cvlist.dataSource = self
        cvlist.rowHeight = 90

        chargerCVs()
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return cvs.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "CVCell",for: indexPath) as! CVCell
        
        let cv = cvs[indexPath.row]
        
        cell.idl.text = String(cv.id)
        cell.filel.text = cv.nom
        
        cell.onDelete = { [weak self] in
            self?.supprimerCV(cv, indexPath: indexPath)
        }

        return cell
    }
    
    func supprimerCV(
        _ cv: CVResponse,
        indexPath: IndexPath
    ) {

        let alert = UIAlertController(
            title: "Suppression",
            message: "Voulez-vous supprimer ce CV ?",
            preferredStyle: .alert
        )

        alert.addAction(
            UIAlertAction(
                title: "Non",
                style: .cancel
            )
        )

        alert.addAction(
            UIAlertAction(
                title: "Oui",
                style: .default
            ) { _ in

                let url = URL(
                    string: "\(self.baseURL)/api/cv/\(cv.id)"
                )!

                var request = URLRequest(url: url)

                request.httpMethod = "DELETE"

                URLSession.shared.dataTask(
                    with: request
                ) { data, response, error in

                    DispatchQueue.main.async {

                        self.cvs.remove(at: indexPath.row)

                        self.cvlist.deleteRows(
                            at: [indexPath],
                            with: .automatic
                        )
                    }

                }.resume()
            }
        )
        present(alert, animated: true)
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        chargerCVs()
    }
    
    func chargerCVs() {

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

                DispatchQueue.main.async {
                    self.cvs = decoded
                    self.cvlist.reloadData()
                }

            } catch {
                print(error)
            }

        }.resume()
    }
}
