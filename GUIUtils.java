package Inventario.gui.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class GUIUtils {
    public static final Color PRIMARY = new Color(78, 115, 223);
    public static final Color SECONDARY = new Color(133, 135, 150);
    public static final Color SUCCESS = new Color(28, 200, 138);
    public static final Color DANGER = new Color(231, 74, 59);
    public static final Color WARNING = new Color(246, 194, 62);
    public static final Color LIGHT = new Color(248, 249, 252);
    public static final Color DARK = new Color(90, 92, 105);

    public static void limpiarCampos(JTextField... campos) {
        for(JTextField campo : campos) {
            campo.setText("");
        }

    }

    public static boolean validarCampos(JTextField... campos) {
        for(JTextField campo : campos) {
            if (campo.getText().trim().isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public static void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog((Component)null, mensaje, "Error", 0);
    }

    public static void mostrarInfo(String mensaje) {
        JOptionPane.showMessageDialog((Component)null, mensaje, "Información", 1);
    }

    public static boolean confirmarEliminacion(String mensaje) {
        int respuesta = JOptionPane.showConfirmDialog((Component)null, mensaje, "Confirmar Eliminación", 0, 2);
        return respuesta == 0;
    }

    public static JButton crearBotonEstilizado(String texto, Color colorFondo) {
        JButton boton = new JButton(texto);
        boton.setBackground(colorFondo);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setFont(new Font("Segoe UI", 1, 10));
        return boton;
    }
}
