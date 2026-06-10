//
//  NewScore.swift
//  candidatures_ios
//
//  Created by Ashley Rakotoarisoa on 10/06/2026.
//

import Foundation

struct NewScore: Decodable {
    let score: Double
    let base_score: Double
    let success: Bool
    let code: Int
}
