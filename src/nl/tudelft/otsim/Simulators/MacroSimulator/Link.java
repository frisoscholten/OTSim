package nl.tudelft.otsim.Simulators.MacroSimulator;

import java.util.ArrayList;

public class Link {
	public double vLim;
	public double kJam;
	public double kCri;
	public double vCri;
	public int lanes;
	public ArrayList<MacroCell> correspondingCells = new ArrayList<MacroCell>();
	
	public Link(MacroCell mc) {
		this.vLim = mc.vLim;
		this.kJam = mc.kJam;
		this.kCri = mc.kCri;
		this.vCri = mc.vCri;
		this.lanes = mc.lanes;
		if (this.kJam > 2) {
			System.out.println("fout");
		}
	}
	public void setCells(ArrayList<MacroCell> list) {
		this.correspondingCells = list;
		for (MacroCell m: list) {
			m.link = this;
		}
	}
	public void updateVars() {
		MacroCell mc = correspondingCells.get(0);
		this.vLim = mc.vLim;
		this.kJam = mc.kJam;
		this.kCri = mc.kCri;
		this.vCri = mc.vCri;
		this.lanes = mc.lanes;
		if (this.kJam > 2) {
			throw new Error("fout");
		}
	}
	public void setKJam(double max) {
		
		this.kJam = max;
		if (this.kJam > 2) {
			System.out.println("fout");
		}
		for (MacroCell mc: this.correspondingCells)
			mc.kJam = max;
		
	}
	public void setVLim(double max) {
		this.vLim = max;
		for (MacroCell mc: this.correspondingCells)
			mc.setVLim(max);
	}
	public void setKCri(double max) {
		this.kCri = max;
		for (MacroCell mc: this.correspondingCells)
			mc.kCri = max;
	}
	public void setVCri(double max) {
		this.vCri = max;
		for (MacroCell mc: this.correspondingCells)
			mc.vCri = max;
	}
}
