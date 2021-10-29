docker build -t channel .
docker tag channel:latest 344855247956.dkr.ecr.us-east-2.amazonaws.com/channel:latest
docker push 344855247956.dkr.ecr.us-east-2.amazonaws.com/channel:latest