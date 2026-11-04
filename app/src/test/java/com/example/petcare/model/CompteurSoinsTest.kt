package com.example.petcare.model

import org.junit.Assert.assertEquals
import org.junit.Test

/** Régression du support fourni, indépendante des exercices à compléter. */
class CompteurSoinsTest {
    @Test fun lesAnimauxOntDesCompteursIndependants() {
        val compteur = CompteurSoins()
        compteur.confirmer(1); compteur.confirmer(1); compteur.confirmer(2)
        compteur.annuler(1)
        assertEquals(1, compteur.nombre(1))
        assertEquals(1, compteur.nombre(2))
    }
    @Test fun annulerNeProduitJamaisDeNombreNegatif() {
        val compteur = CompteurSoins()
        compteur.annuler(1)
        assertEquals(0, compteur.nombre(1))
    }
}
