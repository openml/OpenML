package crossvalidation;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;

public class TaskConverter implements Converter {	
	 private final Converter taskConverter;
	 private final ReflectionProvider reflectionProvider;
	 
	public TaskConverter(Converter taskConverter, ReflectionProvider r){
		this.taskConverter=taskConverter;
		this.reflectionProvider = r;
	}
	
	public boolean canConvert(Class type) {
		return Task.class.isAssignableFrom(type);
	}

	@Override
	public void marshal(Object arg0, HierarchicalStreamWriter arg1,
			MarshallingContext arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader r, UnmarshallingContext context) {
		String taskType  = r.getAttribute("task-type");
		//if(taskType.equals("prediction")){
			Class<?> resultType =  Prediction.class;
		//}
		Object result = reflectionProvider.newInstance(resultType);
        return context.convertAnother(result, resultType, taskConverter);
		}
}

