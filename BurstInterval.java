package ROI;

/**
 * Created by kaiyu on 5/11/2016.
 */
public class BurstInterval {
    public double[] dicriminant;
    public double[] currentWeight;
    public double[] pastWeight;
    public double maxScore;
    public double maxX;
    public double maxY;
    public BurstInterval(double[] coordinates){
        int num = coordinates.length - 1;
        dicriminant = new double[num];
        currentWeight = new double[num];
        pastWeight = new double[num];
        for(int idx = 0; idx < num; idx++){
            dicriminant[idx] = (coordinates[idx] + coordinates[idx+1])/2.0;
            currentWeight[idx] = 0;
            pastWeight[idx] = 0;
        }
        maxScore = 0;
    }
    public void insertInterval(double l, double r, double value, ObjectType t, double y){
        int begin = 0, end = dicriminant.length;
        int lIdx = (begin + end)/2;
        int rIdx = (begin + end)/2;
        while(end - begin > 1){
            if(dicriminant[lIdx] < l){
                begin = lIdx ;
            }
            else{
                end = lIdx;
            }
            lIdx = (begin + end) / 2;
        }
        lIdx = end;
        begin = 0;
        end = dicriminant.length;
        while(end - begin > 1){
            if(dicriminant[rIdx] < r){
                begin = rIdx;
            }
            else{
                end = rIdx;
            }
            rIdx = (begin + end)/2;
        }
        rIdx = begin;
        System.out.println(lIdx+" "+rIdx);
        for(int idx = lIdx; idx <= rIdx; idx++){
            if(t == ObjectType.New){
                currentWeight[idx] += value;
                pastWeight[idx] += value;
            }
            else if(t == ObjectType.Old){
                pastWeight[idx] -= value;
            }

            double score = Math.abs(pastWeight[idx]) + currentWeight[idx];
            if(score > maxScore){
                maxX = dicriminant[idx];
                maxY = y;
                maxScore = score;
            }
        }

    }
    public static void main(String args[]){
        double[] coords = new double[10];
        for(int idx = 0; idx < 10; idx ++){
            coords[idx] = idx;
        }
        BurstInterval bi = new BurstInterval(coords);
        bi.insertInterval(3, 7, 1, ObjectType.New, 8);
        bi.insertInterval(1, 5, 1, ObjectType.New, 7);
        bi.insertInterval(2, 6, 1, ObjectType.Old, 6);
        bi.insertInterval(3, 7, -1, ObjectType.New, 5);
        bi.insertInterval(4, 8, 1, ObjectType.New, 4);
        bi.insertInterval(1, 5, -1, ObjectType.New, 3);
        bi.insertInterval(2, 6, -1, ObjectType.Old, 2);
        bi.insertInterval(4, 8, -1, ObjectType.New, 1);
        System.out.println(bi.maxScore+" "+bi.maxX+" "+bi.maxY);
    }


}
