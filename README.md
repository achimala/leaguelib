# LeagueLib
#### By [Anshu Chimala](http://www.achimala.com), Tyrus Tenneson, and Gavin Saldanha.

LeagueLib is a Java library for the [League of Legends](http://www.leagueoflegends.com) [RTMPS](http://en.wikipedia.org/wiki/Real_Time_Messaging_Protocol) API, built on top of [LoLRTMPSClient](http://code.google.com/p/lolrtmpsclient) by Gabriel Van Eyck.

LeagueLib is built to power efficient, scalable, concurrent League of Legends web applications. Best served on a framework like [Play](http://www.playframework.com/).

This is the stuff we built to replace the backend that runs [LoLTeam](http://www.lolteam.net) and [LoLTalk](http://loltalk.achimala.com). Our original code did its job, but wasn't scalable or efficient enough to meet our rising traffic. In response, we started building a new, highly scalable, massively concurrent framework for these applications, to serve tens of thousands of players all over the world. And we're making it open source so that other developers like us can make awesome League of Legends apps.

### Features
* Completely asynchronous API calls to the League of Legends RTMP server.
* Synchronous API calls just as easily, for use in simple applications or on top of distributed, scalable, concurrent platforms like [Akka](http://www.akka.io)
* Easy support for multiple League of Legends accounts for increased concurrency and minimal rate-limiting side effects.
* Efficient design: request only as much information as you need, using the same API calls and structure that League of Legends uses internally.
* Clean MVC patterns: Accounts can easily be marshaled to files or recorded in databases, distributed to different account queues for different servers, and a single LeagueSummoner model can contain references to real-time data across your application that updates in place.

### License
LeagueLib is licensed under the GNU GPL v3. You may use this code for commercial or non-commercial purposes. Please include attribution (preferably a link to this page) if you use this library. We'd really appreciate it!

### Caution
LeagueLib is still currently in development. We would advise against using it in production applications until it's ready.

Code pulled off the master branch should always work correctly and pass all tests. We intend to put up sample code soon, but until then you should hopefully be able to figure out how to use LeagueLib by viewing [MainTest.java](https://github.com/achimala/leaguelib/blob/master/src/com/achimala/leaguelib/tests/MainTest.java) or by reading through the comments on the LeagueConnection, LeagueAccount, and \[A-Z\]\[a-z\]\+Service.java classes.