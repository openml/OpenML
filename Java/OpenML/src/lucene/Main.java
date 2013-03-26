package lucene;

import java.util.Arrays;

public class Main {

	public static void main(String[] args) {
		if(args.length==0){
			System.out.println("No arguments, exiting.");
			System.exit(0);
		}
		String[] otherArgs = Arrays.copyOfRange(args, 1, args.length);
		try {
			if (args[0].equals("index"))
				Indexer.run(otherArgs);
			else if (args[0].equals("search"))
				Searcher.run(otherArgs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
