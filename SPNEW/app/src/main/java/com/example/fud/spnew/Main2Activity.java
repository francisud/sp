package com.example.fud.spnew;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

public class Main2Activity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main2);

        BottomNavigationView bnv = (BottomNavigationView) findViewById(R.id.navigation);

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {return;}

            HomeFragment homeFragment = new HomeFragment();
            homeFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, homeFragment).commit();

            bnv.setSelectedItemId(R.id.identify);
        }

        bnv.setOnNavigationItemSelectedListener(
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.my_mushrooms:
                            changeFragment(0);
                            break;
                        case R.id.identify:
                            changeFragment(1);
                            break;
                        case R.id.how_to:
                            changeFragment(2);
                            break;
                    }
                    return true;
                }
            });
    }

    private void changeFragment(int id){
        // Create fragment and give it an argument specifying the article it should show

        Fragment newFragment = null;

        if(id == 0)
            newFragment = new MyMushroomsFragment();
        if(id == 1)
            newFragment = new HomeFragment();
        if(id == 2)
            newFragment = new HowToFragment();

        Bundle args = new Bundle();
        newFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.commit();
    }
}
