package org.openml.webapplication.evaluate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.openml.apiconnector.io.HttpConnector;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.EvaluationScore;
import org.openml.apiconnector.xml.Run;
import org.openml.apiconnector.xml.Task;
import org.openml.cortana.utils.Evaluations;
import org.openml.cortana.utils.XMLUtils;
import org.openml.cortana.xml.AutoRun;
import org.openml.webapplication.predictionCounter.PredictionCounter;

import nl.liacs.subdisc.Condition;
import nl.liacs.subdisc.ConditionList;
import nl.liacs.subdisc.ConditionListBuilder;
import nl.liacs.subdisc.ConditionListBuilder.ConditionListA;
import nl.liacs.subdisc.ExternalKnowledgeFileLoader;
import nl.liacs.subdisc.FileLoaderXML;
import nl.liacs.subdisc.ROCList;
import nl.liacs.subdisc.SearchParameters;
import nl.liacs.subdisc.Subgroup;
import nl.liacs.subdisc.SubgroupDiscovery;
import nl.liacs.subdisc.SubgroupSet;
import nl.liacs.subdisc.Table;
import nl.liacs.subdisc.TargetConcept;
import nl.liacs.subdisc.TargetType;
import nl.liacs.subdisc.XMLAutoRun;
import nl.liacs.subdisc.gui.ResultTableModel;

public class EvaluateSubgroups implements PredictionEvaluator {
	
	private final Run currentRun;
	private final Task currentTask;
	private final File cortanaXml;
	private File subgroupsCsv;
	private final String evaluation_measure;
	
	public EvaluateSubgroups(int runId, OpenmlConnector openml) throws Exception {
		// this prediction evaluator works by returning no evaluations.
		// these should all have been included in run.xml already, 
		// and will be stored as user calculated results.
		this.currentRun = openml.runGet(runId);
		this.currentTask = openml.taskGet(currentRun.getTask_id());
		this.evaluation_measure = currentTask.getInputsAsMap().get("quality_measure").getQuality_measure();
		
		int subgroupsFileId = currentRun.getOutputFileAsMap().get("subgroups").getFileId();
		URL subgroupsURL = openml.getOpenmlFileUrl(subgroupsFileId, "subgroups.csv");
		subgroupsCsv = File.createTempFile("subgroups", ".tmp");
		subgroupsCsv = HttpConnector.getFileFromUrl(subgroupsURL, subgroupsCsv.getAbsolutePath(), false);
		
		AutoRun autoRun = XMLUtils.generateAutoRunFromSetup(openml, currentRun.getSetup_id(), currentRun.getTask_id());
		cortanaXml = XMLUtils.autoRunToTmpFile(autoRun, "openml-run-" + runId + "-" + UUID.randomUUID().toString());
	}
	
	@Override
	public EvaluationScore[] getEvaluationScores() {
		try {
			List<EvaluationScore> lst = doMagic();
			EvaluationScore[] res = lst.toArray(new EvaluationScore[lst.size()]);
			return res;
		} catch(IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Runtime Exception in doMagic() function: " + e.getMessage());
		}
	}

	@Override
	public PredictionCounter getPredictionCounter() {
		return null;
	}
	

	// Written by MM
	private final List<EvaluationScore> doMagic() throws IOException
	{
		// load autorun.xml file, it contains all relevant information
		FileLoaderXML l = new FileLoaderXML(cortanaXml);

		Table t = l.getTable();
		SearchParameters s = l.getSearchParameters();

		print("\nloading Table and SearchParameters done");

		// direct creation of Conditions is not possible
		// the Condition constructor requires ConditionBase
		// but ConditionBase(Set) has a package private visibility
		// ConditionBase aConditionBase = new ConditionBase(c, Operator.EQUALS);
		// instead, ConditionLists are obtained through ExternalKnowledgeFileLoader

		// assume result file from xml is named: xml.csv
		List<ConditionList> old = getConditionLists(subgroupsCsv.getAbsolutePath(), t);

		print("loading result file done");

		// 'start' Subgroup, has no Conditions, and all members set
		// used to obtain empty ConditionListA
		// used to obtain SubgroupSet
		// NOTE
		// 'start' itself is not added to the SubgroupSet
		Subgroup start = getStartSubgroup(t, s);

		print("creating Subgroup 'start' done");

		// create Subgroups using 'start' (and add them to ResultSet)
		createSubgroups(old, start);

		// SubgroupSet offers desired methods
		SubgroupSet set = start.getParentSet();
		print(set.size() + " Subgroups");

		// assign IDs, (will not be the same as in GUI)
		set.setIDs();

		// put some numbers here
		int top_k = 10; //{ 10, 100 };
		
		SubgroupSet p = set.getPatternTeam(t, top_k);
		
		// hihi *^_^*
		double jointEntropy = p.getJointEntropy();
		ROCList r = p.getROCList();
		double auroc = r.getAreaUnderCurve();
		
		print("\n-------------------------------------");
		print("XML          : " + cortanaXml.getAbsolutePath());
		print("top-k        : " + top_k);
		print("joint entropy: " + Double.toString(jointEntropy));
		print("ROC AUC      : " + Double.toString(auroc));
		print("hull points  : " + r.size());
		
		EvaluationScore je = new EvaluationScore("joint_entropy", Double.toString(jointEntropy), null, null);
		EvaluationScore auroc10 = new EvaluationScore("pattern_team_auroc10", Double.toString(auroc), null, null);
		List<EvaluationScore> evals = Evaluations.extract(subgroupsCsv, this.evaluation_measure);
		
		evals.add(je);
		evals.add(auroc10);
		
		return evals;
	}

