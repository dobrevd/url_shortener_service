plugins {
    java
    id("org.springframework.boot") version "3.0.6"
    id("io.spring.dependency-management") version "1.1.0"
    id ("jacoco")
}

group = "faang.school"
version = "1.0"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}


dependencies {
    /**
     * Spring Boot Starters
     */
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:4.0.3")
    implementation ("org.springframework.kafka:spring-kafka")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    /**
     * Database
     */
    implementation("org.liquibase:liquibase-core")
    implementation("redis.clients:jedis:5.2.0")
    runtimeOnly("org.postgresql:postgresql")

    /**
     * Utils & Logging
     */
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.14.2")
    implementation("org.slf4j:slf4j-api:2.0.5")
    implementation("ch.qos.logback:logback-classic:1.5.3")
    implementation("ch.qos.logback:logback-core:1.5.3")
    implementation("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")
    implementation("org.mapstruct:mapstruct:1.5.3.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.3.Final")

    /**
     * Test Containers
     */
    testImplementation(platform("org.testcontainers:testcontainers-bom:1.19.1"))
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("com.redis.testcontainers:testcontainers-redis-junit-jupiter:1.4.6")
    testImplementation("org.testcontainers:kafka:1.17.6")
    /**
     * Tests
     */
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.mockito:mockito-core:5.0.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.0.0")
    testImplementation ("org.springframework.kafka:spring-kafka-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
}

tasks.withType<Test> {
    useJUnitPlatform(){
//        excludeTags("Tests_need_to_use_Testcontainers")
//        excludeTags("Integration-tests")
    }
    testLogging {
        showStandardStreams = true
    }
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        html.required.set(true)
        xml.required.set(true)
        csv.required.set(false)
    }
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.test)

    violationRules {
        rule {
            element = "BUNDLE"

            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.68".toBigDecimal()
            }
        }
    }
}

tasks.check {
    dependsOn(tasks.jacocoTestCoverageVerification)
}

tasks.bootJar {
    archiveFileName.set("app.jar")
}