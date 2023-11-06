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

namespace Elastic\Transport;

use Elastic\Transport\NodePool\NodePoolInterface;
use Elastic\Transport\NodePool\SimpleNodePool;
use Elastic\Transport\Exception;
use Elastic\Transport\NodePool\Resurrect\NoResurrect;
use Elastic\Transport\NodePool\Selector\RoundRobin;
use Http\Discovery\Psr18ClientDiscovery;
use Psr\Http\Client\ClientInterface;
use Psr\Log\LoggerInterface;
use Psr\Log\NullLogger;
use Throwable;

class TransportBuilder
{
    protected ClientInterface $client;
    protected NodePoolInterface $nodePool;
    protected LoggerInterface $logger;
    protected array $hosts = [];

    final public function __construct()
    {
    }

    public static function create(): TransportBuilder
    {
        return new static();
    }

    public function setClient(ClientInterface $client): self
    {
        $this->client = $client;
        return $this;
    }

    public function getClient(): ClientInterface
    {
        if (empty($this->client)) {
            $this->client = Psr18ClientDiscovery::find();
        }
        return $this->client;
    }

    public function setNodePool(NodePoolInterface $nodePool): self
    {
        $this->nodePool = $nodePool;
        return $this;
    }

    public function getNodePool(): NodePoolInterface
    {
        if (empty($this->nodePool)) {
            $this->nodePool = new SimpleNodePool(
                new RoundRobin(),
                new NoResurrect()
            );
        }
        return $this->nodePool;
    }

    public function setLogger(LoggerInterface $logger): self
    {
        $this->logger = $logger;
        return $this;
    }

    public function getLogger(): LoggerInterface
    {
        if (empty($this->logger)) {
            $this->logger = new NullLogger();
        }
        return $this->logger;
    }

    public function setHosts(array $hosts): self
    {
        $this->hosts = $hosts;
        return $this;
    }

    public function getHosts(): array
    {
        return $this->hosts;
    }

    public function setCloudId(string $cloudId): self
    {
        $this->hosts = [$this->parseElasticCloudId($cloudId)];
        return $this;
    }

    public function build(): Transport
    {
        return new Transport(
            $this->getClient(),
            $this->getNodePool()->setHosts($this->hosts),
            $this->getLogger()
        );
    }

    /**
     * Return the URL of Elastic Cloud from the Cloud ID
     */
    private function parseElasticCloudId(string $cloudId): string
    {
        try {
            list($name, $encoded) = explode(':', $cloudId);
            list($uri, $uuids)    = explode('$', base64_decode($encoded));
            list($es,)            = explode(':', $uuids);

            return sprintf("https://%s.%s", $es, $uri);
        } catch (Throwable $t) {
            throw new Exception\CloudIdParseException(
                'Cloud ID not valid'
            );
        }
    }
}