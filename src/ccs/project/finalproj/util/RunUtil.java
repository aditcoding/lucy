package ccs.project.finalproj.util;

import ccs.project.finalproj.floats.GatewayToFloatingLucyLand;
import ccs.project.finalproj.string.GatewayToStringLucyLand;

public class RunUtil {
	
	private static String strType = "s";
	private static String fltType = "f";
	private static String testType = "t";

	public static void main(String[] args) {
		//st,ft,s,f,t,sft
		log("Building Lucy. Please wait....");
		String arg = "";
		if(args == null || args.length == 0 || args.length > 1){
			arg = fltType + testType;
		} else {
			arg = args[0];
		}
		if(arg.contains(fltType) || arg.equalsIgnoreCase(testType)){
			GatewayToFloatingLucyLand gFltLucyLand = new GatewayToFloatingLucyLand();
			if(arg.contains(testType)){
				gFltLucyLand.process(testType);
			} else {
				gFltLucyLand.process("");
			}
		}
		else if(arg.contains(strType)){
			GatewayToStringLucyLand gStrLucyLand = new GatewayToStringLucyLand();
			if(arg.contains(testType)){
				gStrLucyLand.process(testType);
			} else {
				gStrLucyLand.process("");
			}
			
		}
	}
	
	static void log(Object... obj) {
		for (Object o : obj) {
			System.out.println(o);
		}
	}
}
