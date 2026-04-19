// Aggregator root — no code here, only version management tasks

tasks.register("checkVersions") {
    group = "versioning"
    description = "Queries Maven Central and prints available updates for libs.versions.toml"
    doLast {
        val toml = file("gradle/libs.versions.toml").readText()
        val checks = mapOf(
            "springBoot"    to ("org.springframework.boot"       to "spring-boot"),
            "springCloud"   to ("org.springframework.cloud"      to "spring-cloud-dependencies"),
            "springDepMgmt" to ("io.spring.dependency-management" to "io.spring.dependency-management.gradle.plugin"),
            "commonsMath3"  to ("org.apache.commons"             to "commons-math3")
        )

        println("\n=== Dependency Version Check ===")
        checks.forEach { (alias, gav) ->
            val (group, artifact) = gav
            val currentRegex = Regex("""$alias\s*=\s*"([^"]+)"""")
            val current = currentRegex.find(toml)?.groupValues?.get(1) ?: "unknown"
            try {
                val url = java.net.URI(
                    "https://search.maven.org/solrsearch/select?q=g:${group}+AND+a:${artifact}&rows=1&wt=json"
                ).toURL()
                val json = url.readText()
                val latest = Regex(""""latestVersion":"([^"]+)"""").find(json)?.groupValues?.get(1) ?: "?"
                val status = if (current == latest) "  up to date" else "  UPDATE AVAILABLE: $current -> $latest"
                println("  $alias$status")
            } catch (e: Exception) {
                println("  $alias  (could not reach Maven Central)")
            }
        }
        println()
        println("To apply updates, edit gradle/libs.versions.toml and re-run ./gradlew build")
    }
}
