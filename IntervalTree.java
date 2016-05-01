package ROI;
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
        hasLWindow = false;
        hasRWindow = false;
    }
    public void insert_interval(double l, double r, double degree, int nodeIdx){
        if(l == r)return;
        while(nodeIdx < tree.length){
            if(tree[nodeIdx].discriminant < l){
                nodeIdx = nodeIdx * 2 + 1;
            }
            else if(tree[nodeIdx].discriminant > r){
                nodeIdx = nodeIdx * 2;
            }
            else{
                tree[nodeIdx].window_x = l;
                tree[nodeIdx].window_y = r;
                tree[nodeIdx].degree = 0;
                tree[nodeIdx].attachedWindow = true;
            }
        }
    }
    public void fin(int nodeIdx, double l, double r, double degree){
        int lchildIdx = nodeIdx * 2;
        int rchildIdx = nodeIdx * 2 + 1;
        //propogate
        if(lchildIdx < tree.length){
            tree[lchildIdx].excess += tree[nodeIdx].excess;
            if(tree[lchildIdx].attachedWindow){
                tree[lchildIdx].degree += tree[nodeIdx].excess;
            }
            if(tree[lchildIdx].target != null){
                tree[lchildIdx].maxdegree += tree[nodeIdx].excess;
            }
        }
        if(rchildIdx < tree.length){
            tree[rchildIdx].excess += tree[nodeIdx].excess;
            if(tree[rchildIdx].attachedWindow){
                tree[rchildIdx].degree += tree[nodeIdx].excess;
            }
            if(tree[rchildIdx].target != null){
                tree[rchildIdx].maxdegree += tree[nodeIdx].excess;
            }
        }
        tree[nodeIdx].excess = 0;
        if(tree[nodeIdx].attachedWindow){
            //window attached. Check whether the attached window should be split.
            if(tree[nodeIdx].window_x <= l && tree[nodeIdx].window_y >= r){
                //new interval is contained in the window
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
                hasLWindow = true;
                hasRWindow = true;
            }
            else if(l > tree[nodeIdx].window_x && l < tree[nodeIdx].window_y && r > tree[nodeIdx].window_y){
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
                hasLWindow = true;
            }
            else if(r > tree[nodeIdx].window_x && r < tree[nodeIdx].window_y && l < tree[nodeIdx].window_x){
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
                hasRWindow = true;
            }
            else{
                System.err.print("Attached window doesn't intersect with the interval.");
            }
        }
        else{
            //no window attached, check whether should LWindow and RWindow should be inserted here.
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

    public void fr(int nodeIdx, double l, double r, double degree){

    }
    public static void main(String args[]){
        double[] tvec = new double[5];
        tvec[0] = 1.0;
        tvec[1] = 2.0;
        tvec[2] = 3.0;
        tvec[3] = 4.0;
        tvec[4] = 5.0;
        IntervalTree it = new IntervalTree();
        it.build(tvec);

    }
}
