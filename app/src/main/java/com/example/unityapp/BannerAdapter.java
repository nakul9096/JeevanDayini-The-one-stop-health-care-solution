package com.example.unityapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;

public class BannerAdapter extends PagerAdapter {

    private int[] banners;
    private Context context;
    private LayoutInflater layoutInflater;

    public BannerAdapter(int[] banners, Context context) {
        this.banners = banners;
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return banners.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = layoutInflater.inflate(R.layout.banner_item, container, false);

        ImageView imageView = itemView.findViewById(R.id.bannerImageView);

        // Set a scaleType to make the image fit the curved frame
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // Use Glide to efficiently load and display the image
        Glide.with(context)
                .load(banners[position])
                .centerCrop() // Center-crop for Glide, matches the scaleType
                .into(imageView);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
