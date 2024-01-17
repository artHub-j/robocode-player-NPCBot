package edu.upc.epsevg.prop.robocode;


import robocode.HitRobotEvent;
import robocode.ScannedRobotEvent;
import java.awt.*;
import java.io.IOException;
import robocode.HitByBulletEvent;
import robocode.RobotDeathEvent;
import robocode.TeamRobot;


/**
 * Bot que prioritza la eliminació del lider enemic. Si el nostre líder escaneja al líder rival, el nostre equip només es centra en disparar-lo a ell.
 * Si no s’ha pogut escanejar al lider rival, disparem amb precisió als enemics. 
 * 
 * La part defensiva de l'estratègia és fer strafing (alternar entre avançar i endarrerir 100 pixels), 
 * per poder esquivar el major nombre de bales possibles, sobretot quan l’enemic intenta predir la nostra posició futura (predictive fire). 
 * De vegades ens movem per les parets per acabar amb enemics que es basin en el ramming del PredatorTeam.
 *
 * Si detectemm al lider enemic, ens hi apropem per poder detectarlo constantment.
 * 
 * @author Arturo Aragón Hidalgo
 * @author Ferran Escala Jané
 */
public class NPCbotLider extends TeamRobot{
    double moveAmount; // Quantitat de pixels a moure.
    private final BotEnemic enemic = new BotEnemic(); //Dades de un enemic qualsevol
    private final BotEnemic liderEnemic = new BotEnemic(); //Dades del lider
    String nomLider = "";
    private enum Estat {EscaneigEnemics, AtacarLider}; //Estats del robot lider
    Estat estat = Estat.EscaneigEnemics;

    
    // Inicialitzar la quantitat de moviment que volem que fagin els nostres robots.
    // De la manera que ho tenim aqui, aconseguim que girin en la mida en un quadrant del mapa.
    @Override
    public void run() {     
        // Pintem el robot
        colorins();
        moveAmount = getBattleFieldWidth()-800;
        // Girar cap a un mur
        setTurnLeft(getHeading() % 90);
        
        while (true) {
            setTurnRadarRight(10000);//Sempre girem el radar
            switch(estat) {
                case EscaneigEnemics://En aquest estat el robot anirà donant 
                                     //voltes pel mapa escanejant enemics.
                    out.println("Estat1 EscaneigEnemics...");
                    // Moure cap a un mur o fins la distancia
                    ahead(moveAmount);
                    // Girar cap el seguent mur
                    turnRight(90);
                    break;
                case AtacarLider:// En aquest estat s'ha detectat el lider enemic
                                 // i ens movem de forma que l'escanejam sempre.
                    out.println("Estat2 AtacarLider...");
                    if (liderEnemic.getDistancia() > 800) {
                        out.println("AVANÇANT...");
                        setTurnRight(liderEnemic.getBearing()); 
                        setAhead(100);
                    }
                    else if (liderEnemic.getDistancia() < 100) {
                        out.println("MASSA APROP, RETROCEDEIXO");
                        setTurnRight(liderEnemic.getBearing()); 
                        setBack(100);
                    } else{
                        out.println("ESQUIVO");
                        ahead(100);
                        back(100);
                    }
                    break;
            }
            execute();
        }
    }
        
    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        // No enviem els companys, no volem que els nostres droids es disparin entre ells!!
        if (isTeammate(e.getName())) {
            return;
        }
        switch(estat) {
            case EscaneigEnemics://Enviem dades d'enemics escanejats
                out.println("Estat1: Enemic detectat!");
                if (e.getEnergy()>=180){//En aquest if comprovem
                    liderEnemic.actualitzar(e, this);
                    nomLider = e.getName();
                    stop();
                    resume();   
                    estat = Estat.AtacarLider;
                    return;
                }
                enemic.actualitzar(e, this);
                enviarEnemic(enemic);
                break;
            case AtacarLider: //Estem escanejant el lider, enviem les seves dades
                if (!e.getName().equals(nomLider)) {
                    return;
                }
                out.println("Estat2: Lider enemic detectat!");
                liderEnemic.actualitzar(e, this);
                enviarEnemic(liderEnemic);
                scan();
                break;
        }
    }
        

    /**
     * onHitRobot:  Ens distanciem 100 pixels cada vegada que xoquem amb algun robot.
     * @param e: event que conte les dades de l'enemic quan xoquem amb ell.
     */
    @Override
    public void onHitRobot(HitRobotEvent e) {
        // Si està davant nostre, torna una mica enrere.
        if (e.getBearing() > -90 && e.getBearing() < 90) {
            back(100);
        } // Si està davant nostre, torna una mica enrere.
        else {
            ahead(100);
        }
    }

    @Override
    public void onHitByBullet(HitByBulletEvent e) {
        setTurnLeft(90 - e.getBearing());
        setAhead(50);
    }

    @Override
    public void onRobotDeath(RobotDeathEvent e) {
        if (e.getName().equals(liderEnemic.getNom())) {
            //mapa.clear();
            liderEnemic.reset();
            estat = Estat.EscaneigEnemics;
            //objectiu = triaObjectiu();
            System.out.println("###################################");
            System.out.println("########LIDER DESTRUIT!!##########");
            System.out.println("###################################");
        }
        else if (e.getName().equals(enemic.getNom()) && liderEnemic.getNom().equals("")) {
            estat = Estat.EscaneigEnemics;
            enemic.reset();
            System.out.println("###################################");
            System.out.println("########ENEMIC DESTRUIT!!##########");
            System.out.println("###################################");
        }
    }

    /**
     * Enviem l'enemic escanejat a la resta de companys.
     * @param enemic: -
     */
    public void enviarEnemic(BotEnemic enemic) {
        try {
            out.println("Enviant missatge d'atacar a: " + enemic.getNom());
            // Enviem la posicio del l'enemic a la resta de companys.
            broadcastMessage(enemic);
        } catch (IOException ex) {
            out.println("Unable to send order: ");
            ex.printStackTrace(out);
        }
    }
    
    public void colorins() {
        setBodyColor(Color.black);
        setGunColor(Color.black);
        setRadarColor(Color.white);
        setBulletColor(Color.cyan);
        setScanColor(Color.cyan);
    }
    
}
