# Version History & Changelog Guide

This document tracks the release history, architecture changes, and milestones for the **Jeevan** platform, with a special focus on the stable, high-performance evolution of its core hubs.

---

## [v1.0.0] - Stable Release (Proven Foundation)
*Initial launch of the multi-hub personal executive companion dashboard.*

### Features
*   **Hub 1: Life Dashboard (Jeevan)**
    *   Integrated central focus-activation dashboard, time boxing grids, real-time UTC chronometers, and personalized visual assets.
*   **Hub 2: Health (Nourish & Strength Arena)**
    *   Dynamic macronutrient calculator, basal metabolic rate profiles, muscle-group targeted workout schedules, and customized recovery logs.
*   **Hub 3: Career (DevOps & SRE Roadmap)**
    *   **Classic 28-Week Iteration**: Introduced the structured week-by-week knowledge roadmap covering SRE foundations (Linux, Bash, Docker, AWS, Kuberenetes, Terraform, CI/CD, and Datadog).
    *   **196-Task Matrix**: Structured mapping containing 7 tasks per week.
    *   **Study Panel**: Day-by-day task selection dropdown enabling immediate progression checkboxes.
    *   **Assessment Arena**: Multi-scenario DevOps quiz questions and active flashcards.
*   **Hub 4: News & Knowledge**
    *   Live tech news aggregator, secure markdown bookmark manager, and technical resource repository.

---

## [v1.1.0] - Pilot Expansion (Navigation Experiments)
*Experimental release exploring alternative navigation paradigms.*

### Features & Adjustments
*   **Career Module Structural Revision**:
    *   Attempted a **Topic-First** structure separating content categories into subjects instead of week number sequences.
    *   Synthesized alternative week drop-downs in roadmap layout grids to adjust week numbers inline.
*   **Cognitive Impact**:
    *   User tests indicated that the topic dropdown and alternative navigation altered learning habits and increased operational friction compared to the linear, calendar-oriented 28-week approach.

---

## [v1.2.0] - Current Stable (The Resilience Rollback)
*Rollback release to restore the production-proven, highly acclaimed week-based educational design based on direct engineering feedback.*

### Restorations
*   **Structural Restoration**:
    *   Successfully restored the **Original 28-Week DevOps and SRE Roadmap** sequence.
    *   Reestablished the **Original Day-Based Daily Task Mapping (1 to 196)**.
    *   Restored original dropdown layouts (simple, low-friction drop-downs for week & study day).
*   **User Data Preservation**:
    *   Preserved all historic SQLite Room database entries, including completed study cards, notes, assessment history, and workout logs. No schema migrations or truncation occurred.
*   **Design & System Stability**:
    *   Corrected compile-time symbol mismatches and nesting errors with Jetpack Compose Lazy List layout contexts.
