# K3d 기반 로컬 Kubernetes 구축 및 배포 가이드

## 1. 프로젝트 구조

```text
.
├── api
│   ├── Dockerfile
│   ├── build.gradle
│   └── src
└── k8s
    ├── k3d-cluster.yaml
    └── base
        ├── namespace.yaml
        ├── configmap.yaml
        ├── secret.yaml
        ├── deployment.yaml
        ├── service.yaml
        └── ingress.yaml
```

---

# 2. Docker 이미지 생성

## Spring Boot 빌드

```bash
cd api
./gradlew clean bootJar
```

## Docker 이미지 생성

```bash
docker build -t api:1.0 .
```

## 이미지 확인

```bash
docker images | grep api
```

---

# 3. K3d 클러스터 생성

## k3d-cluster.yaml

```yaml
apiVersion: k3d.io/v1alpha5
kind: Simple

metadata:
  name: local-k8s

servers: 1
agents: 1

ports:
  - port: 8080:30080
    nodeFilters:
      - loadbalancer

options:
  k3d:
    wait: true
    timeout: "60s"

  kubeconfig:
    updateDefaultKubeconfig: true
    switchCurrentContext: true
```

## 클러스터 생성

```bash
k3d cluster create --config k8s/k3d-cluster.yaml
```

## 클러스터 확인

```bash
kubectl get nodes
```

---

# 4. Namespace 생성

## 00-namespace.yaml

```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: app
```

## 적용

```bash
kubectl apply -f k8s/base/namespace.yaml
```

## 확인

```bash
kubectl get ns
```

---

# 5. Docker 이미지 Import

Kubernetes가 로컬 Docker 이미지를 사용할 수 있도록 Import 수행

```bash
k3d image import api:1.0 -c local-k8s
```

확인

```bash
docker exec -it k3d-local-k8s-server-0 crictl images
```

---

# 6. ConfigMap 생성

## configmap.yaml

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: api-config
  namespace: app

data:
  SPRING_PROFILES_ACTIVE: "local"
  SERVER_PORT: "8080"
```

적용

```bash
kubectl apply -f k8s/base/configmap.yaml
```

---

# 7. Secret 생성

## secret.yaml

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: api-secret
  namespace: app

type: Opaque

stringData:
  API_KEY: "local-test-key"
  DB_USERNAME: "local-user"
  DB_PASSWORD: "local-password"
```

적용

```bash
kubectl apply -f k8s/base/secret.yaml
```

---

# 8. Deployment 생성

## deployment.yaml

```yaml
apiVersion: apps/v1
kind: Deployment

metadata:
  name: api
  namespace: app

spec:
  replicas: 2

  revisionHistoryLimit: 3

  selector:
    matchLabels:
      app: api

  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0

  template:
    metadata:
      labels:
        app: api

    spec:
      containers:
        - name: api
          image: api:1.0
          imagePullPolicy: Never

          ports:
            - containerPort: 8080

          envFrom:
            - configMapRef:
                name: api-config
            - secretRef:
                name: api-secret

          resources:
            requests:
              cpu: "250m"
              memory: "512Mi"
            limits:
              cpu: "500m"
              memory: "1Gi"

          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            initialDelaySeconds: 10
            periodSeconds: 5

          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            initialDelaySeconds: 30
            periodSeconds: 10
```

적용

```bash
kubectl apply -f k8s/base/deployment.yaml
```

확인

```bash
kubectl get pods -n app
```

---

# 9. Service 생성

## service.yaml

```yaml
apiVersion: v1
kind: Service

metadata:
  name: api-service
  namespace: app

spec:
  type: NodePort

  selector:
    app: api

  ports:
    - name: http
      port: 8080
      targetPort: 8080
      nodePort: 30080
```

적용

```bash
kubectl apply -f k8s/base/service.yaml
```

확인

```bash
kubectl get svc -n app
```

---

# 10. Ingress 생성

Ingress Controller 설치

```bash
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/cloud/deploy.yaml
```

설치 확인

```bash
kubectl get pods -n ingress-nginx
```

## ingress.yaml

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress

metadata:
  name: api-ingress
  namespace: app

spec:
  ingressClassName: nginx

  rules:
    - host: api.local
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: api-service
                port:
                  number: 8080
```

적용

```bash
kubectl apply -f k8s/base/ingress.yaml
```

확인

```bash
kubectl get ingress -n app
```

---

# 11. Hosts 등록

MacOS

```bash
sudo vi /etc/hosts
```

추가

```text
127.0.0.1 api.local
```

---

# 12. 접속 테스트

브라우저

```text
http://api.local:8080
```

또는

```bash
curl http://api.local:8080
```

---

# 13. 현재 학습 완료 범위

```text
Docker
→ k3d
→ Namespace
→ Deployment
→ Service
→ ConfigMap
→ Secret
→ Readiness Probe
→ Liveness Probe
→ Ingress
```

---

# 14. 다음 학습 예정

```text
Helm
→ ArgoCD
→ Prometheus/Grafana
→ ELK
→ StatefulSet
```
