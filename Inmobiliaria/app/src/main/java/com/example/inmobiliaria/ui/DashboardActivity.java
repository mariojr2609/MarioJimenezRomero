package com.example.inmobiliaria.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import com.example.inmobiliaria.R;

public class DashboardActivity extends AppCompatActivity {
    private TextView mTextMessage;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.menu_fav:
                    mTextMessage.setText(R.string.tittle_fav);
                    return true;
                case R.id.menu_mylist:
                    mTextMessage.setText(R.string.tittle_mylist);
                    return true;
                case R.id.menu_iniciarsesion:
                    mTextMessage.setText(R.string.title_iniciarsesion);
                    return true;
                case R.id.menu_cerrarsesion:
                    mTextMessage.setText(R.string.title_cerrarsesion);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
}
