variable "aws_region" {
  description = "La région AWS"
  type        = string
  default     = "us-east-1"
}

variable "cluster_name" {
  description = "Nom du cluster EKS"
  type        = string
  default     = "mykubernetes"
}

variable "node_instance_type" {
  description = "Type d'instance des noeuds"
  type        = string
  default     = "t3.medium"
}