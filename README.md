# SecureVault USA Mobile Manager & LD Cloud Automation Hub

A native Android application built with **Kotlin** and **Jetpack Compose** that combines:
1. **Hardware-Backed Secure Password Vault & Autofill Service**:
   - AES-256 GCM encryption via Hardware-backed Android Keystore.
   - Room Database with automatic migrations.
   - AndroidX BiometricPrompt (Fingerprint, Face Unlock, Device PIN).
   - Official Android Autofill Service provider (`android.service.autofill.AutofillService`).
2. **USA Mobile Profile & LD-Cloud Farm Hub**:
   - 1-Tap Auto-generation of realistic USA Mobile devices (Pixel 9 Pro, Galaxy S24 Ultra, iPhone 15 Pro Max, OnePlus 12).
   - Residential Proxy configuration (IP, Port, SOCKS5/HTTP, Auth) with live ping latency tester.
   - Quick-launch bridge into TikTok Creator Center, Meta Ads Manager, and Google AdSense USA.

---

## 🚀 How to Build the APK

### Automatic via GitHub Actions
A GitHub Actions workflow is included in `.github/workflows/android.yml`. Every time code is pushed to this repository, GitHub automatically builds `app-debug.apk`. You can download the APK from the **Actions** tab on GitHub!

### Local Build using Gradle
```bash
./gradlew assembleDebug
```
The APK will be generated at:
```
app/build/outputs/apk/debug/app-debug.apk
```

---

## 🛠️ Tech Stack & Architecture
- **Language**: Kotlin 2.0+
- **UI Framework**: Jetpack Compose with Material Design 3 (M3)
- **Local Storage**: Room 2.7+ (Android SQLite)
- **Security**: Android Keystore, EncryptedSharedPreferences, AES-256
- **Biometrics**: AndroidX BiometricPrompt
- **Autofill**: Android Autofill Framework
