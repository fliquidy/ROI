package ROI;
import java.util.Vector;

public class SpatialObject {
	public int _id;
	public int _date;
	public int _time;
	public double _x;
	public double _y;
	public double _weight;
	public Vector<Integer> attributes;
	public SpatialObject(){
		
	}
	public SpatialObject(int id, int date, int time, double x, double y, double weight){
		this._id = id;
		this._date = date;
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
}
