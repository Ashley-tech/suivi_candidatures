//
//  CandidatureCell.swift
//  candidatures_ios
//
//  Created by Ashley Rakotoarisoa on 05/06/2026.
//

import UIKit

class CandidatureCell: UITableViewCell {

    @IBOutlet weak var dateLabel: UILabel!
    @IBOutlet weak var titreLabel: UILabel!
    @IBOutlet weak var statutLabel: UILabel!
    var onDelete: (() -> Void)?
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    @IBAction func delClicked(_ sender: Any) {
        onDelete?()
    }
    
}
