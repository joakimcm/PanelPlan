package no.uio.ifi.in2000.joakimcm.solcelleapp.data.util

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import no.uio.ifi.in2000.joakimcm.solcelleapp.data.favorites.FavoritesDAO
import no.uio.ifi.in2000.joakimcm.solcelleapp.data.favorites.FavoritesDB
import javax.inject.Singleton

// Tells Hilt how to make the database
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FavoritesDB {
        return Room.databaseBuilder(
            context, FavoritesDB::class.java, "favorites-db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideFavoritesDao(database: FavoritesDB): FavoritesDAO {
        return database.getFavoritesDAO()
    }
}