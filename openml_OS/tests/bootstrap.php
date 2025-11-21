<?php
// PHPUnit Bootstrap for OpenML Tests

// Define test environment constants
define('ENVIRONMENT', 'testing');
define('BASEPATH', realpath(__DIR__ . '/../') . '/');

// Load Composer autoloader
if (file_exists(__DIR__ . '/../vendor/autoload.php')) {
    require_once __DIR__ . '/../vendor/autoload.php';
}

// Set error reporting for tests
error_reporting(E_ALL);
ini_set('display_errors', '1');

// Load test helpers if they exist
if (file_exists(__DIR__ . '/helpers/test_helper.php')) {
    require_once __DIR__ . '/helpers/test_helper.php';
}

echo "PHPUnit Bootstrap loaded for OpenML tests\n";
