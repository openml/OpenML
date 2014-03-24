package org.openml.tools.twentynewsgroups;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToBinary;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class TwentyNewsgroupsGenerator {
	
	private static final String[] IGNORE = {".DS_Store"};
	private static final List<String> NULL_LIST = null;
	private static final int LIMIT = -1;
	
	private static BufferedWriter out;
	private static ArrayList<Document> documents;
	private static ArrayList<String> newsgroups;
	private static int instanceCount = 0;
	
	public static void main( String[] args ) throws Exception {
		new TwentyNewsgroupsGenerator( "/Users/jan/Desktop/20_newsgroups" );
		
	}
	
	public TwentyNewsgroupsGenerator( String root_directory ) throws Exception {
		File root = new File( root_directory );
		String relationName = root.getName();
		
		out = new BufferedWriter( new FileWriter( new File("20_newsgroups.arff") ) );
		documents = new ArrayList<Document>();
		newsgroups = new ArrayList<String>();
		
		traverseDirectory( root );
		
		ArrayList<Attribute> attributes = new ArrayList<Attribute>();
		attributes.add( new Attribute("document", NULL_LIST ) );
		attributes.add( new Attribute("content", NULL_LIST ));
		attributes.add( new Attribute("class", newsgroups ) );
		
		Instances dataset = new Instances(relationName, attributes, instanceCount );
		dataset.setClass( dataset.attribute("class") );
		
		Instances dataset_drift = new Instances( dataset, 0, 0 );
		
		Collections.sort( documents );
		
		for( Document document : documents ) {
			addDocument(dataset, document);
		}
		
		String stringToWVoptions = "-R 2 -W 1000 -prune-rate -1.0 -N 0 -L -M 1 -O ";
		dataset = applyFilter( dataset, new StringToWordVector( 1000 ), stringToWVoptions );
		dataset = applyFilter( dataset, new NumericToBinary(), "" );
		dataset.setRelationName(relationName);
		
		out.write( dataset.toString() );
		out.close();
	}
	
	private void traverseDirectory( File filepointer ) throws IOException {
		for( File f : filepointer.listFiles() ) {
			if( Arrays.asList( IGNORE ).contains( f.getName() ) ) continue;
			
			if( f.isDirectory() ) {
				traverseDirectory( f );
				
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
