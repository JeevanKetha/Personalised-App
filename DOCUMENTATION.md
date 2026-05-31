# JEEVAN OS: SYSTEM ARCHITECTURE & USER MANUAL
*Comprehensive Technical Blueprint, Software Architecture Map, and Operations Manual for Jeevan Personal OS*

---

## SECTION 1: EXEC SUMMARY & PHILOSOPHY
**Jeevan OS** is a futuristic, highly integrated personal operating system and unified lifecycle application. Built with **Kotlin, Jetpack Compose, Coroutines, Flow, and Room SQLite**, Jeevan OS consolidates several disconnected aspects of daily life into a single, high-fidelity visual experience.

### Main Philosophical Pillars:
1. **Cognitive Synergy (Task + Study + Focus)**: Seamless transition between study notes, real-time quizzes, YAML schema validations, and Pomodoro focus timers.
2. **Strategic Wealth Compounder**: Keeping track of personal finances, NSE stock holdings, and career-specific savings goals inside a dynamic Indian stock market tracker.
3. **Continuous Upskilling**: Structured 28-week DevOps & SRE career roadmap with dynamic mock evaluations and custom notes.
4. **Context-Aware Intelligence**: Multi-Agent Routing using Google Gemini API or High-Fidelity Heuristics fallback to advise readers with full context of their database metrics.

---

## SECTION 2: SYSTEM ARCHITECTURE MAPPING

Jeevan OS adheres to **Clean Architecture** principles structured on the **MVVM (Model-View-ViewModel)** design pattern. It operates 100% locally with reactive data streams powered by Kotlin Flows, while retrofitted network clients provide dynamic AI insights and SRE news.

```
                  +----------------------------------------------+
                  |               JeevanMainScreen.kt            |
                  |             (Jetpack Compose UI)             |
                  +----------------------+-----------------------+
                                         |
                       Collects state / Dispatches actions
                                         v
                  +----------------------------------------------+
                  |               JeevanViewModel.kt             |
                  |                (UI State Holder)             |
                  +---------+------------+------------+----------+
                            |            |            |
         Launches Coroutines|            |            | Accesses
                            v            |            v
    +-------------------------+          |    +------------------------+
    |       TimerService      |<---------+    |      GeminiClient      |
    |  (Background Thread CT) | Starts/Stops  |   (Retrofit Service)   |
    +-------------------------+               +-----------+------------+
                                                          | Runs REST Web
                                                          v
                                              +------------------------+
                                              |    Gemini API Proxy    |
                                              |   (gemini-3.5-flash)   |
                                              +---------------+--------+
                                                              | Injects Live State
                                                              v
+-------------------------------------------------------------+--------+
|                                                                      |
|                         JeevanRepository.kt                          |
|                       (Data Access Orchestrator)                     |
|                                                                      |
+---------------------------------+------------------------------------+
                                  |
                           Directs local queries
                                  v
+----------------------------------------------------------------------+
|                                                                      |
|                          JeevanDatabase.kt                           |
|                    (Room SQLite Persistence DB)                      |
|                                                                      |
|  +--------------+  +-----------------+  +-----------------+          |
|  | Transactions |  | Health Logs     |  | Portfolio Stocks|          |
|  +--------------+  +-----------------+  +-----------------+          |
|  +--------------+  +-----------------+  +-----------------+          |
|  | User profile |  | Subtopic Prog   |  | Goals Funds List|          |
|  +--------------+  +-----------------+  +-----------------+          |
|                                                                      |
+----------------------------------------------------------------------+
```

### Component Breakdown
1. **Jetpack Compose Presentation Layer (`ui.screen.JeevanMainScreen`)**:
   - A single-activity edge-to-edge container that utilizes a bottom-navigation interface to swap between designated Hub views.
   - Uses `Crossfade` transitions to render UI states dynamically.
2. **Adaptive State Viewmodel (`ui.viewmodel.JeevanViewModel`)**:
   - Main coordinator of the OS. Exposes more than 30 logical state parameters using read-only `StateFlow` structures initialized by private mutable instances.
   - Spawns background tasks via `viewModelScope.launch` protecting performance.
3. **Database Layer (`data.database.JeevanDatabase`)**:
   - Persistent Room database using destructive migration fallbacks to allow seamless evolution of schemas during engineering.
4. **REST Client Engine (`network.GeminiClient`)**:
   - Retrofit network architecture integrated with standard OkHTTP clients and Moshi adapters to query Gemini endpoints safely.

