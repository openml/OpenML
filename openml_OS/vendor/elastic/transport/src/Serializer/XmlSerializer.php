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

use Elastic\Transport\Exception\InvalidXmlException;
use SimpleXMLElement;

use function libxml_clear_errors;
use function libxml_get_errors;
use function serialize;
use function simplexml_load_string;
use function sprintf;

class XmlSerializer implements SerializerInterface
{
    public static function serialize($data, array $options = []): string
    {
        if ($data instanceof SimpleXMLElement) {
            $xml = $data->asXML();
            return false === $xml ? '' : $xml;
        }
        throw new InvalidXmlException(sprintf(
            "Not a valid SimpleXMLElement: %s", 
            serialize($data)
        ));
    }

    /**
     * @return SimpleXMLElement
     */
    public static function unserialize(string $data, array $options = []): SimpleXMLElement
    {
        $result = simplexml_load_string($data);
        if (false === $result) {
            $errors = libxml_get_errors();
            libxml_clear_errors();
            throw new InvalidXmlException(sprintf(
                "Not a valid XML: %s", 
                serialize($errors)
            ));
        }
        return $result;
    }
}