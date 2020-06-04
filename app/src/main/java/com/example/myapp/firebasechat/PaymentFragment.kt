package com.example.myapp.firebasechat

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


class PaymentFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val view= inflater.inflate(R.layout.fragment_payment, container, false)
        paymentData()
        createLayout()
        return view

    }

    private fun createLayout() {

    }

    private fun paymentData() {

    }


}
