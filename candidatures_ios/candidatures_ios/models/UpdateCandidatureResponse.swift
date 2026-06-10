//
//  UpdateOffreResponse.swift
//  candidatures_ios
//
//  Created by Ashley Rakotoarisoa on 10/06/2026.
//

import Foundation

struct UpdateCandidatureResponse : Decodable {
    let message: String
    let success: Bool
    let candidature: Candidature
    let code: Int
}
