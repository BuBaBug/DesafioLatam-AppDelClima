import android.content.Context
import org.json.JSONArray

object FavoritosManager {
    private const val PREFS_NAME = "favoritos_prefs"
    private const val KEY_FAVORITOS = "favoritos_v2"  // Cambié la clave para evitar conflictos

    fun agregarFavorito(context: Context, ciudad: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_FAVORITOS, "[]") ?: "[]"
        val lista = JSONArray(json)

        val nuevaLista = mutableListOf<String>()

        // Agregar primero la nueva ciudad, evitando duplicados
        nuevaLista.add(ciudad)
        for (i in 0 until lista.length()) {
            val item = lista.getString(i)
            if (item != ciudad) {
                nuevaLista.add(item)
            }
        }

        // Limitar a 5 elementos
        val listaFinal = nuevaLista.take(5)

        // Guardar como JSON
        val nuevoJson = JSONArray()
        listaFinal.forEach { nuevoJson.put(it) }

        prefs.edit().putString(KEY_FAVORITOS, nuevoJson.toString()).apply()
    }

    fun obtenerFavoritos(context: Context): List<String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_FAVORITOS, "[]") ?: "[]"
        val lista = JSONArray(json)

        val resultado = mutableListOf<String>()
        for (i in 0 until lista.length()) {
            resultado.add(lista.getString(i))
        }
        return resultado
    }


}
