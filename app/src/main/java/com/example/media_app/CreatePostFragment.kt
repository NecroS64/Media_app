package com.example.media_app

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CreatePostFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreatePostFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    // TODO: Rename and change types of parameters
    private lateinit var btnPickDate: Button
    private lateinit var tvSelectedDate: TextView
    private lateinit var btnPickTime: Button
    private lateinit var tvSelectedTime: TextView
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_post, container, false)
        if (activity != null && !requireActivity().isFinishing) {
            //(activity as MainActivity).hide_menu()
        }
        btnPickDate = view.findViewById<Button>(R.id.btnPickDateL)
        btnPickDate = view.findViewById(R.id.btnPickDateL)
        tvSelectedDate = view.findViewById<TextView>(R.id.tvSelectedDateL)

        btnPickDate.setOnClickListener {
            showDatePickerDialog(view)
        }
        btnPickTime = view.findViewById(R.id.btnPickTime)
        tvSelectedTime = view.findViewById(R.id.tvSelectedTime)

        btnPickTime.setOnClickListener {
            showTimePickerDialog(view)
        }
        val btnfinish = view.findViewById<Button>(R.id.button_finish)
        btnfinish.setOnClickListener{
            make_json(view)

        }
        // Inflate the layout for this fragment
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CreatePostFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreatePostFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun showDatePickerDialog(view: View) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(view.context, { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)

            // Форматирование даты
            val sdf = SimpleDateFormat("dd.MM.yy EE", Locale.getDefault())
            val formattedDate = sdf.format(calendar.time)

            tvSelectedDate.text = formattedDate
        }, year, month, day).show()
    }

    private fun showTimePickerDialog(view: View) {
        TimePickerDialog(view.context, { _, hourOfDay, minute ->
            // Форматирование времени в "чч:мм"
            val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
            tvSelectedTime.text = formattedTime
        }, 12, 0, true).show() // Часы, минуты, 24-часовой формат
    }

    private fun make_json(view: View) {
        val date = view.findViewById<TextView>(R.id.tvSelectedDateL)
        val time = view.findViewById<TextView>(R.id.tvSelectedTime)
        val copy = view.findViewById<TextView>(R.id.Copy_name)
        val desi = view.findViewById<TextView>(R.id.Desi_name)
        val theme = view.findViewById<TextView>(R.id.Theme_name)
        val descr = view.findViewById<TextView>(R.id.description_name)
        val tags = view.findViewById<TextView>(R.id.tags_name)
        val picture = view.findViewById<TextView>(R.id.Picture_name)
        val not = view.findViewById<RadioButton>(R.id.radioNotify)


        val json = JSONObject()
        json.put("Command","create post")
        val values = JSONObject()

        val Date = date.text.toString()
        val cmd = "0"
        val Time = time.text.toString()
        val Theme = theme.text.toString()
        val description = descr.text.toString()
        val Picture = picture.text.toString()
        val notif = not.isChecked.toString()
        var ntf = 0
        if (notif=="false") ntf = 0
        if (notif=="true") ntf = 1
        val copywriter = JSONArray().apply { put(copy.text.toString()) }
        val designer = JSONArray().apply { put(desi.text.toString()) }
        val tag = JSONArray().apply { put(tags.text.toString()) }




        values.put("date", Date)
        values.put("time", Time)
        values.put("kop", copywriter)
        values.put("dis", designer)
        values.put("theme", Theme)
        values.put("add_inf", description)
        values.put("tegs", tag)
        values.put("text status", "red")
        values.put("pict", Picture)
        values.put("pict status", "red")
        values.put("notif", ntf)

        json.put("Post values",values)
        Log.d("MytagCreatePost",json.toString())
            //tcpClient.sendJson(json.toString())
        viewModel.sendMessage(json.toString())

    }
}