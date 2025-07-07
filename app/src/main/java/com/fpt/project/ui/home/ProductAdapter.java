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
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> products;
    private OnProductClickListener onProductClickListener;
    private OnAddToCartClickListener onAddToCartClickListener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }
    
    public interface OnAddToCartClickListener {
        void onAddToCartClick(Product product);
    }

    public ProductAdapter(List<Product> products, OnProductClickListener productListener, OnAddToCartClickListener cartListener) {
        this.products = products;
        this.onProductClickListener = productListener;
        this.onAddToCartClickListener = cartListener;
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
        holder.bind(product, onProductClickListener, onAddToCartClickListener);
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
        private MaterialButton btnAddToCart;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductDescription = itemView.findViewById(R.id.tvProductDescription);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }

        public void bind(Product product, OnProductClickListener productListener, OnAddToCartClickListener cartListener) {
            tvProductName.setText(product.getName());
            tvProductPrice.setText(String.format("$%.2f", product.getPrice()));
            tvProductDescription.setText(product.getDescription());
            
            // Load image from ProductImage array or use placeholder
            String imageUrl = product.getPrimaryImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                // TODO: Load image using Glide library
                // For now, use placeholder
                ivProduct.setImageResource(android.R.drawable.ic_menu_gallery);
            } else {
                ivProduct.setImageResource(android.R.drawable.ic_menu_gallery);
            }
            
            // Product click - navigate to detail
            itemView.setOnClickListener(v -> {
                if (productListener != null) {
                    productListener.onProductClick(product);
                }
            });
            
            // Add to cart button click
            btnAddToCart.setOnClickListener(v -> {
                if (cartListener != null) {
                    cartListener.onAddToCartClick(product);
                }
            });
            
            // Update button state based on stock
            updateAddToCartButton(product);
        }
        
        private void updateAddToCartButton(Product product) {
            if (product.isActive() && product.getStockQuantity() > 0) {
                btnAddToCart.setEnabled(true);
                btnAddToCart.setText("Add to Cart");
            } else {
                btnAddToCart.setEnabled(false);
                btnAddToCart.setText(product.isActive() ? "Out of Stock" : "Unavailable");
            }
        }
    }
}
