package ROI;
import org.mapdb.*;
import sun.awt.image.ImageWatched;

import java.util.HashMap;
import java.util.LinkedList;
import java.io.File;

/**
 * Created by kaiyu on 5/8/2016.
 */
public class DiskIndex {
    public UpperboundManager _ubm;
    public DB _db;
    public BTreeMap<Cell, LinkedList<SpatialObject>> _cellObjMap;
    public HashMap<Cell, TwoWindowLists> _cacheObj;
    public HashMap<Cell, Integer> _time;
    public double _cacheSize;

    public static String _dbName = "roi";
    public static String _cellObjName = "cell_list";

    public DiskIndex(){
        _db = DBMaker.newFileDB(new File(_dbName)).closeOnJvmShutdown().cacheDisable().make();
        _cacheObj = new HashMap<Cell, TwoWindowLists>();
        _ubm = new UpperboundManager();
        _cellObjMap = _db.createTreeMap(_cellObjName).makeOrGet();
        _cacheSize = 0;
    }
    public LinkedList<SpatialObject> retrieve(Cell c){
        BTreeMap<Cell, LinkedList<SpatialObject>> comap = _db.createTreeMap("DCell_object").makeOrGet();
        LinkedList<SpatialObject> l = comap.get(c);
        return l;
    }
    public void write(Cell c, LinkedList<SpatialObject> l){
        BTreeMap<Cell, LinkedList<SpatialObject>> comap = _db.createTreeMap("DCell_object").makeOrGet();
        comap.put(c, l);
        _db.commit();
    }
    public UpperBound getMax(){
        return _ubm.getTopUB();
    }
    public double maxUB(){
        return _ubm.getTopUB().upperBound();
    }


    public void flush(){
        BTreeMap<Cell, LinkedList<SpatialObject>> comap = _db.createTreeMap("DCell_object").makeOrGet();
        for(Cell c:_cacheObj.keySet()){
            TwoWindowLists tl = _cacheObj.get(c);
            if(StorageManager.currentTime - _time.get(c) > (Config._pastWindow + Config._currentWindow)){
                comap.put(c, tl.getListOfSpatialObject());
            }
            else{
                LinkedList<SpatialObject> l = comap.get(c);
                while((!l.isEmpty()) && (StorageManager.currentTime - l.getFirst()._time > (Config._pastWindow + Config._currentWindow))){
                    l.removeFirst();
                }
                l.addAll(tl.getListOfSpatialObject());
                comap.put(c, l);
            }
            _time.put(c, tl.mostRecentTime());
        }
        _cacheObj.clear();
        _db.commit();
        _cacheSize = 0;
    }


    /******************************************************/
    public double getMaxUBValue(){
        return _ubm.getTopValue();
    }
    public boolean isEmpty(){
        return _ubm.isEmpty();
    }
    public Cell getMaxUBCell(){
        return _ubm.getTopUB()._c;
    }
    public UpperBound getMaxUB(){
        return _ubm.getTopUB();
    }
    public LinkedList<SpatialObject> getList(Cell c){
        return _cellObjMap.get(c);
    }
    public void loadIntoDisk(Cell memC, UpperBound ub, TwoWindowLists tl){
        Cell diskC = _ubm.getTopUB()._c;
        _ubm.addCell(diskC, ub);
        _cellObjMap.put(memC, tl.getListOfSpatialObject());
    }
    public void remove(Cell c){
        _ubm.remove(c);
    }
    public void writeToMemory(Cell diskC, MemIndex memIdx){

    }
    public void commit(){
        _db.commit();
    }
    public void insertIntoIndex(SpatialObject o, Cell c, ObjectType t){
        TwoWindowLists list = null;
        if(_cacheObj.containsKey(c)){
            list = _cacheObj.get(c);
        }
        else{
            list = new TwoWindowLists();
            _cacheObj.put(c, list);
        }
        list.addObject(o, t);
        _ubm.updateUBforCell(c, o, t);
        switch (t){
            case New: _cacheSize += o.size();
                break;
            case Expired: _cacheSize -= o.size();
                break;
            default:
                break;
        }
        if(_cacheSize >= Config._cacheConstraint){
            flush();
        }


    }

}
