package ROI;
import java.util.Objects;

public class Cell {
	public int _x;
	public int _y;
	public Cell(Cell c){
		this._x = c._x;
		this._y = c._y;
	}
	public Cell(int x, int y){
		this._x = x;
		this._y = y;
	}
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
	public void clone(Cell c){
		this._x = c._x;
		this._y = c._y;
	}
	public Cell up(){
		return new Cell(this._x, this._y+1);
	}
	public Cell right(){
		return new Cell(this._x+1, this._y);
	}
	public Cell upright(){
		return new Cell(this._x+1, this._y+1);
	}
	
}
