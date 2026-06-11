//
//  CVViewController.swift
//  candidatures_ios
//
//  Created by Ashley Rakotoarisoa on 04/06/2026.
//

import UIKit
import UniformTypeIdentifiers

class NewCVViewController: UIViewController, UIDocumentPickerDelegate {
    @IBOutlet weak var loadingIcon: UIActivityIndicatorView!
    @IBOutlet weak var message_result: UILabel!
    var selectedFileURL: URL?
    var compte = 0
    let baseURL = Bundle.main.object(forInfoDictionaryKey: "API_BASE_URL") as? String ?? ""
    @IBOutlet weak var file_selected: UILabel!
    @IBAction func selectFile(_ sender: Any) {
        let picker = UIDocumentPickerViewController(
                forOpeningContentTypes: [
                    .pdf,
                    UTType(filenameExtension: "doc")!,
                    UTType(filenameExtension: "docx")!,
                    UTType(filenameExtension: "odt")!
                ]
            )

            picker.delegate = self

            present(
                picker,
                animated: true
            )
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        file_selected.text = "Aucun fichier sélectionné"
        navigationItem.hidesBackButton = true
        loadingIcon.isHidden = true
    }
    
    func documentPicker(
        _ controller: UIDocumentPickerViewController,
        didPickDocumentsAt urls: [URL]
    ) {

        guard let url = urls.first else {
            return
        }

        selectedFileURL = url

        file_selected.text = url.lastPathComponent
    }
    
    @IBAction func add(_ sender: Any) {
        guard let fileURL = selectedFileURL else {
            message_result.text = "Veuillez choisir un fichier"
            message_result.textColor = .red
            return
        }
        print(fileURL)
        loadingIcon.startAnimating()
        loadingIcon.isHidden = false
        envoyerCV(fileURL)
    }
    
    func envoyerCV(_ fileURL: URL) {
        guard let url = URL(string: "\(baseURL)/api/cv/upload") else {
            return
        }

        var request = URLRequest(url: url)
        request.httpMethod = "POST"

        let boundary = UUID().uuidString

        request.setValue(
            "multipart/form-data; boundary=\(boundary)",
            forHTTPHeaderField: "Content-Type"
        )

        var body = Data()

        // ==========================
        // compte
        // ==========================

        body.append("--\(boundary)\r\n".data(using: .utf8)!)
        body.append("Content-Disposition: form-data; name=\"compte\"\r\n\r\n".data(using: .utf8)!)
        body.append("\(compte)\r\n".data(using: .utf8)!)

        do {
            guard fileURL.startAccessingSecurityScopedResource() else {
                self.loadingIcon.stopAnimating()
                self.loadingIcon.isHidden = true
                return
            }

            defer {
                fileURL.stopAccessingSecurityScopedResource()
            }
            let fileData = try Data(contentsOf: fileURL)

            let mimeType =
                UTType(filenameExtension: fileURL.pathExtension)?
                .preferredMIMEType
                ?? "application/octet-stream"

            // ==========================
            // cv
            // ==========================

            body.append("--\(boundary)\r\n".data(using: .utf8)!)
            body.append(
                "Content-Disposition: form-data; name=\"cv\"; filename=\"\(fileURL.lastPathComponent)\"\r\n"
                    .data(using: .utf8)!
            )

            body.append(
                "Content-Type: \(mimeType)\r\n\r\n"
                    .data(using: .utf8)!
            )

            body.append(fileData)
            body.append("\r\n".data(using: .utf8)!)

            // ==========================
            // fin multipart
            // ==========================

            body.append("--\(boundary)--\r\n".data(using: .utf8)!)
        } catch {self.loadingIcon.stopAnimating()
            self.loadingIcon.isHidden = true
            print("Erreur lecture fichier :", error)
            return
        }

        request.httpBody = body

        URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                DispatchQueue.main.async {
                    self.message_result.text = error.localizedDescription
                    self.message_result.textColor = .red
                }
                self.loadingIcon.stopAnimating()
                self.loadingIcon.isHidden = true

                return
            }

            if let response = response as? HTTPURLResponse {
                print("Status :", response.statusCode)

                print("Headers :")
                response.allHeaderFields.forEach {
                    print("\($0.key) : \($0.value)")
                }

                print("URL finale :", response.url?.absoluteString ?? "")
            }

            guard let data = data else {
                self.loadingIcon.stopAnimating()
                self.loadingIcon.isHidden = true
                return
            }

            print(String(data: data, encoding: .utf8) ?? "Réponse illisible")

            DispatchQueue.main.async {
                do {
                    let decoded = try JSONDecoder().decode(
                        AddCVR.self,
                        from: data
                    )
                    self.loadingIcon.stopAnimating()
                    self.loadingIcon.isHidden = true

                    if decoded.success {
                        self.message_result.text = "CV envoyé avec succès"
                        self.message_result.textColor = .systemGreen

                        DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
                            self.navigationController?.popViewController(animated: true)
                        }
                    } else {
                        self.message_result.text = "Erreur lors de l'envoi"
                        self.message_result.textColor = .red
                    }

                } catch {
                    self.loadingIcon.stopAnimating()
                    self.loadingIcon.isHidden = true
                    self.message_result.text = "Réponse serveur invalide"
                    self.message_result.textColor = .red

                    print("Erreur JSON :", error)
                }
            }

        }.resume()
    }
}
