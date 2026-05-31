package com.example.data.entity

data class DailyTaskDetail(
    val topicName: String,
    val learningObjectives: List<String>,
    val studyGuide: String,
    val caseStudy: String,
    val assessmentMapping: String,
    val revisionMapping: String
)

object SreCurriculum {

    fun getWeekTitle(week: Int): String {
        return when (week) {
            1 -> "Linux OS & File System"
            2 -> "Linux Administration & Processes"
            3 -> "Networking + SSH + Bash Scripting"
            4 -> "Git + GitHub + Python for DevOps"
            5 -> "AWS Account Setup + Cost + IAM"
            6 -> "EC2 Deep Dive"
            7 -> "Load Balancing + Auto Scaling + S3"
            8 -> "Route53 + CloudWatch + Systems Manager"
            9 -> "VPC Fundamentals"
            10 -> "Advanced Networking + Security Services"
            11 -> "RDS + DynamoDB + ElastiCache"
            12 -> "Serverless + Application Services"
            13 -> "CloudFormation + AWS Well-Architected"
            14 -> "AWS Cost Optimization + FinOps"
            15 -> "Docker Fundamentals"
            16 -> "Docker Compose + ECR + Security"
            17 -> "ECS (Elastic Container Service)"
            18 -> "GitHub Actions CI/CD"
            19 -> "DevSecOps — Security in Pipelines"
            20 -> "Jenkins + GitLab CI"
            21 -> "Ansible — Configuration Management"
            22 -> "Kubernetes Fundamentals"
            23 -> "EKS + HELM + ArgoCD GitOps"
            24 -> "K8s Security + Monitoring on EKS"
            25 -> "Terraform Fundamentals + Intermediate"
            26 -> "Terraform Advanced + Project"
            27 -> "AIOps for Cloud + AWS AI Services"
            28 -> "SAA-C03 + Final Project + Launch"
            else -> "Ultimate SRE & DevOps Integration"
        }
    }

