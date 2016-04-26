package ROI;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;
import java.util.NavigableSet;
import org.mapdb.*;


public class StorageManager {
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

	
	//config
	Config _config;
	
	public StorageManager(Type t, Config config){
		_config = new Config(config);
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
	public void updateResult(double x, double y){
		System.out.println("Result updated: center "+x+"," + y);
	}
	public void update(SpatialObject o){
		/*
		 * When a new spatial object arrives, it affects cell c1-c4. 
		 * We write it to cache, then we consider the following cases:
		 * 1. c is in memory: update UB, find exact answer if necessary
		 * 2. c is in disk: update UBRest, update UBs in rest cells if necessary
		 */
	}
	
	
	
	public void maintainMemIndex(Cell c, SpatialObject o){
		//process the case when the affected cell is in memory
		exactIndex.get(c).add(o);
		memIndexSize += o.size();
		double old_ub = ubInMem.get(c);
		if(old_ub + o._weight > smax){
			//this cell may be a result, find the exact max score in this cell.
			ExactSolver es = new ExactSolver();
			double ub = es.find();
			ubInMem.put(c, ub);
			if(ub > smax){
				updateResult(es.x, es.y);
			}
		}
		else{
			//this cell cannot be a result
			ubInMem.put(c, old_ub + o._weight);
		}
		db.commit();
	}
	public void maintainDiskIndex(Cell c, SpatialObject o){
		//process the case when the affected cell is in disk

	}
	public void writeToCache(SpatialObject o, Type t){
		/*
		 *We maintain recent objects in memory to reduce IO cost.
		 * Only the objects that are not in memory are stored in cache
		 */
		cache.addElement(o);
		Cell c = o.locateCell(a, b);
		double ub = updateUBInDisk(c, o);
		if(cache.size() > _config.cacheSize || ub > smax){
			//write cache to disk
			flush();
		}
		//cacheSize +=
	}
	
	public void writeToDisk(Cell c, Vector<SpatialObject> oVec){
		//write a cell-object relation to disk
		//NavigableSet<Fun.Tuple2<Cell, SpatialObject>> multiMap =
		//		db.createTreeSet("DCell_object").serializer(BTreeKeySerializer.TUPLE2).makeOrGet();
		//multiMap.add(Fun.t2(c, o));
		BTreeMap<Cell, Vector<SpatialObject>> coMap = db.createTreeMap("DCell_object").makeOrGet();
		Vector<SpatialObject> newList = new Vector<SpatialObject>(coMap.get(c));
		newList.addAll(oVec);
		coMap.put(c, newList);
		commit();
	}
	public void writeDetails(SpatialObject o){
		BTreeMap<Integer, SpatialObject> map = db.createTreeMap("details").makeOrGet();
		map.put(o._id, o);
	}
	public void writeToIndex(SpatialObject o, Type t){
		if(t == Type.Exact){
			BTreeMap<Cell, Vector<Integer>> index = db.createTreeMap("ExactIndex").makeOrGet();
			Cell c = o.locateCell(a, b);
			Cell cu = c.up();
			Cell cr = c.right();
			Cell cur = c.upright();
			if(cellsInMem.contains(c)){
				maintainMemIndex(c, o);
			}
			else{
				maintainDiskIndex(c, o);
			}
			if(cellsInMem.contains(cu)){
				maintainMemIndex(cu, o);
			}
			else{
				maintainDiskIndex(cu, o);
			}
			if(cellsInMem.contains(cr)){
				maintainMemIndex(cr, o);
			}
			else{
				maintainDiskIndex(cr, o);
			}
			if(cellsInMem.contains(cur)){
				maintainMemIndex(cur, o);
			}
			else{
				maintainDiskIndex(cur, o);
			}
		}
	}

	public double updateUBInDisk(Cell c, SpatialObject o){
		//update the loose upper bound for the cells in disk
		if(upperBoundInRestCells.size() < _config.ubInRestCellsCount){
			upperBoundInRestCells.addElement(new CellUB(c, o._weight));
		}
		else{
			for(CellUB cub : upperBoundInRestCells){
				if(cub._cell.equals(c)){
					cub._UB += o._weight;
					Collections.sort(upperBoundInRestCells);
					return upperBoundInRestCells.lastElement()._UB;
				}
			}
			upperBoundInRestCells.elementAt(0)._cell.clone(c);
			upperBoundInRestCells.elementAt(0)._UB += o._weight;
			
		}
		Collections.sort(upperBoundInRestCells);
		return upperBoundInRestCells.lastElement()._UB;
		
	}
	
	public void flush(){
		/*
		 * Write the spatial objects in cache into disk.
		 */
		HashMap<Cell, Vector<SpatialObject>> cacheMap = new HashMap<Cell, Vector<SpatialObject>>();
		HashMap<Cell, Double> cacheUBMap = new HashMap<Cell, Double>();
		for(SpatialObject sp : cache){
			Cell temC = sp.locateCell(a, b);
			if(cacheMap.containsKey(temC)){
				cacheMap.put(temC, new Vector<SpatialObject>());
				cacheUBMap.put(temC, 0.0);
			}
			cacheMap.get(temC).add(sp);
			double oldUB = cacheUBMap.get(temC);
			cacheUBMap.put(temC, oldUB + sp._weight);
		}
		BTreeMap<Cell, Double> cellUBMap = db.createTreeMap("cell_ub").makeOrGet();
		for(Cell ic:cacheMap.keySet()){
			writeToDisk(ic, cacheMap.get(ic));
			double oldUB = cellUBMap.get(ic);
			double newUB = oldUB + cacheUBMap.get(ic);
			cellUBMap.put(ic, newUB);
			if(newUB > upperBoundInRestCellsBackup.firstElement()._UB){
				upperBoundInRestCellsBackup.firstElement()._UB = newUB;
				upperBoundInRestCellsBackup.firstElement()._cell = ic;
				Collections.sort(upperBoundInRestCellsBackup);
			}
		}
		cacheSize = 0;
	}
	public void checkCellStatus(Cell c){
		//check whether a cell should be in disk or memory
		if(cellsInMem.contains(c)){

		}
		else{

		}
	}
	public void balance(){

	}
	/*public void swap(){
		if(ubInMemVec.firstElement()._UB < ub){
			if(MemIndexSize() + upp){

			}
		}
	}*/
	public void loadCellIntoMem(Cell c){
		//load the contents in a cell into memory
		//only cells with top highest upper bound should be maintained in memory
		//we only load cells into memory when there are extra memory
		BTreeMap<Cell, Vector<SpatialObject>> coMap = db.createTreeMap("DCell_object").makeOrGet();
		Vector<SpatialObject> SPVec = new Vector<SpatialObject>(coMap.get(c));
		exactIndex.put(c, SPVec);
		coMap.remove(c);
	}
	public void writeCellToDisk(Cell c){
		//write the contents in a cell into disk
		//when the cells in memory reach the size constraint, we write cells with 
		//lowest upper bounds into disk.
		BTreeMap<Cell, Vector<SpatialObject>> coMap = db.createTreeMap("DCell_object").makeOrGet();
		coMap.put(c, exactIndex.get(c));
		exactIndex.remove(c);
		
	}
	public double cacheSize(){
		//To be done.
		return cacheSize;
	}
	public double MemIndexSize(){
		//To be done.
		return memIndexSize;
	}
	public void commit(){
		db.commit();
	}
}
