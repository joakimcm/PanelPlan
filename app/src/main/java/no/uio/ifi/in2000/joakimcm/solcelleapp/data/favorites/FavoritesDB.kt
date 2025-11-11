package no.uio.ifi.in2000.joakimcm.solcelleapp.data.favorites

import androidx.room.Database
import androidx.room.RoomDatabase
import no.uio.ifi.in2000.joakimcm.solcelleapp.model.favorites.Favorite

@Database(version = 4, entities = [Favorite::class], exportSchema = false)
abstract class FavoritesDB : RoomDatabase() {
    abstract fun getFavoritesDAO(): FavoritesDAO
}