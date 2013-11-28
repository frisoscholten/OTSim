package nl.tudelft.otsim.Simulators.LaneSimulator;

import nl.tudelft.otsim.GUI.Main;

/**
 * This class has the common functionality of regular vehicles and temporary
 * lane change vehicles. This is the position on the network and relative to
 * neighboring movables. Common methods are related to position, neighbors and
 * visualization.
 */

public abstract class Movable  {
	/** Serial number */
	public final int id;

    /** Main model. */
    public Model model;

    /** Lane where the movable is at. */
    public Lane lane;

    /** Position on the lane. */
    public double x;

    /** Speed of the movable [m/s]. */
    public double v;
    
    /** Acceleration of the movable [m/s^2]. */
    public double a;

    /** Movable length [m]. */
    public double l;

    /** Global coordinate */
    public java.awt.geom.Point2D.Double global;
    
    /** Normalized heading of the vehicle. */
    public java.awt.geom.Point2D.Double heading = new java.awt.geom.Point2D.Double();
    
    /** Handle special case of vehicle stopping too early at conflict due to length of conflicting vehicle */
    public boolean ignoreLeader = false;
    
    private Movable[] neighbors = new Movable[6];
    private java.util.ArrayList<Movable> reverseNeighbors = new java.util.ArrayList<Movable> ();
    private int[] neighborUpdated = new int[6];
    
    /* Allowed values for the neighbor parameter of getNeighbor */
    /* The Java enum cannot be used in subtract and exclusive or which would have been so nice */ 
	/** Left neighboring lane; upstream */
    public final static int LEFT_UP = 0;
	/** Left neighboring lane; downstream */
	public final static int LEFT_DOWN = 1; 
	/** Own lane; upstream */
	public final static int UP = 2; 
	/** Own lane; downstream */
	public final static int DOWN = 3;
	/** Right neighboring lane; upstream */
	public final static int RIGHT_UP = 4 ;
	/** Right neighboring lane; downstream */
	public final static int RIGHT_DOWN = 5;

    /** Value for flip parameter of getNeighbor to flip nothing */
    public final static int FLIP_NONE = 0;
    /** Value for flip parameter of getNeighbor to flip left for right */
    public final static int FLIP_LR = 10;
    /** Value for flip parameter of getNeighbor to flip up for down */
    public final static int FLIP_UD = 20;
    /** Value for flip parameter of getNeighbor to flip left for right and up for down */
    public final static int FLIP_DIAGONAL = 30;
    
    /**
     * Retrieve a neighbor of this Movable
     * @param direction Integer; one of the direction values LEFT_UP, LEFT_DOWN, UP, DOWN, RIGHT_UP, RIGHT_DOWN
     * @return Movable; the selected neighbor (which may be null)
     */
    public Movable getNeighbor (int direction) {
    	if ((UP == direction) || (DOWN == direction) || (neighborUpdated[direction] == model.k))
    		return neighbors[direction];
    	// We'll have to find it
    	Lane neighborLane = ((LEFT_UP == direction) || (LEFT_DOWN == direction)) ? lane.left : lane.right;
    	if (null != neighborLane) {
    		double xNeighborLane = lane.getAdjacentX(x, ((LEFT_UP == direction) || (LEFT_DOWN == direction)) ? Model.latDirection.LEFT : Model.latDirection.RIGHT);
    		setNeighbor(direction, neighborLane.findVehicle(xNeighborLane, alignDirection(direction) == UP ? Model.longDirection.UP : Model.longDirection.DOWN));
    	} else {
    		setNeighbor(direction, null);
    		setNeighbor(flipDirection(direction, FLIP_UD), null);
    		neighborUpdated[flipDirection(direction, FLIP_UD)] = model.k;
    	}
    	neighborUpdated[direction] = model.k;
    	return neighbors[direction];
    }
    
    /**
     * Flip a direction of a neighbor
     * @param direction Integer; direction that must be flipped
     * @param flip Integer; how must the direction be flipped
     * @return Integer; the flipped direction
     */
    public static int flipDirection (int direction, int flip) {
    	final int maxDirection = 5;
    	switch (flip) {
    	case 0: return direction;
    	case FLIP_LR: return direction = (maxDirection - direction) ^ 1;
    	case FLIP_UD: return direction ^ 1;
    	case FLIP_DIAGONAL: return maxDirection - direction;
    	default: throw new Error("Bad flip value: " + flip);
    	}
    }
    
