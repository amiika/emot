package emot;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import emot.utils.EmotUtils;

/**
 * Servlet implementation class Poll
 */
public class Poll extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Poll() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String url = request.getParameter("url");
		String emot = request.getParameter("emot");
		String re = request.getParameter("re");
		
		EmotUtils.pollAnswer(request.getRemoteAddr(),url,emot);
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter(); 
		
		String fullPath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
		
		
		out.println("<html>");
		out.println("<body style='margin:50px 0px; padding:0px;text-align:center;'>");
		out.println("<div style='margin:0px auto;'>");
		out.println("<p> Thank you! Redirecting you to "+re+"</p>");
		out.println("</div>");
		out.println("</body>");
		out.println("</html>");
		
		response.sendRedirect(fullPath+"/info/"+url);
		
	}

	}
