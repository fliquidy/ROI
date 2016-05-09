package ROI;
import sun.awt.image.ImageWatched;

import java.util.LinkedList;
/**
 * Created by kaiyu on 5/9/2016.
 */
public class TwoWindowLists {
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
}
