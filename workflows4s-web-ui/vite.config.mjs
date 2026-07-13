import { defineConfig } from 'vite'
import scalaJSPlugin from "@scala-js/vite-plugin-scalajs";

// When sbt drives the build (workflows4s-web-ui-bundle), the Scala.js output is already
// linked and its location is passed via SCALAJS_OUTPUT_DIR. In that case we must not use
// the plugin, which spawns a nested sbt to find the output: it deadlocks against the
// already-running sbt 2 server. `npm run dev` (no env var) keeps using the plugin.
const scalaJSOutputDir = process.env.SCALAJS_OUTPUT_DIR;

const scalaJSFromEnv = {
    name: 'scalajs:resolve-from-env',
    resolveId(source) {
        if (!source.startsWith('scalajs:')) return null;
        return `${scalaJSOutputDir}/${source.substring('scalajs:'.length)}`;
    },
};

export default defineConfig({
    base: "/ui/",
    publicDir: false,
    build: {
        outDir: 'dist',
    },
    plugins: [
        scalaJSOutputDir
            ? scalaJSFromEnv
            : scalaJSPlugin({
                cwd: "..",
                projectID: "workflows4s-web-ui"
            })
    ],
    server: {
        port: 3000,
        proxy: {
            '/api': 'http://localhost:8081',
        }
    }
})
