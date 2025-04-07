package com.example.media_app

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.toDrawable
import androidx.navigation.findNavController

class MyAdapterPost(val data: MutableList<Post>) : RecyclerView.Adapter<MyAdapterPost.MyViewHolder>() {
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

        //val photo = photoImport()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapterPost.MyViewHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.post, parent, false)
        return MyViewHolder(layout)
    }

    override fun onBindViewHolder(holder: MyAdapterPost.MyViewHolder, position: Int) {
        val context = holder.itemView.context
        val post = data[position]
        holder.design.setText(post.designer)
        holder.copy.setText(post.copywritter)
        holder.theme.setText(post.theme)
        holder.date.setText(post.date)
        holder.description.setText(post.add_info)
//        holder.tag.setText(post.tags)
        holder.time.setText(post.time)
        if(post.text_status==0)
            holder.copy_st.setBackgroundResource(R.drawable.circle_red)
        if(post.text_status==1)
            holder.copy_st.setBackgroundResource(R.drawable.circle_yellow)
        if(post.text_status==2)
            holder.copy_st.setBackgroundResource(R.drawable.circle_green)
        if(post.pict_status==0)
            holder.desi_st.setBackgroundResource(R.drawable.circle_red)
        if(post.pict_status==1)
            holder.desi_st.setBackgroundResource(R.drawable.circle_yellow)
        if(post.pict_status==2)
            holder.desi_st.setBackgroundResource(R.drawable.circle_green)
        //holder.desc.text = data[position].name

//        holder.desc.setOnClickListener{
//            Log.d("mytagadapter"," hobby click on button " + position.toString())
//            val message = position.toString()
//            val action = HobbyFragmentDirections
//                .actionHobbyFragmentToPlaceFragment(message)
//            holder.view.findNavController().navigate(action)
//        }
    }

    override fun getItemCount(): Int = data.size

    public fun addItem() {
        //data.add(newItem)  // Добавляем элемент в список
        notifyItemInserted(data.size ) // Обновляем RecyclerView
    }
}