package ROI;

/**
 * Created by kaiyu on 5/4/2016.
 */
public class UpperBound {
    public Bound _bound;
    public Point _p;
    public boolean _isExact;
    public Cell _c;
    public UpperBound(){
        init();
    }
    public UpperBound(Cell c){
        init();
        _c = new Cell(c);
    }
    public void init(){
        _bound = new Bound();
        _p = null;
        _isExact = false;
        _c = null;
    }
    public void setCell(Cell newCell){
        _c = new Cell(newCell);
    }
    public void setBound(double currentValue, double pastValue, double upperbound){
        _bound.setHotUB(upperbound);
        _bound.setColdUB(currentValue, pastValue);
    }
    public void setPoint(double x, double y){
        _p._x = x;
        _p._y = y;
    }

}
