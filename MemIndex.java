package ROI;
import java.io.File;
import java.util.*;
import java.util.LinkedList;
import org.mapdb.*;
/**
 * Created by kaiyu on 5/8/2016.
 */
public class  MemIndex {
    //best result
    public boolean updatedResult;
    public SpatialObject maxPosition;
    public boolean isValid;

    //index and upperbounds
    public UpperboundManager ubm;
    public HashMap<Cell, TwoWindowLists> exactIndex;

    public MemIndex(){
        ubm = new UpperboundManager();
        exactIndex = new HashMap<>();
    }
    public void write(Cell c, LinkedList<SpatialObject> l){
        TwoWindowLists tl = new TwoWindowLists();
        for(SpatialObject o : l){
            if(StorageManager.currentTime - o._time >= StorageManager.currentWindow){
                tl._pastWindow.addLast(o);
            }
            else{
                tl._currentWindow.addLast(o);
            }
        }
        exactIndex.put(c, tl);
    }
    public void remove(Cell c){
        exactIndex.remove(c);
    }


    public void processObject(SpatialObject o, Cell c){
        //process cells which are affected by object and are maintained in memory.
    }

    public void searchCell(double a, double b, UpperBound ub){
        Cell c = ub._c;
        TwoWindowLists tl = exactIndex.get(c);
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
        if((!isValid) || (bi.maxScore > maxPosition._weight)){
            maxPosition._weight = bi.maxScore;
            maxPosition._x = bi.maxX;
            maxPosition._y = bi.maxY;
            updatedResult = true;
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
        updatedResult = false;
        while(ubm.getMax() > maxPosition._weight){
            UpperBound ub = ubm.getMaxUB();
            searchCell(a, b, ub);
        }
    }
    public void insertIntoIndex(SpatialObject o, Cell c, ObjectType t){
        //add into index, update upper bounds.
        if(exactIndex.containsKey(c)){
            exactIndex.get(c).addObject(o, t);
            ubm.updateUBforCell(c, o, t);
        }
        else{
            System.err.println("Cell "+c.toString()+" is not in memory.");
        }
        if(o.locateCell(Config._a, Config._b).equals(maxPosition.locateCell(Config._a, Config._b))){
            isValid = false;
        }
    }
}
