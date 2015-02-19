package nl.tudelft.otsim.Simulators.MacroSimulator;

import java.awt.Color;
//import java.awt.geom.Point2D;
//import java.awt.geom.Point2D.Double;









import nl.tudelft.otsim.Simulators.MacroSimulator.Model;
import nl.tudelft.otsim.Simulators.MacroSimulator.FundamentalDiagrams.IFD;
import nl.tudelft.otsim.Simulators.MacroSimulator.Nodes.Node;
import nl.tudelft.otsim.Simulators.MacroSimulator.Nodes.NodeBoundaryIn;
import nl.tudelft.otsim.Simulators.MacroSimulator.Nodes.NodeBoundaryOut;
import nl.tudelft.otsim.GUI.GraphicsPanel;







import java.util.ArrayList;
import java.util.Random;







import nl.tudelft.otsim.GeoObjects.Vertex;


/**
 * A single cell of road. Different cells are connected in the
 * longitudinal or lateral direction. The <tt>jMacroCell</tt> object also provides a
 * few network utilities to get traffic state information.
 * <br>
 * <br>
 */
public class MacroCell {
	// TODO: delete unnecessary variables and methods! 
	// TODO: merges and splits at nodes
	// TODO: boundary conditions
	
	
	private double width = 0;
	public ArrayList<Vertex> vertices = new ArrayList<Vertex>();
	//private int id;
	//private double speedLimit;
	public ArrayList<Integer> ins = new ArrayList<Integer>();
	public ArrayList<Integer> outs = new ArrayList<Integer>();
	
	// Geographical info
    /** Array of x-coordinates defining the lane curvature. */
    public double[] x;

    /** Array of y-coordinates defining the lane curvature. */
    public double[] y;

    /** Length of the lane [m]. */
    public double l;

    /** Main model. */
    final public Model model;

	/** ID of cell for user recognition. */
    public int id;

    /** Set of upstream cells in case of a merge. */
    public java.util.ArrayList<MacroCell> ups = new java.util.ArrayList<MacroCell>();

    /** Set of downstream cells in case of a split. */
    public java.util.ArrayList<MacroCell> downs = new java.util.ArrayList<MacroCell>();
    
    /** Set of upstream cells in case of a merge. */
    public java.util.ArrayList<Integer> upsInt = new java.util.ArrayList<Integer>();

    /** Set of downstream cells in case of a split. */
    public java.util.ArrayList<Integer> downsInt = new java.util.ArrayList<Integer>();

    /** Left cell (if any). for multi-lane modeling */
    public MacroCell left;

    /** Right cell (if any). */
    public MacroCell right;
    
    public Node nodeIn;
    
    public Node nodeOut;
    
    public int indexNodeIn;
    public int indexNodeOut;
    
    private int configNodeIn;
    private int configNodeOut;

    /** Destination number, NODESTINATION if no destination. */
    public int destination;

    /** Origin number, NOORIGIN if no origin. */
    public int origin;

    //Traffic states
    /** Flow in this cell. [veh/s] */
    public double QCell;
    
    /** Density in this cell. [veh/m] */
    public double KCell;
    
    /** Average spacing in this cell. [km or m] */
    //public double SCell;
    
    /** Average speed in this cell. [m/s] */
    public double VCell;
    
    /** Flux into this cell. [ veh/s] */
    public double FluxIn;
    
    /** Flux out from this cell. [veh/s] */
    public double FluxOut;
    
    /** Supply from this cell. [veh/s] */
    public double Supply;
    
    /** Demand out from this cell. [veh/s] */
    public double Demand;
    
    public double[] DemandTest;
    public double[] SupplyTest;
    public IFD fd;
    
    // Parameters    
    /** Legal speed limit [m/s]. */
    public double vLim = 120/3.6;
    
    /** Legal critical density [veh/m]. */
    public double kCri = 0.018;
    
    public double vCri = 80/3.6;
    public double vCriBeforeInit = 80/3.6;
    
    /** Legal jam density [veh/m]. */
    public double kJam = 0.125;
    
    public double kCriPerLane = 0.020;
    
    /** Legal jam density [veh/m]. */
    public double kJamPerLane = 0.125;
    
    /** Legal flow capacity [veh/m/lane]. */
    public double qCap;
    
