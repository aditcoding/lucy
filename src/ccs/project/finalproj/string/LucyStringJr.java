package ccs.project.finalproj.string;

import java.util.List;

import ccs.project.finalproj.util.Util;
import dmonner.xlbp.Network;
import dmonner.xlbp.UniformWeightInitializer;
import dmonner.xlbp.WeightUpdaterType;
import dmonner.xlbp.compound.InputCompound;
import dmonner.xlbp.compound.LinearTargetCompound;
import dmonner.xlbp.compound.MemoryCellCompound;
import dmonner.xlbp.stat.TestStat;
import dmonner.xlbp.trial.Step;
import dmonner.xlbp.trial.Trainer;
import dmonner.xlbp.trial.Trial;
import dmonner.xlbp.trial.TrialStreamAdapter;

public class LucyStringJr extends TrialStreamAdapter {

	private static final int trialsPerEpoch = 10;
	private List<String[]> trainingData;
	private int counter= 0;
	private static String networkName = "LucyJrIFOPSingleMemoryLayer";
	
	public LucyStringJr(final Network net, List<String[]> trainingData) {
		super("Lucy Jr.", net);
		this.trainingData = trainingData;
	}

	public static Network buildAndTrainNetwork(List<String[]> trainingData) {
		// The number of epochs to allow the network to train.
		final int epochs = 500;
		// Describes the memory cell type.
		// Define three layers -- Input, Memory Cells, Output
		final String memType = "IFOP";
		final int inSize = 1;
		final int memSize = 20;
		final int outSize = 1;
		final InputCompound input = new InputCompound("Input", inSize);
		final MemoryCellCompound mem = new MemoryCellCompound("Mem", memSize, memType);
		//final XEntropyTargetCompound ans = new XEntropyTargetCompound("Ans", 1);
		final LinearTargetCompound ans = new LinearTargetCompound("Ans", outSize);
		// Add weight matrices connecting Input=>Mem and Mem=>Ans.
		ans.addUpstreamWeights(mem);
		mem.addUpstreamWeights(input);
		// Create a new Network to learn the task.
		final Network net = new Network(networkName);
		// Set the learning rate
		net.setWeightUpdaterType(WeightUpdaterType.basic(0.01F));
		//net.setWeightUpdaterType(WeightUpdaterType.momentum(0.1f, 0.9f));
		net.setWeightInitializer(new UniformWeightInitializer(1.0F, -0.1F, 0.1F));
		//Add to network in order of activation
		net.add(input);
		net.add(mem);
		net.add(ans);
		final LucyStringJr task = new LucyStringJr(net, trainingData);
		// Create a new Trainer to train the Network on the task.
		Trainer trainer = new Trainer(net, task) {
			// Override the Trainer's hook method postEpoch to print the per-step accuracy.
			@Override
			public void postEpoch(final int ep, final TestStat stat) {
				//log(ep + ":\t" + stat.getLastTrain().getStepStats().getFraction());
			}
		};
		// Train the Network on the task for the specified number of trials.
		final TestStat result = trainer.run(epochs);
		//log(net.toString("NA"));
		//log(result);
		return net;
	}

	@Override
	public Trial nextTrainTrial() {
		final Trial trial = new Trial(getMetaNetwork());
		//int steps = trainingData.get(0).length-1;
		int steps = 1;
		for (int i = 0; i < steps ; i++) {
			final Step step = trial.nextStep();
			String s = trainingData.get(counter)[i];
			float in = Util.convertStringToTime(s).getTimeInMillis();
			step.addInput(new float[] {in});
			s = trainingData.get(counter)[i+1];
			float tgt = Util.convertStringToTime(s).getTimeInMillis();
			step.addTarget(new float[] {tgt});
		}
		if(counter == trainingData.size()-1){
			counter = -1;
		}
		counter++;
		return trial;
	}

	@Override
	public int nTrainTrials() {
		return trialsPerEpoch;
	}
	
	static void log(Object... obj) {
		for (Object o : obj) {
			System.out.println(o);
		}
	}
}
