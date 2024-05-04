plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.23"
    id("org.jetbrains.kotlin.kapt") version "1.9.23"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.9.23"
    id("org.jetbrains.kotlin.plugin.jpa") version "1.9.23"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.micronaut.application") version "4.3.6"
    id("io.micronaut.aot") version "4.3.6"
    id("io.sentry.jvm.gradle") version "4.4.0"
    alias(libs.plugins.serialization)
}

version = "0.1"
group = "com.example"
buildscript {
    dependencies {
        classpath("org.jetbrains.kotlinx:atomicfu-gradle-plugin:0.23.2")
        classpath("org.liquibase:liquibase-core:4.22.0")
        classpath("org.postgresql:postgresql:42.6.0")
    }
}
val kotlinVersion=project.properties.get("kotlinVersion")
repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.bundles.baseline)
    implementation(libs.bundles.arrow)
    implementation(libs.bundles.ktor)
    implementation(libs.bundles.serialization)
    implementation(libs.bundles.coroutines)
    implementation(platform(libs.sentryBom))
    implementation(platform(libs.otel.bom))
    implementation(platform(libs.otel.instrument.bom))
    api(libs.bundles.otel)
    implementation("io.sentry:sentry")
    implementation("io.sentry:sentry-kotlin-extensions")
    implementation("io.sentry:sentry-logback")
    implementation("io.sentry:sentry-opentelemetry-agent")
    implementation("io.sentry:sentry-opentelemetry-core")
    implementation("io.sentry:sentry-jdbc")
    kapt("io.micronaut.data:micronaut-data-processor")
    kapt("io.micronaut:micronaut-http-validation")
    kapt("io.micronaut.micrometer:micronaut-micrometer-annotation")
    kapt("io.micronaut.openapi:micronaut-openapi")
    kapt("io.micronaut.serde:micronaut-serde-processor")
    kapt("io.micronaut.validation:micronaut-validation-processor")
    implementation("com.ongres.scram:client:2.1")
    implementation("io.micrometer:context-propagation")
    implementation("io.micronaut:micronaut-aop")
    implementation("io.micronaut:micronaut-management")
    implementation("io.micronaut.cache:micronaut-cache-core")
    implementation("io.micronaut.cache:micronaut-cache-caffeine")
    implementation("io.micronaut.data:micronaut-data-hibernate-reactive")
    implementation("io.micronaut.kotlin:micronaut-kotlin-extension-functions")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut.micrometer:micronaut-micrometer-core")
    implementation("io.micronaut.reactor:micronaut-reactor")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation("io.micronaut.sql:micronaut-hibernate-reactive")
    implementation("io.micronaut.sql:micronaut-vertx-pg-client")
    implementation("io.micronaut.validation:micronaut-validation")
    implementation("io.micronaut.liquibase:micronaut-liquibase")
    implementation("io.vertx:vertx-pg-client")
    implementation("jakarta.validation:jakarta.validation-api")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
    compileOnly("io.micronaut:micronaut-http-client")
    compileOnly("io.micronaut.openapi:micronaut-openapi-annotations")
    runtimeOnly("ch.qos.logback:logback-classic")
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")
    runtimeOnly("org.yaml:snakeyaml")
    runtimeOnly("org.postgresql:postgresql:42.6.0")
    testImplementation("io.micronaut:micronaut-http-client")
    testImplementation("org.testcontainers:localstack")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:testcontainers")
    developmentOnly("io.micronaut.controlpanel:micronaut-control-panel-management")
    developmentOnly("io.micronaut.controlpanel:micronaut-control-panel-ui")
}


application {
    mainClass = "com.example.ApplicationKt"
    applicationDefaultJvmArgs += "-Xcontext-receivers"
}
java {
    sourceCompatibility = JavaVersion.toVersion("21")
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

graalvmNative.toolchainDetection = false
micronaut {
    runtime("netty")
    testRuntime("kotest5")
    processing {
        incremental(true)
        annotations("com.example.*")
    }
    aot {
    // Please review carefully the optimizations enabled below
    // Check https://micronaut-projects.github.io/micronaut-aot/latest/guide/ for more details
        optimizeServiceLoading = true
        convertYamlToJava = false
        precomputeOperations = true
        cacheEnvironment = true
        optimizeClassLoading = true
        deduceEnvironment = true
        optimizeNetty = true
        replaceLogbackXml = true
    }
}


tasks {
    optimizedDockerfileNative {
        baseImage("container-registry.oracle.com/graalvm/native-image:21")
        jdkVersion.set("21")
        graalImage.set("container-registry.oracle.com/graalvm/native-image:21")
    }
    optimizedDockerBuildNative {
        images.add("container-registry.oracle.com/graalvm/native-image:21")
        this.inputDir.set(File("."))
        this.dockerFile.set(File("./Dockerfile.native"))
    }
}


allOpen {
    annotation("jakarta.ws.rs.Path")
    annotation("jakarta.inject.Singleton")
    annotation("jakarta.persistence.Entity")
}
noArg {
    annotation("jakarta.persistence.Entity")
    annotation("kotlin.jvm.JvmInline")
}
