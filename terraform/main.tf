terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

# Le VPC par défaut du compte Learner Lab
data "aws_vpc" "default" {
  default = true
}

# Sous-réseaux du VPC par défaut (la zone 1e ne supporte pas EKS)
data "aws_subnets" "default" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.default.id]
  }

  filter {
    name   = "availability-zone"
    values = ["${var.aws_region}a", "${var.aws_region}b", "${var.aws_region}c"]
  }
}

# Rôle IAM existant dans Learner Lab
data "aws_iam_role" "lab_role" {
  name = "LabRole"
}

resource "aws_eks_cluster" "my_cluster" {
  name     = var.cluster_name
  role_arn = data.aws_iam_role.lab_role.arn

  vpc_config {
    subnet_ids = data.aws_subnets.default.ids
  }
}

resource "aws_eks_node_group" "my_node_group" {
  cluster_name    = aws_eks_cluster.my_cluster.name
  node_group_name = "noeud1"
  node_role_arn   = data.aws_iam_role.lab_role.arn
  subnet_ids      = data.aws_subnets.default.ids
  instance_types  = [var.node_instance_type]

  scaling_config {
    desired_size = 2
    max_size     = 3
    min_size     = 1
  }
}

# Ouvrir le NodePort 30000 sur le groupe de sécurité que EKS applique aux noeuds
resource "aws_security_group_rule" "nodeport" {
  type              = "ingress"
  from_port         = 30000
  to_port           = 30000
  protocol          = "tcp"
  cidr_blocks       = ["0.0.0.0/0"]
  security_group_id = aws_eks_cluster.my_cluster.vpc_config[0].cluster_security_group_id
}