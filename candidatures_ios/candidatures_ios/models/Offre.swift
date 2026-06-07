//
//  OffreRespons.swift
//  candidatures_ios
//
//  Created by Ashley Rakotoarisoa on 06/06/2026.
//

import Foundation

struct Offre : Decodable {
    let id: Int
    let type: String?
    let titre : String
    let description: String?
    let nom_entreprise: String?
    let adresse_entreprise: String?
    let adresse_comp_entreprise: String?
    let cp_entreprise: String?
    let ville_entreprise: String?
    let pays_entreprise: String?
    let nom_recruteur : String?
    let prenom_recruteur: String?
    let email_entreprise: String?
    let tel_entreprise: String?
    let periode: String?
    let salaire_min: Double?
    let salaire_max: Double?
    let date_publication: String?
}
