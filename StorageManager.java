package ROI;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import org.mapdb.*;


public class StorageManager {
	static DB db = DBMaker.newFileDB(new File("roi")).closeOnJvmShutdown().make();
	double a;
	double b;
	
	//memory
	public HashSet<Cell> cellsInMem;
	public HashMap<Cell, Vector<SpatialObject>> exactIndex;
	public HashMap<Cell, Double> ubInCache;
	public Vector<SpatialObject> cache;
	double upperBoundInRestCells;
	
	public StorageManager(Type t){
		db.createTreeMap("detail").makeOrGet();//store details of spatial objects
		if(t == Type.Exact){
			cellsInMem = new HashSet<Cell>();
			exactIndex = new HashMap<Cell, Vector<SpatialObject>>();
			ubInCache = new HashMap<Cell, Double>();
			cache = new Vector<SpatialObject>();
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
	public void writeToIndex(SpatialObject o, Type t){
		if(t == Type.Exact){
			BTreeMap<Cell, Vector<Integer>> index = db.createTreeMap("ExactIndex").makeOrGet();
			Cell c = o.locateCell(a, b);
			if(cellsInMem.contains(c)){
				//maintain in memory
				//1. Update upper bound, maintain memory index 
				//2. If new upper bound > current optimal result, find exact result
				if(o._y + this.b){
					
				}
			}
			else{
				//maintain in disk
			}
		}
	}
	
	public void writeToCache(SpatialObject o, Type t){
		/*
		 *We maintain recent objects in memory to reduce IO cost.
		 * 
		 */
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
