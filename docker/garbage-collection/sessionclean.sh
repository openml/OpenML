#!/bin/sh -e
# Just simply deleting code igniter's session files if they're older than $GC_MAXLIFETIME_MINUTES
# I've tried just using 
# session_start();
# session_gc();
# session_destroy();
# But that does not delete any code igniter sessions, although it should according to the documentation I could find.
# In the end, I just settled for this. If you want to make it more beautiful, note that code igniter config can be partly found in
# openml_OS/config/config.php (e.g., 'sess_expiration' which seems to overwrite the session.gc_maxlifetime in php.ini)...


GC_MAXLIFETIME_SECONDS=$(/usr/local/bin/php -r "echo ini_get(\"session.gc_maxlifetime\");")
GC_MAXLIFETIME_MINUTES=$((($GC_MAXLIFETIME_SECONDS + 59) / 60))  # Ceil
GC_SAVEPATH="/tmp"

find -O3 "$GC_SAVEPATH" -ignore_readdir_race -depth -mindepth 1 -name 'ci_session*' -type f -cmin "+$GC_MAXLIFETIME_MINUTES" -delete