---

## SECTION 3: DATABASE & STORAGE ENGINE (ROOM SCHEMA)

The Jeevan Database is composed of **8 core tables** designed for relational integrity:

```
+---------------------------------------------------------------------------------------+
|                                    JEEVAN ROOM DATABASE                               |
|                               (jeevan_life_os_database)                               |
+---------------------------------------------------------------------------------------+
|  1. user_settings (UserProfile)                                                       |
|     - id: Int [PK, default = 1]                                                       |
|     - name: String                                                                    |
|     - jobTarget: String                                                               |
|     - monthlyBudgetLimit: Double                                                      |
|     - dailyWaterGoalMl: Int                                                           |
|     - dailyStepGoal: Int                                                              |
|     - careerStreak: Int                                                               |
|     - balanceAmount: Double                                                           |
|     - weightKg, heightCm, computedBmi: Double                                         |
+---------------------------------------------------------------------------------------+
|  2. transactions (Transaction)                                                        |
|     - id: Int [PK, autoIncrement]                                                     |
|     - title: String                                                                   |
|     - amount: Double                                                                  |
|     - type: String ("EXPENSE" or "INCOME")                                            |
|     - category: String                                                                |
|     - isSubscription: Boolean                                                         |
|     - date: Long                                                                      |
+---------------------------------------------------------------------------------------+
|  3. career_progress (CareerProgress)                                                  |
|     - topicId: String [PK] (e.g., "linux", "kubernetes")                              |
|     - level: Int                                                                      |
|     - xp: Int                                                                         |
|     - completedQuizIds, completedLabIds: String (Comma-separated)                     |
|     - lastActiveTime: Long                                                            |
+---------------------------------------------------------------------------------------+
|  4. subtopic_progress (SubtopicProgress)                                              |
|     - subtopicId: String [PK] (e.g., "aws_iam")                                       |
|     - parentTopicId: String                                                           |
|     - isCompleted: Boolean                                                            |
|     - completionDate: Long?                                                           |
|     - reasonNotCompleted: String?                                                     |
|     - assessmentScore: Int                                                            |
+---------------------------------------------------------------------------------------+
|  5. portfolio_holdings (PortfolioHolding)                                             |
|     - id: Int [PK, autoIncrement]                                                     |
|     - assetName, symbol, exchange, sector: String                                     |
|     - quantity: Double                                                                |
|     - purchasePrice, currentPrice: Double                                             |
|     - purchaseDate: Long?                                                             |
|     - notes: String?                                                                  |
+---------------------------------------------------------------------------------------+
|  6. career_goal_funds (CareerGoalFund)                                                |
|     - id: Int [PK, autoIncrement]                                                     |
|     - name: String                                                                    |
|     - targetAmount, currentAmount: Double                                             |
+---------------------------------------------------------------------------------------+
|  7. health_logs (HealthLog)                                                           |
|     - dateString: String [PK] ("YYYY-MM-DD")                                          |
|     - waterIntakeMl, caloriesBurned, caloriesConsumed: Int                            |
|     - sleepMinutes: Int                                                               |
|     - moodScore: Int (1 to 5)                                                         |
|     - journalEntry: String                                                            |
|     - stepsCount: Int                                                                 |
+---------------------------------------------------------------------------------------+
|  8. news_bookmarks (NewsBookmark)                                                     |
|     - id: Int [PK, autoIncrement]                                                     |
|     - title: String                                                                   |
|     - category: String                                                                |
|     - url, description: String                                                        |
|     - savedAt: Long                                                                   |
+---------------------------------------------------------------------------------------+
```

### DAO Operations (`data.dao.JeevanDao`)
The DAO utilizes standard SQL queries to extract data, feeding them back as either Kotlin Flow reactive streams or synchronous direct requests:
* **Transactions Flow**: `getAllTransactionsFlow()` outputs values sorted by date descending.
* **Direct Seeding Actions**: Direct queries (e.g., `getUserProfileDirect()`) provide safe lifecycle hooks to seed records if empty during initialization.
* **On Conflict Strategy**: All insertion tasks leverage `OnConflictStrategy.REPLACE`, facilitating clean update operations across all records.

---

## SECTION 4: NETWORK LAYOUT & AI ORCHESTRATION CLIENT

The AI Orchestration panel queries Google Gemini through standard network clients. When offline or running without keys, safe local fallback scripts take over.

