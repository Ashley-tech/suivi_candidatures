//
//  CVViewController.swift
//  candidatures_ios
//
//  Created by Ashley Rakotoarisoa on 04/06/2026.
//

import UIKit
import UniformTypeIdentifiers

class NewCVViewController: UIViewController, UIDocumentPickerDelegate {
    @IBOutlet weak var message_result: UILabel!
    var selectedFileURL: URL?
    var compte = 0
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
    }
}
