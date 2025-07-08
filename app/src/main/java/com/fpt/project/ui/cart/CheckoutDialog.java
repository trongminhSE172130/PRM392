package com.fpt.project.ui.cart;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.fpt.project.R;
import com.fpt.project.data.model.request.CheckoutRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class CheckoutDialog extends Dialog {

    // Views
    private TextInputEditText etFullName;
    private TextInputEditText etPhone;
    private TextInputEditText etAddress;
    private TextInputEditText etCity;
    private TextInputEditText etPostalCode;
    private TextInputEditText etShippingNotes;
    private RadioGroup rgPaymentMethod;
    private TextInputEditText etOrderNotes;
    private MaterialButton btnCancel;
    private MaterialButton btnConfirmCheckout;

    // Callback interface
    public interface CheckoutDialogListener {
        void onCheckoutConfirmed(CheckoutRequest checkoutRequest);
        void onCheckoutCancelled();
    }

    private CheckoutDialogListener listener;

    public CheckoutDialog(@NonNull Context context) {
        super(context);
    }

    public CheckoutDialog(@NonNull Context context, CheckoutDialogListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_checkout);
        
        initViews();
        setupClickListeners();
        
        // Make dialog non-cancelable by touching outside
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    private void initViews() {
        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        etCity = findViewById(R.id.etCity);
        etPostalCode = findViewById(R.id.etPostalCode);
        etShippingNotes = findViewById(R.id.etShippingNotes);
        rgPaymentMethod = findViewById(R.id.rgPaymentMethod);
        etOrderNotes = findViewById(R.id.etOrderNotes);
        btnCancel = findViewById(R.id.btnCancel);
        btnConfirmCheckout = findViewById(R.id.btnConfirmCheckout);
    }

    private void setupClickListeners() {
        btnCancel.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCheckoutCancelled();
            }
            dismiss();
        });

        btnConfirmCheckout.setOnClickListener(v -> {
            if (validateInputs()) {
                CheckoutRequest checkoutRequest = createCheckoutRequest();
                if (listener != null) {
                    listener.onCheckoutConfirmed(checkoutRequest);
                }
                dismiss();
            }
        });
    }

    private boolean validateInputs() {
        // Validate full name
        if (TextUtils.isEmpty(etFullName.getText())) {
            etFullName.setError("Full name is required");
            etFullName.requestFocus();
            return false;
        }

        // Validate phone
        if (TextUtils.isEmpty(etPhone.getText())) {
            etPhone.setError("Phone number is required");
            etPhone.requestFocus();
            return false;
        }

        // Validate address
        if (TextUtils.isEmpty(etAddress.getText())) {
            etAddress.setError("Address is required");
            etAddress.requestFocus();
            return false;
        }

        // Validate city
        if (TextUtils.isEmpty(etCity.getText())) {
            etCity.setError("City is required");
            etCity.requestFocus();
            return false;
        }

        // Validate postal code
        if (TextUtils.isEmpty(etPostalCode.getText())) {
            etPostalCode.setError("Postal code is required");
            etPostalCode.requestFocus();
            return false;
        }

        return true;
    }

    private CheckoutRequest createCheckoutRequest() {
        // Create shipping address
        CheckoutRequest.ShippingAddress shippingAddress = new CheckoutRequest.ShippingAddress(
                etFullName.getText().toString().trim(),
                etPhone.getText().toString().trim(),
                etAddress.getText().toString().trim(),
                etCity.getText().toString().trim(),
                etPostalCode.getText().toString().trim(),
                etShippingNotes.getText().toString().trim()
        );

        // Get payment method
        String paymentMethod;
        int selectedPaymentId = rgPaymentMethod.getCheckedRadioButtonId();
        if (selectedPaymentId == R.id.rbVnpay) {
            paymentMethod = "vnpay";
        } else if (selectedPaymentId == R.id.rbCod) {
            paymentMethod = "cod";
        } else {
            paymentMethod = "vnpay"; // Default to VNPay
        }

        // Create checkout request
        return new CheckoutRequest(
                shippingAddress,
                paymentMethod,
                etOrderNotes.getText().toString().trim()
        );
    }

    public void setListener(CheckoutDialogListener listener) {
        this.listener = listener;
    }
} 