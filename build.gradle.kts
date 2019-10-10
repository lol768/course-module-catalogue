import com.moowork.gradle.node.npm.NpmTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
  kotlin("plugin.jpa") version "1.3.31"
  id("org.springframework.boot") version "2.2.0.RC1"
  id("io.spring.dependency-management") version "1.0.7.RELEASE"
  kotlin("jvm") version "1.3.31"
  kotlin("plugin.spring") version "1.3.31"
  id("com.moowork.node") version "1.3.1"
}

group = "uk.ac.warwick"
version = "1.0.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

val developmentOnly by configurations.creating
configurations {
  runtimeClasspath {
    extendsFrom(developmentOnly)
  }
  compileOnly {
    extendsFrom(configurations.annotationProcessor.get())
  }
}

val nexusUser: String by project
val nexusPassword: String by project

repositories {
  maven { url = uri("https://repo.spring.io/milestone") }
  mavenCentral()
  maven {
    credentials {
      username = nexusUser
      password = nexusPassword
    }
    url = uri("https://mvn.elab.warwick.ac.uk/nexus/content/groups/public")
  }
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-freemarker")
  implementation("org.springframework.boot:spring-boot-starter-mail")
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  developmentOnly("org.springframework.boot:spring-boot-devtools")
  runtimeOnly("org.postgresql:postgresql")
  annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  implementation("org.flywaydb:flyway-core:6.0.3")
  implementation("uk.ac.warwick.sso:sso-client:2.76")
  implementation("uk.ac.warwick.util:warwickutils-core:20190916")
  implementation("uk.ac.warwick.util:warwickutils-web:20190916")
  implementation("net.logstash.logback:logstash-logback-encoder:5.3")
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = listOf("-Xjsr305=strict")
    jvmTarget = "1.8"
  }
}

tasks.named("classes") {
  dependsOn(":assets")
}

tasks.register<NpmTask>("installNpmDependencies") {
  inputs.file("package.json").withPathSensitivity(PathSensitivity.RELATIVE)
  outputs.dir("node_modules")

  setArgs(mutableListOf("ci"))
}

tasks.register<NpmTask>("assets") {
  dependsOn(":installNpmDependencies")

  inputs.files("webpack.config.babel.js", "package.json").withPathSensitivity(PathSensitivity.RELATIVE)
  inputs.dir("build-tooling").withPathSensitivity(PathSensitivity.RELATIVE)
  inputs.dir("src/main/assets").withPathSensitivity(PathSensitivity.RELATIVE)
  outputs.dir("build/resounces/main/static/assets")
  outputs.cacheIf { true }

  setArgs(mutableListOf("run", "build"))
}

tasks.withType<BootJar> {
  archiveFileName.set("app.jar")
  launchScript()

  manifest {
    attributes("Main-Class" to "org.springframework.boot.loader.PropertiesLauncher")
  }
}

tasks.withType<BootRun> {
  classpath += files("external-conf")
}
