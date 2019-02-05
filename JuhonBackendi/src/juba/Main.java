package juba;

public class Main {

	public static void main(String[] args) {
		
		int listenPort = 80;
		
		try {
			if (args.length == 0) listenPort = 80;
			else listenPort = Integer.parseInt(args[0]);
			System.out.println("Listen port set to " + listenPort);
		} catch (NumberFormatException e) {
			System.out.println("Listen port not a number!");
			return;
		}
		@SuppressWarnings("unused")
		JubaServer server = new JubaServer(listenPort);
	}

	
}
