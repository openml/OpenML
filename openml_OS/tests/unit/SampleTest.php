<?php

use PHPUnit\Framework\TestCase;

/**
 * Sample test to verify PHPUnit is working
 * Replace this with actual tests for your application
 */
class SampleTest extends TestCase
{
    public function testPhpUnitIsWorking(): void
    {
        $this->assertTrue(true, 'PHPUnit is configured correctly');
    }

    public function testPhpVersion(): void
    {
        $this->assertGreaterThanOrEqual(
            '7.4.0',
            PHP_VERSION,
            'PHP version should be 7.4 or higher'
        );
    }

    public function testRequiredExtensions(): void
    {
        $requiredExtensions = ['mysqli', 'json', 'mbstring', 'xml'];
        
        foreach ($requiredExtensions as $extension) {
            $this->assertTrue(
                extension_loaded($extension),
                "Required PHP extension '{$extension}' is not loaded"
            );
        }
    }
}
