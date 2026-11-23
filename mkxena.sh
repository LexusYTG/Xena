#!/data/data/com.termux/files/usr/bin/bash
# build_xena.sh - compila Lorica (sin X11), la copia a Xena y compila el APK.
# Uso: bash build_xena.sh [debug|release]   (por defecto: debug)

set -e

XENA="$HOME/work/xena"
LORICA="$HOME/work/Lorica"
[ -d "$LORICA/build-noX11" ] || LORICA="$XENA/Lorica"
JNI="$XENA/app_pojavlauncher/src/main/jniLibs/arm64-v8a"
VARIANT="${1:-debug}"

export ANDROID_HOME="${ANDROID_HOME:-$HOME/android-sdk}"
if [ -z "$ANDROID_NDK_HOME" ]; then
  export ANDROID_NDK_HOME="$(ls -d "$ANDROID_HOME"/ndk/* 2>/dev/null | tail -1)"
fi

echo "== Xena:   $XENA"
echo "== Lorica: $LORICA"
echo "== SDK:    $ANDROID_HOME"
echo "== NDK:    $ANDROID_NDK_HOME"

for c in cmake make gradle java readelf; do
  command -v "$c" >/dev/null 2>&1 || { echo "FALTA '$c'. Instalalo (readelf: pkg install binutils)"; exit 1; }
done
[ -d "$XENA/app_pojavlauncher" ] || { echo "No existe $XENA/app_pojavlauncher"; exit 1; }
[ -d "$LORICA/build-noX11" ]    || { echo "No existe $LORICA/build-noX11"; exit 1; }

echo
echo "== 1/4 Compilando Lorica (build-noX11)"
rm -f "$LORICA/lib/libGL.so.1"
cmake --build "$LORICA/build-noX11" -j"$(nproc)"

SO="$LORICA/lib/libGL.so.1"
[ -f "$SO" ] || { echo "No se generó $SO"; exit 1; }

echo
echo "== 2/4 Revisando dependencias del .so"
readelf -d "$SO" | grep -E "NEEDED|SONAME" || true
if readelf -d "$SO" | grep -q 'libX11\|libxcb'; then
  echo
  echo "PIDE X11: no copio nada. Este build no sirve para Android."
  echo "Mirá: grep -i x11 $LORICA/build-noX11/CMakeCache.txt"
  exit 1
fi

echo
echo "== 3/4 Copiando a jniLibs (con respaldo)"
mkdir -p "$JNI"
[ -f "$JNI/liblorica.so" ] && cp "$JNI/liblorica.so" "$HOME/liblorica.so.bak"
cp "$SO" "$JNI/liblorica.so"
ls -l "$JNI/liblorica.so"

echo
echo "== 4/4 Compilando la app ($VARIANT)"
cd "$XENA"
if [ "$VARIANT" = "release" ]; then
  gradle assembleRelease
else
  gradle assembleDebug
fi

echo
echo "== LISTO. APK:"
find "$XENA/app_pojavlauncher/build/outputs/apk" -name "*.apk" 2>/dev/null
adb devices
adb install "$XENA/app_pojavlauncher/build/outputs/apk/debug/app_pojavlauncher-debug.apk"
