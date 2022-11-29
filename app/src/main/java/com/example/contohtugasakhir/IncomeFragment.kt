package com.example.contohtugasakhir

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
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

        labelInput.addTextChangedListener{
            if(it!!.count()>0) labelLayout.error = null
        }
        amountInput.addTextChangedListener{
            if(it!!.count()>0) amountLayout.error = null
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
            val amount = amountInput.text.toString().toLongOrNull()
            val description = descriptionInput.text.toString()
            val date = dateInput.text.toString()

            if (label.isEmpty()) labelLayout.error = "Please enter a valid label"
            else if (amount == null) amountLayout.error = "Please enter a valid amount"
            else {
                val transaction = Transaction(0, label, amount, description, date)
                insert(transaction)
            }
        }

        closeButton.setOnClickListener{
            activity?.finish()
        }
        return view
    }

    private fun updateLabel(cal: Calendar) {
        val myFormat = "yyyy-MM-dd"
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