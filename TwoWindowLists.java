package ROI;

import sun.awt.image.ImageWatched;

import java.util.*;

/**
 * Created by kaiyu on 5/9/2016.
 */
public class TwoWindowLists {
    //store the spatial objects in each cell in two seperate lists for past and current time windows
    public LinkedList<SpatialObject> _currentWindow;
    public LinkedList<SpatialObject> _pastWindow;
    public double _currentSum;
    public double _pastSum;
    public double _spaceCost;
    public TwoWindowLists(){
        _currentWindow = new LinkedList<>();
        _pastWindow = new LinkedList<>();
        _currentSum = 0;
        _pastSum = 0;
        _spaceCost = 0;
    }
    public void add(SpatialObject o){
        _currentWindow.addLast(o);
        _currentSum += o._weight/Config._currentWindow;
        _spaceCost += o.size();
    }
    public void remove(SpatialObject o){
        if(!_pastWindow.isEmpty()){
            SpatialObject o1 = _pastWindow.getFirst();
            if(o1.equals(o)){
                _pastSum -= o1._weight/Config._pastWindow;
                _spaceCost -= o1.size();
                _pastWindow.removeFirst();
            }
        }
    }
    public void transform(SpatialObject oldO){
        SpatialObject o = _currentWindow.getFirst();
        _pastWindow.addLast(o);
        _currentWindow.removeFirst();
        _currentSum -= o._weight/Config._currentWindow;
        _pastSum += o._weight/Config._pastWindow;
    }
    public void addObject(SpatialObject o, ObjectType ot){
        switch(ot){
            case New: add(o);
                break;
            case Old: transform(o);
                break;
            case Expired: remove(o);
                break;
        }
    }

    public void load(LinkedList<SpatialObject> list, int currentTime){
        for(SpatialObject o : list){
            if(currentTime - o._time < Config._currentWindow + Config._pastWindow
                    && currentTime - o._time > Config._currentWindow){
                _pastWindow.addLast(o);
                _pastSum += o._weight / Config._pastWindow;
                _spaceCost += o.size();
            }
            else if(currentTime - o._time < Config._currentWindow){
                _currentWindow.addLast(o);
                _currentSum += o._weight/Config._currentWindow;
                _spaceCost += o.size();
            }
        }
    }
    public int mostRecentTime(){
        if(!_currentWindow.isEmpty()){
            return _currentWindow.getLast()._time;
        }
        if(!_pastWindow.isEmpty()){
            return _pastWindow.getLast()._time;
        }
        System.err.println("Empty TwoWindowList!!");
        return -1;
    }
    public int size(){
        return _currentWindow.size() + _pastWindow.size();
    }
    public LinkedList<SpatialObject> getListOfSpatialObject(){
        LinkedList<SpatialObject> list = new LinkedList<>();
        list.addAll(_pastWindow);
        list.addAll(_currentWindow);
        return list;
    }
    public LinkedList<Interval> getIntervals(double a, double b, double bottom, double top) {
        LinkedList<Interval> list = new LinkedList<>();
        for (SpatialObject o : _pastWindow) {
            Interval itUp, itDown;
            if(o._y > bottom){
                itUp = new Interval(o._x, o._x+b, o._y, o._weight/Config._pastWindow, ObjectType.Old, EdgeType.Up);
            }
            else{
                itUp = new Interval(o._x, o._x + b, bottom, o._weight/Config._pastWindow, ObjectType.Old, EdgeType.Up);
            }
            if(o._y + a < top){
                itDown = new Interval(o._x, o._x+b, o._y + a, o._weight/Config._pastWindow, ObjectType.Old, EdgeType.Down);
            }
            else{
                itDown = new Interval(o._x, o._x+b, top, o._weight/Config._pastWindow, ObjectType.Old, EdgeType.Down);
            }
            list.add(itUp);
            list.add(itDown);
        }
        for(SpatialObject o: _currentWindow){
            Interval itUp, itDown;
            if(o._y > bottom){
                itUp = new Interval(o._x, o._x+b, o._y, o._weight/Config._currentWindow, ObjectType.New, EdgeType.Up);
            }
            else{
                itUp = new Interval(o._x, o._x + b, bottom, o._weight/Config._currentWindow, ObjectType.New, EdgeType.Up);
            }
            if(o._y + a < top){
                itDown = new Interval(o._x, o._x+b, o._y + a, o._weight/Config._currentWindow, ObjectType.New, EdgeType.Down);
            }
            else{
                itDown = new Interval(o._x, o._x+b, top, o._weight/Config._currentWindow, ObjectType.New, EdgeType.Down);
            }
            list.add(itUp);
            list.add(itDown);
        }
        Collections.sort(list);
        return list;
    }
    public double[] getXCoords(double b, double l, double r){
        int idx = 0;
        Iterator<SpatialObject> x = _pastWindow.listIterator(0);
        LinkedList<Double> candidates = new LinkedList<>();
        while(x.hasNext()){
            SpatialObject o = x.next();
            if(o._x > l){
                candidates.addLast(o._x);
            }
            if(o._x + b < r){
                candidates.addLast(o._x + b);
            }
        }
        x = _currentWindow.listIterator(0);
        while(x.hasNext()){
            SpatialObject o = x.next();
            if(o._x > l){
                candidates.addLast(o._x);
            }
            if(o._x + b < r){
                candidates.addLast(o._x + b);
            }
        }
        candidates.addLast(l);
        candidates.addLast(r);
        double[] xcoords = new double[candidates.size()];
        ListIterator<Double> it = candidates.listIterator();
        idx = 0;
        while(it.hasNext()){
            xcoords[idx++] = it.next();
        }
        Arrays.sort(xcoords);
        return xcoords;
    }
    public double spaceCost(){
        return _spaceCost;
    }
    //test
    public void print(){
        System.out.print("past: ");
        Iterator<SpatialObject> x = _pastWindow.listIterator(0);
        while(x.hasNext()){
            SpatialObject o = x.next();
            System.out.print(String.format("(%d, %f, [%f, %f]) ", o._id, o._weight, o._x, o._y));
        }
        System.out.println();
        System.out.print("current: ");
        x = _currentWindow.listIterator(0);
        while(x.hasNext()){
            SpatialObject o = x.next();
            System.out.print(String.format("(%d, %f, [%f, %f]) ", o._id, o._weight, o._x, o._y));
        }
        System.out.println();
    }
    public static void main(String[] args){
        TwoWindowLists tl = new TwoWindowLists();
        tl.add(new SpatialObject(1, 1, 1.0, 1.0, 1.0));
        tl.print();
        tl.add(new SpatialObject(2, 2, 2.0, 2.0, 2.0));
        tl.print();
        tl.print();
        double[] xcoords = tl.getXCoords(0.5, 1, 1);
        Config c = new Config();
        c.setWindow(1, 1);
        System.out.println(Config._currentWindow+" "+Config._pastWindow);
        /*
        for(int i=0; i < xcoords.length; i++){
            System.out.print(xcoords[i]+" ");
        }
        System.out.println();
        LinkedList<Interval> list = tl.getIntervals(0.5, 0.5);
        for(Interval i:list){
            System.out.print(i.y+" ");
            if(i.et == EdgeType.Down){
                System.out.println("down");
            }
            else{
                System.out.println("up");
            }
        }
        */
    }
}
