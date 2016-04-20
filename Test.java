package ROI;
import java.util.*;
public class Test {
	public static void main(String args[]){
		Config config = new Config();
		config.ubInRestCellsCount = 3;
		StorageManager sm = new StorageManager(Type.Exact, config);
		SpatialObject o1 = new SpatialObject();
		o1._weight = 1.0;
		double[] v = new double[8];
		v[0] = sm.updateUBInDisk(new Cell(1, 1), o1);
		for(CellUB c : sm.upperBoundInRestCells){
			System.out.println(c._UB+" "+c._cell._x+" "+c._cell._y);
		}
		v[1] = sm.updateUBInDisk(new Cell(1,2), o1);
		for(CellUB c : sm.upperBoundInRestCells){
			System.out.println(c._UB+" "+c._cell._x+" "+c._cell._y);
		}
		v[2] = sm.updateUBInDisk(new Cell(1,3), o1);
		for(CellUB c : sm.upperBoundInRestCells){
			System.out.println(c._UB+" "+c._cell._x+" "+c._cell._y);
		}
		v[3] = sm.updateUBInDisk(new Cell(1,1), o1);
		for(CellUB c : sm.upperBoundInRestCells){
			System.out.println(c._UB+" "+c._cell._x+" "+c._cell._y);
		}
		v[4] = sm.updateUBInDisk(new Cell(1,1), o1);
		for(CellUB c : sm.upperBoundInRestCells){
			System.out.println(c._UB+" "+c._cell._x+" "+c._cell._y);
		}
		v[5] = sm.updateUBInDisk(new Cell(1,2), o1);
		for(CellUB c : sm.upperBoundInRestCells){
			System.out.println(c._UB+" "+c._cell._x+" "+c._cell._y);
		}
		v[6] = sm.updateUBInDisk(new Cell(1,4), o1);
		for(CellUB c : sm.upperBoundInRestCells){
			System.out.println(c._UB+" "+c._cell._x+" "+c._cell._y);
		}
		v[7] = sm.updateUBInDisk(new Cell(1, 2), o1);
		for(double d : v){
			System.out.println(d);
		}
		for(CellUB c : sm.upperBoundInRestCells){
			System.out.println(c._UB+" "+c._cell._x+" "+c._cell._y);
		}
		
	}
}
