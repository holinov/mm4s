Mattermost for Scala (mm4s)
==========================
[![Build Status](https://travis-ci.org/jw3/mm4s.svg?branch=master)](https://travis-ci.org/jw3/mm4s)
[![Dependencies](https://app.updateimpact.com/badge/701268856357916672/mm4s.svg?config=compile)](https://app.updateimpact.com/latest/701268856357916672/mm4s)

### API
Composable interface to the Mattermost REST API using Akka Streams.

### Bots
API for developing Bots with Akka.  Provides a Gateway API to Mattermost.

##### Configuration
Bots can be configured externally from the docker deployment by setting the following environment variables.

* `MM_HOST` - Mattermost server host name
* `MM_PORT` - Mattermost server port number
* `BOT_USER` - Bot Username - The username for the Bot, which will be used to create a Bot if not already existing
* `BOT_PASS` - Bot Password - The password for the Bot account.  Be warned that security is pretty low speed right now.
* `BOT_TEAM` - Bot Team - The team to connect to, only one supported per Bot.
* `BOT_CHANNEL` - Channel ID - The channel the Bot lives in (for now only one) and messages to.

*Note:* At this time a single bot can be dynamically placed on a single team and channel only. #23

### Dockerbots
Minimal REST API for Docker tailored for deploying containerized bots deployed as a Docker container.

Note that rx-docker uses the following environment variables
* `DOCKER_HOST`: which should use the `http://` scheme not `tcp://`
* `DOCKER_CERT_PATH`: which may need added as a volume and env var

### Initializer
Containerized init scripts for a MM development instance.

### Development

##### Dependency Injection

Dependency injection is provided through [Akka Injects](https://github.com/jw3/akka-injects) and is available within any bot.

See the Akka Injects readme for details on use.

##### Artifacts

Add the bintray resolver to you sbt project

```resolvers += "jw3 at bintray" at "https://dl.bintray.com/jw3/maven"```

The following artifacts can be specified

```"com.github.jw3" %% "mm4s-api" % "0.2.1"```

```"com.github.jw3" %% "mm4s-bots" % "0.2.1"```

```"com.github.jw3" %% "mm4s-dockerbots" % "0.2.1"```

### Bugs and Feedback

For bugs, questions and discussions please use the [Github Issues](https://github.com/jw3/mm4s/issues).

### License

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

<https://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
