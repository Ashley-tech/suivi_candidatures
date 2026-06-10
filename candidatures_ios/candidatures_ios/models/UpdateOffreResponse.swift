//
//  UpdateOffreResponse.swift
//  candidatures_ios
//
//  Created by Ashley Rakotoarisoa on 10/06/2026.
//

import Foundation

struct UpdateOffreResponse : Decodable {
    let message: String
    let success: Bool
    let offre : Offre
    let id: Int
}
