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

use Elastic\Transport\Exception\InvalidIterableException;

use function explode;
use function is_iterable;
use function sprintf;
use function str_getcsv;
use function substr;

class CsvSerializer implements SerializerInterface
{
    /**
     * @inheritdoc
     * 
     * @throws InvalidIterableException
     */
    public static function serialize($data, array $options = []): string
    {
        if (!is_iterable($data)) {
            throw new InvalidIterableException(
                sprintf("The parameter %s is not iterable", serialize($data))
            );
        }
        $result = '';
        foreach ($data as $row) {
            if (is_array($row) || is_object($row)) {
                $result .= implode(',', (array) $row);
            } else {
                $result .= (string) $row;
            }
            $result .= "\n";
        }
        return empty($result) ? $result : substr($result, 0, -1);
    }

    /**
     * @return array
     */
    public static function unserialize(string $data, array $options = []): array
    {
        $result = [];
        foreach (explode("\n", $data) as $row) {
            $result[] = str_getcsv($row);
        }
        return $result;
    }
}