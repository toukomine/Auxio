package org.oxycblt.auxio.library.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import org.oxycblt.auxio.databinding.AlbumItemBinding
import org.oxycblt.auxio.music.models.Album
import org.oxycblt.auxio.recycler.AlbumViewHolder

class AlbumDataAdapter : ListAdapter<Album, AlbumViewHolder>(DiffCallback) {

    var data = listOf<Album>()
        set(newData) {
            field = newData
            submitList(data)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        return AlbumViewHolder(
            AlbumItemBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val album = getItem(position)

        holder.bind(album)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Album>() {
        override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean {
            return oldItem.id == newItem.id
        }
    }
}