package com.xubo.stocksearch;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Displaynews extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.displaynews);
		////////////////////news
		Intent intent = getIntent();
		String message = intent.getStringExtra(StockSearch.EXTRA_MESSAGE);
		
		WebView webview = (WebView)findViewById(R.id.news_display);
		webview.loadData(message, "text/html", "UTF-8");
		webview.setWebViewClient(myClient);
		
	}
	
	WebViewClient myClient = new WebViewClient(){
		@Override 
		public boolean shouldOverrideUrlLoading(WebView view, String url){
			buildDialog(url);
			Log.d("boxu", url);
			return true;
		}
		
	};
	
	public void buildDialog(String url){
		final String uri = url;
		AlertDialog.Builder ald = new AlertDialog.Builder(this);
		ald.setTitle("View");
		ald.setNegativeButton("View", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
				startActivity(browser);
			}
		});
		ald.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
		ald.show();
	}

}
