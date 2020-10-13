<?php
require_once('Api_new.php');

/**
 * @OA\Info(title="OpenML API", version="1.0.0", description="REST API for sharing, organizing and reusing machine learning datasets, code, and experiments. Follows a predictive URL scheme from endpoint https://www.openml.org/api/v1/json (or /xml). You need to add your api_key for every call except GET calls. The API key can be found in your profile on openml.org."
 * )
 */
 
 /**
 * @OA\Server(
 *	url="https://openml.org/api/v1/json",
 *  description="The current OpenML API"
 * )
 * @OA\Server(
 *	url="https://test.openml.org/api/v1/json",
 *  description="The test OpenML API"
 * )
 */
class Api extends Api_new
{

    private $version;

    function __construct()
    {
        parent::__construct();
    }
}

/**
 * @OA\Schema(
 *    schema="inline_response_200_16_flow_untag",
 *	@OA\Property(
 *        property="id",
 *        type="string",
 *        description="ID of the untagged setup",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="EvaluationMeasureList_evaluation_measures_measures",
 *	  @OA\Property(
 *        property="measure",
 *        type="array",
 *        description="The evaluation measure names",
 *		  @OA\Items(
 *			type="string"
 *		  )
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="inline_response_200_10_flow_exists",
 *	@OA\Property(
 *        property="id",
 *        type="string",
 *        description="The id of the flow with the given name and (external) version",
 *    ),
 *	@OA\Property(
 *        property="exists",
 *        type="string",
 *        description="true or false",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="Data_data_set_description",
 *	@OA\Property(
 *        property="default_target_attribute",
 *        type="string",
 *        description="For tabular data, the name of the column that is typically used as the target attribute for that data set",
 *    ),
 *	@OA\Property(
 *        property="upload_date",
 *        type="string",
 *        description="The datetime that the dataset was uploaded, format yyyy-MM-dd HH:mm:ss",
 *    ),
 *	@OA\Property(
 *        property="version_label",
 *        type="string",
 *        description="The version of the dataset, as defined by the uploader, for reference. Can be any format as long as it is unique.",
 *    ),
 *	@OA\Property(
 *        property="description",
 *        type="string",
 *        description="Wiki description of the dataset, in (Git flavoured) markdown format",
 *    ),
 *	@OA\Property(
 *        property="format",
 *        type="string",
 *        description="Data format, for instance ARFF",
 *    ),
 *	@OA\Property(
 *        property="url",
 *        type="string",
 *        description="The URL where the data can be downloaded",
 *    ),
 *	@OA\Property(
 *        property="tag",
 *        type="array",
 *        description="Tags added by OpenML users. Includes study tags in the form `study_1`",
 *		  @OA\Items(
 *			type="string"
 *		  )
 *    ),
 *	@OA\Property(
 *        property="visibility",
 *        type="string",
 *        description="Who can see the dataset. For instance `public`.",
 *    ),
 *	@OA\Property(
 *        property="md5_checksum",
 *        type="string",
 *        description="Checksum to verify downloads of the dataset",
 *    ),
 *	@OA\Property(
 *        property="version",
 *        type="string",
 *        description="The version of the dataset, set by OpenML. A positive integer",
 *    ),
 *	@OA\Property(
 *        property="status",
 *        type="string",
 *        description="active, in_preparation, or deactivated",
 *    ),
 *	@OA\Property(
 *        property="file_id",
 *        type="string",
 *        description="The ID of the dataset file stored on the OpenML server",
 *    ),
 *	@OA\Property(
 *        property="licence",
 *        type="string",
 *        description="The licence granted for using the dataset, for instance Public or CC-BY",
 *    ),
 *	@OA\Property(
 *        property="original_data_url",
 *        type="string",
 *        description="The URL where the original data is hosted.",
 *    ),
 *	@OA\Property(
 *        property="id",
 *        type="string",
 *        description="ID of the dataset, a positive integer",
 *    ),
 *	@OA\Property(
 *        property="name",
 *        type="string",
 *        description="The name of the dataset",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="DataList_data_dataset",
 *	@OA\Property(
 *        property="did",
 *        type="string",
 *        description="The dataset ID",
 *    ),
 *	@OA\Property(
 *        property="status",
 *        type="string",
 *        description="The dataset status, either in_preparation, active, or deactivated",
 *    ),
 *	@OA\Property(
 *        property="quality",
 *        type="array",
 *		  @OA\Items(
 *			ref="#/components/schemas/DataList_data_quality"
 *		  )
 *    ),
 *	@OA\Property(
 *        property="name",
 *        type="string",
 *        description="The dataset name",
 *    ),
 *	@OA\Property(
 *        property="format",
 *        type="string",
 *        description="The data format of the dataset, e.g. ARFF",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="Task_task_description_estimation_procedure",
 *	@OA\Property(
 *        property="parameter",
 *        type="array",
 *		  @OA\Items(
 *			ref="#/components/schemas/Task_task_description_estimation_procedure_parameter"
 *		  )
 *    ),
 *	@OA\Property(
 *        property="type",
 *        type="string",
 *        description="The type of procedure, e.g. crossvalidation",
 *    ),
 *	@OA\Property(
 *        property="data_splits_url",
 *        type="string",
 *        description="The url where the data splits can be downloaded",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="DataList_data_quality",
 *	@OA\Property(
 *        property="name",
 *        type="string",
 *        description="The name of the property",
 *    ),
 *	@OA\Property(
 *        property="value",
 *        type="string",
 *        description="The value of the property",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="FlowList_flows_flow",
 *	@OA\Property(
 *        property="full_name",
 *        type="string",
 *        description="The full flow name (name + internal version number)",
 *    ),
 *	@OA\Property(
 *        property="external_version",
 *        type="string",
 *        description="The external flow version",
 *    ),
 *	@OA\Property(
 *        property="version",
 *        type="string",
 *        description="The internal flow version",
 *    ),
 *	@OA\Property(
 *        property="uploader",
 *        type="string",
 *        description="The ID of the person who uploaded the flow",
 *    ),
 *	@OA\Property(
 *        property="id",
 *        type="string",
 *        description="The flow ID",
 *    ),
 *	@OA\Property(
 *        property="name",
 *        type="string",
 *        description="The flow name",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="Run_run_description_input_data_dataset",
 *	@OA\Property(
 *        property="did",
 *        type="string",
 *        description="The id of the dataset",
 *    ),
 *	@OA\Property(
 *        property="url",
 *        type="string",
 *        description="The download url of the dataset",
 *    ),
 *	@OA\Property(
 *        property="name",
 *        type="string",
 *        description="The name of the dataset",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="EvaluationList_evaluations",
 *	  @OA\Property(
 *        property="evaluation",
 *        type="array",
 *		  @OA\Items(
 *			ref="#/components/schemas/EvaluationList_evaluations_evaluation"
 *		  )
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="inline_response_200_14_study_delete",
 *	@OA\Property(
 *        property="id",
 *        type="string",
 *        description="ID of the deleted setup, a positive integer",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="DataUnprocessed",
 *    type="object",
 *	@OA\Property(
 *        property="data_unprocessed",
 *        ref="#/components/schemas/DataUnprocessed_data_unprocessed",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="DataUnprocessed_data_unprocessed_dataset",
 *	@OA\Property(
 *        property="did",
 *        type="string",
 *        description="ID of the dataset a positive integer",
 *    ),
 *	@OA\Property(
 *        property="status",
 *        type="string",
 *        description="Status of the dataset",
 *    ),
 *	@OA\Property(
 *        property="version",
 *        type="string",
 *        description="Version of the dataset, a positive integer",
 *    ),
 *	@OA\Property(
 *        property="name",
 *        type="string",
 *        description="The name of the dataset",
 *    ),
 *	@OA\Property(
 *        property="format",
 *        type="string",
 *        description="The dataset format, e.g. ARFF",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="EstimationProcedureList_estimationprocedures",
 *	@OA\Property(
 *        property="estimationprocedure",
 *        type="array",
 *		  @OA\Items(
 *			ref="#/components/schemas/EstimationProcedureList_estimationprocedures_estimationprocedure"
 *		  )
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="TaskTypeList",
 *    type="object",
 *	@OA\Property(
 *        property="task_types",
 *        ref="#/components/schemas/TaskTypeList_task_types",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="Run_run_description_output_data_evaluation",
 *	@OA\Property(
 *        property="array_data",
 *        type="string",
 *        description="For composite evaluation measures (e.g. per-class measures, confusion matrix), a string (JSON) representation of the   evaluation.",
 *    ),
 *	@OA\Property(
 *        property="name",
 *        type="string",
 *        description="The name of the evaluation measure",
 *    ),
 *	@OA\Property(
 *        property="value",
 *        type="string",
 *        description="The result of the evaluation",
 *    ),
 *	@OA\Property(
 *        property="flow_id",
 *        type="string",
 *        description="The id of the code used to compute this evaluation method",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="TaskList_task_quality",
 *	@OA\Property(
 *        property="name",
 *        type="string",
 *        description="The name of the quality",
 *    ),
 *	@OA\Property(
 *        property="value",
 *        type="string",
 *        description="The value of the quality",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="Task_task_description",
 *	@OA\Property(
 *        property="input",
 *        type="array",
 *		  @OA\Items(
 *			ref="#/components/schemas/Task_task_description_input"
 *		  )
 *    ),
 *	@OA\Property(
 *        property="task_type",
 *        type="string",
 *        description="The type of the task, e.g. Supervised Classification",
 *    ),
 *	@OA\Property(
 *        property="tag",
 *        type="array",
 *        description="Tags added by OpenML uers. Includes study tags in the form 'study_1'",
 *		  @OA\Items(
 *			type="string"
 *		  )
 *    ),
 *	@OA\Property(
 *        property="task_id",
 *        type="string",
 *        description="ID of the task, a positive integer",
 *    ),
 *	@OA\Property(
 *        property="output",
 *        type="array",
 *		  @OA\Items(
 *			ref="#/components/schemas/Task_task_description_output"
 *		  )
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="inline_response_200_9_upload_flow",
 *	@OA\Property(
 *        property="id",
 *        type="string",
 *        description="ID of the uploaded flow, a positive integer",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="Flow_flow_description",
 *	@OA\Property(
 *        property="upload_date",
 *        type="string",
 *        description="The datetime that the flow was uploaded, format yyyy-MM-dd HH:mm:ss",
 *    ),
 *	@OA\Property(
 *        property="description",
 *        type="string",
 *        description="Wiki description of the flow, in (Git flavoured) markdown format",
 *    ),
 *	@OA\Property(
 *        property="language",
 *        type="string",
 *        description="The programming language the flow is written in.",
 *    ),
 *	@OA\Property(
 *        property="parameter",
 *        type="array",
 *		  @OA\Items(
 *			ref="#/components/schemas/Flow_flow_description_parameter"
 *		  )
 *    ),
 *	@OA\Property(
 *        property="tag",
 *        type="array",
 *        description="Tags added by OpenML users. Includes study tags in the form `study_1`",
 *		  @OA\Items(
 *			type="string"
 *		  )
 *    ),
 *	@OA\Property(
 *        property="version",
 *        type="string",
 *        description="The version of the flow, set by OpenML. A positive integer",
 *    ),
 *	@OA\Property(
 *        property="version_label",
 *        type="string",
 *        description="The version of the flow, as defined by the uploader, for reference. Can be any format as long as it is unique.",
 *    ),
 *	@OA\Property(
 *        property="dependencies",
 *        type="string",
 *        description="The libraries that this flow depends on, and their version numbers.",
 *    ),
 *	@OA\Property(
 *        property="uploader",
 *        type="string",
 *        description="The uploader of the flow",
 *    ),
 *	@OA\Property(
 *        property="id",
 *        type="string",
 *        description="ID of the flow, a positive integer",
 *    ),
 *	@OA\Property(
 *        property="name",
 *        type="string",
 *        description="The name of the flow",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="Task",
 *    type="object",
 *	@OA\Property(
 *        property="task_description",
 *        ref="#/components/schemas/Task_task_description",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="Setup",
 *    type="object",
 *	@OA\Property(
 *        property="setup_parameters",
 *        ref="#/components/schemas/Setup_setup_parameters",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="FlowList_flows",
 *	@OA\Property(
 *        property="flow",
 *        type="array",
 *		  @OA\Items(
 *			ref="#/components/schemas/FlowList_flows_flow"
 *		  )
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="RunTrace",
 *    type="object",
 *	@OA\Property(
 *        property="trace",
 *        ref="#/components/schemas/RunTrace_trace",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="Task_task_description_predictions_feature",
 *	@OA\Property(
 *        property="type",
 *        type="string",
 *        description="The type of the prediction feature, e.g. integer",
 *    ),
 *	@OA\Property(
 *        property="name",
 *        type="string",
 *        description="The name of the prediction feature, e.g. row_id",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="inline_response_200_24_study_delete",
 *	@OA\Property(
 *        property="id",
 *        type="string",
 *        description="ID of the deleted study, a positive integer",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="inline_response_200_5_upload_task",
 *	@OA\Property(
 *        property="id",
 *        type="string",
 *        description="ID of the uploaded task, a positive integer",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="inline_response_200_13_flow_untag",
 *	@OA\Property(
 *        property="id",
 *        type="string",
 *        description="ID of the untagged flow",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="UserList_users",
 *	@OA\Property(
 *        property="user",
 *        type="array",
 *		  @OA\Items(
 *			ref="#/components/schemas/UserList_users_user"
 *		  )
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="DataFeatures",
 *    type="object",
 *	@OA\Property(
 *        property="data_features",
 *        ref="#/components/schemas/DataFeatures_data_features",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="Task_task_description_predictions",
 *	@OA\Property(
 *        property="feature",
 *        type="array",
 *		  @OA\Items(
 *			ref="#/components/schemas/Task_task_description_predictions_feature"
 *		  )
 *    ),
 *	@OA\Property(
 *        property="format",
 *        type="string",
 *        description="The fromat of the predictions, e.g. ARFF",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="inline_response_200_15_flow_tag",
 *	@OA\Property(
 *        property="id",
 *        type="string",
 *        description="ID of the tagged setup",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="Setup_setup_parameters",
 *	@OA\Property(
 *        property="parameter_setting",
 *        type="array",
 *		  @OA\Items(
 *			ref="#/components/schemas/Setup_setup_parameters_parameter_setting"
 *		  )
 *    ),
 *	@OA\Property(
 *        property="flow_id",
 *        type="string",
 *        description="ID of the flow, a positive integer",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="DataList",
 *    type="object",
 *	@OA\Property(
 *        property="data",
 *        ref="#/components/schemas/DataList_data",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="inline_response_200_26_study_attach",
 *	@OA\Property(
 *        property="linked_entities",
 *        type="string",
 *        description="The number of linked entities",
 *    ),
 *	@OA\Property(
 *        property="main_entity_type",
 *        type="string",
 *        description="Main entity type of the of the study",
 *    ),
 *	@OA\Property(
 *        property="id",
 *        type="string",
 *        description="ID of the study, a positive integer",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="inline_response_200_2_data_tag",
 *	@OA\Property(
 *        property="id",
 *        type="string",
 *        description="ID of the tagged dataset",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="inline_response_200_23_upload_flow",
 *	@OA\Property(
 *        property="id",
 *        type="string",
 *        description="ID of the run with the trace, a positive integer",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="EvaluationRequest_evaluation_request",
 *	@OA\Property(
 *        property="run",
 *        ref="#/components/schemas/EvaluationRequest_evaluation_request_run",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="Run",
 *    type="object",
 *	@OA\Property(
 *        property="run_description",
 *        ref="#/components/schemas/Run_run_description",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="inline_response_200_6_task_tag",
 *	@OA\Property(
 *        property="id",
 *        type="string",
 *        description="ID of the tagged task",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="RunList",
 *    type="object",
 *	@OA\Property(
 *        property="runs",
 *        ref="#/components/schemas/RunList_runs",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="inline_response_200_17_data_delete",
 *	@OA\Property(
 *        property="id",
 *        type="string",
 *        description="ID of the deleted run, a positive integer",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="Task_task_description_input",
 *	@OA\Property(
 *        property="data_set",
 *        ref="#/components/schemas/Task_task_description_data_set",
 *    ),
 *	@OA\Property(
 *        property="cost_matrix",
 *        type="array",
 *        description="The cost matrix, indicating the costs for each type of misclassification",
 *		  @OA\Items(
 *			type="array",
 *          @OA\Items(
 *              type="integer",
 *              format="int64"
 *          )
 *		  )
 *    ),
 *	@OA\Property(
 *        property="name",
 *        type="string",
 *        description="The name of the input, e.g. source_data",
 *    ),
 *	@OA\Property(
 *        property="evaluation_measures",
 *        ref="#/components/schemas/Task_task_description_evaluation_measures",
 *    ),
 *	@OA\Property(
 *        property="estimation_procedure",
 *        ref="#/components/schemas/Task_task_description_estimation_procedure",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="UserList",
 *    type="object",
 *	@OA\Property(
 *        property="users",
 *        ref="#/components/schemas/UserList_users",
 *    )
 *
 *)
 */
