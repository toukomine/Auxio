/*
 * Copyright (c) 2021 Auxio Project
 * ParentAdapter.kt is part of Auxio.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.oxycblt.auxio.home.recycler

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Parent
import org.oxycblt.auxio.ui.AlbumViewHolder
import org.oxycblt.auxio.ui.ArtistViewHolder
import org.oxycblt.auxio.ui.GenreViewHolder

/**
 * A universal adapter for displaying [Parent] data.
 */
class ParentAdapter(
    private val doOnClick: (data: Parent) -> Unit,
    private val doOnLongClick: (view: View, data: Parent) -> Unit,
) : HomeAdapter<Parent>() {
    override fun getItemCount(): Int = data.size

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is Genre -> GenreViewHolder.ITEM_TYPE
            is Artist -> ArtistViewHolder.ITEM_TYPE
            is Album -> AlbumViewHolder.ITEM_TYPE
            else -> error("Unsupported item ${data[position]::class.simpleName}")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            GenreViewHolder.ITEM_TYPE -> GenreViewHolder.from(
                parent.context, doOnClick, doOnLongClick
            )

            ArtistViewHolder.ITEM_TYPE -> ArtistViewHolder.from(
                parent.context, doOnClick, doOnLongClick
            )

            AlbumViewHolder.ITEM_TYPE -> AlbumViewHolder.from(
                parent.context, doOnClick, doOnLongClick
            )

            else -> error("Invalid viewholder item type.")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = data[position]) {
            is Genre -> (holder as GenreViewHolder).bind(item)
            is Artist -> (holder as ArtistViewHolder).bind(item)
            is Album -> (holder as AlbumViewHolder).bind(item)
        }
    }
}