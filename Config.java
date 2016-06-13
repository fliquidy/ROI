package ROI;

public class Config {
	public static int _currentWindow;
	public static int _pastWindow;

	public static double _a;
	public static double _b;

	public static double _cacheConstraint;
	public static double _memoryConstraint;


	public static double _swapThreshold;

	public Config(){
		
	}
	public static void setWindow(int cWindow, int pWindow){
		_currentWindow = cWindow;
		_pastWindow = pWindow;
	}

	public static void setRecSize(double a, double b){
		_a = a;
		_b = b;
	}
	public static void setConstraint(double cacheSize, double memorySize, double swapThreshold){
		_cacheConstraint = cacheSize;
		_memoryConstraint = memorySize;
		_swapThreshold = swapThreshold;
	}
}
