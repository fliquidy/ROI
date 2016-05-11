package ROI;
import java.io.File;
import java.util.*;
import java.util.LinkedList;
import org.mapdb.*;
/**
 * Created by kaiyu on 5/8/2016.
 */
public class MemIndex {
    //best result
    public boolean updatedResult;
    public SpatialObject maxPosition;

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
    public void processObject(SpatialObject o, Cell c){
        //process cells which are affected by object and are maintained in memory.
    }
    public void search(){

    }
    public void searchCell(Cell c){
        TwoWindowLists tl = exactIndex.get(c);
        int objectNum = tl.size();
        double[] coords = new double[objectNum];
        for(int idx = 0; idx < tl.pastWindow.size(); idx ++){
            coords[idx] = tl.pastWindow.get(idx)._x;
        }
        for(int idx = 0; idx < tl.currentWindow.size(); idx++){
            coords[idx + tl.pastWindow.size()] = tl.currentWindow.get(idx)._x;
        }
        BurstInterval bi = new BurstInterval(coords);

    }
}
