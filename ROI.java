package ROI;
import java.io.File;
import java.util.NavigableSet;
import java.util.HashMap;
import java.util.NavigableMap;
import java.util.Vector;
import org.mapdb.*;
public class ROI {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double x = 2.5;
		double b = 1.2;
		int f = (int)(x/b);
		System.out.println(f);
		System.out.println("test");
		DB db = DBMaker.newFileDB(new File("test.db")).closeOnJvmShutdown().make();

		NavigableSet<Fun.Tuple2<String, Long>> multiMap = db.getTreeSet("test");
		multiMap = db.createTreeSet("test2").serializer(BTreeKeySerializer.TUPLE2).make();
		multiMap.add(Fun.t2("aa", 1L));
		multiMap.add(Fun.t2("aa", 2L));
		//System.out.println(map.get("something"));
	}

}
