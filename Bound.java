package ROI;

/**
 * Created by Kaiyu on 6/5/2016.
 */
public class Bound {
    double _currentValue;
    double _pastValue;
    boolean isValid;
    public Bound(){set(0, 0);}
    public Bound(double currentValue, double pastValue){
        set(currentValue, pastValue);
    }
    public double upperBound(){
        return _currentValue + _currentValue > _pastValue?_currentValue:_pastValue;
    }
    public boolean valid(){return isValid;}
    public void set(double current, double past){
        _currentValue = current;
        _pastValue = past;
    }
    public void setValid(boolean valid){
        isValid = valid;
    }
    public void addCurrent(double current){
        _currentValue += current;
    }
    public void addPast(double past){
        _pastValue += past;
    }
}
