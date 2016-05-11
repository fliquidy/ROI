package ROI;

/**
 * Created by kaiyu on 5/11/2016.
 */
public class Interval implements Comparable<Interval>{
    public double l;
    public double r;
    public double y;
    public double value;
    public ObjectType ot;
    public EdgeType et;
    public Interval(double lcoord, double rcoord, double ycoord, double weight, ObjectType objt, EdgeType edget){
        this.l = lcoord;
        this.r = rcoord;
        this.y = ycoord;
        
    }
    public int compareTo(Interval it){
        if(y < it.y)return 1;
        if(y > it.y) return -1;
        if(et == EdgeType.Down && it.et == EdgeType.Up) return 1;
        if(et == EdgeType.Up && it.et == EdgeType.Down) return -1;
        return 0;
    }

}
