package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(
    // Нужно:
    // 1. Сохранение
    // 2. Чтение
    // 3. Очистка
    private val sharedPreferences: SharedPreferences
) {
    private val gson = Gson()
    companion object {
        private const val SEARCH_HISTORY_KEY = "search_history"
        private const val MAX_HISTORY_SIZE = 10
    }

    fun saveTrack(track: Track) {
        // начинаем сохранение. сначала получим наш список из шеред преференсес
        val tracks = getSearchHistory().toMutableList()

        tracks.removeAll { it.trackId == track.trackId } // Сразу удалим трек если он есть
        
        // добавляем трек в начало списка
        tracks.add(0, track)
        
        // если слишком много, то удалим последний
        if (tracks.size > MAX_HISTORY_SIZE) {
            tracks.removeAt(tracks.size - 1)
        }
        
        // переведем в нужный формат и сохраним обновленный список
        val json = gson.toJson(tracks)
        sharedPreferences.edit()
            .putString(SEARCH_HISTORY_KEY, json)
            .apply()
    }
    
    fun getSearchHistory(): List<Track> { // достанем список треков из памяти
        val jsonHist = sharedPreferences.getString(SEARCH_HISTORY_KEY, null)
        if (jsonHist != null) {
            return  gson.fromJson(jsonHist, object : TypeToken<List<Track>>() {}.type) // просим gson превратить это в List(Track). TypeToken - чтобы заглянуть внутрь списка
        } else {
            return  emptyList()
        }
    }
    
    fun clearHistory() { // удаляем всю историю удаля значение по ключу
        sharedPreferences.edit()
            .remove(SEARCH_HISTORY_KEY)
            .apply()
    }
}