    public double[] FluxIn2;
	public double[] FluxOut2;
	public int lanes;
	public boolean detector = false;
	public boolean selected = false;
	public Link link;
	
	
    
    /**
     * Constructor that will calculate the lane length from the x and y
     * coordinates.
     * @param x X coordinates of curvature.
     * @param y Y coordinates of curvature.
     * @param id User recognizable lane id.
     * @param model Main model.
     */
    public MacroCell(Model model, double[] x, double[] y, int id) {
        this.model = model;
        this.x = x;
        this.y = y;
        this.id = id;
        l = calcLength();
        
    }
    public MacroCell(Model model, double length, int id) {
        this.model = model;
        this.l = length;
        //this.x = {0,0};
        //this.y = {0,length};
        this.id = id;
             
    }
    public MacroCell(Model model) {
    	this.model = model;
    	this.l = calcLength();
    }
    public void init() {
    	lanes = (int) (width/3.5);
    	//kCri = kCriPerLane*lanes;
    	//kJam = kJamPerLane*lanes;
    	vCri = vCriBeforeInit;
    	recalculateFDparameters();
    	//qCap = fd.calcQCap(this);
    	KCell = 0;
    	QCell = calcQ(KCell);
    	VCell = calcV(KCell);
    	l = calcLength();
    	
    		
    	
    	indexNodeIn = nodeIn.cellsOut.indexOf(this);
    	indexNodeOut = nodeOut.cellsIn.indexOf(this);
    	
    }
    public void recalculateFDparameters() {
    	kCri = kCriPerLane*lanes;
    	kJam = kJamPerLane*lanes;
    	qCap = fd.calcQCap(this);
    }
    
    public void setWidth(double w) {
		this.width = w;
	}
	public double getWidth() {
		return width;
	}
	public void setVLim(double sl) {
		this.vLim = sl;
	}
	public double getVLim() {
		return vLim;
	}

	public void addVertex(Vertex vertex) {
		vertices.add(vertex);
	}
	public void addVertex(int i, Vertex vertex) {
		vertices.add(i, vertex);
	}
	public double calcLength() {
		double tmplength = 0;
		for (int i = 0; i<=(vertices.size()-2); i++) {
			tmplength += vertices.get(i).distance(vertices.get(i+1));
		}
		return tmplength;
	}
	
	public double[] calcPointAtDistance(double p) {
		double length = calcLength();
		if (p>length)
			throw new Error("p is larger than distance l");
		
		int i = 0;
		
		double arc = 0;
		double cumlength = 0;
		//System.out.println(p);
		while (cumlength <= p-0.0001) {
			//System.out.println(cumlength);
			if ((i+1) == vertices.size())
				System.out.println("fout");
			arc = vertices.get(i).distance(vertices.get(i+1));
			
			//System.out.println(arc);
			cumlength += arc;
			i++;
		}
		double ratio = (p-(cumlength-arc))/arc;
		//System.out.println("ratio:");
		//System.out.println(ratio);
		double result[] = new double[3];
		result[0] = ratio * (vertices.get(i).getX() - vertices.get(i-1).getX()) + vertices.get(i-1).getX();
		result[1] = ratio * (vertices.get(i).getY() - vertices.get(i-1).getY()) + vertices.get(i-1).getY();		
		result[2] = i;
		return result;
		
	}
	public void sortVertices() {
		//vertices.
	}
	public void addIn(Integer i) {
		ins.add(i);
	}
	public void addOut(Integer i) {
		outs.add(i);
	}
	
