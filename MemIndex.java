package ROI;
import java.util.*;
import java.util.LinkedList;

/**
 * Created by kaiyu on 5/8/2016.
 */
public class  MemIndex {
    //best result
    public boolean _updatedResult;
    public SpatialObject _maxPosition;
    public boolean _isValid;

    //index and upperbounds
    public MemUBM _ubm;
    public HashMap<Cell, TwoWindowLists> _exactIndex;

    public MemIndex(){
        _ubm = new MemUBM();
        _exactIndex = new HashMap<>();
    }
    public void remove(Cell c){
        _exactIndex.remove(c);
    }


    public void processObject(SpatialObject o, Cell c){
        //process cells which are affected by object and are maintained in memory.
    }

    public void searchCell(double a, double b, UpperBound ub){
        Cell c = ub._c;
        TwoWindowLists tl = _exactIndex.get(c);
        int objectNum = tl.size();
        LinkedList<Interval> intervals = tl.getIntervals(a, b);
        double[] coords = tl.getXCoords(b);
        BurstInterval bi = new BurstInterval(coords);
        Iterator<Interval> it = intervals.listIterator();
        Interval current = null;
        if(it.hasNext())
            current = it.next();
        while(it.hasNext()){
            Interval next = it.next();
            bi.insertInterval(current, next.y);
            current = next;
        }
        bi.insertInterval(current, current.y+1.0);
        Point p = new Point(bi.maxX, bi.maxY, bi.maxScore);
        if((!_isValid) || (bi.maxScore > _maxPosition._weight)){
            _maxPosition._weight = bi.maxScore;
            _maxPosition._x = bi.maxX;
            _maxPosition._y = bi.maxY;
            _updatedResult = true;
            _isValid = true;
        }
        ub._bound.setHotUB(bi.maxScore);
        ub._bound.setExact(true);
        ub._bound.setColdUB(tl._currentSum, tl._pastSum);
        ub._p = p;
    }

    /*******************************************/

    public void search(double a, double b){
        /*search all cells whose upper bound is larger than the current result.
        return true if current result is updated, and false otherwise
        */
        _updatedResult = false;
        while(_ubm.getMaxUB().upperBound() > _maxPosition._weight || (!_isValid)){
            UpperBound ub = _ubm.getMaxUB();
            searchCell(a, b, ub);
        }
    }
    public boolean safeToSkipSearch(){
        return _isValid && _maxPosition._weight >= _ubm.getMaxUB().upperBound();
    }
    public void insertIntoIndex(SpatialObject o, Cell c, ObjectType t){
        //add into index, update upper bounds.
        if(_exactIndex.containsKey(c)){
            _exactIndex.get(c).addObject(o, t);
            _ubm.updateUBforCell(c, o, t);
        }
        else{
            System.err.println("Cell "+c.toString()+" is not in memory.");
        }
        if(o.locateCell(Config._a, Config._b).equals(_maxPosition.locateCell(Config._a, Config._b))){
            _isValid = false;
        }
    }
    public void loadIntoMemory(Cell diskC, UpperBound ub, LinkedList<SpatialObject> list){
        Cell memC = _ubm.getMinUB()._c;
        _ubm.updateCell(memC, ub);
        TwoWindowLists tl = new TwoWindowLists();
        tl.load(list, StorageManager.currentTime);
        _exactIndex.put(diskC, tl);
        _exactIndex.remove(memC);
    }
}