    /**
     * Return the UP or a DOWN component of a direction
     * @param direction Integer; direction to examine
     * @return Integer; UP if the direction is LEFT_UP, UP, or RIGHT_UP;
     * DOWN if the direction is LEFT_DOWN, DOWN, or RIGHTDOWN
     */
    public static int alignDirection (int direction) {
    	return (direction % 2) + UP;
    }
    
    /**
     * Update a neighbor of this Movable.
     * @param direction Integer; the direction of the neighbor that must be updated
     * @param newNeighbor Movable; the new neighbor in the specified direction (may be null)
     */
    public void setNeighbor (int direction, Movable newNeighbor) {
    	Movable oldNeighbor = neighbors[direction];
    	if (null != oldNeighbor) {
    		int index = oldNeighbor.reverseNeighbors.indexOf(this);
    		if (index < 0)
    			throw new Error("Missing reverseNeighbor link");
    		oldNeighbor.reverseNeighbors.remove(index);
    	}
    	if (null != newNeighbor)
    		newNeighbor.reverseNeighbors.add(this);
    	neighbors[direction] = newNeighbor;
    	if ((neighbors[UP] == neighbors[DOWN]) && (neighbors[UP] != null))
    		System.out.println("whoops");
    }
    
    /**
     * Testing only... (very expensive if many vehicles are in simulation)
     */
    public void verifyNeighbors() {
        int[] allDirections = { UP, DOWN, LEFT_UP, LEFT_DOWN, RIGHT_UP, RIGHT_DOWN };
    	for (int dir : allDirections) {
    		Movable neighbor = neighbors[dir];
    		if (null != neighbor) {
    			if (neighbor.reverseNeighbors.indexOf(this) < 0)
    				throw new Error ("Missing reverse link");
    		}
    	}
    	java.util.ArrayList<Movable> allMovables = new java.util.ArrayList<Movable> (model.vehicles);
    	allMovables.addAll(model.lcVehicles);
    	for (Movable m : allMovables) {
    		if (m.reverseNeighbors.contains(this)) {
    			boolean found = false;
    			for (int dir : allDirections)
    				if (neighbors[dir] == m)
    					found = true;
    			if (! found)
    				throw new Error("missing forward link");
    		}
    	}
    }
    
    /**
     * Return a textual description of a direction.
     * @param direction Integer; direction to describe textually
     * @return String; the text that describes the direction
     */
    public static String directionToString(int direction) {
    	switch (direction) {
    	case UP: return "UP";
    	case DOWN: return "DOWN";
    	case LEFT_UP: return "LEFT_UP";
    	case LEFT_DOWN: return "LEFT_DOWN";
    	case RIGHT_UP: return "RIGHT_UP";
    	case RIGHT_DOWN: return "RIGHT_DOWN";
    	default: return "UNDEFINED DIRECTION";
    	}
    }

    /**
     * Return a textual description of a flip.
     * @param flip Integer; the flip to describe textually
     * @return String; the text that describes the flip
     */
    public static String flipToString(int flip) {
    	switch (flip) {
    	case FLIP_NONE: return "NONE";
    	case FLIP_UD: return "UD";
    	case FLIP_LR: return "LR";
    	case FLIP_DIAGONAL: return "DIAGONAL";
    	default: return "UNDEFINED FLIP";
    	}
    }
    
    /**
     * Create a string that describes a specified neighbor of this Movable
     * @param direction Integer; direction of the neighbor
     * @return String; description of the neighbor
     */
    public String neighborToString(int direction) {
    	String result = directionToString(direction) + " is ";
    	Movable neighbor = getNeighbor(direction);
    	if (null == neighbor)
    		result += "NULL";
    	else
    		result += neighbor.toString();
    	return result;
    }
    
    /** Upstream movable, if any */
    //public Movable up;

    /** Downstream movable, if any */
    //public Movable down;

    /** Left upstream movable, if any */
    //public Movable leftUp;

