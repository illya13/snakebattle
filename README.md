# snakebattle
codenjoy snakebattle bot

# more info
- http://codenjoy.com/
- https://github.com/codenjoyme/codenjoy
- https://epam-bot-challenge.com.ua/

# build
```
mvn clean compile assembly:single
```

# start
```
java -jar target/snakebattle-client-jar-with-dependencies.jar
```

# internal details
- [main logic](https://github.com/illya13/snakebattle/blob/master/src/main/java/com/github/illya13/snakebattle/Solver.java#L50):
    - [realtime](https://github.com/illya13/snakebattle/blob/master/src/main/java/com/github/illya13/snakebattle/Solver.java#L65) step,
    - [mid term](https://github.com/illya13/snakebattle/blob/master/src/main/java/com/github/illya13/snakebattle/Solver.java#L132) step,
    - [last call](https://github.com/illya13/snakebattle/blob/master/src/main/java/com/github/illya13/snakebattle/Solver.java#L217) step
- hand made X-times full scan- based [dead zone detection](https://github.com/illya13/snakebattle/blob/master/src/main/java/com/github/illya13/snakebattle/Board.java#L111)
- BFS-based [path finding and short path](https://github.com/illya13/snakebattle/blob/master/src/main/java/com/github/illya13/snakebattle/BFS.java#L36)
- BFS-based [weights-based mid- and long- term "nice to go" direction detection](https://github.com/illya13/snakebattle/blob/master/src/main/java/com/github/illya13/snakebattle/BFS.java#L36)
- Number of [embedded features like "stones mining" + stat-based self learning](https://github.com/illya13/snakebattle/blob/master/src/main/java/com/github/illya13/snakebattle/Learning.java)
- if/then- based and BFS- based enemy [prediction logic](https://github.com/illya13/snakebattle/blob/master/src/main/java/com/github/illya13/snakebattle/SolverHelperImpl.java#L31)

# other repo's
- https://github.com/ashelkov/snake_bot_challenge
- https://github.com/patkovskyi/snakebattle
- https://github.com/Kontsedal/snake-bot
- https://github.com/ViktorKukurba/snakebattle-bot
- https://github.com/Arhnt/snake-public
- https://github.com/gram7gram/epam-snakebot
- https://github.com/Vitaliy-Yarovuy/snakebattle
- https://github.com/jeka-kiselyov/snakebot
- https://github.com/wdcoua/epam-snake-bot/

# links
- https://www.codingame.com/
- https://www.codewars.com/

# nice to have
- https://habr.com/ru/post/282522/
- https://www.baeldung.com/java-monte-carlo-tree-search
