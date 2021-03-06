package berlin.reiche.securitas.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;

import berlin.reiche.securitas.Settings;

import android.net.http.AndroidHttpClient;
import android.util.Base64;
import android.util.Log;

/**
 * A utility class with functionality for the HTTP communication.
 * 
 * @author Konrad Reiche
 * 
 */
public class HttpUtilities {

	/**
	 * Tag for logging.
	 */
	private static final String TAG = HttpUtilities.class.getSimpleName();

	/**
	 * The Android HTTP client is used over and over again. This factory method
	 * constructs one and sets the appropriate parameters.
	 * 
	 * @return a fully configured Android HTTP client.
	 */
	public static AndroidHttpClient newHttpClient() {
		AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
		client.getParams().setIntParameter("http.connection.timeout", 2000);
		return client;
	}

	/**
	 * Sets the headers for delivering the credentials.
	 * 
	 * @param request
	 *            The request to be modified.
	 * @param settings
	 *            the settings containing the necessary credentials.
	 */
	public static void setAuthorization(HttpRequestBase request,
			Settings settings) {

		String user = settings.getUsername();
		String password = settings.getPassword();
		byte[] rawCredentials = (user + ":" + password).getBytes();
		String credentials = Base64.encodeToString(rawCredentials,
				Base64.URL_SAFE | Base64.NO_WRAP);
		request.setHeader("Authorization", "Basic " + credentials);
	}

	/**
	 * Sets the request body by taking a data array and transforming it to
	 * parameter-value pairs.
	 * 
	 * @param request
	 *            the request to be modified.
	 * @param data
	 *            two-dimensional data array containing parameter-value pairs.
	 */
	public static void setRequestBody(HttpPost request, String[][] data) {

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		for (int i = 0; i < data.length; i++) {
			nameValuePairs.add(new BasicNameValuePair(data[i][0], data[i][1]));
		}
		try {
			request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, "The encoding is not supported: " + e.getMessage());
		}
	}

	/**
	 * Wrapper for closing the connection of a Android HTTP client.
	 * 
	 * @param client
	 *            the client to be closed.
	 */
	public static void closeClient(HttpClient client) {
		if (client instanceof AndroidHttpClient) {
			((AndroidHttpClient) client).close();
		} else {
			client.getConnectionManager().shutdown();
		}
	}

}
