package com.example.fud.spnew;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Fragment_Substrate extends DialogFragment {

    SubstrateFragmentListener mListener;

    public interface SubstrateFragmentListener {
        void onSelectSubstrate(DialogFragment dialog, int which);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (SubstrateFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement SubstrateFragmentListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                R.array.substrate_array, R.layout.substrate_listview);

        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.substrate_listview, null);
        LinearLayout layout = (LinearLayout)view.findViewById(R.id.layout);

        View v;
        TextView tv;

        for(int i = 0; i < adapter.getCount(); i++){
            v = new View(getActivity());
            v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 1));
            v.setBackgroundColor(Color.DKGRAY);

            final int id = i;
            tv = new TextView(getActivity());
            tv.setId(i);
            tv.setText(adapter.getItem(i));
            tv.setTextSize(18);
            tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            tv.setPadding(25,25,25,25);
            tv.setGravity(1);

            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onSelectSubstrate(Fragment_Substrate.this, id);
                }
            });

            layout.addView(v);
            layout.addView(tv);
        }

        builder.setView(view);
        return builder.create();
    }

}
