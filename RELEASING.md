# Releasing

To be able to release, you'll need write access to the repo.

Steps:

1. Make sure any PRs are merged and the release is ready to be published.
2. Switch to a new release branch and `git pull` the latest.
3. Change the version in `gradle.properties`.
4. Update `CHANGELOG.md` with a summary of changes.
5. Push the changes with the message "Version x.y.z."
6. Open a PR to merge the release branch into `main`.
7. After the PR got merged, open `Actions` and select the `release` workflow on the left.
8. Select `Run Workflow` on the right, pick the `main` branch, and click `Run Workflow`.
9. To check status, look for the library in [https://repo1.maven.org/maven2/co/touchlab/](https://repo1.maven.org/maven2/co/touchlab/), find an artifact, and refresh until the version you just published shows up.
