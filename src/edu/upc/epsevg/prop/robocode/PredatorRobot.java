package edu.upc.epsevg.prop.robocode;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import robocode.MessageEvent;
import robocode.ScannedRobotEvent;
import robocode.TeamRobot;

import java.util.HashMap;
import java.util.Map;
import robocode.RobotDeathEvent;
import robocode.StatusEvent;

/**
 * 
 * Robot d'equip que busca l'enemic mes proper a l'equip, per despres
 * rodejar-lo i fer-li ramming.
 * @author Arturo Aragón Hidalgo
 * @author Ferran Escala Jané
 */
public class PredatorRobot extends TeamRobot implements Comparable<PredatorRobot> {  
    
    // Variables Globals Generals
    private final HashMap<String, DadesEnemic> mapaEnemics = new HashMap<>();
    private final DadesEnemic DadesEnemicBuit = new DadesEnemic("",0.0,0.0,0.0);
    private DadesEnemic objectiu = new DadesEnemic("",0.0,0.0,0.0);
    private double torretaGirant;
    
    
    private int moveDirection = 1;
    private double direccioRobot;
    private double DISTANCIA_ORBITA = 250;
    
    // Variables Gestio Estat.
    private enum Estat {EscaneigEnemics, AtacarObjectiu, rodejarObjectiu, xocarObjectiu};
    private Estat estat = Estat.EscaneigEnemics;
    
