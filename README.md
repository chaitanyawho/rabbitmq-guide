# RabbitMQ + K8s Monitoring Stack 

> Consider going through the [Wiki](https://github.com/chaitanyawho/rabbitmq-guide/wiki) for a more detailed tutorial on setting up all the components in this stack.

This repository contains the boilerplate code for setting up RabbitMQ with multiple producers and consumers, deployment on Kubernetes, and monitoring using Prometheus and Grafana.

1. Producer
    - Each producer submits messages (representing one submission) with an interval
      of `SUBMISSION_INTERVAL` _milliseconds_
    - To mock submission complexity, a random number representing work duration is appended at the
      end of every event payload.
    - The producers will produce messages not exceeding the `SUBMISSION_COUNT` env
      variable (see docker-compose.yml)

2. Consumer
    - Each consumer service has two worker threads which consume messages from the message queue.
    - The worker threads will _process_ submissions by mocking the grading process (by waiting for
      the work duration specified in the event)

3. RabbitMQ
    - The queue runs on port 5672. To access it from another container on the same network, the host
      should be set as `rabbitmq`
    - The queue can be monitored locally by using the management dashboard on `localhost:15672`

**To run the setup locally, run**

```bash
docker compose up -d
```

Optionally, to run services selectively, run

```bash
docker compose up -d [service-name]
```

---

## Deployment on Kubernetes

To deploy the POC on a k8s cluster,

1. Run the RabbitMQ cluster operator for Kubernetes
   ```bash
    kubectl apply -f k8s/rabbitmq-definition.yaml -n mqg-ns
   ```

2. Verify that the operator is running
   ```bash
   kubectl get all -n mqg-ns
   ```

3. Launch the producers
   ```bash
   kubectl apply -f k8s/producer1-deployment.yaml
   ```
4. Launch the consumers
   ```bash
   kubectl apply -f k8s/consumer1-deployment.yaml
   ```

---

## Monitoring RabbitMQ with Prometheus & Grafana on K8s

1. Set up a Prometheus stack on the k8s cluster:
    1. Add the helm repo for prometheus
       ```bash
       helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
       helm repo update prometheus-community
       ```
    2. Use the helm chart to deploy the prometheus-kube-stack, which will install install
       Prometheus, Promtail, Alertmanager and Grafana on the cluster.
        ```bash
        helm install kube-prom-stack prometheus-community/kube-prometheus-stack --version "60.0.1" -n mqg-ns -f k8s/monitoring/prom-stack-values.yaml
        ```
    3. Verify that the operator is up and running
        ```bash
        kubectl get prometheus -n mqg-ns
        ```

2. Set up RabbitMQ ServiceMonitor and PodMonitor

   ```bash
    kubectl apply -n mqg-ns -f https://raw.githubusercontent.com/rabbitmq/cluster-operator/main/observability/prometheus/monitors/rabbitmq-servicemonitor.yml
    kubectl apply -n mqg-ns -f https://raw.githubusercontent.com/rabbitmq/cluster-operator/main/observability/prometheus/monitors/rabbitmq-cluster-operator-podmonitor.yml
   ```

   Once the mointors are created, prometheus-operator should automatically
   detect these objects and reload Prometheus' scrape_config

3. Access Grafana dashboards on localhost:3000
   ```bash
    kubectl port-forward svc/kube-prom-stack-grafana 3000:80 -n mqg-ns
   ```
   Import dashboards for RabbitMQ (
   e.g. https://grafana.com/grafana/dashboards/10991-rabbitmq-overview/)
   Select datasource as prometheus. Once the dashboard is imported, you should be able to see
   visualizations of the collected metrics from RabbitMQ
