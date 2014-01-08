package nl.tudelft.otsim.GeoObjects;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TreeSet;

import nl.tudelft.otsim.FileIO.ParsedNode;
import nl.tudelft.otsim.FileIO.StaXWriter;
import nl.tudelft.otsim.GUI.GraphicsPanel;
import nl.tudelft.otsim.GUI.InputValidator;
import nl.tudelft.otsim.GUI.Main;
import nl.tudelft.otsim.SpatialTools.Planar;
import nl.tudelft.otsim.Utilities.Reversed;


//TODO set message text
//TODO add TimedMessage
//TODO delete TimedMessage

/**
 * A Variable Message Sign (VMS) shows a time-varying message to passing traffic.
 * 
 * @author Peter Knoppers
 */
public class VMS extends CrossSectionObject {
	private String ID;
	/** Label in XML representation of a VMS */
	public static final String XMLTAG = "VMS";
	
	/** Label of ID in XML representation of a VMS */
	private static final String XML_ID = "ID";
	/** Label of longitudinalPosition in XML representation of a VMS */
	private static final String XML_LONGITUDINALPOSITION = "longitudinalPosition";
	/** Label of lateralCenter in XML representation of a VMS */
	private static final String XML_LATERALPOSITION = "lateralCenter";
	/** Label of width in XML representation of a VMS */
	private static final String XML_WIDTH = "width";
	/** Label of time/text set in XML representation of a VMS */
	private static final String XML_TIMETEXT = "timeText";
	/** Label of a time in XML representation of a VMS time/text pair */
	private static final String XML_TIME = "time";
	/** Label of a text in XML representation of a VMS time/text pair */
	private static final String XML_TEXT = "text";
	
	private TreeSet<TimedMessage> messages = new TreeSet<TimedMessage> ();

	/**
	 * Create a VMS from a parsed XML file.
	 * @param crossSectionElement {@link CrossSectionElement}; owner of the new VMS
	 * @param pn {@link ParsedNode}; the root of the VMS in the parsed XML file
	 * @throws Exception
	 */
	public VMS(CrossSectionElement crossSectionElement, ParsedNode pn) throws Exception {
		this.crossSectionElement = crossSectionElement;
		lateralReference = CrossSectionElement.LateralReferenceCenter;
		longitudinalPosition = lateralPosition = lateralWidth = Double.NaN;
		longitudinalLength = 1;
		ID = null;
		for (String fieldName : pn.getKeys()) {
			String value = pn.getSubNode(fieldName, 0).getValue();
			if (fieldName.equals(XML_ID))
				ID = value;
			else if (fieldName.equals(XML_LONGITUDINALPOSITION))
				longitudinalPosition = Double.parseDouble(value);
			else if (fieldName.equals(XML_LATERALPOSITION))
				lateralPosition = Double.parseDouble(value);
			else if (fieldName.equals(XML_WIDTH))
				lateralWidth = Double.parseDouble(value);
			else
				throw new Exception("VMS does not have a field " + fieldName);
		}
		if ((null == ID) || Double.isNaN(longitudinalPosition) || Double.isNaN(lateralPosition) || Double.isNaN(lateralWidth))
			throw new Exception("VMS is not completely defined" + pn.lineNumber + ", " + pn.columnNumber);
		
		// Put some junk it it for debugging
		messages.add(new TimedMessage(10d, "Hello World!"));
		messages.add(new TimedMessage(100d, "Goodbye World!"));
		messages.add(new TimedMessage(30d, "How are you doing?"));
	}
	
	/**
	 * Create a VMS from a textual description of times and messages.
	 * @param messageList String; textual description of times and messages
	 */
	public VMS(String messageList) {
		String[] fields = messageList.split(",");
		for (String field : fields) {
			String[] subFields = field.split(":");
			messages.add(new TimedMessage(Double.parseDouble(subFields[0]), subFields[1]));
		}
	}

