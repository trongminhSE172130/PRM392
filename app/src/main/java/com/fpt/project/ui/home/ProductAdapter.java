package com.fpt.project.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fpt.project.R;
import com.fpt.project.data.model.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> products;
    private OnProductClickListener onProductClickListener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public ProductAdapter(List<Product> products, OnProductClickListener listener) {
        this.products = products;
        this.onProductClickListener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product, onProductClickListener);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProduct;
        private TextView tvProductName;
        private TextView tvProductPrice;
        private TextView tvProductDescription;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductDescription = itemView.findViewById(R.id.tvProductDescription);
        }

        public void bind(Product product, OnProductClickListener listener) {
            tvProductName.setText(product.getName());
            tvProductPrice.setText(String.format("$%.2f", product.getPrice()));
            tvProductDescription.setText(product.getDescription());
            
            // TODO: Load image using Glide
            // For now, use placeholder
            ivProduct.setImageResource(android.R.drawable.ic_menu_gallery);
            
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductClick(product);
                }
            });
        }
    }
}
