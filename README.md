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
- simplest but efficient `closest item` solver (BFS-based with manual constraints validation) - [BFSSolver.java](src/main/java/com/github/illya13/snakebattle/solver/BFSSolver.java)
- set of features for ML / GA and other optimisation - [Features.java](src/main/java/com/github/illya13/snakebattle/solver/Features.java). Feature scaling [min-max normalization](https://en.m.wikipedia.org/wiki/Feature_scaling#Rescaling_(min-max_normalization)) has been applied.  
- [jenetics](http://jenetics.io/) -based Genetic Algorithm(GA) solver - [GASolver.java](src/main/java/com/github/illya13/snakebattle/solver/GASolver.java). It takes `~65min` to evaluate `30 genotypes` by `2 GA solvers` locally, game timer period: `100ms`. Each `evaluation` is an average reward over `10` game rounds.  
- advanced Board with snakes parsing, BFS and board liveness - [Board.java](src/main/java/com/github/illya13/snakebattle/board/Board.java)

# features
for each point in all possible directions:
- LIVENESS - how far is the point from the walls 
- BARRIER - there is wall at the point
- ENEMY - there is an enemy at the point and it is good / safe to go
- STONE - there is a stone at the point and it is good / safe to go
- BODY - our snake's body
- APPLE - how close we are to the nearest apple
- GOLD - how close we are to the nearest gold
- FURY - how close we are to the nearest fury pill
- FLY - how close we are to the nearest fly pill
- AVERAGE - average distance to the items
- STONE_N_FURY - how close we are to the nearest stone while we are in fury mode
- STONE_N_SIZE - how close we are to the nearest stone with our snake size
- ENEMY_N_FURY - how close we are to the nearest enemy while we are in fury mode
- ENEMY_N_SIZE - how close we are to the nearest enemy with our size and enemy snake's size
- ESCAPE_FURY - how close we are to the nearest enemy which is in fury
- ESCAPE_TRAFFIC - how close we are to the enemy bodies in average

Values distribution: 
![Feutares](features.png)

# GA solver
Intuition:
- snake has a chromosome that encodes its behaviour
- chromosome is a sequence of 16 integer genes from 0 to 10 representing weight of each feature in total weight calculation
- i.e. **weight = sum (w\[i\] * feature\[i\])**
- fitness: average reward of a snake with a given chromosome in game environment    

Parameters:
- population: 100
- offspring fraction: 0.6
- survivors selector: TournamentSelector(3)
- offspring selector: TournamentSelector(3)
- alterers: UniformCrossover(0.2, 0.2), Mutator(0.15)

Generations / rewards:

|   # |  max | avg | best chromosome                                                          |
|-----|------|-----|--------------------------------------------------------------------------|
|   1 |  132 |  27 |       [[0],[8],[4],[7],[1],[10],[0],[7],[4],[5],[2],[2],[6],[3],[8],[5]] |
|   2 |  131 |  33 |        [[0],[8],[4],[7],[1],[7],[0],[7],[7],[5],[2],[2],[6],[1],[8],[5]] |
|   3 |  118 |  39 |     [[0],[5],[0],[10],[3],[10],[9],[7],[0],[1],[10],[2],[3],[8],[7],[2]] |
|   4 |  124 |  46 |        [[0],[8],[4],[7],[1],[7],[0],[7],[7],[5],[2],[2],[6],[1],[8],[5]] |
|   5 |  132 |  44 |       [[0],[8],[4],[7],[1],[10],[0],[7],[4],[5],[0],[2],[6],[3],[8],[5]] |
|   6 |  107 |  50 |       [[1],[7],[6],[7],[0],[0],[7],[10],[2],[7],[3],[9],[3],[9],[8],[5]] |
|   7 |  139 |  55 |     [[0],[7],[10],[7],[1],[10],[9],[6],[4],[5],[10],[4],[6],[8],[1],[3]] |
|   8 |  139 |  61 |     [[0],[7],[10],[7],[1],[10],[9],[6],[4],[5],[10],[4],[6],[8],[1],[3]] |
|   9 |  139 |  63 |     [[0],[7],[10],[7],[1],[10],[9],[6],[4],[5],[10],[4],[6],[8],[1],[3]] |
|  10 |  139 |  59 |     [[0],[7],[10],[7],[1],[10],[9],[6],[4],[5],[10],[4],[6],[8],[1],[3]] |
|  11 |  151 |  52 |     [[0],[7],[10],[7],[1],[10],[9],[6],[4],[5],[10],[4],[6],[8],[1],[3]] |
|  12 |  151 |  55 |     [[0],[7],[10],[7],[1],[10],[9],[6],[4],[5],[10],[4],[6],[8],[1],[3]] |
|  13 |  121 |  53 |      [[0],[2],[5],[7],[5],[10],[0],[6],[0],[5],[10],[6],[6],[9],[7],[2]] |
|  14 |  141 |  59 |     [[0],[7],[10],[7],[1],[10],[9],[6],[4],[5],[10],[4],[6],[8],[1],[3]] |
|  15 |  109 |  55 |      [[0],[8],[10],[7],[4],[10],[0],[7],[4],[5],[2],[4],[6],[8],[8],[5]] |
|  16 |  153 |  56 |      [[0],[8],[10],[7],[4],[10],[0],[7],[4],[5],[2],[4],[6],[8],[8],[5]] |
|  17 |  119 |  56 |        [[0],[6],[4],[7],[7],[6],[4],[7],[9],[7],[2],[2],[9],[2],[8],[5]] |
|  18 |  119 |  56 |    [[0],[7],[10],[7],[5],[10],[7],[10],[4],[5],[10],[4],[5],[1],[1],[3]] |
|  19 |  110 |  57 |      [[0],[8],[10],[7],[4],[10],[0],[7],[4],[5],[2],[4],[6],[8],[8],[5]] |
|  20 |  126 |  55 |     [[0],[7],[10],[7],[1],[10],[2],[4],[4],[1],[5],[4],[8],[10],[3],[1]] |
|  21 |  126 |  59 |     [[0],[7],[10],[7],[7],[10],[0],[4],[4],[5],[2],[2],[10],[1],[3],[1]] |
|  22 |  163 |  62 |      [[0],[8],[8],[7],[5],[10],[2],[8],[4],[1],[5],[4],[8],[10],[4],[3]] |
|  23 | 1110 |  73 |      [[0],[8],[10],[7],[4],[10],[0],[8],[4],[5],[2],[4],[6],[8],[8],[5]] |


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

# using Reinforcement Learning
Materials:
- [papers](papers)
- [https://skymind.ai/wiki/deep-reinforcement-learning](https://skymind.ai/wiki/deep-reinforcement-learning)
- [https://en.wikipedia.org/wiki/Reinforcement_learning](https://en.wikipedia.org/wiki/Reinforcement_learning)
- [https://drive.google.com/drive/folders/1V9jAShWpccLvByv5S1DuOzo6GVvzd4LV](https://drive.google.com/drive/folders/1V9jAShWpccLvByv5S1DuOzo6GVvzd4LV)
- [https://rubenfiszel.github.io/posts/rl4j/2016-08-24-Reinforcement-Learning-and-DQN.html](https://rubenfiszel.github.io/posts/rl4j/2016-08-24-Reinforcement-Learning-and-DQN.html)
- [https://www.kdnuggets.com/2018/03/5-things-reinforcement-learning.html](https://www.kdnuggets.com/2018/03/5-things-reinforcement-learning.html)
- [http://gameaibook.org/book.pdf](http://gameaibook.org/book.pdf)
- [https://yuandong-tian.com/ACMMM17_tutorial.pdf](https://yuandong-tian.com/ACMMM17_tutorial.pdf)