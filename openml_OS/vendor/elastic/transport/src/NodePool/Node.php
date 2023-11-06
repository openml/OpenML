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

use Http\Discovery\Psr17FactoryDiscovery;
use Psr\Http\Message\UriInterface;

use function sprintf;
use function substr;

class Node
{
    protected UriInterface $uri;
    protected bool $alive = true;

    public function __construct(string $host)
    {
        if (substr($host, 0, 5) !== 'http:' && substr($host, 0, 6) !== 'https:') {
            $host = sprintf("http://%s", $host);
        }
        $this->uri = Psr17FactoryDiscovery::findUriFactory()->createUri($host);
    }

    public function markAlive(bool $alive): void
    {
        $this->alive = $alive;
    }

    public function isAlive(): bool
    {
        return $this->alive;
    }

    public function getUri(): UriInterface
    {
        return $this->uri;
    }
}