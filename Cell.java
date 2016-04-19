package ROI;
import java.util.Objects;

public class Cell {
	public int _x;
	public int _y;
	public Cell(double x, double y, double a, double b){
		_x = (int)(x/b);
		_y = (int)(y/a);
	}
	public boolean equals(Object obj){
		if(obj instanceof Cell){
			Cell c = (Cell)obj;
			return _x == c._x && _y == c._y;
		}
		return false;
	}
	public int hashCode(){
		return Objects.hash(_x, _y);
	}
	
}
