package emot.utils;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;


public class Users {
	
public static String createID(String ip) {
	try {
	return SimpleCrypto.encrypt(ip);
	} catch(Exception ex) {
		return null;
	}
} 

}
