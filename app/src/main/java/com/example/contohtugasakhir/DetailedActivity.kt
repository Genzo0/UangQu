package com.example.contohtugasakhir

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DetailedActivity : AppCompatActivity() {
    private lateinit var updateButton : Button
    private lateinit var labelInput : TextInputEditText
    private lateinit var amountInput : TextInputEditText
    private lateinit var descriptionInput : TextInputEditText
    private lateinit var labelLayout : TextInputLayout
    private lateinit var amountLayout : TextInputLayout
    private lateinit var descriptionLayout : TextInputLayout
    private lateinit var closeButton : ImageButton
    private lateinit var rootView : ConstraintLayout
    private lateinit var transaction: Transaction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed)

        updateButton = findViewById(R.id.updateButton)
        labelInput = findViewById(R.id.labelInput)
        amountInput = findViewById(R.id.amountInput)
        descriptionInput = findViewById(R.id.descriptionInput)
        labelLayout = findViewById(R.id.labelLayout)
        amountLayout = findViewById(R.id.amountLayout)
        descriptionLayout = findViewById(R.id.descriptionLayout)
        closeButton = findViewById(R.id.closeButton)
        rootView = findViewById(R.id.rootView)

        transaction = intent.getSerializableExtra("transaction") as Transaction
        labelInput.setText(transaction.label)
        amountInput.setText(transaction.amount.toString())
        descriptionInput.setText(transaction.description)

        rootView.setOnClickListener{
            this.window.decorView.clearFocus()

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }

        labelInput.addTextChangedListener{
            updateButton.visibility = View.VISIBLE
            if(it!!.count()>0) labelLayout.error = null
        }
        amountInput.addTextChangedListener{
            updateButton.visibility = View.VISIBLE
            if(it!!.count()>0) amountLayout.error = null
        }
        descriptionInput.addTextChangedListener{
            updateButton.visibility = View.VISIBLE
        }

        updateButton.setOnClickListener {
            val label = labelInput.text.toString()
            val amount = amountInput.text.toString().toDoubleOrNull()
            val description = descriptionInput.text.toString()

            if(label.isEmpty()) labelLayout.error = "Please enter a valid label"

            else if(amount == null) amountLayout.error = "Please enter a valid amount"

            else {
                val transaction = Transaction(transaction.id, label, amount, description)
                update(transaction)
            }
        }

        closeButton.setOnClickListener{
            finish()
        }

    }

    private fun update(transaction: Transaction){
        val db = Room.databaseBuilder(this, AppDatabase::class.java, "transactions").build()

        GlobalScope.launch{
            db.transactionDao().update(transaction)
            finish()
        }

    }

}