### Retrofit Structure (`GeminiApiService`)
* **Endpoint Address**: `v1beta/models/gemini-3.5-flash:generateContent`
* **JSON Serialization**: Managed by Moshi Converter Factory.
* **HTTP Client Parameters**: Configuration defines a standard 60-second connection and read timeout.

### Multi-Agent Persona Routing Engine
The network client has an integrated dynamic request-routing routing logic. Based on keywords matched within prompts, it adjusts user system guidelines prior to submission:
1. **General Knowledge Agent** (triggered by terms like "*capital*", "*who is*", "*president*"): Direct, concise responses without boilerplate or conversational fillers.
2. **Cloud Career Mentor** (triggered by terms like "*k8s*", "*terraform*", "*jenkins*", "*aws*"): Architecturally sound expertise offering scripts, CLI examples, and interview answers.
3. **Financial Planner Agent** (triggered by terms like "*portfolio*", "*sip*", "*budget*", "*save*"): Allocates capital strategically and reviews monthly thresholds.
4. **Wellness Counselor Agent** (triggered by terms like "*workout*", "*diet*", "*protein*", "*stretches*"): Offers physical, structural, and custom nutritional guidance.
5. **General Companion** (Default route): Futuristic operating companion simulating an offline Jarvis intelligence.

### Zero-Latency Local Fallback Heuristics
If `BuildConfig.GEMINI_API_KEY` is not filled or if the device is disconnected from web interfaces, the OS uses local rules to maintain responsiveness:
```kotlin
private fun getLocalOfflineHeuristicResponse(prompt: String): String {
    val lower = prompt.lowercase()
    return when {
         lower.contains("capital of india") -> "The capital of India is **New Delhi**.\n*(Heuristic match: 100% precision)*"
         lower.contains("hello") -> "Hello, Commander. I am Jeevan - your lifestyle operating system."
         // Additional robust simulated responses for cloud, finance and health queries
    }
}
```

---

## SECTION 5: DETAILED MODULES BREAKDOWN & USER PLAYBOOKS

### HUB 1: DASHBOARD (Core OS Home)
Houses device trackers, primary missions, diagnostics, and a bookmarks dashboard.

```
+----------------------------------------------------------+
|                        JEEVAN OS                         |
|                 INTELLIGENT PERSONAL OS                  |
+----------------------------------------------------------+
|                                                          |
|   [ DATE AND TIME WIDGET: synchronized with device ]     |
|   Saturday, 30 May 2026.  18:05:41 PM                    |
|                                                          |
|   +--------------------------------------------------+   |
|   |  Good evening, Jeevan Explorer.                  |   |
|   |  System: All integrations compiled. Focus active  |   |
|   +--------------------------------------------------+   |
|                                                          |
|   [ WALLET CAPITAL ]              [ VALIDATED UNITS ]    |
|   ₹20,000.0                       12/48                  |
|                                                          |
|   +--------------------------------------------------+   |
|   |  PRIMARY COGNITIVE GOAL                          |   |
|   |  Validate deployment subtopic: AWS IAM COMMANDS  |   |
|   +--------------------------------------------------+   |
|                                                          |
|   [ REAL-TIME DIAGNOSTIC TELEMETRY ]                     |
|   ✔ Database seeded with historic metadata.             |
|   ✔ Indian Stock Market open and updating active indices.|
|                                                          |
+----------------------------------------------------------+
```

#### Playbook Exercises:
1. **Dynamic Workout plan Generation & Manual Refresh**:
   - Navigate to the Health Hub and check your biometrics under "BIOMETRICS & PHYSICAL INDEXING".
   - View your customized physical suggestions under "OFFICE WORKOUT SCHEME (ADAPTED)", which are dynamically tailormade for your body mass index.
   - Click the "+ Refresh" button in the corner to manually trigger recalculations, re-evaluate recent weight statistics and generate a fresh exercise plan anytime.

---

### HUB 2: FINANCE (Strategic Capital)
Tracks ledger registers, manages stock purchase indices, and analyzes savings progress against monthly budgets.

