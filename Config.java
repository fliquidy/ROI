package ROI;

public class Config {
	public int cacheSize;
	public int cellsInMemoryConstraint;
	public int ubInRestCellsCount;
	public Config(){
		
	}
	public Config(Config c){
		this.cacheSize = c.cacheSize;
		this.cellsInMemoryConstraint = c.cellsInMemoryConstraint;
		this.ubInRestCellsCount = c.ubInRestCellsCount;
	}
}
