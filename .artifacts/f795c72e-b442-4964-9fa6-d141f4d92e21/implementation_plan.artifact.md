# Implementation Plan - Force Docker Deployment on Railway

The previous deployment was still showing "Nixpacks" in the Railway UI because the `railway.json` configuration didn't explicitly specify the Docker builder. This plan updates the configuration to force the use of the `Dockerfile`.

## User Review Required

> [!IMPORTANT]
> This change explicitly tells Railway to use the `Dockerfile` in the root directory. This will resolve the "Nixpacks" deprecation warning and ensure your Ktor server builds correctly using the multi-stage Docker environment.

## Proposed Changes

### Railway Configuration

#### [MODIFY] [railway.json](file:///C:/Users/lampt/AndroidStudioProjects/PanaQet/railway.json)
- Add a `build` section with `builder: "DOCKERFILE"`.
- Set `dockerfilePath: "Dockerfile"`.

## Verification Plan

### Automated Tests
- Verify that `railway.json` follows the correct schema for Docker builds.

### Manual Verification
- Push the changes to GitHub.
- Verify in the Railway UI that the builder changes to **Docker** and the build succeeds.
- Check the deployment console to ensure the server starts using the `CMD` defined in the `Dockerfile`.
