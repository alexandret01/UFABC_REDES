package br.ufabc.controlplane.util.log;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class GMPLSLog { 
	private static Logger logger;  

	static {  
		logger = Logger.getLogger("");  

		try {  
			String filename =   
				"/home/vamorim/log.txt";  


			FileHandler fileHandler = new FileHandler(filename);  
			fileHandler.setFormatter(new SimpleFormatter());  

			logger.addHandler(fileHandler);  
			logger.setLevel(Level.ALL);  
		}  
		catch (IOException exIO) {  
			exIO.printStackTrace();  
			System.exit(1);  
		}  
	}  

	public static Logger getLogger() {  
		return logger;  
	}  

}