```
+----------------------------------------------------------+
|                      FINANCIAL HUB                       |
+----------------------------------------------------------+
|  LEDGER BUDGET BALANCE: ₹18,450.0                        |
|  Total Month Expenses logged: ₹1,550.0                   |
|                                                          |
|  +----------------------------------------------------+  |
|  |  FINANCIAL HEALTH CLASSIFIER                       |  |
|  |  Capital power: GOOD. Current savings rate is 92%. |  |
|  +----------------------------------------------------+  |
|                                                          |
|  +----------------------------------------------------+  |
|  |  INDIAN MARKET HOLDINGS RE-EVALUATOR               |  |
|  |  Active portfolio val: ₹124,500 (+3.2% Today Gain) |  |
|  |  Holdings: TCS (8 qty), RELIANCE (14 qty), INFY    |  |
|  +----------------------------------------------------+  |
|                                                          |
|  +----------------------------------------------------+  |
|  |  CAREER TRAINING SAVINGS GOALS (GOAL FUND)         |  |
|  |  DevOps Bootcamp: ₹4,500 / ₹15,000 target          |  |
|  +----------------------------------------------------+  |
+----------------------------------------------------------+
```

#### Playbook Exercises:
1. **Logging Expenses**:
   - Scroll to the Ledger Section.
   - Enter Title (e.g. "*Kubernetes Cloud Costs*"), amount (e.g., "*750*").
   - Click "Log Expense" to update your balance and increase metrics.
2. **Buying Stocks**:
   - Select "+ Add Investment Tracker".
   - Search or input a symbol (e.g., "*TCS*"), Quantity (*10*), Purchase Price (*3820*).
   - Tap "Confirm Asset Acquisition" to add it to your portfolio tracker.
3. **Compiling Financial Reports**:
   - Tap "Export Ledger CSV Report". The system generates a clean Excel/CSV file with formatted columns directly inside your local application cache structure.

---

### HUB 3: CAREER (DevOps & SRE Roadmap)
A structured 28-week curriculum containing training pathways, flashcards, diagnostic interview checks, and a sandbox for YAML schema files.

#### Training Sections:
* **Roadmap sub-tab**: Detailed layout of 28 weeks of SRE knowledge (Linux administration, containerization, ECS, Kubernetes, Ansible, CloudFormation, Datadog tracking).
* **Assessment Arena sub-tab**: Offers multi-scenario diagnostic checks. Select a topic and use the "AI Suggest" dynamic buttons to complete simulated interviews.
* **Diagnostics tab**: A place to inspect overall unit achievements.

---

### HUB 4: HEALTH (Wellness Operations Center)
Focuses on micro-habit consolidation, hydration monitors, weight/BMI calculations, and Pomodoro focus timers.

#### Core Integrations:
1. **Deep Focus Clock**: Countdown system supported by an Android background `TimerService` to keep counting down even when you exit the app.
2. **Vitals Checker**: Inserts weight and height coordinates, calculates BMI index, and offers dynamic workouts based on your performance profile.
3. **Nutrition Tracker**: Logs daily caloric values to guide metabolic checks.

---

### HUB 5: UPDATES CENTER (RSS Feed Stream & SRE Info Hub)
A high-fidelity intelligent aggregator that indexes live career opportunities, technology bulletins, and application updates. Designed to act as an information discovery channel rather than a single database of truth, every update provides transparent redirection markers for self-verification.

#### Modern Feed Channels:
* **JOBS (SRE Career Opportunities)**: Indexes vacancies from premium corporate portals like TCS, Infosys, and Cognizant with full structural field mapping (Company, Role, Experience, Location, Posted Time, and an integrated "Apply Now" official carrier link redirection).
* **DEVOPS_UPDATES (SRE Releases & Bulletins)**: Highlights key technical milestones from official communities (Kubernetes Blog, Docker Documentation, HashiCorp Github, and Python Releases) complete with release summaries and "Verify Source" redirections.
* **GENERAL (SRE Ecosystem Advisory)**: Broadcasts foundational SRE ecosystem announcements, cloud networking alerts, and system-wide documentation updates.

---

## SECTION 5B: SPECIALIZED SOURCE VALIDATION & AUTO-REFRESH MECHANICS

Jeevan OS prioritizes absolute transparency and authenticity. To achieve this, we transitioned the Updates Center and Portfolio News from static offline displays into dynamic, verifiable, self-updating data streams:

```
[User Sees Update/Article] 
         |
         v
[Taps Card Component] ---> (Reads Title, Source Name, Published Date, and Freshness Log)
         |
         v
[Opens Redirection] ---> (Auto-routes to Official Careers Page, Release Note, or Financial Portal)
         |
         v
[Independent Verification] ---> (Zero-trust verified authenticity achieved)
```