	public String toString() {
		
		
		return "("+this.id+ ", in: "+ this.ups.size()+", out: "+ this.downs.size()+ ")";
	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	

    /** calculate using q given fundamental diagram **/
    public double calcQ() {
    	return fd.calcQ(this);
    	/*if (k<0 || k > kJam)
    		throw new Error ("density is not correct" + Double.toString(k));
    	else if (k<kCri) 
    		*//** triangular FD **//*
    		return k*vLim;
    	else
    		*//** triangular FD **//*
    		return (kJam - k)/(kJam - kCri)*(kCri*vLim);*/
    }
    public double calcQ(double k) {
    	return fd.calcQ(this, new double[]{k-this.KCell,0,0,0,0});
    	/*if (k<0 || k > kJam)
    		throw new Error ("density is not correct" + Double.toString(k));
    	else if (k<kCri) 
    		*//** triangular FD **//*
    		return k*vLim;
    	else
    		*//** triangular FD **//*
    		return (kJam - k)/(kJam - kCri)*(kCri*vLim);*/
    }
    
    public double calcV(double k) {
    	if (k>0)
    	return calcQ(k)/k;
    	else 
    		return vLim;
    }
    public void updateV() {
    	VCell = calcV(KCell);
    }
    
    public void calcDemand() {
    	if (KCell < kCri)
    		Demand =  calcQ();
    	else
    		Demand = qCap;
   	
    }
    public void calcSupply() {
    	if (KCell < kCri)
    		Supply =  qCap;
    	else
    		Supply = calcQ();
    }
   /* public double calcDemandValue(double k) {
    	if (k < kCri) {
    		if (k>=0) 
    			return calcQ(k);
    		else
    			return -1*calcQ(-1*k);
    	}
    		
    	else
    		return qCap;
   	
    }*/
    public double calcDemandValue(double[] param) {
    	double k = param[0];
		double vLim = param[1];
		double kCri = param[2];
		double kJam = param[3];
		double vCri = param[4];
    	
    	if (k < kCri) {
    		if (k>=0) 
    			return fd.calcQ(param);
    		else
    			return -1*fd.calcQ(new double[]{-param[0],param[1],param[2],param[3],param[4]});
    	}
    		
    	else
    		return fd.calcQCap(param);
   	
    }
    public double calcSupplyValue(double[] param) {
    	double k = param[0];
		double vLim = param[1];
		double kCri = param[2];
		double kJam = param[3];
		double vCri = param[4];
    	if (k < kCri) {
    		return fd.calcQCap(param);
    	}
    		
    	else {
    		if (k<=kJam) 
    			return fd.calcQ(param);
    		else
    			return -1*fd.calcQ(new double[]{2*param[3]-param[0],param[1],param[2],param[3],param[4]});

    		
    	}
    }
    public void calcFluxOut() {
    	
    	FluxOut = nodeOut.fluxesIn[indexNodeOut];
    	if (FluxOut<0) {
    		throw new Error("kan niet");
    	}
    			
    }
    public void calcFluxIn() {
    	
    	FluxIn = nodeIn.fluxesOut[indexNodeIn];
    	if (FluxIn<0) {
    		throw new Error("kan niet");
    	}
    			
    }
    public void updateDensity() {
    	
    	KCell = KCell + model.dt/l*(FluxIn - FluxOut);
    	if (KCell <0 || KCell > kJam) {
    		System.out.println("error: "+(KCell - model.dt/l*(FluxIn - FluxOut)));
    	}
    	updateVariables();
    }
    public void updateVariables() {
    	if (KCell <0 || KCell > kJam) {
    		System.out.println("error: "+(KCell - model.dt/l*(FluxIn - FluxOut)));
    	}
    	//KCell = Math.max(Math.min(KCell, kJam),0);
    	QCell = calcQ(KCell);
    	VCell = calcV(KCell);
    }
    
    /**
     * Sets the lane length based on the x and y coordinates. This method is 
     * called within the constructor and should only be used if coordinates are
     * changed afterwards (for instance to nicely connect lanes at the same 
     * point).
     * @return double; Total length
     */

    
    public void draw(GraphicsPanel graphicsPanel) {
    	//Color color = getDensColor(KCell); 
    	//Color color = getDensColor(new Random().nextInt());
    	//graphicsPanel.setStroke((float) (1*(5+(KCell/(kJam/lanes))*15)));
    	graphicsPanel.setStroke((float) (1*(lanes*5)));
    	Color color = Color.BLACK; 
    	if (this.selected == false)
    		color = getVelocityColor(VCell); 
    	
		graphicsPanel.setColor(color);
		graphicsPanel.drawPolyLine(vertices);
   	
	}
    public Color getDensColor(double k)
    {
    	double H = 0;
    	if (k<kCri) {
    		H = (kCri-k)/kCri * 0.2+0.1 ;
    	}// Hue (note 0.4 = Green, see huge chart below)
    	else {
    		H = ((kJam-kCri)-(k-kCri))/(kJam-kCri) * 0.1;
    	}// Hue (note 0.4 = Green, see huge chart below)
        double S = 0.9; // Saturation
        double B = 0.9; // Brightness

        return Color.getHSBColor((float) H, (float)S, (float)B);
    }
    public Color getVelocityColor(double v)
    {
    	if (Double.isNaN(v))
    		return Color.black;
    	else {
	    	double H = (v/vLim)*0.4;
	        double S = 0.9; // Saturation
	        double B = 0.9; // Brightness
	
	        return Color.getHSBColor((float) H, (float)S, (float)B);
    	}
    }
    
    
    /**
     * Retrieve the upstream connected MacroCell of this MacroCell.
     * @return MacroCell; the upstream connected MacroCell of this MacroCell
     */
    public java.util.ArrayList<MacroCell> getUps_r() {
    	return ups;
    }
    
    /**
     * Retrieve the downstream connected MacroCell of this MacroCell.
     * @return MacroCell; the downstream connected MacroCell of this MacroCell
     */
    public java.util.ArrayList<MacroCell> getDowns_r() {
    	return downs;
    }
    
    /**
     * Retrieve the left MacroCell of this MacroCell.
     * @return MacroCell; the left MacroCell of this MacroCell
     *//*
    public MacroCell getLeft_r() {
    	return left;
    }
    
    *//**
     * Retrieve the right MacroCell of this MacroCell.
     * @return MacroCell; the right MacroCell of this MacroCell
     *//*
    public MacroCell getRight_r() {
    	return right;
    }*/
    public double getFluxIn_r() {
    	return FluxIn;
    }
    public double getFluxOut_r() {
    	return FluxOut;
    }
    public double getTF_r() {
    	if (nodeOut instanceof NodeBoundaryOut)
    		return 1;
    	else
    		return nodeOut.turningRatio[0][0];
    }
    public double getInflow_r() {
    	if (nodeIn instanceof NodeBoundaryIn)
    	return ((NodeBoundaryIn) nodeIn).getInflow();
    	else
    		return 0;
    }
    /**
     * Return the destination of this MacroCell.
     * @return Integer; the destination of this MacroCell, or a negative value if
     * this MacroCell is not a destination
     */
    public int getDestination_r() {
    	return destination;
    }
    
    /**
     * Return the origin of this MacroCell.
     * @return Integer; the origin of this MacroCell or a negative value if this
     * MacroCell is not an origin
     */
    public int getOrigin_r() {
    	return origin;
    }
    
    /**
     * Retrieve the speed limit on this MacroCell.
     * @return Double; the speed limit on this MacroCell in m/s
     */
    public double getSpeedLimit_r() {
    	return vLim;
    }
    
    /**
     * Retrieve the flow of this MacroCell.
     * @return Double; the flow of this MacroCell
     */
    public double getQ_r() {
    	return QCell;
    }
    
    /**
     * Retrieve the density of this MacroCell.
     * @return Double; the density of this MacroCell
     */
    public double getK_r() {
    	return KCell;
    }
    public double getKCri_r() {
    	return kCri;
    }
    public double getVCri_r() {
    	return vCri;
    }
    public double getCapacity_r() {
    	return vCri*kCri;
    }
    /**
     * Retrieve the average speed of this MacroCell.
     * @return Double; the average speed of this MacroCell
     */
    public double getV_r() {
    	return VCell;
    }
    public double getLanes_r() {
    	return lanes;
    }
    public ArrayList<Vertex> getVertices_r() {
    	return vertices;
    }
    public double getLength_r() {
    	return l;
    }
    public double getCNodeIn_r() {
    	return configNodeIn;
    }
    public double getCNodeOut_r() {
    	return configNodeOut;
    }
    
    /**
     * Sets the flow of this MacroCell.
     * @param flow Flow of this cell [veh/h or veh/s].
     */
    public void setQ(double flow) {
        this.QCell = flow;
    }
    
    /**
     * Sets the density of this MacroCell.
     * @param density Density of this cell [veh/km or veh/m].
     */
    public void setK(double density) {
        this.KCell = density;
    }
    
    /**
     * Sets the average speed of this MacroCell.
     * @param speed Speed of this cell [km/h or m/s].
     */
    public void setV(double speed) {
        this.VCell = speed;
    }
    
	/**
     * Returns the ID of the lane.
     * @return ID of the lane.
     */
    public int id() {
        return id;
    }


    /**
     * Returns the speed limit in m/s.
     * @return Speed limit [m/s]
     */
    //public double getVLim() {
    //    return vLim/3.6;
    //}
    void addIn(MacroCell m) {
    	ups.add(m);
    }
    void addOut(MacroCell m) {
    	downs.add(m);
    }
    public String getIns() {
    	String output;
    	if (ups.size() == 0) {
    		output = "()";
    	} else {
    	output = "(";
    	//System.out.println(output);
    	for (MacroCell c: ups) {
    		output = output.concat(Integer.toString(c.id()).concat(","));
    		//System.out.println(output);
    	}
    	output =  output.substring(0, output.length()-1)+")";
    	//System.out.println(output);
    	}
    	return output;
    	
    }
    public String getOuts() {
    	String output;
    	if (downs.size() == 0) {
    		output = "()";
    	} else {
    	output = "(";
    	//System.out.println(output);
    	for (MacroCell c: downs) {
    		output = output.concat(Integer.toString(c.id()).concat(","));
    		//System.out.println(output);
    	}
    	output =  output.substring(0, output.length()-1)+")";
    	//System.out.println(output);
    	}
    	return output;
    	
    }
    public void smoothVertices(double smoothingFraction) {
    	if (vertices.size() <3) {
    		//System.out.println("path is too small to be smoothed");
    	} else {
    		ArrayList<Vertex> copyVertices = new ArrayList<Vertex>(); 
    		Vertex origin = vertices.get(0);
    		Vertex destination = vertices.get(vertices.size()-1);
    		copyVertices.add(origin);
    		//copyVertices.add(destinatio)
    		
    		for (Vertex v: vertices.subList(1,vertices.size()-1)) {
    			if (v.distance(destination) < smoothingFraction*copyVertices.get(copyVertices.size()-1).distance(destination)) {
    				copyVertices.add(v);
    			}
    			
    		}
    		copyVertices.add(destination);
    		vertices = copyVertices;
    	}
    }
	/**
	 * @return the configNodeIn
	 */
	public int getConfigNodeIn() {
		return configNodeIn;
	}
	/**
	 * @param configNodeIn the configNodeIn to set
	 */
	public void setConfigNodeIn(int configNodeIn) {
		this.configNodeIn = configNodeIn;
	}
	/**
	 * @return the configNodeOut
	 */
	public int getConfigNodeOut() {
		return configNodeOut;
	}
	/**
	 * @param configNodeOut the configNodeOut to set
	 */
	public void setConfigNodeOut(int configNodeOut) {
		this.configNodeOut = configNodeOut;
	}
	public double[] getSquaredDistanceToVertices(Vertex v) {
		double loc = Double.MAX_VALUE;
		if (vertices.size() == 1) {
			return new double[]{v.squaredDistance(vertices.get(0)),loc,0};
		} else {
			double sqrDistance = Double.MAX_VALUE;
			int iSelected = 0;
			double distanceFromBegin = 0;
			for (int i=0; i<vertices.size()-1; i++) {
				double potSqrDistance= Double.MAX_VALUE;
				double potLoc = Double.MAX_VALUE;
				Vertex a = vertices.get(i);
				Vertex b = vertices.get(i+1);
				double length = a.squaredDistance(b);
				if (length == 0) {
					potSqrDistance = v.squaredDistance(a);
					potLoc = 0;
				} else{
				double t = ((Vertex.minus(v, a)).dotProduct(Vertex.minus(b, a)))/length;
				potLoc = t;
				if (t<0)
					potSqrDistance = v.squaredDistance(a);
				else if (t>1)
					potSqrDistance = v.squaredDistance(b);
				else 
					potSqrDistance = v.squaredDistance(Vertex.plus(a,Vertex.scalarMultiplication(t,Vertex.minus(b, a))));
				}
				
				if (potSqrDistance < sqrDistance) {
					sqrDistance = Math.min(sqrDistance,potSqrDistance);
					loc = potLoc;
					iSelected = i;
				
				}
				
				
			}
			if (iSelected > 0) {
				for (int i = 0; i<iSelected; i++) {
					distanceFromBegin += vertices.get(i).distance(vertices.get(i+1));
				}
			}
			distanceFromBegin += vertices.get(iSelected).distance(vertices.get(iSelected+1)) * loc;
			return new double[]{sqrDistance,loc, distanceFromBegin};
		}
		
	}
	public int isCongested() {
		if (VCell < (vCri - 0.001)) {
			return 1;
		} else {
			return 0;
		}
	}
}
