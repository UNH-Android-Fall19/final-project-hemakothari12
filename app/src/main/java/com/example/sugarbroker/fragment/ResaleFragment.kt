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
class ResaleFragment : Fragment() {

    private var root: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_resale, container, false)
        // Inflate the layout for this fragment
        return root
    }


}
