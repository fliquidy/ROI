package ROI;

/**
 * Created by kaiyu on 5/4/2016.
 */
public class UpperBound {
    public static int size;
    public double _coldBound;
    public double _hotBound;
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
        _coldBound = 0;
        _hotBound = 0;
        _p = new Point(0, 0, 0);
        _isExact = false;
        _c = new Cell(0, 0);
    }
    public void setCell(Cell newCell){
        _c._x = newCell._x;
        _c._y = newCell._y;
    }
    public void setColdBound(double value){
        _coldBound = value;
    }
    public void setHotBound(double value){
        _hotBound = value;
        _isExact = true;
    }

    public void setPoint(double x, double y){
        _p._x = x;
        _p._y = y;
    }
    public double upperBound(){
        if(_isExact){
            return _coldBound > _hotBound ? _hotBound:_coldBound;
        }
        else{
            return _coldBound;
        }
    }
    public boolean larger(UpperBound ub){
        //TODO
        if(upperBound() > ub.upperBound())return true;
        if(this._isExact && (!ub._isExact))return true;
        return false;
    }
    public void update(SpatialObject o, ObjectType ot){
        if(_c._x == 3 && _c._y == -3){
            System.out.println("**********");
            System.out.println(_hotBound+ " "+_coldBound);
        }
        boolean cover = false;
        if(Math.abs(o._x - _p._x) < Config._b && Math.abs(o._y - _p._y) < Config._a){
            cover = true;
        }
        switch (ot){

            case New:
                _coldBound += 2 * o._weight/Config._currentWindow;
                _hotBound += 2 * o._weight / Config._currentWindow;
                if(!cover){
                    _isExact = false;
                }
                break;
            case Old:
                _coldBound -= 2 * o._weight/Config._currentWindow;
                if(cover){
                    _hotBound -= o._weight/Config._currentWindow;
                }
                else{
                    _isExact = false;
                }
                break;
            case Expired:
                _hotBound += o._weight / Config._pastWindow;
                if(!cover){
                    _isExact = false;
                }
                break;
        }
        if(_c._x == 3 && _c._y == -3){
            System.out.println(_coldBound);
            System.out.println("**********");
        }
        if(upperBound() < -0.001){
            System.out.println("ERROR@@@"+_c.toString()+" "+_coldBound+" "+_hotBound+" "+_isExact);
            System.exit(0);
        }

    }
    public boolean smaller(UpperBound ub){
        //TODO: check which is better: with a exact small upper bound or with an approximate small upper bound
        if(upperBound() < ub.upperBound())return true;
        if((!this._isExact) && ub._isExact)return true;
        return false;

    }
    public void copy(UpperBound ub){
        setColdBound(ub._coldBound);
        setHotBound(ub._hotBound);
        setPoint(ub._p._x, ub._p._y);
        setCell(ub._c);
    }

}
