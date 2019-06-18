package com.openclassrooms.realestatemanager.view.adapter

import android.content.ClipData.Item
import com.openclassrooms.realestatemanager.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.view.MainFragmentViewHolder
import kotlinx.android.synthetic.main.fragment_main_item.*
import java.util.zip.Inflater


class MainFragmentAdapter () : RecyclerView.Adapter<MainFragmentViewHolder>() {

    // FOR DATA
    private var property: List<Property>

    init {
        this.property = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainFragmentViewHolder {
        val context: Context = parent.context
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.fragment_main_item, parent, false)

        return MainFragmentViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: MainFragmentViewHolder, position: Int) {
        viewHolder.updateWithProperty(this.property[position])
    }

    override fun getItemCount(): Int {
        return this.property.size
    }

    fun getProperty(position: Int): Property {
        return this.property[position]
    }

    fun updateData(property: List<Property>) {
        this.property = property
        this.notifyDataSetChanged()
    }
}