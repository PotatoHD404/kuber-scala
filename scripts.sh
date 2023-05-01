minikube start --nodes 2 -p multinode-demo

kubectl uncordon multinode-demo-m02
kubectl uncordon multinode-demo

kubectl scale --replicas=2 deployment/hello-minikube

kubectl get pods -o wide

kubectl delete pod hello-minikube-7d896cdd88-zns9m && kubectl scale --replicas=5 deployment/hello-minikube