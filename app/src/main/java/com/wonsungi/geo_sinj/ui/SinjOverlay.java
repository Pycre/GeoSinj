package com.wonsungi.geo_sinj.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.RectF;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.bumptech.glide.Glide;
import com.wonsungi.geo_sinj.databinding.ItemSinjBinding;
import com.wonsungi.geo_sinj.model.SinjLocation;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SinjOverlay extends Overlay {

    private final Map<String, android.graphics.RectF> touchAreas = new HashMap<>();
    private final Map<String, SinjViewHolder> markerCache = new HashMap<>();
    private final List<SinjLocation> locations = new ArrayList<>();
    private final LayoutInflater inflater;
    private final OnSinjClickListener listener;

    public SinjOverlay(Context context, OnSinjClickListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    public void submitLocations(List<SinjLocation> newLocations) {
        locations.clear();

        if (newLocations != null) {
            locations.addAll(newLocations);
        }

        markerCache.keySet().removeIf(key -> locations.stream().noneMatch(location -> location.getId().equals(key)));
    }

    private SinjViewHolder getViewHolder(SinjLocation sinj) {
        SinjViewHolder holder = markerCache.get(sinj.getId());
        if (holder != null) return holder;

        ItemSinjBinding binding = ItemSinjBinding.inflate(inflater);
        holder = new SinjViewHolder(binding, listener);
        holder.bind(sinj);

        markerCache.put(sinj.getId(), holder);

        return holder;
    }

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        if (shadow) return;

        for (SinjLocation sinj : locations) {
            GeoPoint point = new GeoPoint(sinj.getLat(), sinj.getLon());
            Point screenPoint = mapView.getProjection().toPixels(point, null);

            SinjViewHolder holder = getViewHolder(sinj);
            holder.draw(screenPoint, canvas);
            touchAreas.put(sinj.getId(), holder.getRectF(screenPoint));
        }
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {
        for (SinjLocation sinj : locations) {
            RectF rect = touchAreas.get(sinj.getId());

            if (rect != null && rect.contains(e.getX(), e.getY())) {
                if (listener != null) {
                    listener.onSinjClick(sinj);
                }
                return true;
            }
        }

        listener.onSinjClick(null);
        return true;
    }

    public static class SinjViewHolder {

        public final ItemSinjBinding binding;
        public final OnSinjClickListener listener;

        public SinjViewHolder(ItemSinjBinding binding, OnSinjClickListener listener) {
            this.binding = binding;
            this.listener = listener;
        }

        public RectF getRectF(Point screenPoint) {
            float left = screenPoint.x - binding.getRoot().getMeasuredWidth() / 2f;
            float top = screenPoint.y - binding.getRoot().getMeasuredHeight();

            return new RectF(left, top, left + binding.getRoot().getMeasuredWidth(), top + binding.getRoot().getMeasuredHeight());
        }

        public void draw(Point screenPoint, Canvas canvas) {
            int width = binding.getRoot().getMeasuredWidth();
            int height = binding.getRoot().getMeasuredHeight();
            binding.getRoot().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            binding.getRoot().layout(0, 0, width, height);

            canvas.save();
            canvas.translate(screenPoint.x - width / 2f, screenPoint.y - height);
            binding.getRoot().draw(canvas);
            canvas.restore();
        }

        public void bind(SinjLocation sinj) {
            binding.getRoot().setOnClickListener(view -> listener.onSinjClick(sinj));

            Glide.with(binding.ivSinj)
                    .load(sinj.getAvatarUrl())
                    .circleCrop().into(binding.ivSinj);
        }
    }

    public interface OnSinjClickListener {
        void onSinjClick(SinjLocation sinj);
    }
}
