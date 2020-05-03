package com.imagevideoeditor.photoeditor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.imagevideoeditor.R;
import com.imagevideoeditor.Utils.InfinitePagerAdapter;
import com.imagevideoeditor.Utils.InfiniteViewPager;


/**
 * <p>
 * This ViewGroup will have the {@link BrushDrawingView} to draw paint on it with {@link ImageView}
 * which our source image
 * </p>
 *
 * @author <a href="https://github.com/burhanrashid52">Burhanuddin Rashid</a>
 * @version 0.1.1
 * @since 1/18/2018
 */

public class PhotoEditorView extends RelativeLayout {

    private static final String TAG = "PhotoEditorView";

    private FilterImageView mImgSource;
    private BrushDrawingView mBrushDrawingView;
    private ImageFilterView mImageFilterView;
    private InfiniteViewPager viewPager;
    private static final int imgSrcId = 1, brushSrcId = 2, glFilterId = 3, viewPagerId = 4;

    public PhotoEditorView(Context context) {
        super(context);
        init(null);
    }

    public PhotoEditorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PhotoEditorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PhotoEditorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    @SuppressLint("Recycle")
    private void init(@Nullable AttributeSet attrs) {
        //Setup image attributes
        mImgSource = new FilterImageView(getContext());
        mImgSource.setId(imgSrcId);
        mImgSource.setAdjustViewBounds(true);
        LayoutParams imgSrcParam = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imgSrcParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PhotoEditorView);
            Drawable imgSrcDrawable = a.getDrawable(R.styleable.PhotoEditorView_photo_src);
            if (imgSrcDrawable != null) {
                mImgSource.setImageDrawable(imgSrcDrawable);
            }
        }

        //Setup brush view
        mBrushDrawingView = new BrushDrawingView(getContext());
        mBrushDrawingView.setVisibility(GONE);
        mBrushDrawingView.setId(brushSrcId);
        //Align brush to the size of image view
        LayoutParams brushParam = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        brushParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        brushParam.addRule(RelativeLayout.ALIGN_TOP, imgSrcId);
        brushParam.addRule(RelativeLayout.ALIGN_BOTTOM, imgSrcId);

        //Setup GLSurface attributes
        mImageFilterView = new ImageFilterView(getContext());
        mImageFilterView.setId(glFilterId);
        mImageFilterView.setVisibility(GONE);

        //Align brush to the size of image view
        LayoutParams imgFilterParam = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imgFilterParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        imgFilterParam.addRule(RelativeLayout.ALIGN_TOP, imgSrcId);
        imgFilterParam.addRule(RelativeLayout.ALIGN_BOTTOM, imgSrcId);

        mImgSource.setOnImageChangedListener(new FilterImageView.OnImageChangedListener() {
            @Override
            public void onBitmapLoaded(@Nullable Bitmap sourceBitmap) {
                mImageFilterView.setFilterEffect(PhotoFilter.NONE);
                mImageFilterView.setSourceBitmap(sourceBitmap);
                Log.d(TAG, "onBitmapLoaded() called with: sourceBitmap = [" + sourceBitmap + "]");
            }
        });


        //setUp View Pager

        viewPager = new InfiniteViewPager(getContext());
        viewPager.setId(viewPagerId);
        viewPager.setVisibility(View.VISIBLE);
        InfinitePagerAdapter infinitePagerAdapter = new InfinitePagerAdapter(new MyViewPagerAdapter(getContext()));
        viewPager.setAdapter(infinitePagerAdapter);
        LayoutParams viewPagerParam = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        viewPagerParam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        viewPagerParam.addRule(RelativeLayout.ALIGN_TOP, viewPagerId);
        viewPagerParam.addRule(RelativeLayout.ALIGN_BOTTOM, viewPagerId);


        //Add image source
        addView(mImgSource, imgSrcParam);

        // Add Viewpager
        addView(viewPager, viewPagerParam);

        //Add Gl FilterView
        addView(mImageFilterView, imgFilterParam);

        //Add brush view
        addView(mBrushDrawingView, brushParam);


    }


    /**
     * Source image which you want to edit
     *
     * @return source ImageView
     */
    public ImageView getSource() {
        return mImgSource;
    }

    BrushDrawingView getBrushDrawingView() {
        return mBrushDrawingView;
    }


    void saveFilter(@NonNull final OnSaveBitmap onSaveBitmap) {
        if (mImageFilterView.getVisibility() == VISIBLE) {
            mImageFilterView.saveBitmap(new OnSaveBitmap() {
                @Override
                public void onBitmapReady(final Bitmap saveBitmap) {
                    Log.e(TAG, "saveFilter: " + saveBitmap);
                    mImgSource.setImageBitmap(saveBitmap);
                    mImageFilterView.setVisibility(GONE);
                    onSaveBitmap.onBitmapReady(saveBitmap);
                }

                @Override
                public void onFailure(Exception e) {
                    onSaveBitmap.onFailure(e);
                }
            });
        } else {
            onSaveBitmap.onBitmapReady(mImgSource.getBitmap());
        }


    }

    public class MyViewPagerAdapter extends PagerAdapter {


        public MyViewPagerAdapter(Context context) {

        }

        @Override
        public Object instantiateItem(@NonNull ViewGroup collection, int position) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.item_slider_filter, collection, false);
            ImageView viewBG = layout.findViewById(R.id.viewBG);
            ImageView ivBottom = layout.findViewById(R.id.ivBottom);
            ivBottom.setVisibility(GONE);

            if (position == 0) {
                viewBG.setBackgroundColor(Color.parseColor("#10000000"));
                viewBG.setVisibility(VISIBLE);
                ivBottom.setVisibility(GONE);
            } else if (position == 1) {
                viewBG.setBackgroundColor(Color.parseColor("#15FF5733"));
                viewBG.setVisibility(VISIBLE);
                ivBottom.setVisibility(GONE);
            } else if (position == 2) {
                viewBG.setVisibility(GONE);
                ivBottom.setVisibility(VISIBLE);
                ivBottom.setImageDrawable(getResources().getDrawable(R.drawable.gg));

            } else if (position == 3) {
                viewBG.setVisibility(VISIBLE);
                ivBottom.setImageDrawable(getResources().getDrawable(R.drawable.hh));
                ivBottom.setVisibility(VISIBLE);

            }else if (position == 4) {
                viewBG.setVisibility(VISIBLE);
                ivBottom.setImageDrawable(getResources().getDrawable(R.drawable.ee));
                ivBottom.setVisibility(VISIBLE);

            }
            collection.addView(layout);

            return layout;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object view) {
            container.removeView((View) view);
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    }

    void setFilterEffect(PhotoFilter filterType) {
        mImageFilterView.setVisibility(VISIBLE);
        mImageFilterView.setSourceBitmap(mImgSource.getBitmap());
        mImageFilterView.setFilterEffect(filterType);
    }

    void setFilterEffect(CustomEffect customEffect) {
        mImageFilterView.setVisibility(VISIBLE);
        mImageFilterView.setSourceBitmap(mImgSource.getBitmap());
        mImageFilterView.setFilterEffect(customEffect);
    }
}
