import time
import logging
from kubernetes import client, config

# Configure logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

def get_node_count():
    v1 = client.CoreV1Api()
    nodes = v1.list_node()
    logging.info(f"Current node count: {len(nodes.items)}")
    return len(nodes.items)

def create_pod(name, cpu, memory):
    v1 = client.CoreV1Api()
    pod = client.V1Pod(
        metadata=client.V1ObjectMeta(name=name),
        spec=client.V1PodSpec(
            containers=[
                client.V1Container(
                    name="test-container",
                    image="busybox",
                    command=["sleep", "3600"],
                    resources=client.V1ResourceRequirements(
                        requests={"cpu": cpu, "memory": memory}
                    ),
                )
            ]
        ),
    )
    v1.create_namespaced_pod(namespace="default", body=pod)
    logging.info(f"Created pod: {name}")

def get_pod_status(name):
    v1 = client.CoreV1Api()
    pod = v1.read_namespaced_pod(name=name, namespace="default")
    logging.info(f"Pod {name} status: {pod.status.phase}")
    return pod.status.phase

def delete_pod(name):
    v1 = client.CoreV1Api()
    v1.delete_namespaced_pod(name=name, namespace="default")
    logging.info(f"Deleted pod: {name}")

def wait_for_node_count(expected_count, timeout):
    start_time = time.time()
    while time.time() - start_time < timeout:
        if get_node_count() == expected_count:
            logging.info(f"Expected node count of {expected_count} reached")
            return True
        time.sleep(10)  # Check every 10 seconds
    logging.warning(f"Timeout reached while waiting for node count of {expected_count}")
    return False

def wait_for_pod_status(name, expected_status, timeout):
    start_time = time.time()
    while time.time() - start_time < timeout:
        if get_pod_status(name) == expected_status:
            logging.info(f"Pod {name} reached expected status: {expected_status}")
            return True
        time.sleep(10)  # Check every 10 seconds
    logging.warning(f"Timeout reached while waiting for pod {name} to reach status {expected_status}")
    return False

def main():
    config.load_kube_config()

    # Check the number of hosts (should be 1)
    logging.info("Checking initial node count")
    assert wait_for_node_count(1, timeout=120), "Expected 1 host in the cluster"

    # Create the first pod
    create_pod("test-pod-1", "1", "500Mi")

    # Wait for the first pod to start (giving 2 minutes)
    logging.info("Waiting for the first pod to start")
    assert wait_for_pod_status("test-pod-1", "Running", timeout=120), "First pod not started"

    # Check the number of hosts (should be 1)
    logging.info("Checking node count after creating the first pod")
    assert wait_for_node_count(1, timeout=120), "Expected 1 host in the cluster"

    # Create the second pod
    create_pod("test-pod-2", "1", "500Mi")

    # Wait for the second pod to start (giving 2 minutes)
    logging.info("Waiting for the second pod to start")
    assert wait_for_pod_status("test-pod-2", "Running", timeout=120), "Second pod not started"

    # Check the number of hosts (should be 2)
    logging.info("Checking node count after creating the second pod")
    assert wait_for_node_count(2, timeout=120), "Expected 2 hosts in the cluster"

    # Delete the second pod
    delete_pod("test-pod-2")

    # Wait for the second pod to be deleted (giving 2 minutes)
    logging.info("Waiting for the second pod to be deleted")
    assert wait_for_pod_status("test-pod-2", "Succeeded", timeout=120), "Second pod not deleted"

    # Check that the first pod is still running
    logging.info("Checking the status of the first pod")
    assert wait_for_pod_status("test-pod-1", "Running", timeout=120), "First pod not running"

    # Check the number of hosts (should be 1)
    logging.info("Checking node count after deleting the second pod")
    assert wait_for_node_count(1, timeout=120), "Expected 1 host in the cluster"

    logging.info("Test passed successfully!")

if __name__ == "__main__":
    main()