	/**
	 * Create a new VMS, put it somewhere on the give CrossSectionElement and give it a unique ID.
	 * @param CSE CrossSectionElement; the CrossSectionElement that will own the new VMS
	 */
	public VMS(CrossSectionElement CSE) {
		longitudinalPosition = CSE.getCrossSection().getLongitudinalLength() / 2;	// put it half way
		lateralReference = CrossSectionElement.LateralReferenceCenter;
		lateralPosition = 0;
		lateralWidth = CSE.getWidth_r();
		longitudinalLength = 1;
		for (int idRank = 1; null == ID; idRank++) {
			ID = "" + idRank;
			for (CrossSectionObject cso : CSE.getCrossSectionObjects(VMS.class))
				if (((VMS) cso).ID.equals(ID)) {
					ID = null;	// try the next possible value
					break;
				}
		}
		this.crossSectionElement = CSE;
	}

	/**
	 * Return the ID of this VMS.
	 * @return String; the ID of this VMS
	 */
	public String getID_r() {
		return ID;
	}
	
	/**
	 * Change the ID of this VMS.
	 * @param newName String; the new name for this VMS
	 */
	public void setID_w(String newName) {
		this.ID = newName;
		crossSectionElement.getCrossSection().getLink().network.setModified();
	}
	
	/**
	 * Create an {@link InputValidator} that ensures a proper ID for this VMS.
	 * @return {@link InputValidator} for a proper VMS ID
	 */
	public InputValidator validateID_v() {
		return new InputValidator(new InputValidator.CustomValidator() {
			@Override
			public boolean validate(String originalValue, String proposedValue) {
				if (! proposedValue.matches("[a-zA-Z_][-a-zA-Z0-9_.]*"))
					return false;	// not a decent name
				if (proposedValue.equals(originalValue))
					return true;	// current name is OK
				// Anything else must be unique among the VMS's in the Network
				return null == crossSectionElement.getCrossSection().getLink().network.lookupVMS(proposedValue);
			}
		});
	}
	
	/**
	 * Retrieve the lateral position of this VMS.
	 * @return Double; the lateral position of this VMS in m from
	 * the center line of the parent {@link CrossSectionElement}
	 */
	public double getLateralPosition_r() {
		return lateralPosition;
	}
	
	/**
	 * Change the lateral position of this VMS.
	 * @param lateralPosition Double; the new lateral position in m from the
	 * center line of the parent (@link CrossSectionElement}
	 */
	public void setLateralPosition_w(double lateralPosition) {
		this.lateralPosition = lateralPosition;
		crossSectionElement.getCrossSection().getLink().network.setModified();
	}
	
	/**
	 * Return an {@link InputValidator} for the lateral position of this VMS.
	 * @return {@link InputValidator} for the lateral position of this VMS
	 */
	public InputValidator validateLateralPosition_v() {
		double range = crossSectionElement.getWidth_r() - lateralWidth;
		return new InputValidator("[-.0-9].*", -range / 2, range / 2);
	}
	
	/**
	 * Retrieve the lateral width of this VMS.
	 * @return Double; the lateral width of this VMS in m
	 */
	public double getWidth_r() {
		return lateralWidth;
	}
	
	/**
	 * Change the lateral width of this VMS.
	 * @param width Double; the new lateral width of this VMS in m
	 */
	public void setWidth_w(double width) {
		lateralWidth = width;
		crossSectionElement.getCrossSection().getLink().network.setModified();
	}
	
	/**
	 * Return an {@link InputValidator} for the lateral width of this VMS.
	 * @return {@link InputValidator} for the lateral width of this VMS
	 */
	public InputValidator validateWidth_v() {
		double limit = crossSectionElement.getWidth_r() - Math.abs(lateralPosition);
		return new InputValidator("[.0-9].*", 0.1, limit);
	}
	
