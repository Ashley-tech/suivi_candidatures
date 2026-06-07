//
//  Candidature.swift
//  candidatures_ios
//
//  Created by Ashley Rakotoarisoa on 25/05/2026.
//

import Foundation

struct CandidatureResponse: Decodable {
    var message: String,
        candidature: Candidature,
        id: Int,
        success: Bool,
        code: Int
}
