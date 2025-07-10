package com.fpt.project.ui.category;

import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
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
        private ImageView ivCategoryIcon;
        private TextView tvCategoryName;
        private TextView tvCategoryDescription;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCategoryIcon = itemView.findViewById(R.id.ivCategoryIcon);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvCategoryDescription = itemView.findViewById(R.id.tvCategoryDescription);

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onCategoryClick(categoryList.get(getAdapterPosition()));
                }
            });
        }

        public void bind(Category category) {
            tvCategoryName.setText(category.getName());
            
            // Set category description
            String description = getCategoryDescription(category.getName());
            tvCategoryDescription.setText(description);

            // Load image from URL if available
            if (category.getImageUrl() != null && !category.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                    .load(category.getImageUrl())
                    .placeholder(getCategoryIcon(category.getName()))
                    .error(getCategoryIcon(category.getName()))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivCategoryIcon);
                
                // Set white icon color for contrast with gradient
                ivCategoryIcon.setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.white));
            } else {
                // Use specific icons based on category name
                int iconRes = getCategoryIcon(category.getName());
                
                ivCategoryIcon.setImageResource(iconRes);
                ivCategoryIcon.setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.white));
            }
            
            // Set gradient background based on category
            setGradientBackground(category.getName());
        }

        private void setGradientBackground(String categoryName) {
            int[] colors = getCategoryColors(categoryName);
            
            GradientDrawable gradient = new GradientDrawable();
            gradient.setShape(GradientDrawable.OVAL);
            gradient.setColors(colors);
            gradient.setGradientType(GradientDrawable.LINEAR_GRADIENT);
            gradient.setOrientation(GradientDrawable.Orientation.TL_BR);
            
            // Apply gradient to the icon container background
            View iconContainer = (View) ivCategoryIcon.getParent();
            if (iconContainer instanceof ViewGroup) {
                ViewGroup containerGroup = (ViewGroup) iconContainer;
                if (containerGroup.getChildCount() > 0) {
                    View gradientView = containerGroup.getChildAt(0);
                    if (gradientView != null) {
                        gradientView.setBackground(gradient);
                    }
                }
                // Fallback: set on the container itself
                if (containerGroup.getChildCount() == 0) {
                    iconContainer.setBackground(gradient);
                }
            }
        }

        private String getCategoryDescription(String categoryName) {
            if (categoryName == null) return "Tap to explore";
            
            String name = categoryName.toLowerCase();
            
            if (name.contains("vitamin") || name.contains("multivitamin")) {
                return "Essential vitamins";
            } else if (name.contains("herbal") || name.contains("supplement")) {
                return "Natural supplements";
            } else if (name.contains("omega") || name.contains("fish")) {
                return "Omega fatty acids";
            } else if (name.contains("workout") || name.contains("pre-workout") || name.contains("booster")) {
                return "Workout boosters";
            } else if (name.contains("protein") || name.contains("powder")) {
                return "Protein powders";
            } else {
                return "Tap to explore";
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

        private int[] getCategoryColors(String categoryName) {
            if (categoryName == null) return new int[]{0xFF2196F3, 0xFF64B5F6};
            
            String name = categoryName.toLowerCase();
            
            if (name.contains("vitamin") || name.contains("multivitamin")) {
                return new int[]{0xFF2196F3, 0xFF64B5F6}; // Blue gradient
            } else if (name.contains("herbal") || name.contains("supplement")) {
                return new int[]{0xFF4CAF50, 0xFF81C784}; // Green gradient
            } else if (name.contains("omega") || name.contains("fish")) {
                return new int[]{0xFFFF9800, 0xFFFFB74D}; // Orange gradient
            } else if (name.contains("workout") || name.contains("pre-workout") || name.contains("booster")) {
                return new int[]{0xFFF44336, 0xFFE57373}; // Red gradient
            } else if (name.contains("protein") || name.contains("powder")) {
                return new int[]{0xFF9C27B0, 0xFFBA68C8}; // Purple gradient
            } else {
                // Fallback gradients based on hash for consistency
                int[][] gradients = {
                    {0xFF2196F3, 0xFF64B5F6}, // Blue
                    {0xFF4CAF50, 0xFF81C784}, // Green  
                    {0xFFFF9800, 0xFFFFB74D}, // Orange
                    {0xFFE91E63, 0xFFF06292}, // Pink
                    {0xFF9C27B0, 0xFFBA68C8}, // Purple
                    {0xFF00BCD4, 0xFF4DD0E1}, // Cyan
                    {0xFFFF5722, 0xFFFF8A65}, // Deep Orange
                    {0xFF795548, 0xFFA1887F}  // Brown
                };
                
                int colorIndex = Math.abs(categoryName.hashCode()) % gradients.length;
                return gradients[colorIndex];
            }
        }
    }
} 