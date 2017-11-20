package com.example.fud.spnew;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

public class Activity_Main extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //database
        Helper_Database helperDatabase = new Helper_Database(this);
        SQLiteDatabase db = helperDatabase.getWritableDatabase();

        setContentView(R.layout.content_main2);

        BottomNavigationView bnv = (BottomNavigationView) findViewById(R.id.navigation);
        bnv.setItemIconTintList(null);

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {return;}

            Fragment_Home fragmentHome = new Fragment_Home();
            fragmentHome.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragmentHome).commit();

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
            newFragment = new Fragment_MyMushrooms();
        if(id == 1)
            newFragment = new Fragment_Home();
        if(id == 2)
            newFragment = new Fragment_HowTo();

        Bundle args = new Bundle();
        newFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.commit();
    }
}
