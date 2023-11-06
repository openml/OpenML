<?php
/**
 * Elastic Transport
 *
 * @link      https://github.com/elastic/elastic-transport-php
 * @copyright Copyright (c) Elasticsearch B.V (https://www.elastic.co)
 * @license   https://opensource.org/licenses/MIT MIT License
 *
 * Licensed to Elasticsearch B.V under one or more agreements.
 * Elasticsearch B.V licenses this file to you under the MIT License.
 * See the LICENSE file in the project root for more information.
 */
declare(strict_types=1);

namespace Elastic\Transport\NodePool\Selector;

use Elastic\Transport\NodePool\Node;
use Elastic\Transport\Exception\NoNodeAvailableException;

class RoundRobin implements SelectorInterface
{
    use SelectorTrait;

    public function nextNode(): Node
    {
        if (empty($this->getNodes())) {
            $className = substr(__CLASS__, strrpos(__CLASS__, '\\') + 1);
            throw new NoNodeAvailableException(sprintf(
                "No node available. Please use %s::setNodes() before calling %s::nextNode().",
                $className,
                $className
            ));
        }
        $node = current($this->nodes);
        if (false === next($this->nodes)) {
            reset($this->nodes);
        }
        return $node;
    }   
}