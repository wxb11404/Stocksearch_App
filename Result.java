package com.xubo.stocksearch;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

public class result extends Fragment {
	private static WebView webview;
	ResultListener newscallback; /////////////////////news
	fbListener fbCallback;
	
	public interface fbListener{
		public void onFbButtonClick();
	}
	
	public interface ResultListener{
		public void onNewsButtonClick();
	}
	
	@Override
	public void onAttach(Activity newsactivity){
		super.onAttach(newsactivity);
		try{
			fbCallback = (fbListener)newsactivity;
			newscallback = (ResultListener)newsactivity;
		}catch(ClassCastException e){
			throw new ClassCastException(newsactivity.toString() + "must implement ResultListener");
		}
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.result, container, false);
		webview = (WebView) view.findViewById(R.id.result);
		final Button newsbutton = (Button)view.findViewById(R.id.newsbutton);
		newsbutton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				buttonClicked(v);
				// TODO Auto-generated method stub
			}
		});		
		
		final Button facebookbutton = (Button)view.findViewById(R.id.facebookbutton);
		facebookbutton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				fbbuttonClicked(v);
			}
		});
		return view;
	}
	
	public void buttonClicked(View view){///////////////////news
		newscallback.onNewsButtonClick();
	}/////////////////news
	
	public void changeTextProperties(String text) {
		
//		textview.setText(Html.fromHtml(text));
		webview.loadData(text, "text/html", "UTF-8");
	}
	
	public void fbbuttonClicked(View view){
		fbCallback.onFbButtonClick();
	}
	
	
}
