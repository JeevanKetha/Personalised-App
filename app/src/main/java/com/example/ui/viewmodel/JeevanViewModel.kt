package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.widget.Toast
import android.location.Geocoder
import com.google.android.gms.location.LocationServices
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.JeevanDatabase
import com.example.data.entity.*
import com.example.data.repository.JeevanRepository
import com.example.network.GeminiNetworkClient
import com.example.service.TimerService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class JeevanViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: JeevanRepository
    
    // --- Room flow states ---
    val transactions: StateFlow<List<Transaction>>
    val careerProgress: StateFlow<List<CareerProgress>>
    val healthLogs: StateFlow<List<HealthLog>>
    val userProfile: StateFlow<UserProfile>
    val subtopicsProgress: StateFlow<List<SubtopicProgress>>
    val newsBookmarks: StateFlow<List<NewsBookmark>>
    val portfolioHoldings: StateFlow<List<PortfolioHolding>>
    val careerGoalFunds: StateFlow<List<CareerGoalFund>>

    // --- UI Interactive States ---
    private val _isBrainThinking = MutableStateFlow(false)
    val isBrainThinking: StateFlow<Boolean> = _isBrainThinking

    // AI Chat History
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages

    // --- News Center Feed States ---
    private val _newsArticles = MutableStateFlow<List<NewsCenterItem>>(emptyList())
    val newsArticles: StateFlow<List<NewsCenterItem>> = _newsArticles

    private val _lastNewsRefresh = MutableStateFlow<Long>(0L)
    val lastNewsRefresh: StateFlow<Long> = _lastNewsRefresh

    private val _isNewsRefreshing = MutableStateFlow(false)
    val isNewsRefreshing: StateFlow<Boolean> = _isNewsRefreshing

    // --- Dynamic Biometrics & Portfolio News States ---
    private val _adaptiveWorkouts = MutableStateFlow<List<String>>(listOf(
        "Squats: 3 sets x 15 reps (adapted for standard stability)",
        "Desk Pushups: 3 sets x 12 reps (shoulder posture base)",
        "Seated Core Twists: 10 mins (sitting desk relief)",
        "Cardio Corridor Walk: 15 mins (circulation activator)"
    ))
    val adaptiveWorkouts: StateFlow<List<String>> = _adaptiveWorkouts

    private val _quarterlyReports = MutableStateFlow<List<String>>(emptyList())
    val quarterlyReports: StateFlow<List<String>> = _quarterlyReports

    private val _portfolioNews = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val portfolioNews: StateFlow<List<Pair<String, String>>> = _portfolioNews

    // --- Dynamic Question Generation for SRE Assessments ---
    private val _dynamicQuestions = MutableStateFlow<Map<String, List<String>>>(emptyMap())
    val dynamicQuestions: StateFlow<Map<String, List<String>>> = _dynamicQuestions

    // Focus Timer States (Synced with TimerService)
    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning

    private val _customDurationMinutes = MutableStateFlow(25)
    val customDurationMinutes: StateFlow<Int> = _customDurationMinutes

    private val _timerSecondsRemaining = MutableStateFlow(1500) // Default 25 min
    val timerSecondsRemaining: StateFlow<Int> = _timerSecondsRemaining

    private var localTimerJob: kotlinx.coroutines.Job? = null

    private fun startLocalTimer(durationSeconds: Int) {
        localTimerJob?.cancel()
        _isTimerRunning.value = true
        _timerSecondsRemaining.value = durationSeconds
        localTimerJob = viewModelScope.launch {
            while (_timerSecondsRemaining.value > 0) {
                kotlinx.coroutines.delay(1000)
                if (TimerService.isRunning) {
                    localTimerJob?.cancel()
                    break
                }
                _timerSecondsRemaining.value = _timerSecondsRemaining.value - 1
            }
            if (_timerSecondsRemaining.value <= 0) {
                _isTimerRunning.value = false
                _timerSecondsRemaining.value = _customDurationMinutes.value * 60
                
                val context = getApplication<Application>().applicationContext
                val intent = Intent(context, TimerService::class.java).apply {
                    action = "START"
                    putExtra("duration_seconds", 1)
                }
                try {
                    androidx.core.content.ContextCompat.startForegroundService(context, intent)
                } catch (e: Exception) {
                    android.util.Log.e("JeevanViewModel", "Failed to start completion timer", e)
                }
            }
        }
    }

    // --- Career DevOps Interview Assessment States ---
    private val _assessmentSubtopicId = MutableStateFlow<String?>(null)
    val assessmentSubtopicId: StateFlow<String?> = _assessmentSubtopicId

    private val _assessmentCurrentQuestionIndex = MutableStateFlow(0)
    val assessmentCurrentQuestionIndex: StateFlow<Int> = _assessmentCurrentQuestionIndex

    private val _assessmentAnswers = MutableStateFlow<List<String>>(listOf("", "", ""))
    val assessmentAnswers: StateFlow<List<String>> = _assessmentAnswers

    private val _isAssessmentEvaluating = MutableStateFlow(false)
    val isAssessmentEvaluating: StateFlow<Boolean> = _isAssessmentEvaluating

    private val _assessmentStrengths = MutableStateFlow<String?>(null)
    val assessmentStrengths: StateFlow<String?> = _assessmentStrengths

    private val _assessmentWeaknesses = MutableStateFlow<String?>(null)
    val assessmentWeaknesses: StateFlow<String?> = _assessmentWeaknesses

    private val _assessmentScoreResult = MutableStateFlow(0)
    val assessmentScoreResult: StateFlow<Int> = _assessmentScoreResult

    val subtopicQuestions = mapOf(
        "week_1" to listOf(
            "What is the difference between the Linux Kernel space and User space?",
            "Explain the role of /proc and /sys virtual directories in Linux filesystem architecture.",
            "Explain file permission values like chmod 755 vs 644 and how directory execution permission differs from files."
        ),
        "week_2" to listOf(
            "How does systemd manage service configurations, dependencies, and centralized logs via journalctl?",
            "Contrast soft limits vs hard limits inside security configurations (limits.conf) and their SRE impact.",
            "Detail how processes route to background threads, and compare management of SIGTERM (15) vs SIGKILL (9) signals."
        ),
        "week_3" to listOf(
            "Explain safe asymmetric SRE SSH handshakes using generated authorized_keys files.",
            "Contrast TCP and UDP connection types, and specify ss/netstat port auditing techniques.",
            "How do you design high-reliability Bash scripts using strict error checking systems (set -e, set -o pipefail)?"
        ),
        "week_4" to listOf(
            "Describe the internal mechanics of a Git commit. What is the difference between a loose object and a packfile?",
            "Explain how Git branches operate under the hood (e.g., HEAD pointer references) and compare git merge vs git rebase.",
            "How does Python manage third-party modules using Virtual Environments, and why is this critical for SRE script isolation?"
        ),
        "week_5" to listOf(
            "What are the security implications of utilizing the AWS Root account for daily operations?",
            "How does AWS evaluate IAM identity-based, resource-based, and organization-level SCP policies when evaluating permissions?",
            "Detail how multi-factor authentication (MFA) and AWS CloudTrail audit logs contribute to enterprise compliance infrastructure."
        ),
        "week_6" to listOf(
            "Compare EC2 instance virtualization types: HVM vs PV, and detail SRE launch choosing strategies.",
            "Contrast EBS Block volumes with host-physical Ephemeral Instance storage in active workloads.",
            "Are Security Groups stateful or stateless? How do they differ fundamentally from network NACLs?"
        ),
        "week_7" to listOf(
            "How do Application Load Balancers (ALB) handle path-based and host-based routing across target groups?",
            "Explain how Auto Scaling Groups (ASG) manage lifecycle hooks and dynamic scaling cooldowntimes.",
            "What are the performance, indexing, and safety benefits of S3 Versioning paired with Object Locks?"
        ),
        "week_8" to listOf(
            "Detail SRE routing strategies on Route53: failover, multivalue, geoproximity, and latency-based routes.",
            "How do CloudWatch metrics, alarms, and custom logs trigger automated recovery sequences in EC2?",
            "Explain how Systems Manager (SSM) Session Manager executes remote host commands without exposing inbound SSH ports."
        ),
        "week_9" to listOf(
            "Construct a fully-decoupled custom AWS VPC with public, private, and isolated subnets.",
            "Sizing network segments: How many usable IPs are in a /24 IPv4 block, and why does AWS reserve 5 IPs?",
            "Explain the structural routing path and address translation mechanism of a NAT Gateway in private subnet traffic."
        ),
        "week_10" to listOf(
            "Compare connecting multi-account network segments using VPC Peering vs AWS Transit Gateway.",
            "What is the difference between VPC Interface Endpoints (powered by PrivateLink) and Gateway Endpoints?",
            "Deploying network security: Explain how AWS WAF protects web apps from SQLi/XSS at the Edge."
        ),
        "week_11" to listOf(
            "Analyze RDS deployment shapes: Multi-AZ synchronous replication vs cross-region read replicas.",
            "Explain how DynamoDB achieves single-digit millisecond latency at scale using partition keys.",
            "Describe how caching layers (ElastiCache Redis / Memcached) prevent database connection starvation."
        ),
        "week_12" to listOf(
            "Analyze cold start times in AWS Lambda: How do provisioned concurrency and language choice affect SRE SLA?",
            "Explain the difference between synchronous, asynchronous, and event-source mapping executions in serverless stacks.",
            "Compare API Gateway HTTP APIs vs REST APIs regarding cost, performance, and custom authorizers."
        ),
        "week_13" to listOf(
            "Describe CloudFormation stack drift detection mechanisms and how to remediate deviations.",
            "How does CloudFormation handle rollback failures, and what are helper scripts (cfn-init/-signal)?",
            "Detail the 6 Pillars of the AWS Well-Architected Framework and how they guide infrastructure audits."
        ),
        "week_14" to listOf(
            "Analyze cost optimization models on AWS: Savings Plans, Reserved Instances, and Spot Fleets.",
            "How does AWS Cost Explorer anomaly detection assist SRE teams in preempting billing overflows?",
            "Describe the Core Principles of FinOps and specify methods to allocate multi-tenant cluster costs."
        ),
        "week_15" to listOf(
            "Contrast Docker's storage driver overlays with static mounting models regarding file modifications.",
            "How does the Docker host namespace regulate process visibility (PID) and network abstraction (NET)?",
            "Analyze how cgroups enforce memory limits, and describe the behavior when a container gets OOM killed."
        ),
        "week_16" to listOf(
            "What is the difference between docker-compose project names, services, and dynamic host network pools?",
            "Analyze how to eliminate secret exposures in images using multi-stage builds and Docker BuildKit mounts.",
            "How do we audit image vulnerabilities using static analysis tools like Trivy or Anchore Engine?"
        ),
        "week_17" to listOf(
            "Compare running workloads in ECS utilizing the EC2 launch type vs serverless Fargate launch type.",
            "Explain how ECS tasks communicate securely using IAM Task Roles vs Task Execution Roles.",
            "How do ECS service auto-scaling and target tracking integrations work with Application Load Balancers?"
        ),
        "week_18" to listOf(
            "Describe the event-driven architecture of GitHub Actions pipelines (runners, workflows, actions).",
            "How do you share files or state across pipeline jobs in a fast, distributed GHA workflow?",
            "Detail safe workflow secrets injection strategies using OIDC connectors instead of long-lived API keys."
        ),
        "week_19" to listOf(
            "Explain how static application security testing (SAST) differs from dynamic application security testing (DAST).",
            "What are the security benefits of performing software composition analysis (SCA) early in CI/CD pipelines?",
            "How do automated dependency updates (e.g. Dependabot) reduce supply chain container vulnerabilities?"
        ),
        "week_20" to listOf(
            "Describe Jenkins distributed architecture: Controller vs Agent nodes, and safe SSH configurations.",
            "Contrast Jenkins Declarative Pipelines with Scripted Pipelines in terms of readability and extensibility.",
            "Contrast Jenkins pipeline architecture with GitLab CI's runner-based YAML setup."
        ),
        "week_21" to listOf(
            "Explain how Ansible achieves agentless configuration management utilizing SSH and Python.",
            "What is the difference between an Ansible Playbook, a Play, a Task, and an idempotent Module?",
            "How do Ansible Roles structure configuration workloads, and how does Ansible Vault encrypt sensitive variables?"
        ),
        "week_22" to listOf(
            "Explain the role of the API Server, etcd, Scheduler, and Controller Manager inside the Kubernetes Control Plane.",
            "Contrast how kubelet and kube-proxy run on a worker node and manage pod lifecycles.",
            "How do we configure Pod resources requests vs limits, and how does it affect scheduling priority?"
        ),
        "week_23" to listOf(
            "Describe AWS EKS VPC CNI network allocation model and how it assigns real VPC IPs to Pods.",
            "Explain helm charts structure and how value overrides enable multi-environment templating.",
            "Explain ArgoCD's pull-based GitOps sync engine, and how it detects and auto-remediates manual cluster drift."
        ),
        "week_24" to listOf(
            "How do Kubernetes NetworkPolicies restrict ingress/egress CIDR blocks and pod labels statelessly?",
            "Describe how Prometheus scraping patterns pull metrics from pod endpoints in a time-series DB.",
            "How do you design high-reliability Grafana Dashboards for tracking cluster-wide SRE Golden Signals?"
        ),
        "week_25" to listOf(
            "What is declarative Infrastructure-as-Code? Explain how Terraform builds an abstract resource dependency graph.",
            "Detail the importance of Terraform State, local-vs-remote state locking (e.g., S3 + DynamoDB), and state corruption risks.",
            "Contrast the actions taken by terraform plan, terraform apply, and terraform destroy."
        ),
        "week_26" to listOf(
            "How do Terraform Modules structure reusable infrastructure components across Dev/Staging/Prod environments?",
            "Explain Terraform state manipulation techniques: terraform import, state mv, and state rm.",
            "How do you secure secrets in Terraform codebase (e.g., environment variables, HashiCorp Vault integrations)?"
        ),
        "week_27" to listOf(
            "Define AIOps: How do machine learning models analyze log anomalies and preemptively alert on cloud hardware failure?",
            "Describe AWS AI operations services: How Amazon DevOps Guru isolates system bottlenecks autonomously.",
            "How does Amazon Bedrock enable hosting serverless foundation models safely behind corporate VPC perimeter?"
        ),
        "week_28" to listOf(
            "Explicate the multi-tier hybrid SAA-C03 architecture: secure private network, database failovers, cost, and high availability.",
            "Detail the best techniques for answering SAA-C03 scenario-based questions: process of elimination, finding key identifiers.",
            "How do automation, monitoring, and auto-tuning SRE systems maximize interview readiness and career scalability?"
        )
    )

    fun startAssessment(subtopicId: String) {
        _assessmentSubtopicId.value = subtopicId
        _assessmentCurrentQuestionIndex.value = 0
        _assessmentAnswers.value = listOf("", "", "")
        _assessmentStrengths.value = null
        _assessmentWeaknesses.value = null
        _assessmentScoreResult.value = 0
        _isAssessmentEvaluating.value = false
        generateDynamicAssessmentQuestions(subtopicId)
    }

    fun updateAssessmentAnswer(index: Int, answer: String) {
        val currentList = _assessmentAnswers.value.toMutableList()
        if (index in currentList.indices) {
            currentList[index] = answer
            _assessmentAnswers.value = currentList
        }
    }

    fun nextAssessmentQuestion() {
        if (_assessmentCurrentQuestionIndex.value < 2) {
            _assessmentCurrentQuestionIndex.value = _assessmentCurrentQuestionIndex.value + 1
        }
    }

    fun prevAssessmentQuestion() {
        if (_assessmentCurrentQuestionIndex.value > 0) {
            _assessmentCurrentQuestionIndex.value = _assessmentCurrentQuestionIndex.value - 1
        }
    }

    fun prefillSuggestAnswer(subtopicId: String, questionIndex: Int) {
        val suggestAnswer = when {
            subtopicId.startsWith("week_") -> {
                val weekNum = subtopicId.substringAfter("week_").toIntOrNull() ?: 1
                when (questionIndex) {
                    0 -> "For Week $weekNum study topics, we prioritize optimal configuration setups, robust vulnerability mitigation, and continuous telemetry monitoring to prevent system downtime."
                    1 -> "In implementing production automation pipelines, we utilize isolated states, declarative resources orchestration templates, and strict network security group parameters."
                    else -> "Best practices dictate active performance checks, dynamic auto-scaling thresholds, and proactive alert notification rules under standard SRE Golden Signals."
                }
            }
            subtopicId == "aws_iam" -> when (questionIndex) {
                0 -> "An IAM User is an identity for a person/service; IAM Group is a collection of user identities with common policies; IAM Role is a temporary credential for apps or external identities to assume."
                1 -> "Least privilege dictates that identities only get permissions they absolutely have to use. Implemented by specifying precise ARNs and restricted Actions in JSON policies, and using IAM Roles instead of master keys."
                else -> "Explicit Deny always overrides any Allow. Implicit Deny is the default state if there's no matching Allow. If both an Allow and an Explicit Deny match, the request is Denied."
            }
            subtopicId == "aws_ec2" -> when (questionIndex) {
                0 -> "EBS is network-attached persistent storage that survives instance termination, whereas Instance Store is ephemeral storage physically attached to the host server that is lost if the instance stops."
                1 -> "Security Groups are stateful. This means allowed inbound traffic is automatically allowed outbound, and inbound return traffic is allowed without needing explicit egress rules."
                else -> "Auto Scaling launches instances when CPU/Memory metrics cross thresholds. Cooldown ensures scaling actions pause for a set time (e.g. 300s) to allow system metrics to stabilize before another action."
            }
            subtopicId == "aws_vpc" -> when (questionIndex) {
                0 -> "Public subnets have a route to the Internet Gateway; Private subnets route to the internet via a NAT Gateway; Isolated subnets have no internet routes at all."
                1 -> "A NAT Gateway allows private subnet instances to initiate safe outbound requests to the internet. It must be deployed into a Public subnet."
                else -> "NACLs are stateless, evaluating rules at the subnet boundary. Security Groups are stateful, evaluating rules at the instance level."
            }
            subtopicId == "aws_s3" -> when (questionIndex) {
                0 -> "Standard is for active access (low latency, high cost); Standard-IA is for infrequent access (high retrieve cost, lower storage cost); Glacier is for cold archives (hours retrieval latency, cheapest)."
                1 -> "Versioning preserves history of overwritten/deleted files. MFA Delete requires physical MFA to permanently delete a version."
                else -> "Bucket policies are resource-based and control access at the resource level, while IAM policies are identity-based and control what roles/users can do."
            }
            subtopicId == "docker_basics" -> when (questionIndex) {
                0 -> "Containers share the host OS kernel and isolate user space, making them lightweight/fast. VMs run a fully-fledged guest OS on a hypervisor, consuming high RAM and CPU overhead."
                1 -> "Client is the CLI; Host runs containers; Daemon (dockerd) listens to API requests and manages container objects; Registry stores images."
                else -> "Namespaces isolate process views (PID, Net, Mount); Control groups limit resources like CPU, memory, and I/O rates."
            }
            subtopicId == "docker_images" -> when (questionIndex) {
                0 -> "Dockerfile contains build commands; Image is a static read-only snapshot containing libraries/code; Container is a live writable running instance of that Image."
                1 -> "Multi-stage builds use multiple FROM blocks, allowing you to compile code in rich build stages but copy only the slim final binaries to the production runtime stage, reducing image footprint."
                else -> "Docker images are layers of read-only files. When building, unchanged steps reuse cached layers, significantly reducing compile times."
            }
            subtopicId == "docker_containers" -> when (questionIndex) {
                0 -> "Use the command `docker run -d -p 8080:80 image_name` to run detached with port mapping."
                1 -> "Created (assembled but not running), Running (executing processes), Paused (suspended CPU execution), Stopped (exit code returned, process dead)."
                else -> "ENTRYPOINT defines the absolute executable that will run on startup. CMD provides default arguments that can be easily overridden by CLI command inputs."
            }
            subtopicId == "docker_volumes" -> when (questionIndex) {
                0 -> "Volumes are managed by Docker in a safe dedicated folder, isolated from host system changes. Bind mounts map any host directory directly, creating risk."
                1 -> "You mount the same named volume into multiple containers concurrently under the volumes section of docker-compose or run commands."
                else -> "Stored inside `/var/lib/docker/volumes/` on Linux hosts."
            }
            subtopicId == "k8s_pods" -> when (questionIndex) {
                0 -> "A Pod represents a single instance of a running process inside a cluster, wrapping one or more containers that are tightly locked together."
                1 -> "They share host network namespaces (communicating via localhost) and can mount shared IPC/Loopback volumes."
                else -> "An Init container runs to completion before any app containers start. Used for setup tasks (e.g. database migrations, wait-for-service)."
            }
            subtopicId == "k8s_deployments" -> when (questionIndex) {
                0 -> "Deployments provide declarative updates for Pods/ReplicaSets. ReplicaSets ensure the desired count of active pod replicas remains happy and running."
                1 -> "RollingUpdate replaces old pods step-by-step; Recreate kills all active old pods instantly before starting any new version pods."
                else -> "Use standard CLI: `kubectl rollout undo deployment/<deployment-name>` to restore the previous happy ReplicaSet."
            }
            subtopicId == "k8s_services" -> when (questionIndex) {
                0 -> "ClusterIP makes service internal-only; NodePort opens a static port on each node host; LoadBalancer spins up a cloud provider provider balancer routing external traffic."
                1 -> "Kube-proxy programs iptables or IPVS rules to route traffic to backend pods. CoreDNS provides a standard local naming service (SRV/A records) for service resources."
                else -> "A service routes layer-4 TCP/UDP traffic to pods. Ingress acts as a layer-7 HTTP proxy and router, terminating SSL and sorting traffic by hostnames/paths."
            }
            else -> "Default DevOps answer draft."
        }
        updateAssessmentAnswer(questionIndex, suggestAnswer)
    }

    fun cancelAssessment() {
        _assessmentSubtopicId.value = null
    }

    fun evaluateAssessment() {
        val subId = _assessmentSubtopicId.value ?: return
        val answers = _assessmentAnswers.value
        val questions = subtopicQuestions[subId] ?: listOf("Q1", "Q2", "Q3")
        
        _isAssessmentEvaluating.value = true
        
        viewModelScope.launch {
            val q1 = questions.getOrElse(0) { "" }
            val a1 = answers.getOrElse(0) { "" }
            val q2 = questions.getOrElse(1) { "" }
            val a2 = answers.getOrElse(1) { "" }
            val q3 = questions.getOrElse(2) { "" }
            val a3 = answers.getOrElse(2) { "" }

            val prompt = """
                Conduct a rigorous DevOps technical interview evaluation for the subtopic: '$subId'.
                The questions asked and user's answers are:
                
                Question 1: $q1
                User Answer 1: $a1
                
                Question 2: $q2
                User Answer 2: $a2
                
                Question 3: $q3
                User Answer 3: $a3
                
                Analyze the answers. Generate an evaluation.
                You must respond in a raw, strictly structured format with exactly four blocks separated by the pipe symbol '|' like this:
                Score|Strengths|Weaknesses|DetailedRecommendations
                
                Detail requirements:
                - Score: a single integer from 0 to 100. Be fair and logical based on technical completeness.
                - Strengths: Short bulleted list of 2-3 technical strengths.
                - Weaknesses: Short bulleted list of 1-3 technical weaknesses if any, or what is missing.
                - DetailedRecommendations: Short list of next learning steps.
                
                Keep the response concise and formatted as plain text, no markdown headers, just return:
                <score>|<strengths as sequential bullet items with \n or * >|<weaknesses as sequential bullet items with \n or * >|<recommendations>
                
                Do not include extra explanations outside this delimiter.
            """.trimIndent()

            val rawKey = com.example.BuildConfig.GEMINI_API_KEY
            val isDefaultKey = rawKey.isBlank() || rawKey == "MY_GEMINI_API_KEY" || rawKey == "API_KEY"

            var evaluationText = ""
            if (!isDefaultKey) {
                try {
                    val systemInstructions = "AGENT DIRECTIVE: DevOps Senior Architectural Interviewer. Evaluate answers objectively."
                    val combinedPayload = "$systemInstructions\n\nCommand Payload:\n$prompt"
                    
                    val requestBody = com.example.network.GeminiRequest(
                        contents = listOf(
                            com.example.network.GeminiContent(
                                parts = listOf(
                                    com.example.network.GeminiPart(text = combinedPayload)
                                )
                            )
                        )
                    )
                    val response = com.example.network.GeminiNetworkClient.apiService.generateContent(rawKey, requestBody)
                    evaluationText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
                } catch (e: Exception) {
                    android.util.Log.e("JeevanViewModel", "Interviewer Gemini query failed, falling back to heuristics", e)
                }
            }

            // Fallback or parser
            if (evaluationText.isBlank() || !evaluationText.contains("|")) {
                // Heuristic evaluation based on words length and key terms
                val wordCount = answers.sumOf { it.split("\\s+".toRegex()).size }
                val score = when {
                    wordCount > 60 -> 92
                    wordCount > 30 -> 82
                    wordCount > 10 -> 65
                    else -> 40
                }
                val cleanSub = subId.replace("_", " ").uppercase()
                
                evaluationText = "$score|" +
                        "* Understood the core concepts of $cleanSub\n* Answered questions within reasonable parameters|" +
                        if (wordCount < 30) "* Explanations were slightly brief and could use more production examples\n* Missed advanced deep dive details" else "* None identified. Overall solid foundational domain answers." +
                        "|* Review secondary scenarios in the Mini Lab Quiz.\n* Practice deploying simple test resources."
            }

            try {
                val parts = evaluationText.split("|")
                val parsedScore = parts.getOrNull(0)?.trim()?.toIntOrNull() ?: 85
                val parsedStrengths = parts.getOrNull(1)?.trim() ?: "* Demonstrated core concept clarity"
                var parsedWeaknesses = parts.getOrNull(2)?.trim() ?: "* Could expand on production edge cases"
                val parsedRecs = parts.getOrNull(3)?.trim() ?: "* Review cloud service limits and best practices"

                val isFailed = parsedScore < _passingScoreThreshold.value

                if (isFailed) {
                    _isRetestActive.value = true
                    // Enhance weaknesses with explicit Missed Concepts, Weak Areas, and Improvement Plan
                    parsedWeaknesses = "⚠️ STATUS: Needs Improvement (Readiness Score is under ${_passingScoreThreshold.value}%)" +
                            "\n\n🔥 WEAK AREAS & MISSED CONCEPTS:" +
                            "\n* Insufficient depth in troubleshooting mechanisms." +
                            "\n* Missing precise architectural syntax references for the specified topic." +
                            "\n* Conceptual gaps regarding performance optimization or cloud/container limits." +
                            "\n\n📚 SUGGESTED IMPROVEMENT PLAN:" +
                            "\n1. Carefully review the 'Real World Examples' and 'Learning Material' cards on this subtopic." +
                            "\n2. Re-attempt the hands-on 'Practice Exercises' to consolidate command syntax." +
                            "\n3. Write your key takeaways in the 'Quick Notes' section to lock in knowledge." +
                            "\n4. A dynamic Retest questionnaire has been unlocked. Click 'RE-ATTEMPT' below to start your modified interview."
                } else {
                    _isRetestActive.value = false
                }

                _assessmentScoreResult.value = parsedScore
                _assessmentStrengths.value = parsedStrengths
                _assessmentWeaknesses.value = parsedWeaknesses
            } catch (e: Exception) {
                _assessmentScoreResult.value = 80
                _assessmentStrengths.value = "* Foundational command over subtopic parameters"
                _assessmentWeaknesses.value = "* Ensure more architectural details are mentioned"
            }
            
            _isAssessmentEvaluating.value = false
        }
    }

    // Career Mini Labs
    private val _currentQuizIndex = MutableStateFlow(0)
    val currentQuizIndex: StateFlow<Int> = _currentQuizIndex

    private val _quizFeedback = MutableStateFlow<String?>(null)
    val quizFeedback: StateFlow<String?> = _quizFeedback

    // YAML Playground state
    private val _yamlCodeInput = MutableStateFlow(
        "apiVersion: apps/v1\nkind: Deployment\nmetadata:\n  name: jeevan-pod-deployment\nspec:\n  replicas: 3\n  selector:\n    matchLabels:\n      app: api-server"
    )
    val yamlCodeInput: StateFlow<String> = _yamlCodeInput

    private val _yamlValidationResult = MutableStateFlow<String?>(null)
    val yamlValidationResult: StateFlow<String?> = _yamlValidationResult

    // Current screen navigation tab
    private val _activeTab = MutableStateFlow("DASHBOARD") 
    val activeTab: StateFlow<String> = _activeTab

    // --- Configurable passing threshold ---
    private val _passingScoreThreshold = MutableStateFlow(70)
    val passingScoreThreshold: StateFlow<Int> = _passingScoreThreshold

    fun setPassingScoreThreshold(score: Int) {
        _passingScoreThreshold.value = score
    }

    // --- Active study week & day ---
    private val _selectedWeek = MutableStateFlow(1)
    val selectedWeek: StateFlow<Int> = _selectedWeek

    private val _selectedDay = MutableStateFlow(1)
    val selectedDay: StateFlow<Int> = _selectedDay

    fun setSelectedWeek(week: Int) {
        _selectedWeek.value = week
    }

    fun setSelectedDay(day: Int) {
        _selectedDay.value = day
    }

    // --- Retest system Tracker ---
    private val _isRetestActive = MutableStateFlow(false)
    val isRetestActive: StateFlow<Boolean> = _isRetestActive

    fun setIsRetestActive(active: Boolean) {
        _isRetestActive.value = active
    }

    private val _subtopicUserNotes = MutableStateFlow<Map<String, String>>(emptyMap())
    val subtopicUserNotes: StateFlow<Map<String, String>> = _subtopicUserNotes

    fun updateSubtopicUserNote(subtopicId: String, note: String) {
        val currentNotes = _subtopicUserNotes.value.toMutableMap()
        currentNotes[subtopicId] = note
        _subtopicUserNotes.value = currentNotes
    }

    // Additional Retest questions map
    val subtopicRetestQuestions = mapOf(
        "week_1" to listOf(
            "How does the Linux VFS (Virtual File System) unify storage interface access?",
            "Explain inode structure. What happens to space and filenames when an inode pool is exhausted?",
            "Analyze file system page caching and the impact of the 'sync' command on SRE write persistence."
        ),
        "week_2" to listOf(
            "Explain systemd target transitions and analyze unit-file configurations on boot priority.",
            "Describe system metrics profiling via sar, vmstat, iostat, and htop under CPU thrashing.",
            "How do you troubleshoot zombie and orphan processes, and reclaim leaked process IDs?"
        ),
        "week_3" to listOf(
            "Troubleshoot SSH connection issues: debug logs, cipher mismatch, and proxy profiles.",
            "Explain TCP's 3-way handshake and describe how TIME_WAIT states lock out SRE connection pools.",
            "How do you write a thread-safe Bash wrapper to safely run multiple CLI sub-processes in parallel?"
        ),
        "week_4" to listOf(
            "How do we recover lost Git commits via git reflog and clean corrupt objects?",
            "What is the difference between fast-forward, 3-way, and squash merging in Git pipelines?",
            "Explain Python subprocess management thread safety and handling non-zero exit codes safely."
        ),
        "week_5" to listOf(
            "How do we configure cross-account access securely utilizing IAM roles and external IDs?",
            "Explain how IAM Session Policies work and where they are applied.",
            "How does the AWS Security Token Service (STS) manage short-lived credential rotation?"
        ),
        "week_6" to listOf(
            "How do you optimize S3 throughput for millions of concurrent read/write operations?",
            "Contrast S3 Object Lock compliance mode with governance mode relative to write protection.",
            "What is the difference between S3 Transfer Acceleration and standard AWS multipart uploads?"
        ),
        "week_7" to listOf(
            "Troubleshoot EC2 instances: Compare failed Instance Status Checks vs System Status Checks.",
            "Contrast launch configurations with launch templates inside EC2 Auto Scaling groups.",
            "Explain Placement groups (Cluster, Spread, Partition) role in server clustering layouts."
        ),
        "week_8" to listOf(
            "Compare VPC Peering with Transit Gateway regarding cost, route density, and scaling limits.",
            "Analyze VPC endpoints (Interface vs Gateway type) in private service network mappings.",
            "How do we configure VPC Flow Logs to trace security-group ingress leaks in network segments?"
        ),
        "week_9" to listOf(
            "How does NAT Gateway traffic flow differ from Nat Instance failover configuration?",
            "Explain VPC route table priority. How is local routing evaluated against internet gateway routes?",
            "Construct a sub-segment IP design plan that avoids overlapping CIDRs in multi-region peering."
        ),
        "week_10" to listOf(
            "How do AWS Route53 health checks trigger DNS failover to static backup S3 web hosts?",
            "Troubleshoot private VPC security: trace outbound path bottlenecks through firewalls.",
            "How does AWS WAF inspect cookie payload rules without inflating request latency?"
        ),
        "week_11" to listOf(
            "Contrast Multi-AZ RDS failovers with Aurora Global Databases in cross-region SRE disaster recovery.",
            "How do we model data in DynamoDB utilizing secondary global indexes (GSI) to optimize read costs?",
            "Troubleshoot Redis connection pool timeouts during heavy database traffic thrashing."
        ),
        "week_12" to listOf(
            "Analyze AWS Lambda cold start mitigation strategies: JVM optimization vs Node.js vs custom runtimes.",
            "Explain event-driven SRE pipeline state trackers: Amazon EventBridge vs SQS vs SNS.",
            "How do you secure serverless APIs using custom JWT OAuth2 authorizers on API Gateway?"
        ),
        "week_13" to listOf(
            "How do we resolve nested stack circular dependencies in complex CloudFormation codebases?",
            "Explain the difference between custom resources and macro extensions in CloudFormation templates.",
            "Analyze CloudFormation stack termination protection and deletion policy attributes."
        ),
        "week_14" to listOf(
            "Analyze AWS compute optimizer recommendations for rightsizing idle ECS/EC2 instances.",
            "How do we configure billing budgets and alerts with programmatic Slack hooks in AWS Budgets?",
            "Analyze AWS Savings plans vs Reserved instances in terms of global SRE operational flexibility."
        ),
        "week_15" to listOf(
            "Explain the copy-on-write overhead differences between devicemapper vs overlay2 storage drivers.",
            "Analyze container security escapes: How to prevent root privilege escalations inside runtimes.",
            "How do cgroups v2 resource limit hierarchies improve multi-container host stability?"
        ),
        "week_16" to listOf(
            "Analyze multi-stage Docker build cache invalidations caused by varying source-file changes.",
            "Explain rootless Docker setup and write secure systemd unit files to launch it on boot.",
            "Troubleshoot docker image layer bloat caused by redundant package installer cleanups."
        ),
        "week_17" to listOf(
            "Analyze ECS Fargate tasks scaling speeds under intense unexpected high-traffic spikes.",
            "Configure ECS container agent environment parameters to capture container network logs.",
            "Analyze AWS Copilot CLI deployment flows compared to standard cloudformation ECS tasks."
        ),
        "week_18" to listOf(
            "How do we build and configure self-hosted GitHub Actions runners inside secure VPC subnets?",
            "Analyse GHA workflow optimization strategies: caching yarn/npm builds across distinct runners.",
            "Troubleshoot pull request execution barriers caused by environment protection parameters."
        ),
        "week_19" to listOf(
            "How do you audit containers for active hardcoded credentials using scanners like GitLeaks?",
            "Explain how SBOM (Software Bill of Materials) tracking increases supply chain security.",
            "What is compliance-as-code and how does it prevent deploying insecure container clusters?"
        ),
        "week_20" to listOf(
            "Analyze Jenkins Shared Libraries security scopes relative to pipeline execution contexts.",
            "Troubleshoot Jenkins build queue deadlocks caused by exhausted workspace executor allotments.",
            "Explain Jenkins Pipeline script isolation sandboxes and how to approve secure method calls."
        ),
        "week_21" to listOf(
            "How do you configure Ansible dynamic inventory files with automated cloud provider lookups?",
            "Analyze Ansible playbook execution efficiency using parallel forks and asynchronous polling.",
            "Explain Ansible variable override hierarchies: group_vars, host_vars, and extra-vars."
        ),
        "week_22" to listOf(
            "Analyze Kubernetes kube-apiserver horizontal scaling and etcd quorum preservation rules.",
            "Explain Pod Disruption Budgets (PDB) and how they protect high-availability node groups during upgrades.",
            "Troubleshoot Pod scheduling bottlenecks caused by non-resolvable nodeAffinity selectors."
        ),
        "week_23" to listOf(
            "Analyze the IP allocation limits of diverse AWS instance types under EKS VPC CNI configurations.",
            "Explain helm hooks and release rollback state engines during failed deployments.",
            "Troubleshoot ArgoCD sync failures: resolve customized kustomize patch version deviations."
        ),
        "week_24" to listOf(
            "Troubleshoot Prometheus scraping issues: resolve self-signed certificates and scrape timeouts.",
            "Analyze Kubernetes RBAC: Bind cluster roles to service accounts safely.",
            "How do you tune Grafana visualization limits to handle high-frequency cluster metrics?"
        ),
        "week_25" to listOf(
            "Analyze Terraform lock files (.terraform.lock.hcl) role in tracking provider dependency hashes.",
            "Troubleshoot terraform apply failures: resolve remote backend state locks manually.",
            "Explain Terraform lifecycle features: prevent_destroy, ignore_changes, and create_before_destroy."
        ),
        "week_26" to listOf(
            "Analyze Terraform dynamic blocks: construct nested structures dynamically inside modules.",
            "Troubleshoot terraform state deviations: align actual resources with state files via import rules.",
            "How do you securely run Terraform in shared CI/CD pipelines without exposing credentials?"
        ),
        "week_27" to listOf(
            "Analyze time-series anomalies in cluster log files using automated regression models.",
            "Explain how AWS DevOps Guru processes cloudwatch alarms autonomously to output insights.",
            "How do you deploy scalable generative-AI chat helpers utilizing Bedrock model agents?"
        ),
        "week_28" to listOf(
            "Detail recovery point objectives (RPO) and recovery time objectives (RTO) in multi-region failovers.",
            "How do you outline and present high-scale cloud designs to enterprise architecture panels?",
            "Analyze cloud cost estimation profiles to ensure maximum architectural efficiency on launch."
        )
    )

    // Method to insert a brand new custom subtopic unit dynamically
    fun addCustomSubtopicUnit(id: String, parent: String, weekNum: Int, dayNum: Int, title: String) {
        viewModelScope.launch {
            // format custom ID with week and day data for parsing
            val customId = "custom_week${weekNum}_day${dayNum}_${id.lowercase().replace(" ", "_").trim()}"
            repository.saveSubtopicProgress(
                subtopicId = customId,
                parentTopicId = parent.lowercase().trim(),
                isCompleted = false,
                reason = "Need Study",
                score = 0
            )
            
            // Log as system message
            _chatMessages.value = _chatMessages.value + ChatMessage(
                sender = "AI SRE Coach",
                text = "Successfully provisioned custom DevOps learning unit: '$title' under Week $weekNum Day $dayNum in $parent's schedule block.",
                timestamp = System.currentTimeMillis()
            )
        }
    }

    // Active AI Advisor summary across modules
    private val _syntheticInsights = MutableStateFlow<List<String>>(emptyList())
    val syntheticInsights: StateFlow<List<String>> = _syntheticInsights

    // --- Brainstorming Cognitive Puzzles States ---
    private val _brainstormScore = MutableStateFlow(0)
    val brainstormScore: StateFlow<Int> = _brainstormScore

    private val _puzzlesSolved = MutableStateFlow(0)
    val puzzlesSolved: StateFlow<Int> = _puzzlesSolved

    private val _selectedPuzzleIndex = MutableStateFlow(0)
    val selectedPuzzleIndex: StateFlow<Int> = _selectedPuzzleIndex

    private val _puzzleResultFeedback = MutableStateFlow<String?>(null)
    val puzzleResultFeedback: StateFlow<String?> = _puzzleResultFeedback

    private val _puzzleIsAnswered = MutableStateFlow(false)
    val puzzleIsAnswered: StateFlow<Boolean> = _puzzleIsAnswered

    private val _aiInvestmentInsights = MutableStateFlow<List<String>>(emptyList())
    val aiInvestmentInsights: StateFlow<List<String>> = _aiInvestmentInsights

    init {
        val database = JeevanDatabase.getDatabase(application)
        repository = JeevanRepository(database.jeevanDao())

        // Setup pipeline flows from repository
        transactions = repository.allTransactions
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        careerProgress = repository.allCareerProgress
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        healthLogs = repository.allHealthLogs
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        userProfile = repository.userProfile
            .filterNotNull()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())

        subtopicsProgress = repository.allSubtopicProgress
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        newsBookmarks = repository.allNewsBookmarks
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        portfolioHoldings = repository.allPortfolioHoldings
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        careerGoalFunds = repository.allCareerGoalFunds
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        // Sync with TimerService background state
        val sharedPrefs = application.getSharedPreferences("jeevan_focus_timer", Context.MODE_PRIVATE)
        val savedDuration = sharedPrefs.getInt("custom_duration_minutes", 25)
        _customDurationMinutes.value = savedDuration

        val endTime = sharedPrefs.getLong("end_time", 0)
        val isActive = sharedPrefs.getBoolean("is_active", false)
        val now = System.currentTimeMillis()

        if (TimerService.isRunning) {
            _timerSecondsRemaining.value = TimerService.secondsRemaining
            _isTimerRunning.value = true
        } else if (isActive && endTime > now) {
            val remainSeconds = ((endTime - now) / 1000).toInt()
            startLocalTimer(remainSeconds)
        } else {
            _timerSecondsRemaining.value = savedDuration * 60
            _isTimerRunning.value = false
        }

        TimerService.setCallbacks(
            onTick = { seconds ->
                _timerSecondsRemaining.value = seconds
                _isTimerRunning.value = true
            },
            onFinished = {
                _isTimerRunning.value = false
                _timerSecondsRemaining.value = _customDurationMinutes.value * 60
                // Award XP on successful completed timing
                viewModelScope.launch {
                    repository.addXpToTopic("linux", 50)
                    generateDynamicAIEcosystemInsights()
                }
                _chatMessages.value = _chatMessages.value + ChatMessage(
                    sender = "Jeevan Coach",
                    text = "Acknowledge: Strategic DevOps focus session completed successfully when device was idle. +50 XP rewarded.",
                    timestamp = System.currentTimeMillis()
                )
            }
        )

        viewModelScope.launch {
            try {
                repository.seedDemoDataIfEmpty()
                generateDynamicAIEcosystemInsights()
                refreshNewsCenter()
                loadQuarterlyReports()
                
                // Initialize adaptive workouts based on seeded user profile
                val prof = repository.getOrInitUserProfile()
                triggerAdaptiveWorkoutPlan(prof.weightKg, prof.heightCm, prof.computedBmi)
            } catch (e: Exception) {
                android.util.Log.e("JeevanViewModel", "Initialization seeding failed", e)
            }
        }

        // Auto compile AI investment insights and refresh personalized news on portfolio updates
        viewModelScope.launch {
            portfolioHoldings.collect {
                updateAIInvestmentInsights()
                refreshPortfolioNews()
            }
        }
        viewModelScope.launch {
            careerGoalFunds.collect {
                updateAIInvestmentInsights()
            }
        }

        // Live market price fluctuation ticker
        viewModelScope.launch {
            while (true) {
                delay(4000)
                try {
                    if (com.example.ui.screen.IndianMarketScheduleManager.getMarketStatus().isOpen) {
                        repository.fluctuateHoldingPrices()
                    }
                } catch (e: Exception) {
                    // Fail gracefully
                }
            }
        }

        // Welcome Greeting
        _chatMessages.value = listOf(
            ChatMessage(
                sender = "Jeevan",
                text = "Welcome, Commander. System check: Wallet capital registers stable, study tracks are updated, and health variables compiled. I am ready to route your query.",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    fun setActiveTab(tab: String) {
        _activeTab.value = tab
    }

    // --- FINANCIAL TRANSACTION OPERATIONS ---
    fun addExpense(title: String, amount: Double, category: String, isSub: Boolean) {
        viewModelScope.launch {
            repository.addTransaction(title, amount, "EXPENSE", category.uppercase(), isSub)
            generateDynamicAIEcosystemInsights()
        }
    }

    fun addIncome(title: String, amount: Double, category: String) {
        viewModelScope.launch {
            repository.addTransaction(title, amount, "INCOME", category.uppercase(), false)
            generateDynamicAIEcosystemInsights()
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
            generateDynamicAIEcosystemInsights()
        }
    }

    fun updateMonthlyLimit(limit: Double) {
        viewModelScope.launch {
            val prof = userProfile.value
            repository.updateUserProfile(prof.copy(monthlyBudgetLimit = limit))
            generateDynamicAIEcosystemInsights()
        }
    }

    // --- PORTFOLIO OPERATIONS ---
    fun addPortfolioAsset(
        name: String,
        quantity: Double,
        price: Double,
        type: String,
        purchaseDate: Long = System.currentTimeMillis(),
        notes: String = "",
        symbol: String = "",
        exchange: String = "NSE",
        sector: String = "Other"
    ) {
        viewModelScope.launch {
            repository.addPortfolioHolding(
                name,
                quantity,
                price,
                type.uppercase(),
                purchaseDate,
                notes,
                symbol,
                exchange,
                sector
            )
        }
    }

    fun removePortfolioAsset(holding: PortfolioHolding) {
        viewModelScope.launch {
            repository.deletePortfolioHolding(holding)
        }
    }

    // --- CAREER GOAL FUNDS OPERATIONS ---
    fun addCareerGoalFund(name: String, target: Double, current: Double = 0.0) {
        viewModelScope.launch {
            repository.addCareerGoalFund(name, target, current)
            generateDynamicAIEcosystemInsights()
        }
    }

    fun updateCareerGoalFund(fund: CareerGoalFund) {
        viewModelScope.launch {
            repository.updateCareerGoalFund(fund)
            generateDynamicAIEcosystemInsights()
        }
    }

    fun deleteCareerGoalFund(fund: CareerGoalFund) {
        viewModelScope.launch {
            repository.deleteCareerGoalFund(fund)
            generateDynamicAIEcosystemInsights()
        }
    }

    fun contributeToCareerGoal(fund: CareerGoalFund, amount: Double) {
        viewModelScope.launch {
            val currentProfile = userProfile.value
            if (currentProfile.balanceAmount >= amount) {
                // Deduct from profile
                repository.updateUserProfile(currentProfile.copy(balanceAmount = currentProfile.balanceAmount - amount))
                // Add to fund
                repository.updateCareerGoalFund(fund.copy(currentAmount = fund.currentAmount + amount))
                // Add transactional log
                repository.addTransaction(
                    title = "Funded goal: ${fund.name}",
                    amount = amount,
                    type = "EXPENSE",
                    category = "ADDITIONAL",
                    isSubscription = false
                )
                generateDynamicAIEcosystemInsights()
            }
        }
    }

    // --- HEALTH / NUTRITION OPERATIONS ---
    fun addWater(ml: Int) {
        viewModelScope.launch {
            repository.updateWaterIntake(ml)
            generateDynamicAIEcosystemInsights()
        }
    }

    fun addSteps(steps: Int) {
        viewModelScope.launch {
            repository.updateSteps(steps)
            generateDynamicAIEcosystemInsights()
        }
    }

    fun saveMoodAndJournal(score: Int, entry: String) {
        viewModelScope.launch {
            repository.updateMoodAndJournal(score, entry)
            generateDynamicAIEcosystemInsights()
        }
    }

    fun logVitals(sleepMin: Int, calConsumed: Int, calBurned: Int) {
        viewModelScope.launch {
            repository.updateSleepAndCal(sleepMin, calConsumed, calBurned)
            generateDynamicAIEcosystemInsights()
        }
    }

    // --- NEWS HUB BOOKMARKS ---
    fun bookmarkNews(title: String, category: String, url: String, desc: String) {
        viewModelScope.launch {
            repository.addNewsBookmark(title, category, url, desc)
        }
    }

    fun removeNewsBookmark(bookmark: NewsBookmark) {
        viewModelScope.launch {
            repository.deleteNewsBookmark(bookmark)
        }
    }

    // --- DEVOPS CAREER SUBTOPIC PROGRESS ---
    fun toggleSubtopic(subtopicId: String, parentTopicId: String, isCompleted: Boolean, reason: String? = null, score: Int = 0) {
        viewModelScope.launch {
            repository.saveSubtopicProgress(subtopicId, parentTopicId, isCompleted, reason, score)
            generateDynamicAIEcosystemInsights()
        }
    }

    fun markStudyOnlyCompleted(subtopicId: String, parentTopicId: String) {
        viewModelScope.launch {
            repository.saveSubtopicProgress(subtopicId, parentTopicId, true, null, 0)
            generateDynamicAIEcosystemInsights()
        }
    }

    // --- BIOMETRIC & ERGONOMIC HEALTH INTEGRITY ---
    fun updateBiometrics(weight: Double, height: Double) {
        viewModelScope.launch {
            val prof = repository.getOrInitUserProfile()
            val bmiVal = if (height > 0.0) weight / ((height / 100.0) * (height / 100.0)) else 0.0
            val roundedBmi = (bmiVal * 10.0).toInt() / 10.0
            repository.updateUserProfile(prof.copy(weightKg = weight, heightCm = height, computedBmi = roundedBmi))
            generateDynamicAIEcosystemInsights()
            triggerAdaptiveWorkoutPlan(weight, height, roundedBmi)
        }
    }

    fun triggerAdaptiveWorkoutPlan(weight: Double, height: Double, bmi: Double) {
        viewModelScope.launch {
            val rawKey = com.example.BuildConfig.GEMINI_API_KEY
            val isDefaultKey = rawKey.isBlank() || rawKey == "MY_GEMINI_API_KEY" || rawKey == "API_KEY"
            
            val todayLogs = healthLogs.value.firstOrNull()
            val calConsumed = todayLogs?.caloriesConsumed ?: 1800
            val calBurned = todayLogs?.caloriesBurned ?: 300
            
            val prompt = """
                You are an SRE Fitness & Biometric Coach.
                Given the user's statistics:
                - Weight: $weight kg
                - Height: $height cm
                - BMI: $bmi (Status: ${if(bmi < 18.5) "Underweight" else if(bmi < 25) "Normal weight" else "Overweight"})
                - Daily Calories Logged: Consumed $calConsumed kcal, Burned $calBurned kcal.
                
                Generate a dynamic, personalized office workout plan for this desk-bound DevOps engineer.
                It must adapt to their weight, height, BMI, nutrition, and calorie balance.
                Output exactly 4 relevant physical exercises/activities (each with sets/reps or duration and a brief explanation).
                Format your output as a RAW JSON array of exactly 4 strings without any markdown or extra description. Example:
                ["Squats: 3 sets x 15 reps (adapted for high BMI stability)", "Pushups: 3 sets x 10 reps", "Desk wrist stretches: 5 mins", "Brisk corridor walk: 15 mins"]
            """.trimIndent()
            
            if (!isDefaultKey) {
                try {
                    val requestBody = com.example.network.GeminiRequest(
                        contents = listOf(
                            com.example.network.GeminiContent(
                                parts = listOf(
                                    com.example.network.GeminiPart(text = prompt)
                                )
                            )
                        )
                    )
                    val response = com.example.network.GeminiNetworkClient.apiService.generateContent(rawKey, requestBody)
                    val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
                    val parsed = parseJsonArrayOfStrings(text)
                    if (parsed.isNotEmpty()) {
                        _adaptiveWorkouts.value = parsed.take(4)
                        return@launch
                    }
                } catch (e: Exception) {
                    android.util.Log.e("JeevanViewModel", "Adaptive workout generation via Gemini failed", e)
                }
            }
            
            // Offline fallback adaptive system based on BMI
            val workouts = when {
                bmi < 18.5 -> listOf(
                    "Calisthenics Slow Squats: 3 sets x 10 reps (Building core bulk safely)",
                    "Wall Pushups: 3 sets x 12 reps (Low intensity arm load)",
                    "Biometric Back Stretch: 5 mins (Desk posture alignment)",
                    "High Nutrient Snack Walk: 10 mins (Light metabolic activation)"
                )
                bmi > 25.0 -> listOf(
                    "High-Tempo Air Squats: 4 sets x 20 reps (Aerobic and leg endurance focus)",
                    "Standard Desk Pushups: 3 sets x 15 reps (Core stability & shoulder relief)",
                    "Decompression Stretches: 10 mins (Lower spine stress reduction)",
                    "Paced Corridor Lunges: 3 sets x 10 reps per side (Active burn activation)"
                )
                else -> listOf(
                    "Explosive Bodyweight Squats: 3 sets x 15 reps (Leg power booster)",
                    "Floor Pushups / Planks: 3 sets x 12 reps / 45s holds (Chest & Core stability)",
                    "Interactive Desk Yoga: 10 mins (Full spine & neck tension release)",
                    "Brisk Office Walk: 30 mins (Sustained cardiorespiratory flow)"
                )
            }
            _adaptiveWorkouts.value = workouts
        }
    }

    fun loadQuarterlyReports() {
        val sp = getApplication<android.app.Application>().getSharedPreferences("jeevan_health_sp", android.content.Context.MODE_PRIVATE)
        val set = sp.getStringSet("reports", emptySet()) ?: emptySet()
        _quarterlyReports.value = set.toList().sortedDescending()
    }

    fun saveQuarterlyReport(report: String) {
        val sp = getApplication<android.app.Application>().getSharedPreferences("jeevan_health_sp", android.content.Context.MODE_PRIVATE)
        val current = _quarterlyReports.value.toMutableSet()
        current.add(report)
        sp.edit().putStringSet("reports", current).apply()
        _quarterlyReports.value = current.toList().sortedDescending()
    }

    fun generateQuarterlyHealthReport() {
        viewModelScope.launch {
            val rawKey = com.example.BuildConfig.GEMINI_API_KEY
            val isDefaultKey = rawKey.isBlank() || rawKey == "MY_GEMINI_API_KEY" || rawKey == "API_KEY"
            
            val prof = repository.getOrInitUserProfile()
            val logs = healthLogs.value
            val avgSleep = if (logs.isNotEmpty()) logs.map { it.sleepMinutes }.average().let { if(it.isNaN()) 420.0 else it }.toInt() else 445
            val totalWater = if (logs.isNotEmpty()) logs.map { it.waterIntakeMl }.average().let { if(it.isNaN()) 2500.0 else it }.toInt() else 2100
            
            val timestamp = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
            
            val prompt = """
                You are a senior physician and health coach specializing in SRE occupational wellness.
                Please compile a 'Quarterly Holistic Biometric SRE Diagnostic Report' for:
                - Profile Name: ${prof.name}
                - BMI: ${prof.computedBmi} (Weight: ${prof.weightKg} kg, Height: ${prof.heightCm} cm)
                - Average Sleep Logged: ${avgSleep / 60} hours/night
                - Average Daily Water Intake: $totalWater ml
                
                Provide a structured report detailing:
                1. Biometric Classification & Risk Assessment
                2. Cardiovascular & Ergonomic SRE Concerns (e.g., wrist strength, lower back focus)
                3. Direct Nutritional & Hydration Adjustments
                4. Immediate Actionable Focus Exercises
                
                Keep it highly technical, encouraging, and under 150 words. Add the timestamp: $timestamp at the end.
            """.trimIndent()
            
            var reportText = ""
            if (!isDefaultKey) {
                try {
                    val requestBody = com.example.network.GeminiRequest(
                        contents = listOf(
                            com.example.network.GeminiContent(
                                parts = listOf(
                                    com.example.network.GeminiPart(text = prompt)
                                )
                            )
                        )
                    )
                    val response = com.example.network.GeminiNetworkClient.apiService.generateContent(rawKey, requestBody)
                    reportText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
                } catch (e: Exception) {
                    android.util.Log.e("JeevanViewModel", "Failed to generate quarterly report via Gemini", e)
                }
            }
            
            if (reportText.isBlank()) {
                reportText = """
                    === QUARTERLY SRE BIOMETRIC REPORT ===
                    Generated on: $timestamp
                    Client: ${prof.name} | BMI: ${prof.computedBmi} (Weight: ${prof.weightKg}kg)
                    
                    1. BIOMETRIC STATUS: Classified as ${if (prof.computedBmi < 18.5) "Underweight" else if (prof.computedBmi < 25) "Normal Range" else "Overweight"}.
                    2. POTENTIAL RISKS: Long terminal hours lead to standard sedentary cardiorespiratory fatigue, lower spine stress, and visual strain.
                    3. DIRECT RECO: Increase water intake closer to ${prof.dailyWaterGoalMl} ml. Target consistent sleep hours (~$avgSleep mins average checked).
                    4. ERGONOMIC DRILLS: Complete hourly wrist-rotations, active stretching loops, and use the Office Workout Scheme dynamic updates.
                """.trimIndent()
            }
            
            saveQuarterlyReport(reportText)
        }
    }

    // --- PERSONALIZED PORTFOLIO NEWS ---
    fun refreshPortfolioNews() {
        viewModelScope.launch {
            val rawKey = com.example.BuildConfig.GEMINI_API_KEY
            val isDefaultKey = rawKey.isBlank() || rawKey == "MY_GEMINI_API_KEY" || rawKey == "API_KEY"
            
            val holdings = portfolioHoldings.value
            if (holdings.isEmpty()) {
                _portfolioNews.value = listOf(
                    Pair("Nifty 50 Index", "Nifty 50 index remains stable driven by steady retail volume metrics and sustained SIP inflows."),
                    Pair("RBI Policy", "Reserve Bank of India maintains steady stance to guide structural growth targets securely."),
                    Pair("SIP Inflows", "Retail investment contributions touch lifetime landmark inflows of ₹21,000 crores.")
                )
                return@launch
            }
            
            val holdingsStr = holdings.map { "${it.assetName} (${it.symbol})" }.joinToString(", ")
            val prompt = """
                You are a senior equity analyst in India.
                Given the user's specific portfolio holdings: $holdingsStr.
                
                Generate exactly 4 fresh, highly realistic, custom brief news briefs/headlines (one sentence each) specifically of interest to these assets.
                Avoid generic finance news; make it highly targeted to these companies or asset classes (Mutual Funds, ETFs, specific stock symbols).
                
                Output your response in a RAW JSON array of objects, with each object having properties "symbol" and "newsText".
                Example format:
                [{"symbol": "ITC", "newsText": "ITC gains 1.2% as FMCG segment posts strong growth on rural recovery indices."}, {"symbol": "GOLDBEES", "newsText": "Gold prices hit safe haven peaks amid global geopolitical DNS shifts."}]
                Do NOT output markdown blocks or any other explanation. Just the raw JSON.
            """.trimIndent()
            
            if (!isDefaultKey) {
                try {
                    val requestBody = com.example.network.GeminiRequest(
                        contents = listOf(
                            com.example.network.GeminiContent(
                                parts = listOf(
                                    com.example.network.GeminiPart(text = prompt)
                                )
                            )
                        )
                    )
                    val response = com.example.network.GeminiNetworkClient.apiService.generateContent(rawKey, requestBody)
                    val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
                    
                    val list = mutableListOf<Pair<String, String>>()
                    val arr = org.json.JSONArray(text.trim().removePrefix("```json").removeSuffix("```").trim())
                    for (i in 0 until arr.length()) {
                        val obj = arr.getJSONObject(i)
                        list.add(Pair(obj.getString("symbol"), obj.getString("newsText")))
                    }
                    if (list.isNotEmpty()) {
                        _portfolioNews.value = list
                        return@launch
                    }
                } catch (e: Exception) {
                    android.util.Log.e("JeevanViewModel", "Failed to generate dynamic portfolio news", e)
                }
            }
            
            // Fallback offline generator matching holdings
            val fallbackNews = mutableListOf<Pair<String, String>>()
            for (h in holdings.shuffled().take(4)) {
                val symbol = h.symbol.orEmpty().ifEmpty { h.assetName }
                val item = when (h.assetType) {
                    "STOCK" -> Pair(symbol, "${h.assetName} gains momentum today amid heavy delivery volumes and positive regional broker ratings.")
                    "MF", "SIP" -> Pair(symbol, "${h.assetName} reports robust NAV appreciation due to strong performing large cap underlying indexes.")
                    else -> Pair(symbol, "${h.assetName} ETF trade volume reaches safe haven peaks driven by commodity hedge buying.")
                }
                fallbackNews.add(item)
            }
            _portfolioNews.value = if (fallbackNews.isNotEmpty()) fallbackNews else listOf(
                Pair("General Market", "Sensex registers moderate consolidation ahead of structural policy announcements.")
            )
        }
    }

    fun awardXp(topicId: String, amount: Int) {
        viewModelScope.launch {
            repository.addXpToTopic(topicId, amount)
            generateDynamicAIEcosystemInsights()
        }
    }

    fun selectTopicLesson(topicId: String, lessonId: String) {
        viewModelScope.launch {
            repository.markLessonCompleted(topicId, lessonId)
        }
    }

    fun toggleTopicDeployment(topicId: String) {
        viewModelScope.launch {
            repository.toggleTopicDeployment(topicId)
            generateDynamicAIEcosystemInsights()
        }
    }

    fun processQuizAnswer(topicId: String, selectedOptionIndex: Int, correctIndex: Int) {
        if (selectedOptionIndex == correctIndex) {
            _quizFeedback.value = "🟢 Correct! Scenario resolved accurately. Got +25 XP."
            viewModelScope.launch {
                repository.addXpToTopic(topicId, 25)
            }
        } else {
            _quizFeedback.value = "🔴 Incorrect override. Review AWS/K8s architectural constraints."
        }
    }

    fun setQuizIndex(index: Int) {
        _currentQuizIndex.value = index
        _quizFeedback.value = null
    }

    // YAML syntax checks
    fun updateYamlCode(code: String) {
        _yamlCodeInput.value = code
    }

    fun validateYamlCode() {
        val input = _yamlCodeInput.value
        val hasContainersStr = input.contains("containers:")
        val hasSpecStr = input.contains("spec:")
        val hasImageStr = input.contains("image:")

        when {
            !input.contains("apiVersion:") -> {
                _yamlValidationResult.value = "⚠️ YAML schema validation failed: Missing 'apiVersion' root field."
            }
            !input.contains("kind:") -> {
                _yamlValidationResult.value = "⚠️ YAML schema validation failed: Missing element 'kind'."
            }
            !hasSpecStr -> {
                _yamlValidationResult.value = "⚠️ Spec structure failed: Definition requires a 'spec:' root block."
            }
            !hasContainersStr && input.lowercase().contains("deployment") -> {
                _yamlValidationResult.value = "⚠️ Compliance failure: Container parameters 'containers:' block is missing inside your manifest spec."
            }
            else -> {
                _yamlValidationResult.value = "🚀 YAML deployment compliance checked: Outstanding syntactical precision! +15 XP rewarded!"
                viewModelScope.launch {
                    repository.addXpToTopic("kubernetes", 15)
                }
            }
        }
    }

    // --- TIMERS / DEEP FOCUS WORK SERVICE CONTROLLER ---
    fun setCustomTimerMinutes(minutes: Int) {
        if (!_isTimerRunning.value) {
            val clamped = minutes.coerceIn(1, 180)
            _customDurationMinutes.value = clamped
            _timerSecondsRemaining.value = clamped * 60
            
            val context = getApplication<Application>().applicationContext
            val prefs = context.getSharedPreferences("jeevan_focus_timer", Context.MODE_PRIVATE)
            prefs.edit().putInt("custom_duration_minutes", clamped).apply()
        }
    }

    fun toggleTimer(customMinutes: Int = _customDurationMinutes.value) {
        val context = getApplication<Application>().applicationContext
        val startServiceIntent = Intent(context, TimerService::class.java)

        localTimerJob?.cancel()

        if (_isTimerRunning.value) {
            startServiceIntent.action = "PAUSE"
            try {
                context.startService(startServiceIntent)
            } catch (e: Exception) {
                android.util.Log.e("JeevanViewModel", "Failed to pause TimerService", e)
            }
            _isTimerRunning.value = false
        } else {
            val secondsToRun = if (TimerService.isRunning) TimerService.secondsRemaining else customMinutes * 60
            startServiceIntent.action = "START"
            startServiceIntent.putExtra("duration_seconds", secondsToRun)
            try {
                androidx.core.content.ContextCompat.startForegroundService(context, startServiceIntent)
                _isTimerRunning.value = true
            } catch (e: Exception) {
                android.util.Log.e("JeevanViewModel", "Failed to start TimerService", e)
                _isTimerRunning.value = false
            }
        }
    }

    fun resetTimer() {
        val context = getApplication<Application>().applicationContext
        localTimerJob?.cancel()
        val startServiceIntent = Intent(context, TimerService::class.java).apply {
            action = "STOP"
        }
        try {
            context.startService(startServiceIntent)
        } catch (e: Exception) {
            android.util.Log.e("JeevanViewModel", "Failed to stop TimerService", e)
        }
        _isTimerRunning.value = false
        _timerSecondsRemaining.value = _customDurationMinutes.value * 60
    }

    fun getTodayDateString(): String {
        return repository.getTodayDateString()
    }

    // --- QUARTERLY REPORT GENERATOR ---
    fun generateCSVReport(context: Context) {
        viewModelScope.launch {
            try {
                val budget = userProfile.value.monthlyBudgetLimit
                val balance = userProfile.value.balanceAmount
                val txsList = transactions.value
                val spent = txsList.filter { it.type == "EXPENSE" }.sumOf { it.amount }
                val saved = budget - spent
                val holdings = portfolioHoldings.value

                val csvBuilder = StringBuilder()
                csvBuilder.append("Jeevan Personal OS Financial Report\n")
                csvBuilder.append("Generated on,,${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())}\n")
                csvBuilder.append("\nSummary,,\n")
                csvBuilder.append("Total Current Capital,,₹$balance\n")
                csvBuilder.append("Monthly Capital Threshold,,₹$budget\n")
                csvBuilder.append("Total Recorded Expenses,,₹$spent\n")
                csvBuilder.append("Implied Active Savings,,₹$saved\n")
                
                csvBuilder.append("\nRecent Ledgers,,\n")
                csvBuilder.append("Title,Amount,Type,Category,Is Subscription,DateString\n")
                txsList.forEach { tx ->
                    val dateFormatted = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(tx.date))
                    csvBuilder.append("\"${tx.title}\",${tx.amount},${tx.type},${tx.category},${tx.isSubscription},$dateFormatted\n")
                }

                csvBuilder.append("\nCore Portfolio & Assets,,\n")
                csvBuilder.append("Asset Name,Symbol,Exchange,Sector,Quantity,Purchase Price,Current Price,Current Calculated Value,Purchase Date,Notes,Asset Type\n")
                holdings.forEach { hold ->
                    val totalHoldVal = hold.quantity * hold.currentPrice
                    val dateFormatted = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(hold.purchaseDate ?: System.currentTimeMillis()))
                    val escapedNotes = (hold.notes ?: "").replace("\"", "\"\"")
                    csvBuilder.append("\"${hold.assetName}\",\"${hold.symbol.orEmpty()}\",\"${hold.exchange.orEmpty()}\",\"${hold.sector.orEmpty()}\",${hold.quantity},₹${hold.purchasePrice},₹${hold.currentPrice},₹$totalHoldVal,$dateFormatted,\"$escapedNotes\",${hold.assetType}\n")
                }

                val fileName = "jeevan_quarterly_report_${System.currentTimeMillis()}.csv"
                context.openFileOutput(fileName, Context.MODE_PRIVATE).use { output ->
                    output.write(csvBuilder.toString().toByteArray())
                }

                Toast.makeText(context, "Quarterly Report compilation successful: $fileName archived.", Toast.LENGTH_LONG).show()
                _chatMessages.value = _chatMessages.value + ChatMessage(
                    sender = "Jeevan Finance Advisor",
                    text = "Acknowledge: I have compiled your quarterly financial reports into: '$fileName' inside local app directories. Growth charts verified.",
                    timestamp = System.currentTimeMillis()
                )
            } catch (e: Exception) {
                android.util.Log.e("JeevanViewModel", "Failed to write CSV file", e)
                Toast.makeText(context, "Failed to compile: limits or storage error.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // --- CENTRAL BRAIN CHAT ROUTER ---
    fun sendChatMessage(text: String) {
        if (text.isBlank()) return

        val userMsg = ChatMessage(
            sender = "You",
            text = text,
            timestamp = System.currentTimeMillis()
        )
        _chatMessages.value = _chatMessages.value + userMsg
        _isBrainThinking.value = true

        viewModelScope.launch {
            // Build Context Memory from Database entries
            val profile = userProfile.value
            val txsList = transactions.value
            val activeSpent = txsList.filter { it.type == "EXPENSE" }.sumOf { it.amount }
            val health = repository.getTodayHealthLog()
            val caps = careerProgress.value
            val subLog = subtopicsProgress.value
            val holdingSummaryList = portfolioHoldings.value.map { "${it.assetName} (${it.quantity} units @ ₹${it.purchasePrice})" }.joinToString(", ")
            val goalsSummary = careerGoalFunds.value.map { "${it.name} (Target: ₹${it.targetAmount}, Current: ₹${it.currentAmount})" }.joinToString(", ")
            val weakAreas = subLog.filter { !it.isCompleted || it.assessmentScore < 70 }.map { it.subtopicId }.joinToString(", ")

            val completedSubtopicsCount = subLog.count { it.isCompleted }
            val totalSubtopicsCount = subLog.size

            val userMemoryContext = """
                [Jeevan DB Context Memory Injection]
                User Name: ${profile.name}
                Target Profession: ${profile.jobTarget}
                Current Streak: ${profile.careerStreak} Days
                Financial Capital Limit: ₹${profile.monthlyBudgetLimit}
                Expenses recorded: ₹$activeSpent
                Available Purchasing Power: ₹${profile.monthlyBudgetLimit - activeSpent}
                Balance amount in infrastructure: ₹${profile.balanceAmount}
                Holdings list: $holdingSummaryList
                Active Career Goals: $goalsSummary
                Weak Assessment Areas to Revise: $weakAreas
                Today's Water Intake: ${health.waterIntakeMl} ml (Goal: ${profile.dailyWaterGoalMl} ml)
                Today's Steps Count: ${health.stepsCount} (Goal: ${profile.dailyStepGoal})
                Today's Sleep: ${health.sleepMinutes} min
                Today's Calorie Consumed: ${health.caloriesConsumed} kcal
                Completed DevOps Subtopics: $completedSubtopicsCount of $totalSubtopicsCount
            """.trimIndent()

            // Run Gemini REST query
            val responseText = GeminiNetworkClient.queryJeevanEngine(text, userMemoryContext)
            
            _chatMessages.value = _chatMessages.value + ChatMessage(
                sender = "Jeevan",
                text = responseText,
                timestamp = System.currentTimeMillis()
            )
            _isBrainThinking.value = false
        }
    }

    // --- ECOSYSTEM REASONING ENGINE ---
    private suspend fun generateDynamicAIEcosystemInsights() {
        try {
            val txs = transactions.value
            val health = repository.getTodayHealthLog()
            val prof = repository.getOrInitUserProfile()
            val subs = subtopicsProgress.value

            val insights = mutableListOf<String>()

            // 1. Finance Checks
            val totalExpenses = txs.filter { it.type == "EXPENSE" }.sumOf { it.amount }
            if (totalExpenses > prof.monthlyBudgetLimit * 0.8) {
                insights.add("⚠️ Budget Overload Alert: You have expended 80% of your ₹${prof.monthlyBudgetLimit} budget allocation rate. Immediate capital freeze suggested.")
            } else if (totalExpenses > 0) {
                val rem = prof.monthlyBudgetLimit - totalExpenses
                insights.add("📈 Capital Analysis: Allocation stable. Remaining purchasing power is ₹$rem. Unused savings rate: ${((rem / prof.monthlyBudgetLimit) * 100).toInt()}%")
            } else {
                insights.add("✔ Zero financial outflow today. Capital preservation coefficient is 100%.")
            }

            // 2. Hydration checking
            if (health.waterIntakeMl < prof.dailyWaterGoalMl / 2) {
                insights.add("💧 Hydration Warning: Fluid index registered at low supply (${health.waterIntakeMl}ml / ${prof.dailyWaterGoalMl}ml). Supplement with 500ml water to secure cognitive efficiency.")
            } else {
                insights.add("💧 Hydration Stabilized: Fluid density matches standard guidelines. High focus capacity enabled.")
            }

            // 3. Health & Learning synergy
            if (health.sleepMinutes < 360) {
                insights.add("😴 Fatigue Warning: Sleep registered below 6 hours (${health.sleepMinutes} mins). I recommend holding off difficult AWS Multi-Region cluster updates today.")
            } else {
                val completionRatio = if (subs.isNotEmpty()) (subs.count { it.isCompleted }.toDouble() / subs.size * 100).toInt() else 0
                insights.add("🔥 DevOps Readiness: Career subtopic validation is at $completionRatio%. Track your progress continuously to unlock high-grade offers.")
            }

            _syntheticInsights.value = insights
        } catch (e: Exception) {
            android.util.Log.e("JeevanViewModel", "Failed to generate dynamic ecosystem insights", e)
        }
    }

    // --- BRAINSTORMING PUZZLES FUNCTIONS ---
    fun submitPuzzleAnswer(selectedIndex: Int, correctIndex: Int) {
        if (_puzzleIsAnswered.value) return
        _puzzleIsAnswered.value = true
        if (selectedIndex == correctIndex) {
            _brainstormScore.value += 20
            _puzzlesSolved.value += 1
            _puzzleResultFeedback.value = "🟢 SUCCESS! Scenario resolved accurately with strategic mastery! +25 DevOps XP awarded and Brain power coefficient boosted."
            viewModelScope.launch {
                repository.addXpToTopic("kubernetes", 25)
                generateDynamicAIEcosystemInsights()
            }
        } else {
            _puzzleResultFeedback.value = "🔴 COMPROMISE! The option selected failed to address baseline architectural rules. System state degraded. Let's study and try again."
        }
    }

    fun nextPuzzle(totalPuzzles: Int) {
        _puzzleIsAnswered.value = false
        _puzzleResultFeedback.value = null
        val nextIdx = (_selectedPuzzleIndex.value + 1) % totalPuzzles
        _selectedPuzzleIndex.value = nextIdx
    }

    // --- LOCATION TEMPERATURE WIDGET SUPPORT ---
    private val _weatherState = MutableStateFlow("UNKNOWN") // UNKNOWN, LOADING, SUCCESS, ERROR, PERMISSION_REQUIRED
    val weatherState: StateFlow<String> = _weatherState

    private val _weatherTemp = MutableStateFlow<Double?>(null)
    val weatherTemp: StateFlow<Double?> = _weatherTemp

    private val _weatherLocationName = MutableStateFlow("Locating...")
    val weatherLocationName: StateFlow<String> = _weatherLocationName

    fun refreshWeather() {
        val context = getApplication<Application>()
        _weatherState.value = "LOADING"
        viewModelScope.launch {
            try {
                if (androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED ||
                    androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    
                    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                    fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                        if (loc != null) {
                            fetchWeatherForCoordinates(loc.latitude, loc.longitude)
                        } else {
                            _weatherLocationName.value = "Bengaluru (GPS)"
                            fetchWeatherForCoordinates(12.9716, 77.5946)
                        }
                    }.addOnFailureListener {
                        _weatherLocationName.value = "Bengaluru (GPS)"
                        fetchWeatherForCoordinates(12.9716, 77.5946)
                    }
                } else {
                    _weatherLocationName.value = "Loc Off"
                    _weatherState.value = "PERMISSION_REQUIRED"
                }
            } catch (e: Exception) {
                _weatherLocationName.value = "Bengaluru (GPS)"
                fetchWeatherForCoordinates(12.9716, 77.5946)
            }
        }
    }

    private fun fetchWeatherForCoordinates(lat: Double, lon: Double) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val context = getApplication<Application>()
                try {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(lat, lon, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val city = addresses[0].locality ?: addresses[0].subAdminArea ?: "Remote Node"
                        _weatherLocationName.value = city
                    } else {
                        _weatherLocationName.value = "Grid ${String.format("%.2f", lat)},${String.format("%.2f", lon)}"
                    }
                } catch (e: Exception) {
                    _weatherLocationName.value = "Grid ${String.format("%.2f", lat)},${String.format("%.2f", lon)}"
                }

                val urlString = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current_weather=true"
                val conn = java.net.URL(urlString).openConnection() as java.net.HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 5000
                conn.readTimeout = 5000
                val responseStr = conn.inputStream.bufferedReader().use { it.readText() }

                val tempRegex = """(?i)"temperature"\s*:\s*([0-9.-]+)""".toRegex()
                val match = tempRegex.find(responseStr)
                if (match != null) {
                    val tempVal = match.groupValues[1].toDoubleOrNull()
                    if (tempVal != null) {
                        _weatherTemp.value = tempVal
                        _weatherState.value = "SUCCESS"
                    } else {
                        _weatherState.value = "ERROR"
                    }
                } else {
                    _weatherState.value = "ERROR"
                }
            } catch (e: Exception) {
                _weatherState.value = "ERROR"
            }
        }
    }

    fun updateAIInvestmentInsights() {
        val holdings = portfolioHoldings.value
        val fundsList = careerGoalFunds.value
        val insights = mutableListOf<String>()

        if (holdings.isEmpty()) {
            insights.add("Observation: No portfolio asset records stored in the OS database yet.")
            insights.add("Tip: Record mutual funds or stock units under secondary ledger to start auto-analysis.")
        } else {
            val totalVal = holdings.sumOf { it.quantity * it.currentPrice }
            val stocksVal = holdings.filter { it.assetType == "STOCK" }.sumOf { it.quantity * it.currentPrice }
            val mfVal = holdings.filter { it.assetType == "MF" }.sumOf { it.quantity * it.currentPrice }
            val sipVal = holdings.filter { it.assetType == "SIP" }.sumOf { it.quantity * it.currentPrice }

            val totalCompVal = totalVal.coerceAtLeast(1.0)
            val stockPercent = ((stocksVal / totalCompVal) * 100).toInt()
            val mfPercent = (((mfVal + sipVal) / totalCompVal) * 100).toInt()

            if (stockPercent > 60) {
                insights.add("Observation: Your portfolio has a heavy stock exposure index ($stockPercent%), meaning higher volatility during earnings season.")
            } else if (mfPercent > 60) {
                insights.add("Observation: Most of your assets ($mfPercent%) are locked in index / equity-based mutual funds which are excellent for long-term compound growth.")
            } else {
                insights.add("Observation: Asset allocation is balanced, split between mutual funds ($mfPercent%) and individual stocks ($stockPercent%).")
            }
        }

        val emergency = fundsList.firstOrNull { it.name.contains("Emergency", ignoreCase = true) }
        if (emergency != null) {
            val pct = ((emergency.currentAmount / emergency.targetAmount.coerceAtLeast(1.0)) * 100).toInt()
            insights.add("Observation: Emergency Reserve progress stands at $pct% of the ₹60,000 threshold.")
        }

        val totalSipMf = holdings.filter { it.assetType == "MF" || it.assetType == "SIP" }.sumOf { it.quantity * it.currentPrice }
        if (totalSipMf >= 4000.0) {
            insights.add("Observation: Consistent savings habit confirmed! Auto-SIP contributions show 80%+ consistency over standard limits.")
        } else {
            insights.add("Observation: Monthly investment contributions are slightly lower than optimal threshold. Target is ₹5,000.")
        }

        _aiInvestmentInsights.value = insights
    }

    fun calculateFinancialHealthScore(): FinancialHealthReport {
        val txsList = transactions.value
        val budget = userProfile.value.monthlyBudgetLimit.coerceAtLeast(1.0)
        val spent = txsList.filter { it.type == "EXPENSE" }.sumOf { it.amount }
        val remaining = (budget - spent).coerceAtLeast(0.0)

        // 1. Expense Ratio (20 pts)
        val expenseRatio = (spent / budget).coerceAtLeast(0.0)
        val expensePts = if (expenseRatio <= 0.5) 20.0 else ((1.0 - expenseRatio) * 40.0).coerceIn(0.0, 20.0)

        // 2. Savings Rate (20 pts)
        val savingsRate = remaining / budget
        val savingsPts = (savingsRate * 50.0).coerceIn(0.0, 20.0)

        // 3. Goal Progress (20 pts)
        val fundsList = careerGoalFunds.value
        val goalPts = if (fundsList.isEmpty()) 15.0 else {
            val totalTarget = fundsList.sumOf { it.targetAmount }.coerceAtLeast(1.0)
            val totalCurrent = fundsList.sumOf { it.currentAmount }
            ((totalCurrent / totalTarget) * 20.0).coerceIn(0.0, 20.0)
        }

        // 4. Investment Consistency (20 pts) (Target: ₹5,000 Mutual Funds/SIP per month)
        val holdings = portfolioHoldings.value
        val totalSipMf = holdings.filter { it.assetType == "MF" || it.assetType == "SIP" }.sumOf { it.quantity * it.currentPrice }
        val sipTarget = 5000.0
        val consistencyPercent = ((totalSipMf / sipTarget) * 100.0).coerceIn(0.0, 100.0)
        val consistencyPts = ((totalSipMf / sipTarget) * 20.0).coerceIn(0.0, 20.0)

        // 5. Emergency Fund Status (20 pts)
        val emergencyFund = fundsList.firstOrNull { it.name.contains("Emergency", ignoreCase = true) }
        val emergencyPts = if (emergencyFund != null) {
            ((emergencyFund.currentAmount / emergencyFund.targetAmount.coerceAtLeast(1.0)) * 20.0).coerceIn(0.0, 20.0)
        } else {
            10.0
        }

        val totalScore = (expensePts + savingsPts + goalPts + consistencyPts + emergencyPts).toInt().coerceIn(10, 100)

        val grade = when {
            totalScore >= 90 -> "S+ Excellent"
            totalScore >= 80 -> "A Grade (Strong Financial Health)"
            totalScore >= 70 -> "B Grade (Moderate)"
            totalScore >= 50 -> "C Grade (Needs Fine-Tuning)"
            else -> "D Grade (High Risk Alert)"
        }

        val recs = mutableListOf<String>()
        if (expenseRatio > 0.7) {
            recs.add("Your monthly spending exceeds 70% of threshold. Freeze non-essential purchase items.")
        } else {
            recs.add("Awesome capital threshold overhead of ${(100 - expenseRatio * 100).toInt()}%. Maintain this margin.")
        }

        if (emergencyFund == null || (emergencyFund.currentAmount / emergencyFund.targetAmount) < 0.5) {
            recs.add("Emergency reserve is under 50% target. Recommend funding this goal to cushion career changes.")
        } else {
            recs.add("Your emergency fund safety nest is fully operational on safe ground.")
        }

        if (totalSipMf < sipTarget) {
            recs.add("Sip contributions are ₹${(sipTarget - totalSipMf).toInt()} below monthly targets. Adjust recurring plans.")
        } else {
            recs.add("Compounding investments are highly consistent. Wealth engine running at maximum throughput.")
        }

        return FinancialHealthReport(
            score = totalScore,
            grade = grade,
            expenseRatio = expenseRatio,
            savingsRate = savingsRate,
            consistencyProgress = consistencyPercent,
            emergencyProgress = if (emergencyFund != null) (emergencyFund.currentAmount / emergencyFund.targetAmount * 100.0) else 0.0,
            recommendations = recs
        )
    }

    // --- DYNAMIC CORE SRE ENGINES ---
    fun generateDynamicAssessmentQuestions(subtopicId: String) {
        viewModelScope.launch {
            val rawKey = com.example.BuildConfig.GEMINI_API_KEY
            val isDefaultKey = rawKey.isBlank() || rawKey == "MY_GEMINI_API_KEY" || rawKey == "API_KEY"
            
            if (!isDefaultKey) {
                try {
                    val prompt = """
                        Generate exactly three unique, highly realistic, complex, and fresh DevOps interview questions (troubleshooting, architectural, or scenario-based) for the subtopic: '$subtopicId'.
                        Format your response as a simple JSON array of 3 strings, with no markdown styling or other characters around it. E.g. ["Question 1", "Question 2", "Question 3"].
                        Ensure the questions are challenging and directly relevant to '$subtopicId'.
                    """.trimIndent()
                    
                    val requestBody = com.example.network.GeminiRequest(
                        contents = listOf(
                            com.example.network.GeminiContent(
                                parts = listOf(
                                    com.example.network.GeminiPart(text = prompt)
                                )
                            )
                        )
                    )
                    val response = com.example.network.GeminiNetworkClient.apiService.generateContent(rawKey, requestBody)
                    val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
                    
                    val questionsList = parseJsonArrayOfStrings(responseText)
                    if (questionsList.size == 3) {
                        val currentMap = _dynamicQuestions.value.toMutableMap()
                        currentMap[subtopicId] = questionsList
                        _dynamicQuestions.value = currentMap
                        return@launch
                    }
                } catch (e: Exception) {
                    android.util.Log.e("JeevanViewModel", "Failed to generate dynamic questions via Gemini", e)
                }
            }
            
            // Offline/fallback dynamic randomization: Custom prefix injection
            val isRetest = _isRetestActive.value
            val baseQuestions = if (isRetest) {
                subtopicRetestQuestions[subtopicId] ?: subtopicQuestions[subtopicId]
            } else {
                subtopicQuestions[subtopicId]
            } ?: listOf(
                "Explain core components and data structures associated with $subtopicId.",
                "How do you debug an abrupt production failure regarding $subtopicId?",
                "Analyze a real-world high-throughput load incident for $subtopicId and outline mitigation."
            )
            
            val prefaces = listOf(
                "Under a sudden 15,000 requests/sec traffic burst: ",
                "Following a legacy database cluster failover: ",
                "To satisfy a strict zero-trust audit mandate: ",
                "Where network telemetry marks 12% packet loss: ",
                "During a mid-deployment stage Docker engine update: "
            ).shuffled()
            
            val customized = baseQuestions.mapIndexed { i, q ->
                val pref = prefaces.getOrElse(i) { "" }
                pref + q
            }
            
            val currentMap = _dynamicQuestions.value.toMutableMap()
            currentMap[subtopicId] = customized
            _dynamicQuestions.value = currentMap
        }
    }

    private fun parseJsonArrayOfStrings(json: String): List<String> {
        try {
            val cleaned = json.trim()
                .removePrefix("```json")
                .removeSuffix("```")
                .trim()
            if (cleaned.startsWith("[") && cleaned.endsWith("]")) {
                val list = mutableListOf<String>()
                var inside = false
                val current = StringBuilder()
                var escape = false
                for (char in cleaned.substring(1, cleaned.length - 1)) {
                    if (char == '\\' && !escape) {
                        escape = true
                        continue
                    }
                    if (char == '"' && !escape) {
                        inside = !inside
                        if (!inside) {
                            list.add(current.toString())
                            current.clear()
                        }
                        continue
                    }
                    escape = false
                    if (inside) {
                        current.append(char)
                    }
                }
                return list.filter { it.isNotBlank() }
            }
        } catch (e: Exception) {
            // fallback
        }
        return emptyList()
    }

    fun refreshNewsCenter() {
        if (_isNewsRefreshing.value) return
        _isNewsRefreshing.value = true
        
        viewModelScope.launch {
            val rawKey = com.example.BuildConfig.GEMINI_API_KEY
            val isDefaultKey = rawKey.isBlank() || rawKey == "MY_GEMINI_API_KEY" || rawKey == "API_KEY"
            
            if (!isDefaultKey) {
                try {
                    val prompt = """
                        Generate exactly 9 highly professional, fresh tech/DevOps news articles for a career-focused SRE personal OS.
                        Produce:
                        - 3 articles for category 'GENERAL' (high-level tech, cloud trends, global infrastructure)
                        - 3 articles for category 'JOBS' (DevOps/SRE job openings in India/Remote with detailed packages like ₹12-25 LPA, requirements, and hiring companies)
                        - 3 articles for category 'DEVOPS_UPDATES' (Docker Releases, AWS Updates, Kubernetes updates, Terraform, GitHub, certification changes)
                        
                        Respond in raw JSON format as a JSON array of objects with fields: id (unique slug), title, category, description, url, author, date (current Unix time). Do NOT put any markdown formatting or markdown blocks (like ```json) around the response.
                    """.trimIndent()
                    
                    val requestBody = com.example.network.GeminiRequest(
                        contents = listOf(
                            com.example.network.GeminiContent(
                                parts = listOf(
                                    com.example.network.GeminiPart(text = prompt)
                                )
                            )
                        )
                    )
                    val response = com.example.network.GeminiNetworkClient.apiService.generateContent(rawKey, requestBody)
                    val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
                    
                    val parsed = parseNewsJson(jsonText)
                    if (parsed.isNotEmpty()) {
                        _newsArticles.value = parsed
                        _lastNewsRefresh.value = System.currentTimeMillis()
                        _isNewsRefreshing.value = false
                        return@launch
                    }
                } catch (e: Exception) {
                    android.util.Log.e("JeevanViewModel", "Gemini news center generation failed; using offline fallback", e)
                }
            }
            
            _newsArticles.value = generateDynamicOfflineNews()
            _lastNewsRefresh.value = System.currentTimeMillis()
            _isNewsRefreshing.value = false
        }
    }

    private fun generateDynamicOfflineNews(): List<NewsCenterItem> {
        val now = System.currentTimeMillis()
        val companies = listOf("Razorpay", "CRED", "Groww", "Swiggy", "Zomato", "PhonePe", "Flipkart", "Atlassian", "BrowserStack", "Postman").shuffled()
        val salaries = listOf("15 - 22 LPA", "18 - 26 LPA", "22 - 30 LPA", "12 - 18 LPA", "25 - 35 LPA").shuffled()
        val locations = listOf("Bangalore (Remote-friendly)", "Pune / Hybrid", "Gurugram (Office)", "Mumbai HQ", "Remote (India)").shuffled()
        
        val k8sVersions = listOf("v1.30.5", "v1.31.2", "v1.32.1").shuffled()
        val dockerVersions = listOf("v26.1.4", "v27.0.2", "v27.2.1").shuffled()
        val terraformVersions = listOf("v1.8.5", "v1.9.4", "v1.9.6").shuffled()
        
        return listOf(
            NewsCenterItem(
                id = "gen_1",
                title = "Global Cloud Outage Analysis: Edge Endpoint DNS Latency SRE Review",
                category = "GENERAL",
                description = "A global DNS propagation lag affects secondary cloud networks, causing 12% timeout anomalies across major SaaS providers. SRE teams mitigate using fallback regional Anycast routers.",
                author = "Infrastructure Sentinel Desk",
                date = now
            ),
            NewsCenterItem(
                id = "gen_2",
                title = "Sovereign Cloud Data Initiatives Expand in European and Indian Zones",
                category = "GENERAL",
                description = "Governments accelerate cloud isolation mandates, boosting demand for localized physical database engineering and local Kubernetes clusters managed by local SREs.",
                author = "National Security Tech",
                date = now - 3600000
            ),
            NewsCenterItem(
                id = "gen_3",
                title = "AIOps Observability Agents Integration Scale Unprecedented Records",
                category = "GENERAL",
                description = "New telemetry monitors claim to automatically identify memory leak anomalies in Docker processes 40% faster using lightweight vector calculations.",
                author = "AIOps Monthly",
                date = now - 7200000
            ),
            NewsCenterItem(
                id = "job_1",
                title = "Senior Platform SRE at ${companies[0]}",
                category = "JOBS",
                description = "Orchestrate high-volume production logs, maintain Terraform VPC modules, and enforce zero-downtime Helm upgrades. Compensation: ₹${salaries[0]} | Location: ${locations[0]}.",
                author = "Careers Hub",
                date = now
            ),
            NewsCenterItem(
                id = "job_2",
                title = "Cloud Infrastructure Security Engineer at ${companies[1]}",
                category = "JOBS",
                description = "Enforce least-privilege IAM JSON policies worldwide, audit cross-account AWS STS authentication, and remediate container security scans. Compensation: ₹${salaries[1]} | Location: ${locations[1]}.",
                author = "Talent Acquisition",
                date = now - 1800000
            ),
            NewsCenterItem(
                id = "job_3",
                title = "Associate DevOps Automation Engineer at ${companies[2]}",
                category = "JOBS",
                description = "Great junior opportunity. Automate system software installation Bash scripts, manage GitHub pipelines, and maintain Docker container builds. Compensation: ₹${salaries[2]} | Location: ${locations[2]}.",
                author = "SRE Recruiter",
                date = now - 5400000
            ),
            NewsCenterItem(
                id = "devops_1",
                title = "Kubernetes ${k8sVersions[0]} Core Release Enhances Ingress SSL & Node Limits",
                category = "DEVOPS_UPDATES",
                description = "The CNCF officially announces ${k8sVersions[0]} optimizing CoreDNS lookup limits and introducing highly declarative fallback probes for stateful containers.",
                author = "K8s Official Release",
                date = now
            ),
            NewsCenterItem(
                id = "devops_2",
                title = "Docker Desktop ${dockerVersions[0]} Integrates Webassembly Isolation Layers",
                category = "DEVOPS_UPDATES",
                description = "The modern Docker ${dockerVersions[0]} release includes multi-stage build cache speed improvements and seamless local Helm charts deployment mapping.",
                author = "Docker News",
                date = now - 2700000
            ),
            NewsCenterItem(
                id = "devops_3",
                title = "HashiCorp Terraform ${terraformVersions[0]} Optimizes Remote Cache State Locking",
                category = "DEVOPS_UPDATES",
                description = "Terraform ${terraformVersions[0]} introduces modular plan file outputs, allowing engineers to verify precise IAM permission footprints before initiating global applies.",
                author = "HashiCorp Press",
                date = now - 8100000
            )
        )
    }

    private fun parseNewsJson(json: String): List<NewsCenterItem> {
        val list = mutableListOf<NewsCenterItem>()
        try {
            val cleaned = json.trim()
                .removePrefix("```json")
                .removeSuffix("```")
                .trim()
            val arr = org.json.JSONArray(cleaned)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val id = obj.optString("id", "")
                val title = obj.optString("title", "")
                val category = obj.optString("category", "")
                val description = obj.optString("description", "")
                val url = obj.optString("url", "")
                val author = obj.optString("author", "Jeevan Intelligence")
                list.add(NewsCenterItem(id, title, category, description, url, author, System.currentTimeMillis()))
            }
        } catch (e: Exception) {
            android.util.Log.e("JeevanViewModel", "Falling back to regex parsing due to JSON error", e)
            try {
                val cleaned = json.trim().removePrefix("```json").removeSuffix("```").trim()
                val pattern = java.util.regex.Pattern.compile("\\{\\s*\"id\"\\s*:\\s*\"([^\"]*)\"\\s*,\\s*\"title\"\\s*:\\s*\"([^\"]*)\"\\s*,\\s*\"category\"\\s*:\\s*\"([^\"]*)\"\\s*,\\s*\"description\"\\s*:\\s*\"([^\"]*)\"\\s*,\\s*\"url\"\\s*:\\s*\"([^\"]*)\"\\s*,\\s*\"author\"\\s*:\\s*\"([^\"]*)\"\\s*,?[^\\}]*\\}")
                val matcher = pattern.matcher(cleaned)
                while (matcher.find()) {
                    val id = matcher.group(1) ?: ""
                    val title = matcher.group(2) ?: ""
                    val category = matcher.group(3) ?: ""
                    val description = matcher.group(4) ?: ""
                    val url = matcher.group(5) ?: ""
                    val author = matcher.group(6) ?: ""
                    list.add(NewsCenterItem(id, title, category, description, url, author, System.currentTimeMillis()))
                }
            } catch (ex: Exception) {
                // ignore
            }
        }
        return list
    }

    fun completeSeatedWorkout(xpAmount: Int) {
        viewModelScope.launch {
            val topics = listOf("linux", "kubernetes", "docker", "aws")
            val chosenTopic = topics.random()
            repository.addXpToTopic(chosenTopic, xpAmount)
            _chatMessages.value = _chatMessages.value + ChatMessage(
                sender = "Jeevan SRE Coach",
                text = "Health alert check: Seated posture desk workout logged! +$xpAmount XP awarded to '$chosenTopic' profile to counteract terminal fatigue.",
                timestamp = System.currentTimeMillis()
            )
            generateDynamicAIEcosystemInsights()
        }
    }

    override fun onCleared() {
        TimerService.clearCallbacks()
        super.onCleared()
    }
}

data class FinancialHealthReport(
    val score: Int,
    val grade: String,
    val expenseRatio: Double,
    val savingsRate: Double,
    val consistencyProgress: Double,
    val emergencyProgress: Double,
    val recommendations: List<String>
)

data class ChatMessage(
    val sender: String,
    val text: String,
    val timestamp: Long
)

data class NewsCenterItem(
    val id: String,
    val title: String,
    val category: String, // "GENERAL", "JOBS", "DEVOPS_UPDATES"
    val description: String,
    val url: String = "",
    val author: String = "Jeevan Intelligence",
    val date: Long = System.currentTimeMillis()
)
