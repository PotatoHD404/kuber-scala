# K8s Autoscaler

[Русский](README.md) | [English](README_EN.md)

K8s Autoscaler is a project that uses K3s, Terraform, and Scala to automatically scale a Kubernetes cluster based on
resource demand. This README provides instructions on how to set up and run the project.

## Getting Started

To get started with the project, clone the repository:

```
git clone https://gitlab.com/PotatoHD/k8s-autoscaler.git
```

If you want to make changes to the project, fork the repository and create a merge request (pull request) with your
changes.

## Minimal Configuration

### Prerequisites

Before you begin, ensure that you have Docker installed on your system. Follow the instructions for your operating
system to install Docker:

- **Windows**: [Install Docker Desktop for Windows](https://docs.docker.com/docker-for-windows/install/)
- **macOS**: [Install Docker Desktop for Mac](https://docs.docker.com/docker-for-mac/install/)
- **Linux**: [Install Docker Engine](https://docs.docker.com/engine/install/)

### Configuration

1. Create a `.env` file in the root directory of the project and specify the following environment variables:

   ```
   YC_TOKEN="your_yandex_cloud_token"
   YC_CLOUD_ID="your_yandex_cloud_id"
   YC_FOLDER_ID="your_yandex_folder_id"
   YC_ZONE="your_yandex_zone"
   K3S_TOKEN="your_k3s_token"
   S3_ACCESS_KEY="your_s3_access_key"
   S3_SECRET_KEY="your_s3_secret_key"
   ```

   Replace the values with your actual data.

2. Generate an SSH key pair (private and public) and save the public key in a file named `id_rsa.pub` in the root
   directory of the project. To generate the keys, run the following command:

   ```
   ssh-keygen -t rsa -b 4096 -C "your_email@example.com"
   ```

   Replace `"your_email@example.com"` with your email address or any other identifier. Follow the prompts to save the
   keys.

### Running the Project

To run the project using Docker Compose, use the following commands:

- Creating a cluster:
  ```
  docker-compose up cluster-creator
  ```

- Destroying a cluster:
  ```
  docker-compose up cluster-destroyer
  ```

After the cluster is successfully created, the public and internal IP addresses of the virtual machines will be
displayed. You can use the public IP addresses to connect to the virtual machines using SSH.

## Local Build and Run

### Prerequisites

To build and run the project locally, ensure that you have the following components installed on your system:

- Java Development Kit (JDK) 11 or higher
- Scala 2.13 or higher
- Scala Build Tool (SBT)
- IntelliJ IDEA (optional, for local development)

### Installing JDK, Scala, and SBT

1. Download and install Java Development Kit (JDK) 11 or higher from
   the [official Oracle website](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) or use a package
   manager for your operating system.

2. Install Scala by following the instructions on the [official Scala website](https://www.scala-lang.org/download/).

3. Install Scala Build Tool (SBT) by following the instructions on
   the [official SBT website](https://www.scala-sbt.org/download.html).

### Installing IntelliJ IDEA (optional)

If you prefer to develop locally using an IDE, you can install IntelliJ IDEA by following these steps:

1. Download IntelliJ IDEA from the [official JetBrains website](https://www.jetbrains.com/idea/download/).
2. Follow the installation instructions for your operating system.

### Building and Pushing Docker Images

1. Build the Docker image for the autoscaler using the following command:

   ```
   docker build -t potatohd/autoscaler -f Dockerfile.autoscaler .
   ```

   If you want to use your own image name, replace `potatohd/autoscaler` with your desired name.

2. Build the Docker image for the cluster creator using the following command:

   ```
   docker build -t potatohd/cluster-creator -f Dockerfile.cluster-creator .
   ```

   If you want to use your own image name, replace `potatohd/cluster-creator` with your desired name.

3. Push the built images to a container registry, such as Docker Hub:

   ```
   docker push potatohd/autoscaler
   docker push potatohd/cluster-creator
   ```

   Replace `potatohd` with your Docker Hub username or another container registry.

   If you are using your own image names, make sure to update the `image` field in the `helm/values.yaml` file for the
   autoscaler and the `image` field in the `docker-compose.yml` file for the cluster creator.

### Building the Helm Chart

1. Navigate to the `helm` directory:

   ```
   cd helm
   ```

2. Build the Helm chart using the following command:

   ```
   helm package .
   ```

   A Helm chart archive will be created, e.g., `k8s-autoscaler-0.1.0.tgz`.

3. Push the built Helm chart to a Helm repository or host it in a location accessible for installation.

### Local Run

1. Navigate to the root directory of the project:

   ```
   cd k8s-autoscaler
   ```

2. Build the project using SBT:

   ```
   sbt compile
   ```

3. Run the `Main` class in the `autoscaler` module:

   ```
   sbt "autoscaler/run"
   ```

   Alternatively, open the project in IntelliJ IDEA, locate the `Main` class in the `autoscaler` module, and run it.

## Configuration

Before running the project, you need to configure the necessary environment variables and files.

### Environment Variables

Create a `.env` file in the root directory of the project and specify the following environment variables:

```
YC_TOKEN="your_yandex_cloud_token"
YC_CLOUD_ID="your_yandex_cloud_id"
YC_FOLDER_ID="your_yandex_folder_id"
YC_ZONE="your_yandex_zone"
K3S_TOKEN="your_k3s_token"
S3_ACCESS_KEY="your_s3_access_key"
S3_SECRET_KEY="your_s3_secret_key"
```

Before replacing the values, make sure you have an account in [Yandex Cloud](https://cloud.yandex.com/) and have created
at least one cloud and folder within it.

Replace the values with your actual data:

- `YC_TOKEN`: Your Yandex Cloud API token. You can obtain it from
  the [Yandex Cloud management console](https://console.cloud.yandex.com/cloud?section=overview). Go to the "Service
  Accounts" section, create a service account with the admin role, and create a new API key.
- `YC_CLOUD_ID`: The ID of your Yandex Cloud. You can find it in
  the [Yandex Cloud management console](https://console.cloud.yandex.com/cloud?section=overview) at the top.
- `YC_FOLDER_ID`: The ID of the folder in your Yandex Cloud where the resources will be created. You can find it in
  the [Yandex Cloud management console](https://console.cloud.yandex.com/cloud?section=overview) under the "Folders"
  section.
- `YC_ZONE`: The Yandex Cloud zone where the resources will be created (e.g., `ru-central1-a`). You can choose an
  appropriate zone from the [Yandex Cloud documentation](https://cloud.yandex.com/docs/overview/concepts/geo-scope) in
  the "Availability Zones" section.
- `K3S_TOKEN`: The token used for authentication in the K3s cluster. You can generate a random token or use an existing
  one (e.g., `86cl4b19zfxrto2ybwnfi4nvqixfo1hqyyj77j9qiqetqr8k`).
- `S3_ACCESS_KEY`: The access key for your S3-compatible storage. You can obtain it from your Yandex Cloud console for
  the service account you created earlier. Create a new access key for it, copy the public part, and keep the private
  part for the next step.
- `S3_SECRET_KEY`: The secret key for your S3-compatible storage. Insert the private part obtained in the previous step.

### SSH Key

Create a file named `id_rsa.pub` in the root directory of the project and paste your public SSH key into it. This key
will be used to connect to the virtual machines in the cluster.

To generate an SSH key pair (private and public), follow these steps:

1. Open a terminal or command prompt on your local machine.
2. Run the following command to generate the SSH key pair:
   ```
   ssh-keygen -t rsa -b 4096 -C "your_email@example.com"
   ```
   Replace `"your_email@example.com"` with your email address or any other identifier.
3. When prompted, press Enter to accept the default location for saving the key pair (`~/.ssh/id_rsa`).
4. Optionally, you can enter a passphrase for additional security. If you don't want to set a passphrase, leave it empty
   and press Enter.
5. The SSH key pair will be generated. You will have two files:
    - `~/.ssh/id_rsa`: The private key. Keep this file secure and do not share it with anyone.
    - `~/.ssh/id_rsa.pub`: The public key. This file can be shared and used for authentication.

Copy the contents of the `~/.ssh/id_rsa.pub` file and paste them into the `id_rsa.pub` file in the root directory of the
project.

## Contributing

If you want to contribute to the project, please follow the standard GitLab workflow:

1. Fork the repository.
2. Create a new branch for your feature or bug fix.
3. Make changes and commit them with descriptive commit messages.
4. Push the changes to your forked repository.
5. Create a merge request (pull request) to the main repository.

## License

This project is licensed under the [MIT License](LICENSE.md).