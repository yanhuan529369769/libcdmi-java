package eu.venusc.cdmi;

import static eu.venusc.cdmi.CDMIContentType.CDMI_CONTAINER;
import static eu.venusc.cdmi.CDMIContentType.CDMI_SPEC_VERSION;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.json.simple.parser.ParseException;

public class ContainerOperations {

	private URL endpoint;
	private HttpClient httpclient;

	public ContainerOperations(URL endpoint, HttpClient httpclient) {

		this.httpclient = httpclient;
		this.endpoint = endpoint;
	}

	public HttpResponse create(String remoteContainer,
			Map<String, Object> parameters) throws ClientProtocolException,
			IOException, CDMIOperationException, URISyntaxException {

	    if (!remoteContainer.endsWith("/"))
	        remoteContainer += "/";

		HttpPut httpput = new HttpPut(Utils.getURI(endpoint, remoteContainer, true));
		httpput.setHeader("Content-Type", CDMI_CONTAINER);
		httpput.setHeader("Accept", CDMI_CONTAINER);
		httpput.setHeader("X-CDMI-Specification-Version", CDMI_SPEC_VERSION);

		ContainerCreateRequest createObj = new ContainerCreateRequest();
		StringWriter out = new StringWriter();
		createObj.writeJSONString(out);

		StringEntity entity = new StringEntity(out.toString());
		httpput.setEntity(entity);
		return httpclient.execute(httpput);
	}

	public HttpResponse delete(String remoteContainer)
			throws ClientProtocolException, IOException, CDMIOperationException, URISyntaxException {
        if (!remoteContainer.endsWith("/"))
            remoteContainer += "/";

		HttpDelete httpdelete = new HttpDelete(Utils.getURI(endpoint, remoteContainer, true));
		httpdelete.setHeader("X-CDMI-Specification-Version", CDMI_SPEC_VERSION);

		return httpclient.execute(httpdelete);
	}

	public HttpResponse read(String remoteContainer, List<String> fields)
			throws ClientProtocolException, IOException, URISyntaxException {
       if (!remoteContainer.endsWith("/"))
            remoteContainer += "/";

		String path = remoteContainer + "?";

		for (String f : fields) {
			path = path + f;
		}
		HttpGet httpget = new HttpGet(Utils.getURI(endpoint, path));
		httpget.setHeader("Accept", CDMI_CONTAINER);
		httpget.setHeader("X-CDMI-Specification-Version", CDMI_SPEC_VERSION);

		return httpclient.execute(httpget);
	}

	public String[] getChildren(String remoteContainer)
			throws ClientProtocolException, IOException,
			CDMIOperationException, ParseException, URISyntaxException {
        if (!remoteContainer.endsWith("/"))
            remoteContainer += "/";

		List<String> fields = new ArrayList<String>();
		fields.add("children");
		HttpResponse response = read(remoteContainer, fields);
		// TODO: better conversion to String[]?
		List<Object> elements = Utils
				.getElementCollection(response, "children");
		return (String[]) elements.toArray(new String[elements.size()]);
	}

}