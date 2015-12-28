package ccs.project.finalproj.floats;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

import ccs.project.finalproj.util.Util;
import dmonner.xlbp.Network;
import dmonner.xlbp.NetworkDotBuilder;

public class GatewayToFloatingLucyLand {
	
	private static String dotFileName = "dotFile";
	private static String trainData = "trainFloat.txt";
	private static String testData = "testFloat.txt";
	private static String testCommand = "t";
	private int firstTolerance = 3;
	private int secondTolerance = 7;
	private int thirdTolerance = 7;
	private int totalTrialsInTest = -1;
	private int jrCorrect = 0;
	private int srCorrect = 0;

	public void process(String arg) {
		try{
			List<float[]> trainingData = Util.readAndFormatFloatData(trainData);
			List<float[]> testingData = Util.readAndFormatFloatData(testData);
			GatewayToFloatingLucyLand lucyLand = new GatewayToFloatingLucyLand();
			Network lucySr = lucyLand.bringLucySrToLife(trainingData, testingData);
			Network lucyJr = lucyLand.bringLucyJrToLife(trainingData);
			//Read input from command line
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		    String input = null;
		    if(arg.equalsIgnoreCase(testCommand)){
		    	log("Testing please wait....");
		    	Network lucy = lucyLand.pickLucy(lucySr,lucyJr,testingData);
		    	log("Picking " + lucy.getName() + " for prediction");
		    	log("Enter time in dd.dd format (24 hr format). Example: if it's 6:45 AM, enter 6.45 , if it's 7:02 AM enter 7.02 , if its 9:45 PM enter 21.45");
			    while ((input = br.readLine()) != null && input.trim().length() != 0) {
			    	float data = Float.parseFloat(input);
					float lucySays = getLucyToWork(lucy, data);
					log("Lucy says next sensor activates at : " + String.format("%.3g", lucySays));
			    } 
		    } else {
		    	log("Enter time in dd.dd format (24 hr format). Example: if it's 6:45 AM, enter 6.45 , if it's 7:02 AM enter 7.02 , if its 9:45 PM enter 21.45");
			    while ((input = br.readLine()) != null && input.trim().length() != 0) {
			    	float data = Float.parseFloat(input);
			    	float jrSays = getLucyToWork(lucyJr, data);
					float srSays = getLucyToWork(lucySr, data);
					log("Lucy Jr. says next sensor activates at : " + String.format("%.3g", jrSays) + " but Lucy Sr. says next sensor activates at : " +
							String.format("%.3g", srSays));
			    } 
		    }
		} catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	private Network pickLucy(Network lucySr, Network lucyJr, List<float[]> testingData) {
		try{
			totalTrialsInTest = testingData.size()*3;
			for(float [] data : testingData){
		    	for(int i=0;i<data.length-1;i++){
		    		float fl = data[i];
		    		float jrSays = getLucyToWork(lucyJr, fl);
					float srSays = getLucyToWork(lucySr, fl);
					//log("Input : " + fl + " Lucy Jr. says : " +  String.format("%.3g", jrSays) + " but Lucy Sr. says : " +  String.format("%.3g", srSays)
						//	+ " and expected output is : " + data[i+1]);
					int jrDiff = Util.getTimeDifferenceinMinutes(fl, new Float(String.format("%.3g", jrSays)));
					int srDiff = Util.getTimeDifferenceinMinutes(fl, new Float(String.format("%.3g", srSays)));
					int corrTimeDiff = Util.getTimeDifferenceinMinutes(fl, data[i+1]);
					if(Math.abs(corrTimeDiff-jrDiff) <= (i==0?firstTolerance:(i==1)?secondTolerance:thirdTolerance)){
						//log(i+","+corrTimeDiff + "," + jrDiff);
						jrCorrect++;
					}
					if(Math.abs(corrTimeDiff-srDiff) <= (i==0?firstTolerance:(i==1)?secondTolerance:thirdTolerance)){
						//log(i+","+corrTimeDiff + "," + srDiff);
						srCorrect++;
					}
					//log(jrDiff + "," + srDiff + "," + corrTimeDiff);
					//Thread.sleep(1000);
		    	}
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

	private static float getLucyToWork(Network lucy, float data) {
		lucy.getInputLayer().setInput(new float [] {data});
		lucy.activateTest();
		float prediction = lucy.getTargetLayer().getActivations()[0];
		return prediction;
	}
	
	private Network bringLucyJrToLife(List<float[]> trainingData) {
		Network net = LucyFloatingJr.buildAndTrainNetwork(trainingData);
		//visualizeNetwork(net);
		return net;
	}
	
	private Network bringLucySrToLife(List<float[]> trainingData, List<float[]> testingData) {
		LucyFloatingSr lucySr = new LucyFloatingSr();
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
