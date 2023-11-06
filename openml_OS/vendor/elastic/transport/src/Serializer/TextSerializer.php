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

namespace Elastic\Transport\Serializer;

use Elastic\Transport\Exception\SerializeException;

use function serialize;

class TextSerializer implements SerializerInterface
{
    /**
     * @throws SerializeException
     */
    public static function serialize($data, array $options = []): string
    {
        if (is_string($data) || is_numeric($data) || (is_object($data) && method_exists($data, '__toString'))) {
            return (string) $data;
        }
        throw new SerializeException(
            sprintf("I cannot serialize %s in a text", serialize($data))
        );
    }

    /**
     * @return string
     */
    public static function unserialize(string $data, array $options = []): string
    {
        return $data;
    }
}