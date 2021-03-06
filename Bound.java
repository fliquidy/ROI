package ROI;

/**
 * Created by Kaiyu on 6/5/2016.
 */
public class Bound {
    double _currentValue;
    double _pastValue;
    double _upperbound;
    public Bound(){
        setColdUB(0, 0);
        setHotUB(0);
    }
    public double upperBound(){
        return 2 * _currentValue > _upperbound ? _upperbound : 2 * _currentValue;
    }

    public void setColdUB(double current, double past){
        _currentValue = current;
        _pastValue = past;
    }
    public void setHotUB(double ub){
        _upperbound = ub;
    }


    public void updateNew(double value){
        _currentValue += value/Config._currentWindow;
        _upperbound += value/Config._currentWindow + value/Config._pastWindow;
    }

    public void updateOld(double value){
        _currentValue -= value/Config._currentWindow;
        _pastValue += value/Config._pastWindow;
        double change = value/Config._pastWindow - value/Config._currentWindow;
        if(change > 0){
            _upperbound += change;
        }
    }

    public void updateExpired(double value){
        _pastValue -= value/Config._pastWindow;
        _upperbound += value/Config._pastWindow;
    }

    public void update(SpatialObject o, ObjectType ot){
        switch (ot){
            case New: updateNew(o._weight);
                break;
            case Old: updateOld(o._weight);
                break;
            case Expired: updateExpired(o._weight);
                break;
        }
    }
    public String toString(){
        return String.format("[%f, %f | %f]", _currentValue, _pastValue, _upperbound);
    }
}
