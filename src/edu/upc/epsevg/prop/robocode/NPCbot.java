package edu.upc.epsevg.prop.robocode;

import robocode.HitRobotEvent;
import java.awt.*;
import robocode.Droid;
import robocode.MessageEvent;
import robocode.StatusEvent;
import robocode.TeamRobot;
import static robocode.util.Utils.normalRelativeAngleDegrees;

/**
 * Bot que prioritza la eliminació del lider enemic. Si el nostre líder escaneja al líder rival, el nostre equip només es centra en disparar-lo a ell.
 * Si no s’ha pogut escanejar al lider rival, disparem amb precisió als enemics.  
 * 
 * @author Arturo Aragón Hidalgo
 * @author Ferran Escala Jané
 */
public class NPCbot extends TeamRobot implements Droid {
    private double moveAmount; // Variable de moviment
    private BotEnemic enemic = new BotEnemic();
    private double timerFire = 0; // Aquest es un timer que utilitzem per saber
    // si s'estan actualitzant els enemics entrants i saber si disparar o no.
    private long fireTime = 0; //Aquest timer no s'ha de confondre amb l'anterior,
    // ja que aquest ens serveix per sincronitzar el gir de la torreta amb disparar.

    @Override
    public void run() {
        // Pintem el robot
        colorins();
        // Inicialitzar la quantitat de moviment que volem que fagin els nostres robots.
        // De la manera que ho tenim aqui, aconseguim que girin en la mida en un quadrant del mapa.
        moveAmount = moveAmount = getBattleFieldWidth() - 1000;
        // Girar cap a un mur
        turnLeft(getHeading() % 90);

        while (true) {
            out.println("Estat1 EscaneigEnemics...");
            //Moure cap a un mur o fins la distancia
            ahead(moveAmount);
            //Girar cap el seguent mur
            turnRight(90);
            execute();
        }
    }

    @Override
    public void onMessageReceived(MessageEvent e) {
        //El lider ens ha enviat un objectiu!
        out.println("Enemic rebut!");
        out.println("NomObjectiu: "+enemic.getNom());
        //Actualitzem la variable enemic amb les noves dades.
        enemic = (BotEnemic) e.getMessage();
        //Tenim objectiu, posem el timer a 0. 
        timerFire=0; 
    }

    @Override
    public void onStatus(StatusEvent e) {
        // Gestionem el timer de disparar i disparem.
        // Com onStatus es un metode que es crida sempre, ens 
        // sassegurem que sempre que pugui disparar, dispari.
        out.println("timerFire= "+timerFire);
        if (timerFire<10){
            dumbFire();
            timerFire++;
        }
    }


    /**
     * onHitRobot:  Ens distanciem 100 pixels cada vegada que xoquem amb algun robot.
     * @param e: event que conte les dades de l'enemic quan xoquem amb ell.
     */
    @Override
    public void onHitRobot(HitRobotEvent e) {
        // Aqui gestionem l'event de xocar amb altres robots.
        if (e.getBearing() > -90 && e.getBearing() < 90) {
            back(100);
        }
        else {
            ahead(100);
        }
    }


    public void distFire(double deltax, double deltay) {
        // Calcular distancia al enemic
        double distEnemic = Math.sqrt(deltax * deltax + deltay * deltax);
        if (distEnemic > 1300 || getEnergy() < 15) {
                fire(1);//Estem lluny, disparem fluix.
        } else if (distEnemic > 600) {
                fire(2);//Estem a mid range, disparem normal.
        } else {
                fire(3);//Estem aprop, all out!!
        }
    }

    
    public void dumbFire(){
        // Calcular x i y del vector al objectiu
        double deltax = enemic.getX() - this.getX();
        double deltay = enemic.getY() - this.getY();
        // Calcular el angle al objectiu
        double theta = Math.toDegrees(Math.atan2(deltax, deltay));
        // Si hem acabat de rotar la torreta, dispara.
        if (fireTime == getTime() && getGunTurnRemaining() == 0) {
            distFire(deltax, deltay);
        }
        // Girar el canó a l'objectiu
        setTurnGunRight(normalRelativeAngleDegrees(theta - getGunHeading()));
        fireTime = getTime() + 1;
    }
    
    public void colorins() {
        setBodyColor(Color.black);
        setGunColor(Color.black);
        setRadarColor(Color.white);
        setBulletColor(Color.cyan);
        setScanColor(Color.cyan);
    }
}