package org.example;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    //private static Scanner scanner = new Scanner(System.in);
    private static Connection connection = null;

    public static void main(String[] args) throws ClassNotFoundException {

        Scanner scanner = new Scanner(System.in);

        String url = "jdbc:mysql://localhost:3306/ComputadorasDB";

        Class.forName("com.mysql.cj.jdbc.Driver");

        try {
            connection = DriverManager.getConnection(url, "root", "admin");
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            crearTablas();

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
                    connection.rollback();
                    e.printStackTrace();
                }

                List<String> componentes = new ArrayList<>();//Lista de componentes
                //Se ingresa cada componente
                while (true) {
                    System.out.println("Ingrese el nombre del componente ('S' para salir):");
                    String nombreComponente = scanner.nextLine();
                    if (nombreComponente.equalsIgnoreCase("s")) {
                        break;
                    }

                    System.out.println("Ingrese el número de serie del componente:");
                    String nroSerie = scanner.nextLine();

                    componentes.add(nombreComponente + "," + nroSerie);
                }
                //Insertamos cada componente de componentes en la DB con el id de la computadora
                String sqlComponente = "INSERT INTO Componente (Nombre, NroSerie, Id_Computadora) VALUES (?, ?, ?)";
                try (PreparedStatement pstmtComponente = connection.prepareStatement(sqlComponente)) {
                    for (String comp : componentes) {
                        String[] datosComponente = comp.split(",");
                        pstmtComponente.setString(1, datosComponente[0]); // Nombre
                        pstmtComponente.setString(2, datosComponente[1]); //Serie
                        pstmtComponente.setLong(3, id); //Id_Computadora

                        int rowsInserted = pstmtComponente.executeUpdate();
                        if (rowsInserted > 0) {
                            System.out.println("¡Componente '" + datosComponente[0] + "' insertado exitosamente!");
                        }
                    }
                } catch (SQLException e) {
                    System.out.println("Error al insertar los componentes.");
                    connection.rollback();
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

    private static void crearTablas() {
        String crearComputadora = "CREATE TABLE IF NOT EXISTS Computadora (" +
                "Id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "Codigo VARCHAR(100) NOT NULL, " +
                "Marca VARCHAR(100) NOT NULL, " +
                "Modelo VARCHAR(100) NOT NULL" +
                ")";

        String crearComponente = "CREATE TABLE IF NOT EXISTS Componente (" +
                "Id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "Nombre VARCHAR(100) NOT NULL, " +
                "NroSerie VARCHAR(150) NOT NULL, " +
                "Id_Computadora BIGINT NOT NULL, " +
                "FOREIGN KEY (Id_Computadora) REFERENCES Computadora(Id)" +
                ")";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(crearComputadora);
            statement.executeUpdate(crearComponente);
            System.out.println("Tablas creadas exitosamente.");
            connection.commit();
        } catch (SQLException e) {
            System.out.println("Error al crear tablas.");
            e.printStackTrace();
            try {
                connection.rollback(); //En caso de error revertir cambios
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        }
    }

}