package com.esprit.controllers;

import com.esprit.models.ReservationEspace;
import com.esprit.services.ReservationEspaceService;
import java.time.LocalDate;
import java.util.List;

public class MesReservationsControllerTest {

    private static int testsExecuted = 0;
    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("=== Début des tests unitaires ===");
        System.out.println();

        // Exécution des tests
        testNormalizeStatus();
        testAjoutReservation();
        testValidationDates();
        testGestionErreurs();

        // Affichage du résumé
        System.out.println("\n=== Résumé des tests ===");
        System.out.println("Tests exécutés : " + testsExecuted);
        System.out.println("Tests réussis : " + testsPassed + " ✅");
        System.out.println("Tests échoués : " + testsFailed + " ❌");
        System.out.println("Taux de réussite : " +
                (testsExecuted > 0 ? (testsPassed * 100 / testsExecuted) : 0) + "%");
        System.out.println("=== Fin des tests ===");

        // Code de sortie approprié
        System.exit(testsFailed > 0 ? 1 : 0);
    }

    private static void testNormalizeStatus() {
        System.out.println("🔍 Test de la méthode normalizeStatus()...");
        testsExecuted++;

        try {
            // Simuler la classe MesReservationsController avec la méthode normalizeStatus
            TestableController controller = new TestableController();

            // Test des variantes pour "Confirmée"
            assertEqualsStatus("confirmed", "Confirmée", controller);
            assertEqualsStatus("validée", "Confirmée", controller);
            assertEqualsStatus("Confirmée", "Confirmée", controller);
            assertEqualsStatus("CONFIRMEE", "Confirmée", controller);

            // Test des variantes pour "En cours"
            assertEqualsStatus("pending", "En cours", controller);
            assertEqualsStatus("en_cours", "En cours", controller);
            assertEqualsStatus("En cours", "En cours", controller);
            assertEqualsStatus("EN_COURS", "En cours", controller);

            // Test des variantes pour "Terminée"
            assertEqualsStatus("completed", "Terminée", controller);
            assertEqualsStatus("done", "Terminée", controller);
            assertEqualsStatus("Terminée", "Terminée", controller);
            assertEqualsStatus("TERMINEE", "Terminée", controller);

            // Test des variantes pour "Annulée"
            assertEqualsStatus("cancelled", "Annulée", controller);
            assertEqualsStatus("canceled", "Annulée", controller);
            assertEqualsStatus("Annulée", "Annulée", controller);

            // Test des valeurs inconnues
            assertEqualsStatus("Autre", "Autre", controller);
            assertEqualsStatus("", "", controller);
            assertEqualsStatus(null, null, controller);

            testsPassed++;
            System.out.println("✅ Test normalizeStatus réussi");

        } catch (Exception e) {
            testsFailed++;
            System.err.println("❌ Échec du test normalizeStatus");
            System.err.println("Erreur: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    private static void testAjoutReservation() {
        System.out.println("🔍 Test d'ajout de réservation...");
        testsExecuted++;

        try {
            TestableController controller = new TestableController();

            // Test avec des données valides
            ReservationEspace reservation = new ReservationEspace();
            reservation.setNomClient("Jean Dupont");
            reservation.setDateDebut(LocalDate.now().plusDays(1));
            reservation.setDateFin(LocalDate.now().plusDays(3));
            reservation.setStatus("En attente");

            // Simuler l'ajout (normalement cette méthode devrait exister)
            boolean result = controller.ajouterReservation(reservation);

            if (!result) {
                throw new Exception("L'ajout de réservation a échoué");
            }

            // Vérifier que la réservation a été ajoutée
            List<ReservationEspace> reservations = controller.getReservations();
            if (reservations.isEmpty()) {
                throw new Exception("Aucune réservation trouvée après ajout");
            }

            ReservationEspace derniere = reservations.get(reservations.size() - 1);
            if (!derniere.getNomClient().equals("Jean Dupont")) {
                throw new Exception("Nom client incorrect");
            }

            testsPassed++;
            System.out.println("✅ Test ajout réservation réussi");

        } catch (Exception e) {
            testsFailed++;
            System.err.println("❌ Échec du test ajout réservation");
            System.err.println("Erreur: " + e.getMessage());
        }
        System.out.println();
    }

    private static void testValidationDates() {
        System.out.println("🔍 Test de validation des dates...");
        testsExecuted++;

        try {
            TestableController controller = new TestableController();

            // Test date de fin antérieure à date de début
            ReservationEspace reservationInvalide = new ReservationEspace();
            reservationInvalide.setDateDebut(LocalDate.now().plusDays(3));
            reservationInvalide.setDateFin(LocalDate.now().plusDays(1));

            if (controller.validerDates(reservationInvalide.getDateDebut(), reservationInvalide.getDateFin())) {
                throw new Exception("Validation devrait échouer pour dates invalides");
            }

            // Test dates valides
            ReservationEspace reservationValide = new ReservationEspace();
            reservationValide.setDateDebut(LocalDate.now().plusDays(1));
            reservationValide.setDateFin(LocalDate.now().plusDays(3));

            if (!controller.validerDates(reservationValide.getDateDebut(), reservationValide.getDateFin())) {
                throw new Exception("Validation devrait réussir pour dates valides");
            }

            testsPassed++;
            System.out.println("✅ Test validation dates réussi");

        } catch (Exception e) {
            testsFailed++;
            System.err.println("❌ Échec du test validation dates");
            System.err.println("Erreur: " + e.getMessage());
        }
        System.out.println();
    }

    private static void testGestionErreurs() {
        System.out.println("🔍 Test de gestion des erreurs...");
        testsExecuted++;

        try {
            TestableController controller = new TestableController();

            // Test avec réservation null
            boolean result = controller.ajouterReservation(null);
            if (result) {
                throw new Exception("L'ajout d'une réservation null devrait échouer");
            }

            // Test avec client vide
            ReservationEspace reservationVide = new ReservationEspace();
            reservationVide.setNomClient("");
            reservationVide.setDateDebut(LocalDate.now().plusDays(1));
            reservationVide.setDateFin(LocalDate.now().plusDays(2));

            result = controller.ajouterReservation(reservationVide);
            if (result) {
                throw new Exception("L'ajout avec nom client vide devrait échouer");
            }

            testsPassed++;
            System.out.println("✅ Test gestion erreurs réussi");

        } catch (Exception e) {
            testsFailed++;
            System.err.println("❌ Échec du test gestion erreurs");
            System.err.println("Erreur: " + e.getMessage());
        }
        System.out.println();
    }

    // Méthode utilitaire pour les assertions
    private static void assertEqualsStatus(String input, String expected, TestableController controller) throws Exception {
        String result = controller.normalizeStatus(input);
        if ((expected == null && result != null) || (expected != null && !expected.equals(result))) {
            throw new Exception("Attendu: '" + expected + "', obtenu: '" + result + "' pour l'entrée: '" + input + "'");
        }
    }

    // Classe interne pour simuler MesReservationsController
    private static class TestableController {
        private List<ReservationEspace> reservations = new java.util.ArrayList<>();

        public String normalizeStatus(String status) {
            if (status == null) return null;
            if (status.isEmpty()) return status;

            String normalizedStatus = status.toLowerCase().trim()
                    .replace("é", "e").replace("è", "e").replace("ê", "e")
                    .replace("à", "a").replace("ù", "u").replace("ç", "c");

            switch (normalizedStatus) {
                case "confirmed":
                case "validee":
                case "confirmee":
                    return "Confirmée";
                case "pending":
                case "en_cours":
                case "en cours":
                    return "En cours";
                case "completed":
                case "done":
                case "terminee":
                    return "Terminée";
                case "cancelled":
                case "canceled":
                case "annulee":
                    return "Annulée";
                default:
                    return status; // Retourne la valeur originale si pas de correspondance
            }
        }

        public boolean ajouterReservation(ReservationEspace reservation) {
            if (reservation == null) return false;
            if (reservation.getNomClient() == null || reservation.getNomClient().trim().isEmpty()) return false;
            if (reservation.getDateDebut() == null || reservation.getDateFin() == null) return false;
            if (!validerDates(reservation.getDateDebut(), reservation.getDateFin())) return false;

            reservations.add(reservation);
            return true;
        }

        public boolean validerDates(LocalDate dateDebut, LocalDate dateFin) {
            if (dateDebut == null || dateFin == null) return false;
            return !dateFin.isBefore(dateDebut);
        }

        public List<ReservationEspace> getReservations() {
            return new java.util.ArrayList<>(reservations);
        }
    }
}