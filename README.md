Mattermost for Scala (mm4s)
==========================
[![Build Status](https://travis-ci.org/jw3/mm4s.svg?branch=master)](https://travis-ci.org/jw3/mm4s)
[![Dependencies](https://app.updateimpact.com/badge/701268856357916672/mm4s.svg?config=compile)](https://app.updateimpact.com/latest/701268856357916672/mm4s)

Mattermost API implemented using Akka HTTP and Streams

**[WIP] Rapidly changing API until the 0.1 release**

#### API Bot

The library provides an Actor implementation which can handle the majority of MM interaction.

You can integrate this API Bot into your Actor hierarchy and use it as a gateway for other Actors.
 
#### Docker Bots

In addition to providing an API for interaction with MM this library also provides a convenient means of deploying Bots as Microservices.

Bot Containers should be provided the following

* MM_HOST - Mattermost Hostname - The name or ip of the MM server.
* BOT_USER - Bot Username - A username for the Bot, which will be used to create a Bot if not already existing
* BOT_PASS - Bot Password - The password for the Bot account.  Be warned that security is pretty low speed right now.
* BOT_TEAM - Bot Team - The team to connect to, only one supported per Bot.
* BOT_CHANNEL - Channel ID - The channel the Bot lives in (for now only one) and messages to.

To build the Docker Bot image from the root project do a

`sbt dockerbot/docker:publishLocal`

and then

`docker run jwiii/mm4s-dockerbot:0.1-SNAPSHOT`

#### Dependency Injection

Dependency injection is provided through [Akka Injects](https://github.com/jw3/akka-injects) and is available within any Dockerbot.

See the Akka Injects readme for details on use.

#### HTTP Bots

*TODO*

The Bot Gateway also supports a HTTP interface that can be used to receive callbacks from MM.

Slash Commands and Outgoing Webhooks can target this interface which will route messages to the underlying actors.

#### Installation

Create a Slash Command that points to the mm4s server

#### Artifacts

*Not yet published, 0.1 will be first release*

Add the bintray resolver

```resolvers += "jw3 at bintray" at "https://dl.bintray.com/jw3/maven"```

Add the mm4s dependency

```"com.github.jw3" %% "mm4s" % "0.1"```

## Bugs and Feedback

For bugs, questions and discussions please use the [Github Issues](https://github.com/jw3/mm4s/issues).

## License

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

<https://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
