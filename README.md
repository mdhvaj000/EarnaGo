# OmniControl Enterprise Android Platform

**OmniControl** is a commercial-grade, multi-tier MLM, Product Marketplace, E-Commerce, Digital Wallet, AI Business Assistant, and Member Management Android application built with modern Android engineering practices, Jetpack Compose, Room Database, Kotlin Coroutines, and Google Gemini AI.

---

## 🌟 Key Modules & Features

### 🏢 MLM & Team Business Engine
- **Unique Business ID & Referral System**: Automated registration with sponsor linking, referral QR codes, and referral links.
- **Unilevel & Hybrid Referral Tree**: Interactive visuals for direct downlines, network depth, level volumes, and commission calculations.
- **Automated Commission Engine**: Real-time distribution of Direct Commission, Level Bonuses, Matching Bonuses, Leadership Royalties, and Rank Rewards.
- **Rank Advancement System**: Automatic qualification tracking and rank upgrades (Member -> Bronze -> Silver -> Gold -> Platinum -> Diamond -> Crown).

### 🛒 Product Marketplace & E-Commerce
- **Global & Regional Catalog**: Multi-category products with stock tracking, GST/tax calculations, discounts, and real-time inventory sync.
- **End-to-End Sales Pipeline**: Cart, Checkout, Digital Invoicing, Order Processing, Shipping Tracking, and Automatic Commission Credits.

### 💳 Digital Wallet & Financial Settlement
- **Multi-Ledger Wallet**: Real-time balance, income, expense, commission, and reward ledgers.
- **Withdrawal & Payout Management**: Direct bank/UPI settlement requests with instant verification and owner approval workflow.
- **Transaction Audit & Export**: Full transaction histories with export capabilities.

### 👑 Owner & Admin Console
- **Role-Based Access Control (RBAC)**: Comprehensive control over platform parameters, products, category pricing, commission rules, and rank requirements.
- **Approval Workflows**: KYC verification, withdrawal approvals, system feature flags, and global broadcasts.
- **Platform Analytics & Health**: Business revenue charts, volume metrics, and user growth monitoring.

### 🤖 Gemini AI Business Assistant
- **AI Business Coach**: Sales forecasting, network analysis, auto-generating WhatsApp/social marketing posts, and member retention advice.
- **Product Recommendation Engine**: Contextual product suggestions tailored to user demographics and purchasing history.

---

## 🛠 Tech Stack & Architecture

- **Language**: Kotlin 2.2+
- **UI Framework**: Jetpack Compose with Material Design 3 (M3)
- **Architecture**: MVVM / Clean Architecture with Repository Pattern
- **Local Database**: Room DB 2.7+ (SQLite) with automatic migration & encryption support
- **Asynchrony**: Kotlin Coroutines & `StateFlow`
- **Network**: Retrofit 2 + Moshi + OkHttp
- **AI Integration**: Firebase / Google Gemini AI REST API
- **Testing**: Robolectric, Roborazzi screenshot verification, JUnit 4
- **Build System**: Gradle 8.11+ (Kotlin DSL) with Version Catalog (`libs.versions.toml`)

---

## 🚀 Building & Running

### Prerequisites
- Android Studio Ladybug | 2024.2.1 or newer
- JDK 17 or higher
- Android SDK 36 (minSdk 24)

### Command Line Builds

#### Build Debug APK
```bash
./gradlew assembleDebug
```
The generated APK will be located at:
`app/build/outputs/apk/debug/app-debug.apk`

#### Build Release APK
```bash
./gradlew assembleRelease
```
The generated APK will be located at:
`app/build/outputs/apk/release/app-release.apk`

#### Build Release Android App Bundle (AAB)
```bash
./gradlew bundleRelease
```
The generated AAB will be located at:
`app/build/outputs/bundle/release/app-release.aab`

#### Run Unit & Robolectric Tests
```bash
./gradlew testDebugUnitTest
```

---

## ⚙️ Environment Secrets Setup

API keys and credentials are injected securely via `.env` at root or environment variables (Secrets Gradle Plugin):

```env
GEMINI_API_KEY=your_gemini_api_key_here
KEYSTORE_PATH=my-upload-key.jks
STORE_PASSWORD=your_store_password
KEY_ALIAS=upload
KEY_PASSWORD=your_key_password
```

---

## 📄 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
