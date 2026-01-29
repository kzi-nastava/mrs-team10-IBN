package com.example.ubercorp.adapters;

import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ubercorp.R;
import com.example.ubercorp.databinding.ItemRouteCardBinding;
import com.example.ubercorp.dto.FavoriteRouteDTO;
import com.example.ubercorp.dto.GetCoordinateDTO;

import java.util.List;
import java.util.Objects;

public class FavoriteRoutesAdapter extends ListAdapter<FavoriteRouteDTO, FavoriteRoutesAdapter.RouteViewHolder> {

    private boolean isEditMode;
    private final OnRouteClickListener onRouteClickListener;
    private final OnRouteRemoveListener onRouteRemoveListener;

    public interface OnRouteClickListener {
        void onRouteClick(FavoriteRouteDTO route);
    }

    public interface OnRouteRemoveListener {
        void onRouteRemove(FavoriteRouteDTO route);
    }

    public FavoriteRoutesAdapter(boolean isEditMode,
                                 OnRouteClickListener onRouteClickListener,
                                 OnRouteRemoveListener onRouteRemoveListener) {
        super(new RouteDiffCallback());
        this.isEditMode = isEditMode;
        this.onRouteClickListener = onRouteClickListener;
        this.onRouteRemoveListener = onRouteRemoveListener;
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRouteCardBinding binding = ItemRouteCardBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new RouteViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public void setEditMode(boolean editMode) {
        this.isEditMode = editMode;
        notifyDataSetChanged();
    }

    class RouteViewHolder extends RecyclerView.ViewHolder {
        private final ItemRouteCardBinding binding;

        RouteViewHolder(ItemRouteCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(FavoriteRouteDTO route) {
            SpannableStringBuilder routePath = buildRoutePath(route);
            binding.tvRoutePath.setText(routePath);

            if (isEditMode) {
                binding.ivSelectIcon.setVisibility(View.GONE);
                binding.flRemoveIcon.setVisibility(View.VISIBLE);
                binding.flRemoveIcon.setOnClickListener(v ->
                        onRouteRemoveListener.onRouteRemove(route)
                );
            } else {
                binding.ivSelectIcon.setVisibility(View.VISIBLE);
                binding.flRemoveIcon.setVisibility(View.GONE);
            }

            binding.cvRouteCard.setOnClickListener(v -> {
                if (!isEditMode) {
                    onRouteClickListener.onRouteClick(route);
                }
            });
        }

        private SpannableStringBuilder buildRoutePath(FavoriteRouteDTO route) {
            SpannableStringBuilder builder = new SpannableStringBuilder();

            int textColor = ContextCompat.getColor(binding.getRoot().getContext(), R.color.route_text);
            int arrowColor = ContextCompat.getColor(binding.getRoot().getContext(), R.color.route_arrow);

            List<GetCoordinateDTO> stations = route.getRouteDTO().getStations();

            builder.append("📍 ");

            int fromStart = builder.length();
            builder.append(cleanAddress(stations.get(0).getAddress()));
            builder.setSpan(new ForegroundColorSpan(textColor), fromStart, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setSpan(new StyleSpan(Typeface.BOLD), fromStart, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            for (int i = 1; i < stations.size() - 1; i++) {
                int arrowStart = builder.length();
                builder.append(" → ");
                builder.setSpan(new ForegroundColorSpan(arrowColor), arrowStart, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                int stopStart = builder.length();
                builder.append(cleanAddress(stations.get(i).getAddress()));
                builder.setSpan(new ForegroundColorSpan(textColor), stopStart, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new StyleSpan(Typeface.BOLD), stopStart, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            int finalArrowStart = builder.length();
            builder.append(" → ");
            builder.setSpan(new ForegroundColorSpan(arrowColor), finalArrowStart, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            int toStart = builder.length();
            builder.append(cleanAddress(stations.get(stations.size() - 1).getAddress()));
            builder.setSpan(new ForegroundColorSpan(textColor), toStart, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setSpan(new StyleSpan(Typeface.BOLD), toStart, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            return builder;
        }
    }
    private String cleanAddress(String address) {
        if (address == null) return "";
        return address
                .replaceAll(",?\\s*Novi Sad(,?\\s*Serbia)?", "")
                .replaceAll(",?\\s*Serbia", "")
                .trim();
    }


    static class RouteDiffCallback extends DiffUtil.ItemCallback<FavoriteRouteDTO> {
        @Override
        public boolean areItemsTheSame(@NonNull FavoriteRouteDTO oldItem, @NonNull FavoriteRouteDTO newItem) {
            return Objects.equals(oldItem.getId(), newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull FavoriteRouteDTO oldItem, @NonNull FavoriteRouteDTO newItem) {
            return oldItem.equals(newItem);
        }
    }
}
