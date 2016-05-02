package ROI;

public class ITreeNode {
	public int targetIdx;
	public double maxdegree;
	public double discriminant;
	public double degree;
	public double excess;
	public double window_x;
	public double window_y;
	public boolean attachedWindow;
	public ITreeNode(double d){
		this.discriminant = d;
	}
}