	// returns a List of (Deprecated) ConditionLists
	private static final List<ConditionList> getConditionLists(String theResultFile, Table t)
	{
		List<ConditionList> l = Collections.emptyList();

		// ExternalKnowledgeFileLoader only parses '.gkf' / '.lkf' files
		File f = new File(theResultFile);
		File g = new File(theResultFile +  "." + System.currentTimeMillis() + ".gkf");

		// TODO file checks go here

		// do not read and write at the same time

		List<String> a = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(f)))
		{
			// assume Conditions are in last column
			int i = ResultTableModel.COLUMN_COUNT-1;
			String c = ResultTableModel.getColumnName(i, TargetType.SINGLE_NOMINAL);

			String s;
			while ((s = br.readLine()) != null)
			{
				if (s.isEmpty())
					continue;

				String[] sa = s.split(XMLAutoRun.RESULT_SET_DELIMITER, -1);
				if (sa.length != ResultTableModel.COLUMN_COUNT)
					continue;

				// ignore Column name
				if (c.equals(sa[i]))
					continue;

				// assume ConditionList is last column
				a.add(sa[sa.length-1]);
			}
		}
		catch (IOException e)
		{
			// TODO do something more meaningful
			e.printStackTrace();
		}

		// create temporary '.gkf' file
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(g)))
		{
			for (int i = 0, j = a.size(); i < j; ++i)
			{
				bw.write(a.get(i));
				if (i < j-1)
					bw.write("\n");
			}
		}
		catch (IOException e)
		{
			// TODO do something more meaningful
			e.printStackTrace();
		}

		ExternalKnowledgeFileLoader e = new ExternalKnowledgeFileLoader(g.getParent());
		e.createConditionListGlobal(t);
		l = e.getGlobal();

		if (g.exists())
		{
			if (g.delete())
				print("Temporary file was successfully deleted: " + g.getName());
			else
				print("ERROR: Temporary file was not deleted: " + g.getName());
		}

		return l;
	}

	// as in SubgroupDiscovery, create the 'base' Subgroup to start from
	private static final Subgroup getStartSubgroup(Table t, SearchParameters s)
	{
		ConditionListA c = getEmptyConditionListA(t, s);

		int i = t.getNrRows();

		BitSet b = new BitSet(i);
		b.set(0, i);

		SubgroupSet set = getSubgroupSet(t, s);

		return new Subgroup(c, b, set);
	}

	/*
	 * workaround
	 * obtain an empty ConditionListA
	 */
	private static final ConditionListA getEmptyConditionListA(Table t, SearchParameters s)
	{
		// this is not possible , emptyList() is package private
		// ConditionListBuilder.emptyList();

		ConditionListA a = null;

		try
		{
			ConditionListBuilder c = ConditionListBuilder.FACTORY;
			Field f = c.getClass().getDeclaredField("EMPTY_LIST");
			f.setAccessible(true);
			a = (ConditionListA) f.get(c);
		}
		catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e)
		{
			e.printStackTrace();
		}

		return a;
	}

	private static final SubgroupSet getSubgroupSet(Table t, SearchParameters s)
	{
		// see Process.runSubgroupDiscovery()
		TargetConcept aTargetConcept = s.getTargetConcept();
		String aTargetValue = aTargetConcept.getTargetValue();
		int itsPositiveCount = aTargetConcept.getPrimaryTarget().countValues(aTargetValue);

		SubgroupDiscovery aSubgroupDiscovery = new SubgroupDiscovery(s, t, itsPositiveCount, null);
		return aSubgroupDiscovery.getResult();
	}

	private static final void createSubgroups(List<ConditionList> l, Subgroup s)
	{
		// direct creation of ConditionListA is not possible
		// the ConditionListBuilder.FACTORY methods:
		// createList() has package private visibility
		// emptyList() has package private visibility
		//
		// to bypass, Subgroup s is used
		// the start Subgroup (empty ConditionListA, all members set) is
		// used to build new Subgroups
		for (ConditionList cl : l)
		{
			Subgroup copy = s.copy();
			for (Condition c : cl)
				copy.addCondition(c);

			// add this Subgroup to SubgroupSet
			// NOTE all Subgroups share the same SubgroupSet
			copy.getParentSet().add(copy);
		}
	}
	
	private static final void print(String theMessage)
	{
		System.out.println(theMessage);
	}
}
