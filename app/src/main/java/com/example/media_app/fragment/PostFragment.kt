package com.example.media_app.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
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
import com.example.media_app.adapter.MyAdapterPost
import com.example.media_app.R
import kotlin.math.abs

class PostFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var gestureDetector: GestureDetector
    private var isAtTop = false


    companion object {
        fun newInstance() = PostFragment()
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("MyTag_postFragment", "create")
        if (activity != null && !requireActivity().isFinishing) {
            (activity as MainActivity).add_menu()
        }


        val view = inflater.inflate(R.layout.fragment_post, container, false)
        recyclerView = view.findViewById(R.id.recycle)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        val adapt = MyAdapterPost(emptyList(), viewModel)
        recyclerView.adapter = adapt

        val newPostButton = view.findViewById<Button>(R.id.newPost)
        newPostButton.setOnClickListener {
            view.findNavController()
                .navigate(R.id.action_postFragment_to_createPostFragment)
        }
        viewModel.posts.observe(viewLifecycleOwner) { postList ->
            adapt.updateData(postList)
        }
        // 1. Определяем, вверху ли пользователь
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = rv.layoutManager as LinearLayoutManager
                isAtTop = layoutManager.findFirstVisibleItemPosition() == 0
            }
        })

        // 2. Детектор жестов
        gestureDetector =
            GestureDetector(requireContext(), object : GestureDetector.SimpleOnGestureListener() {
                override fun onFling(
                    e1: MotionEvent?,
                    p1: MotionEvent,
                    velocityX: Float,
                    velocityY: Float
                ): Boolean {
                    val deltaY = (p1?.y ?: 0f) - (e1?.y ?: 0f)
                    if (deltaY > 500 && abs(velocityY) > 400 && isAtTop) {
                        viewModel.updatePostList()
                        return true
                    }
                    return false
                }
            })

        // 3. Применяем обработчик к RecyclerView
        recyclerView.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            false
        }

        return view
    }





}