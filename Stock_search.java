package com.xubo.stocksearch;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;



public class StockSearch extends FragmentActivity implements toolbar_activity.ToolbarListener , result.ResultListener, result.fbListener{
	public final static String EXTRA_MESSAGE = "com.xubo.StockSearch.MESSAGE";
	public static String News_title[] = new String[100];
	public static String News_link[] = new String[100];
	public static int jsonNewsLength = 0;
	private static final String URL_PREFIX_FRIENDS = "https://graph.facebook.com/me/friends?access_token=";
	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	public Button newsHeadLine = null;
	public int newsFlag = 0;
	public String Stock_Name = new String();
	public String Stock_Symbol = new String();
	public String Stock_Change = new String();
	public String Stock_ChangeInPercent = new String();
	public String Stock_LastTrade = new String();
	public String Stock_ChartURL = new String();
	public int FirstLogin = 0;
	@Override
	
	protected void onCreate(Bundle savedInstanceState) {
		
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		hidefragment();
		////////////////////////////facebook
		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
		
		Session session = Session.getActiveSession();
		if(session == null){
			if(savedInstanceState != null){
				session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
			}
			if(session == null){
				session = new Session(this);
			}
			Session.setActiveSession(session);
			if(session.getState().equals(SessionState.CREATED_TOKEN_LOADED)){
				session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
			}
		}
		
//		onClickLogout();
		newsHeadLine = (Button)findViewById(R.id.newsbutton);
//		updateView();
		////////////////////////////facebook
	}
	
	@Override
	public void onStart(){
		super.onStart();
		Session.getActiveSession().addCallback(statusCallback);
	}
	
