package com.esprit.core;

import com.esprit.models.Espace;
import com.esprit.models.Reservation;
import com.esprit.models.ReservationEspace;
import com.esprit.services.ReservationService;
import com.esprit.services.ReservationEspaceService;

import java.util.List;

public class SharedData {
    private static User currentUser;
    private static Espace selectedEspace;

    // Méthode de synchronisation
    public static void syncUserData() {
        if (currentUser != null) {
            // TODO: Implémenter la logique de sync
        }
    }

    public static void syncReservations(User user) {
        if (user != null) {
            try {
                System.out.println("Début synchronisation pour user: " + user.getId());
                
                List<Reservation> eventRes = new ReservationService().getByUser(user.getId());
                System.out.println(eventRes.size() + " réservations événements trouvées");
                
                List<ReservationEspace> spaceRes = new ReservationEspaceService().getAll(user.getId());
                System.out.println(spaceRes.size() + " réservations espaces trouvées");
                
                user.setCombinedReservations(combineReservations(eventRes, spaceRes));
                System.out.println("Synchronisation terminée");
            } catch (Exception e) {
                System.err.println("Erreur synchronisation: " + e.getMessage());
            }
        }
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setSelectedEspace(Espace espace) {
        selectedEspace = espace;
    }

    public static Espace getSelectedEspace() {
        return selectedEspace;
    }

    /**
     * Exécute un test complet d'intégration entre les modules
     * @return true si la synchronisation a fonctionné, false sinon
     * @throws Exception en cas d'erreur critique
     */
    public static boolean runIntegrationTest() throws Exception {
        // ... implémentation existante ...
    }
}
