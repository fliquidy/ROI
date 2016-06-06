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
    public LinkedList<SpatialObject> currentWindow;
    public LinkedList<SpatialObject> pastWindow;
    public TwoWindowLists(){
        currentWindow = new LinkedList<>();
        pastWindow = new LinkedList<>();
    }
    public void add(SpatialObject o){
        currentWindow.addLast(o);
    }
    public void remove(){
        pastWindow.removeFirst();
    }
    public void transform(double value){
        SpatialObject o = currentWindow.getFirst();
        o._weight = value;
        pastWindow.addLast(o);
        currentWindow.removeFirst();
    }
    public int size(){
        return currentWindow.size() + pastWindow.size();
    }

    public LinkedList<Interval> getIntervals(double a, double b) {
        LinkedList<Interval> list = new LinkedList<>();
        for (SpatialObject o : pastWindow) {
            Interval itUp = new Interval(o._x, o._x+b, o._y, o._weight, ObjectType.Old, EdgeType.Up);
            Interval itDown = new Interval(o._x, o._x+b, o._y + a, o._weight, ObjectType.Old, EdgeType.Down);
            list.add(itUp);
            list.add(itDown);
        }
        for(SpatialObject o: currentWindow){
            Interval itUp = new Interval(o._x, o._x+b, o._y, o._weight, ObjectType.New, EdgeType.Up);
            Interval itDown = new Interval(o._x, o._x+b, o._y + a, o._weight, ObjectType.New, EdgeType.Down);
            list.add(itUp);
            list.add(itDown);
        }
        Collections.sort(list);
        return list;
    }
    public double[] getXCoords(double b){
        double[] xcoords = new double[size()*2];
        int idx = 0;
        Iterator<SpatialObject> x = pastWindow.listIterator(0);
        while(x.hasNext()){
            SpatialObject o = x.next();
            xcoords[idx++] = o._x;
            xcoords[idx++] = o._x + b;
        }
        x = currentWindow.listIterator(0);
        while(x.hasNext()){
            SpatialObject o = x.next();
            xcoords[idx++] = o._x;
            xcoords[idx++] = o._x + b;
        }
        Arrays.sort(xcoords);
        return xcoords;
    }
    //test
    public void print(){
        System.out.print("past: ");
        Iterator<SpatialObject> x = pastWindow.listIterator(0);
        while(x.hasNext()){
            SpatialObject o = x.next();
            System.out.print(String.format("(%d, %f, [%f, %f]) ", o._id, o._weight, o._x, o._y));
        }
        System.out.println();
        System.out.print("current: ");
        x = currentWindow.listIterator(0);
        while(x.hasNext()){
            SpatialObject o = x.next();
            System.out.print(String.format("(%d, %f, [%f, %f]) ", o._id, o._weight, o._x, o._y));
        }
        System.out.println();
    }
    public static void main(String[] args){
        TwoWindowLists tl = new TwoWindowLists();
        tl.add(new SpatialObject(1, 1, 1, 1.0, 1.0, 1.0));
        tl.print();
        tl.add(new SpatialObject(2, 2, 2, 2.0, 2.0, 2.0));
        tl.print();
        tl.transform(0.5);
        tl.print();
        double[] xcoords = tl.getXCoords(0.5);
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
    }
}
