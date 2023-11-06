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

namespace Elastic\Transport\NodePool\Resurrect;

use Elastic\Transport\NodePool\Node;
use Psr\Http\Client\ClientInterface;

class NoResurrect implements ResurrectInterface
{
    protected ClientInterface $client;

    public function ping(Node $node): bool
    {
        return false;
    }
}