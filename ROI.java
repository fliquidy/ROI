package ROI;
import java.io.File;
import java.util.*;

import org.mapdb.*;
public class ROI {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double x = 2.5;
		double b = 1.2;
		int f = (int)(x/b);
		System.out.println(f);
		System.out.println("test");
		DB db = DBMaker.newFileDB(new File("test.db")).closeOnJvmShutdown().cacheDisable().make();
		BTreeMap<Cell, LinkedList<SpatialObject>> mymap = db.createTreeMap("testmap2").makeOrGet();
		LinkedList<SpatialObject> list = new LinkedList<>();
		Cell c = new Cell(1, 1);
		for(int i=0; i < 1000; i++){
			list.addLast(new SpatialObject());
		}
		mymap.put(c, list);
		db.commit();

		long t1 = System.currentTimeMillis();
		BTreeMap<Cell, LinkedList<SpatialObject>> map2 = db.createTreeMap("testmap2").makeOrGet();
		LinkedList<SpatialObject> list2 = map2.get(c);
		for(int i=0; i < 1000; i++){
			list2.addLast(new SpatialObject());
		}
		map2.put(c, list2);
		db.commit();

		long t2 = System.currentTimeMillis();
		System.out.println(map2.get(c).size());
		System.out.println(t2 - t1);
	/*

		BTreeMap<Integer, Vector<Integer>> ff = db.createTreeMap("ff").makeOrGet();
		Vector<Integer> fvv = new Vector<>(100000, 1);
		long ffff = System.currentTimeMillis();
		ff.put(3, fvv);
		db.commit();
		long fffff = System.currentTimeMillis();
		System.out.print(fffff - ffff);
		NavigableSet<Fun.Tuple2<Integer, Integer>> multimap = db.createTreeSet("testmap")
				.serializer(BTreeKeySerializer.TUPLE2).makeOrGet();
		long t1 = System.currentTimeMillis();
		for(int i=0; i < 100000; i++){
			multimap.add(Fun.t2(1, i));
			multimap.add(Fun.t2(2, i));
		}
		long t2 = System.currentTimeMillis();
		System.out.println(t2-t1);
		db.commit();
		long t3 = System.currentTimeMillis();
		System.out.println(t3-t2);
		Vector<Integer> fv = new Vector<Integer>();
		for(int i:Fun.filter(multimap, 1)){
			fv.add(i);
		}
		long t4 = System.currentTimeMillis();
		System.out.println(t4-t3);
*/
	}

}
