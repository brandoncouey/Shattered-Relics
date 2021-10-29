FROM openjdk:8

ADD build/libs/world-*.jar .

ENV index = 1

ENV name = "Development"

ENV location = "US"

ENV type = "Normal"

ENV live = "true"

CMD java -jar world-*.jar $index $name $location $type $live