    /** Left downstream movable, if any */
    //public Movable leftDown;

    /** Right upstream movable, if any */
    //public Movable rightUp;

    /** Right downstream movable, if any */
    //public Movable rightDown;

    /** Marker string for Matlab. */
    public java.lang.String marker;

    /** Matlab handle(s), which are simply double values. */
    public double[] handle;

    /** Left indicator on. */
    public boolean leftIndicator = false;

    /** Right indicator on. */
    public boolean rightIndicator = false;

    /**
     * Constructor that sets the main model.
     * @param model Main model.
     */
    public Movable(Model model) {
    	id = ++model.nextMovableId;
        this.model = model;
    	for (int i = 0; i < neighborUpdated.length; i++)
    		neighborUpdated[i] = -1;
    }

    /**
     * Sets the global x and y positions, as implemented by a subclass.
     */
    public abstract void setXY();
    
    /**
     * Returns the global x and y at the lane center.
     * @return 2-element array with x and y at the lane.
     */
    public java.awt.geom.Point2D.Double atLaneXY() {
        return lane.XY(x);
    }

    /**
     * Returns the location on the adjacent lane, keeping lane length
     * difference in mind.
     * @param dir Defines direction. Use -1 for left lane, 1 for right lane.
     * @return X location on adjacent lane.
     */
    public double getAdjacentX(Model.latDirection dir) {
        return lane.getAdjacentX(x, dir);
    }

    /**
     * Abstract method to translate a vehicle.
     * @param dx Distance to be translated [m].
     */
    public abstract void translate(double dx);

    /**
     * Returns the net headway to a given vehicle. This vehicle should be on the
     * same or an adjacent lane, or anywhere up- or downstream of those lanes.
     * @param leader Leading vehicle, not necessarily the 'down' vehicle.
     * @return Net headway with leader [m].
     */
    public double getHeadway(Movable leader) {
        // Ignore leader which is on the other side of a merge but which came
        // from another lane. It should also be only partially past the conflict.
        // The conflict should deal with the situation.
        if ((leader == getNeighbor(DOWN)) && ignoreLeader) {
            return Double.POSITIVE_INFINITY;
        }
        double s = 0;
        double xAdjTmp;
        if (lane == leader.lane) {
            s = leader.x - x; // same lane
            if (s < 0)
            	s += lane.l;
        }
        else if (lane==leader.lane.left)
            s = leader.getAdjacentX(Model.latDirection.LEFT) - x; // leader is right
        else if (lane==leader.lane.right)
            s = leader.getAdjacentX(Model.latDirection.RIGHT) - x; // leader is left
        else if ((xAdjTmp=lane.xAdj(leader.lane)) != 0)
            s = leader.x + xAdjTmp - x; // leader is up- or downstream
        else if ((xAdjTmp=lane.xAdj(leader.lane.left)) != 0)
            s = leader.getAdjacentX(Model.latDirection.LEFT) + xAdjTmp - x; // leader is on right lane up- or downstream
        else if (lane.right!=null && (xAdjTmp=lane.right.xAdj(leader.lane)) != 0)
            s = leader.x + xAdjTmp - getAdjacentX(Model.latDirection.RIGHT); // leader is on right lane up- or downstream (no up/down lane)
        else if ((xAdjTmp=lane.xAdj(leader.lane.right)) != 0)
            s = leader.getAdjacentX(Model.latDirection.RIGHT) + xAdjTmp - x; // leader is on left lane up- or downstream
        else if (lane.left!=null && (xAdjTmp=lane.left.xAdj(leader.lane)) != 0)
            s = leader.x + xAdjTmp - getAdjacentX(Model.latDirection.LEFT); // leader is on left lane up- or downstream (no up/down lane)
        else if (this instanceof Vehicle) {
            // leader may actually be a leader of the lane change vehicle
            /*
             * This happens for a neighbor of an lcVehicle as:
             * ------------
             *          A
             * ------------
             *     B
             * ------------
             *     C
             * ------------
             * Vehicle A wants the acceleration B->A so it won't cut B off. The
             * acceleration is calculated by the driver of vehicle B. However, B
             * is a lane change vehicle (of C) and has no driver.
             * "B.getDriver()" returns the driver of vehicle C. That driver then
             * needs a headway between it's vehicle (C) and A. This will not be
             * found and so the headway between B and A will be needed.
             */
            Vehicle veh = (Vehicle) this;
            if (veh.lcVehicle == null) {
                // give warning as vehicles are not adjacent
                System.err.println("Headway not found from lanes: "+x+"@"+lane.id+"->"+leader.x+"@"+leader.lane.id+", returning Inf");
                s = Double.POSITIVE_INFINITY;
            } else
                s = veh.lcVehicle.getHeadway(leader);
        }
        s = s-leader.l; // gross -> net
        if (4 == this.id)
        	System.out.println("headway to leader of " + this.id + " is " + s + "m");
        return s;
    }
    
