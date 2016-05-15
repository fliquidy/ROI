package ROI;
import org.mapdb.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.io.File;

/**
 * Created by kaiyu on 5/8/2016.
 */
public class DiskIndex {
    public UpperboundManager ubm;
    public DB db;
    public double cacheSize;
    public TwoWindowLists cacheObj;
    public HashMap<Cell, LinkedList<SpatialObject>> cacheDetails;
    public HashMap<SpatialObject, LinkedList<Cell>> affectedCells;

    public DiskIndex(){
        db = DBMaker.newFileDB(new File("roi")).closeOnJvmShutdown().cacheDisable().make();
        cacheObj = new TwoWindowLists();
        affectedCells = new HashMap<>();
        cacheDetails = new HashMap<>();
    }
    public LinkedList<SpatialObject> retrieve(Cell c){
        BTreeMap<Cell, LinkedList<SpatialObject>> comap = db.createTreeMap("DCell_object").makeOrGet();
        LinkedList<SpatialObject> l = comap.get(c);
        return l;
    }
    public void write(Cell c, LinkedList<SpatialObject> l){
        BTreeMap<Cell, LinkedList<SpatialObject>> comap = db.createTreeMap("DCell_object").makeOrGet();
        comap.put(c, l);
        db.commit();
    }
    public UpperBound getMax(){
        return ubm.getMaxUB();
    }
    public double maxUB(){
        return ubm.getMax();
    }

    public void insertIntoIndex(SpatialObject o, Cell c, ObjectType t){
        if(t == ObjectType.New){
            cacheObj.add(o);
            ubm.updateUB(c, o._weight/StorageManager.currentWindow, -1, ObjectType.New);
            cacheSize += o.size();
        }
        else if(t == ObjectType.Old){
            cacheObj.transform(o._weight/StorageManager.pastWindow);
            ubm.updateUB(c, 0 - o._weight/StorageManager.currentWindow, -1, ObjectType.New);
            ubm.updateUB(c, o._weight/StorageManager.pastWindow, -1, ObjectType.Old);
        }
        else{
            ubm.updateUB(c, 0 - o._weight/StorageManager.pastWindow, -1, ObjectType.Old);
            cacheObj.remove();
            cacheSize -= o.size();
        }
        if(cacheSize > StorageManager.config.cacheSize){
            flush();
        }

    }
    public void flush(){
        BTreeMap<Cell, LinkedList<SpatialObject>> comap = db.createTreeMap("DCell_object").makeOrGet();
        for(Cell c:cacheDetails.keySet()){
            LinkedList<SpatialObject> l = comap.get(c);
            while((!l.isEmpty()) &&
                    (StorageManager.currentTime - l.getFirst()._time > StorageManager.validWindow)){
                l.removeFirst();
            }
            l.addAll(cacheDetails.get(c));
            comap.put(c, l);
        }
        db.commit();
    }
    public void processObject(SpatialObject o, Cell c){
        //process cells affected by object o and are maintained in disk
    }
}
