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

    public double size;

    public MemIndex(){
        _ubm = new MemUBM();
        _exactIndex = new HashMap<>();
        size = 0;
        _maxPosition = new SpatialObject();
    }




    public void searchCell(double a, double b, Cell c){
        System.out.println("Searching "+c.toString());
        TwoWindowLists tl = _exactIndex.get(c);
        int objectNum = tl.size();
        LinkedList<Interval> intervals = tl.getIntervals(a, b, c._y * a, c._y * a + a);
        double[] coords = tl.getXCoords(b, c._x*b, c._x*b + b);
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
        System.out.println("maxScore: "+bi.maxScore);
        Point p = new Point(bi.maxX, bi.maxY, bi.maxScore);
        if((!_isValid) || (bi.maxScore > _maxPosition._weight)){
            _maxPosition._weight = bi.maxScore;
            _maxPosition._x = bi.maxX;
            _maxPosition._y = bi.maxY;
            _updatedResult = true;
            _isValid = true;
        }
        _ubm.setExactBound(c, bi.maxScore);
        _ubm.setPoint(c, p);
        checkObjects(c, p);
        System.out.println(_ubm.getUB(c)._coldBound+" "+_ubm.getUB(c)._hotBound);
        System.out.println(_exactIndex.get(c).size());
    }
    public void printCells(){
        System.out.print("Cells in memory: ");
        for(Cell c:_exactIndex.keySet()){
            System.out.print("("+c.toString()+", "+_ubm.getUBValue(c)+", "+_exactIndex.get(c).size()+") ");
        }
        System.out.println();

    }
    public void listObjectsInCell(Cell c){
        System.out.print("Cell "+c.toString()+" ");
        LinkedList<SpatialObject> list = _exactIndex.get(c).getListOfSpatialObject();
        System.out.print("past window:");
        for(SpatialObject o:_exactIndex.get(c)._pastWindow){
            System.out.print(" id: "+o._id);
        }
        System.out.println();
        System.out.print("current window:");
        for(SpatialObject o:_exactIndex.get(c)._currentWindow){
            System.out.print(" id: "+o._id);
        }
        System.out.println();
    }
    public void checkObjects(Cell c, Point p){
        ListIterator<SpatialObject> it = _exactIndex.get(c)._currentWindow.listIterator();
        System.out.println("@#@#");
        System.out.print("Current:");
        while(it.hasNext()){
            SpatialObject o = it.next();
            if(Math.abs(o._x - p._x) <= Config._b && Math.abs(o._y - p._y) <= Config._a){
                System.out.print(" "+o._id);
            }
        }
        System.out.println();
        it = _exactIndex.get(c)._pastWindow.listIterator();
        System.out.print("past:");
        while(it.hasNext()){
            SpatialObject o = it.next();
            if(Math.abs(o._x - p._x) <= Config._b && Math.abs(o._y - p._y) <= Config._a){
                System.out.print(" "+o._id);
            }
        }
        System.out.println("\n@#@#");
    }

    /*******************************************/
    public boolean containCell(Cell c){
        return _exactIndex.containsKey(c);
    }
    public UpperBound getMinUB(){
        return _ubm.getMinUB();
    }
    public UpperBound getUB(Cell c){
        return _ubm.getUB(c);
    }
    public double getMinUBValue(){
        return _ubm.getMinUB().upperBound();
    }
    public Cell getMinUBCell(){
        return _ubm.getMinUB()._c;
    }
    public void search(double a, double b){
        /*search all cells whose upper bound is larger than the current result.
        return true if current result is updated, and false otherwise
        */
        while((!_isValid) || _ubm.getMaxUB().upperBound() > _maxPosition._weight){
            UpperBound ub = _ubm.getMaxUB();
            System.out.println(_ubm.size()+" ubs in memory.");
            searchCell(a, b, ub._c);
        }
    }
    public boolean needToSearch(){
        return (!_isValid) || (_maxPosition._weight < _ubm.getMaxUB().upperBound());
    }
    public boolean insertIntoIndex(SpatialObject o, Cell c, ObjectType t){
        //add into index, update upper bounds.
        if(_exactIndex.containsKey(c)){
            _exactIndex.get(c).addObject(o, t);
            _ubm.updateUBforCell(c, o, t);
            if(_exactIndex.get(c).size() == 0){
                removeFromMemory(c);
            }
        }
        else{
            TwoWindowLists tl = new TwoWindowLists();
            tl.addObject(o, t);
            _exactIndex.put(c, tl);
            _ubm.updateUBforCell(c, o, t);
        }
        switch(t){
            case New: size += o.size();
                break;
            case Expired: size -= o.size();
                break;
            default:
                break;
        }
        Cell c1 = o.locateCell(Config._a, Config._b);
        Cell c2 = _maxPosition.locateCell(Config._a, Config._b);
        if(_maxPosition != null &&
                c.equals(_maxPosition.locateCell(Config._a, Config._b))){
            _isValid = false;
        }
        return size >= Config._memoryConstraint;
    }
    public void removeFromMemory(Cell c){
        size -= _exactIndex.get(c).spaceCost();
        _exactIndex.remove(c);
        _ubm.remove(c);

    }
    public void loadIntoMemory(Cell diskC, UpperBound ub, TwoWindowLists tl){
        _ubm.addCell(diskC, ub);
        _exactIndex.put(diskC, tl);
        size += tl.spaceCost();
    }

    public void moveMinCellToDisk(DiskIndex diskIdx){
        Cell c = new Cell(_ubm.getMinUB()._c);
        diskIdx.loadIntoDisk(c, _ubm.getMinUB(), _exactIndex.get(c));
        removeFromMemory(c);
    }

    public double size(){
        return size;
    }
}
