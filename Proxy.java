import java.io.*; 
import javax.servlet.*; 
import javax.servlet.http.*; 
import java.net.*;
import java.lang.*;
import org.json.*;
import org.json.JSONObject;


public class stockSearch extends HttpServlet { 
 public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
	 int i;char c = 0;
	 JSONObject json_result = new JSONObject();
	 String result = new String();
	 
	 
	 String result_url = request.getParameter("data");
	 //String ori_url = "http://default-environment-ahpqdaabbm.elasticbeanstalk.com/?symbol=" + result_url;
	 //String result_url = ori_url + request;
	 
	 
	 if(result_url != null){
		 //URL url = new URL("http://default-environment-ahpqdaabbm.elasticbeanstalk.com/?symbol=" + result_url);
		 URL url = new URL("http://default-environment-ahpqdaabbm.elasticbeanstalk.com/?symbol="+result_url);
		 URLConnection urlConnection = url.openConnection();
		 urlConnection.setAllowUserInteraction(false);
		 InputStream urlStream = url.openStream();
	 
		 try{
			 while((i = urlStream.read()) != -1){
				 c = (char)i;
				 result += c;
			 }
		 }catch(Exception e){
			 e.printStackTrace();
		 }finally{
			 if(urlStream != null)
				 urlStream.close();
		 }
	 
	 
		 response.setContentType("text/html"); 
		 PrintWriter out = response.getWriter(); 
		
		 json_result = XML.toJSONObject(result);
		 //out.println(result_url);
		 out.println(json_result);
		 //out.println(request);
	 }
//	 }else{
//		 response.setContentType("text/html"); 
//		 PrintWriter out = response.getWriter();
//		 out.println("Nothing gets called!");
//	 }
// 
 
}
} 