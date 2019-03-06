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

# server
- build / start
```
git clone https://github.com/codenjoyme/codenjoy.git

cd codenjoy/CodingDojo/
mvn clean install -DskipTests=true

cd builder
mvn clean package -Dcontext=codenjoy-contest -Psnakebattle
mvn -DMAVEN_OPTS=-Xmx1024m -Dmaven.test.skip=true clean jetty:run-war -Psnakebattle
```
- admin [http://127.0.0.1:8080/codenjoy-contest/admin](http://127.0.0.1:8080/codenjoy-contest/admin)
- register [http://127.0.0.1:8080/codenjoy-contest/](http://127.0.0.1:8080/codenjoy-contest/) 