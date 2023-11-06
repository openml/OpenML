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

namespace Elastic\Transport\NodePool;

use Elastic\Transport\NodePool\Resurrect\ResurrectInterface;
use Elastic\Transport\NodePool\Selector\SelectorInterface;
use Elastic\Transport\Exception\NoNodeAvailableException;

use function count;
use function shuffle;
use function sprintf;

class SimpleNodePool implements NodePoolInterface
{
    /**
     * @var array
     */
    protected $nodes = [];
    
    /**
     * @var SelectorInterface
     */
    protected $selector;

    /**
     * @var ResurrectInterface
     */
    protected $resurrect;

    public function __construct(SelectorInterface $selector, ResurrectInterface $resurrect)
    {   
        $this->selector = $selector;
        $this->resurrect = $resurrect;
    }

    public function setHosts(array $hosts): self
    {
        $this->nodes = [];
        foreach ($hosts as $host) {
            $this->nodes[] = new Node($host);
        }
        shuffle($this->nodes); // randomize for use different hosts on each execution
        $this->selector->setNodes($this->nodes);
        
        return $this;
    }

    public function nextNode(): Node
    {
        $totNodes = count($this->nodes);
        $dead = 0;

        while ($dead < $totNodes) {
            $next = $this->selector->nextNode();
            if ($next->isAlive()) {
                return $next;
            }
            if ($this->resurrect->ping($next)) {
                $next->markAlive(true);
                return $next;
            }
            $dead++;
        }

        throw new NoNodeAvailableException(sprintf(
            'No alive nodes. All the %d nodes seem to be down.',
            $totNodes
        ));
    }
}