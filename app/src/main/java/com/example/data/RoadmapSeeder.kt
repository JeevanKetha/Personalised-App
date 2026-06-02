package com.example.data

import com.example.data.entity.RoadmapTopic
import com.example.data.entity.RoadmapSubtopic
import com.example.data.repository.JeevanRepository
import android.util.Log

object RoadmapSeeder {
    private const val TAG = "RoadmapSeeder"

    suspend fun seedIfNeeded(repository: JeevanRepository) {
        val profile = repository.getOrInitUserProfile()
        val existingTopics = repository.getAllRoadmapTopicsDirect()
        val needsReseed = existingTopics.size != 28 || existingTopics.firstOrNull()?.title != "Linux OS & File System"

        if (!profile.seeded || needsReseed) {
            Log.d(TAG, "Profile not seeded or outdated/mismatched. Seed default DevOps roadmap...")
            seedDefaultRoadmap(repository)
            repository.updateUserProfile(profile.copy(seeded = true))
            Log.d(TAG, "Roadmap seeding completed successfully.")
        }
    }

    suspend fun seedDefaultRoadmap(repository: JeevanRepository) {
        repository.clearAllRoadmapData()

        val defaultTopics = listOf(
            RoadmapTopic(1, "Linux OS & File System", 1, "Learn Linux OS structure, core file hierarchy, VIM, and essential commands.", "Linux", 1),
            RoadmapTopic(2, "Linux Administration & Processes", 2, "Master package management, processes, users, permissions, and storage mount.", "Settings", 2),
            RoadmapTopic(3, "Networking + SSH + Bash Scripting", 3, "Understand IP networking, diagnostics tools, SSH tunneling, and Bash scripting.", "Network", 3),
            RoadmapTopic(4, "Git + GitHub + Python for DevOps", 4, "Version control, branching, pull requests, and Python automation scripting.", "Code", 4),
            RoadmapTopic(5, "AWS Account Setup + Cost + IAM", 5, "Setup billing alerts, budget limits, multi-factor accounts, and AWS IAM controls.", "Cloud", 5),
            RoadmapTopic(6, "EC2 Deep Dive", 6, "Launch instances, configure security groups, EBS volumes, and run bootstrap scripts.", "Storage", 6),
            RoadmapTopic(7, "Load Balancing + Auto Scaling + S3", 7, "ELB policies, target groups, auto scaling, S3 lifecycle, and static hosting.", "Router", 7),
            RoadmapTopic(8, "Route53 + CloudWatch + Systems Manager", 8, "DNS routing, CloudWatch metrics, EventBridge alarms, and SSM automation.", "Build", 8),
            RoadmapTopic(9, "VPC Fundamentals", 9, "VPC subnets CIDRs design, routing tables, NAT gateways, and connectivity tests.", "Network", 9),
            RoadmapTopic(10, "Advanced Networking + Security Services", 10, "Direct Connect, Transit Gateway, Global Accelerator, WAF, and CloudTrail audits.", "Refresh", 10),
            RoadmapTopic(11, "RDS + DynamoDB + ElastiCache", 11, "AWS relational databases, DynamoDB schema indexes, and cluster caching.", "Play", 11),
            RoadmapTopic(12, "Serverless + Application Services", 12, "Lambda, API Gateway routing rules, SQS queues, SNS notifications, and Step functions.", "Router", 12),
            RoadmapTopic(13, "CloudFormation + AWS Well-Architected", 13, "Deploy IaC infrastructure using CloudFormation and evaluate architectural pillars.", "Settings", 13),
            RoadmapTopic(14, "AWS Cost Optimization + FinOps", 14, "Reserved vs Savings, trusted advisors, consolidated multi-accounts billing, and Control Tower.", "Storage", 14),
            RoadmapTopic(15, "Docker Fundamentals", 15, "Containers virtualization, essential docker instructions, multi-stage optimization.", "Build", 15),
            RoadmapTopic(16, "Docker Compose + ECR + Security", 16, "Multi-container compose, security scanning with Trivy, and ECR private registries.", "Code", 16),
            RoadmapTopic(17, "ECS (Elastic Container Service)", 17, "Microservices via ECS Task definitions, Fargate integrations, and service discovery.", "Storage", 17),
            RoadmapTopic(18, "GitHub Actions CI/CD", 18, "Define build workflows, matrix jobs runtime, OIDC AWS authentication, and deployments.", "Build", 18),
            RoadmapTopic(19, "DevSecOps — Security in Pipelines", 19, "Shift-left SAST/DAST scanner checks, Trivy container security scans, and SonarQube.", "Code", 19),
            RoadmapTopic(20, "Jenkins + GitLab CI", 20, "Setup servers, write declarative pipelines, and execute multi-tool parallel stages.", "Play", 20),
            RoadmapTopic(21, "Ansible — Configuration Management", 21, "Deploy configuration playbooks, register dynamic host files, and configure servers.", "Check", 21),
            RoadmapTopic(22, "Kubernetes Fundamentals", 22, "Deploy pods, replica-sets, statefulsets, container CPU/RAM resource limits, and service limits.", "Settings", 22),
            RoadmapTopic(23, "EKS + HELM + ArgoCD GitOps", 23, "Manage clusters, package Helm charts, and establish GitOps auto sync gates via ArgoCD.", "Refresh", 23),
            RoadmapTopic(24, "K8s Security + Monitoring on EKS", 24, "Open Policy Gatekeeper rules, scrape Prometheus metrics, and draw Grafana dashboards.", "Play", 24),
            RoadmapTopic(25, "Terraform Fundamentals + Intermediate", 25, "Deploy infrastructure using Terraform templates and S3 remote backend states.", "Network", 25),
            RoadmapTopic(26, "Terraform Advanced + Project", 26, "Manage data source references, tfstate drifts, OIDC automation pipelines, and deploy VPC with EKS.", "Storage", 26),
            RoadmapTopic(27, "AIOps for Cloud + AWS AI Services", 27, "AI-assisted DevOps prompt engineering, Bedrock LLM models, and Model Context Protocol (MCP).", "Router", 27),
            RoadmapTopic(28, "SAA-C03 + Final Project + Launch", 28, "Complete architect exams, polish portfolio, mock interviews, and apply for DevOps jobs.", "Check", 28)
        )

        // Bulk insert topics
        defaultTopics.forEach { topic ->
            repository.insertRoadmapTopic(topic)
        }

        // Subtopics mapping with exactly 7 tasks per week aligned with Mon-Sun
        val subtopicsMap = mapOf(
            1 to listOf(
                "DevOps & Cloud Engineer career overview | SDLC, Agile, DevOps lifecycle",
                "Linux: OS structure, file system hierarchy /etc /var /home /usr /tmp",
                "Linux: ls, cd, pwd, mkdir, cp, mv, rm, find, locate",
                "Linux: cat, grep, awk, sed, cut, sort, uniq, wc, pipe usage",
                "Linux: VIM editor — insert, edit, save, quit, search, replace",
                "Avizway: Linux basics playlist — videos 1–5 | Practice on EC2",
                "OverTheWire Bandit Levels 1–10 (gamified practice) + Set $0 billing alarm"
            ),
            2 to listOf(
                "Package management: YUM, DNF, APT | Install Apache, Nginx, MySQL",
                "Process management: ps, top, htop, kill, nice, nohup, systemctl, service",
                "User & group management: adduser, usermod, passwd, sudo, /etc/passwd",
                "Permissions: chmod (octal+symbolic), chown, chgrp, SUID, SGID, sticky bit",
                "Storage: df, du, lsblk, mount, /etc/fstab | EBS attach on EC2",
                "Avizway: Linux admin module | Practice user mgmt + permissions on EC2",
                "Lab: Install Nginx, create users, set permissions, mount EBS volume"
            ),
            3 to listOf(
                "Networking: IP (public/private), CIDR, DNS, HTTP/HTTPS, TCP/UDP, ports",
                "Networking: curl, ping, traceroute, netstat, ss, nslookup, dig, iptables",
                "SSH: ssh-keygen (RSA + ED25519), ssh-copy-id, config file, tunnelling",
                "Bash scripting: variables, if/else, loops (for/while), functions, arrays",
                "Bash: cron jobs, input/output, error handling, exit codes, trap",
                "Write 5 Bash scripts: backup, log cleaner, disk alert, user audit, health check",
                "Push all scripts to GitHub repo 'devops-cloud-journey' + LinkedIn post 1"
            ),
            4 to listOf(
                "Git: init, add, commit, push, pull, clone, status, log, diff",
                "Git: branching, merge, rebase, stash, cherry-pick, conflict resolution",
                "Git: pull requests, forks, .gitignore, GitHub Actions intro",
                "Python for DevOps: os, sys, subprocess, shutil, pathlib, argparse",
                "Python: requests (API calls), json, yaml, logging, virtual environments",
                "learngitbranching.js.org all beginner + intermediate levels",
                "Build Python script: auto-backup to S3 using boto3 (install + configure)"
            ),
            5 to listOf(
                "AWS: Global infrastructure, regions, AZs, edge locations, free tier limits",
                "Cost management: Budgets, Cost Explorer, Savings Plans, Reserved Instances",
                "IAM: Root vs IAM user, MFA, password policies, access keys best practices",
                "IAM: Users, groups, roles, customer managed policies, policy simulator",
                "IAM: Least privilege, cross-account roles, service-linked roles, STS",
                "Avizway: IAM complete module | Hands-on: create users, groups, policies",
                "Python + boto3: list IAM users, create S3 bucket, describe EC2 via script"
            ),
            6 to listOf(
                "EC2: Instance types (t3/m5/c5/r5), pricing models, launch wizard",
                "EC2: Security groups (stateful), key pairs, elastic IPs, placement groups",
                "EC2: EBS volumes, snapshots, AMIs (custom + marketplace), DLM",
                "EC2: User Data, instance metadata (IMDS), EFS, Instance Connect",
                "EC2: AWS CLI operations, IAM instance profiles, SSM Session Manager",
                "Avizway: EC2 full module videos | Build: multi-AZ web app on EC2",
                "Lab: Launch EC2, attach EBS, create AMI, run user-data bootstrap script"
            ),
            7 to listOf(
                "ELB: ALB vs NLB vs CLB | Target groups, listeners, routing rules",
                "ASG: Launch templates, scaling policies (target/step/scheduled/predictive)",
                "S3: Storage classes (Standard/IA/Glacier), lifecycle policies, versioning",
                "S3: Bucket policies, ACLs, encryption (SSE-S3/SSE-KMS), pre-signed URLs",
                "S3: Static website hosting, CloudFront integration, Transfer Acceleration",
                "Avizway: ALB + ASG + S3 modules | Project: static site on S3 + CloudFront",
                "Project 1: EC2 + ALB + ASG + S3 website — fully working + documented"
            ),
            8 to listOf(
                "Route53: Hosted zones, record types (A, CNAME, MX, TXT, Alias), TTL",
                "Route53: Routing policies (Simple/Weighted/Latency/Failover/Geolocation/Multi)",
                "CloudWatch: Metrics, alarms, dashboards, Logs Insights, Container Insights",
                "CloudWatch: EventBridge rules, SNS notifications, Lambda triggers",
                "Systems Manager: Run Command, Session Manager, Parameter Store, Secrets Manager",
                "Avizway: Route53 + CloudWatch module | Set up monitoring dashboard",
                "Lab: domain → Route53 → EC2 | CloudWatch alarm → SNS email alert"
            ),
            9 to listOf(
                "VPC: CIDR design, public vs private subnets, multi-AZ architecture",
                "VPC: Internet Gateway, NAT Gateway, NAT instance, Elastic IPs",
                "VPC: Route tables, Network ACLs (stateless), Security Groups (stateful)",
                "VPC: VPC Peering, Transit Gateway, PrivateLink, VPC Endpoints",
                "VPC: Flow Logs, traffic mirroring, Reachability Analyzer",
                "Avizway: VPC complete module | Build 3-tier VPC from scratch",
                "Lab: Custom VPC (public+private subnet) + NAT + IGW + EC2 reachability test"
            ),
            10 to listOf(
                "Networking: Site-to-Site VPN, Direct Connect, Transit Gateway advanced",
                "Networking: Global Accelerator, CloudFront advanced, WAF, Shield",
                "Security: CloudTrail, AWS Config (rules + remediation), GuardDuty",
                "Security: KMS (CMK, data keys, key rotation), ACM, Secrets Manager",
                "Security: IAM Access Analyzer, Security Hub, Inspector, Macie",
                "Avizway: Security services module | Hands-on: GuardDuty + Config rules",
                "Lab: Enable CloudTrail + Config + GuardDuty + KMS encrypt S3"
            ),
            11 to listOf(
                "RDS: MySQL/PostgreSQL, Multi-AZ, Read Replicas, automated backups",
                "RDS: Parameter groups, option groups, encryption, IAM auth, Proxy",
                "DynamoDB: Tables, items, keys, GSI/LSI, capacity modes, DAX",
                "ElastiCache: Redis vs Memcached, replication, cluster mode, use cases",
                "Database: Redshift overview, Aurora (serverless), DocumentDB, DMS",
                "Avizway: Database module | Lab: RDS + EC2 connect + Multi-AZ failover test",
                "Lab: DynamoDB CRUD via Python boto3 + ElastiCache Redis basic config"
            ),
            12 to listOf(
                "Lambda: Runtime, triggers (S3/DynamoDB/API GW/EventBridge), layers, env vars",
                "Lambda: Cold start, concurrency, reserved concurrency, VPC Lambda",
                "API Gateway: REST vs HTTP API, stages, authorizers, throttling, caching",
                "App services: SQS (standard/FIFO), SNS, EventBridge, Step Functions",
                "Serverless Project: Lambda + API GW + DynamoDB + S3 full stack",
                "Project 2: Serverless app — API GW → Lambda → DynamoDB + SNS notification",
                "Document Project 2 + architecture diagram + push to GitHub + LinkedIn post"
            ),
            13 to listOf(
                "CloudFormation: Templates, stacks, parameters, mappings, conditions",
                "CloudFormation: Outputs, cross-stack references, nested stacks, StackSets",
                "Well-Architected Framework: Pillar 1+2 — Operational Excellence + Security",
                "Well-Architected Framework: Pillar 3+4 — Reliability + Performance Efficiency",
                "Well-Architected Framework: Pillar 5+6 — Cost Optimization + Sustainability",
                "Build: CFT template deploying full VPC + EC2 + ALB + RDS stack",
                "AWS Well-Architected Tool hands-on review + cost optimization report"
            ),
            14 to listOf(
                "Cost: Reserved Instances vs Savings Plans vs On-Demand vs Spot",
                "Cost: Trusted Advisor recommendations, Cost Explorer, Budgets alerts",
                "Cost: Right-sizing EC2, S3 intelligent tiering, data transfer costs",
                "Cost: Multi-account strategy, AWS Organizations, SCPs, billing isolation",
                "AWS Organizations: OU structure, consolidated billing, SSO, Control Tower",
                "Avizway + AWS Skill Builder: Cost optimization labs",
                "AWS CCP exam prep: mock test 1 (examtopics.com) + book CCP exam"
            ),
            15 to listOf(
                "Containers: What, why containers vs VMs, Docker architecture, Docker Hub",
                "Docker: pull, run, exec, stop, rm, images, rmi, ps, inspect, logs",
                "Dockerfile: FROM, RUN, COPY, CMD, ENTRYPOINT, ARG, ENV, EXPOSE, VOLUME",
                "Docker: multi-stage builds, .dockerignore, layer caching, image optimization",
                "Docker: networking (bridge/host/none/overlay), volumes, bind mounts",
                "TechWorld with Nana: Docker full tutorial | play-with-docker.com labs",
                "Hands-on: Containerize a Python/Node app + push to DockerHub"
            ),
            16 to listOf(
                "Docker Compose: services, networks, volumes, depends_on, env files",
                "ECR: Create repo, push/pull images, lifecycle policies, cross-account access",
                "Container security: Docker Scout, Trivy scanning, image signing, Cosign",
                "ECR: Public vs private repos, replication, pull-through cache",
                "Security: Dockerfile best practices, non-root user, read-only filesystem",
                "Avizway: ECR + Docker security module | Trivy scan pipeline",
                "Project 3: Docker app → Trivy scan → ECR → run on EC2 with CloudWatch logs"
            ),
            17 to listOf(
                "ECS: Clusters, tasks, task definitions, services, launch types (EC2 vs Fargate)",
                "ECS: Fargate — serverless containers, vCPU/memory, networking mode",
                "ECS: ALB integration, service auto-scaling, rolling updates, blue/green",
                "ECS: IAM task roles, Secrets Manager integration, logging (awslogs)",
                "ECS: Service discovery, App Mesh intro, ECS Anywhere overview",
                "Avizway: ECS full module | Project: app on ECS Fargate + ALB",
                "Document Project 3 + ECS deployment architecture diagram + GitHub"
            ),
            18 to listOf(
                "GHA: Workflow syntax, events (push/PR/schedule/manual), jobs, steps",
                "GHA: runners (GitHub-hosted + self-hosted), environments, secrets",
                "GHA: Matrix builds, reusable workflows, composite actions, caching",
                "GHA: Deploy to EC2, push image to ECR, deploy to ECS via GHA",
                "GHA: OIDC auth with AWS (no keys), environment protection rules",
                "TechWorld w/ Nana: GitHub Actions tutorial | Build full pipeline",
                "Pipeline: code push → build → test → push ECR → deploy ECS (fully automated)"
            ),
            19 to listOf(
                "DevSecOps: Shift-left security, SAST vs DAST vs SCA, threat modeling",
                "GHA Security: gitleaks (secret scanning), flake8, bandit (SAST), pip-audit",
                "GHA Security: hadolint (Dockerfile lint), Trivy (container scan), SBOM",
                "SonarQube: Install, configure, quality gates, integrate into GHA pipeline",
                "Nexus/Artifactory: Artifact management, npm/Maven/Docker proxy repos",
                "Avizway: DevSecOps complete module | Add all security tools to pipeline",
                "Project 4: Full DevSecOps pipeline — SAST + SCA + container scan + deploy"
            ),
            20 to listOf(
                "Jenkins: Install, architecture, plugins, declarative vs scripted pipeline",
                "Jenkins: Jenkinsfile, stages, agents, parameters, shared libraries",
                "Jenkins: Multi-branch pipelines, webhooks, Blue Ocean UI",
                "GitLab CI: .gitlab-ci.yml, stages, jobs, runners, artifacts, environments",
                "GitLab CI: Auto DevOps, container registry, Kubernetes integration",
                "Simplilearn: Jenkins full course | Build Jenkins pipeline to EC2",
                "Lab: Jenkins multi-stage pipeline + GitLab CI mirror — same app, two tools"
            ),
            21 to listOf(
                "Ansible: What, why, architecture (control node, managed nodes, inventory)",
                "Ansible: Ad-hoc commands, playbooks, YAML syntax, modules, tasks",
                "Ansible: Variables, facts, templates (Jinja2), handlers, conditionals, loops",
                "Ansible: Roles structure, Galaxy, dynamic inventory, vault (secrets)",
                "Ansible: Kubernetes cluster automation playbooks, AWS provisioning",
                "Avizway + GFG: Ansible module | Playbook: install & configure Nginx on 3 EC2s",
                "Project 5: Ansible playbook — full server config (Nginx+app+monitoring) on EC2"
            ),
            22 to listOf(
                "K8s: Architecture (control plane + worker nodes), objects overview",
                "K8s: Pods, ReplicaSets, Deployments, StatefulSets, DaemonSets",
                "K8s: Services (ClusterIP/NodePort/LoadBalancer), Ingress, NetworkPolicy",
                "K8s: ConfigMaps, Secrets, resource limits, liveness/readiness probes",
                "K8s: RBAC, ServiceAccounts, PersistentVolumes, StorageClasses",
                "TechWorld w/ Nana: Kubernetes full course Part 1 + killercoda.com labs",
                "killercoda.com: 8 K8s scenario labs + play-with-k8s.com practice"
            ),
            23 to listOf(
                "EKS: Create cluster (eksctl + Console), node groups, managed vs self-managed",
                "EKS: HELM — charts, values, templates, repositories, Helm upgrade/rollback",
                "EKS: Cluster Autoscaler, HPA, VPA, Goldilocks resource recommendations",
                "EKS: Ingress (ALB controller), IRSA, RBAC, EKS ControlPlane logging",
                "ArgoCD: GitOps concepts, install, configure, App of Apps, sync policies",
                "Avizway: EKS + ArgoCD full module | Deploy app via ArgoCD on EKS",
                "Project 6: App on EKS with ArgoCD GitOps + HELM chart + HPA"
            ),
            24 to listOf(
                "K8s security: OPA Gatekeeper, PodSecurity, network policies, image scanning",
                "EKS: ISTIO service mesh intro, mTLS, traffic management",
                "Prometheus: Install on EKS, scrape configs, PromQL basics",
                "Grafana: Connect to Prometheus, dashboards, Loki for logs, Alloy",
                "EKS Monitoring: CW Container Insights, IRSA, custom metrics",
                "TechWorld w/ Nana: Prometheus + Grafana on K8s | Full monitoring setup",
                "Add monitoring to Projects 5 + 6 | Clean all GitHub repos + READMEs"
            ),
            25 to listOf(
                "Terraform: providers, HCL syntax, init/validate/plan/apply/destroy/fmt",
                "Terraform: variables, locals, outputs, type constraints, tfvars files",
                "Terraform: modules (local + registry), module versioning, module outputs",
                "Terraform: State file, remote state (S3 + DynamoDB lock), workspaces",
                "Terraform: meta-arguments (count/for_each/depends_on/lifecycle)",
                "Avizway Terraform playlist + HashiCorp official tutorials",
                "Build: Terraform module library — VPC, EC2, S3, IAM, RDS modules"
            ),
            26 to listOf(
                "Terraform: Data sources, dynamic blocks, provisioners, null_resource",
                "Terraform: Drift detection, import existing infra, state manipulation",
                "Terraform: HCP Console, Sentinel policies, policy as code",
                "Terraform: Multi-account, multi-region deployments, assume_role",
                "Terraform: CI/CD integration (Terraform in GitHub Actions pipeline)",
                "Project 7: Full AWS infra — VPC+EC2+EKS+RDS+S3+IAM via Terraform",
                "Document Project 7 + architecture diagram + terraform.md README"
            ),
            27 to listOf(
                "AIOps: What is AIOps, AWS KIRO CLI + IDE, effective AI prompting",
                "Amazon Bedrock: LLMs on AWS, model selection, inference, RAG basics",
                "MCP (Model Context Protocol), AWS DevOps Agent configuration",
                "AI-assisted DevOps: Generate Terraform, Dockerfiles, K8s YAMLs using AI",
                "MLflow intro: model tracking, experiment logging, model registry",
                "Avizway: AIOps module | Amazon Bedrock hands-on demo",
                "Add 'AIOps | Amazon Bedrock | MLflow' to resume + LinkedIn update"
            ),
            28 to listOf(
                "Final project: Plan end-to-end system architecture — draw full diagram",
                "Final project: Terraform provision + Docker + ECS/EKS + GHA DevSecOps pipeline",
                "Final project: ArgoCD GitOps + Prometheus/Grafana monitoring + alerts",
                "Book + take AWS SAA-C03 exam (Tutorial Dojo mock first if not done)",
                "Resume final: Cloud & DevSecOps Engineer + 7 GitHub project links",
                "Interview Q&A: 50 most common DevOps + Cloud Engineer questions",
                "Start applying: LinkedIn + Naukri + Instahyre | Message 10 Hyd engineers"
            )
        )

        subtopicsMap.forEach { (topicId, subNames) ->
            subNames.forEachIndexed { idx, title ->
                val day = idx + 1
                val subtopic = RoadmapSubtopic(
                    id = 0,
                    parentTopicId = topicId,
                    title = title,
                    resourceUrl = when {
                        topicId in 1..3 && idx == 5 -> "https://youtube.com/c/avizway"
                        topicId in 1..3 && title.contains("Linux Journey", ignoreCase = true) -> "https://linuxjourney.com"
                        topicId in 1..3 && title.contains("OverTheWire", ignoreCase = true) -> "https://overthewire.org/wargames/bandit"
                        topicId in 1..3 && title.contains("AWS Free", ignoreCase = true) -> "https://aws.amazon.com/free"
                        
                        topicId in 4..5 && title.contains("learngitbranching", ignoreCase = true) -> "https://learngitbranching.js.org"
                        topicId in 4..5 && title.contains("freeCodeCamp", ignoreCase = true) -> "https://www.youtube.com/watch?v=RGOj5yH7evk"
                        topicId in 4..5 && title.contains("Corey Schafer", ignoreCase = true) -> "https://www.youtube.com/c/Coreyms"
                        topicId in 4..5 && title.contains("Skill Builder", ignoreCase = true) -> "https://skillbuilder.aws"
                        
                        topicId in 15..17 && title.contains("TechWorld with Nana", ignoreCase = true) -> "https://www.youtube.com/watch?v=3c-iBn73dDE"
                        topicId in 15..17 && title.contains("Play with Docker", ignoreCase = true) -> "https://labs.play-with-docker.com"
                        topicId in 15..17 && title.contains("Trivy", ignoreCase = true) -> "https://aquasecurity.github.io/trivy"
                        
                        topicId in 18..19 && title.contains("TechWorld", ignoreCase = true) -> "https://www.youtube.com/watch?v=R8_veQiYBhI"
                        topicId in 18..19 && title.contains("GitHub", ignoreCase = true) -> "https://docs.github.com/en/actions"
                        topicId in 18..19 && title.contains("SonarQube", ignoreCase = true) -> "https://docs.sonarqube.org"
                        
                        topicId in 20..21 && title.contains("Simplilearn", ignoreCase = true) -> "https://www.youtube.com/watch?v=LFDrDnKP_gA"
                        topicId in 20..21 && title.contains("Ansible", ignoreCase = true) -> "https://docs.ansible.com"
                        topicId in 20..21 && title.contains("GitLab", ignoreCase = true) -> "https://docs.gitlab.com/ee/ci"
                        
                        topicId in 22..24 && title.contains("TechWorld", ignoreCase = true) -> "https://www.youtube.com/watch?v=X48VuDVv0do"
                        topicId in 22..24 && title.contains("Killercoda", ignoreCase = true) -> "https://killercoda.com"
                        topicId in 22..24 && title.contains("Grafana", ignoreCase = true) -> "https://grafana.com/tutorials"
                        
                        topicId in 25..27 && title.contains("HashiCorp", ignoreCase = true) -> "https://developer.hashicorp.com/terraform/tutorials"
                        topicId in 25..27 && title.contains("Bedrock", ignoreCase = true) -> "https://aws.amazon.com/bedrock"
                        topicId in 25..27 && title.contains("Abhishek", ignoreCase = true) -> "https://www.youtube.com/@AbhishekVeeramalla"
                        
                        topicId == 28 && title.contains("Dojo", ignoreCase = true) -> "https://tutorialsdojo.com"
                        topicId == 28 && title.contains("Naukri", ignoreCase = true) -> "https://www.naukri.com"
                        topicId == 28 && title.contains("Instahyre", ignoreCase = true) -> "https://www.instahyre.com"
                        topicId == 28 && title.contains("LinkedIn", ignoreCase = true) -> "https://www.linkedin.com/jobs"
                        
                        else -> "https://example.com/devops/learning/week-$topicId-day-$day"
                    },
                    estimatedHours = when(day) {
                        6 -> 3.5
                        7 -> 3.0
                        else -> 1.5
                    },
                    orderIndex = day
                )
                repository.insertRoadmapSubtopic(subtopic)
            }
        }
    }
}