	/**
	 * Return the Vertices that form the outline of the detection area of this
	 * VMS
	 * @return ArrayList&lt;{@link Vertex}&gt; vertices of the polygon of this 
	 * VMS
	 */
	public ArrayList<Vertex> getPolygon_r() {
		ArrayList<Vertex> guideLine = Planar.slicePolyline(crossSectionElement.getLinkPointList(lateralReference, true, false), longitudinalPosition, longitudinalLength);
		ArrayList<Vertex> result = Planar.createParallelVertices(guideLine, null, -lateralWidth / 2,  -lateralWidth / 2);
		for (Vertex v : Reversed.reversed(Planar.createParallelVertices(guideLine, null, lateralWidth / 2, lateralWidth / 2)))
			result.add(v);
		return result;
	}
	
	/**
	 * Retrieve the longitudinalPosition of this VMS.
	 * @return Double; the longitudinalPosition of this VMS
	 */
	public double getLongitudinalPosition_r() {
		return longitudinalPosition;
	}
	
	/**
	 * Change the longitudinalPosition of this VMS.
	 * @param longitudinalPosition Double; the new longitudinalPosition of this
	 * VMS
	 */
	public void setLongitudinalPosition_w(double longitudinalPosition) {
		this.longitudinalPosition = longitudinalPosition;
	}
	
	/**
	 * Validate a proposed longitudinalPosition for this VMS.
	 * @return InputValidator for proposed values of the longitudinalPosition 
	 * of this VMS
	 */
	public InputValidator validateLongitudinalPosition_v() {
		double length = crossSectionElement.getCrossSection().getLongitudinalLength();
		return new InputValidator("[-.,0-9].*", -length, length);
	}
	
	/**
	 * A VMS can always be deleted.
	 * <br /> This method is only used by the {@link nl.tudelft.otsim.GUI.ObjectInspector}.
	 * @return Boolean; always true
	 */
	@SuppressWarnings("static-method")
	public boolean mayDeleteVMS_d() {
		return true;
	}
	
	/**
	 * Delete this VMS.
	 */
	public void deleteVMS_d() {
		crossSectionElement.deleteCrossSectionObject(this);
	}
	
	@Override
	public String toString() {
		return String.format(Main.locale, "VMS %s at longitudinalPosition %.3fm, width %.3fm", ID, longitudinalPosition, lateralWidth);
	}
	
	/**
	 * Create a string representation of the messages this VMS suitable for import
	 * @return String; the string representation of the messages of this VMS
	 */
	public String export () {
		String result = "VMS";
		String separator = "\t";
		for (TimedMessage tm : messages) {
			result += String.format(Locale.US, "%s%.3f:%s", separator, tm.getTime(), tm.getMessage_r());
			separator = ",";
		}
		return result + "\n";
	}
	
	@Override
	public void paint(GraphicsPanel graphicsPanel) {
		graphicsPanel.setStroke(0F);
		graphicsPanel.setColor(Color.BLUE);
		ArrayList<Vertex> polygon = getPolygon_r();
		//System.out.println("polygon is " + GeometryTools.verticesToString(polygon));
		if (polygon.size() > 0)
			graphicsPanel.drawPolygon(polygon.toArray());
	}
	
	private boolean writeMessages(StaXWriter staXWriter) {
		for (TimedMessage tm : messages)
			if (! (staXWriter.writeNodeStart(XML_TIMETEXT)
					&& staXWriter.writeNode(XML_TIME, String.format(Locale.US, "%.3f",  tm.getTime()))
					&& staXWriter.writeNode(XML_TEXT, tm.getMessage_r())
					&& staXWriter.writeNodeEnd(XML_TIMETEXT)))
				return false;
		return true;
	}

	@Override
	public boolean writeXML(StaXWriter staXWriter) {
		return staXWriter.writeNodeStart(XMLTAG)
				&& staXWriter.writeNode(XML_ID, getID_r())
				&& staXWriter.writeNode(XML_LATERALPOSITION, Double.toString(lateralPosition))
				&& staXWriter.writeNode(XML_WIDTH, Double.toString(lateralWidth))
				&& staXWriter.writeNode(XML_LONGITUDINALPOSITION, Double.toString(longitudinalPosition))
				&& writeMessages(staXWriter)
				&& staXWriter.writeNodeEnd(XMLTAG);
	}

