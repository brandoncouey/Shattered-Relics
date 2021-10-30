FROM openjdk:8

ADD build/libs/channel-*.jar .

ENV index = "1"

ENV live = "true"

CMD java -jar channel-*.jar $index $live