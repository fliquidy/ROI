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
		BTreeMap<Cell, LinkedList<SpatialObject>> treeMap = db.createTreeMap(cellListName).makeOrGet();
		while(memIdx.getMinUBValue() < diskIdx.getMaxUBValue() + Config._swapThreshold){
			Cell diskC = new Cell(diskIdx.getMaxUBCell());
			TwoWindowLists dtl = new TwoWindowLists();
			dtl.load(treeMap.get(diskC), StorageManager.currentTime);
			int diskCSize = dtl.size();
			while(diskCSize + memIdx.size() > Config._memoryConstraint ){
				if(memIdx._exactIndex.isEmpty()){
					System.err.println("Too large to load into memory. More memory is needed.");
					System.exit(0);
				}
				Cell memC = new Cell(memIdx.getMinUBCell());
				TwoWindowLists mtl = memIdx._exactIndex.get(memC);
				diskIdx.loadIntoDisk(memC, memIdx.getMinUB(), mtl, treeMap);
				memIdx.remove(memC);
			}
			memIdx.loadIntoMemory(diskC, diskIdx.getMaxUB(), dtl);
		}
		while(memIdx.getMinUBValue() < diskIdx.getMaxUBValue() + Config._swapThreshold){
			Cell memC = new Cell(memIdx.getMinUBCell());
			Cell diskC = new Cell(diskIdx.getMaxUBCell());
			TwoWindowLists mtl = memIdx._exactIndex.get(memC);
			TwoWindowLists dtl = new TwoWindowLists();
			LinkedList<SpatialObject> list = treeMap.get(diskC);
			dtl.load(list, StorageManager.currentTime);
			int memCSize = mtl.size();
			int diskCSize = dtl.size();

			diskIdx.loadIntoDisk(memC, memIdx.getMinUB(), mtl, treeMap);
			memIdx.loadIntoMemory(diskC, diskIdx.getMaxUB(), dtl);
		}

	}

	public void processSpatialObject(SpatialObject o, ObjectType t){
		StorageManager.currentTime = o._time;
		Cell c = o.locateCell(a, b);
		Cell cu = c.up();
		Cell cr = c.right();
		Cell cur = c.upright();
		if(cellsInMem.contains(c)){
			memIdx.insertIntoIndex(o, c, t);
		}
		else{
			diskIdx.insertIntoIndex(o, c, t);
		}
		if(cellsInMem.contains(cu)){
			memIdx.insertIntoIndex(o, cu, t);
		}
		else{
			diskIdx.insertIntoIndex(o, cu, t);
		}
		if(cellsInMem.contains(cr)){
			memIdx.insertIntoIndex(o, cr, t);
		}
		else{
			diskIdx.insertIntoIndex(o, cr, t);
		}
		if(cellsInMem.contains(cur)){
			memIdx.insertIntoIndex(o, cur, t);
		}
		else{
			diskIdx.insertIntoIndex(o, cur, t);
		}


	}

}
