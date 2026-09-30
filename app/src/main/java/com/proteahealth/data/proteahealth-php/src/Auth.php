<?php
declare(strict_types=1);

final class Auth
{
    /**
     * Every endpoint is admin-only. Send the token as
     *   Authorization: Bearer <token>   or   X-Admin-Token: <token>
     */
    public static function requireAdmin(): void
    {
        $expected = $GLOBALS['config']['admin_token'];
        if ($expected === '') {
            Http::message(503, 'Server is not configured (ADMIN_TOKEN missing)');
        }

        $given = $_SERVER['HTTP_X_ADMIN_TOKEN'] ?? '';
        $auth  = $_SERVER['HTTP_AUTHORIZATION'] ?? '';
        if ($given === '' && stripos($auth, 'Bearer ') === 0) {
            $given = trim(substr($auth, 7));
        }

        if ($given === '' || !hash_equals($expected, $given)) {   // timing-safe compare
            header('WWW-Authenticate: Bearer');
            Http::message(401, 'Unauthorized');
        }
    }
}
