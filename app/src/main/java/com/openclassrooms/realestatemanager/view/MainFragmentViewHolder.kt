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
import kotlinx.android.synthetic.main.fragment_main_item.view.*

class MainFragmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val test = view.main_fragment_txt

    fun updateWithProperty(property: Property) {

        test.text = property.description
    }
}