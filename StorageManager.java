package ROI;
import java.io.File;
import java.util.*;

import org.mapdb.*;


public class StorageManager {

	public static double validWindow;
	public static int currentTime;

	public static String dbName;
	public static String cellListName = "cell_list";

	public MemIndex memIdx;
	public DiskIndex diskIdx;



	/*
	public boolean swap(MemIndex mIdx, DiskIndex dIdx){
		if(mIdx.minUB() > dIdx.maxUB()){
			return false;
		}
		UpperBound dub = dIdx.getMax();
		UpperBound mub = mIdx.getMin();
		mub.upperbound = 0 - mub.upperbound;
		mIdx._ubm.updateCellUB(mub.c, dub.c, dub.upperbound, dub.updatetime);
		mIdx.setUB(mub.c, dub.c, dub.upperbound, dub.updatetime);
		dIdx._ubm.updateCellUB(dub.c, mub.c, 0 - mub.upperbound, mub.updatetime);
		LinkedList<SpatialObject> dl = dIdx.retrieve(dub.c);
		mIdx.write(dub.c, dl);
		LinkedList<SpatialObject> ml = mIdx.retrieve(mub.c);
		dIdx.write(mub.c, ml);
		mIdx.remove(mub.c);
		return true;
	}
	*/
	public void updateResult(){

	}



	static DB db = DBMaker.newFileDB(new File("roi")).closeOnJvmShutdown().make();
	double a;
	double b;
	
	public SpatialObject omax;
	public double smax;

	private double memIndexSize;
	private double cacheSize;
	
	//memory
	public HashSet<Cell> cellsInMem;
	public HashMap<Cell, Vector<SpatialObject>> exactIndex;
	public HashMap<Cell, Double> ubInCache;
	public HashMap<Cell, Double> ubInMem;
	public Vector<CellUB> ubInMemVec;
	public Vector<SpatialObject> cache;
	public Vector<CellUB> upperBoundInRestCells;
	public Vector<CellUB> upperBoundInRestCellsBackup;

	public UpperboundManager MemUM;

	public UpperboundManager DiskUM;
	
	//config

	public StorageManager(Type t, Config config){
		smax = 0;
		
		db.createTreeMap("detail").makeOrGet();//store details of spatial objects
		
		if(t == Type.Exact){
			cellsInMem = new HashSet<Cell>();
			exactIndex = new HashMap<Cell, Vector<SpatialObject>>();
			ubInCache = new HashMap<Cell, Double>();
			ubInMem = new HashMap<Cell, Double>();
			cache = new Vector<SpatialObject>();
			ubInMemVec = new Vector<CellUB>();
			upperBoundInRestCells = new Vector<CellUB>();
			upperBoundInRestCellsBackup = new Vector<CellUB>();

		}
		else if(t == Type.GB){
			
		}
		else if(t == Type.OB){
			
		}
	}
	public void writeDetails(SpatialObject o){
		BTreeMap<Integer, SpatialObject> map = db.createTreeMap("details").makeOrGet();
		map.put(o._id, o);
	}




//**********

	public void balance(){
		//balance cells between memory and disk
		Cell diskC = null;
		TwoWindowLists dtl = null;
		while(memIdx.getMinUBValue() < diskIdx.getMaxUBValue() + Config._swapThreshold ||
				memIdx._maxPosition._weight < diskIdx.getMaxUBValue() ) {
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
		if(memIdx.containCell(c)){
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
		Cell c = o.locateCell(a, b);
		Cell cu = c.up();
		Cell cr = c.right();
		Cell cur = c.upright();
		processCellObj(c, o, t);
		processCellObj(cu, o, t);
		processCellObj(cr, o, t);
		processCellObj(cur, o, t);
		while(memIdx.needToSearch() || diskIdx.getMaxUB().upperBound() > memIdx._maxPosition._weight){
			memIdx.search(Config._a, Config._b);
			balance();
		}
		if(memIdx._updatedResult){
			System.out.println("Best region changed:");
			System.out.println("Top-right corner: ("+memIdx._maxPosition._x+", "+memIdx._maxPosition._y+")");
		}
	}

}
