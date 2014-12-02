package emot.utils;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.jena.web.DatasetAdapter;
import org.apache.jena.web.DatasetGraphAccessorHTTP;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;
import com.maxmind.geoip.regionName;

public class EmotUtils {
	
	public static Logger logger = Logger.getLogger(EmotUtils.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
	public static final String endpoint = "http://localhost:3030/emot/sparql";
	public static final String dataEndpoint= "http://localhost:3030/emot/data";

public static int getTotalCount() throws IOException {
	logger.info("Getting total EMOT.EmotURL count ...");
	
	URL url = new URL(EmotUtils.endpoint);
	HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	connection.connect();
	
	String query = 
			"SELECT (count(DISTINCT ?e) as ?c)"+ 
			"WHERE { ?e <http://emot.com/schema#createdAt> ?at . }";
	    	
	    	QueryExecution qe = QueryExecutionFactory.sparqlService(connection.getURL().toString(), query);
			
			ResultSet results = qe.execSelect();
			int totalCount = 0;
			while(results.hasNext())
		    {
		      QuerySolution soln = results.nextSolution() ;
		      totalCount = soln.getLiteral("c").getInt(); 
		    }
			qe.close();
			connection.disconnect();
			return totalCount;
}

public static int getRequestCount(String ip) throws IOException {
	logger.info("Getting request count ...");
	
	URL url = new URL(EmotUtils.endpoint);
	HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	connection.connect();
	
	String reqId = Users.createID(ip);
	
	String query = 
			"SELECT (count(DISTINCT ?e) as ?c)"+ 
			"WHERE { "+
			"?user <http://emot.com/schema#ID> '"+reqId+"' ."+
			"?user <http://emot.com/schema#hasEmotion> ?emotion . "+
			"?emotion <http://emot.com/schema#happenedAt> ?s . "+
			"BIND(substr(str(now()),1,10) as ?today) "+ 
			"BIND(substr(str(?s),1,10) as ?lday) "+ 
			"FILTER(?today=?lday) }";
	    	
	    	QueryExecution qe = QueryExecutionFactory.sparqlService(connection.getURL().toString(), query);
			
			ResultSet results = qe.execSelect();
			int requestCount = 0;
			while(results.hasNext())
		    {
		      QuerySolution soln = results.nextSolution() ;
		      requestCount = soln.getLiteral("c").getInt(); 
		    }
			qe.close();
			connection.disconnect();
			
			return requestCount;
}

	
public static String[] getEmotURL(String urlID) { 
		
		
		try {
			URL url = new URL(endpoint);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.connect();
			
			if(!urlID.startsWith("/")) urlID="/"+urlID;
			
			String query = 
					"SELECT DISTINCT ?url ?type"+ 
					" WHERE { <http://emot.com/id"+urlID+"> <http://emot.com/schema#redirectsTo> ?url . "+
					" ?emotion <http://emot.com/schema#isResponseTo> <http://emot.com/id"+urlID+"> . "+
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

public static void shorten(String ip, String url, String urlID, String emot) { 
	logger.info("New EMOT.EmotURL:");
	Model model = ModelFactory.createDefaultModel();
	Resource emotionClass = ResourceFactory.createResource("http://emot.com/schema#"+emot);
	
	if(!urlID.startsWith("/")) urlID="/"+urlID;
	
    String now = sdf.format(new Date());
    
    String newId = Users.createID(ip);
    if(newId==null) newId = "unknown";
    
    Resource user = model.createResource("http://emot.com/id/usr/"+newId)
    		.addProperty(RDF.type, EMOT.User)
			.addProperty(EMOT.ID,"u"+newId);
    
	Resource newEmotURL = model.createResource("http://emot.com/id"+urlID)
	.addProperty(RDF.type, EMOT.EmotURL)
	.addProperty(EMOT.createdAt, model.createTypedLiteral(now,XSDDatatype.XSDdateTime))
	.addProperty(EMOT.createdBy, user)
	.addProperty(EMOT.redirectsTo,model.createResource(url));
	
	Resource newEmotion = model.createResource("http://emot.com/id/emotion"+urlID)
			.addProperty(RDF.type,emotionClass)
			.addProperty(EMOT.happenedAt, model.createTypedLiteral(now,XSDDatatype.XSDdateTime))
			.addProperty(EMOT.isResponseTo, newEmotURL);
	
	 user.addProperty(EMOT.hasEmotion,newEmotion);

	logger.info(model.toString());
	
	DatasetGraphAccessorHTTP accessor = new DatasetGraphAccessorHTTP(EmotUtils.dataEndpoint);
	DatasetAdapter adapter = new DatasetAdapter(accessor);
	adapter.add(model);

}

public static JSONArray spotLight(String inputUrl) {
	
URL url = null;
try {
	url = new URL("http://demo.seco.hut.fi/spotlight/rest/annotate?condidence=0.5&url="+inputUrl);
} catch (MalformedURLException e) {
	e.printStackTrace();
}

try {
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestProperty("Accept", "application/xml");
    InputStream inputStream = connection.getInputStream();
    

    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setValidating(false);
    dbf.setNamespaceAware(true);
    dbf.setFeature("http://xml.org/sax/features/namespaces", false);
    dbf.setFeature("http://xml.org/sax/features/validation", false);
    dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
    dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
    
    DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
    Document document = documentBuilder.parse(inputStream);
    document.getDocumentElement().normalize();

    
    NodeList resources = document.getElementsByTagName("Resource");
    
   // HashMap<String, Double> resourceMap = new HashMap<String,Double>();
    
    JSONArray jArr = new JSONArray();
    HashSet<String> uris = new HashSet<String>();
    
    uris.add("http://dbpedia.org/resource/Concept");
    uris.add("http://dbpedia.org/resource/Quality");
    uris.add("http://dbpedia.org/resource/Value");
    
    
    for(int i=0; i<resources.getLength(); i++){
        Node resource = resources.item(i);
        NamedNodeMap attributes = resource.getAttributes();
        
        Node uriNode = attributes.getNamedItem("URI");
        Node nameNode = attributes.getNamedItem("surfaceForm");
        Node similarityNode = attributes.getNamedItem("similarityScore");
        
        Double simValue = Double.parseDouble(similarityNode.getNodeValue());
        String uri = uriNode.getNodeValue();
        String name = Character.toUpperCase(nameNode.getNodeValue().charAt(0)) + nameNode.getNodeValue().substring(1); 
        
        if(!uris.contains(uri)) {
         uris.add(uri);
        JSONObject jObj = new JSONObject();
        jObj.put("uri",uri);
        jObj.put("name", name);
        jObj.put("similarity", simValue);
        
        jArr.put(jObj);
        
        }
     //   resourceMap.put(uri.getNodeValue(), simValue);
        		
      }
    
   // Map<String,Double> sortedResources = sortByValue(resourceMap);
    
    //JSONArray jArr = new JSONArray();
    //jArr.add(sortedResources);
    
    return jArr;
    

} catch (Exception exception) {
    System.out.println("Exception is" + exception);
    return null;

}
			
}


public static void pollAnswer(String ip, String urlID, String emot) { 
	logger.info("New poll answer:");
	Model model = ModelFactory.createDefaultModel();
	Resource emotionClass = ResourceFactory.createResource("http://emot.com/schema#"+emot);
	
	if(!urlID.startsWith("/")) urlID="/"+urlID;
	
    String now = sdf.format(new Date());
    String newId = Users.createID(ip);
    
    Resource user = model.createResource("http://emot.com/id/u"+newId)
    		.addProperty(RDF.type, EMOT.User)
			.addProperty(EMOT.ID,"u"+newId);
    
	Resource newEmotURL = model.createResource("http://emot.com/id"+urlID);
	
	Resource newEmotion = model.createResource("http://emot.com/id/emotion/e"+UUID.randomUUID())
			.addProperty(RDF.type,emotionClass)
			.addProperty(EMOT.happenedAt, model.createTypedLiteral(now,XSDDatatype.XSDdateTime))
			.addProperty(EMOT.isResponseTo, newEmotURL);
	
	 user.addProperty(EMOT.hasEmotion,newEmotion);

	logger.info(model.toString());
	
	DatasetGraphAccessorHTTP accessor = new DatasetGraphAccessorHTTP(EmotUtils.dataEndpoint);
	DatasetAdapter adapter = new DatasetAdapter(accessor);
	adapter.add(model);

}

public static Location getLocationFromIp(String ip) throws IOException {
	//path = "/work/eclipse/workspace/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/emot/.";

	ClassLoader cl = Thread.currentThread().getContextClassLoader();
    URL fileurl = cl.getResource("/GeoLiteCity.dat");
	String filePath = fileurl.getFile();
	logger.info("Loaded GEOFILE FROM "+filePath);
	
	File dbfile = new File(filePath);
	logger.info("ip-table is loaded: "+dbfile.canRead());
	LookupService lookupService = new LookupService(dbfile, LookupService.GEOIP_STANDARD);
	
	//FIXME: IP lookup fix for localhost
	if(ip.equals("127.0.0.1")) ip = "130.233.124.147";

    logger.info("Getting location for "+ip);
	Location location = lookupService.getLocation(ip);
	
	return location;
}

public static void createVisit(String ip, String urlID, Location location) {
	logger.info("Creating new visit ...");

	Model model = ModelFactory.createDefaultModel();
	
	if(!urlID.startsWith("/")) urlID="/"+urlID;
	
    String now = sdf.format(new Date());
    String visitId = Users.createID(ip);
    
    Resource user = model.createResource("http://emot.com/id/user/u"+visitId)
    		.addProperty(RDF.type, EMOT.User)
			.addProperty(EMOT.ID,"u"+visitId);
    
    Resource visit = model.createResource("http://emot.com/id/visit/"+UUID.randomUUID())
    		.addProperty(RDF.type, EMOT.Visit)
    		.addProperty(EMOT.visitedAt, model.createTypedLiteral(now,XSDDatatype.XSDdateTime))
			.addProperty(EMOT.visitedUser, user);
    
	// Populate region. Note that regionName is a MaxMind class, not an instance variable
	if (location != null) {
        logger.info("Found location");
        location.region = regionName.regionNameByCode(location.countryCode, location.region);
        
        if(location.city!=null) visit.addProperty(EMOT.city, model.createTypedLiteral(location.city,XSDDatatype.XSDstring));
        if(location.countryCode!=null) visit.addProperty(EMOT.countryCode, model.createTypedLiteral(location.countryCode,XSDDatatype.XSDstring));
        if(location.countryName!=null) visit.addProperty(EMOT.country, model.createTypedLiteral(location.countryName,XSDDatatype.XSDstring));
        if(location.region!=null) visit.addProperty(EMOT.region, model.createTypedLiteral(location.region,XSDDatatype.XSDstring));
        Float latf = location.latitude;
        Float lngf = location.longitude;
        if(!latf.isNaN()) visit.addProperty(EMOT.lat, model.createTypedLiteral(latf,XSDDatatype.XSDfloat));
        if(!lngf.isNaN()) visit.addProperty(EMOT.lng, model.createTypedLiteral(lngf,XSDDatatype.XSDfloat));

	} else {
		logger.info("location not found");
	}
    		
	Resource oldEmotURL = model.createResource("http://emot.com/id"+urlID)
	.addProperty(EMOT.hasVisit, visit);
	
	logger.info(model.toString());
	
	DatasetGraphAccessorHTTP accessor = new DatasetGraphAccessorHTTP(EmotUtils.dataEndpoint);
	DatasetAdapter adapter = new DatasetAdapter(accessor);
	adapter.add(model);

}

public static String[] getInfo(String urlID) { 
	
	
	try {
		URL url = new URL(endpoint);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.connect();
		
		if(!urlID.startsWith("/")) urlID="/"+urlID;
		
		String query = 
				"SELECT ?at ?url ?type (count(DISTINCT ?visit) as ?c)"+ 
				" WHERE {  "+
				" <http://emot.com/id"+urlID+"> <http://emot.com/schema#createdAt> ?at . "+
				" ?emotion <http://emot.com/schema#isResponseTo> <http://emot.com/id"+urlID+"> . "+
				" ?emotion a ?type . "+
				" <http://emot.com/id"+urlID+"> <http://emot.com/schema#createdBy> ?by . "+
				" <http://emot.com/id"+urlID+"> <http://emot.com/schema#redirectsTo> ?url . "
			   + "OPTIONAL {<http://emot.com/id"+urlID+"> <http://emot.com/schema#hasVisit> ?visit . }"
			   	+ "} GROUP BY ?by ?at ?url ?type";
		
		logger.info("Getting info: ");
		logger.info(query);
		
		QueryExecution qe = QueryExecutionFactory.sparqlService(connection.getURL().toString(), query);
		
		ResultSet results = qe.execSelect();
		String[] arr = {"0",null,null,null};
        
		while(results.hasNext())
	    {
		
	      QuerySolution soln = results.nextSolution();
	      arr[0] = soln.getLiteral("c").getString();
	      if(soln.getLiteral("at")==null) return null;
	      arr[1] = soln.getLiteral("at").getString();
	      arr[2] = soln.getResource("url").getURI();
	      arr[3] = soln.getResource("type").getURI();
	      logger.info("Emots:");
	      logger.info(arr[1]);
	      logger.info(arr[2]);
	      logger.info(arr[3]);
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
