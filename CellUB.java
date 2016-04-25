package ROI;

public class CellUB implements Comparable<CellUB>{
	public Cell _cell;
	double _UB;
	int _count;
	public CellUB(Cell c, double UB){
		this._cell = new Cell(c);
		this._UB = UB;
	}
	public CellUB(Cell c, double UB, int count){
		this._cell = new Cell(c);
		this._UB = UB;
		this._count = count;
	}
	@Override
	public int compareTo(CellUB c) {
		// TODO Auto-generated method stub
		return Double.compare(this._UB, c._UB);

	}
	
}
