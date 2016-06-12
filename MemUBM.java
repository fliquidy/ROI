package ROI;

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
        maxUBM.addCell(c, ub);
        minUBM.addCell(c, ub);
    }
    public void updateUBforCell(Cell c, SpatialObject o, ObjectType ot){
        maxUBM.updateUBforCell(c, o, ot);
        minUBM.updateUBforCell(c, o, ot);
    }
    public void replaceCell(Cell c, UpperBound ub){
        maxUBM.replaceCell(c, ub);
        minUBM.replaceCell(c, ub);
    }
    public void remove(Cell c){
        maxUBM.remove(c);
        minUBM.remove(c);
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

        SpatialObject o1 = new SpatialObject(1, 1, 1, 1.0, 1.0, 1.0);
        SpatialObject o2 = new SpatialObject(2, 2, 2, 2.0, 2.0, 2.0);
        SpatialObject o3 = new SpatialObject(3, 3, 3, 2.0, 2.0, 3.0);
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

