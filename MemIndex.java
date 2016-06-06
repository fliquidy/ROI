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
    public UpperboundManager ubmin;
    public HashMap<Cell, TwoWindowLists> exactIndex;

    public MemIndex(){
        ubm = new UpperboundManager();
        ubmin = new UpperboundManager();
        exactIndex = new HashMap<>();
    }
    public void setUB(Cell oldc, Cell newc, double value, double time){
        ubm.updateCellUB(oldc, newc, value, time);
        ubmin.updateCellUB(oldc, newc, value, time);
    }
    public void increaseUB(Cell c, double value, double time, ObjectType t){
        ubm.updateUB(c, value, time, t);
        ubmin.updateUB(c, 0-value, time, t);
    }
    public UpperBound getMin(){
        return ubmin.getMaxUB();
    }
    public double minUB(){
        return 0 - ubmin.getMax();
    }
    public void write(Cell c, LinkedList<SpatialObject> l){
        TwoWindowLists tl = new TwoWindowLists();
        for(SpatialObject o : l){
            if(StorageManager.currentTime - o._time >= StorageManager.currentWindow){
                tl.pastWindow.addLast(o);
            }
            else{
                tl.currentWindow.addLast(o);
            }
        }
        exactIndex.put(c, tl);
    }
    public void remove(Cell c){
        exactIndex.remove(c);
    }
    public LinkedList<SpatialObject> retrieve(Cell c){
        TwoWindowLists tl = exactIndex.get(c);
        LinkedList<SpatialObject> l = new LinkedList<>(tl.pastWindow);
        l.addAll(tl.currentWindow);
        return l;
    }

    public void processObject(SpatialObject o, Cell c){
        //process cells which are affected by object and are maintained in memory.
    }

    public Point searchCell(Cell c, double a, double b){
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
        }
        return p;
    }

    /*******************************************/

    public boolean search(double a, double b){
        /*search all cells whose upper bound is larger than the current result.
        return true if current result is updated, and false otherwise
        */
        while(ubm.getMax() > maxPosition._weight){
            Cell c = ubm.getMaxUB().c;
            Point p = searchCell(c, a, b);

        }
        return false;
    }
    public void insertIntoIndex(SpatialObject o, Cell c, ObjectType t){
        //add into index, update upper bounds.
        if(exactIndex.containsKey(c)){
            if(t == ObjectType.New) {
                //add to index
                exactIndex.get(c).add(o);
                //ubm.updateUB(c, o._weight/StorageManager.currentWindow, -1, ObjectType.New);
                increaseUB(c, o._weight/StorageManager.currentTime, -1, ObjectType.New);
            }
            else if(t == ObjectType.Old){
                //update weight
                exactIndex.get(c).transform(o._weight/StorageManager.pastWindow);
//                ubm.updateUB(c, 0 - o._weight/StorageManager.currentWindow, -1, ObjectType.New);
//                ubm.updateUB(c, o._weight/StorageManager.pastWindow, -1, ObjectType.Old);
                increaseUB(c, 0-o._weight/StorageManager.currentTime, -1, ObjectType.New);
                increaseUB(c, o._weight/StorageManager.pastWindow, -1, ObjectType.Old);
            }
            else {
                //remove from index, doesn't affect the upper bound
//                ubm.updateUB(c, 0 - o._weight/StorageManager.pastWindow, -1, ObjectType.Old);
                increaseUB(c, 0 - o._weight/StorageManager.pastWindow, -1, ObjectType.Old);
                exactIndex.get(c).remove();
            }
        }
        else{
            System.err.println("Cell "+c.toString()+" is not in memory.");
        }
    }
}
