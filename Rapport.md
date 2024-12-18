# **Rapport**

## Sommaire
1. [Méthode de Monte-Carlo](#i---methode-de-monte-carlo)  
   1.1. [Présentation de la méthode de Monte Carlo](#presentation-de-la-methode-de-monte-carlo)  
   1.2. [Exemple : Calcul de π](#exemple-calcul-de-π)  
   1.3. [Génération de points aléatoires](#generation-de-points-aleatoires)  
   1.4. [Estimation de π](#estimation-de-π)
2. [Algorithme de parallélisation](#ii---algorithme-de-parallelisation)  
   2.1. [Représentation de l'algorithme](#representation-de-lalgorithme)  
   2.2. [Analyse de la parallélisation](#analyse-de-la-parallelisation)  
   2.3. [Paradigmes de parallélisation](#paradigmes-de-parallelisation)
3. [Mise en œuvre](#iii---mise-en-œuvre)  
   3.1. [Analyse de Assignement102](#analyse-de-assignement102)  
   3.2. [Analyse de Pi](#analyse-de-pi)


## I - Méthode de Monte-Carlo

### 1.1. Présentation de la méthode de Monte Carlo
La méthode de Monte Carlo est une technique numérique qui résout des problèmes complexes via des simulations aléatoires, en particulier lorsqu'aucune méthode analytique n'est disponible. Elle génère des valeurs aléatoires pour explorer un espace de possibilités et estimer un résultat statistiquement.

### 1.2. Exemple : Calcul de π
La méthode de Monte Carlo peut être utilisée pour estimer la valeur de $\pi$ en considérant un cercle de rayon 1 inscrit dans un carré de côté 1. En générant des points aléatoires à l'intérieur du carré et en comptant ceux qui tombent à l'intérieur du cercle, on peut estimer $\pi$.

#### Image du modèle :
![Modèle Monte-Carlo](images/Monte-Carlo.png)

### 1.3. Génération de points aléatoires
Les points $P(x_p, y_p)$ sont générés de manière uniforme dans le carré, avec $x_p$ et $y_p$ issus d'une distribution uniforme $U(]0,1[)$. La probabilité qu’un point se trouve dans le cercle est donnée par :

$$
P = \frac{\pi}{4}
$$

En tirant un grand nombre de points $N_\text{tot}$ et en comptant $N_\text{cible}$ ceux dans le cercle, on peut estimer $\pi$ via la formule :

$$
\pi \approx 4 \times \frac{N_\text{cible}}{N_\text{tot}}
$$

### 1.4. Estimation de π
L’estimation de $\pi$ s’obtient en calculant :

$$
\pi \approx 4 \times \left(\frac{N_\text{cible}}{N_\text{tot}}\right)
$$

---

## II - Algorithme de parallélisation

### 2.1. Représentation de l'algorithme
Voici une version simplifiée de l'algorithme utilisé pour l'estimation de $\pi$ via Monte Carlo :

```
initialiser N_cible = 0;
pour p de 0 à N_tot - 1 :
x_p = valeur aléatoire entre 0 et 1;
y_p = valeur aléatoire entre 0 et 1;
si (x_p² + y_p²) < 1 :
N_cible++;
fin pour
```

### 2.2. Analyse de la parallélisation
L'algorithme peut être parallélisé en identifiant les tâches indépendantes et en synchronisant les sections critiques, comme l'incrémentation de `N_cible`.

#### Tâches principales :
- $T_0$ : Tirage et traitement des $N_\text{tot}$ points.
- $T_1$ : Calcul de l'estimation finale de $\pi$.

#### Dépendances :
- $T_1$ dépend de $T_0$ (il faut traiter tous les points avant de calculer $\pi$).
- $T_{0p2}$ dépend de $T_{0p1}$ (les points doivent être générés avant leur traitement).

#### Ressource critique :
`N_cible` est une ressource critique qui nécessite une synchronisation pour éviter des conflits lors de l'accès concurrent.

### 2.3. Paradigmes de parallélisation

#### A. Parallélisme de boucle
Chaque itération de la boucle est indépendante, mais la synchronisation est nécessaire pour l’incrémentation de `N_cible`.

```
parallel for p de 0 à N_tot - 1 :
    x_p = rand();
    y_p = rand();
  si (x_p² + y_p²) < 1 :
        N_cible++;
fin for
```

#### B. Approche Master-Worker
Le maître distribue les tâches aux travailleurs, qui retournent le nombre de points dans le cercle. Le maître agrège les résultats pour obtenir `N_cible`.

```
Master_MC:
  for i = 0 : N_worker - 1
    N_cible[i] = Worker_MC[i](N_tot[i])
  for i = 0 : N_worker - 1 
    cpt = cpt + N_cible[i]
```

---

## III - Mise en œuvre

### Analyse de Assignement102
Ce programme utilise une approche d'**itération parallèle**.
L'objectif est de tirer parti des ressources d'un ordinateur avec plusieurs cœurs de processeur pour accélérer le calcul.
Le programme utilise le langage Java et la bibliothèque ExecutorService pour gérer l'exécution parallèle des tâches.

**Structure générale du code :**
Le programme est divisé en deux classes principales :
**Assignment102** : La classe principale qui effectue le calcul de Pi et affiche les résultats.
**PiMonteCarlo** : La classe qui contient la logique d'estimation de Pi en utilisant la méthode de Monte Carlo.

**Description de la classe Assignment102 :**
La classe Assignment102 commence par créer une instance de la classe PiMonteCarlo en lui passant un nombre d'essais (100 000 dans cet exemple) pour réaliser l'estimation de Pi.
Ensuite, le programme mesure le temps d'exécution de la méthode getPi() de l'objet PiMonteCarlo en utilisant System.currentTimeMillis() avant et après l'appel.
Après l'exécution, plusieurs résultats sont affichés :
- La valeur estimée de Pi.
- La différence entre la valeur estimée et la valeur exacte de Pi (Math.PI).
- L'erreur en pourcentage entre la valeur estimée et la valeur exacte.
- Le nombre de processeurs disponibles sur la machine pour l'exécution parallèle.
- La durée d'exécution du calcul.

**Description de la classe PiMonteCarlo :**
La classe PiMonteCarlo contient la logique du calcul de Pi en utilisant la méthode de Monte Carlo,
où des points aléatoires sont générés dans un carré et on compte combien tombent à l'intérieur d'un cercle inscrit dans ce carré.

**Attributs de la classe PiMonteCarlo :**
**nAtomSuccess** : Un compteur atomique (AtomicInteger) qui garde le nombre de points tombant dans le cercle (ces points sont considérés comme des "réussites").
**nThrows** : Le nombre d'essais ou de points générés, passé lors de l'initialisation de l'objet PiMonteCarlo.
**value** : La variable qui contiendra la valeur estimée de Pi après le calcul.

**La classe interne MonteCarlo :**
Il s'agit d'une classe interne implémentant l'interface Runnable.
Chaque instance de cette classe représente une tâche parallèle qui génère un point aléatoire (en utilisant Math.random() pour les coordonnées x et y),
et vérifie si ce point se trouve à l'intérieur du cercle unité.
La condition x * x + y * y <= 1 permet de déterminer si le point est à l'intérieur du cercle inscrit dans le carré de côté 1.

**Méthode getPi() :**
Cette méthode initialise un ExecutorService en utilisant une **piscine de travail** (WorkStealingPool),
qui crée un nombre de threads égal au nombre de processeurs disponibles sur la machine, afin de paralléliser les calculs.
Pour chaque essai (nThrows), une tâche parallèle (MonteCarlo) est soumise à l'exécuteur,
ce qui permet de générer plusieurs points en parallèle. Cela constitue une **itération parallèle**,
où chaque thread travaille indépendamment sur une partie du problème.
Après avoir soumis toutes les tâches, l'exécuteur est fermé avec executor.shutdown(),
et le programme attend que toutes les tâches soient terminées avec while (!executor.isTerminated()).
À la fin des calculs, la valeur estimée de Pi est calculée avec la formule π≈4×(Ncible/Ntot)


**Résultats et affichage :**
Une fois le calcul terminé, le programme affiche :
L'approximation de Pi obtenue.
La différence entre la valeur estimée et la valeur exacte de Pi.
L'erreur relative en pourcentage.
Le nombre de processeurs disponibles pour l'exécution parallèle.
Le temps d'exécution total du calcul, en millisecondes.

Ce programme montre comment la méthode de Monte Carlo peut être utilisée pour estimer,
Pi, en générant des points aléatoires. L'approche d'**itération parallèle** permet de répartir les calculs sur plusieurs threads,
accélérant ainsi le processus de calcul.


### Analyse de Pi
Cette version utilise des **Callables**, des **Futures**, et un **pool de threads** pour répartir les calculs sur plusieurs travailleurs (threads),
permettant ainsi d'accélérer le processus de simulation. Le programme est écrit en Java.

**Structure générale du code :**
Le code est composé de trois classes principales :
**`Pi`** : La classe principale qui orchestre le calcul de Pi en appelant la méthode du maître (`Master`).
**`Master`** : La classe qui crée et gère les travailleurs, coordonne l'exécution parallèle et agrège les résultats.
**`Worker`** : La classe qui exécute le calcul pour estimer Pi dans chaque tâche parallèle en simulant des points aléatoires.

**Description de la classe `Pi` :**
La classe `Pi` contient la méthode `main`, où l'exécution du programme commence. Elle crée une instance de la classe `Master` et lui passe les paramètres : le nombre total d'itérations (50000) et le nombre de travailleurs (10).
Ensuite, la méthode `doRun()` du maître est appelée pour effectuer le calcul parallèle, et le résultat (total de réussites) est affiché à l'écran.

**Description de la classe `Master` :**
**`doRun(int totalCount, int numWorkers)`** : Cette méthode gère l'exécution parallèle des travailleurs. Elle crée d'abord une liste de tâches (`Callable<Long>`), où chaque tâche est une instance de la classe `Worker`. Chaque travailleur exécutera un calcul avec un nombre d'itérations défini par `totalCount`.
Un **pool de threads** (`ExecutorService` avec `newFixedThreadPool(numWorkers)`) est créé pour gérer l'exécution parallèle. Les tâches sont soumises à ce pool avec la méthode `invokeAll()`, qui renvoie une liste de `Future<Long>`. Chaque `Future` contient le résultat d'un travailleur.
Le programme attend la fin de toutes les tâches et rassemble les résultats en additionnant les valeurs retournées par chaque travailleur. Ces valeurs représentent le nombre de points dans le cercle unité pour chaque tâche.
Après l'agrégation des résultats, la méthode calcule la valeur de Pi en utilisant la formule :

Enfin, elle affiche la valeur estimée de Pi, l'erreur relative par rapport à la valeur exacte de Pi, le nombre total de points générés, le nombre de processeurs utilisés et le temps d'exécution du calcul.

**Description de la classe `Worker` :**
Chaque **`Worker`** est une tâche parallèle, représentée par un `Callable<Long>`. Cette classe simule la méthode de Monte Carlo pour une estimation de Pi en générant des points aléatoires dans un carré de côté 1.
Pour chaque itération, deux nombres aléatoires (`x` et `y`) sont générés et vérifiés pour voir s'ils se trouvent à l'intérieur d'un cercle inscrit dans ce carré.
Si le point est dans le cercle, un compteur (`circleCount`) est incrémenté. Le résultat de chaque tâche est le nombre total de points dans le cercle pour cette tâche.

**Exécution parallèle avec `Callable` et `Future` :**
Les **Callables** sont utilisés pour encapsuler les tâches à exécuter en parallèle. Contrairement à `Runnable`, un `Callable` peut renvoyer un résultat (ici, un `Long` représentant le nombre de points dans le cercle).
Les **Futures** sont des objets qui représentent le résultat d'une opération qui n'est pas encore terminée. La méthode `get()` de `Future` bloque l'exécution du programme jusqu'à ce que la tâche associée soit terminée, garantissant ainsi que tous les résultats des travailleurs sont collectés avant de procéder à l'agrégation.

**Résultats et affichage :**
Après l'exécution, le programme affiche :
L'approximation de Pi calculée.
L'erreur relative par rapport à la valeur exacte de Pi.
Le nombre total de points générés (calculé comme `totalCount * numWorkers`).
Le nombre de travailleurs utilisés (numéro de processeurs).
Le temps d'exécution du calcul en millisecondes.
Ces résultats permettent d'analyser la précision de l'estimation et l'efficacité de l'exécution parallèle.

Ce programme montre l'utilisation de la méthode de Monte Carlo pour estimer Pi,
avec une approche d'exécution parallèle à l'aide de **Callables**, **Futures**, et d'un **pool de threads**.
Grâce à cette approche, les calculs sont répartis entre plusieurs threads,
ce qui permet de réduire le temps nécessaire pour obtenir une estimation précise de Pi.
Le programme montre aussi l'impact du nombre de travailleurs (threads) sur les performances,
le nombre total d'itérations et la précision de l'estimation.


