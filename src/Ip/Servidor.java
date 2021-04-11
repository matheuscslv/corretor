package Ip;

public class Servidor {
    
    private static String ip = "localhost";
    private static final String usuario = "root";
    private static final String senha = "";
    
    public static void setIp(String ip) {
        Servidor.ip = ip;
    }
    
    public static String getIp() {
        return ip;
    }

    public static String getUsuario() {
        return usuario;
    }

    public static String getSenha() {
        return senha;
    }
    
}
