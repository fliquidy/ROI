package ROI;

public class Config {
	public static double _currentWindow;
	public static double _pastWindow;

	public static double _a;
	public static double _b;

	public static double _cacheConstraint;


	public static double _swapThreshold;

	public Config(){
		
	}
	public void setWindow(double cWindow, double pWindow){
		_currentWindow = cWindow;
		_pastWindow = pWindow;
	}

	public void setRecSize(double a, double b){
		_a = a;
		_b = b;
	}
}
