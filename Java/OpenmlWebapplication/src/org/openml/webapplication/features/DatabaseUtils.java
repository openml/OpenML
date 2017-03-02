package org.openml.webapplication.features;

import org.json.JSONArray;
import org.json.JSONException;
import org.openml.apiconnector.algorithms.Conversion;
import org.openml.apiconnector.algorithms.QueryUtils;
import org.openml.apiconnector.io.OpenmlConnector;
import org.openml.apiconnector.xml.DataSetDescription;
import weka.core.Instances;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class DatabaseUtils {
    public DatabaseUtils(OpenmlConnector connector){
        this.apiconnector = connector;
    }
    private OpenmlConnector apiconnector;

    public List<String> GetQualitiesAvailable(int datasetId, Integer window_size) throws Exception {
        List<String> qualitiesAvailable = Arrays.asList(apiconnector.dataQualities(datasetId).getQualityNames());
        if (window_size != null) {
            // alternative approach to knowing which features are already complete.
            String sql =
                    "SELECT i.quality, CEIL(`q`.`value` / " + window_size + ") AS `numIntervals`, COUNT(*) AS `present` " +
                            "FROM `data_quality` `q`, `dataset` `d` LEFT JOIN `data_quality_interval` `i` ON `d`.`did` = `i`.`data` AND `i`.`interval_end` - `i`.`interval_start` =  " + window_size + " " +
                            "WHERE `d`.`did` = `q`.`data` AND `q`.`quality` = 'NumberOfInstances'  AND `d`.`error` = 'false' AND `d`.`processed` IS NOT NULL AND d.did = " + datasetId + " " +
                            "GROUP BY `d`.`did`,`i`.`quality` HAVING `present` = `numIntervals`";
            Conversion.log("OK", "FantailQuery for interval queries", sql);
            qualitiesAvailable = Arrays.asList(QueryUtils.getStringsFromDatabase(apiconnector, sql));
        }
        return qualitiesAvailable;
    }

    public DataSetDescription GetDatasetDescription(int did) throws Exception {
        return apiconnector.dataGet(did);
    }

    public Instances getDataset(DataSetDescription dsd) throws Exception
    {
        Conversion.log("OK", "Extract Features", "Start downloading dataset: " + dsd.getId());

        Instances dataset = new Instances(new FileReader(dsd.getDataset(apiconnector.getApiKey())));

        if (dsd.getDefault_target_attribute() == null) {
            throw new RuntimeException("Default target attribute is null. ");
        }

        return dataset;
    }

    public Integer findDatasetIdWithoutMetafeatures(List<String> globalMetafeatures,
                                                    List<String> attributeMetafeatures,
                                                    Integer window_size,
                                                    boolean random,
                                                    String priorityTag
    ) throws JSONException, Exception {
        int globalMetafeaturesCount = globalMetafeatures.size();
        String tagJoin = "";
        String tagSelect = "";
        String tagSort = "";
        if (priorityTag != null) {
            tagSelect = ", t.tag ";
            tagSort = "t.tag DESC, "; // to avoid NULL values first
            tagJoin = "LEFT JOIN dataset_tag t ON q.data = t.id AND t.tag = '" + priorityTag + "' ";
        }

        // In the case the windows_size is specified we are only considering only the data_quality_interval table.
        // In that case we take the number of instances out or regular quality table and join dataset table
        // with all entries from the interval table. We compute the number of intervals (q.value / windows size),
        // from that we calculate ratio of all qualities per interval. In the correct case we should expect
        // all features in all intervals
        String sql =
                "SELECT `d`.`did`, `q`.`value` AS `numInstances`, `i`.`interval_end` - `i`.`interval_start` AS `interval_size`, " +
                        "CEIL(`q`.`value` / " + window_size + ") AS `numIntervals`, " +
                        "(COUNT(*) / CEIL(`q`.`value` / " + window_size + ")) AS `qualitiesPerInterval`, " +
                        "COUNT(*) AS `qualities` " + tagSelect +
                        "FROM `data_quality` `q` " + tagJoin +
                        ", `dataset` `d`" +
                        "LEFT JOIN `data_quality_interval` `i` ON `d`.`did` = `i`.`data` AND `i`.`interval_end` - `i`.`interval_start` =  " + window_size + " " +
                        "WHERE `d`.`did` = `q`.`data` " +
                        "AND `q`.`quality` = 'NumberOfInstances'  " +
                        "AND `d`.`error` = 'false' AND `d`.`processed` IS NOT NULL " +
                        "GROUP BY `d`.`did` " +
                        "HAVING (COUNT(*) / CEIL(`q`.`value` / " + window_size + ")) < " + globalMetafeaturesCount + " " +
                        "ORDER BY " + tagSort + "`qualitiesPerInterval` ASC LIMIT 0,100; ";

        if(window_size == null) {
            //find all datasets with all metafeatures in globalMetafeatures that have all of them computed
            // and substract this from the set of all datasets
            sql =   "SELECT DISTINCT dataset.did FROM dataset LEFT JOIN (" +
                    "SELECT q.data, COUNT(*) AS `numQualities`" + tagSelect +
                            " FROM data_quality q " + tagJoin +
                            " WHERE q.quality in ('" +  String.join("','", globalMetafeatures) + "')" +
                            " GROUP BY q.data HAVING numQualities = " + globalMetafeaturesCount + ") as `result2`" +
                            " ON dataset.did = result2.data WHERE result2.data IS NULL" +
                            " ORDER BY " + tagSort + " dataset.did LIMIT 0,100";
        }

        Conversion.log("OK", "FantailQuery", sql);
        JSONArray sqlResult = (JSONArray) apiconnector.freeQuery(sql).get("data");
        Integer datasetWithoutGlobal = pickSample(sqlResult, random);
        if (datasetWithoutGlobal != null){
            return datasetWithoutGlobal;
        }
        if (window_size == null){
            return getDatasetWithoutAttributeMetafeatures(attributeMetafeatures, tagJoin, tagSelect, tagSort, random);
        }
        return null;

    }

    public Integer pickSample(JSONArray sqlResult, boolean random) throws JSONException {
        int randomint = 0;
        if (random) {
            Random randomgen = new Random(System.currentTimeMillis());
            randomint = Math.abs(randomgen.nextInt());
        }

        if(sqlResult.length() > 0) {
            JSONArray result = (JSONArray) sqlResult.get(randomint % sqlResult.length());
            int dataset_id = result.getInt(0);
            return dataset_id;
        } else {
            return null;
        }
    }

    public int getDatasetWithoutAttributeMetafeatures(
            List<String> attributeMetafeatures, String tagJoin, String tagSelect, String tagSort, boolean random) throws Exception {
        int attributeMetafeaturesCount = attributeMetafeatures.size();
        //Same query like in the sql for obtaining datasets without global attributes
        String sql =   "SELECT DISTINCT dataset.did FROM dataset LEFT JOIN (" +
                "SELECT q.data, COUNT(*) AS `numQualities`" + tagSelect +
                " FROM feature_quality q " + tagJoin +
                " JOIN (SELECT data_feature.did, COUNT(*) as `number_of_attributes` FROM data_feature" +
                " GROUP BY data_feature.did) as `attCounts` ON attCounts.did = q.data" +
                " WHERE q.quality in ('" +  String.join("','", attributeMetafeatures) + "')" +
                " GROUP BY q.data HAVING numQualities = " + "max(attCounts.number_of_attributes)*"+ attributeMetafeaturesCount + ") as `result2`" +
                " ON dataset.did = result2.data WHERE result2.data IS NULL" +
                " ORDER BY " + tagSort + " dataset.did LIMIT 0,100";

        JSONArray sqlResult = (JSONArray) apiconnector.freeQuery(sql).get("data");
        Conversion.log("OK", "FantailQuery", sql);
        return pickSample(sqlResult, random);
    }
}
