package com.openclassrooms.realestatemanager.controllers.fragments

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.injection.Injection
import com.openclassrooms.realestatemanager.utils.*
import com.openclassrooms.realestatemanager.view.PropertyViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_add_property.*



abstract class BaseFragment : Fragment(){

    lateinit var propertyViewModel: PropertyViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(getFragmentLayout(), container, false)

        addToOnCreateView(rootView, savedInstanceState)

        setHasOptionsMenu(true)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureViewModel()
        addToOnViewCreated()
    }

    abstract fun getFragmentLayout(): Int

    abstract fun addToOnCreateView(rootView: View, savedInstanceState: Bundle?)

    abstract fun addToOnViewCreated()

    // ---------------------
    // CONFIGURATION
    // ---------------------

    private fun configureViewModel() {
        val mViewModelFactory = Injection.provideViewModelFactory(context!!)
        this.propertyViewModel = ViewModelProviders.of(this, mViewModelFactory).get(PropertyViewModel::class.java)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_toolbar, menu)

        if (SharedPref.read(PREF_DEVICE, "EURO")!! == "EURO"){
            menu.getItem(0).icon = ContextCompat.getDrawable(context!!, R.drawable.ic_euro_symbol_24)
        } else{
            menu.getItem(0).icon = ContextCompat.getDrawable(context!!, R.drawable.ic_attach_money_24)
        }

        super.onCreateOptionsMenu(menu, inflater)
    }
}