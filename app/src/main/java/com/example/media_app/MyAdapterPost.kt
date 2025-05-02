package com.example.media_app

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.toDrawable
import androidx.navigation.findNavController

class MyAdapterPost(var data: List<Post>) : RecyclerView.Adapter<MyAdapterPost.MyViewHolder>() {
    private var expandedPosition: Int? = null

    class MyViewHolder(val row: View) : RecyclerView.ViewHolder(row) {
        val design = row.findViewById<TextView>(R.id.desi_val)
        val copy = row.findViewById<TextView>(R.id.copy_val)
        val theme = row.findViewById<TextView>(R.id.theme_val)
        val description = row.findViewById<TextView>(R.id.description_val)
        val tag = row.findViewById<TextView>(R.id.tag_val)
        val time = row.findViewById<TextView>(R.id.time_val)
        val date = row.findViewById<TextView>(R.id.date_val)
        val copy_st = row.findViewById<View>(R.id.copy_status)
        val desi_st = row.findViewById<View>(R.id.desi_status)
        val body = row.findViewById<LinearLayout>(R.id.templ)
        val dopBut = row.findViewById<LinearLayout>(R.id.dopButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapterPost.MyViewHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.post, parent, false)
        return MyViewHolder(layout)
    }

    override fun onBindViewHolder(holder: MyAdapterPost.MyViewHolder, position: Int) {
        if (data.isNotEmpty()) {
            val actualPosition = holder.adapterPosition
            val isExpanded = actualPosition == expandedPosition

            // Анимация расширения или сворачивания
            animateExpandableView(holder.dopBut, isExpanded)

            // Заполнение данных
            val post = data[position]
            holder.design.text = post.designer
            holder.copy.text = post.copywritter
            holder.theme.text = post.theme
            holder.date.text = post.date
            holder.description.text = post.add_info
            holder.time.text = post.time

            // Изменение статуса
            setStatus(holder.copy_st, post.text_status)
            setStatus(holder.desi_st, post.pict_status)

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

    private fun setStatus(view: View, status: Int) {
        when (status) {
            0 -> view.setBackgroundResource(R.drawable.circle_red)
            1 -> view.setBackgroundResource(R.drawable.circle_yellow)
            2 -> view.setBackgroundResource(R.drawable.circle_green)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: List<Post>) {
        data = newData
        notifyDataSetChanged() // Для оптимизации используйте DiffUtil
    }
}
