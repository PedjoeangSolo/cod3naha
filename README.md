# COD3NAHA — Frontline: WWII (Android)

Original offline single-player WWII-inspired FPS prototype for Android. This is not a port of Call of Duty 3 and contains no Activision code or proprietary assets.

## Playable prototype
- Native Android Java + OpenGL ES 2.0; no emulator or external game engine.
- Landscape shooting range with generated low-poly geometry.
- Touch-look on right side; movement on left side.
- FIRE button, hit counter, moving target dummies, and restart.
- GitHub Actions builds a debug APK.

## Build
Open this repository in Android Studio with JDK 17. Install Android SDK Platform 35, sync Gradle, then choose Build > Build APK(s). Output: app/build/outputs/apk/debug/app-debug.apk.

Alternatively, open Actions, select the latest successful Android Debug APK run, and download artifact frontline-debug-apk.

## Device target
Lightweight starting point for Android ARM64, including TECNO Pova 7 4G. Uses OpenGL ES 2.0 for broad compatibility. Performance must be tested on-device.

## Roadmap
- [ ] Original rigged character models and animation
- [ ] Original weapon models, reload, recoil, and audio
- [ ] Mission objectives, enemy AI, cover, and level streaming
- [ ] Frame pacing and thermal profiling on target phone
- [ ] Sensitivity, graphics, and HUD settings

Source code: MIT. All future art/audio must be original or appropriately licensed.