/**
 * @OA\Schema(
 *    schema="Study_study_runs",
 *	@OA\Property(
 *        property="run_id",
 *        type="array",
 *		  @OA\Items(
 *			type="string"
 *		  )
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="DataFeatures_data_features_feature",
 *	@OA\Property(
 *        property="index",
 *        type="string",
 *        description="Feature index",
 *    ),
 *	@OA\Property(
 *        property="name",
 *        type="string",
 *        description="Feature name",
 *    ),
 *	@OA\Property(
 *        property="data_type",
 *        type="string",
 *        description="Feature data type",
 *    ),
 *	@OA\Property(
 *        property="is_target",
 *        type="string",
 *        description="Whether this feature is seen as a target feature",
 *    ),
 *	@OA\Property(
 *        property="is_ignore",
 *        type="string",
 *        description="Whether this feature should be ignored in modelling (e.g. every value is unique)",
 *    ),
 *	@OA\Property(
 *        property="is_row_identifier",
 *        type="string",
 *        description="Whether this feature is a row identifier",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="inline_response_200_21_upload_flow",
 *	@OA\Property(
 *        property="id",
 *        type="string",
 *        description="ID of the evaluated run, a positive integer",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="TaskType",
 *    type="object",
 *	@OA\Property(
 *        property="description",
 *        type="string",
 *        description="A description of the task type",
 *    ),
 *	@OA\Property(
 *        property="date",
 *        type="string",
 *        description="The date when the task type was created",
 *    ),
 *	@OA\Property(
 *        property="output",
 *        type="array",
 *		  @OA\Items(
 *			ref="#/components/schemas/TaskType_output"
 *		  )
 *    ),
 *	@OA\Property(
 *        property="contributor",
 *        type="array",
 *		  @OA\Items(
 *			type="string"
 *		  )
 *    ),
 *	@OA\Property(
 *        property="input",
 *        type="array",
 *		  @OA\Items(
 *			ref="#/components/schemas/TaskType_input"
 *		  )
 *    ),
 *	@OA\Property(
 *        property="id",
 *        type="string",
 *        description="ID of the task type, a positive integer",
 *    ),
 *	@OA\Property(
 *        property="name",
 *        type="string",
 *        description="The name of the task type, e.g. Supervised Classification",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="Run_run_description_output_data_file",
 *	@OA\Property(
 *        property="did",
 *        type="string",
 *        description="The id of the uploaded file",
 *    ),
 *	@OA\Property(
 *        property="file_id",
 *        type="string",
 *        description="The reference id of the uploaded file, for downloading afterward",
 *    ),
 *	@OA\Property(
 *        property="name",
 *        type="string",
 *        description="The name of the uploaded file (e.g., description, predictions, model,...)",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="TaskType_input",
 *	@OA\Property(
 *        property="data_set",
 *        ref="#/components/schemas/Task_task_description_data_set",
 *    ),
 *	@OA\Property(
 *        property="cost_matrix",
 *        type="array",
 *		  @OA\Items(
 *			type="array",
 *          @OA\Items(
 *              type="integer",
 *              format="int64"
 *          )
 *		  )
 *    ),
 *	@OA\Property(
 *        property="name",
 *        type="string",
 *        description="The name of the input, e.g. source_data",
 *    ),
 *	@OA\Property(
 *        property="evaluation_measures",
 *        ref="#/components/schemas/Task_task_description_evaluation_measures",
 *    ),
 *	@OA\Property(
 *        property="estimation_procedure",
 *        ref="#/components/schemas/Task_task_description_estimation_procedure",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="Run_run_description_output_data",
 *	@OA\Property(
 *        property="evaluation",
 *        type="array",
 *		  @OA\Items(
 *			ref="#/components/schemas/Run_run_description_output_data_evaluation"
 *		  )
 *    ),
 *	@OA\Property(
 *        property="file",
 *        type="array",
 *		  @OA\Items(
 *			ref="#/components/schemas/Run_run_description_output_data_file"
 *		  )
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="inline_response_200_3_data_untag",
 *	@OA\Property(
 *        property="id",
 *        type="string",
 *        description="ID of the untagged dataset",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="Task_task_description_output",
 *	@OA\Property(
 *        property="name",
 *        type="string",
 *        description="The name of the output, e.g. predictions",
 *    ),
 *	@OA\Property(
 *        property="predictions",
 *        ref="#/components/schemas/Task_task_description_predictions",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="DataQualityList",
 *    type="object",
 *	@OA\Property(
 *        property="data_qualities_list",
 *        ref="#/components/schemas/DataQualityList_data_qualities_list",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="DataUnprocessed_data_unprocessed",
 *	@OA\Property(
 *        property="dataset",
 *        ref="#/components/schemas/DataUnprocessed_data_unprocessed_dataset",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="Study",
 *    type="object",
 *	@OA\Property(
 *        property="study",
 *        ref="#/components/schemas/Study_study",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="DataList_data",
 *	@OA\Property(
 *        property="dataset",
 *        type="array",
 *		  @OA\Items(
 *			ref="#/components/schemas/DataList_data_dataset"
 *		  )
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="UserList_users_user",
 *	@OA\Property(
 *        property="username",
 *        type="string",
 *        description="The full user name",
 *    ),
 *	@OA\Property(
 *        property="id",
 *        type="string",
 *        description="The user ID",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="TaskType_output",
 *	@OA\Property(
 *        property="name",
 *        type="string",
 *        description="The name of the output, e.g. predictions",
 *    ),
 *	@OA\Property(
 *        property="predictions",
 *        ref="#/components/schemas/TaskType_predictions",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="RunList_runs_run",
 *	@OA\Property(
 *        property="task_id",
 *        type="string",
 *        description="The ID of the task solved by this run",
 *    ),
 *	@OA\Property(
 *        property="run_id",
 *        type="string",
 *        description="The run ID",
 *    ),
 *	@OA\Property(
 *        property="error_message",
 *        type="string",
 *        description="Error message generated by the run (if any)",
 *    ),
 *	@OA\Property(
 *        property="setup_id",
 *        type="string",
 *        description="Ignore (internal representation of the parameter setting)",
 *    ),
 *	@OA\Property(
 *        property="uploader",
 *        type="string",
 *        description="The ID of the person uploading this run",
 *    ),
 *	@OA\Property(
 *        property="flow_id",
 *        type="string",
 *        description="The ID of the flow used in this run",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="DataFeatures_data_features",
 *	@OA\Property(
 *        property="feature",
 *        type="array",
 *		  @OA\Items(
 *			ref="#/components/schemas/DataFeatures_data_features_feature"
 *		  )
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="DataQualities_data_qualities_quality",
 *	@OA\Property(
 *        property="name",
 *        type="string",
 *        description="The name of the dataset quality measures",
 *    ),
 *	@OA\Property(
 *        property="value",
 *        type="string",
 *        description="The value for this dataset",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="inline_response_200_12_flow_tag",
 *	@OA\Property(
 *        property="id",
 *        type="string",
 *        description="ID of the tagged flow",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="inline_response_200_8_flow_delete",
 *	@OA\Property(
 *        property="id",
 *        type="string",
 *        description="ID of the deleted flow, a positive integer",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="DataQualities_data_qualities",
 *	@OA\Property(
 *        property="quality",
 *        type="array",
 *		  @OA\Items(
 *			ref="#/components/schemas/DataQualities_data_qualities_quality"
 *		  )
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="EvaluationRequest",
 *    type="object",
 *	@OA\Property(
 *        property="evaluation_request",
 *        ref="#/components/schemas/EvaluationRequest_evaluation_request",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="inline_response_200_18_upload_flow",
 *	@OA\Property(
 *        property="id",
 *        type="string",
 *        description="ID of the uploaded run, a positive integer",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="SetupList",
 *    type="object",
 *	@OA\Property(
 *        property="setups",
 *        ref="#/components/schemas/SetupList_setups",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="TaskList",
 *    type="object",
 *	@OA\Property(
 *        property="task",
 *        ref="#/components/schemas/TaskList_task",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="inline_response_200_4_task_delete",
 *	@OA\Property(
 *        property="id",
 *        type="string",
 *        description="ID of the deleted task, a positive integer",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="Task_task_description_data_set",
 *	@OA\Property(
 *        property="data_set_id",
 *        type="string",
 *        description="The id of the dataset",
 *    ),
 *	@OA\Property(
 *        property="target_feature",
 *        type="string",
 *        description="The name of the target feature for this task",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="EvaluationList",
 *    type="object",
 *	@OA\Property(
 *        property="evaluations",
 *        ref="#/components/schemas/EvaluationList_evaluations",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="Run_run_description",
 *	@OA\Property(
 *        property="setup_string",
 *        type="string",
 *        description="Configuration of the flow as a string, to be interpreted by the flow, its library, or command line interface.",
 *    ),
 *	@OA\Property(
 *        property="task_type",
 *        type="string",
 *        description="The type of task solved by this run (e.g., classification)",
 *    ),
 *	@OA\Property(
 *        property="task_id",
 *        type="string",
 *        description="The id of the task solved by this run",
 *    ),
 *	@OA\Property(
 *        property="task_evaluation_measure",
 *        type="string",
 *        description="The evaluation measure that is supposed to be optimized in the task, if any",
 *    ),
 *	@OA\Property(
 *        property="uploader_name",
 *        type="string",
 *        description="The name of the uploader of the run",
 *    ),
 *	@OA\Property(
 *        property="input_data",
 *        ref="#/components/schemas/Run_run_description_input_data",
 *    ),
 *	@OA\Property(
 *        property="tag",
 *        type="array",
 *		  @OA\Items(
 *			type="string"
 *		  ),
 *        description="Tags added by OpenML users. Includes study tags in the form `study_1`",
 *    ),
 *	@OA\Property(
 *        property="output_data",
 *        ref="#/components/schemas/Run_run_description_output_data",
 *    ),
 *	@OA\Property(
 *        property="uploader",
 *        type="string",
 *        description="The uploader of the run",
 *    ),
 *	@OA\Property(
 *        property="flow_id",
 *        type="string",
 *        description="The id of the flow used in this run",
 *    ),
 *	@OA\Property(
 *        property="flow_name",
 *        type="string",
 *        description="The name of the flow used in this run",
 *    ),
 *	@OA\Property(
 *        property="id",
 *        type="string",
 *        description="ID of the run, a positive integer",
 *    ),
 *	@OA\Property(
 *        property="parameter_setting",
 *        type="array",
 *		  @OA\Items(
 *			ref="#/components/schemas/Run_run_description_parameter_setting"
 *		  )
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="FlowList",
 *    type="object",
 *	@OA\Property(
 *        property="flows",
 *        ref="#/components/schemas/FlowList_flows",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="inline_response_200_19_run_tag",
 *	@OA\Property(
 *        property="id",
 *        type="string",
 *        description="ID of the tagged run",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="Study_study_data",
 *	@OA\Property(
 *        property="data_id",
 *        type="array",
 *		  @OA\Items(
 *			type="string"
 *		  )
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="inline_response_200_data_delete",
 *	@OA\Property(
 *        property="id",
 *        type="string",
 *        description="ID of the deleted dataset, a positive integer",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="RunTrace_trace",
 *	@OA\Property(
 *        property="trace_iteration",
 *        type="array",
 *		  @OA\Items(
 *			ref="#/components/schemas/RunTrace_trace_trace_iteration"
 *		  )
 *    ),
 *	@OA\Property(
 *        property="run_id",
 *        type="string",
 *        description="run ID",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="inline_response_200_7_task_untag",
 *	@OA\Property(
 *        property="id",
 *        type="string",
 *        description="ID of the untagged task",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="Data",
 *    type="object",
 *	@OA\Property(
 *        property="data_set_description",
 *        ref="#/components/schemas/Data_data_set_description",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="Task_task_description_evaluation_measures",
 *	@OA\Property(
 *        property="evaluation_measure",
 *        type="string",
 *        description="The evaluation measure to optimize in this task",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="SetupList_setups_parameter",
 *	@OA\Property(
 *        property="default_value",
 *        type="string",
 *        description="The parameter's default value",
 *    ),
 *	@OA\Property(
 *        property="data_type",
 *        type="string",
 *        description="The parameter's data type",
 *    ),
 *	@OA\Property(
 *        property="value",
 *        type="string",
 *        description="The parameter value in this setup",
 *    ),
 *	@OA\Property(
 *        property="parameter_name",
 *        type="string",
 *        description="The parameter's short name",
 *    ),
 *	@OA\Property(
 *        property="full_name",
 *        type="string",
 *        description="The parameter's full name",
 *    ),
 *	@OA\Property(
 *        property="flow_id",
 *        type="string",
 *        description="The (sub)flow ID",
 *    ),
 *	@OA\Property(
 *        property="flow_name",
 *        type="string",
 *        description="The (sub)flow name",
 *    ),
 *	@OA\Property(
 *        property="id",
 *        type="string",
 *        description="The parameter ID",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="TaskList_task_input",
 *	@OA\Property(
 *        property="name",
 *        type="string",
 *        description="The name of the input",
 *    ),
 *	@OA\Property(
 *        property="value",
 *        type="string",
 *        description="The value of the input",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="StudyList_study_list",
 *	@OA\Property(
 *        property="study",
 *        type="array",
 *		  @OA\Items(
 *			ref="#/components/schemas/StudyList_study_list_study"
 *		  )
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="Study_study_tag",
 *	@OA\Property(
 *        property="write_access",
 *        type="string",
 *        description="The write access level of the study (e.g. public)",
 *    ),
 *	@OA\Property(
 *        property="name",
 *        type="string",
 *        description="The name of the study (e.g. study_1)",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="Flow",
 *    type="object",
 *	@OA\Property(
 *        property="flow_description",
 *        ref="#/components/schemas/Flow_flow_description",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="Run_run_description_parameter_setting",
 *	@OA\Property(
 *        property="name",
 *        type="string",
 *        description="The name of the parameter",
 *    ),
 *	@OA\Property(
 *        property="value",
 *        type="string",
 *        description="The value of the parameter used",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="EvaluationRequest_evaluation_request_run",
 *	@OA\Property(
 *        property="setup_id",
 *        type="string",
 *        description="ID of the setup, a positive integer",
 *    ),
 *	@OA\Property(
 *        property="upload_time",
 *        type="string",
 *        description="The datetime that the dataset was uploaded, format yyyy-MM-dd HH:mm:ss",
 *    ),
 *	@OA\Property(
 *        property="uploader",
 *        type="string",
 *        description="ID of the uploader, a positive integer",
 *    ),
 *	@OA\Property(
 *        property="task_id",
 *        type="string",
 *        description="ID of the task, a positive integer",
 *    ),
 *	@OA\Property(
 *        property="run_id",
 *        type="string",
 *        description="ID of the run, a positive integer",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="Error",
 *    type="object",
 *	@OA\Property(
 *        property="message",
 *        type="string",
 *    ),
 *	@OA\Property(
 *        property="code",
 *        type="integer",
 *    ),
 *	@OA\Property(
 *        property="additional_message",
 *        type="string",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="EstimationProcedureList_estimationprocedures_estimationprocedure",
 *	@OA\Property(
 *        property="name",
 *        type="string",
 *        description="The estimation procedure name, e.g. '10 fold Crossvalidation'",
 *    ),
 *	@OA\Property(
 *        property="folds",
 *        type="string",
 *        description="The number of cross-validation folds, e.g. '10'",
 *    ),
 *	@OA\Property(
 *        property="stratified_sampling",
 *        type="string",
 *        description="Whether or not the sampling is stratified, 'true' or 'false'",
 *    ),
 *	@OA\Property(
 *        property="ttid",
 *        type="string",
 *        description="The task type ID",
 *    ),
 *	@OA\Property(
 *        property="repeats",
 *        type="string",
 *        description="The number of repeats, e.g. '10'",
 *    ),
 *	@OA\Property(
 *        property="type",
 *        type="string",
 *        description="The estimation procedure type, e.g. 'crossvalidation'",
 *    ),
 *	@OA\Property(
 *        property="id",
 *        type="string",
 *        description="The estimation procedure ID",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="TaskTypeList_task_types",
 *	@OA\Property(
 *        property="task_type",
 *        type="array",
 *		  @OA\Items(
 *			ref="#/components/schemas/TaskTypeList_task_types_task_type"
 *		  )
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="TaskList_task_task",
 *	@OA\Property(
 *        property="status",
 *        type="string",
 *        description="The status of the source dataset, active, in_preparation, or deactivated",
 *    ),
 *	@OA\Property(
 *        property="task_type",
 *        type="string",
 *        description="The type of task (e.g. Supervised Classificationr)",
 *    ),
 *	@OA\Property(
 *        property="name",
 *        type="string",
 *        description="The name of the source dataset",
 *    ),
 *	@OA\Property(
 *        property="task_id",
 *        type="string",
 *        description="The ID of the task",
 *    ),
 *	@OA\Property(
 *        property="format",
 *        type="string",
 *        description="The format of the source dataset",
 *    ),
 *	@OA\Property(
 *        property="did",
 *        type="string",
 *        description="The id of the source dataset",
 *    ),
 *	@OA\Property(
 *        property="tag",
 *        type="array",
 *		  @OA\Items(
 *			type="string"
 *		  )
 *    ),
 *	@OA\Property(
 *        property="input",
 *        type="array",
 *		  @OA\Items(
 *			ref="#/components/schemas/TaskList_task_input"
 *		  )
 *    ),
 *	@OA\Property(
 *        property="quality",
 *        type="array",
 *		  @OA\Items(
 *			ref="#/components/schemas/TaskList_task_quality"
 *		  )
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="inline_response_200_20_run_untag",
 *	@OA\Property(
 *        property="id",
 *        type="string",
 *        description="ID of the untagged run",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="StudyList",
 *    type="object",
 *	@OA\Property(
 *        property="study_list",
 *        ref="#/components/schemas/StudyList_study_list",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="Study_study",
 *	@OA\Property(
 *        property="runs",
 *        ref="#/components/schemas/Study_study_runs",
 *    ),
 *	@OA\Property(
 *        property="tasks",
 *        ref="#/components/schemas/Study_study_tasks",
 *    ),
 *	@OA\Property(
 *        property="name",
 *        type="string",
 *        description="The name of the study",
 *    ),
 *	@OA\Property(
 *        property="creator",
 *        type="string",
 *        description="A comma-separated list of the study creators",
 *    ),
 *	@OA\Property(
 *        property="flows",
 *        ref="#/components/schemas/Study_study_flows",
 *    ),
 *	@OA\Property(
 *        property="creation_date",
 *        type="string",
 *        description="The datetime that the dataset was uploaded, format yyyy-MM-dd HH:mm:ss",
 *    ),
 *	@OA\Property(
 *        property="alias",
 *        type="string",
 *        description="The alias of the study",
 *    ),
 *	@OA\Property(
 *        property="tag",
 *        ref="#/components/schemas/Study_study_tag",
 *    ),
 *	@OA\Property(
 *        property="main_entity_type",
 *        type="string",
 *        description="The type of entity collected in the study (e.g. task or run)",
 *    ),
 *	@OA\Property(
 *        property="data",
 *        ref="#/components/schemas/Study_study_data",
 *    ),
 *	@OA\Property(
 *        property="id",
 *        type="string",
 *        description="The ID of the study",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="inline_response_200_25_upload_study",
 *	@OA\Property(
 *        property="id",
 *        type="string",
 *        description="ID of the uploaded study, a positive integer",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="EvaluationList_evaluations_evaluation",
 *	@OA\Property(
 *        property="function",
 *        type="string",
 *        description="The name of the evaluation function",
 *    ),
 *	@OA\Property(
 *        property="task_id",
 *        type="string",
 *        description="The ID of the tasks solved by this run",
 *    ),
 *	@OA\Property(
 *        property="run_id",
 *        type="string",
 *        description="The run ID",
 *    ),
 *	@OA\Property(
 *        property="array_data",
 *        type="string",
 *        description="For structured evaluation measures, an array of evaluation values (e.g. per-class predictions, evaluation matrices,...)",
 *    ),
 *	@OA\Property(
 *        property="value",
 *        type="string",
 *        description="The outcome of the evaluation",
 *    ),
 *	@OA\Property(
 *        property="flow_id",
 *        type="string",
 *        description="The ID of the flow used by this run",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="EstimationProcedureList",
 *    type="object",
 *	@OA\Property(
 *        property="estimationprocedures",
 *        ref="#/components/schemas/EstimationProcedureList_estimationprocedures",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="DataQualityList_data_qualities_list",
 *	@OA\Property(
 *        property="quality",
 *        type="array",
 *		  @OA\Items(
 *			type="string"
 *		  )
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="Study_study_flows",
 *	@OA\Property(
 *        property="flow_id",
 *        type="array",
 *		  @OA\Items(
 *			type="string"
 *		  )
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="StudyList_study_list_study",
 *	@OA\Property(
 *        property="alias",
 *        type="string",
 *        description="The alias of the study",
 *    ),
 *	@OA\Property(
 *        property="creation_date",
 *        type="string",
 *        description="The datetime that the dataset was uploaded, format yyyy-MM-dd HH:mm:ss",
 *    ),
 *	@OA\Property(
 *        property="creator",
 *        type="string",
 *        description="A comma-separated list of the study creators",
 *    ),
 *	@OA\Property(
 *        property="id",
 *        type="string",
 *        description="The ID of the study",
 *    ),
 *	@OA\Property(
 *        property="name",
 *        type="string",
 *        description="The name of the study",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="Run_run_description_input_data",
 *	@OA\Property(
 *        property="dataset",
 *        ref="#/components/schemas/Run_run_description_input_data_dataset",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="TaskTypeList_task_types_task_type",
 *	@OA\Property(
 *        property="description",
 *        type="string",
 *        description="A description of the task type",
 *    ),
 *	@OA\Property(
 *        property="creator",
 *        type="string",
 *        description="A comma-separated list of the task type creators",
 *    ),
 *	@OA\Property(
 *        property="id",
 *        type="string",
 *        description="The ID of the task type",
 *    ),
 *	@OA\Property(
 *        property="name",
 *        type="string",
 *        description="The name of the task type",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="EvaluationMeasureList_evaluation_measures",
 *	@OA\Property(
 *        property="measures",
 *        ref="#/components/schemas/EvaluationMeasureList_evaluation_measures_measures",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="TaskType_predictions",
 *	@OA\Property(
 *        property="feature",
 *        type="array",
 *		  @OA\Items(
 *			ref="#/components/schemas/Task_task_description_predictions_feature"
 *		  )
 *    ),
 *	@OA\Property(
 *        property="format",
 *        type="string",
 *        description="The format of the predictions, e.g. ARFF",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="Flow_flow_description_parameter",
 *	@OA\Property(
 *        property="default_value",
 *        type="string",
 *        description="The default value of the parameter",
 *    ),
 *	@OA\Property(
 *        property="name",
 *        type="string",
 *        description="The name of the parameter",
 *    ),
 *	@OA\Property(
 *        property="data_type",
 *        type="string",
 *        description="The data type of the parameter",
 *    ),
 *	@OA\Property(
 *        property="description",
 *        type="string",
 *        description="A description of the parameter",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="RunTrace_trace_trace_iteration",
 *	@OA\Property(
 *        property="setup_string",
 *        type="string",
 *        description="A JSON representation of the setup (configuration)",
 *    ),
 *	@OA\Property(
 *        property="repeat",
 *        type="string",
 *        description="The number of the repeat in the outer cross-valudation",
 *    ),
 *	@OA\Property(
 *        property="selected",
 *        type="string",
 *        description="Whether this setup was selected as the best one (true or false)",
 *    ),
 *	@OA\Property(
 *        property="iteration",
 *        type="string",
 *        description="A number of the optimization iteration",
 *    ),
 *	@OA\Property(
 *        property="fold",
 *        type="string",
 *        description="The number of the fold in the inner cross-validation",
 *    ),
 *	@OA\Property(
 *        property="evaluation",
 *        type="string",
 *        description="The evaluation score of the setup",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="EvaluationMeasureList",
 *    type="object",
 *	@OA\Property(
 *        property="evaluation_measures",
 *        ref="#/components/schemas/EvaluationMeasureList_evaluation_measures",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="SetupList_setups",
 *	@OA\Property(
 *        property="setup",
 *        type="array",
 *		  @OA\Items(
 *			ref="#/components/schemas/SetupList_setups_setup"
 *		  )
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="TaskList_task",
 *	@OA\Property(
 *        property="task",
 *        type="array",
 *		  @OA\Items(
 *			ref="#/components/schemas/TaskList_task_task"
 *		  )
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="SetupList_setups_setup",
 *	@OA\Property(
 *        property="setup_id",
 *        type="string",
 *        description="The setup ID",
 *    ),
 *	@OA\Property(
 *        property="parameter",
 *        type="array",
 *		  @OA\Items(
 *			ref="#/components/schemas/SetupList_setups_parameter"
 *		  )
 *    ),
 *	@OA\Property(
 *        property="flow_id",
 *        type="string",
 *        description="The ID of the flow used by this run",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="Task_task_description_estimation_procedure_parameter",
 *	@OA\Property(
 *        property="name",
 *        type="string",
 *        description="The name of the parameter",
 *    ),
 *	@OA\Property(
 *        property="value",
 *        type="string",
 *        description="The value of the parameter",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="RunList_runs",
 *	@OA\Property(
 *        property="run",
 *        type="array",
 *		  @OA\Items(
 *			ref="#/components/schemas/RunList_runs_run"
 *		  )
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="Study_study_tasks",
 *	@OA\Property(
 *        property="task_id",
 *        type="array",
 *		  @OA\Items(
 *			type="string"
 *		  )
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="inline_response_200_1_upload_data_set",
 *	@OA\Property(
 *        property="id",
 *        type="string",
 *        description="ID of the uploaded dataset, a positive integer",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="Setup_setup_parameters_parameter_setting",
 *	@OA\Property(
 *        property="default_value",
 *        type="string",
 *        description="The default value of the parameter used",
 *    ),
 *	@OA\Property(
 *        property="value",
 *        type="string",
 *        description="The value of the parameter used",
 *    ),
 *	@OA\Property(
 *        property="data_type",
 *        type="string",
 *        description="The data type of the hyperparameter value",
 *    ),
 *	@OA\Property(
 *        property="full_name",
 *        type="string",
 *        description="The full name of the hyperparameter",
 *    ),
 *	@OA\Property(
 *        property="parameter_name",
 *        type="string",
 *        description="The short name of the hyperparameter",
 *    ),
 *)
 */
/**
 * @OA\Schema(
 *    schema="DataQualities",
 *    type="object",
 *	@OA\Property(
 *        property="data_qualities",
 *        ref="#/components/schemas/DataQualities_data_qualities",
 *    ),
 *)
 */

?>
