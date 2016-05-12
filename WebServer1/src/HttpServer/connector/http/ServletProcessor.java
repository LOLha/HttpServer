package HttpServer.connector.http;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import XmlConfig.ServletConfig;

public class ServletProcessor {
	
	ServletConfig config = new ServletConfig();
	
	public void process(HttpRequest request,HttpResponse response){
	    if(pattern(request,config.getServletConfig())){
                 String servletClass = request.getServletClass();	    	
	    	try {
				Class c = Class.forName(servletClass);
				Servlet s =(Servlet) c.newInstance();
				s.service(request, response);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (ServletException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    	
	    }
		
		
		
	}

	private boolean pattern(HttpRequest request,
			Map<String, String> servletConfig) {
		Set<Entry<String,String>> s = config.getServletConfig().entrySet();
		Iterator<Entry<String,String>> it = s.iterator();
		while(it.hasNext()){
			Entry<String,String> entry = it.next();
			String servletName1 = entry.getKey();
			String servletClass = entry.getValue();
			if(servletName1.equals(request.getServletName())){
				request.setServletClass(servletClass);
				return true;
			}
			
		}
		
		return false;
	}
	

}
