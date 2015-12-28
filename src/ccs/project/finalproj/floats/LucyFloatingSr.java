package ccs.project.finalproj.floats;

import java.util.List;

import dmonner.xlbp.Network;
import dmonner.xlbp.UniformWeightInitializer;
import dmonner.xlbp.WeightUpdaterType;
import dmonner.xlbp.compound.InputCompound;
import dmonner.xlbp.compound.LinearTargetCompound;
import dmonner.xlbp.compound.MemoryCellCompound;

public class LucyFloatingSr {

	private String networkName = "LucySrIOTDoubleMemoryLayer";
	
	public Network buildNetwork() {
		try{
			final String mcType = "IOT";
			final int inSize = 1;
			final int memSize = 40;
			final int outSize = 1;
			final InputCompound in = new InputCompound("Input", inSize);
			final MemoryCellCompound mc = new MemoryCellCompound("Mem", memSize, mcType);
			final MemoryCellCompound mc2 = new MemoryCellCompound("Mem2", memSize, mcType);
			//final XEntropyTargetCompound out = new XEntropyTargetCompound("Ans", outSize);
			final LinearTargetCompound out = new LinearTargetCompound("Ans", outSize);
			out.addUpstreamWeights(mc2);
			mc2.addUpstreamWeights(mc);
			mc.addUpstreamWeights(in);
			final Network net = new Network(networkName);
			net.setWeightUpdaterType(WeightUpdaterType.basic(0.1F));
			//net.setWeightUpdaterType(WeightUpdaterType.momentum(0.1f, 0.9f));
			net.setWeightInitializer(new UniformWeightInitializer(1.0F, -0.1F, 0.1F));
			net.add(in);
			net.add(mc);
			net.add(mc2);
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

	public Network trainNet(Network net, List<float []> trainingData) {
		int epochs = 100;
		int trials = trainingData.size();
		int steps = trainingData.get(0).length-1;
		// for each epoch of training
		for (int e = 0; e < epochs; e++) {
			// for each trial in the data set
			for (int t = 0; t < trials; t++) {
				//net.clear();
				// for each sequential step in the trial
				for (int s = 0; s < steps; s++) {
					// INPUT
					// Put the input pattern on the InputCompound; activate the
					// Network and update each unit’s
					// weight-update "eligibility" in preparation for training.
					float inputToStep = trainingData.get(t)[s];
					net.getInputLayer().setInput(new float[] {inputToStep});
					net.activateTrain();
					net.updateEligibilities();
					// OUTPUT
					// Tell the output units what target pattern they should
					// expect to see; then have the Network
					// work backwards from the outputs, calculating each unit’s
					// error responsibility; then, update
					// the weights on all connections.
					float targetToStep = -1;
					//if(s!=steps-1){
						targetToStep = trainingData.get(t)[s+1];
					//}
					net.getTargetLayer().setTarget(new float[] {targetToStep});
					net.updateResponsibilities();
					net.updateWeights();
				}
			}
		}
		return net;
	}
	
	public void evaluate(Network net, List<float []> testData){
		int trials = testData.size();
		int steps = testData.get(0).length-1;
		// for each trial in the data set
		//BitStat bitStat = new BitStat();
		for (int t = 0; t < trials; t++) {
			//net.clear();
			// for each sequential step in the trial
			for (int s = 0; s < steps; s++) {
				// INPUT
				float inputToStep = testData.get(t)[s];
				float [] networkInput = new float [] {inputToStep};
				net.getInputLayer().setInput(networkInput);
				net.activateTest();
				// OUTPUT ANALYSIS
				// Determine here whether the Network has produced the correct
				// answer by comparing the target
				// output pattern with the Network’s output pattern; 
				float[] networkOutput = net.getTargetLayer().getActivations();
				for(float f : networkOutput){
					log("Input: " + inputToStep + "," + " Expected: " + testData.get(t)[s+1] + " Got: " + String.format("%.3g", f));
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
