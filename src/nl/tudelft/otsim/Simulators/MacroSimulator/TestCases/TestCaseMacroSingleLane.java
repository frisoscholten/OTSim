package nl.tudelft.otsim.Simulators.MacroSimulator.TestCases;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import Jama.Matrix;
import nl.tudelft.otsim.Events.Scheduler;
import nl.tudelft.otsim.GUI.FakeGraphicsPanel;
import nl.tudelft.otsim.Simulators.MacroSimulator.MacroCell;
import nl.tudelft.otsim.Simulators.MacroSimulator.MacroSimulator;
import nl.tudelft.otsim.Simulators.MacroSimulator.Model;
import nl.tudelft.otsim.Simulators.MacroSimulator.Nodes.Node;

public class TestCaseMacroSingleLane {

	public static void main(String[] args) {
		double[] vfree = new double[]{130/3.6,130/3.6};
		String networkConfiguration = "EndTime:	3600.00\nSeed:	1\nRouteBased:\tfalse\n"
				+ "Roadway:	0	from	1	to	2	speedlimit	"+(vfree[0]*3.6)+"	lanes	2	vertices	(0.000,-0.250,0.000)	(2000.000,-0.250,0.000)	ins	outs	1\n"
				+ "Roadway:	1	from	2	to	3	speedlimit	"+(vfree[0]*3.6)+"	lanes	2	vertices	(2000.000,-0.250,0.000)	(3000.000,-0.250,0.000)	ins	0	outs	2\n"
				+ "Roadway:	2	from	3	to	4	speedlimit	"+(vfree[1]*3.6)+"	lanes	1	vertices	(3000.000,-0.250,0.000)	(3500.000,-0.250,0.000)	ins	1	outs\n";
		double[] inflow = new double[]{1500.0,3000.0,1500.0};
		double[] moment = new double[]{300,1200};
		String inflows = "Inflow:	1	[0.000/"+inflow[0]+":"+moment[0]+"/"+inflow[0]+":"+(moment[0]+1)+"/"+(inflow[1])+":"+(moment[1])+"/"+inflow[1]+":"+(moment[1]+1)+"/"+inflow[2]+":3600.0/"+inflow[2]+"]\n";
		double[] fd1 = new double[]{0.025,0.125,22.2222};
		double[] fd2 = new double[]{0.025,0.125,22.2222};
		String FDs = "FD:	0\t"+fd1[0]+"\t"+fd1[1]+"\t"+fd1[2]+"\tSMULDERS\n"
				+  "FD:	1\t"+fd1[0]+"\t"+fd1[1]+"\t"+fd1[2]+"\tSMULDERS\n"
				+ "FD:	2\t"+fd2[0]+"\t"+fd2[1]+"\t"+fd2[2]+"\tSMULDERS\n";
		String configuration = networkConfiguration+inflows+FDs;

		Scheduler scheduler = new Scheduler(MacroSimulator.simulatorType, new FakeGraphicsPanel(), configuration);
		Model macromodel = (Model) scheduler.getSimulator().getModel();
		macromodel.init();
		//macromodel.
		//System.out.println(Arrays.toString(macromodel.saveStateToArray()));
		String output = "";
		String output2;
		String output2b;
		String output2c;
		PrintWriter out;
		try {
			out = new PrintWriter("testSingleLane.m");
			out.println("clearvars -except i res;close all;");
			double endTime= 3600;
			double dt = 2;
			out.println("tijd = 0:"+dt+":"+endTime+";");
			for (double i = 0; i<=endTime/dt; i++) {

				scheduler.stepUpTo(i*dt);
				Matrix[] res = AssimilationConfiguration.getOutput(macromodel, new StateDefinition[]{StateDefinition.K_CELL, StateDefinition.V_CELL, StateDefinition.Q_CELL});
				output2 = "outputX("+(i+1)+",:)="+Arrays.toString(res[0].transpose().getArray()[0])+";\n";
				output2b = "outputV("+(i+1)+",:)="+Arrays.toString(res[1].transpose().getArray()[0])+";\n";
				output2c = "outputQ("+(i+1)+",:)="+Arrays.toString(res[2].transpose().getArray()[0])+";\n";
				MacroCell m = macromodel.getCells().get(macromodel.getCells().size()-1);
				System.out.println("cell m: " + m.KCell +" & " + m.kCri +" & " + m.kJam +" & " + m.vLim +" & " + m.vCri);
				System.out.println("2: " + m.VCell +" & " +m.QCell +" & " +m.qCap +" & " +m.Demand +" & " +m.Supply);
				System.out.println(m.FluxIn  +" & " +m.FluxOut);
				//System.out.println(m.QCell - m.calcQ(m.KCell));
				output = output+output2+output2b+output2c;
				out.print(output2+output2b+output2c);
			}

			ArrayList<MacroCell> cells = macromodel.getCells();
			java.util.ArrayList<Node> nodes = macromodel.getNodes();
			MacroCell ref = null;
			for (Node n: nodes) {
				if (n.cellsIn.isEmpty()) {
					ref = n.cellsOut.get(0);
				}
			}
			String output3;
			double[] cellLengths2 = new double[macromodel.getCells().size()];
			for (MacroCell c: macromodel.getCells()) {
				cellLengths2[macromodel.getCells().indexOf(c)] = c.l;
			}
			//for (int j=0; j<cells.size(); j++) {
			out.println("l=("+Arrays.toString(cellLengths2)+");");
			out.println("loc=cumsum(l);");
			//}

			out.println("imagesc(tijd,loc,outputX');");
			out.println("colorbar");
			out.println("colormap(hot);figure;");
			out.println("imagesc(tijd,loc,outputV');");
			out.println("colorbar");
			out.println("colormap(hot);figure;");
			out.println("imagesc(tijd,loc,outputQ');");
			out.println("colorbar");
			out.println("colormap(hot);");
			out.println("[a,b] = find((outputX>0.1));");
			out.println("t1 = tijd(min(a(b==max(b))));");
			out.println("x1 =  loc(max(b));");
			out.println("t2 = tijd(max(a(b==max(b))));");
			out.println("x2 =  loc(max(b));");
			out.println("t3a = tijd(min(a(b==min(b))));");
			out.println("t3b = tijd(max(a(b==min(b))));");
			out.println("x3 =  loc(min(b));");
			out.println("v1 =  (x3-x1)/(t3a-t1);");
			out.println("v2 =  (x3-x2)/(t3b-t2);");
			out.println("tfd1=0:0.0001:"+(fd1[1]*2)+";");
			out.println("tfd2=0:0.0001:"+fd2[1]+";");
			out.println("fd1 = smulders(tfd1,"+(fd1[0]*2)+","+(fd1[1]*2)+","+vfree[0]+","+fd1[2]+");");
			out.println("fd2 = smulders(tfd2,"+fd2[0]+","+fd2[1]+","+vfree[1]+","+fd2[2]+");");
			
			out.println("plot(tfd1,fd1,tfd2,fd2);");
			out.println("tq = find(abs(fd1 - max(fd2)) == min(abs(fd1 - max(fd2))));");
			out.println("qj = fd1(tq); kj = tfd1(tq);");
			out.println("tq1 = find(abs(fd1(tfd1<"+(fd1[0]*2)+") - "+(inflow[1])+"/3600) == min(abs(fd1(tfd1<"+(fd1[0]*2)+") - "+(inflow[1])+"/3600)));");
			out.println("tq2 = find(abs(fd1(tfd1<"+(fd1[0]*2)+") - "+(inflow[2])+"/3600) == min(abs(fd1(tfd1<"+(fd1[0]*2)+") - "+(inflow[2])+"/3600)));");
			out.println("q1 = fd1(tq1); k1 = tfd1(tq1);");
			out.println("q2 = fd1(tq2); k2 = tfd1(tq2);");
			out.println("v1s = (q1-qj)/(k1-kj);");
			out.println("v2s = (q2-qj)/(k2-kj);");
			out.println("if (exist('i','var') == 0) ");
			out.println("    i = 1;");
			out.println("end");
			out.println("res(i,:) = [v1,v1s,v2,v2s,"+Arrays.toString(inflow)+","+Arrays.toString(fd1)+","+Arrays.toString(fd2)+"];");
			out.println("i=i+1;");

			out.close();
			//System.out.println(output);
			System.out.println("klaar");

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

}
