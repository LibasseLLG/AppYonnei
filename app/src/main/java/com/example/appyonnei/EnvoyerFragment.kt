package com.example.appyonnei

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.paypal.android.sdk.payments.PayPalConfiguration
import com.paypal.android.sdk.payments.PayPalPayment
import com.paypal.android.sdk.payments.PayPalService
import com.paypal.android.sdk.payments.PaymentActivity
import com.paypal.android.sdk.payments.PaymentConfirmation
import config.Config
import org.json.JSONException
import java.math.BigDecimal

class EnvoyerFragment : Fragment() {

    private val PAYPAL_REQUEST_CODE: Int = 7171

    // Configuration PayPal avec environnement sandbox et ID client
    private val config: PayPalConfiguration = PayPalConfiguration()
        .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
        .clientId(Config().PAYPAL_CLIENT_ID)

    // Bouton et champ de texte
    private lateinit var btnEnvoyer: Button
    private lateinit var edtAmount: EditText

    // Montant du paiement
    private var amount: String = ""


    //Cette méthode est appelée lorsque le fragment est détruit. Elle arrête le service PayPal.
    override fun onDestroy() {
        activity?.stopService(Intent(activity, PayPalService::class.java))
        super.onDestroy()
    }


    //Cette méthode est appelée pour créer et retourner la vue associée au fragment.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_envoyer, container, false)

        // Start Paypal Service
        val intent = Intent(activity, PayPalService::class.java)
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
        activity?.startService(intent)

        // Initialiser les vues (bouton et champ de texte)
        btnEnvoyer = view.findViewById(R.id.btnPayNow)
        edtAmount = view.findViewById(R.id.edtAmount)

        // Ajouter un listener au bouton "Payer maintenant"
        btnEnvoyer.setOnClickListener {
            processPayment()
        }

        // Retourner la vue du fragment
        return view
    }


    //Cette méthode est appelée lorsque l'utilisateur clique sur le bouton "Payer maintenant". Elle récupère le montant entré, crée un objet PayPalPayment, et démarre l'activité de paiement PayPal avec cet objet.


    private fun processPayment() {
        amount = edtAmount.text.toString()
        val payPalPayment = PayPalPayment(
            BigDecimal(amount),
            "USD",
            "Donate for Libasse",
            PayPalPayment.PAYMENT_INTENT_SALE
        )
        val intent = Intent(activity, PaymentActivity::class.java)
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment)
        startActivityForResult(intent, PAYPAL_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PAYPAL_REQUEST_CODE) {
            // Le paiement a été effectué avec succès
            if (resultCode == Activity.RESULT_OK) {
                val confirmation =
                    data?.getParcelableExtra<PaymentConfirmation>(PaymentActivity.EXTRA_RESULT_CONFIRMATION)
                if (confirmation != null) {
                    try {
                        val jsonString = confirmation.toJSONObject().toString(4)
                        // Démarrer l'activité pour afficher les détails du paiement
                        startActivity(
                            Intent(activity, PaymentDetails::class.java)
                                .putExtra("PaymentDetails", jsonString)
                                .putExtra("PaymentAmount", amount)
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // L'utilisateur a annulé le paiement
                Toast.makeText(activity, "Cancel", Toast.LENGTH_SHORT).show()
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                // Des informations invalides ont été fournies au paiement
                Toast.makeText(activity, "Invalid", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
