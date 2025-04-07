package com.example.media_app

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject
import java.sql.Time


val layoutList = mutableListOf<View>()


class PostFragment : Fragment() {



    companion object {
        fun newInstance() = PostFragment()
    }

    private val viewModel: PostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (activity != null && !requireActivity().isFinishing) {
            (activity as MainActivity).add_menu()
        }

        val view = inflater.inflate(R.layout.fragment_post, container, false)
        val rv: RecyclerView = view.findViewById(R.id.recycle)
        rv.layoutManager = LinearLayoutManager(view.context)
        val adapt = MyAdapterPost(MainActivity.pvm.getPosts())
        rv.adapter = adapt
        setupTCPClient(view,adapt)
        val newPostButton = view.findViewById<Button>(R.id.newPost)
        newPostButton.setOnClickListener {
            view.findNavController()
                .navigate(R.id.action_postFragment_to_createPostFragment)
        }
        return view
    }


    private fun setupTCPClient(inflater: View,adapt :MyAdapterPost) {
        Log.d("myTag_view", "Setting up TCP client")

        tcpClient.onResponseReceivedListener = object : OnResponseReceivedListener {
            override fun onResponseReceived(response: String) {
                requireActivity().runOnUiThread {
                    try {
                        Log.d("myTag_view", "Received: $response")
                        Log.d("myTag_view", "Current layout count: ${layoutList.size}")

                        val resp = JSONObject(response)
                        val json = resp.optJSONObject("Post values") ?: return@runOnUiThread
                        val id = json.optInt("id",0)
                        val date = json.optString("date","")
                        val time = json.optString("time","")
                        val copywriter = json.optJSONArray("kop")?.optString(0, "") ?: ""
                        val designer = json.optJSONArray("dis")?.optString(0, "") ?: ""
                        val theme = json.optString("theme", "")
                        val add_info = json.optString("add_inf","")
                        val tag = json.optJSONArray("tegs")?.optString(0, "") ?: ""
                        val text_status = json.optString("text_status","")
                        val pict = json.optString("pict","")
                        val pict_status = json.optString("pict_status","")
                        val id_sheet = json.optInt("id_sheet",0)
                        val post = Post(id,date, time,copywriter,designer,theme, add_info,tag,
                            STATUS.valueOf(text_status).value,pict,STATUS.valueOf(pict_status).value,
                            id_sheet)

                        //adapt.addItem()
                        MainActivity.pvm.newPost(post,adapt)
                        // Создание нового элемента макета
//                        val itemView = inflater.inflate(R.layout.item_template, constraintLayoutPost, false)
//                        itemView.id = View.generateViewId()
//
//                        // Заполнение данных
//                        itemView.findViewById<TextView>(R.id.copy_val).text = copywriter
//                        itemView.findViewById<TextView>(R.id.desi_val).text = designer
//                        itemView.findViewById<TextView>(R.id.theme_val).text = theme
//                        itemView.findViewById<TextView>(R.id.tag_val).text = tag
//
//                        // Добавляем в макет
//                        constraintLayoutPost.addView(itemView)
//                        layoutList.add(itemView)
//
//                        // Применение constraint'ов
//                        applyConstraints()

                    } catch (e: Exception) {
                        Log.e("myTag_view", "Error processing response", e)
                    }
                }
            }
        }
    }


}