package moa.streams.openml;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import openml.algorithms.InstancesHelper;
import openml.algorithms.TaskInformation;
import openml.io.ApiConnector;
import openml.xml.DataSetDescription;
import openml.xml.Task;
import weka.core.Instance;
import weka.core.Instances;
import moa.core.InputStreamProgressMonitor;
import moa.core.InstancesHeader;
import moa.core.ObjectRepository;
import moa.options.AbstractOptionHandler;
import moa.options.IntOption;
import moa.streams.InstanceStream;
import moa.tasks.TaskMonitor;

public class OpenmlTaskReader extends AbstractOptionHandler implements InstanceStream {
	
	private static final long serialVersionUID = -7235218716820175095L;
	
	@Override
    public String getPurposeString() {
        return "A stream read from an OpenML repository.";
    }

    public IntOption openmlTaskIdOption = new IntOption(
            "taskId",
            't',
            "The OpenML task that will be performed.",
            1, 1, Integer.MAX_VALUE);
    
    public IntOption instanceRandomSeedOption = new IntOption(
            "instanceRandomSeed", 'i',
            "Please note that this option will just be ignored. "+
            "It is necessary to make the stream compatible with  MOA tasks. ", 
            1);

    protected Instances instances;

    protected Reader fileReader;

    protected boolean hitEndOfFile;

    protected Instance lastInstanceRead;

    protected int numInstancesRead;

    protected InputStreamProgressMonitor fileProgressMonitor;
    
    protected Task openmlTask;

    public OpenmlTaskReader() {
    }

    public OpenmlTaskReader( int taskId ) {
        this.openmlTaskIdOption.setValue(taskId);
        restart();
    }

    @Override
    public void prepareForUseImpl(TaskMonitor monitor,
            ObjectRepository repository) {
        restart();
    }

    @Override
    public InstancesHeader getHeader() {
        return new InstancesHeader(this.instances);
    }

    @Override
    public long estimatedRemainingInstances() {
        double progressFraction = this.fileProgressMonitor.getProgressFraction();
        if ((progressFraction > 0.0) && (this.numInstancesRead > 0)) {
            return (long) ((this.numInstancesRead / progressFraction) - this.numInstancesRead);
        }
        return -1;
    }

    @Override
    public boolean hasMoreInstances() {
        return !this.hitEndOfFile;
    }

    @Override
    public Instance nextInstance() {
        Instance prevInstance = this.lastInstanceRead;
        this.hitEndOfFile = !readNextInstanceFromFile();
        return prevInstance;
    }

    @Override
    public boolean isRestartable() {
        return true;
    }

    @Override
    public void restart() {
        try {
            if (this.fileReader != null) {
                this.fileReader.close();
            }
            if(this.openmlTask == null) {
            	this.openmlTask = ApiConnector.openmlTasksSearch(this.openmlTaskIdOption.getValue());
            }
            
            DataSetDescription dsd = TaskInformation.getSourceData(this.openmlTask).getDataSetDescription();
            String classname = TaskInformation.getSourceData(this.openmlTask).getTarget_feature();
            String identifier = "dataset_" + dsd.getId() + "_" + dsd.getName() + ".arff";
            
            InputStream fileStream = new FileInputStream( InstancesHelper.downloadAndCache("dataset", identifier, dsd.getUrl(), dsd.getMd5_checksum() ) );
            this.fileProgressMonitor = new InputStreamProgressMonitor(
                    fileStream);
            this.fileReader = new BufferedReader(new InputStreamReader(
                    this.fileProgressMonitor));
            this.instances = new Instances(this.fileReader, 1);
            this.instances.setClass( instances.attribute(classname) );
            
            this.numInstancesRead = 0;
            this.lastInstanceRead = null;
            this.hitEndOfFile = !readNextInstanceFromFile();
        } catch (Exception ioe) {
            throw new RuntimeException("ArffFileStream restart failed.", ioe);
        }
    }

    protected boolean readNextInstanceFromFile() {
        try {
            if (this.instances.readInstance(this.fileReader)) {
                this.lastInstanceRead = this.instances.instance(0);
                this.instances.delete(); // keep instances clean
                this.numInstancesRead++;
                return true;
            }
            if (this.fileReader != null) {
                this.fileReader.close();
                this.fileReader = null;
            }
            return false;
        } catch (IOException ioe) {
            throw new RuntimeException(
                    "ArffFileStream failed to read instance from stream.", ioe);
        }
    }

    @Override
    public void getDescription(StringBuilder sb, int indent) {
        // TODO Auto-generated method stub
    }
	
}
