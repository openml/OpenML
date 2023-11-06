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

use Elastic\Transport\Exception\InvalidJsonException;
use Elastic\Transport\Exception\UndefinedPropertyException;
use JsonException;

use function in_array;
use function json_decode;
use function json_encode;
use function sprintf;

class JsonSerializer implements SerializerInterface
{
   /**
     * The available $options are: 
     * 'remove_null'  => (bool) enable/disable the removing of
     *                   null values (default is true)
     * 
     * @param mixed $data
     */
    public static function serialize($data, array $options = []): string
    {
        if (empty($data)) {
            return '{}';
        }
        if (is_string($data)) {
            return $data;
        }
        try {
            $removeNull = $options['remove_null'] ?? true;
            if ($removeNull) {
                Utility::removeNullValue($data);
            }
            return json_encode($data, JSON_PRESERVE_ZERO_FRACTION + JSON_INVALID_UTF8_SUBSTITUTE + JSON_THROW_ON_ERROR);
        } catch (JsonException $e) {
            throw new InvalidJsonException(sprintf(
                "I cannot serialize to Json: %s", 
                $e->getMessage()
            ));
        }
    }

    /**
     * The available options are:
     * 'type' => (string) specify if the output should be an array
     *           or an object (default is array)
     * 
     * @inheritdoc
     */
    public static function unserialize(string $data, array $options = [])
    {
        try {
            $type = $options['type'] ?? 'array';
            if (!in_array($type, ['object', 'array'])) {
                throw new UndefinedPropertyException("The unserialize 'type' option must be object or array");
            }
            return json_decode($data, $type === 'array', 512, JSON_THROW_ON_ERROR);
        } catch (JsonException $e) {
            throw new InvalidJsonException(sprintf(
                "Not a valid Json: %s", 
                $e->getMessage()
            ));
        }
    }
}