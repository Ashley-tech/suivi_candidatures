//
//  KeychainHelper.swift
//  candidatures_ios
//
//  Created by Ashley Rakotoarisoa on 23/05/2026.
//

import Foundation
import Security

final class KeychainHelper {

    static let shared = KeychainHelper()

    private init() {}

    func save(_ value: String, service: String, account: String) {

        let data = value.data(using: .utf8)!

        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrService as String: service,
            kSecAttrAccount as String: account,
            kSecValueData as String: data
        ]

        SecItemDelete(query as CFDictionary)
        SecItemAdd(query as CFDictionary, nil)
    }

    func read(service: String, account: String) -> String? {

        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrService as String: service,
            kSecAttrAccount as String: account,
            kSecReturnData as String: true,
            kSecMatchLimit as String: kSecMatchLimitOne
        ]

        var result: AnyObject?

        SecItemCopyMatching(query as CFDictionary, &result)

        guard let data = result as? Data else {
            return nil
        }

        return String(data: data, encoding: .utf8)
    }

    func delete(service: String, account: String) {

        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrService as String: service,
            kSecAttrAccount as String: account
        ]

        SecItemDelete(query as CFDictionary)
    }
}
