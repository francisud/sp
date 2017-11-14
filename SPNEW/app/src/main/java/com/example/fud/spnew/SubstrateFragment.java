package com.example.fud.spnew;


import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SubstrateFragment extends DialogFragment {

//    AlertDialog.Builder builder = new AlertDialog.Builder(this);
//    LayoutInflater inflater = this.getLayoutInflater();
//    View substrateView = inflater.inflate(R.layout.test_layout, null);
//        builder.setView(substrateView);
//
//    final AlertDialog alertDialogObject = builder.create();
//        alertDialogObject.show();
//
//        substrateView.findViewById(R.id.test).setOnClickListener(new View.OnClickListener(){
//        public void onClick(View v){
//            Log.d("debug", "test");
//            alertDialogObject.cancel();
//        }
//    });



    public interface SubstrateFragmentListener {
        void onSelect(DialogFragment dialog, int which);
    }

    // Use this instance of the interface to deliver action events
    SubstrateFragmentListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (SubstrateFragmentListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                R.array.substrate_array, R.layout.substrate_listview);

        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.substrate_listview, null);
        LinearLayout layout = (LinearLayout)view.findViewById(R.id.layout);

        TextView tv;
        for(int i = 0; i < adapter.getCount(); i++){
            final int id = i;
            tv = new TextView(getActivity());
            tv.setId(i);
            tv.setText(adapter.getItem(i));
            tv.setTextSize(18);
            tv.setClickable(true);
            tv.setPadding(0, 10, 0, 0);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onSelect(SubstrateFragment.this, id);
                }
            });
            layout.addView(tv);
        }

        builder.setView(view);

        return builder.create();
    }


    }
