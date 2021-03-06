package org.tndata.android.compass.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Action;
import org.tndata.android.compass.util.ImageLoader;

public class ActionListView extends LinearLayout {
    private ImageView mIconImageView;
    private TextView mTitleTextView;
    private Action mAction;
    private Context mContext;

    ImageLoader mImageLoader;

    public ActionListView(Context context, ImageLoader loader) {
        this(context, null, 0, loader);
    }

    public ActionListView(Context context, AttributeSet attrs, ImageLoader loader) {
        this(context, attrs, 0, loader);
    }

    public ActionListView(Context context, AttributeSet attrs, int defStyle, ImageLoader loader) {
        super(context, attrs, defStyle);
        mImageLoader = loader;
        initViews(context, attrs);
    }

    private void initViews(Context context, AttributeSet attrs) {
        mContext = context;
        View view = inflate(context, R.layout.view_behavior_item, this);

        mIconImageView = (ImageView) view.findViewById(R.id.view_behavior_icon_imageview);
        mTitleTextView = (TextView) view
                .findViewById(R.id.view_behavior_textview);
        if (mAction != null) {
            updateUi();
        }
    }

    public void setAction(Action action) {
        mAction = action;
        if (mTitleTextView != null) {
            updateUi();
        }
    }

    private void updateUi() {
        try {
            mTitleTextView.setText(mAction.getTitle());
            if (mAction.getIconUrl() != null
                    && !mAction.getIconUrl().isEmpty()) {
                mImageLoader.loadBitmap(mIconImageView,
                        mAction.getIconUrl(), false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Action getAction() {
        return mAction;
    }
}

