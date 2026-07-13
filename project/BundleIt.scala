// format: off
// Convenience tooling for moving bundled frontend code into resources.

import java.io.File

object BundleIt {

  def bundle(from: File, to: File, scalaJSOutputDir: File): Set[File] = {
    val distDir = os.Path(from)

    // SCALAJS_OUTPUT_DIR makes vite reuse the already-linked Scala.js output instead of
    // spawning a nested sbt (which deadlocks against the running sbt 2 server).
    // Output is captured, not inherited: the daemonized server's pipes are never drained
    // and vite would block forever on write.
    val viteBuild =
      os.proc("npm", "run", "build")
        .call(
          cwd = distDir,
          env = Map("SCALAJS_OUTPUT_DIR" -> scalaJSOutputDir.getAbsolutePath),
          check = false,
          mergeErrIntoOut = true,
        )

    if (viteBuild.exitCode != 0) sys.error("Vite build failed:\n" + viteBuild.out.text())

    val sourceDir = distDir / "dist"
    val targetDir = os.Path(to) / "workflows4s-web-ui-bundle"

    os.makeDir.all(targetDir)

    os.walk(sourceDir)
      .filter(os.isFile)
      .map { file =>
        val targetPath = targetDir / file.relativeTo(sourceDir)
        os.copy.over(file, targetPath, createFolders = true)
        targetPath.toIO
      }
      .toSet
  }

}
