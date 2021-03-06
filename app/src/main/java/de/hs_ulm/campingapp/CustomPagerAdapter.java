package de.hs_ulm.campingapp;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Rene on 25.01.2018.
 */

public class CustomPagerAdapter extends PagerAdapter {
    Context mContext;
    LayoutInflater mLayoutInflater;
    ArrayList<String> imgPaths;
    public CustomPagerAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imgPaths = new ArrayList<String>();
    }
    public void add(String imgPath) {
        imgPaths.add(imgPath);
        this.notifyDataSetChanged();
    }
    @Override
    public int getCount()
    {
        return imgPaths.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ConstraintLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);
        ImageView imgV = itemView.findViewById(R.id.pagerImgView);
        ImageView marrowLeft = itemView.findViewById(R.id.arrowLeft);
        ImageView marrowRight = itemView.findViewById(R.id.arrowRight);
        new DownloadImageTask(imgV).execute(imgPaths.get(position));
        if((position > 0 && position < imgPaths.size()-1) || (imgPaths.size() < 2)) {
            marrowLeft.setVisibility(View.VISIBLE);
            marrowRight.setVisibility(View.VISIBLE);
        }
        if(position == 0) {
            marrowLeft.setVisibility(View.GONE);
        }
        if(position == imgPaths.size()-1) {
            marrowRight.setVisibility(View.GONE);
        }


        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ConstraintLayout) object);
    }
}
