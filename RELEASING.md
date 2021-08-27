# Releasing

In order to be able to release, you'll need an account at [https://oss.sonatype.org/](https://oss.sonatype.org/), and admin access to the repo.

To create an account, ask around the slack (if you're not working at Touchlab, this page isn't for you anyway. Sorry :( )

Steps:

1. Make sure any PR's are applied and the release is ready to be published.
2. Switch to the branch you'll release from, and `git pull` the latest.
3. Change the version in `gradle.properties`.
4. Git add/commit with message "Version _._._"
5. Push that change (directly to the branch, no PR).
6. Open Actions, select the `deploy` workflow on the left.
7. Select `Run Workflow` on the right, pick the branch, and click `Run Workflow`.
8. When complete, log into [https://oss.sonatype.org/](https://oss.sonatype.org/).
9. Select `Staging Profiles` on the left.
10. Visually review the pushed artifacts (briefly). Just get a sense that it looks correct.
11. Select all and click `Close`.
12. Hit the Refresh button periodically. Not the browser refresh. There's a button on the page called "Refresh". 
13. It'll take a while. Eventually `Release` will be enabled. Click that.
14. To check status, look for the library in [https://repo1.maven.org/maven2/co/touchlab/](https://repo1.maven.org/maven2/co/touchlab/), find an artifact, and refresh until the version you just published shows up.