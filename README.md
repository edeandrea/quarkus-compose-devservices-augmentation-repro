# Compose Dev Services start during augmentation, once per `@TestProfile`

Minimal reproducer (Quarkus 3.40.1.1): `compose-devservices.yml` defines one `nginx` service and three
`@QuarkusTest` classes each use their own `@TestProfile`.

```
./mvnw clean test -Dquarkus.http.test-port=0
```

## Expected
Each profile's app boots, runs its tests, shuts down; Compose containers for a profile exist only while
that profile's app is running (what the new Dev Services infra does for Kafka, Redis, ...).

## Actual
All three `docker|podman compose up -d` calls (one Compose project per profile, random suffix) run during
JUnit discovery/augmentation, before the first test (excerpt of `run-3.40.1.1.log`, podman):

```
07:42:21,887 ComposeRunner (build-23) Compose is running command: podman compose up -d
07:42:27,255 ComposeRunner (build-27) Compose is running command: podman compose up -d
07:42:30,917 ComposeRunner (build-28) Compose is running command: podman compose up -d
[INFO] Running repro.ProfileATest
>>> TEST START ProfileATest at 07:42:32.099
```

Three projects (`...-onajfh`, `...-ghvtpq`, `...-tpiwnc`) are up simultaneously. With N profiles and a real
Compose file that is N full stacks started up front.

## Cause
`ComposeDevServicesProcessor.config(...)` (a `@BuildStep`) calls `startCompose(...)` -> `compose.start()` directly.
#48988 / PR #54498 migrated only the registry bookkeeping, not the start.

Notes: `quarkus-jdbc-postgresql` is only there to bring the Dev Services infrastructure in
(`quarkus-devservices` is not directly addressable in the BOM); its DB dev service is disabled.
