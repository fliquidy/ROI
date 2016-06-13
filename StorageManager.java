package ROI;
import java.io.File;
import java.util.*;

import org.mapdb.*;


public class StorageManager {

	public static double validWindow;
	public static int currentTime;

	public static String dbName="base";
	public static String cellListName = "cell_list";

	public MemIndex memIdx;
	public DiskIndex diskIdx;



	public SpatialObject omax;
	public double smax;





	public StorageManager(Type t){
		switch (t){
			case Exact:
				memIdx = new MemIndex();
				diskIdx = new DiskIndex();
				break;
			case GB:
				break;
			case OB:
				break;
		}
	}





//**********

	public void balance(){
		//balance cells between memory and disk
		Cell diskC = null;
		TwoWindowLists dtl = null;
		while((!diskIdx.isEmpty()) && (memIdx.getMinUBValue() < diskIdx.getMaxUBValue() + Config._swapThreshold ||
				memIdx._maxPosition._weight < diskIdx.getMaxUBValue()) ) {
			if (diskC == null) {
				diskC = new Cell(diskIdx.getMaxUBCell());
			}
			if (dtl == null) {
				dtl = new TwoWindowLists();
				dtl.load(diskIdx.getList(diskC), StorageManager.currentTime);
			}
			if (memIdx.size() + dtl.size() > Config._memoryConstraint) {
				Cell memC = new Cell(memIdx.getMinUBCell());
				TwoWindowLists mtl = memIdx._exactIndex.get(memC);
				diskIdx.loadIntoDisk(memC, memIdx.getMinUB(), mtl);
				memIdx.removeFromMemory(memC);
			} else {
				memIdx.loadIntoMemory(diskC, diskIdx.getMaxUB(), dtl);
				diskIdx.remove(diskC);
				diskC = null;
				dtl = null;
			}

		}
	}
	public void processCellObj(Cell c, SpatialObject o, ObjectType ot){
		if(memIdx.containCell(c) || memIdx.size < Config._memoryConstraint){
			boolean isFull = memIdx.insertIntoIndex(o, c, ot);
			if(isFull){
				memIdx.moveMinCellToDisk(diskIdx);
			}
		}
		else{
			diskIdx.insertIntoIndex(o, c, ot);
		}
	}

	public void processSpatialObject(SpatialObject o, ObjectType t){
		StorageManager.currentTime = o._time;
		Cell c = o.locateCell(Config._a, Config._b);
		Cell cu = c.up();
		Cell cr = c.right();
		Cell cur = c.upright();
		processCellObj(c, o, t);
		processCellObj(cu, o, t);
		processCellObj(cr, o, t);
		processCellObj(cur, o, t);
		while(memIdx.needToSearch() ||
				((!diskIdx.isEmpty()) && diskIdx.getMaxUB().upperBound() > memIdx._maxPosition._weight)){
			memIdx.search(Config._a, Config._b);
			balance();
		}
		if(memIdx._updatedResult){
			System.out.println("Best region changed:");
			System.out.println("Top-right corner: ("+memIdx._maxPosition._x+", "+memIdx._maxPosition._y+")");
			System.out.println("Score: " + memIdx._maxPosition._weight);
		}
	}

}