    fun getDailyTaskDetail(week: Int, day: Int): DailyTaskDetail {
        val weekTitle = getWeekTitle(week)
        
        // Base topics for each week
        val topicName = when (week) {
            1 -> when (day) {
                1 -> "Linux File System Structure & Hierarchy"
                2 -> "Standard Directory Navigation Methods"
                3 -> "File & Directory Management (touch, cp, mv, rm)"
                4 -> "File Searching and Patterns Filtering (find, grep)"
                5 -> "File Permissions, Chmod & Security Masking"
                6 -> "User and Group Identity Management (useradd)"
                else -> "Weekly Linux File System Comprehensive Review"
            }
            2 -> when (day) {
                1 -> "Process State Management & Core Vitals Monitoring"
                2 -> "Disk Space & Memory Allocations (df, du, free)"
                3 -> "Package Management (yum, dnf, apt)"
                4 -> "Systemd Service Architecture & Control (systemctl)"
                5 -> "Logging Contexts & Log Parsing (journalctl)"
                6 -> "Advanced System Debugging & Sockets (lsof, strace)"
                else -> "Weekly Administration & Core Diagnostics Review"
            }
            3 -> when (day) {
                1 -> "IP Addressing, CIDR Subnets & Class Schemes"
                2 -> "Common DevOps Port Mapping & Protocol Handshakes"
                3 -> "SSH Secure Shell Keypair Generation & Security Host Locking"
                4 -> "SSH Port Tunnelling & Bastion Gateway Proxies"
                5 -> "Network Diagnostics & Sockets Auditing (curl, telnet, ss)"
                6 -> "Bash Scripting Core Logic (Variables, Loops, Conditions)"
                else -> "Weekly Networking & Secure Command review"
            }
            4 -> when (day) {
                1 -> "Git Config, Initializers & Repository Management"
                2 -> "Typical Git Commit Core Loops & Remote Push Workflows"
                3 -> "Git Branching Strategy & Merge Conflict Resolution"
                4 -> "Git Stash & Log Timeline Investigations"
                5 -> "Undoing Commits Safely (revert, restore, amend)"
                6 -> "Python Scripting for DevOps & Configuration Parsing"
                else -> "Weekly Code Control & Automation Review"
            }
            5 -> when (day) {
                1 -> "AWS Account Provisioning & Billing Alarm Setup"
                2 -> "AWS Free Tier Coverage & Cost Allocations"
                3 -> "IAM Users, Groups & Basic Policies Infrastructure"
                4 -> "Secure IAM Roles & Resource Delegation"
                5 -> "AWS Single Sign-On (MFA) & AWS Organizations"
                6 -> "IAM Troubleshooting & Credentials Audits"
                else -> "Weekly Cloud Governance & Security Review"
            }
            6 -> when (day) {
                1 -> "EC2 Compute Instances Deployments & AMI Choices"
                2 -> "Sizing EC2 Instance Types under Virtualized Limits"
                3 -> "Security Groups Rules Mapping & Virtual Network Cards"
                4 -> "Automating VM Bootstrapping with User Data Custom Shells"
                5 -> "Understanding EBS Block Volumes Attach & Ext4 Formatting"
                6 -> "SSH Keypairs & SSH config connection shortcuts"
                else -> "Weekly EC2 Compute Space Review"
            }
            7 -> when (day) {
                1 -> "Application Load Balancer (ALB) Routing (L7)"
                2 -> "Network Load Balancer (NLB) Protocol Routing (L4)"
                3 -> "Auto Scaling Groups (ASG) Thresholds Policies Setup"
                4 -> "Amazon S3 Buckets, Storage Classes & Lifecycles"
                5 -> "Amazon S3 Access Locks & Bucket Policies"
                6 -> "S3 Cross-Region Replication & Object Locks"
                else -> "Weekly Load Balancing & Object Storage Review"
            }
            8 -> when (day) {
                1 -> "Route53 Hosted Zones & Record Types (A, CNAME, MX)"
                2 -> "Route53 Advanced Routing Policies (Failover, Latency)"
                3 -> "Route53 Health Checks & VPC Route Resolvers"
                4 -> "AWS CloudWatch Metrics & SRE Dashboards"
                5 -> "AWS CloudWatch Alarms & EventBridge Rules"
                6 -> "Systems Manager Parameter Store & Session Manager Access"
                else -> "Weekly AWS Host Routing & Observability Review"
            }
            9 -> when (day) {
                1 -> "VPC Subnet Architecture (Public, Private, Isolated tiers)"
                2 -> "Internet Gateways (IGW) & Route Tables Mapping"
                3 -> "NAT Gateways vs NAT Instances"
                4 -> "Security Groups vs Network ACLs"
                5 -> "VPC Endpoint Gateway vs Interface Endpoints"
                6 -> "VPC Flow Logs Capture & Traffic Audits"
                else -> "Weekly Virtual Private Cloud Review"
            }
            10 -> when (day) {
                1 -> "VPC Peering Connections Setup & Routing"
                2 -> "AWS Transit Gateway (TGW) Hub-and-Spoke networks"
                3 -> "AWS Client VPN vs Site-to-Site tunnels"
                4 -> "AWS Direct Connect (DX) Dedicated Lines"
                5 -> "AWS WAF Web Application Firewall configuration"
                6 -> "Network Reachability Analyzer troubleshooting"
                else -> "Weekly Advanced Cloud Connectivity Review"
            }
            else -> when (day) {
                1 -> "Architectural Core Basics of $weekTitle"
                2 -> "Advanced Parameters & Deep Divergent Configuration"
                3 -> "Production Toolchain Selection & Integration Mapping"
                4 -> "Disaster Recovery Rules & Incident Troubleshooting Paths"
                5 -> "Industry Best Practices & Hardening Verification Standards"
                6 -> "Telemetry Diagnostics Lab & Metrics Collection"
                else -> "Comprehensive Cognitive Level Assessment Check"
            }
        }

        val objectives = when (week) {
            1 -> listOf("Grasp standard files layout", "Optimize directory routing", "Secure critical system items")
            2 -> listOf("Analyze processes and CPU usage", "Format new disk partitions", "Configure systemd daemons")
            3 -> listOf("Understand IPv4 CIDR boundaries", "Generate SSH keypairs safely", "Write functional Bash automation loops")
            4 -> listOf("Implement Git workflows correctly", "Resolve merge conflicts", "Build Python automation scripts")
            5 -> listOf("Setup account spending locks", "Establish IAM policy boundaries", "De-authorize credentials risks")
            6 -> listOf("Compute EC2 instance limits", "Format ext4 and xfs EBS volumes", "Establish bastion host pathways")
            7 -> listOf("Design fault-tolerant load balancing", "Configure ASG auto-recovery", "Enforce S3 lifecycle savings policies")
            8 -> listOf("Configure DNS routing tables", "Grasp CloudWatch metrics alarms", "Enforce AWS Systems Manager access")
            9 -> listOf("Isolate database resources in secure tiers", "Calculate subnets CIDR layouts", "Inspect flow logs traces")
            10 -> listOf("Connect multiple AWS VPC boundaries", "Configure high-speed Transit Gateways", "Setup Web Application Firewalls")
            else -> listOf("Grasp advanced cloud automation parameters", "Troubleshoot system faults", "Perform SRE validation loops")
        }

        val studyGuide = "This study session focuses on: $topicName. Analyze how modern DevOps architectures implement these techniques under real enterprise loads. For instance, in $weekTitle, understanding exact parameters prevents system thrashing, security breaches, or unexpected cost overheads. Study the standard commands, review the terminal parameters, and prepare to verify your absolute comprehension in the cognitive lab quiz."

        val caseStudy = "PRODUCTION ANALYSIS: A major cloud infrastructure team experienced node throttling due to unmapped dependencies on $topicName. Automated configurations and restricted access boundaries rehabilitated performance, preserving 40% computing resource space and securing the overall SRE pipeline."

        val assessment = "Cognitive quiz mapping on $topicName covering core architecture options and scenario troubleshooting."
        val revision = "Automatically scheduled spaced-repetition logic for $weekTitle based on final assessment score."

        return DailyTaskDetail(
            topicName = topicName,
            learningObjectives = objectives,
            studyGuide = studyGuide,
            caseStudy = caseStudy,
            assessmentMapping = assessment,
            revisionMapping = revision
        )
    }
}
