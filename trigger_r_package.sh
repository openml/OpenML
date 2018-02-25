#!/bin/sh

msg=$(git log --format=%B -n 1)

body='{
"request": {
"message": "Triggered by OpenML commit: '"$msg"'",
"branch":"master"
}}'

curl -s -X POST \
-H "Content-Type: application/json" \
-H "Accept: application/json" \
-H "Travis-API-Version: 3" \
-H "Authorization: token ZYffIAar6Mzry1O45YxhBQ" \
-d "$body" \
https://api.travis-ci.org/repo/openml%2Fopenml-r/requests
