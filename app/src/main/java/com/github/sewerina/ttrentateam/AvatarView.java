package com.github.sewerina.ttrentateam;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class AvatarView extends LinearLayout {

    ImageView mImage;
    TextView mEmail;
    TextView mFirstName;
    TextView mLastName;

    public AvatarView(Context context) {
        this(context, null);
    }

    public AvatarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setOrientation(HORIZONTAL);

        inflate(context, R.layout.widget_avatar, this);

        mImage = findViewById(R.id.iv_avatar);
        mEmail = findViewById(R.id.tv_email);
        mFirstName = findViewById(R.id.tv_firstName);
        mLastName = findViewById(R.id.tv_lastName);

        TypedArray mainTypedArray = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.AvatarView, 0, R.style.DefaultAvatar);

        Drawable drawable = mainTypedArray.getDrawable(R.styleable.AvatarView_imageValue);
        mImage.setImageDrawable(drawable);

        String email = mainTypedArray.getString(R.styleable.AvatarView_emailText);
        mEmail.setText(email);

        String firstName = mainTypedArray.getString(R.styleable.AvatarView_firstNameText);
        mFirstName.setText(firstName);

        String lastName = mainTypedArray.getString(R.styleable.AvatarView_lastNameText);
        mLastName.setText(lastName);

        int[] textAttr = new int[]{
                android.R.attr.textSize, // 16842901
                android.R.attr.textColor, // 16842904
        };
        int textStyle = mainTypedArray.getResourceId(R.styleable.AvatarView_textStyle, -1);
        if (textStyle > 0) {
            TypedArray textTypedArray = context.getTheme()
                    .obtainStyledAttributes(textStyle, textAttr);

            int textSizeInPx = textTypedArray.getDimensionPixelSize(0, 0);
            mEmail.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeInPx);
            mFirstName.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeInPx);
            mLastName.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeInPx);

            int textColorRes = textTypedArray.getResourceId(1, -1);
            if (textColorRes > 0) {
                mEmail.setTextColor(ContextCompat.getColor(context, textColorRes));
                mFirstName.setTextColor(ContextCompat.getColor(context, textColorRes));
                mLastName.setTextColor(ContextCompat.getColor(context, textColorRes));
            }

            textTypedArray.recycle();
        }

        mainTypedArray.recycle();
    }
}
