package com.example.contohtugasakhir

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.room.Room
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.Month
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoField
import java.time.temporal.TemporalAccessor
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [IncomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class IncomeFragment : Fragment() {
    private lateinit var addTransactionButton : Button
    private lateinit var labelInput : TextInputEditText
    private lateinit var amountInput : TextInputEditText
    private lateinit var descriptionInput : TextInputEditText
    private lateinit var labelLayout : TextInputLayout
    private lateinit var amountLayout : TextInputLayout
    private lateinit var descriptionLayout : TextInputLayout
    private lateinit var closeButton : ImageButton
    private lateinit var dateLayout : TextInputLayout
    private lateinit var dateInput : TextInputEditText
    private lateinit var incomeLayout : RelativeLayout
    private val sharedViewModel : AddViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_income, container, false)
        addTransactionButton = view.findViewById(R.id.addTransactionButton)
        labelInput = view.findViewById(R.id.labelInput)
        amountInput = view.findViewById(R.id.amountInput)
        descriptionInput = view.findViewById(R.id.descriptionInput)
        labelLayout = view.findViewById(R.id.labelLayout)
        amountLayout = view.findViewById(R.id.amountLayout)
        descriptionLayout = view.findViewById(R.id.descriptionLayout)
        closeButton = view.findViewById(R.id.closeButton)
        dateLayout = view.findViewById(R.id.dateLayout)
        dateInput = view.findViewById(R.id.dateInput)
        incomeLayout = view.findViewById(R.id.incomeLayout)

        labelInput.addTextChangedListener{
            if(it!!.count()>0) {
                labelLayout.error = null
            }
            sharedViewModel.setLabelIncome(it.toString())
        }

        amountInput.addTextChangedListener(object : TextWatcher {
            var setEditText = amountInput.text.toString().trim()
            override fun afterTextChanged(s: Editable?) {
                if(s!!.count()>0) amountLayout.error = null
                sharedViewModel.setAmountIncome(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!s.toString().equals(setEditText)){
                    amountInput.removeTextChangedListener(this)
                    var replace = s.toString().replace(Regex("[Rp.]"), "")
                    if(!replace.isEmpty()){
                        setEditText = rupiahFormats(replace.toLong())
                    } else {
                        setEditText = ""
                    }
                    amountInput.setText(setEditText)
                    amountInput.setSelection(setEditText.length)
                    amountInput.addTextChangedListener(this)
                }
            }
        })

        descriptionInput.addTextChangedListener{
            sharedViewModel.setDescriptionIncome(it.toString())
        }

        val cal = Calendar.getInstance()
        dateNow(SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis()))

        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel(cal)
        }

        dateInput.setOnClickListener{
            DatePickerDialog(requireContext(), dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        addTransactionButton.setOnClickListener {
            val label = labelInput.text.toString()
            val amount = amountInput.text.toString().replace(Regex("[Rp.]"), "").toLongOrNull()
            val description = descriptionInput.text.toString()
            val date = dateInput.text.toString()

            if (label.isEmpty()) labelLayout.error = "Please enter a valid label"
            else if (amount == null) amountLayout.error = "Please enter a valid amount"
            else {
                val transaction = Transaction(0, label, amount, description, date)
                insert(transaction)
            }
        }

        incomeLayout.setOnClickListener{
            this.activity?.window?.decorView?.clearFocus()

            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }

        closeButton.setOnClickListener{
            activity?.finish()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        labelInput = view.findViewById(R.id.labelInput)
        amountInput = view.findViewById(R.id.amountInput)
        descriptionInput = view.findViewById(R.id.descriptionInput)

        labelInput.setText(sharedViewModel.getLabelIncome())
        amountInput.setText(sharedViewModel.getAmountIncome())
        descriptionInput.setText(sharedViewModel.getDescriptionIncome())
    }

    private fun getMonth(month : Int): String{
        val localeId = Locale("id", "ID")
        val month = Month.of(month).getDisplayName(TextStyle.FULL_STANDALONE, localeId)
        return month
    }

    private fun dateNow(format: String){
        val date = format.split('-')
        val year = date[0]
        val month = getMonth(date[1].toString().toInt())
        val day = date[2]
        dateInput.setText(day + " " + month + " " + year)
    }

    private fun updateLabel(cal: Calendar) {
        val myFormat = "yyyy-MM-dd"
        val localeIndonesia = Locale("id", "ID")
        val sdf = SimpleDateFormat(myFormat, localeIndonesia)
        val date =sdf.format(cal.time).split('-')
        val year = date[0]
        val month = getMonth(date[1].toString().toInt())
        val day = date[2]
        dateInput.setText(day + " " + month + " " + year)
    }

    private fun insert(transaction: Transaction){
        val db = Room.databaseBuilder(requireContext(), AppDatabase::class.java, "transactions").build()

        GlobalScope.launch{
            db.transactionDao().insert(transaction)
            activity?.finish()
        }
    }

    private fun rupiahFormats(number : Long) : String{
        val localeId = Locale("id", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeId)
        var rupiahFormats = numberFormat.format((number))
        var split = rupiahFormats.split(',')
        var length = split[0].length
        if(number >= 0){
            return split[0].substring(0,2)+"."+split[0].substring(2,length)
        } else {
            return split[0].substring(0,3)+"."+split[0].substring(3,length)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment IncomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            IncomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}