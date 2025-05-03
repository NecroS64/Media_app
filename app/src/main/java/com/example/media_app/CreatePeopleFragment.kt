package com.example.media_app

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import org.json.JSONArray
import org.json.JSONObject
import vadiole.colorpicker.ColorModel
import vadiole.colorpicker.ColorPickerDialog

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CreatePeopleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreatePeopleFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()

    // TODO: Rename and change types of parameters\
    private lateinit var btnPickTextColor: Button
    private lateinit var btnPickBackColor: Button
    private lateinit var newName: EditText
    private lateinit var resultName:TextView
    private lateinit var role: Spinner
    private lateinit var right: Spinner

    var currentRole: String = "null"
    var currentRight: Int = 0
    var currentTextColor = Color.BLUE
    var currentBackColor = Color.WHITE

    private var param1: String? = null
    private var param2: String? = null
    val roleMap = linkedMapOf(
        "Выбери роль" to "",
        "Чмо" to "v",
        "Лох обоссанный" to "d",
        "Питонис-онанист" to "c"
    )
    val rightMap = linkedMapOf(
        "Выбери права" to "",
        "водительские" to 0,
        "мужские" to 1,
        "настоящие" to 2
    )


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
        val view = inflater.inflate(R.layout.fragment_create_people, container, false)
        role = view.findViewById(R.id.spinnerRole)
        right = view.findViewById(R.id.spinnerRight)
        newName = view.findViewById(R.id.nameNewSlave)
        btnPickBackColor = view.findViewById(R.id.btnPickBackColor)
        btnPickTextColor = view.findViewById(R.id.btnPickTextColor)
        resultName = view.findViewById(R.id.resultName)
        setSpinnerRole(role)
        setSpinnerRight(right)
        btnPickBackColor.setOnClickListener {
            setBackColor()
        }
        btnPickTextColor.setOnClickListener {
            setTextColor()
        }
        newName.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                resultName.text=p0.toString()
            }

            override fun afterTextChanged(p0: Editable?) {
                resultName.text=p0.toString()
            }

        })
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
         * @return A new instance of fragment CreatePeopleFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreatePeopleFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun setSpinnerRole(spinnerROLE: Spinner) {
        val keys = roleMap.keys.toList() // список ключей (для показа пользователю)

        val adapter = object : ArrayAdapter<String>(
            requireContext(), android.R.layout.simple_spinner_item, keys
        ) {
            override fun isEnabled(position: Int): Boolean {
                return position != 0 // отключаем первый (подсказку)
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view = super.getDropDownView(position, convertView, parent)
                (view as TextView).setTextColor(if (position == 0) Color.GRAY else Color.BLACK)
                return view
            }
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerROLE.adapter = adapter
        spinnerROLE.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    val key = keys[position]
                    val value = roleMap[key] ?: ""
                    currentRole = value
                    // Используй `value` — это внутреннее значение
                    Log.d("MyTag_createPeople", "Выбрано: $key -> $value")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

    }

    fun setSpinnerRight(spinnerRight: Spinner) {
        val keys = rightMap.keys.toList() // список ключей (для показа пользователю)

        val adapter = object : ArrayAdapter<String>(
            requireContext(), android.R.layout.simple_spinner_item, keys
        ) {
            override fun isEnabled(position: Int): Boolean {
                return position != 0 // отключаем первый (подсказку)
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view = super.getDropDownView(position, convertView, parent)
                (view as TextView).setTextColor(if (position == 0) Color.GRAY else Color.BLACK)
                return view
            }
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRight.adapter = adapter
        spinnerRight.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    val key = keys[position]
                    val value = rightMap[key] ?: ""
                    currentRight = value as Int
                    // Используй `value` — это внутреннее значение
                    Log.d("MyTag_createPeople", "Выбрано: $key -> $value")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

    }

    fun setTextColor() {

        val colorPicker = ColorPickerDialog.Builder()
            .setInitialColor(currentTextColor)
            .setColorModel(ColorModel.RGB)
            .setColorModelSwitchEnabled(true)
            .setButtonOkText(android.R.string.ok)
            .setButtonCancelText(android.R.string.cancel)
            .onColorSelected { color: Int ->
                // Обработка выбранного цвета
                currentTextColor=color
                resultName.setTextColor(color)
            }
            .create()

        colorPicker.show((context as? AppCompatActivity)?.supportFragmentManager!!, "color_picker")


    }

    fun setBackColor() {
        val colorPicker = ColorPickerDialog.Builder()
            .setInitialColor(currentBackColor)
            .setColorModel(ColorModel.RGB)
            .setColorModelSwitchEnabled(true)
            .setButtonOkText(android.R.string.ok)
            .setButtonCancelText(android.R.string.cancel)
            .onColorSelected { color: Int ->
                // Обработка выбранного цвета
                currentBackColor = color
                resultName.setBackgroundColor(color)
            }
            .create()

        colorPicker.show((context as? AppCompatActivity)?.supportFragmentManager!!, "color_picker")
    }
    private fun make_json(view: View) {


        var red = Color.red(currentTextColor) / 255f
        var green = Color.green(currentTextColor) / 255f
        var blue = Color.blue(currentTextColor) / 255f
        var rgbTFloat = "$red:$green:$blue"
        val pass = view.findViewById<EditText>(R.id.secretCode).text
        red = Color.red(currentTextColor) / 255f
        green = Color.green(currentTextColor) / 255f
        blue = Color.blue(currentTextColor) / 255f
        var rgbBFloat = "$red:$green:$blue"
        val json = JSONObject()
        json.put("Command","register")
        val values = JSONObject()
        values.put("Name",newName.text)
        values.put("Role",currentRole)
        values.put("Rights",currentRight)
        values.put("Text color",rgbTFloat)
        values.put("Back color",rgbBFloat)
        values.put("Password",pass)
        json.put("People values",values)
        Log.d("Mytag_CreatePeople",json.toString())
        //tcpClient.sendJson(json.toString())
        viewModel.sendMessage(json.toString())

    }
}