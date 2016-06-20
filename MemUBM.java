package ROI;

import java.util.Iterator;
import java.util.ListIterator;

/**
 * Created by Kaiyu on 6/8/2016.
 */
public class MemUBM {
    public UpperboundManager maxUBM;
    public UpperboundManager minUBM;
    public MemUBM(int capicity){
        maxUBM = new UpperboundManager(capicity);
        maxUBM.isMaxHeap = true;
        minUBM = new UpperboundManager(capicity);
        minUBM.isMaxHeap = false;
    }
    public MemUBM(){
        maxUBM = new UpperboundManager();
        maxUBM.isMaxHeap = true;
        minUBM = new UpperboundManager();
        minUBM.isMaxHeap = false;
    }
    public void addCell(Cell c, UpperBound ub){
        UpperBound ubMax = new UpperBound();
        UpperBound ubMin = new UpperBound();
        ubMax.copy(ub);
        ubMin.copy(ub);
        maxUBM.addCell(c, ubMax);
        minUBM.addCell(c, ubMin);
    }
    public boolean check(){
        boolean flag = true;
        if(maxUBM.myheap.size() != minUBM.myheap.size()){
            flag = false;
            System.err.println("myheap size not match.");
        }
        if(maxUBM.mymap.size() != minUBM.mymap.size()){
            flag = false;
            System.err.println("mymap size not match.");
        }
        if(maxUBM.myheap.size() - 1 != maxUBM.mymap.size()){
            flag = false;
            System.err.println("myheap mymap not match");
        }
        ListIterator<UpperBound> it = maxUBM.myheap.listIterator();
        it.next();
        while(it.hasNext()){
            UpperBound ub = it.next();
            if(!maxUBM.mymap.containsKey(ub._c)){
                System.err.println("maxUBM "+ub._c);
                flag = false;
            }
        }
        it = minUBM.myheap.listIterator();
        it.next();
        while(it.hasNext()){
            UpperBound ub = it.next();
            if(!minUBM.mymap.containsKey(ub._c)){
                System.err.println("minUBM " + ub._c);
                flag = false;
            }
        }
        for(Cell c:maxUBM.mymap.keySet()){
            int idx = maxUBM.mymap.get(c);
            if(!c.equals(maxUBM.myheap.get(idx)._c)){
                System.out.println("maxUBM mapping not correct "+c.toString());
                flag = false;
            }
        }
        for(Cell c:minUBM.mymap.keySet()){
            int idx = minUBM.mymap.get(c);
            if(!c.equals(minUBM.myheap.get(idx)._c)){
                System.out.println("minUBM mapping not correct "+c.toString());
                flag = false;
            }
        }
        return flag;
    }
    public void updateUBforCell(Cell c, SpatialObject o, ObjectType ot){
        maxUBM.updateUBforCell(c, o, ot);
        minUBM.updateUBforCell(c, o, ot);
    }
    public void remove(Cell c){
        maxUBM.remove(c);
        minUBM.remove(c);
    }
    public void setExactBound(Cell c, double bound){
        maxUBM.setExactBound(c, bound);
        minUBM.setExactBound(c, bound);
    }
    public void setPoint(Cell c, Point p){
        maxUBM.getUB(c).setPoint(p._x, p._y);
        minUBM.getUB(c).setPoint(p._x, p._y);
    }
    public int size(){
        //if(maxUBM.size() != minUBM.size()){
        //    System.err.println("maxUBM and minUBM don't match.");
        //}
        return maxUBM.size();
    }
    public double getUBValue(Cell c){
        return maxUBM.getUB(c).upperBound();
    }
    public UpperBound getMaxUB(){
        return maxUBM.getTopUB();
    }
    public UpperBound getMinUB(){
        return minUBM.getTopUB();
    }
    public UpperBound getUB(Cell c){
        return maxUBM.getUB(c);
    }
    public static void main(String[] args){
        Config._currentWindow = 10;
        Config._pastWindow = 10;
        MemUBM mubm = new MemUBM();
        Cell c1 = new Cell(0, 0);
        Cell c2 = new Cell(1, 1);
        Cell c3 = new Cell(2, 2);

        SpatialObject o1 = new SpatialObject(1, 1, 1.0, 1.0, 1.0);
        SpatialObject o2 = new SpatialObject(2, 2, 2.0, 2.0, 2.0);
        SpatialObject o3 = new SpatialObject(3, 3, 2.0, 2.0, 3.0);
        mubm.updateUBforCell(c1, o1, ObjectType.New);
        mubm.maxUBM.print();
        mubm.minUBM.print();
        mubm.updateUBforCell(c1, o2, ObjectType.New);
        mubm.maxUBM.print();
        mubm.minUBM.print();
        mubm.updateUBforCell(c2, o1, ObjectType.New);
        mubm.maxUBM.print();
        mubm.minUBM.print();
        mubm.updateUBforCell(c2, o2, ObjectType.New);
        mubm.maxUBM.print();
        mubm.minUBM.print();
        mubm.updateUBforCell(c2, o2, ObjectType.Old);
        mubm.maxUBM.print();
        mubm.minUBM.print();
        mubm.updateUBforCell(c3, o1, ObjectType.New);
        mubm.maxUBM.print();
        mubm.minUBM.print();
        mubm.updateUBforCell(c1, o3, ObjectType.New);
        mubm.maxUBM.print();
        mubm.minUBM.print();
        System.out.println(mubm.getMaxUB()._c.toString());
        System.out.println(mubm.getMinUB()._c.toString());


    }
}

