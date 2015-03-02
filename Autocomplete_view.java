package com.xubo.stocksearch;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;


////Reference From
////http://blog.csdn.net/jwzhangjie/article/details/15771953
public class autocompleteview extends AutoCompleteTextView{
	private int mBlock = 0;
	public static String News_title[] = new String[100];
	public static String News_link[] = new String[100];
	public static int jsonNewsLength = 0;
	
    public autocompleteview(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        init(context);  
    }  
    
    //////////////////
    private void init(Context con){  
        final AutoAdapter adapter = new AutoAdapter(con);  
        setAdapter(adapter); 
        addTextChangedListener(new TextWatcher() {  
            @Override  
            public void afterTextChanged(Editable s) {  
            	
            	if(mBlock == 1){
            		return;
            	}
            	// add asyncTask to send request to get the suggested string
            	String url = "";
            	String resultString = new String();
            	String Symbol[] = new String[100];
            	String Name[] = new String[100];
            	String Exch[] = new String[100];
            	JSONObject query_result = null;
            	JSONArray ResultArray = null;
            	int suggestLength = 0;
            	///////////
                String input = s.toString();  
                /////
                input = input.trim();
                if(input.length() >= 9){
                	return;
                }
                /////
                adapter.suggestList.clear();  

                
                HttpGetter get = new HttpGetter();
                try{
                	url = "http://autoc.finance.yahoo.com/autoc?query="+input+"&callback=YAHOO.Finance.SymbolSuggest.ssCallback";
                	get.execute(url);
                	resultString = get.get();
                }catch(Exception e){
                	e.printStackTrace();
                }
                
                if(resultString == null || resultString == ""){
                	
                	return;
                }
                
//                int leftLocation = resultString.indexOf("{");
//                int rightLocation = resultString.length() - 1;
//                String objString = resultString.substring(leftLocation, rightLocation);
                try{
                	int leftLocation = resultString.indexOf("{");
                    int rightLocation = resultString.length() - 1;
                    String objString = resultString.substring(leftLocation, rightLocation);
                	query_result = new JSONObject(objString);
                	ResultArray = query_result.getJSONObject("ResultSet").getJSONArray("Result");
                	suggestLength = ResultArray.length();
                	for(int i=0; i<suggestLength; i++){
                		JSONObject obj = ResultArray.getJSONObject(i);
                		Symbol[i] = obj.getString("symbol"); 
                		Name[i] = obj.getString("name");
                		Exch[i] = obj.getString("exch");
                	}
                }catch(JSONException e){
                	e.printStackTrace();
                	return;
                }catch(StringIndexOutOfBoundsException e){
                	return;
                }
                
                
                if (input.length() > 0) {  
                    for (int i = 0; i < suggestLength; i++) {  
                        adapter.suggestList.add(Symbol[i]+","+Name[i]+"("+Exch[i]+")");  //add source to adapter
                    }  
                }  
                ////////////////////////////////////
                adapter.notifyDataSetChanged();  
                showDropDown();  
            }  
            @Override  
            public void beforeTextChanged(CharSequence s, int start, int count,  int after) {  }  
            @Override  
            public void onTextChanged(CharSequence s, int start, int before,  int count) {  }  
        });  
        setThreshold(1);  
    }
  
    @Override
    protected void replaceText(CharSequence text){
    	mBlock = 1;
    	text = text.subSequence(0, text.toString().indexOf(","));
    	setText(text);
    	mBlock = 0;
    }
    
    
    ////Reference From
    ////http://blog.csdn.net/jwzhangjie/article/details/15771953
    class AutoAdapter extends BaseAdapter implements Filterable {  
        List<String> suggestList;  
        private Context AdContext;  
        private MyFilter suggestFilter;  
          
        public AutoAdapter(Context con) {  
            AdContext = con;  
            suggestList = new ArrayList<String>();  
        }  
        @Override  
        public int getCount() {  
        	if(suggestList == null){
        		return 0 ;
        	}else{
        		return suggestList.size();
        	}
        }  
        @Override  
        public Object getItem(int position) {  
        	if(suggestList == null){
        		return null;
        	}else{
        		return suggestList.get(position);
        	}
        }  
//        @Override  
//        public long getItemId(int position) {  
//            return position;  
//        }  
        @Override  
        public View getView(int position, View convertView, ViewGroup parent) {  
            if (convertView == null) {  
                TextView tv = new TextView(AdContext);  
                tv.setTextColor(Color.BLACK);  
                tv.setTextSize(16);  
                convertView = tv;  
            }  
            TextView txt = (TextView) convertView;  
            txt.setText(suggestList.get(position));  
            return txt;  
        }  
  
        
        @Override  
        public Filter getFilter() {  
            if (suggestFilter == null) {  
                suggestFilter = new MyFilter();  
            }  
            return suggestFilter;  
        }  
          
        private class MyFilter extends Filter {  
  
            @Override  
            protected FilterResults performFiltering(CharSequence constraint) {  
                FilterResults results = new FilterResults();  
                if (suggestList == null) {  
                    suggestList = new ArrayList<String>();  
                }  
                results.values = suggestList;  
                results.count = suggestList.size();  
                return results;  
            }  
            @Override  
            protected void publishResults(CharSequence constraint, FilterResults results) {  
            }  
        }

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
		
    }
    ////////asyncTask
    public class HttpGetter extends AsyncTask<String, Void, String>{
    	@Override
    	protected String doInBackground(String...urls){
    		String result = new String();
    		
    		try{
    			
    			HttpClient httpclient = new DefaultHttpClient();
    			URLEncoder.encode(urls[0],"UTF-8");
        		HttpGet httpget = new HttpGet(urls[0]);
    			HttpResponse response = httpclient.execute(httpget);
    			StatusLine statusLine = response.getStatusLine();
    			int statusCode = statusLine.getStatusCode();
    			if(statusCode == 200){
    				HttpEntity entity = response.getEntity();
    				InputStream input = entity.getContent();
    				BufferedReader read = new BufferedReader(new InputStreamReader(input));
    				StringBuilder interstring = new StringBuilder();
    				String line = new String();
    				while((line = read.readLine()) != null){
    					interstring.append(line);
    				}
    				result = interstring.toString();
    			}
    		}catch(Exception e){
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
  
}
