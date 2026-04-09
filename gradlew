#!/usr/bin/env sh
set -e

if command -v gradle >/dev/null 2>&1; then
  exec gradle "$@"
fi

echo "Error: Gradle is not installed on PATH."
echo "Install Gradle or open the project in Android Studio and run a Gradle sync/build."
exit 1
