package com.example.contohtugasakhir

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var transactions : List<Transaction>
    private lateinit var oldtransactions : List<Transaction>
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var linearLayoutManager : LinearLayoutManager
    private lateinit var recyclerView : RecyclerView
    private lateinit var balance : TextView
    private lateinit var budget : TextView
    private lateinit var expense : TextView
    private lateinit var addButton : FloatingActionButton
    private lateinit var db : AppDatabase
    private lateinit var deletedTransaction : Transaction
    private lateinit var coordinator : CoordinatorLayout
    private lateinit var queryEditText : TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerview)
        addButton = findViewById(R.id.addButton)
        coordinator = findViewById(R.id.coordinator)

        transactions = arrayListOf()

        transactionAdapter = TransactionAdapter(transactions)
        linearLayoutManager = LinearLayoutManager(this)

        db = Room.databaseBuilder(this, AppDatabase::class.java, "transactions").build()


        recyclerView.apply {
            adapter = transactionAdapter
            layoutManager = linearLayoutManager
        }

        //swipe to remove
        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView : RecyclerView,
                viewHolder: ViewHolder,
                target : RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
                deleteTransaction(transactions[viewHolder.adapterPosition])
            }
        }

        val swipeHelper = ItemTouchHelper(itemTouchHelper)
        swipeHelper.attachToRecyclerView(recyclerView)

        addButton.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivity(intent)
        }

        coordinator.setOnClickListener{
            this.window.decorView.clearFocus()

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    override fun onStart() {
        super.onStart()
        // 1
        val searchTextObservable = searchOnTextChangeObservable()

        searchTextObservable
            // 1
            .subscribeOn(AndroidSchedulers.mainThread())
            // 2
            .observeOn(Schedulers.io())
            // 3
            .map { search(it) }
            // 4
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                showResult(it)
            }
    }

    override fun onResume() {
        super.onResume()
        fetchAll()
    }

    private fun fetchAll(){
        GlobalScope.launch {
            transactions = db.transactionDao().getAll()
            runOnUiThread {
                updateDashboard()
                transactionAdapter.setData(transactions)
            }
        }
    }

    private fun deleteTransaction(transaction: Transaction){
        deletedTransaction = transaction
        oldtransactions = transactions

        GlobalScope.launch {
            db.transactionDao().delete(transaction)

            transactions = transactions.filter { it.id != transaction.id }
            runOnUiThread {
                updateDashboard()
                transactionAdapter.setData(transactions)
                showSnackbar()
            }
        }
    }

    private fun showSnackbar(){
        val view = findViewById<View>(R.id.coordinator)
        val snackbar = Snackbar.make(view, "Transaksi Dihapus", Snackbar.LENGTH_LONG)
        snackbar.setAction("Undo"){
            undoDelete()
        }
            .setActionTextColor(ContextCompat.getColor(this, R.color.red))
            .setTextColor(ContextCompat.getColor(this, R.color.white))
            .show()
    }

    private fun undoDelete(){
        GlobalScope.launch {
            db.transactionDao().insert(deletedTransaction)

            transactions = oldtransactions

            runOnUiThread {
                transactionAdapter.setData(transactions)
                updateDashboard()
            }
        }
    }

    private fun updateDashboard(){
        val totalAmount = transactions.map{it.amount}.sum()
        val budgetAmount = transactions.filter{it.amount>0}.map{it.amount}.sum()
        val expenseAmount = totalAmount-budgetAmount
        balance = findViewById(R.id.balance)
        budget = findViewById(R.id.budget)
        expense = findViewById(R.id.expense)

        balance.text = "${rupiahFormats(totalAmount)}"
        budget.text = "${rupiahFormats(budgetAmount)}"
        expense.text = "${rupiahFormats(Math.abs(expenseAmount))}"
    }

    private fun search(query : String): List<Transaction>{
        return db.transactionDao().findTransaction("%$query%")
    }

    private fun searchOnTextChangeObservable(): Observable<String> {
//        searchButton = findViewById(R.id.searchButton)
        queryEditText = findViewById(R.id.queryEditText)
        // 2
        return Observable.create { emitter ->
            // 3
//            searchButton.setOnClickListener {
//                // 4
//                emitter.onNext(queryEditText.text.toString())
//            }

            //baru
            queryEditText.addTextChangedListener {
                emitter.onNext(queryEditText.text.toString())
            }
            //baru

            // 5
//            emitter.setCancellable {
//                // 6
//                searchButton.setOnClickListener(null)
//            }
        }
    }

    protected fun showResult(result: List<Transaction>) {
//        if (result.isEmpty()) {
//            Toast.makeText(this, "Tidak ada data", Toast.LENGTH_SHORT).show()
//        }
        transactionAdapter.setData(result)
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
}