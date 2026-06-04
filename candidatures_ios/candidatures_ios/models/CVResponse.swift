//
//  CVResponse.swift
//  candidatures_ios
//
//  Created by Ashley Rakotoarisoa on 02/06/2026.
//

import Foundation

struct CVResponse: Decodable {
    let id: Int,
        nom: String,
        download_url: String,
        mime_type:String,
        date_upload: String,
        visible: Int
}
