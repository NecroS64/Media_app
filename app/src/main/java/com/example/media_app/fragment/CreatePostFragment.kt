package com.example.media_app.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.media_app.MainActivity
import com.example.media_app.MainViewModel
import com.example.media_app.R
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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
    private lateinit var desi: Spinner
    private lateinit var copy: Spinner
    private lateinit var copyName: String
    private lateinit var desiName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_post, container, false)
        if (activity != null && !requireActivity().isFinishing) {
            (activity as MainActivity).hide_menu()
        }
        btnPickDate = view.findViewById<Button>(R.id.btnPickDateL)
        btnPickDate = view.findViewById(R.id.btnPickDateL)
        tvSelectedDate = view.findViewById<TextView>(R.id.tvSelectedDateL)
        desi = view.findViewById(R.id.spinnerDesigner)
        copy = view.findViewById(R.id.spinnerCopywriter)
        setSpinnerDes(desi)
        setSpinnerCopy(copy)
        btnPickDate.setOnClickListener {
            showDatePickerDialog(view)
        }
        btnPickTime = view.findViewById(R.id.btnPickTime)
        tvSelectedTime = view.findViewById(R.id.tvSelectedTime)

        btnPickTime.setOnClickListener {
            showTimePickerDialog(view)
        }
        val btnfinish = view.findViewById<Button>(R.id.button_finish)
        btnfinish.setOnClickListener {
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
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
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

    fun setSpinnerCopy(spinnerRight: Spinner) {
        val currentList = viewModel.people.value ?: emptyList()
        val keys = listOf("Выберите копирайтера") + currentList.map { it.name }

        val adapter = object : ArrayAdapter<String>(
            requireContext(), android.R.layout.simple_spinner_item, keys
        ) {
            override fun isEnabled(position: Int): Boolean {
                return position != 0 // отключаем первый (подсказку)
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                (view as TextView).setTextColor(if (position == 0) Color.GRAY else Color.BLACK)
                return view
            }
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRight.adapter = adapter

        spinnerRight.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position > 0) {
                    val key = keys[position]
                    val value = currentList[position - 1]
                    copyName = key
                    Log.d("MyTag_createPeople", "Выбрано: $key -> $value")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }



    fun setSpinnerDes(spinnerRight: Spinner) {
        val currentList = viewModel.people.value ?: emptyList()
        val keys = listOf("Выберите дизайнера") + currentList.map { it.name }

        val adapter = object : ArrayAdapter<String>(
            requireContext(), android.R.layout.simple_spinner_item, keys
        ) {
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                (view as TextView).setTextColor(if (position == 0) Color.GRAY else Color.BLACK)
                return view
            }
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRight.adapter = adapter

        spinnerRight.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position > 0) {
                    val key = keys[position]
                    val value = currentList[position - 1]
                    desiName = key
                    Log.d("MyTag_createPeople", "Выбрано: $key -> $value")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }



    private fun make_json(view: View) {
        val date = view.findViewById<TextView>(R.id.tvSelectedDateL)
        val time = view.findViewById<TextView>(R.id.tvSelectedTime)
        //val copy = view.findViewById<TextView>(R.id.Copy_name)
        //val desi = view.findViewById<TextView>(R.id.Desi_name)


        val theme = view.findViewById<TextView>(R.id.Theme_name)
        val descr = view.findViewById<TextView>(R.id.description_name)
        val tags = view.findViewById<TextView>(R.id.tags_name)
        val picture = view.findViewById<TextView>(R.id.Picture_name)
        val not = view.findViewById<Switch>(R.id.switchNotify)


        val json = JSONObject()
        json.put("Command", "create post")
        val values = JSONObject()

        val Date = date.text.toString()
        val cmd = "0"
        val Time = time.text.toString()
        val Theme = theme.text.toString()
        val description = descr.text.toString()
        val Picture = picture.text.toString()
        var ntf = 0
        if (not.isChecked) ntf = 1
        val copywriter = JSONArray().apply { put(copyName) }
        val designer = JSONArray().apply { put(desiName) }
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

        json.put("Post values", values)
        Log.d("MytagCreatePost", json.toString())
        //tcpClient.sendJson(json.toString())
        viewModel.sendMessage(json.toString())

    }
}