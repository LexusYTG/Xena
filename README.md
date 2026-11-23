# Xena

Xena es un lanzador de Minecraft Java Edition para Android, basado en [PojavLauncher](https://github.com/PojavLauncherTeam/PojavLauncher), enfocado en dispositivos con GPU Mali y con una interfaz nueva. Usa **Lorica** como capa gráfica.

Autor: **Lexus YTG**.

## Créditos

- **PojavLauncher**: Xena es un fork de PojavLauncher (PojavLauncher Team y colaboradores). Los avisos de copyright y los encabezados de los archivos originales se conservan intactos.
- **Lorica**: capa gráfica propia (OpenGL hacia GLES/Vulkan). Su núcleo desciende de un proyecto con licencia MIT de Sebastien Chevalier y Ryan Hileman; el resto es propio. El aviso y el texto de esa licencia están en `Lorica/NOTICE/LICENSE-MIT.txt`. Más detalles en `Lorica/README.md`.

## Licencia

Xena se distribuye bajo la **GNU General Public License v3.0** (ver `COPYING`).

Parte del código proviene de PojavLauncher, publicado bajo LGPL-3.0, y de Lorica, publicada bajo GPL-3.0. El texto de la licencia original de Pojav está en `HEREDADO-DE-POJAVLAUNCHER/LICENSE`. Cada componente conserva los avisos de copyright de sus autores.

Copyright (C) 2026 Lexus YTG — Xena, basado en PojavLauncher.

## Avisos legales

- Xena es un proyecto independiente. No está afiliado, respaldado ni patrocinado por Mojang, Microsoft ni por el equipo de PojavLauncher. Minecraft es marca de sus respectivos titulares.
- Para jugar en línea necesitás una cuenta de Minecraft Java Edition propia. Xena no vende ni distribuye Minecraft.
- Los mods, paquetes de mods, cargadores y versiones los crean y alojan terceros. Instalarlos y ejecutarlos es bajo tu responsabilidad.
- El software se ofrece «tal como está», sin garantías de ningún tipo, en la medida que la ley lo permita.

## Privacidad

Al abrir la app por primera vez se muestra un contrato de licencia y una política de tratamiento de datos personales (Ley 25.326, Argentina) que hay que aceptar para continuar. Si no se acepta, la app no se conecta a nada, borra sus propios datos y se cierra.

El texto completo, con los permisos que declara la app y la lista de servicios de terceros a los que se conecta, está en `app_pojavlauncher/src/main/res/raw/eula_es.txt`.

## Compilar

Requisitos usados hoy:

- JDK 17
- Gradle 9.9.1
- Android SDK con NDK 29.0.14206865
- `aapt2` de Termux, vía `android.aapt2FromMavenOverride`

El script `mkxena.sh` compila Lorica, copia la biblioteca a `jniLibs`, ejecuta `gradle assembleDebug` e instala el APK por `adb`:

```
bash mkxena.sh
```

El APK queda en `app_pojavlauncher/build/outputs/apk/debug/app_pojavlauncher-debug.apk`.

El `namespace` y los paquetes Java siguen siendo `net.kdt.pojavlaunch` a propósito, porque los símbolos JNI del código nativo dependen de esos nombres. El `applicationId` es `com.lexus2026.xena`.
