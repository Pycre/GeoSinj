package com.wonsungi.geo_sinj.map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.RectF;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.bumptech.glide.Glide;
import com.wonsungi.geo_sinj.databinding.ItemSinjBinding;
import com.wonsungi.geo_sinj.databinding.ItemUserBinding;
import com.wonsungi.geo_sinj.location.LocationService;
import com.wonsungi.geo_sinj.model.SinjLocation;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapOverlay extends Overlay {

    private final LayoutInflater inflater;
    private final OnSinjClickListener listener;

    private final Map<String, ItemSinjBinding> sinjBindings = new HashMap<>();
    private final Map<String, RectF> touchAreas = new HashMap<>();

    private final ItemUserBinding userBinding;
    private List<SinjLocation> sinjLocations = List.of();

    public MapOverlay(Context context, OnSinjClickListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;

        userBinding = ItemUserBinding.inflate(inflater);
    }

    public void submitLocations(List<SinjLocation> locations) {
        sinjLocations = locations != null ? locations : List.of();
        sinjBindings.keySet().removeIf(key -> sinjLocations.stream().noneMatch(location -> location.id().equals(key)));
    }

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        if (shadow) return;

        touchAreas.clear();

        drawSinjs(canvas, mapView);
        drawUser(canvas, mapView);
    }

    private void drawSinjs(Canvas canvas, MapView mapView) {
        for (SinjLocation sinj : sinjLocations) {

            Point screenPoint = mapView.getProjection().toPixels(new GeoPoint(sinj.lat(), sinj.lon()), null);
            ItemSinjBinding binding = getOrCreateBinding(sinj);
            binding.getRoot().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

            int width = binding.getRoot().getMeasuredWidth();
            int height = binding.getRoot().getMeasuredHeight();

            binding.getRoot().layout(0, 0, width, height);

            canvas.save();
            canvas.translate(screenPoint.x - width / 2f, screenPoint.y - height);
            binding.getRoot().draw(canvas);
            canvas.restore();

            touchAreas.put(sinj.id(), new RectF(screenPoint.x - width / 2f, screenPoint.y - height, screenPoint.x + width / 2f, screenPoint.y));
        }
    }

    private void drawUser(Canvas canvas, MapView mapView) {
        Location location = LocationService.lastLocation;
        if (location == null) return;

        Point screenPoint = mapView.getProjection().toPixels(new GeoPoint(location.getLatitude(), location.getLongitude()), null);
        userBinding.getRoot().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        int width = userBinding.getRoot().getMeasuredWidth();
        int height = userBinding.getRoot().getMeasuredHeight();

        userBinding.getRoot().layout(0, 0, width, height);

        canvas.save();
        canvas.translate(screenPoint.x - width / 2f, screenPoint.y - height / 2f);
        userBinding.getRoot().draw(canvas);
        canvas.restore();
    }

    private ItemSinjBinding getOrCreateBinding(SinjLocation sinj) {
        ItemSinjBinding binding = sinjBindings.get(sinj.id());
        if (binding != null) return binding;

        binding = ItemSinjBinding.inflate(inflater);
        Glide.with(binding.ivSinj).load(sinj.avatarUrl()).circleCrop().into(binding.ivSinj);
        sinjBindings.put(sinj.id(), binding);

        return binding;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {
        for (SinjLocation sinj : sinjLocations) {
            RectF rect = touchAreas.get(sinj.id());

            if (rect != null && rect.contains(e.getX(), e.getY())) {
                if (listener != null) listener.onSinjClick(sinj);
                return true;
            }
        }

        if (listener != null) listener.onSinjClick(null);

        return true;
    }

    public interface OnSinjClickListener {
        void onSinjClick(SinjLocation sinj);
    }
}
