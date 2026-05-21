//
//  MailResponse.swift
//  candidatures_ios
//
//  Created by Ashley Rakotoarisoa on 19/05/2026.
//

import Foundation

struct MailResponse:Decodable {
    var message: String,
        error: String,
    success: Bool
}
