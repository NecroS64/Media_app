package com.example.media_app.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.media_app.MainActivity
import com.example.media_app.MainViewModel
import com.example.media_app.adapter.MyAdapterPeople
import com.example.media_app.R

/**
 * A simple [Fragment] subclass.
 * Use the [PeopleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PeopleFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d("MyTag_peopleFragment", "create")
        if (activity != null && !requireActivity().isFinishing) {
            (activity as MainActivity).add_menu()
        }


        val view = inflater.inflate(R.layout.fragment_people, container, false)
        recyclerView = view.findViewById(R.id.recycle)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        val adapt = MyAdapterPeople(emptyList(), viewModel)
        recyclerView.adapter = adapt

        val newPostButton = view.findViewById<Button>(R.id.newPeople)
        newPostButton.setOnClickListener {
            view.findNavController()
                .navigate(R.id.action_peopleFragment_to_createPeopleFragment)
        }
        viewModel.people.observe(viewLifecycleOwner) {  peopleList->
            adapt.updateData(peopleList)
        }


        return view
        //return inflater.inflate(R.layout.fragment_people, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PeopleFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PeopleFragment().apply {
                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
                }
            }
    }
}