//
//  OffreRespons.swift
//  candidatures_ios
//
//  Created by Ashley Rakotoarisoa on 06/06/2026.
//

import Foundation

struct OffreResponse : Decodable {
    let message: String
        let success: Bool
        let offre_id: Int?
}
