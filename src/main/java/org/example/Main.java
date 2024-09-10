package org.example;
import java.sql.*;
import java.util.Scanner;

public class Main {

    //private static Scanner scanner = new Scanner(System.in);
    private static Connection connection = null;

    public static void main(String[] args) throws ClassNotFoundException {

        Scanner scanner = new Scanner(System.in);

        String url = "jdbc:mysql://localhost:3306/ComputadorasDB";

        Class.forName("com.mysql.cj.jdbc.Driver");



        try {
            connection = DriverManager.getConnection(url, "root", "root");
            Statement statement = connection.createStatement();
            while (true) {
                System.out.println("Ingrese el id de la computadora:");
                long id = Long.parseLong(scanner.nextLine());

                System.out.println("Ingrese el código de la computadora:");
                String codigo = scanner.nextLine();

                System.out.println("Ingrese la Marca de la computadora:");
                String marca = scanner.nextLine();

                System.out.println("Ingrese el modelo de la computadora:");
                String modelo = scanner.nextLine();

                String sql = "INSERT INTO Computadora (Id, Codigo, Marca, Modelo) VALUES (?, ?, ?, ?)";
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setLong(1, id);
                    pstmt.setString(2, codigo);
                    pstmt.setString(3, marca);
                    pstmt.setString(4, modelo);

                    int rowsInserted = pstmt.executeUpdate();
                    if (rowsInserted > 0) {
                        System.out.println("¡Inserción exitosa!");
                    }
                } catch (SQLException e) {
                    System.out.println("Error al insertar los datos.");
                    e.printStackTrace();
                }

                System.out.println("¿Desea ingresar otra computadora? (s/n)");
                String continuar = scanner.nextLine();
                if (!continuar.equalsIgnoreCase("s")) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public void connectDB() throws SQLException {
    }

}