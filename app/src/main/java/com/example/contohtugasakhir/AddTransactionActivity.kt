package com.example.contohtugasakhir

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddTransactionActivity : AppCompatActivity() {
    private lateinit var addTransactionButton : Button
    private lateinit var labelInput : TextInputEditText
    private lateinit var amountInput : TextInputEditText
    private lateinit var descriptionInput : TextInputEditText
    private lateinit var labelLayout : TextInputLayout
    private lateinit var amountLayout : TextInputLayout
    private lateinit var descriptionLayout : TextInputLayout
    private lateinit var closeButton : ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        addTransactionButton = findViewById(R.id.addTransactionButton)
        labelInput = findViewById(R.id.labelInput)
        amountInput = findViewById(R.id.amountInput)
        descriptionInput = findViewById(R.id.descriptionInput)
        labelLayout = findViewById(R.id.labelLayout)
        amountLayout = findViewById(R.id.amountLayout)
        descriptionLayout = findViewById(R.id.descriptionLayout)
        closeButton = findViewById(R.id.closeButton)

        labelInput.addTextChangedListener{
            if(it!!.count()>0) labelLayout.error = null
        }
        amountInput.addTextChangedListener{
            if(it!!.count()>0) amountLayout.error = null
        }

        addTransactionButton.setOnClickListener {
            val label = labelInput.text.toString()
            val amount = amountInput.text.toString().toDoubleOrNull()
            val description = descriptionInput.text.toString()

            if(label.isEmpty()) labelLayout.error = "Please enter a valid label"

            else if(amount == null) amountLayout.error = "Please enter a valid amount"

            else {
                val transaction = Transaction(0, label, amount, description)
                insert(transaction)
            }
        }

        closeButton.setOnClickListener{
            finish()
        }

    }
    private fun insert(transaction: Transaction){
        val db = Room.databaseBuilder(this, AppDatabase::class.java, "transactions").build()

        GlobalScope.launch{
            db.transactionDao().insertAll(transaction)
            finish()
        }

    }

}