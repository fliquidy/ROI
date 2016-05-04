package ROI;
import java.util.HashMap;
import java.util.ArrayList;
/**
 * Created by kaiyu on 5/4/2016.
 */
public class MyBinaryHeap {
    ArrayList<UpperBound> myheap;
    HashMap<Cell, Integer> mymap;
    public void update(Cell c, double value, double timestamp){


    }
    public static void main(String args[]){
        HashMap<Cell, CellUB> mymap = new HashMap<>();
        Cell c1 = new Cell(0, 0);
        Cell c2 = new Cell(1, 1);
        Cell c3 = new Cell(2, 2);
        CellUB cub1 = new CellUB(c1, 1.0);
        CellUB cub2 = new CellUB(c2, 2.0);
        CellUB cub3 = new CellUB(c3, 3.0);
        mymap.put(c1, cub1);
        mymap.put(c2, cub2);
        mymap.put(c3, cub3);
        ArrayList<CellUB> myheap = new ArrayList<>();
        myheap.add(mymap.get(c1));
        myheap.add(mymap.get(c2));
        myheap.add(mymap.get(c3));
        CellUB tmpCU = myheap.get(1);
        myheap.set(1, myheap.get(0));
        myheap.set(0, tmpCU);
        for(int i=0; i < 3; i++){
            System.out.println(myheap.get(i)._UB);
        }
        System.out.println(mymap.get(c1)._UB);
        System.out.println(mymap.get(c2)._UB);
        System.out.println(mymap.get(c3)._UB);


    }
}
