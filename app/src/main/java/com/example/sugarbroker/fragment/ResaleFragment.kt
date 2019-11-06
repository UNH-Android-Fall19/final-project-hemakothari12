package com.example.sugarbroker.fragment


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment

import com.example.sugarbroker.R

/**
 * [Resale Fragment] subclass.
 */
class ResaleFragment : Fragment() {

    private var root: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_resale, container, false)

        setHasOptionsMenu(true)

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater)
    }


}
