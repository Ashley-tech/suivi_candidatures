//
//  SignupResponse.swift
//  candidatures_ios
//
//  Created by Ashley Rakotoarisoa on 21/05/2026.
//

import Foundation

struct SignupResponse: Decodable {
    let message: String,
         compte: Compte,
        success: Bool,
        code: Int
}
