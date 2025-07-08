package com.fpt.project.ui.category;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.fpt.project.R;
import com.fpt.project.data.model.Category;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categoryList;
    private OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public CategoryAdapter(List<Category> categoryList, OnCategoryClickListener listener) {
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        return categoryList != null ? categoryList.size() : 0;
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivCategoryImage;
        private TextView tvCategoryName;
        private TextView tvCategoryDescription;
        private TextView tvProductCount;
        private TextView tvCategoryBadge;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCategoryImage = itemView.findViewById(R.id.ivCategoryImage);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvCategoryDescription = itemView.findViewById(R.id.tvCategoryDescription);
            tvProductCount = itemView.findViewById(R.id.tvProductCount);
            tvCategoryBadge = itemView.findViewById(R.id.tvCategoryBadge);

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onCategoryClick(categoryList.get(getAdapterPosition()));
                }
            });
        }

        public void bind(Category category) {
            tvCategoryName.setText(category.getName());
            
            // Set description
            if (category.getDescription() != null && !category.getDescription().isEmpty()) {
                tvCategoryDescription.setText(category.getDescription());
                tvCategoryDescription.setVisibility(View.VISIBLE);
            } else {
                // Set default descriptions for better UX
                String defaultDescription = getDefaultDescription(category.getName());
                if (defaultDescription != null) {
                    tvCategoryDescription.setText(defaultDescription);
                    tvCategoryDescription.setVisibility(View.VISIBLE);
                } else {
                    tvCategoryDescription.setVisibility(View.GONE);
                }
            }

            // Load image from URL if available
            if (category.getImageUrl() != null && !category.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                    .load(category.getImageUrl())
                    .placeholder(getCategoryIcon(category.getName()))
                    .error(getCategoryIcon(category.getName()))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .into(ivCategoryImage);
                
                // Clear any color filter when loading actual image
                ivCategoryImage.setColorFilter(null);
            } else {
                // Use specific icons based on category name
                int iconRes = getCategoryIcon(category.getName());
                int iconColor = getCategoryColor(category.getName());
                
                ivCategoryImage.setImageResource(iconRes);
                
                // Set the icon color directly instead of using color filter
                ivCategoryImage.setColorFilter(iconColor);
                
                // Find and set background color for the parent FrameLayout container
                if (ivCategoryImage.getParent() instanceof View) {
                    View iconFrame = (View) ivCategoryImage.getParent();
                    if (iconFrame.getParent() instanceof View) {
                        View iconContainer = (View) iconFrame.getParent();
                        // Set a lighter version of the color as background
                        int backgroundAlpha = 0x20; // 12.5% opacity
                        int backgroundColor = (iconColor & 0x00FFFFFF) | (backgroundAlpha << 24);
                        iconContainer.setBackgroundColor(backgroundColor);
                    }
                }
            }

            // Show/hide product count (optional - can be enabled later)
            if (tvProductCount != null) {
                tvProductCount.setVisibility(View.GONE);
            }

            // Show/hide featured badge (optional)
            if (tvCategoryBadge != null) {
                tvCategoryBadge.setVisibility(View.GONE);
            }
        }

        private int getCategoryIcon(String categoryName) {
            if (categoryName == null) return R.drawable.ic_category;
            
            String name = categoryName.toLowerCase();
            
            if (name.contains("vitamin") || name.contains("multivitamin")) {
                return R.drawable.ic_vitamins;
            } else if (name.contains("herbal") || name.contains("supplement")) {
                return R.drawable.ic_herbal;
            } else if (name.contains("omega") || name.contains("fish")) {
                return R.drawable.ic_omega;
            } else if (name.contains("workout") || name.contains("pre-workout") || name.contains("booster")) {
                return R.drawable.ic_workout;
            } else if (name.contains("protein") || name.contains("powder")) {
                return R.drawable.ic_protein;
            } else {
                return R.drawable.ic_category; // Default category icon
            }
        }

        private int getCategoryColor(String categoryName) {
            if (categoryName == null) return 0xFF2196F3;
            
            String name = categoryName.toLowerCase();
            
            if (name.contains("vitamin") || name.contains("multivitamin")) {
                return 0xFF2196F3; // Blue
            } else if (name.contains("herbal") || name.contains("supplement")) {
                return 0xFF4CAF50; // Green
            } else if (name.contains("omega") || name.contains("fish")) {
                return 0xFFFF9800; // Orange
            } else if (name.contains("workout") || name.contains("pre-workout") || name.contains("booster")) {
                return 0xFFF44336; // Red
            } else if (name.contains("protein") || name.contains("powder")) {
                return 0xFF9C27B0; // Purple
            } else {
                // Fallback to color based on hash for consistency
                int[] colors = {
                    0xFF2196F3, // Blue
                    0xFF4CAF50, // Green  
                    0xFFFF9800, // Orange
                    0xFFE91E63, // Pink
                    0xFF9C27B0, // Purple
                    0xFF00BCD4, // Cyan
                    0xFFFF5722, // Deep Orange
                    0xFF795548  // Brown
                };
                
                int colorIndex = Math.abs(categoryName.hashCode()) % colors.length;
                return colors[colorIndex];
            }
        }

        private String getDefaultDescription(String categoryName) {
            if (categoryName == null) return null;
            
            String name = categoryName.toLowerCase();
            
            if (name.contains("vitamin") || name.contains("multivitamin")) {
                return "Daily essential vitamins for overall health";
            } else if (name.contains("herbal")) {
                return "Natural remedies and herbal wellness solutions";
            } else if (name.contains("omega") || name.contains("fish")) {
                return "Heart and brain support from omega-rich oils";
            } else if (name.contains("workout") || name.contains("pre-workout")) {
                return "Energy-boosting supplements for workouts";
            } else if (name.contains("protein")) {
                return "Whey, plant-based, and casein protein supplements";
            } else if (name.contains("supplement")) {
                return "Vitamins and dietary supplements";
            } else {
                return null;
            }
        }
    }
} 