	private final static VMS.TimedMessage addTimedMessage = new VMS.TimedMessage(-1d, "Add a timed message");
	
	/**
	 * Retrieve a list of all messages
	 * @return ArrayList&lt;{@link TimedMessage}&gt;; the list of all messages
	 */
	public ArrayList<TimedMessage> getTimedMessages_r() {
		ArrayList<TimedMessage> result = new ArrayList<TimedMessage>(messages);
		result.add(addTimedMessage);		
		return result;
	}

	/**
	 * Simple fixed message that is displayed at a specified time
	 * 
	 * @author Peter Knoppers
	 */
	public static class TimedMessage implements Comparable<TimedMessage> {
		private double time;
		private String message;
		
		@Override
		public int compareTo(TimedMessage other) {
			if (other.time > time)
				return -1;
			else if (other.time < time)
				return 1;
			return 0;
		}
		
		/**
		 * Create a TimedMessage.
		 * @param time Double; time [s] when the message is displayed
		 * @param message String; the message to display
		 */
		public TimedMessage(Double time, String message) {
			this.time = time;
			this.message = message;
		}
		
		/**
		 * Modify the time at which this TimedMessage is displayed
		 * @param newTime Double; the time [s] when the message is displayed
		 */
		public void setTime (Double newTime) { this.time = newTime; };
		
		/**
		 * Modify the text that is displayed.
		 * @param newMessage String; the text that is displayed
		 */
		public void setMessage_w (String newMessage) { this.message = newMessage; };
		
		/**
		 * Retrieve the time at which this TimedMessage is displayed.
		 * @return Double; the time [s] at which this TimedMessage is displayed
		 */
		public Double getTime () { return time; };
		
		/**
		 * Retrieve the text that is displayed
		 * @return String; the text that is displayed
		 */
		public String getMessage_r () { return message; };
		
		@Override
		public String toString() {
			if (time < 0)
				return message;
			return String.format (Main.locale, "%.3f: %s", time, message);
		}
		
		/**
		 * Retrieve a string representation of the time at which this 
		 * TimedMessage is displayed.
		 * @return String; a string representation of the time at which this
		 * TimedMessage is displayed
		 */
		public String getTime_r () {
			return String.format (Main.locale, "%.3f", time);
		}
		
		/**
		 * Change the time at which this TimedMessage is displayed.
		 * @param newTime Double; the new value for the time at which this
		 * TimedMessage is displayed
		 */
		public void setTime_w (String newTime) {
			time = Double.parseDouble(Planar.fixRadix(newTime));
		}
		
		/**
		 * Return an {@link InputValidator} for the time.
		 * @return {@link InputValidator}; InputValidator for the time
		 */
		@SuppressWarnings("static-method")
		public InputValidator validateTime_v () {
			return new InputValidator(new InputValidator.CustomValidator () {
				@Override
				public boolean validate(String originalValue, String proposedValue) {
					try {
						return Double.parseDouble(Planar.fixRadix(proposedValue)) >= 0;
					} catch (Exception e) {
						return false;
					}
				}
				
			});
		}
			
		/**
		 * Return an {@link InputValidator} for the text.
		 * @return {@link InputValidator} for the text
		 */
		@SuppressWarnings("static-method")
		public InputValidator validateMessage_v () {
			return new InputValidator(".*");
		}
		
		/**
		 * A TimedMessage can always be deleted.
		 * <br /> This method is only used by the {@link nl.tudelft.otsim.GUI.ObjectInspector}.
		 * @return Boolean; always true
		 */
		@SuppressWarnings("static-method")
		public boolean mayDeleteMessage_d() {
			return true;
		}
		
	}
}

