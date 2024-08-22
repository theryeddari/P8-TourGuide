# TourGuide

**TourGuide** est une application Spring Boot conçue pour recommander aux utilisateurs des attractions touristiques à proximité. Face à une croissance rapide du nombre d'utilisateurs, des améliorations ont été apportées pour optimiser les performances et corriger des bugs critiques.

#Document de présentation ffonctionnel et Technique

Vous trouverez ici le document [Document de présentation Tour Guide](https://github.com/theryeddari/P8-TourGuide/blob/0b07aedbef2358009283224ffca095af8d911081/Documentation%20TourGuide.pdf)

## Prérequis

- **Java** : 21
- **Spring Boot** : 3.2.2
- **JUnit** : 5
- **Log4j2**
- **Jacoco** : pour la couverture de code
- **Surefire** : pour les rapports de tests

## Installation

La branch Main est utilisé comme branch de présentation par défaut.

Pour toute utilisation du code, ajout ou modification veuillez creer une nouvelle branch à partir de la branch Master qui est diponible dans le projet aprés avoir cloner le repro.

[Branch Master](https://github.com/theryeddari/P8-TourGuide/tree/8b0c295b35227ea6c349c2568c96b538c3524746/TourGuide) 

Clonez le dépôt de la branch Master:

```
git clone https://github.com/theryeddari/P8-TourGuide.git
```
Copier le code
```
cd TourGuide
```
Compilez et installez les dépendances :

```
mvn install:install-file -Dfile="libs/gpsUtil.jar" -DgroupId="gpsUtil" -DartifactId="gpsUtil" -Dversion="1.0.0" -Dpackaging="jar"  
mvn install:install-file -Dfile="libs/RewardCentral.jar" -DgroupId="rewardCentral" -DartifactId="rewardCentral" -Dversion="1.0.0" -Dpackaging="jar"  
mvn install:install-file -Dfile="libs/TripPricer.jar" -DgroupId="tripPricer" -DartifactId="tripPricer" -Dversion="1.0.0" -Dpackaging="jar"
```

```
./mvnw clean install
```
Lancez l'application :

```
./mvnw spring-boot:run
```
## Améliorations et Corrections

### Optimisation des Performances

- gpsUtil : L'appel à gpsUtil a été optimisé pour traiter 100 000 localisations en moins de 15 minutes.
- RewardsService : Le temps de réponse a été réduit pour calculer les récompenses de 100 000 utilisateurs en moins de 20 minutes.
- Corrections de Bugs
- Tests Unitaires : Correction des tests échouant de manière intermittente.
- Recommandations d'Attractions :
Correction du bug empêchant l'envoi de recommandations. Les 5 attractions les plus proches sont désormais toujours recommandées.

### Tests
Pour exécuter les tests unitaires :

``` 
./mvnw test
```
Le rapport de test est disponible dans le dossier target/site/index.html ou présent  ici :

[rapport SureFire](https://github.com/theryeddari/P8-TourGuide/blob/e51bbd3725823a355724c16e5ba6e0512cbd124f/surefire.png)

### Couverture de code

Pour générer un rapport de couverture :

```
./mvnw verify
```

Le rapports de couverture est disponible dans le dossier target/site/jacoco/index.html ou présent  ici

[rapport Jacoco](https://github.com/theryeddari/P8-TourGuide/blob/e51bbd3725823a355724c16e5ba6e0512cbd124f/jacoco.png)

## Pipeline d'Intégration Continue

### CI Tool : GitHub Actions
Processus : Compilation, exécution des tests, vérification des performances ,génération de l'artefact (JAR) et vérification statique avec OpenSonar lorsque que la branch master est push

## Structure du Projet
```
/src
  /main
    /java
      /com/openclassrooms/tourguide
    /resources
  /test
    /java
      /com/openclassrooms/tourguide
```
## License
Ce projet est sous licence MIT. Voir le fichier LICENSE pour plus de détails.

### Contact
Pour toute question, contactez moi.
