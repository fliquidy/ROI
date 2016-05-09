package ROI;

/**
 * Created by kaiyu on 5/4/2016.
 */
public class UpperBound {
    public double upperbound;
    private double currentUB;
    private double pastUB;
    public double updatetime;
    public Cell c;
    public UpperBound(double ub, double ut, Cell cell){
        upperbound = ub;
        updatetime = ut;
        c = new Cell(cell);
    }
    public UpperBound(){
        upperbound = 0;
        updatetime = 0;
    }
    public void updateCurrentUB(double ub){
        currentUB += ub;
        upperbound = currentUB>pastUB?currentUB:pastUB;
        upperbound += currentUB;
    }
    public void updatePastUB(double ub){
        pastUB += ub;
        upperbound = currentUB>pastUB?currentUB:pastUB;
        upperbound += currentUB;
    }
    public void update(double ub){
        upperbound = ub;
    }
    public void update(double ub, double ut){
        upperbound = ub;
        updatetime = ut;
    }
    public void update(Cell newc, double ub, double ut){
        c = new Cell(newc);
        upperbound = ub;
        updatetime = ut;
    }
    public void update(Cell newc, double ub){
        c = new Cell(newc);
        upperbound = ub;
    }
    public UpperBound copy(){
        UpperBound ub = new UpperBound(this.upperbound, this.updatetime, this.c);
        return ub;
    }

}