	@Override
	public void onStop(){
		super.onStop();
		Session.getActiveSession().removeCallback(statusCallback);
	} 
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		Session session = Session.getActiveSession();
		Session.saveSession(session, outState);
	}
	
	public void hidefragment(){
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment resultfrg = fm.findFragmentById(R.id.result);
		
		ft.hide(resultfrg);
		ft.commit();
	}
	
	public void showfragment(){
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment resultfrg = fm.findFragmentById(R.id.result);
		
		if(resultfrg.isHidden()){
			ft.show(resultfrg);
		}
		ft.commit();
	}
	///////////////////////
	
	///////////////////////
	public void onButtonClick(String text){
		text = text.trim();
		if(text.length() != 0){
			if(text.indexOf(" ") != -1){
				stockSymbolError();
				return;
			}
			
			
			result resultArea = (result)getSupportFragmentManager().findFragmentById(R.id.result);
			HttpGetter get = new HttpGetter();
			URL url = null;
			String html = new String();
			String stock_resultstring=null;
			try {
				
				url = new URL("http://cs-server.usc.edu:39057/examples/servlet/stockSearch?data=" + text);
				get.execute(url);
				stock_resultstring = get.get(); //get string from asyncTask
				html = parseJson(stock_resultstring);
				if(html != null){
					resultArea.changeTextProperties(html);
					showfragment();
					if(newsFlag == 0){
						newsHeadLine.setVisibility(View.INVISIBLE);
					}else{
						////do nothing
						newsHeadLine.setVisibility(View.VISIBLE);
					}
				}else{
					//
					processError();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
				stockSymbolError();
//				return;
			}catch(Exception e){
				e.printStackTrace();
				stockSymbolError();
//				return;
			}
			
		
			
		}else{
			AlertDialog.Builder ald = new AlertDialog.Builder(this);
			ald.setTitle("Please Enter Stock Symbol");
			
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
	
	public void onNewsButtonClick(){
		Intent intent = new Intent(this, Displaynews.class);
		String message = new String();
		message += "<!DOCTYPE html><html><body>";
		for(int i=0; i<jsonNewsLength; i++){
			message += "<a href="+News_link[i]+">"+News_title[i]+"</a><hr>";
		}
		message += "</body></html>";
		intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
		
		Toast.makeText(StockSearch.this, "Showing   "+jsonNewsLength+"   headlines", Toast.LENGTH_LONG).show();
	}
	
	
	public void onFbButtonClick(){
		Log.d("fb","You Click");
		updateView();
		
	}
	
	public class HttpGetter extends AsyncTask<URL, Void, String>{
		@Override
		protected String doInBackground(URL...urls){
			
			String result = null;
			try{
				
				HttpClient httpclient = new DefaultHttpClient();
				URLEncoder.encode(urls[0].toString(),"UTF-8");
				HttpGet httpget = new HttpGet(urls[0].toString());
				HttpResponse response = httpclient.execute(httpget);
				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if(statusCode == 200){
					HttpEntity entity = response.getEntity();
					InputStream input = entity.getContent();
					BufferedReader read = new BufferedReader(new InputStreamReader(input));
					StringBuilder interstring = new StringBuilder();
					String line = null;
					while((line = read.readLine()) != null){
						interstring.append(line);
					}
					result = interstring.toString();
				}
			}catch(IOException e){
				e.printStackTrace();
				return null;
			}
			return result;
		}
		@Override
		protected void onPostExecute(String result){
			super.onPostExecute(result);
			
			
		}
		
	}
	
	public String parseJson(String stock_resultstring){
		String output_sum = new String();
		JSONObject stock_result = null;
		JSONObject stock_qoute = null;
		JSONObject stock_result_temp = null;
		JSONObject stock_news = null;
		JSONArray jsonNews = null;
		String MarketCap = null;String Open = null;String OneyrTarget = null; String YearLow = null;
		String Volume = null;String Bid = null; String Ask = null; String DaysHigh = null;String AvrVolume = null;
		String YearHigh = null; String DaysLow = null; String PreviousClose = null;
		Double changeFlag = 0.0; Double changePFlag = 0.0;

		try{
			stock_result_temp = new JSONObject(stock_resultstring);
			stock_result = stock_result_temp.getJSONObject("results");
			stock_qoute = stock_result.getJSONObject("qoute");
			
			try{
				stock_news = stock_result.getJSONObject("News");
				jsonNews = stock_news.getJSONArray("Item");
				jsonNewsLength = jsonNews.length();
				for(int i=0; i<jsonNewsLength; i++){
					JSONObject obj = jsonNews.getJSONObject(i);
					News_title[i] = obj.getString("title");
					News_link[i] = obj.getString("link");
				}
				newsFlag = 1;
			}catch(JSONException e){
				newsFlag = 0;
			}
			
			
			Stock_ChartURL = stock_result.getString("StockChartImageURL");
			///////////////////
			Stock_Symbol = stock_result.getString("Symbol");
			Stock_Name = stock_result.getString("Name");
			///////////////////
			Bid = stock_qoute.getString("Bid");
			Ask = stock_qoute.getString("Ask");
			MarketCap = stock_qoute.getString("MarketCapitalization");
			Open = stock_qoute.getString("Open");
			OneyrTarget = stock_qoute.getString("OneyrTargetPrice");
			Volume = stock_qoute.getString("Volume");
			YearHigh = stock_qoute.getString("YearHigh");
			YearLow = stock_qoute.getString("YearLow");
			DaysHigh = stock_qoute.getString("DaysHigh");
			DaysLow = stock_qoute.getString("DaysLow");
			Stock_ChangeInPercent = stock_qoute.getString("ChangeinPercent");
			PreviousClose = stock_qoute.getString("PreviousClose");
			Stock_Change = stock_qoute.getString("Change");
			Stock_LastTrade = stock_qoute.getString("LastTradePriceOnly");
			AvrVolume = stock_qoute.getString("AverageDailyVolume");
			///////////////////fetch news
		}catch(JSONException e){
			e.printStackTrace();
			/////////////handle exception
//			output_sum = "<html><head></head><body><p>Stock News not Available</p></body></html>";
			
//			processError();
			return null;
			/////////////handle exception
		}

		output_sum += "<html><head></head><body style=\"padding:0;margin:0;\">";
		output_sum += "<h3 style=\"padding:0;margin:0;\"><center>"+Stock_Name+"("+Stock_Symbol+")</h3>";
		output_sum += "<h2 style=\"padding:0;margin:0;\"><center>"+Stock_LastTrade+"</h2>";
		
		changeFlag = Double.parseDouble(Stock_Change);
		changePFlag = Double.parseDouble(Stock_ChangeInPercent);
		if(changeFlag < 0){
			changeFlag = -1*changeFlag;
			Stock_Change = changeFlag.toString();
			changePFlag = -1*changePFlag;
			Stock_ChangeInPercent = changePFlag.toString();
			output_sum += "<center><img src="+"http://www-scf.usc.edu/~csci571/2014Spring/hw6/down_r.gif"+"><font color='red'>"+Stock_Change+"("+Stock_ChangeInPercent+"%)</font>";
		}else{
			output_sum += "<center><img src="+"http://www-scf.usc.edu/~csci571/2014Spring/hw6/up_g.gif"+"><font color='green'>"+Stock_Change+"("+Stock_ChangeInPercent+"%)</font>";
		}
		output_sum += "<table width='80%' style=\"font-size:12px\"><center>";
		output_sum += "<tr><td align='left'><left>Prev Close</td><td align='right'>"+PreviousClose+"</td></tr>";
		output_sum += "<tr><td>Open</td><td align='right'>"+Open+"</td></tr>";
		output_sum += "<tr><td>Bid</td><td align='right'>"+Bid+"</td></tr>";
		output_sum += "<tr><td>Ask</td><td align='right'>"+Ask+"</td></tr>";
		output_sum += "<tr><td>1st Yr Target</td><td align='right'>"+OneyrTarget+"</td></tr>";
		output_sum += "<tr><td>Day Range</td><td align='right'>"+DaysLow+"-"+DaysHigh+"</td></tr>";
		output_sum += "<tr><td>52 wk Range</td><td align='right'>"+YearLow+"-"+YearHigh+"</td></tr>";
		output_sum += "<tr><td>Volume</td><td align='right'>"+Volume+"</td></tr>";
		output_sum += "<tr><td>Avg Vol(3m)</td><td align='right'>"+AvrVolume+"</td></tr>";
		output_sum += "<tr><td>Market Cap</td><td align='right'>"+MarketCap+"</td></tr>";
		output_sum += "</table>";
		output_sum += "<img src="+Stock_ChartURL+" width='80%'>";
		output_sum += "</body></html>";
	
//		Log.d("xubo", chart_url);


		
		return output_sum;
	}
	
	
	
	private void updateView(){
		Session session = Session.getActiveSession();
		if(session.isOpened()){
			Log.d("facebook", "open");
			postFeed();
		}else{
			Log.d("facebook", "close");
			onClickLogin();
		}
	}
	
	private void onClickLogin(){
		Session session = Session.getActiveSession();
		if(!session.isOpened() && !session.isClosed()){
			session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
		}else{
			Session.openActiveSession(this, true, statusCallback);
		}
	}
	
	private void onClickLogout() {
        Session session = Session.getActiveSession();
        if (!session.isClosed()) {
            session.closeAndClearTokenInformation();
        }
    }
	
	
	
	private class SessionStatusCallback implements Session.StatusCallback{
		@Override
		public void call(Session session, SessionState state, Exception exception){
			
			if(FirstLogin == 0){
				FirstLogin = 1;
				return;
			}else{
				if(state == SessionState.OPENED)
					updateView();
				}
			}
			
			
	}
	
	
	private void postFeed(){
		String Link = "http://finance.yahoo.com/q?s="+Stock_Symbol;
		Bundle feed = new Bundle();
		feed.putString("name", Stock_Name);
		feed.putString("caption", "Stock Information of "+Stock_Name+"("+Stock_Symbol+")");
		feed.putString("description", "Last Trade Price:"+Stock_LastTrade+" Change:"+Stock_Change+"("+Stock_ChangeInPercent+"%)");
		feed.putString("link", Link);
		feed.putString("picture", Stock_ChartURL);
		
		//invoke the dialog
		WebDialog feedDialog = (
				new WebDialog.FeedDialogBuilder(StockSearch.this, Session.getActiveSession(),
						feed).setOnCompleteListener(new OnCompleteListener(){
							@Override
							public void onComplete(Bundle values, FacebookException error){
								if(error == null){
									final String postId = values.getString("post_id");
									if(postId != null){
										Toast.makeText(StockSearch.this, "Post Successfully  "+postId, Toast.LENGTH_SHORT).show();
									}else{
										Toast.makeText(StockSearch.this, "Post Cancelled", Toast.LENGTH_SHORT).show();
									}
								}
							}
						})
				).build();
		feedDialog.show();
	}
	
	public void processError(){
		AlertDialog.Builder ald = new AlertDialog.Builder(this);
		ald.setTitle("Stock News Not Available");
		
		ald.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
		ald.show();
		
	}
	
	public void stockSymbolError(){
		AlertDialog.Builder ald = new AlertDialog.Builder(this);
		ald.setTitle("Wrong Stock Symbol");
		
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