package ROI;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
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
    public void remove(){
        _pastSum -= _pastWindow.getFirst()._weight/Config._pastWindow;
        _spaceCost -= _pastWindow.getFirst().size();
        _pastWindow.removeFirst();
    }
    public void transform(){
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
            case Old: transform();
                break;
            case Expired: remove();
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
        return _currentWindow.getLast()._time;
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
    public LinkedList<Interval> getIntervals(double a, double b) {
        LinkedList<Interval> list = new LinkedList<>();
        for (SpatialObject o : _pastWindow) {
            Interval itUp = new Interval(o._x, o._x+b, o._y, o._weight/Config._pastWindow, ObjectType.Old, EdgeType.Up);
            Interval itDown = new Interval(o._x, o._x+b, o._y + a, o._weight/Config._pastWindow, ObjectType.Old, EdgeType.Down);
            list.add(itUp);
            list.add(itDown);
        }
        for(SpatialObject o: _currentWindow){
            Interval itUp = new Interval(o._x, o._x+b, o._y, o._weight/Config._currentWindow, ObjectType.New, EdgeType.Up);
            Interval itDown = new Interval(o._x, o._x+b, o._y + a, o._weight/Config._currentWindow, ObjectType.New, EdgeType.Down);
            list.add(itUp);
            list.add(itDown);
        }
        Collections.sort(list);
        return list;
    }
    public double[] getXCoords(double b){
        double[] xcoords = new double[size()*2];
        int idx = 0;
        Iterator<SpatialObject> x = _pastWindow.listIterator(0);
        while(x.hasNext()){
            SpatialObject o = x.next();
            xcoords[idx++] = o._x;
            xcoords[idx++] = o._x + b;
        }
        x = _currentWindow.listIterator(0);
        while(x.hasNext()){
            SpatialObject o = x.next();
            xcoords[idx++] = o._x;
            xcoords[idx++] = o._x + b;
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
        tl.transform();
        tl.print();
        double[] xcoords = tl.getXCoords(0.5);
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
