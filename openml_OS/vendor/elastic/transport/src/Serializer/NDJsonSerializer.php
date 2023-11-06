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

use ArrayObject;
use Elastic\Transport\Exception\InvalidJsonException;
use JsonException;

use function explode;
use function json_decode;
use function sprintf;
use function strpos;

class NDJsonSerializer implements SerializerInterface
{
    /**
     * The available $options are: 
     * 'remove_null'  => (bool) enable/disable the removing of
     *                   null values (default is true)
     * 
     * @param array $data
     */
    public static function serialize($data, array $options = []): string
    {
        $result = '';
        foreach ($data as $row) {
            if (empty($row)) {
                $result .= "{}\n";
                continue;
            }
            $result .= JsonSerializer::serialize($row, $options) . "\n";
        }
        return $result;
    }

    /**
     * The available options are:
     * 'type' => (string) specify if the array result should contain object
     *           or array (default is array)
     * 
     * @inheritdoc
     */
    public static function unserialize(string $data, array $options = [])
    {
        $array = explode(strpos($data, "\r\n") !== false ? "\r\n" : "\n", $data);
        $result = [];
        foreach ($array as $json) {
            if (empty($json)) {
                continue;
            }
            try {
                $result[] = JsonSerializer::unserialize($json, $options);
            } catch (JsonException $e) {
                throw new InvalidJsonException(sprintf(
                    "Not a valid NDJson: %s", 
                    $e->getMessage()
                ));
            }    
        }
        $type = $options['type'] ?? 'array';
        return $type === 'array' ? $result : new ArrayObject($result);
    }
}