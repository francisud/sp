package com.example.fud.spnew;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

public class Activity_Main extends FragmentActivity {

    Fragment[] fragments;

    Fragment_Identify fragment_identify;
    Fragment_MyMushrooms fragment_myMushrooms;
    Fragment_Families fragment_families;
    Fragment_HowTo fragment_howTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragment_identify = new Fragment_Identify();
        fragment_myMushrooms = new Fragment_MyMushrooms();
        fragment_families = new Fragment_Families();
        fragment_howTo = new Fragment_HowTo();

        fragments = new Fragment[4];
        fragments[0] = fragment_identify;
        fragments[1] = fragment_myMushrooms;
        fragments[2] = fragment_families;
        fragments[3] = fragment_howTo;


        //database
        Helper_Database helperDatabase = new Helper_Database(this);
        SQLiteDatabase db = helperDatabase.getWritableDatabase();

        setContentView(R.layout.content_main2);

        BottomNavigationView bnv = (BottomNavigationView) findViewById(R.id.navigation);
        bnv.setItemIconTintList(null);

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {return;}

            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.fragment_container, fragment_identify, "0");
            transaction.add(R.id.fragment_container, fragment_myMushrooms, "1");
            transaction.add(R.id.fragment_container, fragment_families, "2");
            transaction.add(R.id.fragment_container, fragment_howTo, "3");

            transaction.hide(fragment_myMushrooms);
            transaction.hide(fragment_families);
            transaction.hide(fragment_howTo);

            transaction.commit();

            bnv.setSelectedItemId(R.id.identify);
        }

        bnv.setOnNavigationItemSelectedListener(
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.identify:
                            changeFragment(0);
                            break;
                        case R.id.my_mushrooms:
                            changeFragment(1);
                            break;
                        case R.id.families:
                            changeFragment(2);
                            break;
                        case R.id.how_to:
                            changeFragment(3);
                            break;
                    }
                    return true;
                }
            });
    }

    private void changeFragment(int id){
        Fragment newFragment = null;

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        for(int i = 0; i < 4; i++){
            if(i == id){
                newFragment = fragments[i];
                transaction.show(newFragment);
            }

            else{
                newFragment = fragments[i];
                transaction.hide(newFragment);
            }
        }
        transaction.commit();
    }
}
