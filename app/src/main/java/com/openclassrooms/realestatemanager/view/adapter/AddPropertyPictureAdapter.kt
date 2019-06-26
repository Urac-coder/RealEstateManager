package com.openclassrooms.realestatemanager.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.models.Picture
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.view.AddPropertyPictureViewHolder
import com.openclassrooms.realestatemanager.view.MainFragmentViewHolder

class AddPropertyPictureAdapter () : RecyclerView.Adapter<AddPropertyPictureViewHolder>() {

    // FOR DATA
    private var picture: List<Picture>

    init {
        this.picture = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddPropertyPictureViewHolder {
        val context: Context = parent.context
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.fragment_add_property_picture_item, parent, false)

        return AddPropertyPictureViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: AddPropertyPictureViewHolder, position: Int) {
        viewHolder.updateWithPicture(this.picture[position])
    }

    override fun getItemCount(): Int {
        return this.picture.size
    }

    fun getPicture(position: Int): Picture {
        return this.picture[position]
    }

    fun updateData(picture: List<Picture>) {
        this.picture = picture
        this.notifyDataSetChanged()
    }
}