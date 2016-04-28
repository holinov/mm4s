A Docker Deployer for Bots
---

An opinionated bot deployment API using the Docker REST API to deploy containers.

Stand up the DockerBot service
```
$ docker run -p 8080:8080 --rm -e DOCKER_HOST=172.17.0.1:2375 jwiii/dockerbots:0.2.2
```

Deploy container with call to the DockerBot service
```
$ curl -H 'Content-type: application/json' \
        -d '{"name":"notbot","image":"hello-world","ports":[]}' \
        localhost:8080/deploy
```

Result
```json
{
  "id": "b499ac8340f78150c894c250da5be1738ed064df218852188e3751fd34ba9a11",
  "request": {
    "name": "notbot",
    "image": "hello-world",
    "ports": []
  }
}
```

This was an example of deploying in a non-TLS enabled environment.

If TLS was enabled the `DOCKER_CERT_PATH` env var and port `2376` would be used on the `docker run`
