#!/usr/bin/env bash

MM_HOST="${MM_HOST:-mattermost}"
MM_PORT="${MM_PORT:-80}"
MM_URL="http://$MM_HOST:$MM_PORT/api/v1"

echoerr() { if [[ ${QUIET} -ne 1 ]]; then echo "$@" 1>&2; fi }

main() {
    local admin_username="$1" admin_email="$2"

    echoerr "installing human and bot accounts at $MM_URL"

    local humans=$(curl -s -H "Content-Type: application/json" -d "{\"email\":\"$admin_email\",\"name\":\"humans\",\"display_name\":\"Humans\",\"type\":\"O\"}" "$MM_URL/teams/create" | jq -r .id)
    echoerr "created human team: $humans"

    local bots=$(curl -s -H "Content-Type: application/json" -d "{\"email\":\"$admin_email\",\"name\":\"bots\",\"display_name\":\"Bots\",\"type\":\"O\"}" "$MM_URL/teams/create" | jq -r .id)
    echoerr "created bots team: $bots"

    local admin_id=$(curl -s -H "Content-Type: application/json" -d "{\"email\":\"$admin_email\",\"username\":\"$admin_username\",\"password\":\"password\",\"team_id\":\"$humans\"}" "$MM_URL/users/create" | jq -r .id)
    echoerr "created human admin account: u[$admin_username] p[password] id[$admin_id]"
}

main "admin" "admin@mm4s.com"
