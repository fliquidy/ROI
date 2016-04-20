package ROI;

public class CellUB implements Comparable<CellUB>{
	public Cell _cell;
	double _UB;
	public CellUB(Cell c, double UB){
		this._cell = new Cell(c);
		this._UB = UB;
	}
	@Override
	public int compareTo(CellUB c) {
		// TODO Auto-generated method stub
		return Double.compare(this._UB, c._UB);

	}
	
}
