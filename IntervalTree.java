package ROI;
import javax.naming.ldap.Rdn;
import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;
import java.lang.Math;
/**
 * Created by kaiyu on 4/28/2016.
 */
public class IntervalTree {
    public ITreeNode[] tree;
    public double Ll;
    public double Lr;
    public double LDegree;
    public boolean hasLWindow;
    public boolean hasRWindow;
    public double Rl;
    public double Rr;
    public double RDegree;
    public double maxScore;
    public double maxXPos;
    public double maxYPos;

    public void build(double[] leaves){
        //build the interval tree.
        int height = (int) Math.floor(Math.log((double)leaves.length)/Math.log(2.0))+1;
        int length = (int)Math.pow(2, height + 1);
        tree = new ITreeNode[length];
        int idx = (int)Math.pow(2, height);
        for(int lIdx = 0; lIdx < leaves.length; ++lIdx){
            tree[idx + lIdx] = new ITreeNode(leaves[lIdx]);
        }
        double rightBound = leaves[leaves.length-1];
        for(int lIdx = leaves.length; lIdx < (int)Math.pow(2, height); ++lIdx){
            rightBound += 1;
            tree[idx + lIdx] = new ITreeNode(rightBound);
        }
        for(int tIdx = 1; tIdx < idx; ++tIdx){
            int level = (int)(Math.floor(Math.log(tIdx)/Math.log(2.0)));
            int rIdx = (tIdx * 2 + 1) * (int)(Math.pow(2, height - level - 1));
            tree[tIdx] = new ITreeNode(0.5 * (tree[rIdx].discriminant + tree[rIdx-1].discriminant));
        }
        tree[0] = new ITreeNode(0);
        hasLWindow = false;
        hasRWindow = false;
        tree[1].attachedWindow = true;
        tree[1].window_x = leaves[0];
        tree[1].window_y = rightBound;
        tree[1].degree = 0;
    }
    public double maxScore(){
    	return tree[1].maxdegree;
    }
    public SpatialObject getMaxPoint(){
    	SpatialObject so = new SpatialObject();
    	so._weight = tree[1].maxdegree;
    	int targetIdx = tree[1].targetIdx;
    	so._x = maxXPos; 
    	so._y = maxYPos;
    	return so;
    }
    public void forward(boolean isTop, double l, double r, double weight, double y){
        int vnIdx = 1;
        while(tree[vnIdx].discriminant > r || tree[vnIdx].discriminant < l){
          //  System.out.println("vnIdx="+vnIdx);
            fin(vnIdx, l, r, weight, isTop);
            if(tree[vnIdx].discriminant > r){
                vnIdx = vnIdx * 2;
            }
            else{
                vnIdx = vnIdx * 2 + 1;
            }
        }
//        System.out.println("vnIdx = "+vnIdx);
        fin(vnIdx, l, r, weight, isTop);
        int lvnIdx = vnIdx * 2;
        int rvnIdx = vnIdx * 2 + 1;
        while(lvnIdx < tree.length && lvnIdx > 0){
//            System.out.println("lvnIdx = "+lvnIdx);
            lvnIdx = fl(lvnIdx, l, r, weight, isTop);
        }
        while(rvnIdx < tree.length && rvnIdx > 0){
//            System.out.println("rvnIdx = "+rvnIdx);
            rvnIdx = fr(rvnIdx, l, r, weight, isTop);
        }
        lvnIdx = 0 - lvnIdx;
        rvnIdx = 0 - rvnIdx;
//        System.out.println("backward:"+vnIdx + " "+lvnIdx + " "+rvnIdx);
        backward(vnIdx, lvnIdx, rvnIdx, y);
//        System.out.println("processed");
        for(int idx = 1; idx < tree.length; idx++){
            System.out.print(idx+" ("+tree[idx].window_x+","+tree[idx].window_y+"): "+tree[idx].degree+" "+tree[idx].excess+" "+tree[idx].targetIdx+" "+tree[idx].maxdegree+" ## ");
        }
        System.out.println();

    }
    public void backward(int vnIdx, int lcIdx, int rcIdx, double y){
        while(lcIdx != vnIdx){
            //System.out.println(lcIdx+" "+vnIdx);
            tree[lcIdx].maxdegree = 0;
            tree[lcIdx].targetIdx = 0;
            if(tree[lcIdx].attachedWindow && tree[lcIdx].degree > tree[lcIdx].maxdegree){
                tree[lcIdx].maxdegree = tree[lcIdx].degree;
                tree[lcIdx].targetIdx = lcIdx;
            }
            int lc = 2 * lcIdx;
            int rc = 2 * lcIdx + 1;
            if(lc < tree.length && tree[lc].targetIdx != 0 && tree[lc].maxdegree > tree[lcIdx].maxdegree){
                tree[lcIdx].maxdegree = tree[lc].maxdegree;
                tree[lcIdx].targetIdx = tree[lc].targetIdx;
            }
            if(rc < tree.length && tree[rc].targetIdx != 0 && tree[rc].maxdegree > tree[lcIdx].maxdegree){
                tree[lcIdx].maxdegree = tree[rc].maxdegree;
                tree[lcIdx].targetIdx = tree[rc].targetIdx;
            }
            //System.out.println("backward: nodeIdx: "+lcIdx+", maxD: "+tree[lcIdx].maxdegree+", target: "+tree[lcIdx].targetIdx);
            lcIdx = lcIdx / 2;
        }
        while(rcIdx != vnIdx) {
            tree[rcIdx].maxdegree = 0;
            tree[rcIdx].targetIdx = 0;
            if (tree[rcIdx].attachedWindow && tree[rcIdx].degree > tree[rcIdx].maxdegree) {
                tree[rcIdx].maxdegree = tree[rcIdx].degree;
                tree[rcIdx].targetIdx = rcIdx;
            }
            int lc = 2 * rcIdx;
            int rc = 2 * rcIdx + 1;
            if (lc < tree.length && tree[lc].targetIdx != 0 && tree[lc].maxdegree > tree[rcIdx].maxdegree) {
                tree[rcIdx].maxdegree = tree[lc].maxdegree;
                tree[rcIdx].targetIdx = tree[lc].targetIdx;
            }
            if (rc < tree.length && tree[rc].targetIdx != 0 && tree[rc].maxdegree > tree[rcIdx].maxdegree) {
                tree[rcIdx].maxdegree = tree[rc].maxdegree;
                tree[rcIdx].targetIdx = tree[rc].targetIdx;
            }
            rcIdx = rcIdx / 2;
            //System.out.println("backward: nodeIdx: "+rcIdx+", maxD: "+tree[rcIdx].maxdegree+", target: "+tree[rcIdx].targetIdx);
        }
        while(vnIdx > 0){
            tree[vnIdx].maxdegree = 0;
            tree[vnIdx].targetIdx = 0;
            if(tree[vnIdx].attachedWindow && tree[vnIdx].degree > tree[vnIdx].maxdegree){
                tree[vnIdx].maxdegree = tree[vnIdx].degree;
                tree[vnIdx].targetIdx = vnIdx;
            }
            int lc = 2 * vnIdx;
            int rc = 2 * vnIdx + 1;
            if(lc < tree.length && tree[lc].targetIdx != 0 && tree[lc].maxdegree > tree[vnIdx].maxdegree){
                tree[vnIdx].maxdegree = tree[lc].maxdegree;
                tree[vnIdx].targetIdx = tree[lc].targetIdx;
            }
            if(rc < tree.length && tree[rc].targetIdx != 0 && tree[rc].maxdegree > tree[vnIdx].maxdegree ){
                tree[vnIdx].maxdegree = tree[rc].maxdegree;
                tree[vnIdx].targetIdx = tree[rc].targetIdx;
            }

            //System.out.println("backward: nodeIdx: "+vnIdx+", maxD: "+tree[vnIdx].maxdegree+", target: "+tree[vnIdx].targetIdx);
            vnIdx = vnIdx / 2;
        }
        if(tree[1].maxdegree > maxScore){
        	maxScore = tree[1].maxdegree;
        	int targetIdx = tree[1].targetIdx;
        	maxXPos = (tree[targetIdx].window_x + tree[targetIdx].window_y) / 2;
        	maxYPos = y;
        }

    }
    public void fin(int nodeIdx, double l, double r, double degree, boolean isTop){
        int lchildIdx = nodeIdx * 2;
        int rchildIdx = nodeIdx * 2 + 1;
        //propogate
        if(lchildIdx < tree.length){
            tree[lchildIdx].excess += tree[nodeIdx].excess;
            if(tree[lchildIdx].attachedWindow){
                tree[lchildIdx].degree += tree[nodeIdx].excess;
            }
            if(tree[lchildIdx].targetIdx > 0){
                tree[lchildIdx].maxdegree += tree[nodeIdx].excess;
            }
        }
        if(rchildIdx < tree.length){
            tree[rchildIdx].excess += tree[nodeIdx].excess;
            if(tree[rchildIdx].attachedWindow){
                tree[rchildIdx].degree += tree[nodeIdx].excess;
            }
            if(tree[rchildIdx].targetIdx > 0){
                tree[rchildIdx].maxdegree += tree[nodeIdx].excess;
            }
        }
        tree[nodeIdx].excess = 0;
 //       System.out.println("propogated");
        if(tree[nodeIdx].attachedWindow){
            //window attached. Check whether the attached window should be split.
            //System.out.println("nodeIDx: "+nodeIdx+" [x,y]: "+tree[nodeIdx].window_x+","+tree[nodeIdx].window_y+" [l,r]: "+l+","+r);
            if(tree[nodeIdx].window_x <= l && tree[nodeIdx].window_y >= r){
                //new interval is contained in the window
//                System.out.println("branch 1");
                if(tree[nodeIdx].discriminant < l){
                    Ll = l;
                    Lr = r;
                    LDegree = tree[nodeIdx].degree + degree;
                    Rl = r;
                    Rr = tree[nodeIdx].window_y;
                    RDegree = tree[nodeIdx].degree;
                    tree[nodeIdx].window_y = l;
                }
                else if(tree[nodeIdx].discriminant > r){
                    Ll = tree[nodeIdx].window_x;
                    Lr = l;
                    LDegree = tree[nodeIdx].degree;
                    Rl = l;
                    Rr = r;
                    RDegree = tree[nodeIdx].degree + degree;
                    tree[nodeIdx].window_x = r;
                }
                else{
                    Ll = tree[nodeIdx].window_x;
                    Lr = l;
                    LDegree = tree[nodeIdx].degree;
                    Rl = r;
                    Rr = tree[nodeIdx].window_y;
                    RDegree = tree[nodeIdx].degree;
                    tree[nodeIdx].degree += degree;
                    tree[nodeIdx].window_x = l;
                    tree[nodeIdx].window_y = r;
                }
                if(Ll != Lr) {
                    hasLWindow = true;
                }
                if(Rl != Rr) {
                    hasRWindow = true;
                }

            }
            else if(l > tree[nodeIdx].window_x && l < tree[nodeIdx].window_y && r > tree[nodeIdx].window_y){
//                System.out.println("branch 2");
                if(tree[nodeIdx].discriminant < l){
                    Ll = l;
                    Lr = tree[nodeIdx].window_y;
                    LDegree = tree[nodeIdx].degree + degree;
                    tree[nodeIdx].window_y = l;
                }
                else{
                    Ll = tree[nodeIdx].window_x;
                    Lr = l;
                    LDegree = tree[nodeIdx].degree;
                    tree[nodeIdx].window_x = l;
                    tree[nodeIdx].degree += degree;
                }
                if(Ll != Lr) {
                    hasLWindow = true;
                }
            }
            else if(r > tree[nodeIdx].window_x && r < tree[nodeIdx].window_y && l < tree[nodeIdx].window_x){
//                System.out.println("branch 3");
                if(tree[nodeIdx].discriminant < r){
                    Rl = r;
                    Rr = tree[nodeIdx].window_y;
                    RDegree = tree[nodeIdx].degree;
                    tree[nodeIdx].window_y = r;
                    tree[nodeIdx].degree += degree;
                }
                else{
                    Rl = tree[nodeIdx].window_x;
                    Rr = r;
                    RDegree = tree[nodeIdx].degree + degree;
                    tree[nodeIdx].window_x = r;
                }
                if(Rl != Rr) {
                    hasRWindow = true;
                }
            }
            else if(l <= tree[nodeIdx].window_x && r >= tree[nodeIdx].window_y){
                if(isTop){
                    tree[nodeIdx].degree += degree;
                }
                else{
                    tree[nodeIdx].degree -= degree;
                }
            }
            else{
                System.err.println("Attached window doesn't intersect with the interval.");
            }
        }
        else{
            //no window attached, check whether should LWindow and RWindow should be inserted here.
//            System.out.print("branch 4");
            if(hasLWindow && tree[nodeIdx].discriminant > Ll && tree[nodeIdx].discriminant < Lr){
                tree[nodeIdx].window_x = Ll;
                tree[nodeIdx].window_y = Lr;
                tree[nodeIdx].degree = LDegree;
                tree[nodeIdx].attachedWindow = true;
                hasLWindow = false;
            }
            if(hasRWindow && tree[nodeIdx].discriminant > Rl && tree[nodeIdx].discriminant < Rr){
                tree[nodeIdx].window_x = Rl;
                tree[nodeIdx].window_y = Rr;
                tree[nodeIdx].degree = RDegree;
                tree[nodeIdx].attachedWindow = true;
                hasRWindow = false;
            }
        }
    }
    public int fl(int nodeIdx, double l, double r, double degree, boolean isTop){
        int lchildIdx = nodeIdx * 2;
        int rchildIdx = nodeIdx * 2 + 1;
        //propogate
        if(lchildIdx < tree.length){
            tree[lchildIdx].excess += tree[nodeIdx].excess;
            if(tree[lchildIdx].attachedWindow){
                tree[lchildIdx].degree += tree[nodeIdx].excess;
            }
            if(tree[lchildIdx].targetIdx > 0){
                tree[lchildIdx].maxdegree += tree[nodeIdx].excess;
            }
        }
        if(rchildIdx < tree.length){
            tree[rchildIdx].excess += tree[nodeIdx].excess;
            if(tree[rchildIdx].attachedWindow){
                tree[rchildIdx].degree += tree[nodeIdx].excess;
            }
            if(tree[rchildIdx].targetIdx > 0){
                tree[rchildIdx].maxdegree += tree[nodeIdx].excess;
            }
        }
        tree[nodeIdx].excess = 0;
        if(tree[nodeIdx].attachedWindow){
            //System.out.println("FL: nodeIdx: "+nodeIdx+" [x,y]: "+tree[nodeIdx].window_x+", "+tree[nodeIdx].window_y+", [l,r]: "+l+", "+r);
            if(tree[nodeIdx].window_x >= l && tree[nodeIdx].window_y <= r){
                if(isTop){
                    tree[nodeIdx].degree += degree;
                }
                else{
                    tree[nodeIdx].degree -= degree;
                }
            }
            else if(l >= tree[nodeIdx].window_x && l <= tree[nodeIdx].window_y){
                if(r <= tree[nodeIdx].window_y){
                    System.err.print("Error: interval is contained by in the window");
                }
                if(tree[nodeIdx].discriminant < l){
                    if(l != tree[nodeIdx].window_y) {
                        Ll = l;
                        Lr = tree[nodeIdx].window_y;
                        LDegree = tree[nodeIdx].degree + degree;
                        hasLWindow = true;
                        tree[nodeIdx].window_y = l;
                    }
                }
                else{
                    if(tree[nodeIdx].window_x != l) {
                        Ll = tree[nodeIdx].window_x;
                        Lr = l;
                        LDegree = tree[nodeIdx].degree;
                        hasLWindow = true;
                        tree[nodeIdx].window_x = l;
                    }
                    tree[nodeIdx].degree += degree;
                }
            }
        }
        else{
            if(hasLWindow && tree[nodeIdx].discriminant > Ll && tree[nodeIdx].discriminant < Lr){
                tree[nodeIdx].window_x = Ll;
                tree[nodeIdx].window_y = Lr;
                tree[nodeIdx].degree = LDegree;
                tree[nodeIdx].attachedWindow = true;
                hasLWindow = false;
            }
            if(hasRWindow && tree[nodeIdx].discriminant > Rl && tree[nodeIdx].discriminant < Rr){
                tree[nodeIdx].window_x = Rl;
                tree[nodeIdx].window_y = Rr;
                tree[nodeIdx].degree = RDegree;
                tree[nodeIdx].attachedWindow = true;
                hasRWindow = false;
            }
        }
        if(tree[nodeIdx].discriminant > l ){
            if(isTop){
                if(rchildIdx < tree.length){
                    tree[rchildIdx].excess += degree;
                    tree[rchildIdx].degree += degree;
                    if(tree[rchildIdx].targetIdx > 0){
                        tree[rchildIdx].maxdegree += degree;
                    }
                }
            }
            else{
                if(rchildIdx < tree.length){
                    tree[rchildIdx].excess -= degree;
                    tree[rchildIdx].degree -= degree;
                    if(tree[rchildIdx].targetIdx > 0){
                        tree[rchildIdx].maxdegree -= degree;
                    }
                }
            }
            if(lchildIdx < tree.length){
                return lchildIdx;
            }
            else{
                return 0 - nodeIdx;
            }
        }
        else{
            if(rchildIdx < tree.length){
                return rchildIdx;
            }
            else{
                return 0 - nodeIdx;
            }
        }
    }
    public int fr(int nodeIdx, double l, double r, double degree, boolean isTop){
        int lchildIdx = nodeIdx * 2;
        int rchildIdx = nodeIdx * 2 + 1;
        //propogate
        if(lchildIdx < tree.length){
            tree[lchildIdx].excess += tree[nodeIdx].excess;
            if(tree[lchildIdx].attachedWindow){
                tree[lchildIdx].degree += tree[nodeIdx].excess;
            }
            if(tree[lchildIdx].targetIdx > 0){
                tree[lchildIdx].maxdegree += tree[nodeIdx].excess;
            }
        }
        if(rchildIdx < tree.length){
            tree[rchildIdx].excess += tree[nodeIdx].excess;
            if(tree[rchildIdx].attachedWindow){
                tree[rchildIdx].degree += tree[nodeIdx].excess;
            }
            if(tree[rchildIdx].targetIdx > 0){
                tree[rchildIdx].maxdegree += tree[nodeIdx].excess;
            }
        }
        tree[nodeIdx].excess = 0;
        if(tree[nodeIdx].attachedWindow){
//            System.out.println("FR: nodeIdx: "+nodeIdx+" [x,y]: "+tree[nodeIdx].window_x+", "+tree[nodeIdx].window_y+", [l,r]: "+l+", "+r);
            if(tree[nodeIdx].window_x >= l && tree[nodeIdx].window_y <= r){
                if(isTop){
                    tree[nodeIdx].degree += degree;
                }
                else{
                    tree[nodeIdx].degree -= degree;
                }
            }
            else if(r >= tree[nodeIdx].window_x && r <= tree[nodeIdx].window_y){
                if(l >= tree[nodeIdx].window_x){
                    System.err.print("Error: interval is contained by in the window");
                }
                if(tree[nodeIdx].discriminant > r){
                    if(r != tree[nodeIdx].window_x) {
                        Rl = tree[nodeIdx].window_x;
                        Rr = r;
                        RDegree = tree[nodeIdx].degree + degree;
                        hasRWindow = true;
                        tree[nodeIdx].window_x = r;
                    }
                }
                else{
                    if(r != tree[nodeIdx].window_y) {
                        Rl = r;
                        Rr = tree[nodeIdx].window_y;
                        RDegree = tree[nodeIdx].degree;
                        hasRWindow = true;
                        tree[nodeIdx].window_y = r;
                    }
                    tree[nodeIdx].degree += degree;
                }
            }
 //           System.out.println("FR: nodeIdx: "+nodeIdx+" [x,y]: "+tree[nodeIdx].window_x+", "+tree[nodeIdx].window_y+", [l,r]: "+l+", "+r);
        }
        else{
            if(hasLWindow && tree[nodeIdx].discriminant > Ll && tree[nodeIdx].discriminant < Lr){
                tree[nodeIdx].window_x = Ll;
                tree[nodeIdx].window_y = Lr;
                tree[nodeIdx].degree = LDegree;
                tree[nodeIdx].attachedWindow = true;
                hasLWindow = false;
            }
            if(hasRWindow && tree[nodeIdx].discriminant > Rl && tree[nodeIdx].discriminant < Rr){
                tree[nodeIdx].window_x = Rl;
                tree[nodeIdx].window_y = Rr;
                tree[nodeIdx].degree = RDegree;
                tree[nodeIdx].attachedWindow = true;
                hasRWindow = false;
            }
        }
        if(tree[nodeIdx].discriminant < r ){
            if(isTop){
                if(lchildIdx < tree.length){
                   tree[lchildIdx].excess += degree;
                    tree[lchildIdx].degree += degree;
                    if(tree[lchildIdx].targetIdx > 0){
                        tree[lchildIdx].maxdegree += degree;
                    }
                }
            }
            else{
                if(lchildIdx < tree.length){
                    tree[lchildIdx].excess -= degree;
                    tree[lchildIdx].degree -= degree;
                    if(tree[lchildIdx].targetIdx > 0){
                        tree[lchildIdx].maxdegree -= degree;
                    }
                }
            }
            if(rchildIdx < tree.length){
                return rchildIdx;
            }
            else{
                return 0 - nodeIdx;
            }
        }
        else{
            if(lchildIdx < tree.length){
                return lchildIdx;
            }
            else{
                return 0 - nodeIdx;
            }
        }
    }
    public void search(double[] xs, double[] ys, double[] weights, double a, double b){
    	double leaves[] = new double[xs.length * 2 + 2];
    	double minX = xs[0];
    	double maxX = xs[0];
    	for(int idx = 0; idx < xs.length; idx++){
    		leaves[idx * 2] = xs[idx] - b/2;
    		leaves[idx * 2 + 1] = xs[idx] + b/2;
    		if(leaves[idx* 2] < minX){
    			minX = leaves[idx*2];
    		}
    		if(leaves[idx*2+1] > maxX){
    			maxX = leaves[idx*2 + 1];
    		}
    	}
    	leaves[xs.length * 2] = minX;
    	leaves[xs.length * 2 + 1] = maxX;
    	Arrays.sort(leaves);
    	build(leaves);
        Interval[] intervals = new Interval[xs.length * 2];
        for(int i=0; i < xs.length; i++){
            intervals[2*i] = new Interval(xs[i]-b/2, xs[i] + b/2, ys[i] - a/2, weights[i], ObjectType.New, EdgeType.Up);
            intervals[2* i + 1] = new Interval(xs[i] - b/2, xs[i] + b/2, ys[i] + a/2, weights[i], ObjectType.New, EdgeType.Down);
        }
        Arrays.sort(intervals);
        for(int i=0; i < intervals.length; i++){
        	if(intervals[i].et == EdgeType.Up){
        		forward(true, intervals[i].l, intervals[i].r, intervals[i].value, intervals[i].y);
        	}
        	else{
        		forward(false,  intervals[i].l, intervals[i].r, intervals[i].value, intervals[i].y);
        	}
        	System.out.println(maxScore+" (" + maxXPos +", "+ maxYPos+")");
        }
    }
    public static void testTuah(){
        double[] xs = new double[10];
        double[] ys = new double[10];
        double[] ws = new double[10];
        for(int i=0; i<10; i++){
        	ws[i] = 1;
        }
        xs[0] = 0.7332764052899778;
        xs[1] = 0.4323687715914065;
        xs[2] = 0.1327378978953987;
        xs[3] = 0.8323628141241542;
        xs[4] = 0.8843138653539059;
        xs[5] = 0.4718945615276069;
        xs[6] = 0.4634896416403228;
        xs[7] = 0.7408227484127;
        xs[8] = 0.28553462333832036;
        xs[9] = 0.05608415151339008;

        ys[0] =  0.4256064451517516;
        ys[1] =  0.9740435584295989;
        ys[2] =  0.5197047406937022;
        ys[3] =  0.7271674417008329;
        ys[4] =  0.5587298778208042;
        ys[5] =  0.5508769615680242;
        ys[6] =  0.9011985556051308;
        ys[7] =  0.27999403189856154;
        ys[8] =  0.8587223635178663;
        ys[9] =  0.11515839130071759;

        
        IntervalTree it = new IntervalTree();
        it.search(xs, ys, ws, 0.5, 0.5);
    }
    public static void testInterval(){
        double[] leaves = new double[10];
        for(int i=0; i < 10; i++){
            leaves[i] = i;
        }
        IntervalTree it = new IntervalTree();
        it.build(leaves);
        System.out.println("Interval tree built.");
        for(int i=1; i < it.tree.length; i++){
            System.out.print("("+it.tree[i].discriminant+", "+it.tree[i].targetIdx+")\t");
        }
        System.out.println();
        double[] x = new double[8];
        double[] y = new double[8];
        boolean[] top = new boolean[8];
        x[0] = 2.0;
        y[0] = 6.0;
        top[0] = true;

        x[1] = 4.0;
        y[1] = 8.0;
        top[1] = true;

        x[2] = 1.0;
        y[2] = 5.0;
        top[2] = true;

        x[3] = 2.0;
        y[3] = 6.0;
        top[3] = false;

        x[4] = 3.0;
        y[4] = 7.0;
        top[4] = true;

        x[5] = 4.0;
        y[5] = 8.0;
        top[5] = false;

        x[6] = 1.0;
        y[6] = 5.0;
        top[6] = false;

        x[7] = 3.0;
        y[7] = 7.0;
        top[7] = false;

        for(int i=0; i < 8; i++){
            it.forward(top[i], x[i], y[i], 1.0, 1);
            System.out.println("processing "+x[i]+" "+y[i]+" "+top[i]);
            System.out.println("current best result: ["+it.tree[it.tree[1].targetIdx].window_x+", "+it.tree[it.tree[1].targetIdx].window_y+"] "+it.tree[1].maxdegree);
            for(int j=1; j < it.tree.length; j++){
                System.out.print(j+", ["+it.tree[j].window_x+","+it.tree[j].window_y+"], "+it.tree[j].degree+" , "+it.tree[j].excess+"||| ");
            }
            System.out.println();
        }
    }
    public static void main(String args[]){
        testTuah();
    }
}
