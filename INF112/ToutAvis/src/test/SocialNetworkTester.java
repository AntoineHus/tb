package test;

import avis.SocialNetwork;
import exception.BadEntry;
import exception.MemberAlreadyExists;

import java.util.HashMap;
import java.util.LinkedList;

public class SocialNetworkTester {

    public static void main(String[] args) throws Exception {
        // Tests initialisation
        TestsInitialisation.main(args);

        // Tests unitaires
        LinkedList<HashMap<String, Integer>> testsResults = new LinkedList<>();

        Class[] tests = {
                TestsAddMember.class,
                TestsAddItemBook.class,
                TestsAddItemFilm.class,
                TestsReviewItemBook.class,
                TestsReviewItemFilm.class,
                TestsGradeReviewItemBook.class,
                TestsGradeReviewItemFilm.class,
                TestsConsultItems.class
        };

        for (Class klass : tests) {
            SocialNetwork sn = new SocialNetwork();
            HashMap<String, String> members = fakeMembers(sn, 2);

            SocialNetworkTest test = (SocialNetworkTest) klass.getConstructors()[0].newInstance();
            HashMap<String, Integer> testResult = test.runTests(sn, "User1", members.get("User1"), "User2", members.get("User2"));

            System.out.println("-> " + klass.getSimpleName() + ": " + testResult.get("errors") + " erreur(s) / " + testResult.get("total") + " tests effectués");
            testsResults.add(testResult);
        }

        System.out.println("\n*** Synthèse des tests unitaires ***");

        Integer totalTests = 0, totalErrors = 0;
        for (HashMap<String, Integer> testResult : testsResults) {
            totalTests += testResult.get("total");
            totalErrors += testResult.get("errors");
        }

        System.out.println(totalTests + " tests, " + totalErrors + " erreurs.\n");

        // Tests des performances
        System.out.println("*** Tests des performances ***");
        System.out.println("Note: une review par item est ajouté.");
        System.out.println("Members | Books | Films | addMember() | addItemBook() | addItemFilm() | reviewItemBook() | reviewItemFilm() | consultItems()");

        for (int i = 1; i <= 100000; i *= 10) {
            HashMap<String, Long> performanceResult = TestsPerformances.testPerformances(i, i, i);
            System.out.format("%8d|%7d|%7d|%10d ms|%12d ms|%12d ms|%15d ms|%15d ms|%12d ms\n", i, i, i, performanceResult.get("addMember"), performanceResult.get("addItemBook"), performanceResult.get("addItemFilm"), performanceResult.get("reviewItemBook"), performanceResult.get("reviewItemFilm"), performanceResult.get("consultItems"));

        }
    }

    private static HashMap<String, String> fakeMembers(SocialNetwork sn, int count) throws MemberAlreadyExists, BadEntry {
        HashMap<String, String> members = new HashMap<>();

        for (int i = 1; i <= 2; i++) {
            String pseudo = "User" + i;
            String password = "Pa$$w0rd";
            String profil = "1337";
            members.put(pseudo, password);
            sn.addMember(pseudo, password, profil);
        }

        return members;
    }

}
