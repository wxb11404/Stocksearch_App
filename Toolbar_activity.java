package com.xubo.stocksearch;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.AdapterView.OnItemClickListener;

public class toolbar_activity extends Fragment{
	private static AutoCompleteTextView edittext;
	ToolbarListener activityCallback;
	
	public interface ToolbarListener{
		public void onButtonClick(String text);
	}
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		try{
			activityCallback = (ToolbarListener)activity;
		}catch(ClassCastException e){
			throw new ClassCastException(activity.toString() + "must implement ToolbatListener");
		}
	}
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.toolbar, container, false);
		edittext = (AutoCompleteTextView)view.findViewById(R.id.searchbar);
		final Button send = (Button)view.findViewById(R.id.send);
		send.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				buttonClicked(v);
				// TODO Auto-generated method stub
//				return;
			}
		});
		
		final AutoCompleteTextView auto = (AutoCompleteTextView)view.findViewById(R.id.searchbar);
		auto.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.d("qwer", "qqq");
				buttonClicked(view);
			}
        });
		
		
		return view;
	}
	
	public void buttonClicked(View view){
		activityCallback.onButtonClick(edittext.getText().toString());
	}
	
}
