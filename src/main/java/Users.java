package emot.utils;
import org.apache.commons.codec.binary.Base64;


public class Users {
	
public static String createID(String ip) {
	try {
		return new String(Base64.encodeBase64(ip.getBytes()));
	} catch(Exception ex) {
		return null;
	}
} 

}
