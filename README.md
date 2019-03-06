# snakebattle
codenjoy snakebattle bot

# more info
- http://codenjoy.com/
- https://github.com/codenjoyme/codenjoy

# build
```
mvn clean compile assembly:single
```

# start
```
java -jar target/snakebattle-client-jar-with-dependencies.jar
```
OR
```
java -jar target/snakebattle-client-jar-with-dependencies.jar <SOLVER> <HASH 1> <CODE 1> <STAT_FILENAME 1> ... <HASH n> <CODE n> <STAT_FILENAME n>
```
where 
```
SOLVER: "BFS" | "GA" | "RL"
```
Examples:
```
java -jar snakebattle-client-jar-with-dependencies.jar "BFS" "keme1dgf50kkvavrwzln" "285147973966974500" "bot1.json"
java -jar snakebattle-client-jar-with-dependencies.jar "GA" "6ejguzn33aqhhawzdyao" "3485839216718225428" "bot3.json" "nq0g7eyaofe0a8xrplph" "3242248498438480567" "bot4.json"
```

# internal details
- game state - [State.java](src/main/java/com/github/illya13/snakebattle/State.java)
- solver interface - [Solver.java](src/main/java/com/github/illya13/snakebattle/Solver.java) 
- simplest closest item solver (BFS-based with manual constraints validation) - [BFSSolver.java](src/main/java/com/github/illya13/snakebattle/solver/BFSSolver.java)
- set of features for ML / GA and other optimisation [Features.java](src/main/java/com/github/illya13/snakebattle/solver/Features.java). Feature scaling [min-max normalization](https://en.m.wikipedia.org/wiki/Feature_scaling#Rescaling_(min-max_normalization)) applied.  
- [jenetics](http://jenetics.io/) -based Genetic Algorithm(GA) solver - [GASolver.java](src/main/java/com/github/illya13/snakebattle/GASolver.java)

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