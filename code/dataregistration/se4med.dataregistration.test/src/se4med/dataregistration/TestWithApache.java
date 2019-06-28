package se4med.dataregistration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;

public class TestWithApache {

	public static void main(String[] args)
			throws ClientProtocolException, IOException, URISyntaxException, NoSuchAlgorithmException {

		URIBuilder builder = new URIBuilder(TestConfiguration.url_service);
		builder.setParameter(Se4MedDataRegServlet.ACTION_PARAM_NAME, Se4MedDataRegServlet.AUTHENTICATE_DOCTOR_NS_ACTION);
		builder.setParameter(Se4MedDataRegServlet.EMAIL_PARAM, TestConfiguration.emaildoc);
		builder.setParameter(Se4MedDataRegServlet.PASSWORD_PARAM, toSha256(TestConfiguration.password));
		// builder.setParameter(Se4MedDataRegServlet.IDPATIENT_PARAM, "1"); // String
		builder.setParameter(Se4MedDataRegServlet.IDAPP_PARAM, "StereoTest");

		HttpPost httppost = new HttpPost(builder.build());
		HttpClient httpclient = HttpClients.createDefault();

		// Execute and get the response
		HttpResponse response = httpclient.execute(httppost);
		System.out.println(response);
		HttpEntity entity = response.getEntity();

		System.out.println(httppost.getURI());
		if (entity != null) {
			try (InputStream instream = entity.getContent()) {
				// Do something useful
				System.out.println(instream);
				BufferedReader in = new BufferedReader(new InputStreamReader(instream));
				String inputLine;
				StringBuffer content = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					content.append(inputLine);
				}
				in.close();
				System.out.println(content);
			}
		}
	}

	public static String toSha256(String password) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(password.getBytes());
		return bytesToHex(md.digest());
	}

	public static String bytesToHex(byte[] bytes) {
		StringBuffer result = new StringBuffer();
		for (byte byt : bytes)
			result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
		return result.toString();
	}
}