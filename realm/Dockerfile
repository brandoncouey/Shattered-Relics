FROM openjdk:8

ADD build/libs/realm-*.jar .

ENV index = "1"

ENV live = "true"

EXPOSE 18750:18750/tcp

CMD java -jar realm-*.jar $index $live