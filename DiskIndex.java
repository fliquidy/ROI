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
    public HashMap<Cell, LinkedList<SpatialObject>> _cacheObj;
    public HashMap<Cell, Integer> _time;
    public double _cacheSize;

    public static String _dbName = "c:\\users\\kaiyu\\Documents\\roi";
    public static String _cellObjName = "cell_list";

    public DiskIndex(){
        clean();
        _db = DBMaker.newFileDB(new File(_dbName)).closeOnJvmShutdown().cacheDisable().make();
        //_db = DBMaker.newMemoryDB().make();
        _cacheObj = new HashMap<Cell, LinkedList<SpatialObject>>();
        _ubm = new UpperboundManager();
        _ubm.isMaxHeap = true;
        _time = new HashMap<Cell, Integer>();
        _cellObjMap = _db.createTreeMap(_cellObjName).makeOrGet();
        _cacheSize = 0;
    }
    public void clean(){
        File f1 = new File(_dbName);
        if(f1.exists()){
            f1.delete();
        }
        File f2 = new File(_dbName+".t");
        if(f2.exists()){
            f2.delete();
        }
        File f3 = new File(_dbName+".p");
        if(f3.exists()){
            f3.delete();
        }
    }
    public boolean containCell(Cell c){
        return _ubm.containCell(c);
    }
    public void printCells(){
        System.out.print("Cells on disk: ");
        for(Cell c:_ubm.mymap.keySet()){
            System.out.print("("+c.toString()+", "+_ubm.getUB(c).upperBound()+") ");
        }
        System.out.println();
    }
    public void flush(){
        for(Cell c:_cacheObj.keySet()){
            LinkedList<SpatialObject> list = _cacheObj.get(c);
            if(list.size() == 0){
                continue;
            }
            if(_time.containsKey(c) && StorageManager.currentTime - _time.get(c) >= (Config._pastWindow + Config._currentWindow)){
                _cellObjMap.put(c, list);
            }
            else{
                LinkedList<SpatialObject> l = _cellObjMap.get(c);
                if(l == null){
                    l = list;
                }
                else{
                    while((!l.isEmpty()) && (StorageManager.currentTime - l.getFirst()._time >= (Config._pastWindow + Config._currentWindow))){
                        l.removeFirst();
                    }
                    l.addAll(list);
                }

                _cellObjMap.put(c, l);
            }
            int time = list.getLast()._time;
            _time.put(c, time);
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
        _ubm.addCell(memC, ub);
        _cacheObj.put(memC, tl.getListOfSpatialObject());
        _cacheSize += tl._spaceCost;
        if(_cacheSize > Config._cacheConstraint){
            flush();
        }
    }
    public void remove(Cell c){
        _ubm.remove(c);
        if(_time.containsKey(c)){
            _time.remove(c);
        }
        if(_cacheObj.containsKey(c)){
            _cacheObj.remove(c);
        }
        if(_cellObjMap.containsKey(c)){
            _cellObjMap.remove(c);
        }
    }
    public TwoWindowLists getTwoWindowLists(Cell c){
        TwoWindowLists tl = new TwoWindowLists();
        LinkedList<SpatialObject> list = getList(c);
        if(list != null){
        for(SpatialObject o : list){
            if(StorageManager.currentTime - o._time < Config._currentWindow + Config._pastWindow
                    && StorageManager.currentTime - o._time >= Config._currentWindow){
                tl._pastWindow.addLast(o);
                tl._pastSum += o._weight / Config._pastWindow;
                tl._spaceCost += o.size();
            }
            else if(StorageManager.currentTime - o._time < Config._currentWindow){
                tl._currentWindow.addLast(o);
                tl._currentSum += o._weight/Config._currentWindow;
                tl._spaceCost += o.size();
            }
        }
        }
        if(_cacheObj.containsKey(c)) {
            for (SpatialObject o : _cacheObj.get(c)) {
                if (StorageManager.currentTime - o._time < Config._currentWindow + Config._pastWindow
                        && StorageManager.currentTime - o._time >= Config._currentWindow) {
                    tl._pastWindow.addLast(o);
                    tl._pastSum += o._weight / Config._pastWindow;
                    tl._spaceCost += o.size();
                } else if (StorageManager.currentTime - o._time < Config._currentWindow) {
                    tl._currentWindow.addLast(o);
                    tl._currentSum += o._weight / Config._currentWindow;
                    tl._spaceCost += o.size();
                }
            }
        }
        return tl;
    }
    public void commit(){
        _db.commit();
    }
    public void insertIntoIndex(SpatialObject o, Cell c, ObjectType t){
        Cell cc = new Cell(1, -6);
        LinkedList<SpatialObject> l = getList(cc);
        LinkedList<SpatialObject> list;
        if(_cacheObj.containsKey(c)){
            list = _cacheObj.get(c);
        }
        else{
            list = new LinkedList<SpatialObject>();
            _cacheObj.put(c, list);
        }
        if(t == ObjectType.New){
            list.addLast(o);
            _cacheSize += o.size();
        }
       // if(t == ObjectType.Expired){
        //    if((!list.isEmpty()) && list.getFirst()._id == o._id){
         //       list.removeFirst();
          //  }
        //}
        while((!list.isEmpty()) && StorageManager.currentTime - list.getFirst()._time >= Config._currentWindow + Config._pastWindow){
            _cacheSize -= list.getFirst().size();
            list.removeFirst();
        }
        _ubm.updateUBforCell(c, o, t);
        if(_cacheSize >= Config._cacheConstraint){
            flush();
        }


    }

}
