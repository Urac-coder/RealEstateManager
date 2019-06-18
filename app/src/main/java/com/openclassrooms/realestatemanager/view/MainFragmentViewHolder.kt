package com.openclassrooms.realestatemanager.view

import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import com.openclassrooms.realestatemanager.R
import androidx.core.app.NotificationCompat.getCategory
import android.content.ClipData.Item
import android.view.View
import butterknife.ButterKnife
import android.widget.ImageButton
import butterknife.BindView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.models.Property
import java.lang.ref.WeakReference
import kotlinx.android.synthetic.main.fragment_main_item.*
import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import androidx.core.app.NotificationCompat.getCategory
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_main_item.view.*
import java.text.DecimalFormat

class MainFragmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val view: View = view
    val decimalFormat = DecimalFormat("#,###,##")

    fun updateWithProperty(property: Property) {

        var price = "$" + decimalFormat.format(property.price)

        view.main_fragment_type.text = property.type
        view.main_fragment_city.text = property.city
        view.main_fragment_price.text = price
        Glide.with(itemView).load(property.photoUrl).into(view.main_fragment_item_pic)
    }
}