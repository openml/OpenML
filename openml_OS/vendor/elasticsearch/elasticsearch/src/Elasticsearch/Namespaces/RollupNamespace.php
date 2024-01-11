<?php
/**
 * Elasticsearch PHP client
 *
 * @link      https://github.com/elastic/elasticsearch-php/
 * @copyright Copyright (c) Elasticsearch B.V (https://www.elastic.co)
 * @license   http://www.apache.org/licenses/LICENSE-2.0 Apache License, Version 2.0
 * @license   https://www.gnu.org/licenses/lgpl-2.1.html GNU Lesser General Public License, Version 2.1 
 * 
 * Licensed to Elasticsearch B.V under one or more agreements.
 * Elasticsearch B.V licenses this file to you under the Apache 2.0 License or
 * the GNU Lesser General Public License, Version 2.1, at your option.
 * See the LICENSE file in the project root for more information.
 */
declare(strict_types = 1);

namespace Elasticsearch\Namespaces;

use Elasticsearch\Namespaces\AbstractNamespace;

/**
 * Class RollupNamespace
 *
 * NOTE: this file is autogenerated using util/GenerateEndpoints.php
 * and Elasticsearch 6.8.16 (1f62092)
 */
class RollupNamespace extends AbstractNamespace
{

    /**
     * $params['id'] = (string) The ID of the job to delete (Required)
     *
     * @param array $params Associative array of parameters
     * @return array
     * @see 
     */
    public function deleteJob(array $params = [])
    {
        $id = $this->extractArgument($params, 'id');

        $endpointBuilder = $this->endpoints;
        $endpoint = $endpointBuilder('Rollup\DeleteJob');
        $endpoint->setParams($params);
        $endpoint->setId($id);

        return $this->performRequest($endpoint);
    }
    /**
     * $params['id'] = (string) The ID of the job(s) to fetch. Accepts glob patterns, or left blank for all jobs
     *
     * @param array $params Associative array of parameters
     * @return array
     * @see 
     */
    public function getJobs(array $params = [])
    {
        $id = $this->extractArgument($params, 'id');

        $endpointBuilder = $this->endpoints;
        $endpoint = $endpointBuilder('Rollup\GetJobs');
        $endpoint->setParams($params);
        $endpoint->setId($id);

        return $this->performRequest($endpoint);
    }
    /**
     * $params['id'] = (string) The ID of the index to check rollup capabilities on, or left blank for all jobs
     *
     * @param array $params Associative array of parameters
     * @return array
     * @see 
     */
    public function getRollupCaps(array $params = [])
    {
        $id = $this->extractArgument($params, 'id');

        $endpointBuilder = $this->endpoints;
        $endpoint = $endpointBuilder('Rollup\GetRollupCaps');
        $endpoint->setParams($params);
        $endpoint->setId($id);

        return $this->performRequest($endpoint);
    }
    /**
     * $params['index'] = (string) The rollup index or index pattern to obtain rollup capabilities from. (Required)
     *
     * @param array $params Associative array of parameters
     * @return array
     * @see 
     */
    public function getRollupIndexCaps(array $params = [])
    {
        $index = $this->extractArgument($params, 'index');

        $endpointBuilder = $this->endpoints;
        $endpoint = $endpointBuilder('Rollup\GetRollupIndexCaps');
        $endpoint->setParams($params);
        $endpoint->setIndex($index);

        return $this->performRequest($endpoint);
    }
    /**
     * $params['id']   = (string) The ID of the job to create (Required)
     * $params['body'] = (array) The job configuration (Required)
     *
     * @param array $params Associative array of parameters
     * @return array
     * @see 
     */
    public function putJob(array $params = [])
    {
        $id = $this->extractArgument($params, 'id');
        $body = $this->extractArgument($params, 'body');

        $endpointBuilder = $this->endpoints;
        $endpoint = $endpointBuilder('Rollup\PutJob');
        $endpoint->setParams($params);
        $endpoint->setId($id);
        $endpoint->setBody($body);

        return $this->performRequest($endpoint);
    }
    /**
     * $params['index']      = (string) The index or index-pattern (containing rollup or regular data) that should be searched (Required)
     * $params['type']       = (string) The doc type inside the index
     * $params['typed_keys'] = (boolean) Specify whether aggregation and suggester names should be prefixed by their respective types in the response
     * $params['body']       = (array) The search request body (Required)
     *
     * @param array $params Associative array of parameters
     * @return array
     * @see 
     */
    public function rollupSearch(array $params = [])
    {
        $index = $this->extractArgument($params, 'index');
        $type = $this->extractArgument($params, 'type');
        $body = $this->extractArgument($params, 'body');

        $endpointBuilder = $this->endpoints;
        $endpoint = $endpointBuilder('Rollup\RollupSearch');
        $endpoint->setParams($params);
        $endpoint->setIndex($index);
        $endpoint->setType($type);
        $endpoint->setBody($body);

        return $this->performRequest($endpoint);
    }
    /**
     * $params['id'] = (string) The ID of the job to start (Required)
     *
     * @param array $params Associative array of parameters
     * @return array
     * @see 
     */
    public function startJob(array $params = [])
    {
        $id = $this->extractArgument($params, 'id');

        $endpointBuilder = $this->endpoints;
        $endpoint = $endpointBuilder('Rollup\StartJob');
        $endpoint->setParams($params);
        $endpoint->setId($id);

        return $this->performRequest($endpoint);
    }
    /**
     * $params['id']                  = (string) The ID of the job to stop (Required)
     * $params['wait_for_completion'] = (boolean) True if the API should block until the job has fully stopped, false if should be executed async. Defaults to false.
     * $params['timeout']             = (time) Block for (at maximum) the specified duration while waiting for the job to stop.  Defaults to 30s.
     *
     * @param array $params Associative array of parameters
     * @return array
     * @see 
     */
    public function stopJob(array $params = [])
    {
        $id = $this->extractArgument($params, 'id');

        $endpointBuilder = $this->endpoints;
        $endpoint = $endpointBuilder('Rollup\StopJob');
        $endpoint->setParams($params);
        $endpoint->setId($id);

        return $this->performRequest($endpoint);
    }
}
