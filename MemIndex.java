package ROI;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;
import java.util.NavigableSet;
import org.mapdb.*;
/**
 * Created by kaiyu on 5/8/2016.
 */
public class MemIndex {
    public boolean updatedResult;
    public SpatialObject maxPosition;
    public UpperboundManager ubm;

    public void processObject(SpatialObject o, Cell c){
        //process cells which are affected by object and are maintained in memory.
    }

}
