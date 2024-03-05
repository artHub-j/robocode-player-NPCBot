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
## Results against classmates
<img src="https://github.com/artHub-j/robocode-player-NPCBot/assets/92806890/1ae16acd-4b79-447a-8213-3f47116211e7" width="500;" alt="robocodeTorneigClass"/>

## Authors

<table border="6">
  <tr>
    <td align="center">
      <a href="https://github.com/artHub-j">
        <img src="https://github.com/CulturaLink/culturalink-main/assets/92806890/416f28b8-f634-4ce7-ad76-b383863d5774" width="75px;" alt="artHub-j"/><br>
        <sub><b>@artHub-j</b></sub>
      </a><br/>
      <sub>Arturo Aragón</sub>
    </td> <!-- --------------------------------------------------------------------------------------------------------------------------------------- -->
    <td align="center">
      <a href="https://github.com/FerranEJ/">
        <img src="https://github.com/CulturaLink/culturalink-main/assets/92806890/afbe2348-87fe-47f9-809e-19de349a8b6b" width="75px;" alt="FerranEJ"/><br>
        <sub><b>@FerranEJ</b></sub>
      </a><br/>
      <sub>Ferran Escala</sub>
    </td> <!-- --------------------------------------------------------------------------------------------------------------------------------------- -->
  </tr>
</table>
