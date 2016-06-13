package ROI;
import java.util.Vector;

public class SpatialObject implements java.io.Serializable {
	public int _id;
	public int _time;
	public double _x;
	public double _y;
	public double _weight;
	public Vector<Integer> attributes;
	public SpatialObject(){
		
	}

	public SpatialObject(int id, int time, double x, double y, double weight){
		this._id = id;
		this._time = time;
		this._x = x;
		this._y = y;
		this._weight = weight;
		attributes = new Vector<Integer>();
	}
	
	public Cell locateCell(double a, double b){
		Cell c = new Cell(_x, _y, a, b);
		return c;
	}
	public double size(){
		//in KB
		return 1.0 * (3 * 4 + 3 * 8 + attributes.size() * 4)/1024;
	}
}
