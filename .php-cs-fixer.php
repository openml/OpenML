<?php

/**
 * PHP-CS-Fixer configuration for OpenML.
 *
 * Enforces PSR-12 coding style across the openml_OS application directory.
 * Run checks:  composer run cs:check
 * Auto-fix:    composer run cs:fix
 */

$config = new PhpCsFixer\Config();

return $config
    ->setRules([
        '@PSR12'              => true,
        '@PHP74Migration'     => true,
        'array_syntax'        => ['syntax' => 'short'],
        'no_unused_imports'   => true,
        'ordered_imports'     => ['sort_algorithm' => 'alpha'],
        'single_quote'        => true,
        'trailing_comma_in_multiline' => ['elements' => ['arrays']],
        'no_trailing_whitespace'      => true,
        'no_extra_blank_lines'        => true,
    ])
    ->setFinder(
        PhpCsFixer\Finder::create()
            ->in(__DIR__ . '/openml_OS')
            ->exclude([
                'libraries',
                'third_party',
                'vendor',
                'cache',
                'logs',
            ])
            ->name('*.php')
    )
    ->setUsingCache(true)
    ->setCacheFile(__DIR__ . '/.php-cs-fixer.cache');
