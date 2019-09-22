package com.github.sewerina.ttrentateam;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.sewerina.ttrentateam.db.UserEntity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefreshLayout;
    private UserAdapter mUserAdapter;
    private MainViewModel mViewModel;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_about:
                    startActivity(new Intent(MainActivity.this, AboutAppActivity.class));
                    return true;

                case R.id.navigation_home:
                    return true;

                case R.id.navigation_dashboard:
                    return true;

                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUserAdapter = new UserAdapter();
        mRecyclerView.setAdapter(mUserAdapter);

        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mViewModel.getUsers().observe(this, new Observer<List<UserEntity>>() {
                    @Override
                    public void onChanged(List<UserEntity> userList) {
                        mUserAdapter.update(userList);
                    }
                }
        );
        mViewModel.isRefresh().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean refreshing) {
                mRefreshLayout.setRefreshing(refreshing);
            }
        });
        mViewModel.getMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                if (!message.isEmpty()) {
                    Toast toast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });

        mRefreshLayout = findViewById(R.id.swipeRefresh);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                load();
            }
        });

        load();
    }

    @Override
    protected void onResume() {
        super.onResume();
        load();
    }

    private void load() {
        if (isNetworkAvailableAndConnected()) {
            mViewModel.loadFromWeb();
        } else {
            mViewModel.loadFromDb();
        }
    }

    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            }

            networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            }

            networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            }
        }

        return false;
    }

    private class UserHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mFirstNameTv;
        private TextView mLastNameTv;

        private UserEntity mUserEntity;

        public UserHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mFirstNameTv = itemView.findViewById(R.id.tv_firstName);
            mLastNameTv = itemView.findViewById(R.id.tv_lastName);
        }

        void bind(UserEntity entity) {
            mUserEntity = entity;
            mFirstNameTv.setText(mUserEntity.firstName);
            mLastNameTv.setText(mUserEntity.lastName);
        }

        @Override
        public void onClick(View v) {
            startActivity(UserActivity.newIntent(v.getContext(), mUserEntity));
        }
    }

    private class UserAdapter extends RecyclerView.Adapter<UserHolder> {
        private final List<UserEntity> mUsers = new ArrayList<>();

        @NonNull
        @Override
        public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent, false);
            return new UserHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull UserHolder holder, int position) {
            holder.bind(mUsers.get(position));
        }

        @Override
        public int getItemCount() {
            return mUsers.size();
        }

        void update(List<UserEntity> entities) {
            mUsers.clear();
            mUsers.addAll(entities);
            notifyDataSetChanged();
        }
    }

}
