package edu.upc.epsevg.prop.robocode;

import java.io.Serializable;
import robocode.Robot;
import robocode.ScannedRobotEvent;

/**
 * Classe que representa un botEnemic, hi guardem les dades dels enemics escanejats.
 *
 * @author Arturo Aragon Hidalgo
 * @author Ferran Escala Jané
 */
public class BotEnemic implements Serializable {
    private double bearing, distancia, energia, heading, velocitat, x, y; 
    private String nom;
    
    /**
     * Constructor de BotEnemic
    */
    public BotEnemic(){
        this.bearing = 0.0;
        this.distancia = 0.0;
        this.energia = 0.0;
        this.heading = 0.0;
        this.velocitat = 0.0;
        this.x = 0.0;
        this.y = 0.0;
        this.nom = "";
    }

    /* Gettter del Bearing
     *
    */
    public double getBearing(){
        return this.bearing;
    }

    // Getter de la distancia
    public double getDistancia(){
        return this.distancia;
    }

    // Getter de l'energia
    public double getEnergia(){
        return this.energia;
    }

    // Getter del heading
    public double getHeading(){
        return this.heading;
    }

    // Getter de la velocitat
    public double getVelocitat(){
        return this.velocitat;
    }
    
    // Getter de la coord X
    public double getX(){
        return this.x;
    }
    
    // Getter de la coord Y
    public double getY(){ 
        return this.y;
    }

    // Getter del Nom
    public String getNom(){
        return this.nom;
    }
    
    /**
     * Actualitza el BotEnemic amb les dades de l'event e i
     * obtenim les x i y a partir del Robot bot.
     * @param e event amb les dades del robot escanejat.
     * @param bot dades d'un Robot
     */
    public void actualitzar(ScannedRobotEvent e, Robot bot){
        this.bearing = e.getBearing();
        this.distancia = e.getDistance();
        this.energia = e.getEnergy();
        this.heading = e.getHeading();
        this.velocitat = e.getVelocity();
        this.nom = e.getName();
        double bearingAbsolutGra = bot.getHeading() + e.getBearing();
        if (bearingAbsolutGra < 0) bearingAbsolutGra += 360;
        x = bot.getX() + Math.sin(Math.toRadians(bearingAbsolutGra)) * e.getDistance();
        y = bot.getY() + Math.cos(Math.toRadians(bearingAbsolutGra)) * e.getDistance();
    }

    public void reset(){
        this.bearing = 0.0;
        this.distancia = 0.0;
        this.energia = 0.0;
        this.heading = 0.0;
        this.velocitat = 0.0;
        this.x = 0.0;
        this.y = 0.0;
        this.nom = "";
    }
}