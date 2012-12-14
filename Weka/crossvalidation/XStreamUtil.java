//

package crossvalidation;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.*;

public class XStreamUtil {
	
	XStream xstream;
	
	public XStreamUtil() {
		this.xstream = new XStream();
		xstream.processAnnotations(TaskContainer.class);
		xstream.processAnnotations(Prediction.class);
		xstream.processAnnotations(Task.class);
		xstream.processAnnotations(CrossValidation.class);
		xstream.autodetectAnnotations(true);	
	}
	
	public static void main(String argv[]) throws IOException{
		XStreamUtil util = new XStreamUtil();
		String pathToXmlTask = "/home/gitte/git/OpenML/Weka/crossvalidation/irisTask.xml";
		TaskContainer taskContainer = util.toTask(pathToXmlTask);
		//taskContainer.setPredictions(new ArrayList<Prediction>());
		System.out.println(taskContainer.getTaskId());
		System.out.println(taskContainer.getPredictions().size());

		
	}

	public TaskContainer toTask(String xmlUrl) throws FileNotFoundException {
		FileReader fr = new FileReader(new File(xmlUrl));
		return (TaskContainer)xstream.fromXML(fr);
       }

}
