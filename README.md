# <img src="https://github.com/artHub-j/robocode-player-NPCBot/assets/92806890/3f601599-0cf6-427a-be1a-8f06585c85e3" width="40" /> robocode-player-NPCBot
Robocode player + team strategy. Quarter finals in our class Robocode Competition. Mark: 7.9

## Bot Strategy

For each enemy robot we update the distance by the largest scan by each robot on our team using messages that we handle in onMessageRecieved().
Once all possible robots have been scanned, we go through the HashMap and look for the smallest possible distance for each key. In this way we obtain the robot with the shortest distance towards our team.

### 1. Scan
![image](https://github.com/artHub-j/robocode-player-NPCBot/assets/92806890/04c64f35-c87b-4376-b9b9-6af692480cc3)
### 2. Compare
![image](https://github.com/artHub-j/robocode-player-NPCBot/assets/92806890/a8f802e8-eb95-444c-b43c-1a457e73f7b5)
### 3. Choose Objective
![image](https://github.com/artHub-j/robocode-player-NPCBot/assets/92806890/11631cd0-11c2-4bf9-9366-0d5b25a8c211)

## Team Strategy

## Results against classmates

![robocodeTorneigClass](https://github.com/artHub-j/robocode-player-NPCBot/assets/92806890/1ae16acd-4b79-447a-8213-3f47116211e7)
