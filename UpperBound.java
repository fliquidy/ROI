package ROI;

/**
 * Created by kaiyu on 5/4/2016.
 */
public class UpperBound {
    public static int size;
    public double _bound;
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
        _bound = 0;
        _p = new Point(0, 0, 0);
        _isExact = false;
        _c = new Cell(0, 0);
    }
    public void setCell(Cell newCell){
        _c._x = newCell._x;
        _c._y = newCell._y;
    }
    public void setBound(double value){
        _bound = value;
    }

    public void setPoint(double x, double y){
        _p._x = x;
        _p._y = y;
    }
    public double upperBound(){
        return _bound;
    }
    public boolean larger(UpperBound ub){
        //TODO
        if(_bound > ub._bound)return true;
        if(this._isExact && (!ub._isExact))return true;
        return false;
    }
    public void update(SpatialObject o, ObjectType ot){
        switch (ot){
            case New:
                _bound += 2 * o._weight/Config._currentWindow;
                break;
            case Old:
                _bound -= 2 * o._weight/Config._currentWindow;
                break;
            case Expired:
                break;
        }
    }
    public boolean smaller(UpperBound ub){
        //TODO: check which is better: with a exact small upper bound or with an approximate small upper bound
        if(_bound < ub._bound)return true;
        if((!this._isExact) && ub._isExact)return true;
        return false;

    }
    public void copy(UpperBound ub){
        setBound(ub._bound);
        setPoint(ub._p._x, ub._p._y);
        setCell(ub._c);
    }

}
