#!/bin/bash
set -euo pipefail

VERSION_FILE="version.properties"
PLUGINS_TASK=":plugins:publishToMavenLocal"

bump_type="${1:-patch}"

if [[ "$bump_type" != "patch" && "$bump_type" != "minor" ]]; then
  echo "Usage: $0 [patch|minor]" >&2
  echo "  patch (default) — bump the patch version" >&2
  echo "  minor           — bump the minor version" >&2
  exit 1
fi

if [[ ! -f "$VERSION_FILE" ]]; then
  echo "error: $VERSION_FILE not found" >&2
  exit 1
fi

current="$(grep -E '^version=' "$VERSION_FILE" | head -n1 | cut -d= -f2 | tr -d '[:space:]')"

if [[ ! "$current" =~ ^([0-9]+)\.([0-9]+)\.([0-9]+)$ ]]; then
  echo "error: cannot parse current version '$current'" >&2
  exit 1
fi

major="${BASH_REMATCH[1]}"
minor="${BASH_REMATCH[2]}"
patch="${BASH_REMATCH[3]}"

case "$bump_type" in
  patch) new_patch=$((patch + 1)); new_minor="$minor" ;;
  minor) new_patch=0;            new_minor=$((minor + 1)) ;;
esac

new_version="${major}.${new_minor}.${new_patch}"

echo "Bumping ${bump_type}: ${current} -> ${new_version}"

# Phase 1 prep: raise 'version' to the new version, lower 'pluginVersion' to the
# currently-published version for the bootstrap phase (uncommented).
cat > "$VERSION_FILE" <<EOF
version=${new_version}
pluginVersion=${current}
EOF

echo "--- Phase 1: bootstrap — publish plugins at ${new_version} (via old plugins ${current})"
./gradlew "$PLUGINS_TASK"

# Phase 2 prep: comment out and raise 'pluginVersion' to the new version so the
# full build resolves the new plugins.
cat > "$VERSION_FILE" <<EOF
version=${new_version}
# pluginVersion=${new_version}
EOF

echo "--- Phase 2: full publish at ${new_version}"
./gradlew publishToMavenLocal

git add "$VERSION_FILE"
git commit -m "v${new_version}"
echo "Committed version.properties as v${new_version}"
