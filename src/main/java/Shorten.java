

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.jena.web.DatasetAdapter;
import org.apache.jena.web.DatasetGraphAccessorHTTP;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * Servlet implementation class Shorten
 */
public class Shorten extends HttpServlet {
	private static final long serialVersionUID = 1L;


	public static Logger logger = Logger.getLogger(Shorten.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	 HttpSession session = request.getSession(true);
		
	 if (session.isNew()) {
		  logger.info("This is the New Session");
		  } else {
		  logger.info("This is the old Session");
		  }
		
		ServletOutputStream out = response.getOutputStream();
		String longUrl = request.getParameter("url");
		String emot = request.getParameter("emot");
		
		if(longUrl==null) return;
		
		
		try {	
			// Get request count
			int requestCount = EmotUtils.getRequestCount(request.getRemoteAddr());
			
			if(requestCount>100) {
				out.print("You are too emotional. Try again tomorrow!");
				return;
			} else {
			// User is allowed to create new EMOT
				int totalCount = EmotUtils.getTotalCount();
				
				logger.info("Total count of emots is "+totalCount);
				String newId = getId(totalCount);
				logger.info("Unique id for "+totalCount+" = "+newId);
				
				try{
					// Create new EMOT
					EmotUtils.shorten(request.getRemoteAddr(),longUrl,newId,emot);
					logger.info("Redirecting to new info address");
					String fullPath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
					response.sendRedirect(fullPath+"/Info?url="+newId);
					//response.sendRedirect(fullPath+"/Info?url="+fullPath+"/"+newId+"&redirect="+longUrl);
					//response.sendRedirect(request.getRequestURI()+"/info/"+newId);
					
				} catch(Exception ex) {
					response.sendError(
							HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
							"ERROR in querying the endpoint: " + ex.getMessage());
					return;
				}
				
			}

			// Everything ready
			return;
		} catch(UnknownHostException uh) {
			response.sendError(
					HttpServletResponse.SC_NOT_FOUND,
					"ERROR in querying the endpoint: " + uh.getMessage());
			return;
		} catch(QueryExceptionHTTP err) {
			logger.info("Error querying "+EmotUtils.endpoint);
			logger.info(err.getResponseMessage());
			logger.info(err.getResponseCode());
			response.sendError(
					HttpServletResponse.SC_NOT_FOUND,
					"ERROR in querying the endpoint: " + err.getResponseMessage());
			return;
		} catch (Exception ex) {
			logger.info("ErrorClass: "+ex.getClass());
			logger.info("Error querying "+EmotUtils.endpoint);
			response.sendError(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"ERROR in querying the endpoint: " + ex.getMessage());
			return;
		}
	}
	
    private String getId(int number) {
    	return Base62.fromBase10(number+10000);
    }
    
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
