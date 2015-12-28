package ccs.project.finalproj.string;

import java.util.List;

import ccs.project.finalproj.util.Util;
import dmonner.xlbp.Network;
import dmonner.xlbp.UniformWeightInitializer;
import dmonner.xlbp.WeightUpdaterType;
import dmonner.xlbp.compound.InputCompound;
import dmonner.xlbp.compound.LinearTargetCompound;
import dmonner.xlbp.compound.MemoryCellCompound;

public class LucyStringSr {

	private String networkName = "LucySrIOTSingleMemoryLayer";
	
	public Network buildNetwork() {
		try{
			final String mcType = "IOT";
			final int inSize = 1;
			final int memSize = 30;
			final int outSize = 1;
			final InputCompound in = new InputCompound("Input", inSize);
			final MemoryCellCompound mc = new MemoryCellCompound("Mem", memSize, mcType);
			final MemoryCellCompound mc2 = new MemoryCellCompound("Mem2", memSize, mcType);
			//final XEntropyTargetCompound out = new XEntropyTargetCompound("Ans", outSize);
			final LinearTargetCompound out = new LinearTargetCompound("Ans", outSize);
			out.addUpstreamWeights(mc2);
			mc2.addUpstreamWeights(mc);
			mc.addUpstreamWeights(in);
			out.addUpstreamWeights(mc);
			mc.addUpstreamWeights(in);
			final Network net = new Network(networkName);
			//net.setWeightUpdaterType(WeightUpdaterType.basic(0.1F));
			net.setWeightUpdaterType(WeightUpdaterType.momentum(0.1f, 0.9f));
			net.setWeightInitializer(new UniformWeightInitializer(1.0F, -0.1F, 0.1F));
			net.add(in);
			net.add(mc);
			//net.add(mc2);
			net.add(out);
			net.optimize();
			net.build();
			//log(net.toString("NAWLIXECS"));
			return net;
		} catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public Network trainNet(Network net, List<String []> trainingData) {
	    int epochs = 500;
		int trials = trainingData.size();
		//int steps = trainingData.get(0).length-1;
		int steps = 1;
		// for each epoch of training
		for (int e = 0; e < epochs; e++) {
			// for each trial in the data set
			for (int t = 0; t < trials; t++) {
				//net.clear();
				// for each sequential step in the trial
				for (int s = 0; s < steps; s++) {
					// Put the input pattern on the InputCompound; activate the
					// Network and update each unit’s
					// weight-update "eligibility" in preparation for training.
					String inputToStep = trainingData.get(t)[s];
					float in = Util.convertStringToTime(inputToStep).getTimeInMillis();
					net.getInputLayer().setInput(new float[] {in});
					net.activateTrain();
					net.updateEligibilities();
					// OUTPUT
					// Tell the output units what target pattern they should
					// expect to see; then have the Network
					// work backwards from the outputs, calculating each unit’s
					// error responsibility; then, update
					// the weights on all connections.
					String targetToStep = trainingData.get(t)[s+1];
					float tgt = Util.convertStringToTime(targetToStep).getTimeInMillis();
					//int timeDiff = Util.getTimeDifferenceinMinutes(inputToStep, targetToStep);
					//log(timeDiff);
					net.getTargetLayer().setTarget(new float[] {tgt});
					net.updateResponsibilities();
					net.updateWeights();
				}
			}
		}
		return net;
	}
	
	public void evaluate(Network net, List<String []> testData){
		int trials = testData.size();
		//int steps = testData.get(0).length-1;
		int steps = 1;
		// for each trial in the data set
		//BitStat bitStat = new BitStat();
		for (int t = 0; t < trials; t++) {
			//net.clear();
			// for each sequential step in the trial
			for (int s = 0; s < steps; s++) {
				String inputToStep = testData.get(t)[s];
				float in = Util.convertStringToTime(inputToStep).getTimeInMillis();
				float [] networkInput = new float [] {in};
				net.getInputLayer().setInput(networkInput);
				net.activateTest();
				// Determine here whether the Network has produced the correct
				// answer by comparing the target
				// output pattern with the Network’s output pattern; 
				float[] networkOutput = net.getTargetLayer().getActivations();
				for(float f : networkOutput){
					log(inputToStep + "," + f);
				}
				//bitStat.compareExact(networkInput, networkOutput);
			}
		}
		//bitStat.analyze();
	}
	
	static void log(Object... obj) {
		for (Object o : obj) {
			System.out.println(o);
		}
	}
}
