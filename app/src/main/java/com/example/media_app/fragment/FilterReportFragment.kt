package com.example.media_app.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.media_app.R

/**
 * A simple [Fragment] subclass.
 * Use the [FilterReportFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FilterReportFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var currentRole: String = "null"
    val roleMap = linkedMapOf(
        "Выбери роль" to "",
        "Видеограф" to "v",
        "Дизайнер" to "d",
        "Копирайтер" to "k"
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_filter_report, container, false)
        val spinner = view.findViewById<Spinner>(R.id.filterSpinner)
        setSpinnerRight(spinner)
        val switch = view.findViewById<Switch>(R.id.orderSwitch)
        switch.setOnClickListener{
            Log.d("myTag_switch", "click");
            if (switch.isChecked) {
                //change_TCP_server(ip_server)
                switch.text = "По убыванию"
            } else {
                //change_TCP_server(ip_local)
                switch.text = "По возрастанию"
            }
        }
        val btn =view.findViewById<Button>(R.id.buttonReady)
        btn.setOnClickListener {
            val switchState = if (switch.isChecked) 1 else 0
            val spinnerValue = spinner.selectedItemPosition

            val action = FilterReportFragmentDirections
                .actionFilterReportFragmentToReportFragment(switchState, spinnerValue)

            view.findNavController().navigate(action)

        }
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FilterReportFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FilterReportFragment().apply {
                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun setSpinnerRight(spinner: Spinner) {
        val keys = roleMap.keys.toList() // список ключей (для показа пользователю)

        val adapter = object : ArrayAdapter<String>(
            requireContext(), android.R.layout.simple_spinner_item, keys
        ) {
            override fun isEnabled(position: Int): Boolean {
                return position != 0 // отключаем первый (подсказку)
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view = super.getDropDownView(position, convertView, parent)
                (view as TextView).setTextColor(if (position == 0) Color.GRAY else Color.BLACK)
                return view
            }
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    val key = keys[position]
                    val value = roleMap[key] ?: ""
                    currentRole = value as String
                    // Используй `value` — это внутреннее значение
                    Log.d("MyTag_filter", "Выбрано: $key -> $value")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

    }



}