package com.github.sewerina.ttrentateam;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.sewerina.ttrentateam.db.UserEntity;
import com.squareup.picasso.Picasso;

public class UserActivity extends AppCompatActivity {
    private static final String EXTRA_USER_ENTITY = "userEntity";
    private UserEntity mUserEntity;

    public static Intent newIntent(Context context, UserEntity userEntity) {
        Intent intent = new Intent(context, UserActivity.class);
        intent.putExtra(EXTRA_USER_ENTITY, userEntity);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        if (getIntent() != null) {
            mUserEntity = (UserEntity) getIntent().getSerializableExtra(EXTRA_USER_ENTITY);
        }

        AvatarView avatarView = findViewById(R.id.avatarView);
        avatarView.mFirstName.setText(mUserEntity.firstName);
        avatarView.mLastName.setText(mUserEntity.lastName);
        avatarView.mEmail.setText(mUserEntity.email);
        Picasso.get()
                .load(mUserEntity.photo)
                .placeholder(R.drawable.def)
                .into(avatarView.mImage);

    }
}
