package no.uio.ifi.in2000.joakimcm.solcelleapp.data.favorites

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import no.uio.ifi.in2000.joakimcm.solcelleapp.model.favorites.Favorite


@Dao
interface FavoritesDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: Favorite)

    @Query("Select * From favorite")
    suspend fun getFavorites(): List<Favorite>

    @Query("DELETE FROM favorite WHERE id = :id")
    suspend fun deleteFavoriteById(id: Int?)
}