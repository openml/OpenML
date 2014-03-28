package org.openml.tools.twentynewsgroups;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToBinary;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class TwentyNewsgroupsGenerator {
	
	private static final String[] IGNORE = {".DS_Store"};
	private static final List<String> NULL_LIST = null;
	private static final int LIMIT = 0;
	
	private static ArrayList<String> newsgroups; // will be set during documentsToInstances()
	private static int instanceCount;
	
	public static void main( String[] args ) throws Exception {
		new TwentyNewsgroupsGenerator( "/Users/jan/Desktop/", "20_newsgroups" );
		
	}
	
	public TwentyNewsgroupsGenerator( String root_directory, String newsgroup_subdirectory ) throws Exception {
		newsgroups = new ArrayList<String>(); 
		String primaryClassName = "class";
		String secondaryClassName = "class_newsgroup";
		
		Instances dataset = documentsToInstances( root_directory, newsgroup_subdirectory, primaryClassName, "20_newsgroups.arff" );
		
		dataset.renameAttribute( dataset.attribute( primaryClassName ), secondaryClassName );
		
		multilabelInstancesToBinaryDrift( root_directory, new Instances(dataset), secondaryClassName, primaryClassName, "20_newsgroups.drift.arff" );
		
		//testDriftDataset( dataset, root_directory + "20_newsgroups.drift.arff", primaryClassName );
	}
	
	private Instances documentsToInstances( String root_directory, String newsgroup_subdirectory, String className, String filename ) throws Exception {
		instanceCount = 0;
		File root = new File( root_directory + newsgroup_subdirectory );
		String relationName = root.getName();
		BufferedWriter outDefault = new BufferedWriter( new FileWriter( new File(root_directory + filename ) ) );
		ArrayList<Document> documents = traverseDirectory( root, new ArrayList<Document>() );
		
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		attributes.add( new Attribute("document", NULL_LIST ) );
		attributes.add( new Attribute("content", NULL_LIST ));
		attributes.add( new Attribute(className, newsgroups ) );
		
		Instances dataset = new Instances(relationName, attributes, instanceCount );
		dataset.setClass( dataset.attribute(className) );
		
		Collections.sort( documents );
		
		for( Document document : documents ) {
			addDocument(dataset, document);
		}
		
		String stringToWVoptions = "-R 2 -W 1000 -prune-rate -1.0 -N 0 -L -M 1 -O ";
		dataset = applyFilter( dataset, new StringToWordVector( 1000 ), stringToWVoptions );
		dataset = applyFilter( dataset, new NumericToBinary(), "" );
		dataset.setRelationName(relationName);
		
		outDefault.write( dataset.toString() );
		outDefault.close();
		
		return dataset;
	}
	
	private void multilabelInstancesToBinaryDrift( String root_directory, Instances dataset, String classNameSource, String className, String filename ) throws IOException {
		BufferedWriter outDrift = new BufferedWriter( new FileWriter( new File( root_directory + filename ) ));
		String[] newClassValues = { "true", "false" };
		Attribute newClass = new Attribute(className, Arrays.asList( newClassValues ) );
		int oldClassIdx = dataset.attribute( classNameSource ).index();
		
		Instances dataset_drift = new Instances( dataset, 0, 0 );
		dataset_drift.insertAttributeAt( newClass, oldClassIdx );
		dataset_drift.setClass( newClass );
		dataset_drift.setRelationName( dataset_drift.relationName() + "_drift" );
		dataset_drift.deleteAttributeAt( dataset_drift.attribute( classNameSource ).index() );
		
		outDrift.write( dataset_drift.toString() );
		
		for( int iNewsgroup = 0; iNewsgroup < newsgroups.size(); ++iNewsgroup ) {
			for( int iInstance = 0; iInstance < dataset.size(); ++iInstance ) {
				Instance inst = new DenseInstance( dataset.numAttributes() );
				inst.setDataset( dataset_drift );
				Instance originalInstance = dataset.instance(iInstance);
				
				for( int iAttribute = 0; iAttribute < dataset.numAttributes(); ++iAttribute ) {
					if( iAttribute == oldClassIdx ) {
						if( originalInstance.stringValue( oldClassIdx ).equals( newsgroups.get( iNewsgroup ) ) ) {
							inst.setValue( oldClassIdx, 0.0 );
						} else {
							inst.setValue( oldClassIdx, 1.0 );
						}
					} else {
						inst.setValue( iAttribute, originalInstance.value( iAttribute ) );
					}
				}
				
				outDrift.write( inst.toString() + "\n" );
			}
		}
	
		outDrift.close();
	}
	
	private void testDriftDataset( Instances original, String drift, String className ) throws IOException {
		Instances instancesDrift = new Instances( new FileReader( new File( drift ) ) );
		int classIdx = instancesDrift.attribute( className ).index();
		Attribute classAtt = instancesDrift.attribute( className );
		
		int[] originalDistribution = new int[newsgroups.size()];
		for( int iInstance = 0; iInstance < original.size(); ++iInstance ) {
			Instance inst = original.instance(iInstance);
			originalDistribution[(int) inst.value( classAtt )] ++;
		}
		
		System.out.println("Original Distribution: \n" + Arrays.toString( originalDistribution ) );
		
		for( int iNewsgroup = 0; iNewsgroup < newsgroups.size(); ++iNewsgroup ) {
			int currentCount = 0;
			for( int iInstance = 0; iInstance < original.size(); ++iInstance ) {
				int idx = iNewsgroup * original.size() + iInstance;
				Instance inst = instancesDrift.instance(idx);
				currentCount += inst.stringValue( classIdx ).equals("true") ? 1 : 0 ;
			}
			if( currentCount != originalDistribution[iNewsgroup] ) System.err.println("ERROR!!");
			
		}
	}
	
	private ArrayList<Document> traverseDirectory( File filepointer, ArrayList<Document> documents ) throws IOException {
		for( File f : filepointer.listFiles() ) {
			if( Arrays.asList( IGNORE ).contains( f.getName() ) ) continue;
			
			if( f.isDirectory() ) {
				traverseDirectory( f, documents );
			} else {
				if( instanceCount < LIMIT || LIMIT < 1 ) {
					String contents = readFile( f );
					Document current = new Document( f.getName(), contents, filepointer.getName() );
					documents.add( current );
					if( newsgroups.contains(filepointer.getName() ) == false ) {
						newsgroups.add( filepointer.getName() );
					}
					instanceCount += 1;
				}
			}
		}
		return documents;
	}
	
	private static void addDocument( Instances dataset, Document document ) {
		double[] newInst = new double[3];
		newInst[0] = (double) dataset.attribute(0).addStringValue( document.name );
		newInst[1] = (double) dataset.attribute(1).addStringValue( document.content );
		newInst[2] = dataset.attribute(2).indexOfValue( document.newsgroup );
		dataset.add( new DenseInstance( 1.0, newInst) );
	}
	
	private static Instances applyFilter( Instances dataset, Filter filter, String options ) throws Exception {
		((OptionHandler) filter).setOptions( Utils.splitOptions( options ) );
		filter.setInputFormat(dataset);
		return Filter.useFilter(dataset, filter);
	}
	
	private static String readFile( File textfile ) throws IOException {
		InputStreamReader is = new InputStreamReader(new FileInputStream(textfile));
		StringBuffer contents = new StringBuffer();
		int c;
		while ((c = is.read()) != -1) {
			contents.append((char) c);
		}
		is.close();
		return contents.toString();
	}
	
	private static class Document implements Comparable<Document> {
		private final String newsgroup;
		private final String name;
		private final String content;
		
		public Document( String name, String content, String newsgroup ) {
			this.name = name;
			this.content = content;
			this.newsgroup = newsgroup;
		}
		
		@Override
		public String toString() {
			String res = "[" + newsgroup + "] " + name + " - ";
			if( content.length() > 100 ) {
				res += content.substring( 0, 100 );
			} else {
				res += content;
			}
			return res;
		}
		
		@Override
		public int compareTo( Document o ) {
			int self = Integer.parseInt( name );
			int other = Integer.parseInt( o.name );
			
			return self - other;
		}
	}
}