package com.example.media_app.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.media_app.R
import com.example.media_app.MainViewModel
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [ReportFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ReportFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
        }
    }
    val args: ReportFragmentArgs by navArgs()
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val switchState = args.switchState
        val spinnerValue = args.spinnerValue
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_report, container, false)
        val btn = view.findViewById<Button>(R.id.repBtn)
        val filtbtn = view.findViewById<Button>(R.id.filtBtn)
        val text = view.findViewById<TextView>(R.id.textView5)
        filtbtn.setOnClickListener {
            view.findNavController()
                .navigate(R.id.action_reportFragment_to_filterReportFragment)
        }
        btn.setOnClickListener {

            var spin:Char ='0'
            when(spinnerValue)
            {
                0 -> spin ='0'
                1 -> spin ='v'
                2 -> spin ='d'
                3 -> spin ='k'
            }
            viewModel.viewModelScope.launch {
                val result = viewModel.getPeopleCostCount(switchState,spinnerValue-1)


            for (item in result)
            {
                text.setText(text.text.toString() +"\n${item.name.toString()} \nРоль:${when(item.role){
                    0 -> "Видеограф"
                    1 -> "Дизайнер"
                    2 -> "Копирайтер"

                    else -> "not role"
                }           } \nСделано постов:${item.post_count.toString()} \n")
            }
            }
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
         * @return A new instance of fragment ReportFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ReportFragment().apply {
                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
                }
            }
    }
}