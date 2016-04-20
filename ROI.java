package ROI;
import java.io.File;

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
		BTreeMap<String, Integer> map = db.createTreeMap("treemap").makeOrGet();
		map.put("ff", 2);
		db.commit();
		db.close();
		
		db = DBMaker.newFileDB(new File("test.db")).closeOnJvmShutdown().make();
		map = db.createTreeMap("treemap").makeOrGet();
		map.put("test", 1);
		System.out.println(map.get("ff"));
		
		//System.out.println(map.get("something"));
	}

}
