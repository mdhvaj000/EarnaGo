package com.example.data.network

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.util.Locale

object UpiPaymentManager {

    /**
     * Creates a standard UPI Deep Link URI string for Indian Payment Apps
     * (PhonePe, Google Pay, Paytm, BHIM, Mobikwik, CRED)
     */
    fun buildUpiUri(
        payeeVpa: String = "earnago@upi",
        payeeName: String = "EarnaGo Enterprise India",
        transactionId: String,
        transactionRef: String,
        note: String,
        amountInInr: Double
    ): Uri {
        val formattedAmount = String.format(Locale.US, "%.2f", amountInInr)
        val uriString = Uri.Builder()
            .scheme("upi")
            .authority("pay")
            .appendQueryParameter("pa", payeeVpa)
            .appendQueryParameter("pn", payeeName)
            .appendQueryParameter("mc", "5311") // Merchant Category Code (General Retail)
            .appendQueryParameter("tid", transactionId)
            .appendQueryParameter("tr", transactionRef)
            .appendQueryParameter("tn", note)
            .appendQueryParameter("am", formattedAmount)
            .appendQueryParameter("cu", "INR")
            .build()
            .toString()

        return Uri.parse(uriString)
    }

    /**
     * Launches UPI Intent Chooser to open Google Pay, PhonePe, Paytm, or BHIM
     */
    fun launchUpiPayment(
        context: Context,
        payeeVpa: String = "earnago@upi",
        payeeName: String = "EarnaGo Enterprise India",
        transactionId: String,
        transactionRef: String,
        note: String,
        amountInInr: Double,
        onError: (String) -> Unit
    ) {
        try {
            val upiUri = buildUpiUri(payeeVpa, payeeName, transactionId, transactionRef, note, amountInInr)
            val upiIntent = Intent(Intent.ACTION_VIEW, upiUri)
            val chooser = Intent.createChooser(upiIntent, "Pay securely via UPI (GPay, PhonePe, Paytm)")
            context.startActivity(chooser)
        } catch (e: Exception) {
            onError("Unable to launch UPI app: ${e.localizedMessage ?: "No supported UPI payment app found"}")
        }
    }

    /**
     * Tax Calculation for Indian GST (Goods & Services Tax)
     */
    data class GstBreakdown(
        val baseAmount: Double,
        val gstPercentage: Double,
        val cgstAmount: Double,
        val sgstAmount: Double,
        val totalTax: Double,
        val totalInclusiveAmount: Double
    )

    fun calculateGst(inclusivePrice: Double, gstRate: Double = 18.0): GstBreakdown {
        val baseAmount = inclusivePrice / (1.0 + (gstRate / 100.0))
        val totalTax = inclusivePrice - baseAmount
        val cgst = totalTax / 2.0
        val sgst = totalTax / 2.0
        return GstBreakdown(
            baseAmount = baseAmount,
            gstPercentage = gstRate,
            cgstAmount = cgst,
            sgstAmount = sgst,
            totalTax = totalTax,
            totalInclusiveAmount = inclusivePrice
        )
    }
}
