//
//  ModifyCandidatureViewController.swift
//  candidatures_ios
//
//  Created by Ashley Rakotoarisoa on 07/06/2026.
//

import UIKit

class ModifyCandidatureViewController: UIViewController, UIPickerViewDelegate, UIPickerViewDataSource {
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 0
        //A compléter
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return 0
        //A compléter
    }
    
    var type_selected = ""
    var titre = ""
    var descrip = ""
    var e = ""

    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }
    


}
