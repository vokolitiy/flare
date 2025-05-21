plugins {
    id("java")
}

group = "io.seatbooker"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web:3.4.5")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.4.5")
    implementation("org.springframework.boot:spring-boot-starter-security:3.4.5")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.4.5")
    implementation("org.springframework.boot:spring-boot-starter-test:3.4.5")
    //use H2 database instead of Postgres for development for now
    implementation("com.h2database:h2:2.3.230")
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
    implementation("io.jsonwebtoken:jjwt-jackson:0.11.5")
    testImplementation("com.jayway.jsonpath:json-path:2.9.0")
}

tasks.test {
    useJUnitPlatform()
}