    /**
     * Returns the distance between a vehicle and a RSU.
     * @param rsu RSU.
     * @return Distance [m] between vehicle and RSU.
     */
    public double getDistanceToRSU(RSU rsu) {
    	double reverseDistance = rsu.lane.xAdj(lane) + x - rsu.x;
    	if ((0 == reverseDistance) || (reverseDistance > l)) 
            return rsu.x + lane.xAdj(rsu.lane) - x; // Not found; or too far away on loop
    	return - reverseDistance;	// vehicle nose downstream of rsu and rear upstream of rsu
    }

    /**
     * Deletes a vehicle entirely while taking care of any neighbor reference
     * to the vehicle.
     */
    public void delete() {
        /* When deleting a vehicle, all pointers to it need to be removed in
         * order for the garbage collector to remove the object from memory.
         * Vehicles are referenced from: model, lane, OBU, driver, trajectory,
         * lcVehicle<->vehicle and neighboring vehicles.
         */
        
        // remove from lane and neighbors
        cut();

        // remove from various objects
        if (this instanceof Vehicle) {
            Vehicle veh = (Vehicle) this;
            // lcVehicle
            if (veh.lcVehicle != null) {
                veh.lcVehicle.delete(); // will be removed from memory
                veh.lcVehicle = null;
            }
            // model
            model.removeVehicle(veh);
            // trajectory
            if (veh.trajectory != null) {
                veh.trajectory.vehicle = null; // data remains, vehicle does not
                veh.trajectory = null;
            }
            // OBU
            if (veh.isEquipped()) {
                veh.OBU.delete(); // should remove pointers set in constructor
                veh.OBU.vehicle = null;
                veh.OBU = null; // OBU will be removed from memory
            }
            // delete storages of driver
            veh.driver.accelerations = null;
            veh.driver.antFromLeft = null;
            veh.driver.antFromRight = null;
            veh.driver.antInLane = null;
            // driver
            veh.driver.vehicle = null;
            veh.driver = null; // driver will be removed from memory
        } else if (this instanceof LCVehicle) {
            LCVehicle veh = (LCVehicle) this;
            // model
            model.removeVehicle(veh);
            // vehicle
            veh.vehicle.lcVehicle = null;
            veh.vehicle = null;
        }
    }

    /**
     * Cuts a vehicle from a lane. All pointers from the lane and neighbors
     * to this vehicle are updated or removed.
     */
    public void cut() {
        // remove from lane vector
    	lane.cut(this);

        while (reverseNeighbors.size() > 0)
        	fixLinkFromNeighbor(reverseNeighbors.get(0));
        
        // check connection consistency (debug)
        if (model.debug)
            model.checkForRemainingPointers(this);
		
        // delete own references
        int[] allDirections = { UP, DOWN, LEFT_UP, LEFT_DOWN, RIGHT_UP, RIGHT_DOWN };
        for (int direction : allDirections)
        	setNeighbor(direction, null);
    }
    
