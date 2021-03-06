package test;

import avis.SocialNetwork;
import exception.*;

import java.util.HashMap;

public class TestsGradeReviewItemBook implements SocialNetworkTest {

    private int gradeReviewItemBookOKTest(String idTest, SocialNetwork sn, String pseudo, String password, String reviewPseudo, String reviewTitle, Float grade, Float expectedGrade) {
        try {
            float newGrade = sn.gradeReviewItemBook(pseudo, password, reviewPseudo, reviewTitle, grade);

            if (newGrade != expectedGrade) {
                System.out.println("Test " + idTest + " : la note de l'avis n'a pas été correctement mise à jour.");
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

    private int gradeReviewItemBookNotItemTest(String idTest, SocialNetwork sn, String pseudo, String password, String reviewPseudo, String reviewTitle, Float grade, String messErreur) {
        try {
            sn.gradeReviewItemBook(pseudo, password, reviewPseudo, reviewTitle, grade);
            System.out.println("Test " + idTest + " : " + messErreur);
            return 1;
        } catch (NotItem notItem) {
            return 0;
        } catch (Exception e) {
            System.out.println("Test " + idTest + " : exception non prévue. " + e);
            e.printStackTrace();
            return 1;
        }
    }

    private int gradeReviewItemBookNotReviewTest(String idTest, SocialNetwork sn, String pseudo, String password, String reviewPseudo, String reviewTitle, Float grade, String messErreur) {
        try {
            sn.gradeReviewItemBook(pseudo, password, reviewPseudo, reviewTitle, grade);
            System.out.println("Test " + idTest + " : " + messErreur);
            return 1;
        } catch (NotReview notReview) {
            return 0;
        } catch (Exception e) {
            System.out.println("Test " + idTest + " : exception non prévue. " + e);
            e.printStackTrace();
            return 1;
        }
    }

    private int gradeReviewItemBookExceptionTest(String idTest, Class<?> expectedException, SocialNetwork sn, String realPseudo, String realPassword, String pseudo, String password, String realReviewPseudo, String reviewPseudo, String reviewTitle, Float grade, Float expectedGrade, String messErreur) throws NotMember, NotReview, NotItem, SelfGrading {
        try {
            sn.gradeReviewItemBook(pseudo, password, reviewPseudo, reviewTitle, grade);

            // Cas erroné: une exception était attendue.
            System.out.println("Test " + idTest + " : " + messErreur);
            return 1;
        } catch (Exception e) {
            if (expectedException.isInstance(e)) {
                float newGrade;

                try {
                    newGrade = sn.gradeReviewItemBook(realPseudo, realPassword, realReviewPseudo, reviewTitle, expectedGrade);
                }

                // Cas normal: exception attendue, on ne peut pas recuperer la note d'une review avec un titre invalide.
                catch (BadEntry badEntry) {
                    return 0;
                }

                // Cas normal: exception attendue et note de la review inchangé.
                if (newGrade == expectedGrade) {
                    return 0;
                }

                // Cas erroné: exception attendue mais la note de la review a changé.
                else {
                    System.out.println("Test " + idTest + " : l'exception " + e.getClass().getSimpleName() + " a bien été levé, mais la note de la review a été modifié");
                    return 1;
                }
            }

            // Cas erroné: l'exception n'était pas attendue.
            else {
                System.out.println("Test " + idTest + " : exception non prévue. " + e);
                e.printStackTrace();
                return 1;
            }
        }
    }

    public HashMap<String, Integer> runTests(SocialNetwork sn, String pseudo1, String password1, String pseudo2, String password2) throws NotMember, BadEntry, ItemBookAlreadyExists, NotItem, NotReview, SelfGrading, MemberAlreadyExists {
        System.out.println("\n# Tests de notation d'avis sur des livres");

        int nbTests = 0;
        int nbErreurs = 0;

        // Ajout d'un livre pour les tests
        String title = "Le Capital au XXIè siècle";
        String genre = "Economy";
        String author = "Thomas Piketty";
        int pageCount = 976;

        System.out.println("* Ajout d'un livre pour les tests: " + title);
        sn.addItemBook(pseudo1, password1, title, genre, author, pageCount);

        // Ajout d'une review pour les tests
        float grade = 5.0f;
        String comment = "Amazing !";

        System.out.println("* Ajout d'une review pour les tests: " + title + " : " + comment);
        String pseudo3 = "Pseudo3";
        String password3 = "Password3";
        String profil3 = "Profil3";
        sn.addMember(pseudo3, password3, profil3);
        sn.reviewItemBook(pseudo3, password3, title, grade, comment);

        int nbFilms = sn.nbFilms();
        int nbLivres = sn.nbBooks();

        // Valeurs par defaut pour les tests
        float expectedGrade = 2.0f;

        // Fiche 13
        // Tentatives de notation de reviews avec des entrées correctes

        // Notation d'une review de l'utilisateur 3 avec l'utilisateur 1
        nbTests++;
        nbErreurs += gradeReviewItemBookOKTest("13.1", sn, pseudo1, password1, pseudo3, title, 1.0f, 1.0f);

        // Modification de la note de la review de l'utilisateur 3 avec l'utilisateur 1
        nbTests++;
        nbErreurs += gradeReviewItemBookOKTest("13.2", sn, pseudo1, password1, pseudo3, title, 2.0f, 2.0f);
        nbTests++;
        nbErreurs += gradeReviewItemBookOKTest("13.3", sn, pseudo1, password1, pseudo3, title.toLowerCase(), 2.0f, 2.0f);
        nbTests++;
        nbErreurs += gradeReviewItemBookOKTest("13.4", sn, pseudo1, password1, pseudo3, "  " + title + "  ", 2.0f, 2.0f);

        // Notation d'une review de l'utilisateur 3 avec l'utilisateur 2
        nbTests++;
        nbErreurs += gradeReviewItemBookOKTest("13.5", sn, pseudo2, password2, pseudo3, "  " + title + "  ", 2.0f, 2.0f);

        // Fiche 14
        // Tentatives de notation de reviews avec des entrées incorrectes

        nbTests++;
        nbErreurs += gradeReviewItemBookExceptionTest("14.1", BadEntry.class, sn, pseudo1, password1, pseudo1, password1, pseudo3, pseudo3, title, -1.0f, expectedGrade, "La notation d'une review avec une note négative est autorisé.");
        nbTests++;
        nbErreurs += gradeReviewItemBookExceptionTest("14.2", BadEntry.class, sn, pseudo1, password1, pseudo1, password1, pseudo3, pseudo3, title, 4.0f, expectedGrade, "La notation d'une review avec une note supérieure au maximum est autorisé.");
        nbTests++;
        nbErreurs += gradeReviewItemBookExceptionTest("14.3", BadEntry.class, sn, pseudo1, password1, pseudo1, password1, pseudo3, pseudo3, null, 1.0f, expectedGrade, "La notation d'une review avec un titre non instancié est autorisé.");
        nbTests++;
        nbErreurs += gradeReviewItemBookExceptionTest("14.4", BadEntry.class, sn, pseudo1, password1, pseudo1, password1, pseudo3, pseudo3, "   ", 1.0f, expectedGrade, "La notation d'une review avec un titre compose uniquement d'espaces est autorisé.");
        nbTests++;
        nbErreurs += gradeReviewItemBookExceptionTest("14.5", BadEntry.class, sn, pseudo1, password1, pseudo1, password1, pseudo3, null, title, 1.0f, expectedGrade, "La notation d'une review avec un pseudo non instancié pour le donneur d'avis est autorisé.");
        nbTests++;
        nbErreurs += gradeReviewItemBookExceptionTest("14.6", BadEntry.class, sn, pseudo1, password1, null, password1, pseudo3, pseudo3, title, 1.0f, expectedGrade, "La notation d'une review avec un pseudo non instancié pour le noteur est autorisé.");
        nbTests++;
        nbErreurs += gradeReviewItemBookExceptionTest("14.7", BadEntry.class, sn, pseudo1, password1, pseudo1, null, pseudo3, pseudo3, title, 1.0f, expectedGrade, "La notation d'une review avec un password non instancié pour le noteur est autorisé.");
        nbTests++;
        nbErreurs += gradeReviewItemBookExceptionTest("14.8", BadEntry.class, sn, pseudo1, password1, pseudo1, password1, pseudo3, "   ", title, 1.0f, expectedGrade, "La notation d'une review avec un pseudo composé uniquement d'espaces pour le donneur d'avis est autorisé.");
        nbTests++;
        nbErreurs += gradeReviewItemBookExceptionTest("14.9", BadEntry.class, sn, pseudo1, password1, "   ", password1, pseudo3, pseudo3, title, 1.0f, expectedGrade, "La notation d'une review avec un pseudo composé uniquement d'espaces pour le noteur est autorisé.");
        nbTests++;
        nbErreurs += gradeReviewItemBookExceptionTest("14.10", BadEntry.class, sn, pseudo1, password1, pseudo1, " 123 ", pseudo3, pseudo3, title, 1.0f, expectedGrade, "La notation d'une review avec un password composé de moins de 4 caractères est autorisé.");

        nbTests++;
        nbErreurs += gradeReviewItemBookNotReviewTest("14.11", sn, pseudo1, password1, pseudo2, title, 1.0f, "La notation d'une review inexistante est autorisé");

        nbTests++;
        nbErreurs += gradeReviewItemBookExceptionTest("14.12", NotMember.class, sn, pseudo1, password1, pseudo1, password1, pseudo3, "St7veJ0bs", title, 1.0f, expectedGrade, "La notation d'une review pour un donneur d'avis inexistant est autorisé.");
        nbTests++;
        nbErreurs += gradeReviewItemBookExceptionTest("14.13", NotMember.class, sn, pseudo1, password1, "BillGate$$38", "Micro$$oft", pseudo3, pseudo3, title, 1.0f, expectedGrade, "La notation d'une review pour un noteur inexistant est autorisé.");
        nbTests++;
        nbErreurs += gradeReviewItemBookExceptionTest("14.14", NotMember.class, sn, pseudo1, password1, pseudo1, "Ju5nPa5lo2015", pseudo3, pseudo3, title, 1.0f, expectedGrade, "La notation d'une review avec un mauvais mot de passe est autorisé.");

        nbTests++;
        nbErreurs += gradeReviewItemBookNotItemTest("14.15", sn, pseudo1, password1, pseudo2, "Alice chez les Barbapapas.", 1.0f, "La notation d'un item inexistant est autorisée.");

        nbTests++;
        nbErreurs += gradeReviewItemBookExceptionTest("14.16", SelfGrading.class, sn, pseudo1, password1, pseudo3, password3, pseudo3, pseudo3, title, 1.0f, expectedGrade, "La notation de sa propre review est autorisée.");

        nbTests++;
        if (nbFilms != sn.nbFilms()) {
            System.out.println("Erreur: le nombre de films après utilisation de gradeReviewItemBook a été modifié.");
            nbErreurs++;
        }

        nbTests++;
        if (nbLivres != sn.nbBooks()) {
            System.out.println("Erreur: le nombre de livres après utilisation de gradeReviewItemBook a été modifié.");
            nbErreurs++;
        }

        HashMap<String, Integer> testsResults = new HashMap<>();
        testsResults.put("errors", nbErreurs);
        testsResults.put("total", nbTests);
        return testsResults;
    }
}
