package com.example.media_app.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.media_app.MainActivity
import com.example.media_app.MainViewModel
import com.example.media_app.R

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
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
//        val view = inflater.inflate(R.layout.fragment_login, container, false)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("MyTag_v", "create")

        val swo = view.findViewById<Switch>(R.id.switch1)
        swo.setOnClickListener {
            Log.d("myTag_switch", "click")
            swo.text = if (swo.isChecked) "server" else "local"
        }

        view.findViewById<Button>(R.id.rsbtn).setOnClickListener {
            viewModel.deleteAllPost()
        }

        view.findViewById<Button>(R.id.button2).setOnClickListener {
            val name = view.findViewById<EditText>(R.id.editTextText)
            if (name.text.isEmpty()) {
                view.findViewById<TextView>(R.id.textView).text = "Имя напиши, дурачок)))"
                return@setOnClickListener
            }

            Log.d("myTag_textEdit", name.text.toString())
            viewModel.autho(name.text.toString())
        }

        viewModel.navigateToDetails.observe(viewLifecycleOwner) {
            if (activity != null && !requireActivity().isFinishing) {
                (activity as MainActivity).add_menu()
            }
            viewModel.saveUser(true)
            findNavController().navigate(R.id.action_loginFragment_to_postFragment2)
        }

        if (viewModel.loadUser()) {
            Log.d("MyTag_Cache", "loading cache")
            viewModel.sendPost()
            if (activity != null && !requireActivity().isFinishing) {
                (activity as MainActivity).add_menu()
            }
            findNavController().navigate(R.id.action_loginFragment_to_postFragment2)
        }
    }


    override fun onDestroy() {
        //tcpClient.disconnect() // Закрытие соединения
        super.onDestroy()
    }
}