### 1. Unified 2-Hour Background Auto-Refresh Engine
* **Threading Loop**: Spawns a dedicated asynchronous worker loop mapped to `viewModelScope.launch` inside the unified `JeevanViewModel` lifecycle.
* **Duration Strategy**: Polls, fetches, and refreshes all tech updates, job notices, system advisories, and stock/ETF portfolio news exactly every **120 minutes (7,200,000 ms)**.
* **Visual Freshness Indicators**: Both hubs display clear, high-contrast, synced telemetry trackers displaying:
  * **Last Updated**: Real-time localized publication check (e.g., `10:15 AM`)
  * **Next Auto-Refresh**: Explicit time of next scheduled fetch (e.g., `12:15 PM`)
  * Displays customized synced tags (e.g., `SYNCED: HH:MM AM/PM`) under individual elements.

### 2. Personalized Holding-Specific Portfolio News
* **Capital Protection Flow**: Instead of generic, unrelated finance news, the app inspects active asset holdings inside local Room SQLite databases in real time.
* **Equity Matching Metrics**: Filters and prioritizes news strictly related to user investments:
  * Taps **TCS** -> Delivers custom SRE platform release notes with direct links to `tcs.com`.
  * Taps **Infosys** -> Delivers sovereign cloud security frameworks with direct links to `infosys.com`.
  * Taps **HDFC Bank** -> Delivers core cloud telemetry optimization news linked to `hdfcbank.com`.
  * Indexes custom alerts for gold, silver, index funds, JSW, NTPC, IOC, power grids, and general market indices.
* **Source Redirections**: Every portfolio card acts as an interactive button that seamlessly routes the user to official bulletins (TCS Investor Relations, NSE India Official, AMC Portal, or RBI Bulletins) with zero intermediate redirection noise.

### 3. Job Redirection Compliance
* Job cards include complete semantic blocks detailing expectations, experience ranges (e.g., "2-4 Years"), and geographical boundaries (e.g., "Hyderabad (Hybrid)").
* The "Apply Now" call-to-action acts as a direct validation tool, safely auto-opening the parent enterprise's official careers listing page with complete validation security.

---

## SECTION 6: CODE QUALITY & HOOKS (TEST TAG MAP)

For automated UI inspections, deep-link test executions, or play-store platform checks, Jeevan OS uses specific tags assigned to Jetpack Compose elements:

```
+--------------------------------------+---------------------------------------+
| UI COMPONENT                         | TEST TAG EXPOSED                      |
+--------------------------------------+---------------------------------------+
| App Scaffold Container               | "jeevan_main_scaffold"                |
| Live Time Widget Container           | "device_clock_widget_container"       |
| Primary Weather Widget               | "location_weather_widget"             |
| Journal Title Input Field            | "news_title_input"                    |
| Journal Record Submission            | "news_submit_button"                  |
| Focus Timer Duration Picker (Up)     | "timer_increase_button"               |
| Focus Timer Duration Picker (Down)   | "timer_decrease_button"               |
| Focus Start Trigger                  | "start_timer_button"                  |
| Focus Reset Trigger                  | "reset_timer_button"                  |
| Ledger Title Text Field              | "transact_title_input"                |
| Ledger Amount Input Field            | "transact_amount_input"               |
| Record Expense Button                | "log_expense_button"                  |
| Portfolio Snapshot Container         | "portfolio_snapshot_card"             |
| SRE Updates Center Container         | "news_center_hub"                     |
| Portfolio News Container Card        | "portfolio_news_card"                 |
| Portfolio News Refresh Trigger Button | "refresh_portfolio_news_btn"          |
| Individual Portfolio News Item Card  | "portfolio_news_item_<ID>"            |
| Updates Center News Card Element     | "news_article_card_<ID>"              |
+--------------------------------------+---------------------------------------+
```

---

## SECTION 7: DEVELOPER OPERATION & BUILD RUNS

To manage dependencies, test execution, and compilation tasks inside the Android Workspace sandbox, use the following commands:
* **Sync & Build APK**: Run `gradle assembleDebug` in the workspace to construct an installation package.
* **Run Local Unit Tests**: Executing `gradle test` verifies JVM dependencies and Compose routing rules without requiring hardware emulation.
* **Secrets Injections**: Always map keys inside your secure **Secrets panel in AI Studio** using `BuildConfig.GEMINI_API_KEY` for high-fidelity Dynamic Core Gemini analyses. Do NOT configure local workspace files with raw tokens.
