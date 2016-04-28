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
    public void fin(int nodeIdx){

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
