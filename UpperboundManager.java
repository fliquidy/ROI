package ROI;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * Created by kaiyu on 5/4/2016.
 */
public class UpperboundManager {
    ArrayList<UpperBound> myheap;
    HashMap<Cell, Integer> mymap;
    boolean isMaxHeap;
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
        return myheap.get(mymap.get(c))._bound.upperBound();
    }
    public void updateUBforCell(Cell c, SpatialObject o, ObjectType ot){
        int idx = 0;
        if(mymap.containsKey(c)){
            idx = mymap.get(c);
        }
        else{
           idx = myheap.size();
            UpperBound ub = new UpperBound();
            ub.setCell(c);
            myheap.add(ub);
            mymap.put(c, idx);
        }
        myheap.get(idx)._bound.update(o, ot);
        if(isMaxHeap){
            updateMaxHeap(idx);
        }
        else{
            updateMinHeap(idx);
        }
    }
    public void updateCell(Cell c, UpperBound ub){
        if(!mymap.containsKey(c)){
            System.err.println("Cell ("+c._x+", "+c._y+") is not in UpperBoundManager");
            System.exit(0);
        }
        int idx = mymap.get(c);
        myheap.get(idx).copy(ub);
        mymap.put(ub._c, idx);
        mymap.remove(c);
        if(isMaxHeap){
            updateMaxHeap(idx);
        }
        else{
            updateMinHeap(idx);
        }
    }

    public double getTopValue(){
        if(myheap.isEmpty()){
            return -1.0;
        }
        return myheap.get(1).upperBound();
    }
    public UpperBound getTopUB(){
        if(myheap.size()<2){
            return null;
        }
        return myheap.get(1);
    }
    private void updateMinHeap(int index){
        int father = index/2;
        int lchild = index * 2;
        int rchild = index * 2 + 1;
        //System.out.println("processing "+index+", "+myheap.get(index).upperBound());
        if(father > 0 && myheap.get(father).upperBound() > myheap.get(index).upperBound()){
            //move upwards
            while(father > 0 && myheap.get(father).upperBound() > myheap.get(index).upperBound()){
                UpperBound tmp = myheap.get(index);
                myheap.set(index, myheap.get(father));
                myheap.set(father, tmp);
                mymap.put(myheap.get(index)._c, index);
                mymap.put(myheap.get(father)._c, father);
                father = father / 2;
                index = index / 2;
            }
        }
        else if((lchild < myheap.size() && myheap.get(lchild).upperBound() < myheap.get(index).upperBound())
                ||(rchild < myheap.size() && myheap.get(rchild).upperBound() < myheap.get(index).upperBound())
                ){
            //move downwards
            while(lchild < myheap.size()){
                int swapIdx = 0;
                if(rchild < myheap.size()){
                    swapIdx = myheap.get(lchild).upperBound() < myheap.get(rchild).upperBound() ? lchild : rchild;
                }
                else{
                    swapIdx = lchild;
                }
                if(myheap.get(swapIdx).upperBound() >= myheap.get(index).upperBound()){
                    break;
                }
                UpperBound tmp = myheap.get(swapIdx);
                myheap.set(swapIdx, myheap.get(index));
                myheap.set(index, tmp);
                mymap.put(myheap.get(index)._c, index);
                mymap.put(myheap.get(swapIdx)._c, swapIdx);
                index = index * 2;
                lchild = index * 2;
                rchild = index * 2 + 1;
            }
        }
    }
    private void updateMaxHeap(int index){
        int father = index/2;
        int lchild = index * 2;
        int rchild = index * 2 + 1;
        //System.out.println("processing "+index+", "+myheap.get(index).upperBound());
        if(father > 0 && myheap.get(father).upperBound() < myheap.get(index).upperBound()){
            //move upwards
            while(father > 0 && myheap.get(father).upperBound() < myheap.get(index).upperBound()){
                UpperBound tmp = myheap.get(index);
                myheap.set(index, myheap.get(father));
                myheap.set(father, tmp);
                mymap.put(myheap.get(index)._c, index);
                mymap.put(myheap.get(father)._c, father);
                father = father / 2;
                index = index / 2;
            }
        }
        else if((lchild < myheap.size() && myheap.get(lchild).upperBound() > myheap.get(index).upperBound())
                ||(rchild < myheap.size() && myheap.get(rchild).upperBound() > myheap.get(index).upperBound())
                ){
                //move downwards
            while(lchild < myheap.size()){
                int swapIdx = 0;
                if(rchild < myheap.size()){
                   swapIdx = myheap.get(lchild).upperBound() > myheap.get(rchild).upperBound() ? lchild : rchild;
                }
                else{
                    swapIdx = lchild;
                }
                if(myheap.get(swapIdx).upperBound() <= myheap.get(index).upperBound()){
                    break;
                }
                UpperBound tmp = myheap.get(swapIdx);
                myheap.set(swapIdx, myheap.get(index));
                myheap.set(index, tmp);
                mymap.put(myheap.get(index)._c, index);
                mymap.put(myheap.get(swapIdx)._c, swapIdx);
                index = index * 2;
                lchild = index * 2;
                rchild = index * 2 + 1;
            }
        }
    }

    public void print(){
        for(int i=1; i < myheap.size(); i++){
            System.out.print("["+myheap.get(i)._c.toString()+", "+myheap.get(i)._bound.toString()+"] ");
        }
        System.out.println();
    }
    public static void main(String args[]){

    }


}
