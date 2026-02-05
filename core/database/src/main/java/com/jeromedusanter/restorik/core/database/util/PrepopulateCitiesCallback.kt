package com.jeromedusanter.restorik.core.database.util

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class PrepopulateCitiesCallback : RoomDatabase.Callback() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        applicationScope.launch {
            prepopulateCities(db = db)
        }
    }

    private fun prepopulateCities(db: SupportSQLiteDatabase) {
        // Top 100 cities in France by population
        val frenchCityList = listOf(
            "Paris", "Marseille", "Lyon", "Toulouse", "Nice",
            "Nantes", "Strasbourg", "Montpellier", "Bordeaux", "Lille",
            "Rennes", "Reims", "Le Havre", "Saint-Étienne", "Toulon",
            "Grenoble", "Dijon", "Angers", "Nîmes", "Villeurbanne",
            "Le Mans", "Aix-en-Provence", "Clermont-Ferrand", "Brest", "Tours",
            "Amiens", "Limoges", "Annecy", "Perpignan", "Boulogne-Billancourt",
            "Metz", "Besançon", "Orléans", "Saint-Denis", "Argenteuil",
            "Rouen", "Mulhouse", "Montreuil", "Caen", "Nancy",
            "Tourcoing", "Roubaix", "Nanterre", "Vitry-sur-Seine", "Avignon",
            "Créteil", "Dunkerque", "Poitiers", "Asnières-sur-Seine", "Courbevoie",
            "Versailles", "Colombes", "Fort-de-France", "Aulnay-sous-Bois", "Saint-Paul",
            "Rueil-Malmaison", "Pau", "Aubervilliers", "Le Tampon", "Champigny-sur-Marne",
            "Antibes", "Calais", "La Rochelle", "Cannes", "Béziers",
            "Colmar", "Bourges", "Saint-Maur-des-Fossés", "Mérignac", "Saint-Louis",
            "Drancy", "Ajaccio", "Levallois-Perret", "Issy-les-Moulineaux", "Noisy-le-Grand",
            "Villeneuve-d'Ascq", "Neuilly-sur-Seine", "Valence", "Cergy", "Antony",
            "Vénissieux", "Pessac", "Ivry-sur-Seine", "Clichy", "Chambéry",
            "Troyes", "Montauban", "Niort", "Lorient", "Sarcelles",
            "Villejuif", "Saint-André", "Hyères", "Saint-Quentin", "Beauvais",
            "Cholet", "Maisons-Alfort", "Épinay-sur-Seine", "Meaux", "Chelles"
        )

        db.beginTransaction()
        try {
            frenchCityList.forEach { cityName ->
                // Escape single quotes by replacing ' with ''
                val escapedCityName = cityName.replace("'", "''")
                db.execSQL("INSERT OR IGNORE INTO cities (name) VALUES ('$escapedCityName')")
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }
}
