<?php
namespace GuzzleHttp\Tests\Stream;

use BadMethodCallException;
use GuzzleHttp\Stream\Stream;
use GuzzleHttp\Stream\FnStream;
use PHPUnit\Framework\TestCase;

/**
 * @covers GuzzleHttp\Stream\FnStream
 */
class FnStreamTest extends TestCase
{
    public function testThrowsWhenNotImplemented()
    {
        $this->expectException(BadMethodCallException::class);
        $this->expectExceptionMessage('seek() is not implemented in the FnStream');
        (new FnStream([]))->seek(1);
    }

    public function testProxiesToFunction()
    {
        $s = new FnStream([
            'read' => function ($len) {
                $this->assertEquals(3, $len);
                return 'foo';
            },
        ]);

        $this->assertEquals('foo', $s->read(3));
    }

    public function testCanCloseOnDestruct()
    {
        $called = false;
        $s = new FnStream([
            'close' => function () use (&$called) {
                $called = true;
            },
        ]);
        unset($s);
        $this->assertTrue($called);
    }

    /**
     * @doesNotPerformAssertions
     */
    public function testDoesNotRequireClose()
    {
        $s = new FnStream([]);
        unset($s);
    }

    public function testDecoratesStream()
    {
        $a = Stream::factory('foo');
        $b = FnStream::decorate($a, []);
        $this->assertEquals(3, $b->getSize());
        $this->assertEquals($b->isWritable(), true);
        $this->assertEquals($b->isReadable(), true);
        $this->assertEquals($b->isSeekable(), true);
        $this->assertEquals($b->read(3), 'foo');
        $this->assertEquals($b->tell(), 3);
        $this->assertEquals($a->tell(), 3);
        $this->assertEmpty($b->read(1));
        $this->assertEquals($b->eof(), true);
        $this->assertEquals($a->eof(), true);
        $b->seek(0);
        $this->assertEquals('foo', (string) $b);
        $b->seek(0);
        $this->assertEquals('foo', $b->getContents());
        $this->assertEquals($a->getMetadata(), $b->getMetadata());
        $b->seek(0, SEEK_END);
        $b->write('bar');
        $this->assertEquals('foobar', (string) $b);
        $this->assertIsResource($b->detach());
        $b->close();
    }

    public function testDecoratesWithCustomizations()
    {
        $called = false;
        $a = Stream::factory('foo');
        $b = FnStream::decorate($a, [
            'read' => function ($len) use (&$called, $a) {
                $called = true;
                return $a->read($len);
            }
        ]);
        $this->assertEquals('foo', $b->read(3));
        $this->assertTrue($called);
    }
}
