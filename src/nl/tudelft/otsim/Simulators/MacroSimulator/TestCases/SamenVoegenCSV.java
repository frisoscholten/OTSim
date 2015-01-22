package nl.tudelft.otsim.Simulators.MacroSimulator.TestCases;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class SamenVoegenCSV {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Path path = FileSystems.getDefault().getPath("C:\\Users\\Friso\\Documents\\PilotDatafusie", "intensity.csv");
		Path path2 = FileSystems.getDefault().getPath("C:\\Users\\Friso\\Documents\\PilotDatafusie", "speed.csv");

		try {
			String intensity = new String(Files.readAllBytes(path), "UTF-8");
			String speed = new String(Files.readAllBytes(path2), "UTF-8");
			
			
			String[] splitFlow = intensity.split("\n");
			
			String[][] fieldsFlow = new String[1566][48];
			for (int i = 0; i<splitFlow.length; i++) {
				fieldsFlow[i] = (splitFlow[i].trim()).split(",");
				
			}
			String[] splitSpeed = speed.split("\n");
			String[][] fieldsSpeed = new String[1566][48];
			for (int i = 0; i<splitSpeed.length; i++) {
				fieldsSpeed[i] = (splitSpeed[i].trim()).split(",");
			}
			
			String detector = "";
			PrintWriter out2 = new PrintWriter("C:\\Users\\Friso\\Documents\\PilotDatafusie\\0.csv");;
			for (int j = 1; j< 1566; j++) {
				String ID = fieldsFlow[j][0]+"."+fieldsFlow[j][28];
				if (!ID.equals(detector)) {
					out2.close();
					out2 = new PrintWriter("C:\\Users\\Friso\\Documents\\PilotDatafusie\\"+ID+".csv");
					out2.println("DetectorName,PeriodStart,PeriodEnd,avgVehicleFlow,avgVehicleSpeed,dataError");
				}
				detector = ID;
				out2.println(ID+","+fieldsFlow[j][3]+","+fieldsFlow[j][4] +","+fieldsFlow[j][16] +","+fieldsSpeed[j][17] +","+fieldsFlow[j][14]);
				
			}
			out2.close();
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
