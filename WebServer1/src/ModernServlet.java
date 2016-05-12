import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ModernServlet extends HttpServlet{

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
	    PrintWriter out = response.getWriter();
	    out.println("<html>");
	    out.println("<head>");
	    out.println("<title>Modern Servlet</title>");
	    out.println("</head>");
	    out.println("<body>");
	    out.println("<h1>");
	    out.println("hello");
	    //out.println(request.getQueryString());
	    out.print("</h1>");
	    out.println("<br/>");
	    out.println("<h1>");
	   // out.println(request.getParameter("hbc"));
	   // Enumeration<String> en = request.getParameterNames();
	    //while(en.hasMoreElements()){
	    //	out.println(en.nextElement());
	    //}
 	    out.print("</h1>");
	    out.println("</body>");
		System.out.println(request.getRequestURI());
		
	}
	
	public static void main(String[] args) {
		
	}
	

}
