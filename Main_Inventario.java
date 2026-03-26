package Inventario;

import Inventario.gui.InventarioApp;
import javax.swing.SwingUtilities;

public class Main_Inventario {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InventarioApp());
    }

}
