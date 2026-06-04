//
//  DeleteResponse.swift
//  candidatures_ios
//
//  Created by Ashley Rakotoarisoa on 31/05/2026.
//

import Foundation

struct DeleteResponse: Decodable {
    let message: String,
        success: Bool,
        code: Int
}
