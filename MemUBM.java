package ROI;

/**
 * Created by Kaiyu on 6/8/2016.
 */
public class MemUBM {
    public UpperboundManager maxUBM;
    public UpperboundManager minUBM;
    public MemUBM(int capicity){
        maxUBM = new UpperboundManager(capicity);
        minUBM = new UpperboundManager(capicity);
    }
    public MemUBM(){
        maxUBM = new UpperboundManager();
        minUBM = new UpperboundManager();
    }
    public void updateUBforCell(Cell c, SpatialObject o, ObjectType ot){
        maxUBM.updateUBforCell(c, o, ot);
        o._weight = 0 - o._weight;
        minUBM.updateUBforCell(c, o, ot);
    }
    public void updateCell(Cell c, UpperBound ub){
        maxUBM.updateCell(c, ub);

    }
}
