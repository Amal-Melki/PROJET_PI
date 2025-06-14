## Guide d'Installation Complet

### Prérequis
- JDK 17+ [Télécharger](https://adoptium.net/)
- JavaFX SDK 17+ [Télécharger](https://gluonhq.com/products/javafx/)

### Configuration
1. Extraire JavaFX dans `C:\\javafx-sdk-17.0.2`
2. Configurer `run.bat`:
   ```batch
   set JAVA_HOME="C:\\Program Files\\Java\\jdk-17"
   set FX_HOME="C:\\javafx-sdk-17.0.2\\lib"
   ```

### Utilisation
```bash
cd core
check_env.bat  # Vérifie l'environnement
run.bat        # Lance l'application
```

### Dépannage
- **JavaFX manquant** : Vérifier FX_HOME
- **CSS non chargé** : Vérifier chemin dans MainLayout.fxml
- **Erreurs** : Consulter les logs console
