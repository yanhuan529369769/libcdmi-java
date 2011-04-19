package eu.venusc.cdmi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Utils {

	/**
	 * This methods returns content of a file with bytes. Taken from
	 * http://www.exampledepot.com/egs/java.io/File2ByteArray.html
	 * 
	 * @param file
	 * @return byte[] 
	 * @throws IOException
	 */
	public static byte[] getBytesFromFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);

		// Get the size of the file
		long length = file.length();

		// You cannot create an array using a long type.
		// It needs to be an int type.
		// Before converting to an int type, check
		// to ensure that file is not larger than Integer.MAX_VALUE.
		if (length > Integer.MAX_VALUE) {
			// File is too large
			throw new IOException("File is too large: " + file.getName());

		}

		// Create the byte array to hold the data
		byte[] bytes = new byte[(int) length];

		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file "
					+ file.getName());
		}

		// Close the input stream and return bytes
		is.close();
		return bytes;
	}


	public static String getContent(String file) throws IOException {

		FileReader fileReader = new FileReader(file);
		BufferedReader in = new BufferedReader(fileReader);
		StringBuilder content = new StringBuilder("");
		String str;
		while ((str = in.readLine()) != null) {
			content.append(str);
		}
		return content.toString();

	}

	public static List getElementCollection(HttpResponse response,
			String elementName) throws IOException, ParseException {

		JSONParser parser = new JSONParser();
		InputStream stream = response.getEntity().getContent();
		InputStreamReader is = new InputStreamReader(stream);

		ContainerFactory containerFactory = new ContainerFactory() {
			public List creatArrayContainer() {
				return new LinkedList();
			}

			public Map createObjectContainer() {
				return new LinkedHashMap();
			}
		};
		Map jsonMap = (Map) parser.parse(is, containerFactory);
		List foundElements = (LinkedList) jsonMap.get(elementName);
		stream.close();
		is.close();
		return foundElements;
	}

	
	public static Object getElement(HttpResponse response, String elementName)
			throws IOException, ParseException {
		JSONParser parser = new JSONParser();
		InputStream stream = response.getEntity().getContent();
		InputStreamReader is = new InputStreamReader(stream);

		ContainerFactory containerFactory = new ContainerFactory() {
			public List creatArrayContainer() {
				return new LinkedList();
			}

			public Map createObjectContainer() {
				return new LinkedHashMap();
			}
		};

		Map jsonMap = (Map) parser.parse(is, containerFactory);

		Object content = jsonMap.get(elementName);
		stream.close();
		is.close();
		return content;

	}


	public static Object getObjectContent(HttpResponse response)
			throws IOException, ParseException {
		JSONParser parser = new JSONParser();
		InputStream stream = response.getEntity().getContent();
		InputStreamReader is = new InputStreamReader(stream);
		

		parser = new JSONParser();
		stream = response.getEntity().getContent();
		is = new InputStreamReader(stream);

		ContainerFactory containerFactory = new ContainerFactory() {
			public List creatArrayContainer() {
				return new LinkedList();
			}

			public Map createObjectContainer() {
				return new LinkedHashMap();
			}
		};
		Map jsonMap = (Map) parser.parse(is, containerFactory);

		String content = jsonMap.get("value").toString();
		Object decodedObj = Base64.decodeBase64(content);
		stream.close();
		is.close();
		return decodedObj;
	}

	
	public static String getTextContent(HttpResponse response)
			throws IOException, ParseException {

		JSONParser parser = new JSONParser();
		InputStream stream = response.getEntity().getContent();
		InputStreamReader is = new InputStreamReader(stream);

		ContainerFactory containerFactory = new ContainerFactory() {
			public List creatArrayContainer() {
				return new LinkedList();
			}

			public Map createObjectContainer() {
				return new LinkedHashMap();
			}
		};

		Map jsonMap = (Map) parser.parse(is, containerFactory);
		String content = jsonMap.get("value").toString();
		stream.close();
		is.close();
		return content;

	}

	
	static File createFile(String content, String name, String format)
			throws IOException {

		File tempFile = File.createTempFile(name, format);
		// Write to temporary file
		BufferedWriter out = new BufferedWriter(new FileWriter(tempFile));
		out.write(content);
		out.close();
		tempFile.deleteOnExit();
		return tempFile;

	}

}
