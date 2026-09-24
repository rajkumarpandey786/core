echo 'export PATH="/Applications/Docker.app/Contents/Resources/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc

To start in Foreground:
~~~~~~~~~~~~~~~~~~~~~~~
docker run --name hazelcast \
  -p 5701:5701 \
  hazelcast/hazelcast
 
To start in Foreground:
~~~~~~~~~~~~~~~~~~~~~~~
docker run -d \
  --name hazelcast \
  -p 5701:5701 \
  hazelcast/hazelcast

To run the Docker image:
~~~~~~~~~~~~~~~~~~~~~~~~~
To Start existing image=> docker start hazelcast
  

  localhost:5701
  
  docker ps
  
  docker logs hazelcast | tail -20
  
  lsof -i :5701
  
  exit => ctl +C 
  
  
  docker stop hazelcast
  docker rm hazelcast
  
  -------
  nano hazelcast.yaml
  
  ```yaml
hazelcast:
  cluster-name: dev

  cp-subsystem:
    cp-member-count: 1
```
  Ctrl + O
Enter
Ctrl + X

Start Hazelcast with this configuration
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
docker run -d \
  --name hazelcast \
  -p 5701:5701 \
  -v ~/hazelcast-local/hazelcast.yaml:/opt/hazelcast/config/hazelcast.yaml \
  hazelcast/hazelcast:5.7.0
  
  docker-logs
  ~~~~
  docker logs hazelcast
  
  docker logs hazelcast | grep -i cp
  
