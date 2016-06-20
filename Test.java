package ROI;
import java.io.*;
import java.util.*;
public class Test {

	public static void main(String args[]){
		try {
			InputStream is = new FileInputStream("toy.data");
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader bfr = new BufferedReader(isr);
			String line = null;
			StorageManager sm = new StorageManager(Type.Exact);
			Config.setWindow(50, 50);
			Config.setRecSize(20, 20);
			Config.setConstraint(5, 5, 0);
			int cnt = 0;
			long start = System.currentTimeMillis();
            while((line = bfr.readLine()) != null){
				String[] split = line.split("\\s+");
                System.out.println(cnt+": processing: "+line);
				if(cnt == 31){
					System.out.println();
				}
                cnt ++;
				int time = Integer.parseInt(split[0]);
				int id = Integer.parseInt(split[1]);
				double x = Double.parseDouble(split[2]);
				double y = Double.parseDouble(split[3]);
				int status = Integer.parseInt(split[4]);
				SpatialObject so = new SpatialObject(id, time, x, y, 1);
				ObjectType t = null;
				switch (status){
					case 0: t = ObjectType.New;
						break;
					case 1: t = ObjectType.Old;
						break;
					case 2: t = ObjectType.Expired;
						break;
				}

				sm.processSpatialObject(so, t);

			}
			long end = System.currentTimeMillis();
			System.out.println(end-start+" ms");
		}
		catch(IOException ioe){

		}
		
	}
}
