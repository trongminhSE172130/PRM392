package com.fpt.project.ui.cart;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.fpt.project.MainActivity;
import com.fpt.project.R;

public class PaymentResultActivity extends AppCompatActivity {

    private static final String TAG = "PaymentResultActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Handle the deep link intent
        Intent intent = getIntent();
        handlePaymentResult(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handlePaymentResult(intent);
    }

    private void handlePaymentResult(Intent intent) {
        Uri data = intent.getData();
        
        if (data != null) {
            Log.d(TAG, "Received payment result URL: " + data.toString());
            
            // Parse payment result parameters
            String responseCode = data.getQueryParameter("vnp_ResponseCode");
            String transactionStatus = data.getQueryParameter("vnp_TransactionStatus");
            String orderId = data.getQueryParameter("order_id"); // From our custom parameter
            String vnpTxnRef = data.getQueryParameter("vnp_TxnRef"); // From VNPay (backend order ID)
            String amount = data.getQueryParameter("vnp_Amount");
            String transactionNo = data.getQueryParameter("vnp_TransactionNo");
            
            // Use vnp_TxnRef if order_id is not available (backend might use this)
            if (orderId == null && vnpTxnRef != null) {
                orderId = vnpTxnRef;
            }
            
            Log.d(TAG, "Payment result - Response Code: " + responseCode + 
                  ", Order ID: " + orderId + ", Amount: " + amount);
            
            if ("00".equals(responseCode)) {
                // Payment successful
                showPaymentSuccess(orderId, amount, transactionNo);
            } else {
                // Payment failed or cancelled
                showPaymentFailure(responseCode, orderId);
            }
        } else {
            Log.e(TAG, "No data received in payment result intent");
            showPaymentFailure("no_data", null);
        }
    }

    private void showPaymentSuccess(String orderId, String amount, String transactionNo) {
        Log.d(TAG, "Payment successful for order: " + orderId);
        
        // Format amount (VNPay sends amount in smallest unit, e.g., cents)
        String formattedAmount = "Unknown";
        if (amount != null) {
            try {
                double amountValue = Double.parseDouble(amount) / 100.0; // Convert from cents to dollars
                formattedAmount = String.format("$%.2f", amountValue);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Error parsing amount: " + amount, e);
            }
        }
        
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Payment Successful!")
                .setMessage("Your payment has been processed successfully!\n\n" +
                           "Order ID: #" + orderId + "\n" +
                           "Amount: " + formattedAmount + "\n" +
                           (transactionNo != null ? "Transaction: " + transactionNo + "\n" : "") +
                           "\nYou will receive a confirmation email shortly.")
                .setPositiveButton("Continue Shopping", (dialog, which) -> {
                    navigateToHome();
                })
                .setCancelable(false)
                .show();
    }

    private void showPaymentFailure(String errorCode, String orderId) {
        Log.d(TAG, "Payment failed - Error Code: " + errorCode + ", Order: " + orderId);
        
        String errorMessage = getPaymentErrorMessage(errorCode);
        String orderInfo = orderId != null ? "\nOrder ID: #" + orderId : "";
        
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Payment Failed")
                .setMessage("Payment was not successful." + orderInfo + "\n\n" +
                           "Error: " + errorMessage + "\n\n" +
                           "Please try again or contact support if the problem persists.")
                .setPositiveButton("Try Again", (dialog, which) -> {
                    // Navigate back to cart to retry
                    navigateToCart();
                })
                .setNegativeButton("OK", (dialog, which) -> {
                    navigateToHome();
                })
                .setCancelable(false)
                .show();
    }

    private String getPaymentErrorMessage(String errorCode) {
        if (errorCode == null) return "Unknown error";
        
        switch (errorCode) {
            case "24": return "Transaction cancelled by user";
            case "11": return "Transaction timeout";
            case "12": return "Account locked";
            case "13": return "Incorrect OTP";
            case "51": return "Insufficient funds";
            case "65": return "Transaction limit exceeded";
            case "75": return "Payment gateway is under maintenance";
            case "79": return "Transaction limit exceeded for the day";
            case "no_data": return "No payment data received";
            default: return "Payment processing error (Code: " + errorCode + ")";
        }
    }

    private void navigateToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("navigate_to", "home");
        startActivity(intent);
        finish();
    }

    private void navigateToCart() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("navigate_to", "cart");
        startActivity(intent);
        finish();
    }
} 