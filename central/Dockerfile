FROM openjdk:8

ADD build/libs/central-*.jar .

EXPOSE 18650:18650/tcp

ENV live = "true"

CMD java -jar central-*.jar $live
