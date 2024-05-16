import time
from kubernetes import client, config

def get_node_count():
    v1 = client.CoreV1Api()
    nodes = v1.list_node()
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

def get_pod_status(name):
    v1 = client.CoreV1Api()
    pod = v1.read_namespaced_pod(name=name, namespace="default")
    return pod.status.phase

def delete_pod(name):
    v1 = client.CoreV1Api()
    v1.delete_namespaced_pod(name=name, namespace="default")

def wait_for_node_count(expected_count, timeout):
    start_time = time.time()
    while time.time() - start_time < timeout:
        if get_node_count() == expected_count:
            return True
        time.sleep(10)  # Проверяем каждые 10 секунд
    return False

def wait_for_pod_status(name, expected_status, timeout):
    start_time = time.time()
    while time.time() - start_time < timeout:
        if get_pod_status(name) == expected_status:
            return True
        time.sleep(10)  # Проверяем каждые 10 секунд
    return False

def main():
    config.load_kube_config()

    # Проверяем количество хостов (должно быть 1)
    assert wait_for_node_count(1, timeout=120), "Ожидался 1 хост в кластере"

    # Создаем первую поду
    create_pod("test-pod-1", "1", "500Mi")

    # Ждем, пока первая пода запустится (даем 2 минуты)
    assert wait_for_pod_status("test-pod-1", "Running", timeout=120), "Первая пода не запущена"

    # Проверяем количество хостов (должно быть 1)
    assert wait_for_node_count(1, timeout=120), "Ожидался 1 хост в кластере"

    # Создаем вторую поду
    create_pod("test-pod-2", "1", "500Mi")

    # Ждем, пока вторая пода запустится (даем 2 минуты)
    assert wait_for_pod_status("test-pod-2", "Running", timeout=120), "Вторая пода не запущена"

    # Проверяем количество хостов (должно быть 2)
    assert wait_for_node_count(2, timeout=120), "Ожидалось 2 хоста в кластере"

    # Удаляем вторую поду
    delete_pod("test-pod-2")

    # Ждем, пока вторая пода удалится (даем 2 минуты)
    assert wait_for_pod_status("test-pod-2", "Succeeded", timeout=120), "Вторая пода не удалена"

    # Проверяем, что первая пода все еще запущена
    assert wait_for_pod_status("test-pod-1", "Running", timeout=120), "Первая пода не запущена"

    # Проверяем количество хостов (должно быть 1)
    assert wait_for_node_count(1, timeout=120), "Ожидался 1 хост в кластере"

    print("Тест пройден успешно!")

if __name__ == "__main__":
    main()