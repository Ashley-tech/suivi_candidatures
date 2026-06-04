//
//  CVCell.swift
//  candidatures_ios
//
//  Created by Ashley Rakotoarisoa on 04/06/2026.
//

import UIKit

class CVCell: UITableViewCell {

    @IBOutlet weak var filel: UILabel!
    @IBOutlet weak var idl: UILabel!
    
    var onDelete: (() -> Void)?
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }
    
    @IBAction func delClicked(_ sender: Any) {
        onDelete?()
    }
    
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
