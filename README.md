# radar-tracking-system

[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=flat-square&logo=linkedin&colorB=555
[linkedin-url]: https://linkedin.com/in/boraciner
[![LinkedIn][linkedin-shield]][linkedin-url]

<br />
<p align="center">
    <img src="sys.jpeg" alt="System Diagram">
    <h3 align="center">Multi-Target Radar Tracking System with Kalman Filter</h3>
</p>

A microservices-based radar tracking system built with Spring Boot. It simulates multiple radar targets, runs a Kalman filter tracker, and visualises raw plots, ground truth, and filtered tracks side by side in a real-time canvas UI.

---

## Stack

- **Java 21** / **Spring Boot 3.4.1** / **Spring Cloud 2024.0.0 (Leyton)**
- **Apache Kafka** — message bus between services
- **Eureka** — service discovery
- **HTML5 Canvas + WebSocket** — live radar scope in the browser

---

## Services

| Service | Port | Role |
|---|---|---|
| `naming-service` | 8761 | Eureka server — service registry |
| `radar-service` | 8000 | Simulates 3 independent targets with Gaussian measurement noise |
| `plot-listener-service` | 8100 | REST → Kafka bridge, publishes plots to `PlotTopic` |
| `tracker-service` | 8200 | Kalman filter tracker, publishes confirmed tracks to `TrackTopic` |
| `map-viewer-service` | 8080 | WebSocket server + canvas radar scope UI |

---

## How It Works

```
radar-service  →  plot-listener-service  →  [PlotTopic]
                                                 ↓
                                         tracker-service  →  [TrackTopic]
                                                 ↓                  ↓
                                         map-viewer-service (Kafka consumer)
                                                 ↓
                                           WebSocket /user
                                                 ↓
                                           Browser UI (canvas)
```

**Radar service** generates 3 targets every second, each following a distinct trajectory with Gaussian noise (σ = 0.15) added to simulate real sensor measurements:
- Target 1 — parabolic (U-shape)
- Target 2 — diagonal line
- Target 3 — sinusoidal wave

Each `Plot` message carries both the noisy measurement (`x`, `y`) and the true position (`trueX`, `trueY`) so tracker performance can be evaluated visually.

**Tracker service** runs a 2D constant-velocity Kalman filter on each target:
- State vector: `[x, y, vx, vy]`
- Nearest-neighbour data association with Euclidean gate = 2.0 units
- Track lifecycle: `TENTATIVE` (< 3 hits) → `CONFIRMED` → `DELETED` (5 consecutive misses)
- Only confirmed tracks are published to `TrackTopic`

**Map viewer** renders three independently toggleable layers on an HTML5 canvas:
- **×** Raw plots — noisy measurements, colour-coded by target
- **▲** Ground truth — true positions before noise was applied
- **○** Kalman tracks — filtered estimates with fading trails and track ID labels

---

## Running Locally

Make sure Kafka and Zookeeper are running first:

```bash
docker-compose up -d
```

Then start all services (opens a separate terminal window for each):

```bash
start-all.bat
```

Or start them manually in this order:

```bash
# 1 — Eureka
cd naming-service && mvn spring-boot:run

# 2 — Kafka bridge
cd plot-listener-service && mvn spring-boot:run

# 3 — Tracker
cd tracker-service && mvn spring-boot:run

# 4 — Map viewer
cd map-viewer-service && mvn spring-boot:run

# 5 — Radar (start last so the pipeline is ready)
cd radar-service && mvn spring-boot:run
```

Once everything is up:

| URL | What |
|---|---|
| `http://localhost:8080` | Radar scope UI |
| `http://localhost:8761` | Eureka dashboard |

---

## Radar Scope UI

The map viewer at `http://localhost:8080` shows a live 700×700 canvas with:

- Three layer toggles (Plots / Tracks / Truth) in the control panel
- Colour-coded targets and tracks with fading history trails
- Side panel showing active track count, per-track velocity estimates, and target legend
- Auto-reconnect if the WebSocket drops

---

## Kafka Topics

| Topic | Producer | Consumer(s) |
|---|---|---|
| `PlotTopic` | plot-listener-service | tracker-service, map-viewer-service |
| `TrackTopic` | tracker-service | map-viewer-service |
