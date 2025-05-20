package com.example.media_app.adapter

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.media_app.MainViewModel
import com.example.media_app.api.PeopleTable
import com.example.media_app.R

class MyAdapterPeople(var data: List<PeopleTable>, private val viewModel: MainViewModel) : RecyclerView.Adapter<MyAdapterPeople.MyViewHolder>() {


    private var expandedPosition: Int? = null

    class MyViewHolder(val row: View) : RecyclerView.ViewHolder(row) {
//        val design = row.findViewById<TextView>(R.id.desi_val)
        val nameView = row.findViewById<TextView>(R.id.name)
        val roleView = row.findViewById<TextView>(R.id.role)
        val statusView = row.findViewById<ImageView>(R.id.statusPeople)
        val dopBut = row.findViewById<LinearLayout>(R.id.dopButton)
        val delBut = row.findViewById<ImageButton>(R.id.deleteButton)
        val editBut = row.findViewById<ImageButton>(R.id.editButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.people, parent, false)
        return MyViewHolder(layout)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (data.isNotEmpty()) {
            val actualPosition = holder.adapterPosition
            val isExpanded = actualPosition == expandedPosition

            // Анимация расширения или сворачивания
            animateExpandableView(holder.dopBut, isExpanded)

            // Заполнение данных
            val people = data[position]
            holder.nameView.text = people.name
            when(people.role){
                0 -> holder.roleView.text ="Копирайтер"
                1 -> holder.roleView.text ="Дизайнер"
                2 -> holder.roleView.text ="Видеограф"
            }

            // Изменение статуса
            setStatus(holder.statusView, people.workStatus!!)

            // Обработчик клика для раскрытия/сворачивания
            holder.itemView.setOnClickListener {
                val previousExpanded = expandedPosition
                if (isExpanded) {
                    expandedPosition = null
                } else {
                    expandedPosition = actualPosition
                }
                notifyItemChanged(actualPosition)
                previousExpanded?.let { notifyItemChanged(it) }
            }
            holder.delBut.setOnClickListener{
                //viewModel.deletePost(post.id)
                Log.d("MyTag_adaptPeople","click on delete button")
            }
            holder.editBut.setOnClickListener{
                Log.d("MyTag_adaptPeople","click on edit button")
            }
        }
    }

    private fun animateExpandableView(dopBut: LinearLayout, isExpanded: Boolean) {
        if (isExpanded) {
            dopBut.visibility = View.VISIBLE
            dopBut.measure(
                View.MeasureSpec.makeMeasureSpec(dopBut.width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            val targetHeight = dopBut.measuredHeight
//            val targetHeight = 126
            Log.d("MyTag_Anim","targetHeight = $targetHeight")

            dopBut.layoutParams.height = 0

            val animator = ValueAnimator.ofInt(0, targetHeight)
            animator.addUpdateListener { animation ->
                dopBut.layoutParams.height = animation.animatedValue as Int
                dopBut.requestLayout()
            }
            animator.duration = 300
            animator.start()
        } else {
            val currentHeight = dopBut.height
            val animator = ValueAnimator.ofInt(currentHeight, 0)
            animator.addUpdateListener { animation ->
                dopBut.layoutParams.height = animation.animatedValue as Int
                dopBut.requestLayout()
            }
            animator.duration = 300
            animator.start()
        }
    }

    private fun setStatus(view: ImageView, status: Int) {
        when (status) {
            0 -> view.setImageResource(R.drawable.ic_sleep)
            1 -> view.setImageResource(R.drawable.ic_working)}
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: List<PeopleTable>) {
        data = newData
        notifyDataSetChanged() // Для оптимизации используйте DiffUtil
    }
}