package ccs.project.finalproj.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Util {
	
	/*private static Map<String, Integer> mappings = new HashMap<String, Integer>();
	
	static {
		mappings.put("pSensor", 91);
		mappings.put("bSensor", 92);
		mappings.put("dSensor", 93);
		mappings.put("mwOven", 94);
		mappings.put("toaster", 95);
		mappings.put("stove", 96);
	}*/

	public static Calendar convertFloatToTime(float fl){
		try{
			String s = Float.toString(fl);
			int hour = Integer.parseInt(s.split("\\.")[0]);
			int minute = Integer.parseInt(s.split("\\.")[1]);
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, hour);
			cal.set(Calendar.MINUTE, minute);
			cal.set(Calendar.SECOND, 0);
			return cal;
		} catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static int getTimeDifferenceinMinutes(float f1, float f2){
		int ret = 0;
		Calendar cal1 = convertFloatToTime(f1);
		Calendar cal2 = convertFloatToTime(f2);
		long diffMillis = Math.abs(cal1.getTimeInMillis() - cal2.getTimeInMillis());
		ret = (int)diffMillis/(1000*60);
		return ret;
	}
	
	
	public static Calendar convertStringToTime(String s){
		try{
			int hour = Integer.parseInt(s.split(":")[0]);
			int minute = Integer.parseInt(s.split(":")[1]);
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, hour);
			cal.set(Calendar.MINUTE, minute);
			cal.set(Calendar.SECOND, 0);
			return cal;
		} catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static int getTimeDifferenceinMinutes(String f1, String f2){
		int ret = 0;
		//log(f1, f2);
		Calendar cal1 = convertStringToTime(f1);
		Calendar cal2 = convertStringToTime(f2);
		long diffMillis = Math.abs(cal1.getTimeInMillis() - cal2.getTimeInMillis());
		ret = (int)diffMillis/(1000*60);
		//log(ret);
		return ret;
	}
	
	public static List<float []> readAndFormatFloatData(String inputFilePath){
		BufferedReader br = null;
		try {
			String currentLine;
			String [] inputArray;
			List<float []> outputArrayList = new ArrayList<float[]>(); 
			br = new BufferedReader(new FileReader(inputFilePath));
			while ((currentLine = br.readLine()) != null  && currentLine.trim().length() != 0) {
				inputArray = currentLine.split(",");
				float [] outputArray = new float [inputArray.length];
				int i = 0;
				for(String s : inputArray){
					//String sensor = s.split("-")[0];
					String time = s.split("-")[1];
					outputArray[i] = Float.parseFloat(time);
					i++;
				}
				outputArrayList.add(outputArray);
			}
			return outputArrayList;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}
	
	public static List<String[]> readAndFormatStringData(String inputFilePath) {
		BufferedReader br = null;
		try {
			String currentLine;
			String[] inputArray;
			List<String[]> outputArrayList = new ArrayList<String[]>();
			br = new BufferedReader(new FileReader(inputFilePath));
			while ((currentLine = br.readLine()) != null
					&& currentLine.trim().length() != 0) {
				inputArray = currentLine.split(",");
				String[] outputArray = new String[inputArray.length];
				int i = 0;
				for (String s : inputArray) {
					// String sensor = s.split("-")[0];
					String time = s.split("-")[1];
					outputArray[i] = time;
					i++;
				}
				outputArrayList.add(outputArray);
			}
			return outputArrayList;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}
	
	static void log(Object... obj) {
		for (Object o : obj) {
			System.out.println(o);
		}
	}
}
