package juba;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A single thread listening and responding through it's assigned port
 *
 */
public class JubaServer implements Runnable {
	
	private int listenPort;
	
	private String dataFolder = "data/";

	final private static String byCityQuery = "/weather?q=";
	final private static int byCityQueryLength = byCityQuery.length();
	
	public JubaServer(int listenPort) {
		this.listenPort = listenPort;
		this.run();
	}
	
	@Override
	public void run() {
		ServerSocket servSock = null;
		try {
			servSock = new ServerSocket(listenPort);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		System.out.println("Thread listening to port " + listenPort);
		
		while(true) {
			System.out.println("Waiting.");
			try {
				Socket connSock = servSock.accept();
				// Somebody connected
				InetAddress client = connSock.getInetAddress();
				System.out.print("Connection from " + client +
						" aka. " + client.getHostName());
	
				BufferedReader input =
						new BufferedReader(
								new InputStreamReader(connSock.getInputStream()));
				
				DataOutputStream output =
						new DataOutputStream(connSock.getOutputStream());
				
				httpReturn(input, output);
				System.out.println(" - Response sent.");

			} catch (IOException e) {
				try {
					servSock.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}
	}

	/**
	 * Responds to http request
	 * 
	 * @param input BufferedReader from which to read request
	 * @param output DataOutputStream to write response to
	 */
	private void httpReturn(BufferedReader input, DataOutputStream output) {
			
		try {
			
			String inputString = input.readLine();
			if (inputString.startsWith("GET")) {
				// API spec:
				// <address>/weather?q={city}
				// no whitespace allowed
				
				if (inputString.contains(byCityQuery)) {
					
					int bcqIndex = inputString.indexOf(byCityQuery);
					
					String cityKey =
							inputString.substring( // Citykey is found between end of cityquery and ' '
									bcqIndex + byCityQueryLength,
									inputString.indexOf(' ', bcqIndex));
					
					cityKey = cityKey.toUpperCase();
					output.writeBytes(httpHeader(200)+cityKeyToData(cityKey));
				}
			}

			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * http header mockup
	 * 
	 * @param code - HTTP header code
	 * @return HTTP header as string
	 */
	private String httpHeader(int code) {
		String header = "HTTP/2.0 ";
		
		switch(code) {
		case 200:
			header += "200 OK";
			break;
		case 400:
			header += "400 Bad Request";
			break;
		case 403:
			header += "403 Forbidden";
			break;
		case 404:
			header += "404 Not Found";
			break;
		case 500:
			header += "500 Internal Server Error";
			break;
		case 501:
			header += "501 Not Implemented";
			break;
		}
		
		header += "\r\n";
		header += "Connection: close\r\n";
		header += "content-type: application/json;charset=UTF-8\r\n";
	    header += "Server: JUBA server\r\n\r\n";
		
		return header;
	}
	
	/**
	 * @param cityKey
	 * @return Data in string form acquired using cityKey. Hopefully as JSON.
	 */
	private String cityKeyToData(String cityKey) {
		// Add your databases here or elsewhere
		// This simulates SELECT * FROM cities WHERE id='cityKey' or something to that effect
		String cityData = "";
		
		try {
			BufferedReader buffRead =
					new BufferedReader(new FileReader(dataFolder + cityKey));
			String line;
			while ((line = buffRead.readLine()) != null) {
				cityData += line;
			}
			
			buffRead.close();
			
		} catch (FileNotFoundException e) {
			return "{\"error\":\"No such city\"}";
		} catch (IOException e) {
			e.printStackTrace();
			return "{\"error\":\"Strange error\"}";
		}
		return cityData;
	}
}
