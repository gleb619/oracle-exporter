FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY target/*.jar app.jar
ENTRYPOINT [
"java",
"-Djava.security.egd=file:/dev/./urandom",
"-server",
"-noverify",
"-Xms128m",
"-Xmx128m",
"-Xss512k",
"-XX:MetaspaceSize=64m",
"-XX:MaxMetaspaceSize=64m",
"-Dfile.encoding=UTF8",
"-DuriEncoding=UTF-8",
"-XX:+PrintCommandLineFlags",
"-XshowSettings:vm",
"-XX:+UseSerialGC",
"-XX:+ScavengeBeforeFullGC",
"-XX:+ExplicitGCInvokesConcurrentAndUnloadsClasses",
"-XX:+AlwaysPreTouch",
"-XX:+UnlockExperimentalVMOptions",
"-XX:+UseCGroupMemoryLimitForHeap",
"-jar",
"/app.jar"]