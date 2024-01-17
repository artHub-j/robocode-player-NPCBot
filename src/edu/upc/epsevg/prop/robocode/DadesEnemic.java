package edu.upc.epsevg.prop.robocode;

import java.io.Serializable;
import java.util.Objects;

/**
 * Classe que representa un botEnemic, hi guardem les dades dels enemics escanejats.
 * 
 * @author Arturo Aragón Hidalgo
 * @author Ferran Escala Jané
 */
public class DadesEnemic implements Comparable<DadesEnemic>, Serializable {
    private String nomEnemic;
    private double distancia;
    private double x;
    private double y;

    public DadesEnemic (String nom, double distancia, double x, double y) {
        this.nomEnemic = nom;
        this.distancia = distancia;
        this.x = x;
        this.y = y;
    }

    public String getNomEnemic() {
        return nomEnemic;
    }

    public double getDistancia() {
        return distancia;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setNomEnemic(String nom) {
        this.nomEnemic = nom;
    }

    public void setDistancia(double distancia) {
        this.distancia = distancia;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + Objects.hashCode(this.nomEnemic);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DadesEnemic other = (DadesEnemic) obj;
        return Objects.equals(this.nomEnemic, other.nomEnemic);
    }

    @Override
    public int compareTo(DadesEnemic o) {
        return (this.nomEnemic.compareTo(o.nomEnemic));
    }
}