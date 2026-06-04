//
//  CVsViewController.swift
//  candidatures_ios
//
//  Created by Ashley Rakotoarisoa on 04/06/2026.
//

import UIKit
import UniformTypeIdentifiers

class CVsViewController: UIViewController, UITableViewDelegate, UITableViewDataSource, UIDocumentPickerDelegate {
    @IBOutlet weak var cvlist: UITableView!
    var cvs: [CVResponse] = []
    var downloadedFileURL : URL?
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
        
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        
        cell.tilel.text = "\(cv.id) - \(cv.nom)"
        let inputFormatter = DateFormatter()
        inputFormatter.dateFormat = "yyyy-MM-dd HH:mm:ss"

        let outputFormatter = DateFormatter()
        outputFormatter.dateFormat = "yyyy-MM-dd"

        if let date = inputFormatter.date(from: cv.date_upload) {
            cell.uploadel.text = "Date upload : \(outputFormatter.string(from: date))"
        } else {
            cell.uploadel.text = "Date upload : \(cv.date_upload)"
        }
        
        cell.onDelete = { [weak self] in
            self?.supprimerCV(cv, indexPath: indexPath)
        }
        
        cell.onDownload = {[weak self] in self?.downloadCV(cv, indexPath: indexPath)
        }

        return cell
    }
    
    func downloadCV(_ cv: CVResponse, indexPath: IndexPath) {
        let url = URL(
            string: "\(baseURL)/api/cv/\(cv.id)/download"
        )!

        var request = URLRequest(url: url)
        request.httpMethod = "GET"

        URLSession.shared.downloadTask(with: request) { localURL, response, error in

            guard let localURL = localURL else {
                print("Erreur téléchargement")
                return
            }

            do {

                let documentsURL = FileManager.default.urls(
                    for: .documentDirectory,
                    in: .userDomainMask
                ).first!

                let destinationURL = documentsURL.appendingPathComponent(cv.nom)

                if FileManager.default.fileExists(
                    atPath: destinationURL.path
                ) {
                    try FileManager.default.removeItem(
                        at: destinationURL
                    )
                }

                try FileManager.default.copyItem(
                    at: localURL,
                    to: destinationURL
                )

                DispatchQueue.main.async {

                    self.downloadedFileURL = destinationURL

                    let picker = UIDocumentPickerViewController(
                        forExporting: [destinationURL]
                    )

                    picker.delegate = self

                    self.present(
                        picker,
                        animated: true
                    )
                }
            } catch {
                print("Erreur :", error)
            }
        }.resume()
    }
    
    func documentPicker(
        _ controller: UIDocumentPickerViewController,
        didPickDocumentsAt urls: [URL]
    ) {
        print("Export terminé")
    }

    func documentPickerWasCancelled(
        _ controller: UIDocumentPickerViewController
    ) {
        print("Export annulé")
    }
    
    func supprimerCV(
        _ cv: CVResponse,
        indexPath: IndexPath
    ) {

        let alert = UIAlertController(
            title: "Suppression",
            message: "Voulez-vous supprimer le CV \(cv.nom) ?",
            preferredStyle: .alert
        )

        alert.addAction(
            UIAlertAction(
                title: "Oui",
                style: .destructive
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
        
        alert.addAction(
            UIAlertAction(
                title: "Non",
                style: .default
            )
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
