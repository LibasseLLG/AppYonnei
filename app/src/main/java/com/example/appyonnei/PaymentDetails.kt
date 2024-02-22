package com.example.appyonnei

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONException
import org.json.JSONObject

class PaymentDetails : AppCompatActivity() {



    lateinit var txId: TextView
    lateinit var txAmount: TextView
    lateinit var txStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_details)

        // Initialisation des TextViews
        txId = findViewById(R.id.txId)
        txAmount = findViewById(R.id.txAmount)
        txStatus = findViewById(R.id.txStatus)

        //Get Intent

        val intent = intent

        try {
            val jsonObject = JSONObject(intent.getStringExtra("PaymentDetails"))
            showDetails(jsonObject.getJSONObject("response"), intent.getStringExtra("PaymentAmount"))
        } catch (e: JSONException) {
            e.printStackTrace()
        }


    }

    private fun showDetails(jsonObject: JSONObject, paymentAmount: String?) {
        try {
            txId.text = jsonObject.getString("id")
            txStatus.text = jsonObject.getString("state")
            txAmount.text = String.format("$%s", paymentAmount)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

}

