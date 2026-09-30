<?php
declare(strict_types=1);

// Secrets come from environment variables, never from source code.
return [
    'db' => [
        'host' => getenv('DB_HOST') ?: 'localhost',
        'port' => getenv('DB_PORT') ?: '3306',
        'name' => getenv('DB_NAME') ?: 'proteahealth',
        'user' => getenv('DB_USER') ?: '',
        'pass' => getenv('DB_PASS') ?: '',
    ],
    'admin_token' => getenv('ADMIN_TOKEN') ?: '',
    'debug'       => getenv('APP_DEBUG') === '1',
];
