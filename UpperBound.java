package ROI;

/**
 * Created by kaiyu on 5/4/2016.
 */
public class UpperBound {
    public Bound bound;
    public Point p;
    public boolean isExact;
    public Cell c;
    public UpperBound(double current, double past, Cell cell){
        bound = new Bound(current, past);
        c = new Cell(cell);

    }
    public UpperBound(){
        bound = new Bound();
        isExact = false;
    }
    public void updateCurrentUB(double ub){
        bound.addCurrent(ub);
        isExact = false;
    }
    public void updatePastUB(double ub){
        bound.addPast(ub);
        isExact = false;
    }
    public void updateExactUB(double current, double past){
        bound.set(current, past);
        isExact = true;
    }
    public void set(Cell newCell, double current, double past){
        c = new Cell(newCell);
        bound.set(current, past);
    }
    public UpperBound copy(){
        return new UpperBound(bound._currentValue, bound._pastValue, c);
    }

}
