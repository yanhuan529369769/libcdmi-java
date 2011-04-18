package eu.venusc.cdmi;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

public class ContainerOperations {

	private URL endpoint;
	private DefaultHttpClient httpclient;

	/**
	 * 
	 * @param endpoint
	 * @param httpclient
	 */

	public ContainerOperations(URL endpoint, DefaultHttpClient httpclient) {

		this.httpclient = httpclient;
		this.endpoint = endpoint;
	}

	/**
	 * To create a container
	 * @param remoteContainer
	 * @param parameters
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws CDMIOperationException
	 */
	public HttpResponse create(String remoteContainer, Map parameters)
			throws ClientProtocolException, IOException, CDMIOperationException {

		HttpPut httpput = new HttpPut(endpoint + "/" + remoteContainer);

		httpput.setHeader("Content-Type", CDMIContentType.CDMI_CONTAINER);
		httpput.setHeader("Accept", CDMIContentType.CDMI_CONTAINER);
		httpput.setHeader("X-CDMI-Specification-Version",
				CDMIContentType.CDMI_SPEC_VERSION);

		ContainerCreateRequest createObj = new ContainerCreateRequest();
		StringWriter out = new StringWriter();
		createObj.writeJSONString(out);

		StringEntity entity = new StringEntity(out.toString());
		httpput.setEntity(entity);
		return httpclient.execute(httpput);
	}

	/**
	 * To delete a container
	 * @param remoteContainer
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws CDMIOperationException
	 */
	public HttpResponse delete(String remoteContainer)
			throws ClientProtocolException, IOException, CDMIOperationException {

		HttpDelete httpdelete = new HttpDelete(endpoint + "/" + remoteContainer);

		httpdelete.setHeader("Content-Type", CDMIContentType.CDMI_CONTAINER);
		httpdelete.setHeader("Accept", CDMIContentType.CDMI_CONTAINER);
		httpdelete.setHeader("X-CDMI-Specification-Version",
				CDMIContentType.CDMI_SPEC_VERSION);

		return httpclient.execute(httpdelete);
	}

	/**
	 * To read container information
	 * @param remoteContainer
	 * @param fields
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public HttpResponse read(String remoteContainer, List<String> fields)
			throws ClientProtocolException, IOException {
		String path = endpoint.toString() + "/" + remoteContainer + "/?";

		for (String f : fields) {
			path = path + f;
		}

		HttpGet httpget = new HttpGet(path);
		httpget.setHeader("Content-Type", CDMIContentType.CDMI_CONTAINER);
		httpget.setHeader("Accept", CDMIContentType.CDMI_CONTAINER);
		httpget.setHeader("X-CDMI-Specification-Version",
				CDMIContentType.CDMI_SPEC_VERSION);

		return httpclient.execute(httpget);
	}

	/**
	 * To get childrens of a specific container
	 * @param remoteContainer
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws CDMIOperationException
	 * @throws ParseException
	 */
	public Object[] getChildren(String remoteContainer)
			throws ClientProtocolException, IOException,
			CDMIOperationException, ParseException {
		String path = endpoint.toString();
		path = path + "/" + remoteContainer;

		List<String> children = new ArrayList<String>();
		children.add("children");
		HttpResponse hr = read(remoteContainer, children);
		return Utils.getElementArrary(hr, "children");

	}

}
