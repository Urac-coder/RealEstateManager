package com.openclassrooms.realestatemanager.controllers.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.openclassrooms.realestatemanager.R
import kotlinx.android.synthetic.main.fragment_search_property.*

class SearchPropertyFragment: Fragment(){

    var iterator: Boolean = true

    companion object {
        fun newInstance(): SearchPropertyFragment{
            return SearchPropertyFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search_property, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


}