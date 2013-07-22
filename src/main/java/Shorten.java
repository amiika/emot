

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
	public static final String endpoint = "http://localhost:3030/emot/sparql";
	public static final String eupdate = "http://localhost:3030/emot/update";

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
		logger.info("parameter is "+ longUrl);
		
		if(longUrl==null) return;
		
		String query = 
		"SELECT (count(DISTINCT ?e) as ?c)"+ 
		"WHERE { "+
		"?user <http://emot.com/schema#IP> '"+request.getRemoteAddr()+"' ."+
		"?user <http://emot.com/schema#hasEmotion> ?emotion . "+
		"?emotion <http://emot.com/schema#happenedAt> ?s . "+
		"BIND(substr(str(now()),1,10) as ?today) "+ 
		"BIND(substr(str(?s),1,10) as ?lday) "+ 
		"FILTER(?today=?lday) }";
		
		try {
			logger.info("Querying "+endpoint);
			logger.info("Query: "+query);
			
			URL url = new URL(endpoint);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.connect();
			logger.info("Opened connection");
			logger.info(connection.getURL());
			
			
			int requestCount = getCount(connection.getURL().toString(), query);
			
			/*
			QueryExecution qe = QueryExecutionFactory.sparqlService(connection.getURL().toString(), query);
			
			ResultSet results = qe.execSelect();
			int requestCount = 0;
			while(results.hasNext())
		    {
		      QuerySolution soln = results.nextSolution() ;
		      requestCount = soln.getLiteral("c").getInt(); 
		    }*/
			
			if(requestCount>100) {
				out.print("You are too emotional. Try again tomorrow!");
				return;
			} else {
				
				query = 
						"SELECT (count(DISTINCT ?e) as ?c)"+ 
						"WHERE { ?e <http://emot.com/schema#createdAt> ?at . }";
				
				int totalCount = getCount(connection.getURL().toString(), query);
				
				logger.info("Total count of emots is "+totalCount);
				logger.info("Unique id for "+totalCount+" = "+getId(totalCount));
				
				try{
					String newId = getId(totalCount);
					shorten(request.getRemoteAddr(),longUrl,newId,emot);
					logger.info("Redirecting to new info address");
					String fullPath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
					response.sendRedirect(fullPath+"/Info?url="+fullPath+"/"+newId+"&redirect="+longUrl);
					//response.sendRedirect(request.getRequestURI()+"/info/"+newId);
					
				} catch(Exception ex) {
					response.sendError(
							HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
							"ERROR in querying the endpoint: " + ex.getMessage());
					return;
				}
				
			}

			
		
			
			
			return;
		} catch(UnknownHostException uh) {
			response.sendError(
					HttpServletResponse.SC_NOT_FOUND,
					"ERROR in querying the endpoint: " + uh.getMessage());
			return;
		} catch(QueryExceptionHTTP err) {
			logger.info("Error querying "+endpoint);
			logger.info("Query: "+query);
			logger.info(err.getResponseMessage());
			logger.info(err.getResponseCode());
			response.sendError(
					HttpServletResponse.SC_NOT_FOUND,
					"ERROR in querying the endpoint: " + err.getResponseMessage());
			return;
		} catch (Exception ex) {
			logger.info("ErrorClass: "+ex.getClass());
			logger.info("Error querying "+endpoint);
			logger.info("Query: "+query);
			response.sendError(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"ERROR in querying the endpoint: " + ex.getMessage());
			return;
		}
	}
	

    
    private String getId(int number) {
    	return Base62.fromBase10(number+10000);
    }
    
    private int getCount(String connection, String query) {
    	
    	QueryExecution qe = QueryExecutionFactory.sparqlService(connection, query);
		
		ResultSet results = qe.execSelect();
		int requestCount = 0;
		while(results.hasNext())
	    {
	      QuerySolution soln = results.nextSolution() ;
	      requestCount = soln.getLiteral("c").getInt(); 
	    }
		qe.close();
		
		return requestCount;
    	
    }
    
    private static Resource EmotURL = ResourceFactory.createResource("http://emot.com/schema#EmotURL");
    private static Property createdAt = ResourceFactory.createProperty("http://emot.com/schema#createdAt");
    private static Property createdBy = ResourceFactory.createProperty("http://emot.com/schema#createdBy");
    private static Property redirectsTo = ResourceFactory.createProperty("http://emot.com/schema#redirectsTo");
    private static Property trigger = ResourceFactory.createProperty("http://emot.com/schema#triggers");
    
    private static Resource Emotion = ResourceFactory.createResource("http://emot.com/schema#Emotion");
    private static Property happenedAt = ResourceFactory.createProperty("http://emot.com/schema#happenedAt");
    private static Property isResponseTo = ResourceFactory.createProperty("http://emot.com/schema#isResponseTo");
   
    
    private static Resource User = ResourceFactory.createResource("http://emot.com/schema#User");
    private static Property hasEmotion = ResourceFactory.createProperty("http://emot.com/schema#hasEmotion");
    private static Property IP = ResourceFactory.createProperty("http://emot.com/schema#IP");
    private static Property socialLogin = ResourceFactory.createProperty("http://emot.com/schema#socialLogin");

    
    private static Property becauseOf = ResourceFactory.createProperty("http://emot.com/schema#becauseOf");
    
	private void shorten(String ip, String url, String Id,String emot) { 
		Model model = ModelFactory.createDefaultModel();
		Resource emotionClass = ResourceFactory.createResource("http://emot.com/schema#"+emot);
		
        String now = sdf.format(new Date());
        
        Resource user = model.createResource("http://emot.com/user/u"+ip.replace(".",""))
				.addProperty(IP,ip);
        
		Resource newEmotURL = model.createResource("http://emot.com/"+Id)
		.addProperty(RDF.type, EmotURL)
		.addProperty(createdAt, model.createTypedLiteral(now,XSDDatatype.XSDdateTime))
		.addProperty(createdBy, user)
		.addProperty(redirectsTo,model.createResource(url));
		
		Resource newEmotion = model.createResource("http://emot.com/emotion/"+Id)
				.addProperty(RDF.type,emotionClass)
				.addProperty(happenedAt, model.createTypedLiteral(now,XSDDatatype.XSDdateTime))
				.addProperty(isResponseTo, newEmotURL);
		        
		logger.info(model.toString());
		
		DatasetGraphAccessorHTTP accessor = new DatasetGraphAccessorHTTP("http://localhost:3030/emot/data");
		DatasetAdapter adapter = new DatasetAdapter(accessor);
		adapter.add(model);

	}
	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
