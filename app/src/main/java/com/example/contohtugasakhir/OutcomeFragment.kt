package com.example.contohtugasakhir

import android.app.DatePickerDialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.room.Room
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OutcomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OutcomeFragment : Fragment() {
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
    private lateinit var outcomeLayout : ConstraintLayout
    private val sharedViewModel : AddViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_outcome, container, false)
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
        outcomeLayout = view.findViewById(R.id.outcomeLayout)

        labelInput.addTextChangedListener{
            if(it!!.count()>0) {
                labelLayout.error = null
            }
            sharedViewModel.setLabelOutcome(it.toString())
        }

        amountInput.addTextChangedListener(object : TextWatcher {
            var setEditText = amountInput.text.toString().trim()
            override fun afterTextChanged(s: Editable?) {
                if(s!!.count()>0) {
                    amountLayout.error = null
                }
                sharedViewModel.setAmountOutcome(s.toString())
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

            sharedViewModel.setDescriptionOutcome(it.toString())

        }

        val cal = Calendar.getInstance()
        dateInput.setText(SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis()))

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
                val transaction = Transaction(0, label, amount*-1, description, date)
                insert(transaction)
            }
        }

        outcomeLayout.setOnClickListener{
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

        labelInput.setText(sharedViewModel.getLabelOutcome())
        amountInput.setText(sharedViewModel.getAmountOutcome())
        descriptionInput.setText(sharedViewModel.getDescriptionOutcome())
    }



    private fun updateLabel(cal: Calendar) {
        val myFormat = "yyyy-MM-dd" // mention the format you need
        val localeIndonesia = Locale("id", "ID")
        val sdf = SimpleDateFormat(myFormat, localeIndonesia)
        dateInput.setText(sdf.format(cal.time))
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
         * @return A new instance of fragment OutcomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OutcomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}