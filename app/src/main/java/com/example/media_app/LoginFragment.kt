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
import androidx.navigation.findNavController
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


lateinit var tcpClient: TcpClient

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
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
                    change_TCP_server(ip_server)
                    swo.text = "server"
                } else {
                    change_TCP_server(ip_local)
                    swo.text = "local"
                }
            }
            if (swo.isChecked) {
                serverIp = ip_server
            } else {
                serverIp = ip_local
            }

            val serverPort = 3749         // Порт сервера
            tcpClient = TcpClient(serverIp, serverPort)
            Log.d("MyTagLogin","try to connect")
            tcpClient.connect()
            val but = view.findViewById<Button>(R.id.button2)
            but.setOnClickListener {
                val name = view.findViewById<EditText>(R.id.editTextText)

                if (name.text.isEmpty()) {
                    val tx = view.findViewById<TextView>(R.id.textView)
                    tx.text = "Имя напиши, дурачок)))"
                    return@setOnClickListener
                }

                Log.d("myTag_textEdit", name.text.toString());
                tcpClient.onResponseReceivedListener = object : OnResponseReceivedListener {
                    override fun onResponseReceived(response: String) {
                        // Здесь можно обновить UI в FirstActivity
                        val tx = view.findViewById<TextView>(R.id.textView)
                        Log.d("myTag_main", response);
                        val json = JSONObject(response)
                        val answer = json.optString("Answer")
                        if (answer == "succesfull") {
                            tx.text = "нашелся"
                            TCP_send_post()
                            next()
                            if (activity != null && !requireActivity().isFinishing) {
                                (activity as MainActivity).add_menu()
                            }



                            view.findNavController()
                                .navigate(R.id.action_loginFragment_to_postFragment2)

                        } else if (response == "failed") {
                            tx.text = "ты кто нафиг?"
                        } else {
                            Log.d("myTag_main", "не там");
                            Log.d("myTag_main", response);
                        }
                    }
                }
                autho(name.text.toString())
            }
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
    fun change_TCP_server(ip: String )
    {
        tcpClient.disconnect()
        val serverPort = 3749         // Порт сервера
        tcpClient = TcpClient(ip, serverPort)
        tcpClient.connect()
    }

    fun TCP_send_post()
    {
        val json = JSONObject()
        json.put("Command","send post")
        json.put("Filter", "Null")
        tcpClient.sendJson(json.toString())
    }

    fun autho(name: String)
    {
        val json = JSONObject()
        json.put("Command","authorization")
        json.put("Name", name)
        tcpClient.sendJson(json.toString())
    }

    fun next()
    {
//        val intent =  Intent(this, ViewPost::class.java)
//        startActivity(intent)
    }
    override fun onDestroy() {
        tcpClient.disconnect() // Закрытие соединения
        super.onDestroy()
    }
}