package ROI;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * Created by kaiyu on 5/4/2016.
 */
public class UpperboundManager {
    ArrayList<UpperBound> myheap;
    HashMap<Cell, Integer> mymap;
    public UpperboundManager(int capacity){
        myheap = new ArrayList<>(capacity);
        mymap = new HashMap<>(capacity);
        UpperBound ub = new UpperBound();
        myheap.add(ub);
    }
    public UpperboundManager(){
        myheap = new ArrayList<>();
        mymap = new HashMap<>();
        UpperBound ub = new UpperBound();
        myheap.add(ub);
    }
    public double getUB(Cell c){
        return myheap.get(mymap.get(c)).upperbound;
    }
    public void updateUB(Cell c, double value, double time, ObjectType t){
        int idx = 0;
        if(mymap.containsKey(c)){
            idx = mymap.get(c);
        }
        else{
           idx = myheap.size();
            UpperBound ub = new UpperBound(value, time, c);
            myheap.add(ub);
            mymap.put(c, idx);
        }
        if(t == ObjectType.Old){
            myheap.get(idx).updatePastUB(value);
        }
        else if(t == ObjectType.New){
            myheap.get(idx).updateCurrentUB(value);
        }
        else{
            System.err.println("Wrong type");
        }
        updateHeap(idx);
    }
    public void updateCellUB(Cell oldC, Cell newC, double value, double time){
        int idx = mymap.get(oldC);
        if(time > 0){
            myheap.get(idx).update(newC, value, time);
        }
        else{
            myheap.get(idx).update(newC, value);
        }
        updateHeap(idx);
    }
    public void updateMax(Cell c, double value, double time){
        mymap.remove(myheap.get(1).c);
        mymap.put(c, 1);
        if(time > 0){
            myheap.get(1).update(value);
        }
        else{
            myheap.get(1).update(value, time);
        }
        updateHeap(1);
    }
    public void update(Cell c, double value, double time){
        int idx = 0;
        if(mymap.containsKey(c)){
            idx = mymap.get(c);
        }
        else{
           idx = myheap.size();
            UpperBound ub = new UpperBound(value, time, c);
            myheap.add(ub);
            mymap.put(c, idx);
        }
        if(time > 0){
            myheap.get(idx).update(value);
        }
        else {
            myheap.get(idx).update(value, time);
        }
        updateHeap(idx);
    }

    public double getMax(){
        if(myheap.isEmpty()){
            return -1.0;
        }
        return myheap.get(1).upperbound;
    }
    public UpperBound getMaxUB(){
        if(myheap.size()<2){
            return null;
        }
        return myheap.get(1).copy();
    }

    private void updateHeap(int index){
        int father = index/2;
        int lchild = index * 2;
        int rchild = index * 2 + 1;
        System.out.println("processing "+index+", "+myheap.get(index).upperbound);
        if(father > 0 && myheap.get(father).upperbound < myheap.get(index).upperbound){
            //move upwards
            while(father > 0 && myheap.get(father).upperbound < myheap.get(index).upperbound){
                UpperBound tmp = myheap.get(index);
                myheap.set(index, myheap.get(father));
                myheap.set(father, tmp);
                mymap.put(myheap.get(index).c, index);
                mymap.put(myheap.get(father).c, father);
                father = father / 2;
                index = index / 2;
            }
        }
        else if((lchild < myheap.size() && myheap.get(lchild).upperbound > myheap.get(index).upperbound)
                ||(rchild < myheap.size() && myheap.get(rchild).upperbound > myheap.get(index).upperbound)
                ){
                //move downwards
            while(lchild < myheap.size()){
                int swapIdx = 0;
                if(rchild < myheap.size()){
                   swapIdx = myheap.get(lchild).upperbound > myheap.get(rchild).upperbound ? lchild : rchild;
                }
                else{
                    swapIdx = lchild;
                }
                if(myheap.get(swapIdx).upperbound <= myheap.get(index).upperbound){
                    break;
                }
                UpperBound tmp = myheap.get(swapIdx);
                myheap.set(swapIdx, myheap.get(index));
                myheap.set(index, tmp);
                mymap.put(myheap.get(index).c, index);
                mymap.put(myheap.get(swapIdx).c, swapIdx);
                index = index * 2;
                lchild = index * 2;
                rchild = index * 2 + 1;
            }
        }
    }
    public void print(){
        for(int i=1; i < myheap.size(); i++){
            System.out.print("["+myheap.get(i).c.toString()+", "+myheap.get(i).upperbound+"] ");
        }
        System.out.println();
    }
    public static void main(String args[]){
        UpperboundManager um = new UpperboundManager(5);
        Cell[] c = new Cell[5];
        for(int i=0; i < 5; i++){
            c[i] = new Cell(i, i);
            um.update(c[i], i, i);
        }
        um.print();
        um.update(c[1], 5, 5);
        um.print();
        System.out.println("#################");
        um.update(c[3], 2, 2);
        um.print();
        System.out.println("#################");
        um.update(c[0], 4, 4);
        um.print();
        System.out.println("#################");
        um.update(c[0], 0, 0);
        um.print();
        System.out.println("#################");
        um.update(c[0], 6, 6);
        um.print();
    }

}
