package com.example.media_app

import TcpClient
import android.content.Intent
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
import androidx.navigation.findNavController
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"



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
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
//        val view = inflater.inflate(R.layout.fragment_login, container, false)



        }

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            val view = inflater.inflate(R.layout.fragment_login, container, false)
            Log.d("MyTag_v", "create")
            // Inflate the layout for this fragment


            var serverIp = ip_local // IP адрес сервера
            val swo = view.findViewById<Switch>(R.id.switch1)
            swo.setOnClickListener {
                Log.d("myTag_switch", "click");
                if (swo.isChecked) {
                    //change_TCP_server(ip_server)
                    swo.text = "server"
                } else {
                    //change_TCP_server(ip_local)
                    swo.text = "local"
                }
            }

            val but = view.findViewById<Button>(R.id.button2)
            but.setOnClickListener {
                val name = view.findViewById<EditText>(R.id.editTextText)

                if (name.text.isEmpty()) {
                    val tx = view.findViewById<TextView>(R.id.textView)
                    tx.text = "Имя напиши, дурачок)))"
                    return@setOnClickListener
                }

                Log.d("myTag_textEdit", name.text.toString());
                viewModel.autho(name.text.toString())
                //viewModel.sendMessage("Привет, сервер!")
            }
            viewModel.navigateToDetails.observe(viewLifecycleOwner) {
                if (activity != null && !requireActivity().isFinishing) {
                    (activity as MainActivity).add_menu()
                }
                viewModel.send_post()
                view.findNavController()
                    .navigate(R.id.action_loginFragment_to_postFragment2)}

//            view.findViewById<Button>(R.id.butlog).setOnClickListener {
//                if (activity != null && !requireActivity().isFinishing) {
//                    (activity as MainActivity).add_menu()
//                }
//
//
//
//                view.findNavController()
//                    .navigate(R.id.action_loginFragment_to_postFragment2)
//            }
            return view







        }

    override fun onDestroy() {
        //tcpClient.disconnect() // Закрытие соединения
        super.onDestroy()
    }
}

//                if (activity != null && !requireActivity().isFinishing) {
//                    (activity as MainActivity).add_menu()
//                }
//
//
//
//                view.findNavController()
//                    .navigate(R.id.action_loginFragment_to_postFragment2)