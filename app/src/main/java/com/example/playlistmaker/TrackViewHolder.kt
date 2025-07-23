package com.example.playlistmaker

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val trackName: TextView = itemView.findViewById(R.id.songName)
    private val artistName: TextView = itemView.findViewById(R.id.songArtist)
    private val trackTime: TextView = itemView.findViewById(R.id.songTime)
    private val songPicture: ImageView = itemView.findViewById(R.id.songPicture)

    fun bind(track: Track) {
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = track.trackTime

        val cornerRadiusPx = dpToPx(2f, itemView.context)
        Glide.with(itemView.context)
            .load(track.artworkUrl100)
            .transform(RoundedCorners(cornerRadiusPx))
            .placeholder(R.drawable.lupa)
            .into(songPicture)
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }
}