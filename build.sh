#!/usr/bin/env bash
set -euo pipefail

base=$(cd -- "$(dirname -- "$0")" && pwd)
hmg_classes=${1:?usage: bash build.sh <HMG-API out/classes> <author signing key>}
key=${2:?supply the local author signing key}
sdk=${ANDROID_HOME:-$HOME/Android}
android_jar="$sdk/platforms/android-29/android.jar"
d8="$sdk/build-tools/35.0.0/d8"
signing=hmgapi.engine.Signing
archive=EV6_KR_Wired_Android_Auto_TBT_v0.1.0_TEST.zip

[[ -f "$android_jar" && -x "$d8" ]] || { echo "Android 29 and build-tools 35.0.0 are required." >&2; exit 1; }
[[ -f "$hmg_classes/hmgapi/hook/ModContext.class" && -f "$hmg_classes/hmgapi/engine/Signing.class" ]] || {
  echo "Build the HMG-API 1.0.2 runtime classes first." >&2
  exit 1
}
[[ -f "$key" && -f "$key.pub" ]] || { echo "The author signing key and its public key are required." >&2; exit 1; }

mkdir -p "$base/.build"
work=$(mktemp -d "$base/.build/build.XXXXXX")
mkdir -p "$work/classes" "$work/payload"
mapfile -d '' sources < <(find "$base/mod/src" -name '*.java' -print0)
javac --release 8 -cp "$android_jar:$hmg_classes" -d "$work/classes" "${sources[@]}"
mapfile -d '' classes < <(find "$work/classes" -name '*.class' -print0)
"$d8" --release --min-api 19 --lib "$android_jar" --classpath "$hmg_classes" \
  --output "$work/payload/hooks.jar" "${classes[@]}"
cp "$base/mod/mod.json" "$work/payload/"
java -cp "$hmg_classes" "$signing" sign "$work/payload" "$key"

python3 - "$work/payload" "$work/$archive" <<'PY'
from pathlib import Path
import sys
from zipfile import ZIP_DEFLATED, ZipFile

root, output = map(Path, sys.argv[1:])
with ZipFile(output, "w", compression=ZIP_DEFLATED) as package:
    for path in sorted(p for p in root.rglob("*") if p.is_file()):
        package.write(path, path.relative_to(root).as_posix())
with ZipFile(output) as package:
    if package.testzip() is not None:
        raise SystemExit("Damaged ZIP entry")
    package.extractall(output.parent / "verified")
PY

uuid=$(java -cp "$hmg_classes" "$signing" uuid "$key.pub")
java -cp "$hmg_classes" "$signing" verify "$work/verified" "$uuid"
mv -- "$work/$archive" "$base/$archive"
sha256sum "$base/$archive"
