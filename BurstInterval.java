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
    public void insertInterval(Interval interval, double nextY){
        int begin = 0, end = dicriminant.length-1;
        int lIdx = (begin + end)/2;
        int rIdx = (begin + end)/2;
        while(end - begin > 1){
            if(dicriminant[lIdx] < interval.l){
                begin = lIdx ;
            }
            else{
                end = lIdx;
            }
            lIdx = (begin + end) / 2;
        }
        lIdx = end;
        begin = 0;
        end = dicriminant.length - 1;
        while(end - begin > 1){
            if(dicriminant[rIdx] < interval.r){
                begin = rIdx;
            }
            else{
                end = rIdx;
            }
            rIdx = (begin + end)/2;
        }
        rIdx = begin;
        for(int idx = lIdx; idx <= rIdx; idx++){
            if(interval.ot == ObjectType.New){
                if(interval.et == EdgeType.Up){
                    currentWeight[idx] += interval.value;
                    pastWeight[idx] += interval.value;
                }
                else{
                    currentWeight[idx] -= interval.value;
                    pastWeight[idx] -= interval.value;
                }
            }
            else if(interval.ot == ObjectType.Old){
                if(interval.et == EdgeType.Up){
                    pastWeight[idx] -= interval.value;
                }
                else{
                    pastWeight[idx] += interval.value;
                }
            }
            double score = Math.abs(pastWeight[idx]) + currentWeight[idx];
            if(score > maxScore){
                maxX = dicriminant[idx];
                maxY = (interval.y+nextY)/2;
                maxScore = score;
            }
        }

    }



}
