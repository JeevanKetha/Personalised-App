# Jeevan OS — Personal Life Operating System

A unified Android application that consolidates Finance, Health, and Career
management into a single, AI-powered personal operating system.

Built with Kotlin, Jetpack Compose, Clean Architecture + MVVM, Room SQLite,
and Google Gemini API.

> Built using Google AI Studio as a co-developer. The product vision,
> feature design, and all architectural decisions are my own.

---

## What it does

**Finance Hub** — Expense ledger, NSE/BSE portfolio tracker, savings goals,
CSV export

**Health Hub** — Steps, water intake, sleep, nutrition logging, BMI tracker,
workout suggestions

**Career Hub** — 28-week DevOps & SRE curriculum, AI mock interviews,
YAML sandbox, subtopic progress tracking

**Focus Timer** — Background Pomodoro timer (survives app exit via
foreground TimerService)

**AI Companion** — Multi-agent routing (Financial Planner, Career Mentor,
Wellness Counselor, Cloud Mentor) with persistent 10-message memory per agent

**Updates Center** — SRE job listings, DevOps bulletins, portfolio-matched
news with 2-hour auto-refresh

---

## Tech stack

- Kotlin + Jetpack Compose
- Clean Architecture + MVVM
- Room SQLite (8 tables)
- Google Gemini API with on-device heuristic fallback
- Kotlin Coroutines + Flows
- WorkManager for background sync
- Android EncryptedSharedPreferences for secrets

---

## Getting started

**Prerequisites:** Android Studio, Android device or emulator (API 26+)

1. Clone the repo
2. Open in Android Studio
3. Get a free Gemini API key from https://aistudio.google.com
4. On first launch, tap the key icon in the AI Companion to enter your key
   (stored securely on-device via AES-256 encryption)
5. Run on device or emulator

No .env file needed — API key is entered and stored securely in-app.

---

## Project status

Active development. Currently implementing:
- Android Health Connect integration (Phase 2A)
- Health Intelligence Ecosystem — BMI, Nutrition, Seasonal AI (Phase 2B)
- Glance home screen widget
- Encrypted Google Drive backup

---

## License

MIT
