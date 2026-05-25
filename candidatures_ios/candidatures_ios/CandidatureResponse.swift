//
//  Candidature.swift
//  candidatures_ios
//
//  Created by Ashley Rakotoarisoa on 25/05/2026.
//

import Foundation

struct CandidatureResponse: Decodable {
    var id: Int,
compte: Int,
offre:Int,
        cv: Int,
        statut: String,
        date_candidature: String,
score_matching: Double
}
