package test;

import avis.SocialNetwork;
import exception.BadEntry;

import java.util.HashMap;
import java.util.LinkedList;

public class TestsConsultItems implements SocialNetworkTest {

    private int consultItemsOKTest(String idTest, SocialNetwork sn, String title, int expectedSize) {
        try {
            LinkedList<String> items = sn.consultItems(title);

            if (items.size() != expectedSize) {
                System.out.println("Test " + idTest + " : le nombre d'items renvoyé est incorrect.");
                return 1;
            } else {
                return 0;
            }

        } catch (Exception e) {
            System.out.println("Test " + idTest + " : exception non prévue. " + e);
            e.printStackTrace();
            return 1;
        }
    }

    private int consultItemsBadEntryTest(String idTest, SocialNetwork sn, String title, String messErreur) {
        try {
            sn.consultItems(title);

            System.out.println("Test " + idTest + " : " + messErreur);
            return 1;
        } catch (BadEntry e) {
            return 0;
        } catch (Exception e) {
            System.out.println("Test " + idTest + " : exception non prévue. " + e);
            e.printStackTrace();
            return 1;
        }
    }

    public HashMap<String, Integer> runTests(SocialNetwork sn, String pseudo1, String password1, String pseudo2, String password2) throws Exception {
        System.out.println("\n# Tests de consultation d'items");

        int nbTests = 0;
        int nbErreurs = 0;

        // Ajout d'items dans le SocialNetwork
        String title = "Trainspotting";
        System.out.println("* Ajout d'un film pour les tests: " + title);
        sn.addItemFilm(pseudo1, password1, title, "Drame", "Danny Boyle", "John Hodge", 94);
        System.out.println("* Ajout d'un livre pour les tests: " + title);
        sn.addItemBook(pseudo1, password1, title, "Roman", "Irvine Welsh", 380);

        int nbLivres = sn.nbBooks();
        int nbFilms = sn.nbFilms();

        // Fiche 11
        // Tentatives de consultation d'items avec des entrées correctes

        // Test à vide
        nbTests++;
        nbErreurs += consultItemsOKTest("11.1", new SocialNetwork(), "Les Réseaux", 0);

        // Tests avec le SocialNetwork rempli
        nbTests++;
        nbErreurs += consultItemsOKTest("11.2", sn, "Les Réseaux", 0);
        nbTests++;
        nbErreurs += consultItemsOKTest("11.3", sn, title, 2);
        nbTests++;
        nbErreurs += consultItemsOKTest("11.4", sn, title.toLowerCase(), 2);
        nbTests++;
        nbErreurs += consultItemsOKTest("11.5", sn, "   " + title + "  ", 2);

        // Fiche 12
        // Tentatives de consultation d'items avec des entrées incorrectes

        nbTests++;
        nbErreurs += consultItemsBadEntryTest("12.1", sn, null, "La consultation d'un item avec un titre null est autorisé.");
        nbTests++;
        nbErreurs += consultItemsBadEntryTest("12.2", sn, "   ", "La consultation d'un item avec un titre composé uniquement d'espaces est autorisé.");

        nbTests++;
        if (nbLivres != sn.nbBooks()) {
            System.out.println("Erreur: le nombre de livres après utilisation de consultItems a été modifié.");
            nbErreurs++;
        }

        nbTests++;
        if (nbFilms != sn.nbFilms()) {
            System.out.println("Erreur: le nombre de films après utilisation de consultItems a été modifié.");
            nbErreurs++;
        }

        HashMap<String, Integer> testsResults = new HashMap<>();
        testsResults.put("errors", nbErreurs);
        testsResults.put("total", nbTests);
        return testsResults;
    }
}
