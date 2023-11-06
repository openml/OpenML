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

use Elastic\Transport\Exception\InvalidArgumentException;

use function is_array;
use function is_object;
use function var_export;
use function sprintf;

class Utility
{
    /**
     * Remove null values form array or object
     * 
     * @param mixed $data
     * @return void
     */
    public static function removeNullValue(&$data): void
    {
        if (!is_object($data) && !is_array($data)) {
            throw new InvalidArgumentException(
                sprintf("The parameter %s must be an object or array", var_export($data, true))
            );
        }
        /** @phpstan-ignore-next-line */
        foreach ($data as $property => &$value) {
            if (is_object($value) || is_array($value)) {
                self::removeNullValue($value);
            }
            if (null === $value) {
                if (is_array($data)) {
                    unset($data[$property]);
                } 
                if (is_object($data)) {
                    unset($data->$property);
                }
            }
        }
    }
}