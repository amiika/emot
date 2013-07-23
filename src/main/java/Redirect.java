

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

/**
 * Servlet implementation class Redirect
 */
public class Redirect extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static Logger logger = Logger.getLogger(Redirect.class);
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Redirect() {
        //super();
		BasicConfigurator.configure();
		//logger.info("Starting servlet ...");
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String urlId = request.getServletPath();

		if(urlId.contains("/img/")){
			logger.info("Getting image"+request.getRequestURI());
			
			  ServletContext sc = getServletContext();  
			  String path = request.getRequestURI().substring(
					  request.getContextPath().length() +
					  1, request.getRequestURI().length());
			  
			     String filename = sc.getRealPath(path);  
			  
			     // Get the MIME type of the image  
			     String mimeType = sc.getMimeType(filename);  
			     if (mimeType == null) {  
			         sc.log("Could not get MIME type of "+filename);  
			         response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);  
			         return;  
			     }  
			     // Set content type  
			     response.setContentType(mimeType);  
			  
			     // Set content size  
			     File file = new File(filename);  
			     response.setContentLength((int)file.length());  
			  
			     // Open the file and output streams  
			     FileInputStream in = new FileInputStream(file);  
			     OutputStream out = response.getOutputStream();  
			  
			     // Copy the contents of the file to the output stream  
			     byte[] buf = new byte[1024];  
			     int count = 0;  
			     while ((count = in.read(buf)) >= 0) {  
			         out.write(buf, 0, count);  
			     }  
			     in.close();  
			     out.close();  
			
			return;
		}
		/*
		if(urlId.contains("/info/")) {
			logger.info(urlId.substring(urlId.lastIndexOf("/")+1,urlId.length()));
			response.sendRedirect(request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath()+"/Info?url="+);
			return;
		}*/
			
		if(urlId.length()<=1) {
			response.sendRedirect("index.jsp");
			return;
		}
			
		String[] longUrl = EmotUtils.getEmotUrl(urlId);
		
		if(longUrl[0]!=null) {
			String emotion = longUrl[1].substring(longUrl[1].lastIndexOf("#")+1);
			/* Print temp page with a smile */
			PrintWriter out = response.getWriter();
			out.println("<html>");
			out.println("<body style='margin:50px 0px; padding:0px;text-align:center;'>");
			out.println("<img style='width:200px; height: 200px; margin:0px auto;' src='img/"+emotion+".svg'/>");
			out.println("<h2>Redirecting to "+longUrl[0]+" ...</h2>");
			out.println("</body>");
			out.println("</html>");
			
			EmotUtils.createVisit(request.getRemoteAddr(),urlId);
			
			/* Redirect after 1 second delay */
			response.setHeader("Refresh", "1; URL="+longUrl[0]);
			//response.sendRedirect(longUrl);
			
			logger.info(urlId +" redirects to "+longUrl[0]);
			return;
		}
		else {
			response.sendRedirect("index.jsp");
			logger.info("Short url not found!");
			return;
		}
		
	}
	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
