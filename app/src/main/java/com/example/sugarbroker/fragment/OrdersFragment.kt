package com.example.sugarbroker.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.sugarbroker.R

/**
 * A simple [Fragment] subclass.
 */
class OrdersFragment : Fragment() {

    private var root: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_orders, container, false)

        return root
    }


}
