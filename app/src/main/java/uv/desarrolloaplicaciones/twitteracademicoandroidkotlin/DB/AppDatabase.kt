package uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.DB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.DB.DAO.SiguiedoDAO
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.DB.DAO.TipoUsuarioDAO
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.DB.DAO.TweetDAO
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.DB.DAO.UsuarioDAO
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.DB.DATA.Siguiendo
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.DB.DATA.TipoUsuario
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.DB.DATA.Tweet
import uv.desarrolloaplicaciones.twitteracademicoandroidkotlin.DB.DATA.Usuario

@Database(
    entities = [Siguiendo::class, TipoUsuario::class, Usuario::class, Tweet::class],
    version = 1
)
abstract class AppDatabase: RoomDatabase() {

    abstract fun seguidores(): SiguiedoDAO
    abstract fun TiposUsuario(): TipoUsuarioDAO
    abstract fun Usuario(): UsuarioDAO
    abstract fun Tweets(): TweetDAO

    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE

            if(tempInstance != null){
                return tempInstance
            }

            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()

                INSTANCE = instance

                return instance
            }
        }
    }
}