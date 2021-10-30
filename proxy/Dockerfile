FROM openjdk:8

ADD build/libs/proxy-*.jar .

EXPOSE 18550:18550/tcp

ENV index = "1"

ENV live = "true"

CMD ./gradlew jarAll

CMD java -jar proxy-*.jar $index $live
