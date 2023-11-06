<img align="right" width="auto" height="auto" src="https://www.elastic.co/static-res/images/elastic-logo-200.png"/>

# HTTP transport for Elastic PHP clients

[![Build status](https://github.com/elastic/elastic-transport-php/workflows/Test/badge.svg)](https://github.com/elastic/elastic-transport-php/actions)

This is a HTTP transport PHP library for communicate with [Elastic](https://www.elastic.co/)
products, like [Elasticsearch](https://github.com/elastic/elasticsearch).

It implements [PSR-7](https://www.php-fig.org/psr/psr-7/) standard for managing
HTTP messages and [PSR-18](https://www.php-fig.org/psr/psr-18/) for sending HTTP requests.
Moreover, it uses the [PSR-17](https://www.php-fig.org/psr/psr-17/) for building `PSR-7` objects
like HTTP requests, HTTP responses, URI, etc.

It uses the [HTTPlug](http://httplug.io/) library to automatic discovery a [PSR-18](https://www.php-fig.org/psr/psr-18/)
client, a [PSR-17](https://www.php-fig.org/psr/psr-17/) factory and the [HttpAsyncClient](https://github.com/php-http/httplug/blob/master/src/HttpAsyncClient.php)
interface with [Promise](https://docs.php-http.org/en/latest/components/promise.html) for
asyncronous HTTP requestes.

The architecture of the Transport is flexible and customizable, you can configure it 
using a [PSR-18](https://www.php-fig.org/psr/psr-18/) client, a [PSR-3](https://www.php-fig.org/psr/psr-3/)
logger and a custom [NodePoolInterface](src/NodePool/NodePoolInterface.php), to manage
a cluster of nodes.

## Quick start

The main component of this library is the [Transport](src/Transport.php) class. 

This class uses 3 components:

- a PSR-18 client, using [ClientInterface](https://www.php-fig.org/psr/psr-18/#interfaces);
- a Node pool, using [NodePoolInterface](src/NodePool/NodePoolInterface.php);
- a PSR-3 logger, using [LoggerInterface](https://www.php-fig.org/psr/psr-3/#3-psrlogloggerinterface).

While the `PSR-3` and `PSR-18` are well known standard in the PHP community, the `NodePoolInterface`
is a new interface proposed in this library. The idea of this interface is to provide a class
that is able to select a node for a list of hosts. For instance, using Elasticsearch, that is a 
distributed search engine, you need to manage a cluster of nodes. Each node exposes a common
HTTP API and you can send the HTTP requests to one or more nodes.
The `NodePoolInterface` is a component that can be used to manage the routing of the HTTP
requests to the cluster node topology.

In order to buid a `Transport` instance, you can use the `TransportBuilder` as follows:

```php
use Elastic\Transport\TransportBuilder;

$transport = TransportBuilder::create()
    ->setHosts(['localhost:9200'])
    ->build();
```

This example shows how to set the transport to communicate with one node located at `localhost:9200`
(e.g. Elasticsearch default port).

By default, `TransportBuilder` will use the autodiscovery feature of [HTTPlug](http://httplug.io/)
for the [PSR-18](https://www.php-fig.org/psr/psr-18/) client, the [SimpleNodePool](src/NodePool/SimpleNodePool.php)
as `NodePoolInterface` and the [NullLogger](https://github.com/php-fig/log/blob/master/Psr/Log/NullLogger.php)
as `LoggerInterface`.

The `Tranport` class itself implements the [PSR-18](https://www.php-fig.org/psr/psr-18/) and the
[HttpAsyncClient](https://github.com/php-http/httplug/blob/master/src/HttpAsyncClient.php) interfaces, 
that means you can use it to send any HTTP request using the `Tranport::sendRequest()` function
as follows:

```php
use Http\Discovery\Psr17FactoryDiscovery;

$factory = Psr17FactoryDiscovery::findRequestFactory();
$request = $factory->createRequest('GET', '/info'); // PSR-7 request
$response = $transport->sendRequest($request);
var_dump($response); // PSR-7 response
```

The `sendRequest` function will use `$request` to send the HTTP request to the `localhost:9200`
node specified in the previous example code. This behaviour can be used to specify only the URL path
in the HTTP request, the host is selected at runtime using the `NodePool` implementation.

**NOTE**: if you send a `$request` that contains already a host the `Transport` will
use it without using the `NodePool` to select a node specified in `TransportBuilder::setHosts()`
settings.

For instance, the following example will send the `/info` request to `domain` and not `localhost`.

```php
use Elastic\Transport\TransportBuilder;

$transport = TransportBuilder::create()
    ->setHosts(['localhost:9200'])
    ->build();

$request = new Request('GET', 'https://domain.com/info');
$response = $transport->sendRequest($request); // the HTTP request will be sent to domain.com

echo $transport->lastRequest()->getUri()->getHost(); // domain.com
```

## Asyncronous requests

You can send an asyncronous HTTP request using the `Transport::sendAsyncRequest()` as follows:

```php
use Http\Discovery\Psr17FactoryDiscovery;

$factory = Psr17FactoryDiscovery::findRequestFactory();
$request = $factory->createRequest('GET', '/info'); // PSR-7 request
$promise= $transport->sendAsyncRequest($request);
var_dump($promise); // Promise
var_dump($promise->wait()); // PSR-7 response
```

The `$promise` contains a [Promise](https://docs.php-http.org/en/latest/components/promise.html) object.
A promise is an object that does not block the execution of PHP. This means the promise does not
contain the HTTP response. In order to read the HTTP response you need to use the `wait()` function.

Another approach to use a promise is to specify the functions to be called on success and on faliure
of the HTTP request. This can achieved using the `then()` function as follows:

```php
$promise->then(function (ResponseInterface $response) {
    // onFulfilled callback, $reponse is PSR-7
    echo 'The response is available';

    return $response;
}, function (Exception $e) {
    // onRejected callback
    echo 'An error happens';

    throw $e;
});
```

For more information about the usage of Promise objetcs you can read the [documentation](https://docs.php-http.org/en/latest/components/promise.html)
from HTTPlug.

## Set the number of retries

You can specify the number of retries for any HTTP requests. This means if the HTTP request will fail
the client will automatically try to perform another request (or more).

By default, the number of retries is zero (0). If you want you can change it using the `Transport::setRetries()`
function, as follows:

```php
use Elastic\Transport\TransportBuilder;

$transport = TransportBuilder::create()
    ->setHosts([
        '10.0.0.10:9200',
        '10.0.0.20:9200',
        '10.0.0.30:9200'
    ])
    ->build();

$transport->setRetries(1);
$factory = Psr17FactoryDiscovery::findRequestFactory();
$request = $factory->createRequest('GET', '/info'); 
// If a node is down, the transports retry automatically using another one 
$response = $transport->sendRequest($request); 
```

This feature can be interesting as retry mechanism especially useful if you have a cluster of nodes.
You can read the following section about `Node Pool` to understand how to configure the selection
of nodes in a cluster environment.

## Node Pool

The `SimpleNodePool` is the default node pool algorithm used by `Tranposrt`.
It uses the following default values: [RoundRobin](src/NodePool/Selector/RoundRobin.php) as
`SelectorInterface` and [NoResurrect](src/NodePool/Resurrect/FalseResurrect.php) as `ResurrectInterface`.

The [Round-robin](https://en.wikipedia.org/wiki/Round-robin_scheduling) algorithm select the nodes in
order, from the first node in the array to the latest. When arrived to the latest nodes, 
it will start again from the first. 

\* **NOTE**: the order of the nodes is randomized at runtime to maximize the usage of all the hosts.

The [NoResurrect](src/NodePool/Resurrect/FalseResurrect.php) option does not try to resurrect the
node that has been marked as dead. For instance, using `Elasticsearch` you can try to
resurrect a dead node using the `HEAD /` API. If you want to use this behaviour you can use the
[ElasticsearchResurrect](/src/NodePool/Resurrect/ElasticsearchResurrect.php) class.

## Use a custom Selector

You can specify a `SelectorInterface` implementation when you create a `NodePoolInterface` instance.
For instance, imagine you implemented a `CustomSelector` and a custom `CustomResurrect` you can
use it as follows:

```php
use Elastic\Transport\NodePool\SimpleNodePool;
use Elastic\Transport\TransportBuilder;

$nodePool = new SimpleNodePool(
    new CustomSelector(),
    new CustomResurrect()
);

$transport = TransportBuilder::create()
    ->setHosts(['localhost:9200'])
    ->setNodePool($nodePool)
    ->build();
```

## Use a custom PSR-3 loggers

You can specify a PSR-3 `LoggerInterface` implementation using the `TransportBuilder`.
For instance, if you want to use [monolog](https://github.com/Seldaek/monolog) library
you can use the following configuration:

```php
use Elastic\Transport\TransportBuilder;
use Monolog\Logger;
use Monolog\Handler\StreamHandler;

$logger = new Logger('name');
$logger->pushHandler(new StreamHandler('debug.log', Logger::DEBUG));

$transport = TransportBuilder::create()
    ->setHosts(['localhost:9200'])
    ->setLogger($logger)
    ->build();
```
## Use a custom PSR-18 clients

You can specify a `PSR-18` client using the `TransportBuilder::setClient()` function.
For instance, if you want to use [Symfony HTTP Client](https://symfony.com/doc/current/http_client.html)
you can use the following configuration:

```php
use Elastic\Transport\TransportBuilder;
use Symfony\Component\HttpClient\Psr18Client;

$transport = TransportBuilder::create()
    ->setHosts(['localhost:9200'])
    ->setClient(new Psr18Client)
    ->build();
```

As mentioned in the introduction, we use the [HTTPlug](http://httplug.io/) library
to automatic discovery a [PSR-18](https://www.php-fig.org/psr/psr-18/) client.

You can use the `TransportBuilder::setClient()` to specify the client manually, for
instance if you have multiple HTTP client library installed.

By default, if the [PSR-18](https://www.php-fig.org/psr/psr-18/) client implements the
[HttpAsyncClient](https://github.com/php-http/httplug/blob/master/src/HttpAsyncClient.php)
it will use it when using `Transport::sendAsyncRequest()`. If you want you can override
this setting using the `Transport::setAsyncClient()` function. That means you can use
a [PSR-18](https://www.php-fig.org/psr/psr-18/) client for the syncronous requests and
a different [HttpAsyncClient](https://github.com/php-http/httplug/blob/master/src/HttpAsyncClient.php)
client for the asyncronous requests.

## Copyright and License

Copyright (c) [Elasticsearch B.V](https://www.elastic.co).

This software is licensed under the MIT License.
Read the [LICENSE](LICENSE) file for more information.
