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

public class JubaServer implements Runnable {
	
	int listenPort;
	
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
		}
		System.out.println("Bound to localhost.");
		
		while(true) {
			System.out.println("Waiting.");
			try {
				Socket connSock = servSock.accept();
				// Somebody connected
				InetAddress client = connSock.getInetAddress();
				System.out.println("Connection from " + client +
						" aka. " + client.getHostName());
	
				BufferedReader input =
						new BufferedReader(
								new InputStreamReader(connSock.getInputStream()));
				
				DataOutputStream output =
						new DataOutputStream(connSock.getOutputStream());
				
				httpReturn(input, output);
				
				
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
					
					//System.out.println("Searching for: " + cityKey); // OK
					
					output.writeBytes(CityKeyToData(cityKey));

				}
				
			}
			
			//output.writeBytes("<h1>TEST TEST 123 123 TEST TEST</h1>");
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	private String CityKeyToData(String cityKey) {
		// Add your databases here or elsewhere
		// I guess this simulates GET FROM cities WHERE id = cityKey or something to that effect
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
