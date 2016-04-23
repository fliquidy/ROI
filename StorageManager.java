package ROI;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import org.mapdb.*;


public class StorageManager {
	static DB db = DBMaker.newFileDB(new File("roi")).closeOnJvmShutdown().make();
	double a;
	double b;
	
	public SpatialObject omax;
	public double smax;
	
	//memory
	public HashSet<Cell> cellsInMem;
	public HashMap<Cell, Vector<SpatialObject>> exactIndex;
	public HashMap<Cell, Double> ubInCache;
	public HashMap<Cell, Double> ubInMem;
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
			for(SpatialObject sp : cache){
				
			}
		}
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
	}
	public void loadCellIntoMem(Cell c){
		//load the contents in a cell into memory
		//only cells with top highest upper bound should be maintained in memory
		//we only load cells into memory when there are extra memory
	}
	public void writeCellToDisk(Cell c){
		//write the contents in a cell into disk
		//when the cells in memory reach the size constraint, we write cells with 
		//lowest upper bounds into disk.
		
	}
	public void commit(){
		db.commit();
	}
}
