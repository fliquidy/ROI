package ROI;

/**
 * Created by kaiyu on 5/4/2016.
 */
public class UpperBound {
    public double upperbound;
    public double updatetime;
    public Cell c;
    public UpperBound(double ub, double ut, Cell cell){
        upperbound = ub;
        updatetime = ut;
        c = cell;
    }
    public UpperBound(){
        upperbound = 0;
        updatetime = 0;
    }
    public void update(double ub, double ut){
        upperbound = ub;
        updatetime = ut;
    }

}
