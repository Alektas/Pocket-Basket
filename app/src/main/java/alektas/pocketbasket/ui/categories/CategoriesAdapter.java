package alektas.pocketbasket.ui.categories;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import alektas.pocketbasket.R;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> {
    private List<Category> mCategories;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onClick(int position);
    }

    public CategoriesAdapter(List<Category> categories, OnItemClickListener listener) {
        setHasStableIds(true);
        mCategories = categories == null ? new ArrayList<>() : categories;
        mListener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_categories, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        int realPosition = position % mCategories.size();
        holder.bind(mCategories.get(realPosition));
        holder.itemView.setOnClickListener(v -> {
            if (mListener != null) mListener.onClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return mCategories.isEmpty() ? 0 : Integer.MAX_VALUE;
    }

    @Override
    public long getItemId(int position) {
        return mCategories.get(position % mCategories.size()).getId();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView icon;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.category_name);
            icon = itemView.findViewById(R.id.category_icon);
        }

        void bind(Category category) {
            name.setText(category.getName());
            icon.setImageResource(category.getIconRes());
        }
    }
}
