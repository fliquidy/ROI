package ROI;
import org.mapdb.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.io.File;

/**
 * Created by kaiyu on 5/8/2016.
 */
public class DiskIndex {
    public UpperboundManager _ubm;
    public DB _db;
    public HashMap<Cell, TwoWindowLists> _cacheObj;
    public HashMap<Cell, Integer> _time;
    public double _cacheSize;

    public DiskIndex(){
        _db = DBMaker.newFileDB(new File("roi")).closeOnJvmShutdown().cacheDisable().make();
        _cacheObj = new HashMap<Cell, TwoWindowLists>();
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
        return _ubm.getMaxUB();
    }
    public double maxUB(){
        return _ubm.getMax();
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
            _cacheObj.remove(c);
        }
        _db.commit();
        _cacheSize = 0;
    }
    public void processObject(SpatialObject o, Cell c){
        //process cells affected by object o and are maintained in disk
    }

    /******************************************************/

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
