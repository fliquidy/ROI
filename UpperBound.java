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
        _p = new Point(0, 0, 0);
        _isExact = false;
        _c = new Cell(0, 0);
    }
    public void setCell(Cell newCell){
        _c._x = newCell._x;
        _c._y = newCell._y;
    }
    public void setBound(double currentValue, double pastValue, double upperbound){
        _bound.setHotUB(upperbound);
        _bound.setColdUB(currentValue, pastValue);
    }
    public double upperBound(){
        return _bound.upperBound();
    }
    public void setPoint(double x, double y){
        _p._x = x;
        _p._y = y;
    }
    public void copy(UpperBound ub){
        setBound(ub._bound._currentValue, ub._bound._pastValue, ub._bound._upperbound);
        _bound.setExact(ub._bound.isExact());
        setPoint(ub._p._x, ub._p._y);
        setCell(ub._c);
    }

}