    private void fixLinkFromNeighbor(Movable neighbor) {
        int[] allDirections = { UP, DOWN, LEFT_UP, LEFT_DOWN, RIGHT_UP, RIGHT_DOWN };
    	for (int direction: allDirections)
    		if (this == neighbor.neighbors[direction]) {
    			Movable newNeighbor = neighbors[alignDirection(direction)];
    			if (this == neighbor)
    				newNeighbor = null;	// one movable on a roundabout
    			else if (this == newNeighbor)
    				newNeighbor = null;	// two movables on a roundabout
    			neighbor.setNeighbor(direction, newNeighbor);
    			return;
    		}
    	// We should never get here!
    	neighbor.verifyNeighbors();
    	verifyNeighbors();
    	System.err.println("neighbors are " + linkedNeighbors());
    	System.err.println("neighbors of neighbor are " + neighbor.linkedNeighbors());
    	throw new Error("Could not find neighbor link to " + toString() + " from " + neighbor.toString());
    }
    
    /**
     * Returns a set of vehicles that is upstream of this, or an upstream merge.
     * In case no vehicle is found upstream of one of the merge lanes, any 
     * further merge is used to continue searching.
     * @param merge Lane to look for vehicles upstream of.
     * @return Set of vehicles that is upstream of this, or an upstream merge.
     */
    protected java.util.ArrayList<Movable> findVehiclesUpstreamOfMerge(Lane merge) {
        java.util.ArrayList<Movable> out = new java.util.ArrayList<Movable>();
        for (Lane j : merge.upMerge.ups) {
        	if (j.marked)
        		continue;
        	j.marked = true;
            Movable d = j.findVehicle(j.l, Model.longDirection.UP);
            if (d != null)
                out.add(d);
            else if (j.upMerge != null)
                out.addAll(findVehiclesUpstreamOfMerge(j));
            j.marked = false;
        }
        return out;
    }
    
    /**
     * Returns a set of vehicles that is downstream of this, or a downstream 
     * split. In case no vehicle is found downstream of one of the split lanes,
     * any further split is used to continue searching.
     * @param split Lane to look for vehicles upstream of.
     * @return Set of vehicles that is downstream of this, or a downstream split.
     */
    protected java.util.ArrayList<Movable> findVehiclesDownstreamOfSplit(Lane split) {
        java.util.ArrayList<Movable> out = new java.util.ArrayList<Movable>();
        for (Lane j : split.downSplit.downs) {
        	if (j.marked)
        		continue;
        	j.marked = true;
            Movable d = j.findVehicle(0, Model.longDirection.DOWN);
            if (d != null)
                out.add(d);
            else if (j.downSplit!=null)
                out.addAll(findVehiclesDownstreamOfSplit(j));
            j.marked = false;
        }
        return out;
    }
    
    /**
     * Places a vehicle on a lane, sets new neighbors and sets this vehicle as
     * neighbor of surrounding vehicles.
     * @param atLane Lane where the vehicle needs to be placed at.
     * @param atX Location where the vehicle needs to be placed at.
     */
    public void paste(Lane atLane, double atX) {
        // In case the lane is exceeded, change the lane to search on. This
        // could occur when searching for neighbors when ending a lane change
        // within the same time step a lane is exceeded.
        if ((atX > atLane.l) && (atLane.down != null)) {
            paste(atLane.down, atX - atLane.l);
            return;
        }
        // find up/down neighbors
        setNeighbor(UP, atLane.findVehicle(atX, Model.longDirection.UP));
        if (null != getNeighbor(UP)) {
        	setNeighbor(DOWN, getNeighbor(UP).getNeighbor(DOWN));	// put this Movable in between
        	if ((null == getNeighbor(UP).getNeighbor(DOWN)) && (getNeighbor(UP).lane.downSplit != atLane.downSplit))
        		setNeighbor(DOWN, atLane.findVehicle(atX, Model.longDirection.DOWN)); // just passed split, so up has no down (as this was cut)
        	else
        		getNeighbor(UP).setNeighbor(DOWN, this);
        } else
        	setNeighbor(DOWN, atLane.findVehicle(atX, Model.longDirection.DOWN));
        if ((null != getNeighbor(DOWN)) && (getNeighbor(DOWN).lane.upMerge == atLane.upMerge))
        	getNeighbor(DOWN).setNeighbor(UP, this);	// same lane, down has this as up
        // set properties
        lane = atLane;
        x = atX;
        // Set pointers to this of vehicles at other side of split or merge.
        if ((null != lane.upMerge) && (null == getNeighbor(UP))) {
            java.util.ArrayList<Movable> ups = findVehiclesUpstreamOfMerge(lane);
            for (Movable d : ups)
            	if (((null == d.getNeighbor(DOWN)) || (d.getNeighbor(DOWN) == getNeighbor(DOWN))) 
            			&& ((null == d.lane.downSplit) || ((d.lane.downSplit == lane.downSplit) && (d.lane.xAdj(lane.downSplit) > lane.xAdj(lane.downSplit)))))
            		d.setNeighbor(DOWN, this);
        }
        if ((lane.downSplit != null) && (null == getNeighbor(DOWN))) {
            java.util.ArrayList<Movable> downs = findVehiclesDownstreamOfSplit(lane);
            for (Movable d : downs)
            	if (((null == d.getNeighbor(UP)) || (d.getNeighbor(UP) == getNeighbor(UP)))
            			&& ((null == d.lane.upMerge) || ((d.lane.upMerge == lane.upMerge) && (lane.upMerge.xAdj(d.lane) > lane.upMerge.xAdj(lane)))))
            		d.setNeighbor(UP, this);
        }
        // add to lane vector
        atLane.paste(this, x);
    }
    
