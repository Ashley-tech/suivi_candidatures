//
//  SessionManager.swift
//  candidatures_ios
//
//  Created by Ashley Rakotoarisoa on 23/05/2026.
//

import Foundation

final class SessionManager {

    static let shared = SessionManager()

    private init() {}

    var isLoggedIn: Bool {
        return KeychainHelper.shared.read(service: "token", account: "user") != nil
    }

    func saveToken(_ token: String) {
        KeychainHelper.shared.save(
            token,
            service: "token",
            account: "user"
        )
    }

    func getToken() -> String? {
        return KeychainHelper.shared.read(
            service: "token",
            account: "user"
        )
    }

    func logout() {
        KeychainHelper.shared.delete(
            service: "token",
            account: "user"
        )
    }
}
