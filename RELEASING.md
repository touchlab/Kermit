# Releasing

In order to be able to release, you'll need write access to the repo.

Steps:

1. Make sure any PR's are applied and the release is ready to be published.
2. Switch to the branch you'll release from, and `git pull` the latest.
3. Change the version in `gradle.properties`.
4. Git add/commit with message "Version _._._"
5. Update `CHANGELOG.md` with a summary of changes
6. Push that change (directly to the branch, no PR).
7. Open Actions, select the `release` workflow on the left.
8. Select `Run Workflow` on the right, pick the branch, and click `Run Workflow`.
10. To check status, look for the library in [https://repo1.maven.org/maven2/co/touchlab/](https://repo1.maven.org/maven2/co/touchlab/), find an artifact, and refresh until the version you just published shows up.
