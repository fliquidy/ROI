package ROI;
import java.util.Collections;
import java.util.Vector;
import java.lang.Math;
/**
 * Created by kaiyu on 4/28/2016.
 */
public class IntervalTree {
    public ITreeNode[] tree;
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
                tree[nodeIdx].
            }
        }
    }
    public void fin(int nodeIdx, double l, double r){
        int lchildIdx = nodeIdx * 2;
        int rchildIdx = nodeIdx * 2 + 1;
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

        }


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