    // Variables Timer
    private Boolean gettingTime = false;
    private long startTime = 0;
    private Boolean objDinsRadar = false;
    
       
    @Override
    public void run() 
    {
        setAdjustGunForRobotTurn(true);
        DISTANCIA_ORBITA = (getBattleFieldHeight() + getBattleFieldWidth()) / 18 + 40;
        torretaGirant = 10;
        if (gettingTime == false) {
            startTime = getTime();
            gettingTime = true;
        }	
        while (true) {
            evitaParets((double)50.0);
            switch(estat) {
                case EscaneigEnemics:
                    System.out.println("--------- ESTAT 0 - Escaneig Enemics ---------");
                    setTurnGunRight(360);
                    if (getTime() > startTime + 25) {
                        if (objectiu.getNomEnemic().equals("") && !mapaEnemics.isEmpty()) {
                            objectiu = triaObjectiu();
                        }
                    }
                    break;
                case AtacarObjectiu:
                    System.out.println("--------- ESTAT 1 - Atacar Objectiu ---------");
                    
                    // Si el robot al que hem d'atacar es troba fora del rang d'escaneig d'algun robot,
                    // gracies a les coordenades que guardem al DadesEnemic, podem dirigir-nos cap a ell
                    // en qualsevol situacio.
                    if (!objDinsRadar) 
                    {    
                        // Definim les coordenades x i y de l'objectiu.
                        double objectiuX = objectiu.getX(); 
                        double objectiuY = objectiu.getY(); 
                        
                        // Calculem la distancia cap a l'objectiu.
                        double deltaX = objectiuX - getX();
                        double deltaY = objectiuY - getY();
                        double distancia = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
                        
                        // Calcular l'angle cap a l'objectiu.
                        double anguloHaciaObjetivo = Math.toDegrees(Math.atan2(deltaX, deltaY));
                     
                        // Girem cap a l'objectiu.
                        out.println("   > Angle NO AJUSTAT: " + anguloHaciaObjetivo);
                        anguloHaciaObjetivo = direccioRobot - anguloHaciaObjetivo;
                        
                        // Ajustem l'angle.
                        turnLeft(anguloHaciaObjetivo); 
                        out.println("   > Angle AJUSTAT: " + anguloHaciaObjetivo);
                        out.println("DireccioRobot: " + direccioRobot);
                        
                        // Avancem cap a l'objectiu.
                        setAhead(distancia);
                        out.println("   > Coord Objectiu: (" + objectiuX + ", " + objectiuY + ")");
                        objDinsRadar = true;
                    }
                    setTurnGunRight(360); // Seguim girant el radar.
                    break;
                case rodejarObjectiu:
                    System.out.println("--------- ESTAT 2 - Rodejar Objectiu ---------");
                    setTurnGunRight(360); // Seguim girant el radar.
                    break;
                case xocarObjectiu:
                    setTurnGunRight(360); // Seguim girant el radar.
                    break;
                default:
                    throw new AssertionError(estat.name());
            }
            execute();
        }
    }
    
    
    /**
     *  Recorre el mapa i busca la distancia mes petita. La clau del valor assignat, es l'objectiu a atacar.
     *  @return objectiu amb la distancia mes petita envers el nostre equip.
     */
    public DadesEnemic triaObjectiu()
    {
        DadesEnemic obj = DadesEnemicBuit;
        Double distanciaMin = Double.MAX_VALUE;
        for (Map.Entry<String, DadesEnemic> entry : mapaEnemics.entrySet()) {
            if (entry.getValue().getDistancia() < distanciaMin) {
                distanciaMin = entry.getValue().getDistancia();
                obj = entry.getValue();
            }
        }
        
        objDinsRadar = false;
        estat = Estat.AtacarObjectiu;
        
        imprimir_mapa("FINAL");
        out.println(" > [Objectiu FINAL: " + obj.getNomEnemic() + " amb distancia " + distanciaMin + "]");
                
        return obj;
    }

    
    @Override
    public void onStatus(StatusEvent e) {
        if (getTime() % 100 == 0) moveDirection = 1;
        else if ((getTime()+50) % 100 == 0) moveDirection = -1;
        direccioRobot = e.getStatus().getHeading();
    }
    
    
    /**
     * Al trobar-nos a prop de la distSegura, mentres orbitem a l’objectiu, 
     * alternem la direcció del moviment de tots els robots de l’equip. Enviem un missatge 
     * amb la nova direccio a la resta de teammates i d’aquesta manera evitem xocar-nos amb la paret.
     * @param distSegura
     */
    public void evitaParets(double distSegura)
    {
        Double hMapa = getBattleFieldHeight();
        Double wMapa = getBattleFieldWidth();
        Double x = getX();
        Double y = getY();
        
        if (x < distSegura || x > wMapa - distSegura || y < distSegura || y > hMapa - distSegura) {
            out.println("EVITA PARET");
            moveDirection = moveDirection * -1;
            
            try {
                broadcastMessage(moveDirection);
            } catch (IOException ex) {
                Logger.getLogger(PredatorRobot.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
    @Override
    public void onMessageReceived(MessageEvent event) 
    {
        if (event.getMessage() instanceof DadesEnemic) {
            Boolean actualitzat = false;

            DadesEnemic nd = (DadesEnemic) event.getMessage();
            String enemyName = nd.getNomEnemic();
            double novaDistancia = nd.getDistancia();

            out.println(" > S'ha rebut un missatge: ");
            out.println("       - Enviat per: " + event.getSender() + ": ");
            out.println("       - Missatge: " + enemyName +  ", " + novaDistancia);

            if (!isTeammate(enemyName)) { // Nomes tractem robots enemics.
                // Comprovem si el robot ja es troba al mapaEnemics.
                if (mapaEnemics.containsKey(enemyName)) {
                    // Guardem la distancia previa.
                    double distanciaPrevia = mapaEnemics.get(enemyName).getDistancia();

                    // Actualitza el valor distancia amb la distancia mes gran.
                    if (novaDistancia > distanciaPrevia) {
                        mapaEnemics.put(enemyName, nd);
                        actualitzat = true;
                    }
                } else { // Si l'enemic no es troba al mapaEnemics, l'afegim.
                    mapaEnemics.put(enemyName, nd);
                    actualitzat = true;
                }
            }
            if (actualitzat) imprimir_mapa("ACTUALITZAT PER " + event.getSender());
            else imprimir_mapa("NO S'ACTUALITZA");
        }
        else if (event.getMessage() instanceof Integer) {
            moveDirection = (Integer) event.getMessage();
        }
    }
    

    /**
     * Normalitzem l'angle. Si es major a 180 -> angle = angle - 360
     * Si es menor a 180 -> angle = angle + 360
     * @param angle
     * @return engle normalitzat.
     */
    public double normalitzarBearing(double angle) {
        while (angle >  180) {
            angle -= 360;
        }
        while (angle < -180) {
            angle += 360;
        }
        return angle;
    }
    
    
    /**
     * Imprimeix el mapa d'enemics indicant el seu estat marcat per 
     * l'String s.
     * @param s
     */
    public void imprimir_mapa(String s) {
        out.println(" > Mapa d'enemics " + s + ": ");
        for (Map.Entry<String, DadesEnemic> entry : mapaEnemics.entrySet()) {
            String enemy = entry.getKey();
            double distance = entry.getValue().getDistancia();
            out.println("    Enemic: " + enemy + " - Distancia: " + distance);
        }
        out.println();
    }
    
    
    /**
     * El robot actual modifica el mapa d’enemics, ja sigui omplint-lo amb un nou robot no escanejat prèviament 
     * o modificant-lo amb un enemic amb distància més gran.Posteriorment, enviem com a missatge Serialitzat 
     * de tipus DadesEnemic, les dades del robot escanejat cap a la resta de teammates. 
     * @param e: conte els events de onScannedRobot()
     */
    public void escanejarEnemics(ScannedRobotEvent e) {
        Boolean actualitzat = false;
        out.println("   > Escanejant Enemics...");
        
        // Actualitzem el mapa amb les dades del rival que ha escanejat aquest robot.
        if (!isTeammate(e.getName())) // Nomes tractem robots de l'equip rival.
        {
            double bearingEnemic = e.getBearing();
            double distEnemic = e.getDistance();
            double absolutBearing = getHeadingRadians() + Math.toRadians(bearingEnemic);
            double enemicX = getX() + distEnemic * Math.sin(absolutBearing);
            double enemicY = getY() + distEnemic * Math.cos(absolutBearing);
            DadesEnemic botScanejat = new DadesEnemic(e.getName(), e.getDistance(), enemicX, enemicY);

            out.println("   > Enemic Detectat: " + e.getName() + ", " + e.getDistance());
            // Comprovem si el robot ja es troba al mapa d'enemics.
            if (mapaEnemics.containsKey(e.getName())) {
                // Guardem la distancia previa.
                double distPrevia = mapaEnemics.get(e.getName()).getDistancia();
                // Actualitza el valor distancia amb la distancia mes gran.
                if (e.getDistance() > distPrevia) {
                    mapaEnemics.put(e.getName(), botScanejat);
                    actualitzat = true;
                }
            } else { // Si l'enemic no es troba al mapa d'enemics, l'agefim.
                mapaEnemics.put(e.getName(), botScanejat);
                actualitzat = true;
            }
            
            if (actualitzat) imprimir_mapa("ACTUALITZAT PER MI");
            else imprimir_mapa("NO L'ACTUALITZO");
            
            // Enviem les dades de l'enemic escanejat a la resta de robots del nostre equip
            // Per, despres, poder comparar les distancies i actualitzar el mapa amb la distancia 
            // mes gran trobada per cada possible objectiu.
            try {
                out.println("Enviant missatge...");
                broadcastMessage(botScanejat);
            } catch (IOException ex) {
                out.println("No s'hapogut enviar el missatge.");
                Logger.getLogger(PredatorRobot.class.getName()).log(Level.SEVERE, null, ex);
            }
            gettingTime = false;
        }
    }
    
    
    /**
     * Al darrer escaneig, intentem buscar a l’objectiu final de l’equip. 
     * Si el trobem ens dirigim cap a ell a una distancia máxima de DISTANCIA_ORBITA 
     * (radi de l'òrbita per girar sobre l’objectiu) mentre el disparem. 
     * Si l’enemic arriba a un nivell de vida de 25u canviem a l’estat de ramming.
     * @param e: conte els events de onScannedRobot()
     */
    public void atacarObjectiu(ScannedRobotEvent e) {
        
        // Si no trobem l'objectiu sortim.
        out.println("   > Cercant l'objectiu per aproximar...");
        if (!e.getName().equals(objectiu.getNomEnemic())) {
            return;
        }

        // Si el nivell de vida es igual o menor a 25, canviem d'estat a ramming.
        if (e.getEnergy() <= 25){
            estat = Estat.xocarObjectiu;
            return;
        }
        
        // Hem detectat l'objectiu
        out.println(" - atacarObjectiu() Robot " + objectiu.getNomEnemic() + " detectat");
        objDinsRadar = true; // l'objectiu es troba dins el rang del nostre radar.

        // Ens apropem a l'objectiu disparant-lo a una distancia maxima de DISTANCIA_ORBITA
        if (e.getDistance() > DISTANCIA_ORBITA) { 
            out.println("       Avancem equip...");
            torretaGirant = normalitzarBearing(e.getBearing() + (getHeading() - getRadarHeading()));
            setTurnGunRight(torretaGirant);
            setTurnRight(e.getBearing()); 
            setAhead(e.getDistance() - 140);
            smartFire(e.getDistance());
            return;
        }
        
        // Canviem a l'estat d'orbitar l'objectiu.
        estat = Estat.rodejarObjectiu;
        scan();
    }
     
    
    /**
     * Una vegada hem detectat l’objectiu i estem a la distancia d'òrbita indicada, 
     * procedim a orbitar l’enemic de la següent forma.
     * @param e: conte els events de onScannedRobot()
     */
    public void rodejarObjectiu(ScannedRobotEvent e) {
        out.println("   > Cercant l'objectiu a rodejar...");        
       
        // Si no trobem l'objectiu sortim.
        if (!e.getName().equals(objectiu.getNomEnemic())) {
            return;
        }

        // Si el nivell de vida es igual o menor a 25, canviem d'estat a ramming.
        if (e.getEnergy() <= 25){
            estat = Estat.xocarObjectiu;
            return;
        }

        // Hem detectat l'objectiu
        out.println(" - rodejarObjectiu() Robot " + objectiu.getNomEnemic() + " detectat");
        
        //Si ens torbem a la distancia d'orbita indicada, procedim a orbitar. 
        if (e.getDistance() < DISTANCIA_ORBITA + 20) {
            out.println(" > Rodejant Objectiu...");
            // L'objectiu es aprop. Calculem l'angle de gir
            torretaGirant = normalitzarBearing(e.getBearing() + (getHeading() - getRadarHeading())); 
            setTurnGunRight(torretaGirant);
            smartFire(e.getDistance());
            
            // Segons la posicio actual del robot, girem a l'esquerra 
            // I ens col.loquem perpendicularment envers l'enemic.
            if (moveDirection >= 1) { 
                setTurnLeft(normalitzarBearing(e.getBearing() + 90));
                setAhead(100);
            }
            // Segons la posicio actual del robot, girem a la dreta 
            // I ens col.loquem perpendicularment envers l'enemic.
            else if (moveDirection <= -1) {
                setTurnRight(normalitzarBearing(e.getBearing() + 270));
                setBack(100);
            }
            return;
        }
        estat = Estat.AtacarObjectiu;
        scan();
    }
    
    
    /**
     * Una vegada que l’equip s’apropa a la distancia d'òrbita indicada, 
     * procedim a posicionar els tancs envers l’enemic per avançar i poder fer-li el ramming.
     * @param e: conte els events de onScannedRobot()
     */
    public void xocarObjectiu(ScannedRobotEvent e) {     
        out.println("   > Cercant l'objectiu a xocar...");  
        
        // Si no trobem l'objectiu sortim.
        if (!e.getName().equals(objectiu.getNomEnemic())) {
            return;
        }
        
        // Hem detectat l'objectiu
        out.println(" - xocantObjectiu() Robot " + objectiu.getNomEnemic() + " detectat");
        out.println(" > Ramming Mode ACTIVAT...");
       
        // Calculem l'angle de gir.
        torretaGirant = normalitzarBearing(e.getBearing() + (getHeading() - getRadarHeading()));

        // Procedim a girar i orbitar.
        setTurnGunRight(torretaGirant);
        setTurnRight(e.getBearing());
        if(e.getBearing() > 60 && e.getBearing() <300)
            setTurnRight(e.getBearing());
        else { // Li fem ramming a l'objectiu.
            setAhead(100);
        }
        scan();   
    }
    
    
    /**
     * Segons la distancia de l'enemic i la nostra energia, 
     * modifiquem la potencia del dispar.
     * @param robotDistance: conte la distancia del robot al que disparem.
     */
    public void smartFire(double robotDistance) {
        if (robotDistance > 200 || getEnergy() < 15) {
            fire(1);
        } else if (robotDistance > 50) {
            fire(2);
        } else {
            fire(3);
        }
    }
    
    
    @Override
    public void onScannedRobot(ScannedRobotEvent e) 
    {
        switch (estat) {
            case EscaneigEnemics:
                escanejarEnemics(e);
                break;
            case AtacarObjectiu:
                atacarObjectiu(e);
                break;
            case rodejarObjectiu:
                rodejarObjectiu(e);
                break;
            case xocarObjectiu:
                xocarObjectiu(e);
                break;
        }        
    }
    
    
    @Override
    public void onRobotDeath(RobotDeathEvent e) 
    {
        // Una vegada eliminat l'objectiu, canviem d'estat per buscar el seguent 
        // amb la distancia mes petita envers el nostre equip.
        if (e.getName().equals(objectiu.getNomEnemic())) {
            // Buidem el mapa per tornar a escanejar. Ja que les distancies actuals no 
            // son les mes recents (els enemics es mouen constantment).
            mapaEnemics.clear(); 
            objectiu = DadesEnemicBuit;
            estat = Estat.EscaneigEnemics;
            System.out.println("################################");
            System.out.println("####### ENEMIC DESTRUIT! #######");
            System.out.println("################################");
	}
    }

    
    @Override
    public int compareTo(PredatorRobot o) 
    {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}