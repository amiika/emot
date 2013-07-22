import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;


public class Utils {
	
	private Utils() {
		BasicConfigurator.configure();
	}
	
	public static Logger logger = Logger.getLogger(Utils.class);
	public static final String endpoint = "http://localhost:3030/emot/sparql";
	
public static String[] getEmotUrl(String shortUrl) { 
		
		
		try {
			URL url = new URL(endpoint);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.connect();
			
			if(!shortUrl.startsWith("/")) shortUrl="/"+shortUrl;
			
			String query = 
					"SELECT DISTINCT ?url ?type"+ 
					" WHERE { <http://emot.com"+shortUrl+"> <http://emot.com/schema#redirectsTo> ?url . "+
					" ?emotion <http://emot.com/schema#isResponseTo> <http://emot.com"+shortUrl+"> . "+
					" ?emotion a ?type } ";
			
			logger.info(query);
			
			QueryExecution qe = QueryExecutionFactory.sparqlService(connection.getURL().toString(), query);
			
			ResultSet results = qe.execSelect();
			String[] arr = {null,null};

			while(results.hasNext())
		    {
		      QuerySolution soln = results.nextSolution() ;
		      arr[0] = soln.getResource("url").getURI(); 
		      arr[1] = soln.getResource("type").getURI();
		    }
			
			qe.close();
			
			return arr;
		
		} catch (MalformedURLException e1) {
			
			e1.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}


}
