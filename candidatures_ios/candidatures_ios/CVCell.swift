//
//  CVCell.swift
//  candidatures_ios
//
//  Created by Ashley Rakotoarisoa on 04/06/2026.
//

import UIKit

class CVCell: UITableViewCell {

    @IBOutlet weak var uploadel: UILabel!
    @IBOutlet weak var tilel: UILabel!
    
    var onDelete: (() -> Void)?
    var onDownload : (() -> Void)?
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }
    @IBAction func downloadClicked(_ sender: Any) {
        onDownload?()
    }
    
    @IBAction func delClicked(_ sender: Any) {
        onDelete?()
    }
    
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
