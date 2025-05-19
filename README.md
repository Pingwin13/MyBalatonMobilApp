# Projekt téma : Balatoni Látnivalók
Az app-ba regisztrációt követően be tudsz jelentkezni majd látod a látnivalókat, tudsz újat hozzáadni.
A profil oldalon látod a saját látnivalóidat amiket szerkeszteni és törölni tudsz.
A látnivalók oldalon a látnivalókat tudod név szerint és feltöltés ideje szerint rendezni.
A látnivaló hozzáadásánál két módon tudsz képet feltölteni: Kamera,galéria.
Új látnivaló hozzáadása után 60 mp elteltével érkezik az értesítés.

# Segítség az értékeléshez
## A keresés nem működik!!
## 1.Fordítási hiba nincs
## 2.Futtatási hiba nincs
## 3.Firebase autentikáció
LoginActivity.java és SignUpActivity.java
## 4.Adatmodell definiálása
AttractionModel.java
## 5.Legalább 4 különböző activity
Main
login
signup
attractions
addattraction
profile
## 6.Beviteli mezők beviteli típusa
activity_login.xml és activity_sign_up.xml
## 7.Layout típusok
activity_login.xml: RelativeLayout 
activity_attractions.xml: ConstraintLayout
## 8.Reszponzív design
layout/layout-sw600dp/layout-land mappak-ba találhatók
## 9.Animációk
LoginActivity.java: Pulse animáció a gombon
MainActivity.java: welcomeText animáció
## 10.Intentek és navigáció
Minden activity-ben vannak intentek és mindenhonnan el lehet navigálni.
## 11.Lifecycle Hook-ok
AttractionsActivity.java: onResume() Látnivalók újratöltése
## 12.Android erőforrások és permissionek
Kamera
Galéria
Notifications
Internet
## 13.Rendszerszolgáltatások
NotificationReceiver.java
csak 1 van :(
## 14. CRUD műveletek
Látnivaló hozzáadása
Látnivalók
Profil oldalon(szerkesztés,törlés)
## 15.Komplex Firestore lekérdezések
profile.java : felhasználó látnivalói
attractionsActivity.java: sortByName és sortByDate
## 16.Szubjektív értékelés


