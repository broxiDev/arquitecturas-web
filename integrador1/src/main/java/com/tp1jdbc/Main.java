package com.tp1jdbc;

import com.tp1jdbc.dao.ClienteDAO;
import com.tp1jdbc.dao.ProductoDAO;
import com.tp1jdbc.entities.Cliente;
import com.tp1jdbc.entities.Producto;
import com.tp1jdbc.utils.DataLoader;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {

        // IMPORTANTE: comentar una vez cargados los datos en la BD
        DataLoader.inicializarMetadata();

        //Punto 3: producto que más recaudó
        ProductoDAO productoDao = new ProductoDAO();

        Producto productoQueMasRecaudo = productoDao.obtenerProductoQueMasRecaudo();
        System.out.println("Producto que mas recaudo: " + productoQueMasRecaudo);

        //Punto 4: Cliente con mejor Facturacion
        ClienteDAO clienteDAO = new ClienteDAO();

        Cliente clienteMejorFacturacion = clienteDAO.obtenerClienteConMayorFacturacion();
        System.out.println("Cliente con mejor facturacion: " + clienteMejorFacturacion);
    }
}