    /**
     * Returns the driver of any movable.
     * @return Driver of the movable.
     */
    public abstract Driver getDriver();

    /**
     * Retrieve the {@link Lane} of this Movable.
     * @return {@link Lane}; the current Lane of this Movable
     */
    public Lane getLane_r() {
    	return lane;
    }
    
    /**
     * Retrieve the longitudinal position in the current {@link Lane}.
     * @return String; the longitudinal position in the current {@link Lane}
     */
    public String getLongitudinalPositionInLane_r() {
    	return String.format(Main.locale, "%.2fm (of %.2fm)", x, lane.l);
    }
    
    @Override
	public String toString() {
    	String location = "null";
    	if (null != global)
    		location = String.format(Main.locale, "%.3f,%.3f", global.x, global.y);
    	String comment = "";
    	if (this instanceof LCVehicle)
    		comment = " owner is " + ((LCVehicle) this).vehicle.toString();
    	return String.format (Main.locale, "%d at (%s), (%s%s)", id, location, getClass().getName(), comment);
    }
    
    private String printNeighbor(String caption, int direction) {
    	Movable neighbor = neighbors[direction];
    	if (null == neighbor)
    		return "";
    	return " " + caption + " " + neighbor.toString();
    }
    
    /**
     * Show the connectivity between Movables (for debugging).
     * @return String
     */
    public String linkedNeighbors() {
    	return printNeighbor("up", UP) + printNeighbor("down", DOWN) 
    			+ printNeighbor("leftUp", LEFT_UP) + printNeighbor("rightUp", RIGHT_UP)
    			+ printNeighbor("leftDown", LEFT_DOWN) + printNeighbor("rightDown", RIGHT_DOWN);
    }
    
    /**
     * Retrieve the leader of this Movable.
     * @return Movable; the leader of this Movable (may be null)
     */
    public Movable getLeader_r () {
    	return getNeighbor(DOWN);
    }
    
    /**
     * Retrieve the left leader of this Movable.
     * @return Movable; the left leader of this Movable (may be null)
     */
    public Movable getLeaderLeft_r () {
    	return getNeighbor(LEFT_DOWN);
    }

    /**
     * Retrieve the right leader of this Movable.
     * @return Movable; the right leader of this Movable (may be null)
     */
    public Movable getLeaderRight_r () {
    	return getNeighbor(RIGHT_DOWN);
    }

    /**
     * Retrieve the follower of this Movable.
     * @return Movable; the follower of this Movable (may be null)
     */
    public Movable getFollower_r () {
    	return getNeighbor(UP);
    }
    
    /**
     * Retrieve the left follower of this Movable.
     * @return Movable; the left follower of this Movable (may be null)
     */
    public Movable getFollowerLeft_r () {
    	return getNeighbor(LEFT_UP);
    }

    /**
     * Retrieve the right follower of this Movable.
     * @return Movable; the right follower of this Movable (may be null)
     */
    public Movable getFollowerRight_r () {
    	return getNeighbor(RIGHT_UP);
    }

}