//
//  Compte.swift
//  candidatures_ios
//
//  Created by Ashley Rakotoarisoa on 19/05/2026.
//
import Foundation

struct Compte: Codable {
    let id: Int
    let sexe: String?
    let nom: String?
    let prenom: String?
    let email: String?
    let date_naissance: String?
    let nationalite: String?
    let titre: String?
    let adresse: String?
    let ville: String?
    let pays: String?
    let numero: String?
    let website: String?
    let mdp: String?
}
