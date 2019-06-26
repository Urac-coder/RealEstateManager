package com.openclassrooms.realestatemanager.view

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.models.Picture
import com.openclassrooms.realestatemanager.models.Property
import kotlinx.android.synthetic.main.fragment_add_property_picture_item.view.*
import java.text.DecimalFormat

class AddPropertyPictureViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val view: View = view

    fun updateWithPicture(picture: Picture) {
        view.add_property_display_picDescription.text = picture.description
        Glide.with(itemView).load(picture.url).into(view.add_property_display_pic)
    }
}