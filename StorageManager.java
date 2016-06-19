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
		System.out.println("In memory: "+memIdx._exactIndex.size()+" In disk: "+diskIdx._ubm.mymap.size());
		if(!memIdx._ubm.check()){
			System.out.println("not match");
		}
		while((!diskIdx.isEmpty()) && (memIdx.getMinUBValue() < diskIdx.getMaxUBValue() + Config._swapThreshold ||
				memIdx._maxPosition._weight < diskIdx.getMaxUBValue()) ) {
			if (diskC == null) {
				diskC = new Cell(diskIdx.getMaxUBCell());
			}
			if (dtl == null) {
				dtl = diskIdx.getTwoWindowLists(diskC);
				System.out.println("retrieving "+diskC.toString());
			}
			if (memIdx.size() + dtl.spaceCost() > Config._memoryConstraint) {
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
		if(c._x == 2 && c._y == -4){
			System.out.println();
		}
		if(diskIdx.containCell(c) || //cell in disk
				((!memIdx.containCell(c)) && memIdx.size > Config._memoryConstraint) // cell not in memory and memory is full
				)	{
			diskIdx.insertIntoIndex(o, c, ot);
		}
		else{
			//System.out.println("inserting into "+c.toString()+" type: "+ot.toString()+" object ID: "+o._id);
			boolean isFull = memIdx.insertIntoIndex(o, c, ot);
			//System.out.print("before: memory usage: "+memIdx.size);
			if(isFull){
				memIdx.moveMinCellToDisk(diskIdx);
			}
			if(memIdx._ubm.size() == 0){
				System.err.println("memory constraint is too small. Please allocate more memory.");
				System.exit(0);
			}

			//System.out.println(" after: memory usage: "+memIdx.size);
		}
	}

	public void processSpatialObject(SpatialObject o, ObjectType t){
		StorageManager.currentTime = o._time;
		memIdx._updatedResult = false;
		Cell c = o.locateCell(Config._a, Config._b);
		Cell cu = c.up();
		Cell cr = c.right();
		Cell cur = c.upright();
		System.out.println("Affected cells: "+c.toString()+" "+cu.toString()+" "+cr.toString()+" "+cur.toString());
		if(!memIdx._ubm.check()){
			System.out.println("error 1");
		}
		processCellObj(c, o, t);
		if(!memIdx._ubm.check()){
			System.out.println("error 1");
		}
		processCellObj(cu, o, t);
		if(!memIdx._ubm.check()){
			System.out.println("error 1");
		}
		processCellObj(cr, o, t);
		if(!memIdx._ubm.check()){
			System.out.println("error 1");
		}
		processCellObj(cur, o, t);
		if(!memIdx._ubm.check()){
			System.out.println("error 2");
		}
		while(memIdx.needToSearch() ||
				((!diskIdx.isEmpty()) && diskIdx.getMaxUB().upperBound() > memIdx._maxPosition._weight)){
			memIdx.search(Config._a, Config._b);
			balance();
		}
		if(!memIdx._ubm.check()){
			System.out.println("error 3");
		}
		if(memIdx._updatedResult){
			System.out.println("Best region changed. Cell: "+memIdx._maxPosition.locateCell(Config._a, Config._b).toString());
			System.out.println("Top-right corner: ("+memIdx._maxPosition._x+", "+memIdx._maxPosition._y+")");
			System.out.println("Score: " + memIdx._maxPosition._weight);
			memIdx.printCells();
			diskIdx.printCells();
			memIdx.listObjectsInCell(memIdx._maxPosition.locateCell(Config._a, Config._b));
			System.out.println("memory cost: "+memIdx.size);
		}
	}

}
