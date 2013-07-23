

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
 * Servlet implementation class Info
 */
public class Info extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static Logger logger = Logger.getLogger(Info.class);
       
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String url = request.getParameter("url");
		//String redirect = request.getParameter("redirect");
		
		String[] infoArr = EmotUtils.getInfo(url);
		
		String fullPath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
		
		PrintWriter out = response.getWriter(); 
		
		out.println("<html>");
		out.println("<body style='margin:50px 0px; padding:0px;text-align:center;'>");
		out.println("<div style='margin:0px auto;'>");
		out.println("<h1>Emoted url: <a href='"+fullPath+"/"+url+"'>"+fullPath+"/"+url+"</a></h1>");
		out.println("<p> Created at "+infoArr[1]+"</p>");
		out.println("<p> Redirects to <a href='"+infoArr[2]+"'>"+infoArr[2]+"</a></p>");
		out.println("<p> Number of times clicked: "+infoArr[0]+"</p>");
		out.println("</div>");
		out.println("</body>");
		out.println("</html>");
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
