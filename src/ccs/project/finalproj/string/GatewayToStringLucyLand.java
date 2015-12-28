package ccs.project.finalproj.string;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import ccs.project.finalproj.util.Util;
import dmonner.xlbp.Network;
import dmonner.xlbp.NetworkDotBuilder;

public class GatewayToStringLucyLand {
	
	private static String dotFileName = "dotFile";
	private static String trainData = "train.txt";
	private static String testData = "test.txt";
	private static String testCommand = "t";
	private int tolerance = 3;
	private int totalTrialsInTest = -1;
	private int jrCorrect = 0;
	private int srCorrect = 0;
	
	public void process(String arg) {
		try{
			List<String[]> trainingData = Util.readAndFormatStringData(trainData);
			List<String[]> testingData = Util.readAndFormatStringData(testData);
			GatewayToStringLucyLand lucyLand = new GatewayToStringLucyLand();
			Network lucySr = lucyLand.bringLucySrToLife(trainingData, testingData);
			Network lucyJr = lucyLand.bringLucyJrToLife(trainingData);
			//Read input from command line
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		    String input = null;
		    if(arg.equalsIgnoreCase(testCommand)){
		    	log("Testing please wait....");
		    	Network lucy = lucyLand.pickLucy(lucySr,lucyJr,testingData);
		    	log("Picking " + lucy.getName() + " for prediction");
		    	log("Enter time when pressure sensor is activated in dd:dd format (24 hr format). Example: if it's 6:45 AM, enter 6:45 , if it's 7:02 AM enter 7:02 , if its 9:45 PM enter 21:45");
			    while ((input = br.readLine()) != null && input.trim().length() != 0) {
			    	String lucySays = getLucyToWork(lucy, input);
			    	log("Lucy says next sensor activates at : " + lucySays);
			    }
		    } else {
		    	log("Enter time when pressure sensor is activated in dd:dd format (24 hr format). Example: if it's 6:45 AM, enter 6:45 , if it's 7:02 AM enter 7:02 , if its 9:45 PM enter 21:45");
			    while ((input = br.readLine()) != null && input.trim().length() != 0) {
			    	String jrSays = getLucyToWork(lucyJr, input);
			    	String srSays = getLucyToWork(lucySr, input);
					log("Lucy Jr. says : " + jrSays + " but Lucy Sr. says : " + srSays);
			    }
		    }
		} catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	private Network pickLucy(Network lucySr, Network lucyJr,
			List<String[]> testingData) {
		try{
			totalTrialsInTest = testingData.size();
			for(String [] data : testingData){
		    	//for(String s : data){
		    		String s = data[0];
		    		String jrSays = getLucyToWork(lucyJr, s);
					String srSays = getLucyToWork(lucySr, s);
					//log("Input : " + s + " Lucy Jr. says : " + jrSays + " but Lucy Sr. says : " + srSays
						//	 + " and expected output is : " + data[1]);
					int jrDiff = Util.getTimeDifferenceinMinutes(s,jrSays);
					int srDiff = Util.getTimeDifferenceinMinutes(s,srSays);
					int corrTimeDiff = Util.getTimeDifferenceinMinutes(s, data[1]);
					if(Math.abs(corrTimeDiff-jrDiff) <= tolerance){
						//log(i+","+corrTimeDiff + "," + jrDiff);
						jrCorrect++;
					}
					if(Math.abs(corrTimeDiff-srDiff) <= tolerance){
						//log(i+","+corrTimeDiff + "," + srDiff);
						srCorrect++;
					}
					//Thread.sleep(1000);
		    	//}
		    }
			log("Total : " + totalTrialsInTest + ", " + "Lucy Jr. Correct : " + jrCorrect + ", " + "Lucy Sr. Correct : " + srCorrect);
			float srAccuracy = ((float)srCorrect/totalTrialsInTest)*100;
			float jrAccuracy = ((float)jrCorrect/totalTrialsInTest)*100;
			log("Lucy Sr. Accuracy : " + srAccuracy + ", Lucy Jr. Accuracy : " + jrAccuracy);
			return srAccuracy>=jrAccuracy?lucySr:lucyJr;
		} catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	private static String getLucyToWork(Network lucy, String data) {
		float fl = Util.convertStringToTime(data).getTimeInMillis();
		lucy.getInputLayer().setInput(new float [] {fl});
		lucy.activateTest();
		long prediction = (long)lucy.getTargetLayer().getActivations()[0];
		Date date = new Date(prediction);
		return date.getHours() + ":" + date.getMinutes();
	}

	private Network bringLucyJrToLife(List<String[]> trainingData) {
		Network net = LucyStringJr.buildAndTrainNetwork(trainingData);
		//visualizeNetwork(net);
		return net;
	}

	private Network bringLucySrToLife(List<String[]> trainingData, List<String[]> testingData) {
		LucyStringSr lucySr = new LucyStringSr();
		Network net = lucySr.buildNetwork();
		net = lucySr.trainNet(net, trainingData);
		//lucySr.evaluate(net, testingData); 
		//visualizeNetwork(net);
		return net;
	}

	private void visualizeNetwork(Network net) {
		PrintWriter outfile = null;
		try {
			outfile = new PrintWriter(dotFileName);
			outfile.println(new NetworkDotBuilder(net));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			outfile.close();
		}
	}

	static void log(Object... obj) {
		for (Object o : obj) {
			System.out.println(o);
		}
	}
}
