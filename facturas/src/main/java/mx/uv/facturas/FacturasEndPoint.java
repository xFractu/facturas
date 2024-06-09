package mx.uv.facturas;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import mx.uv.t4is.facturas.GenerarFacturaRequest;
import mx.uv.t4is.facturas.GenerarFacturaResponse;
import mx.uv.t4is.facturas.GenerarFacturaResponse.Productos;
import mx.uv.t4is.facturas.GenerarFacturaResponse.Productos.Producto;
import mx.uv.t4is.facturas.RecuperarFacturaRequest;
import mx.uv.t4is.facturas.RecuperarFacturaResponse;

@Endpoint
public class FacturasEndPoint {

    private Map<String, GenerarFacturaResponse> facturas = new HashMap<>();

    @PayloadRoot(localPart = "generarFacturaRequest", namespace = "t4is.uv.mx/facturas")
    @ResponsePayload
    public GenerarFacturaResponse generarFactura(@RequestPayload GenerarFacturaRequest request) {
        GenerarFacturaResponse response = new GenerarFacturaResponse();
        Productos productosResponse = new Productos();
        String uuid = UUID.randomUUID().toString(); // Generar un UUID único

        BigDecimal precioTotal = BigDecimal.ZERO;

        for (GenerarFacturaRequest.Productos.Producto productoRequest : request.getProductos().getProducto()) {
            Producto producto = new Producto();
            producto.setNombre(productoRequest.getNombre());
            producto.setCantidad(productoRequest.getCantidad());
            producto.setPrecioUnitario(productoRequest.getPrecioUnitario());

            BigDecimal totalProducto = productoRequest.getPrecioUnitario().multiply(BigDecimal.valueOf(productoRequest.getCantidad().intValue()));
            precioTotal = precioTotal.add(totalProducto);

            productosResponse.getProducto().add(producto);
        }

        response.setUUID(uuid);
        response.setProductos(productosResponse);
        response.setPrecioTotal(precioTotal);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String fechaGeneracion = LocalDateTime.now().format(formatter);
        response.setFechaGeneracion(fechaGeneracion);

        GenerarFacturaResponse.DatosVendedor datosVendedor = new GenerarFacturaResponse.DatosVendedor();
        datosVendedor.setNombre(request.getDatosVendedor().getNombre());
        datosVendedor.setDireccion(request.getDatosVendedor().getDireccion());

        response.setDatosVendedor(datosVendedor);

        facturas.put(uuid, response); // Guardar factura en el mapa

        return response;
    }

    @PayloadRoot(localPart = "recuperarFacturaRequest", namespace = "t4is.uv.mx/facturas")
    @ResponsePayload
    public RecuperarFacturaResponse recuperarFactura(@RequestPayload RecuperarFacturaRequest request) {
        RecuperarFacturaResponse response = new RecuperarFacturaResponse();

        GenerarFacturaResponse factura = facturas.get(request.getUUID());
        if (factura != null) {
            response.setUUID(factura.getUUID());

            // Crear un nuevo objeto RecuperarFacturaResponse.Productos
            RecuperarFacturaResponse.Productos productosResponse = new RecuperarFacturaResponse.Productos();

            // Copiar los productos del objeto GenerarFacturaResponse.Productos al nuevo objeto
            for (GenerarFacturaResponse.Productos.Producto producto : factura.getProductos().getProducto()) {
                RecuperarFacturaResponse.Productos.Producto nuevoProducto = new RecuperarFacturaResponse.Productos.Producto();
                nuevoProducto.setNombre(producto.getNombre());
                nuevoProducto.setCantidad(producto.getCantidad());
                nuevoProducto.setPrecioUnitario(producto.getPrecioUnitario());
                productosResponse.getProducto().add(nuevoProducto);
            }

            // Establecer el nuevo objeto Productos en la respuesta
            response.setProductos(productosResponse);

            // Crear un nuevo objeto RecuperarFacturaResponse.DatosVendedor
            RecuperarFacturaResponse.DatosVendedor datosVendedorResponse = new RecuperarFacturaResponse.DatosVendedor();
            datosVendedorResponse.setNombre(factura.getDatosVendedor().getNombre());
            datosVendedorResponse.setDireccion(factura.getDatosVendedor().getDireccion());

            // Establecer el nuevo objeto DatosVendedor en la respuesta
            response.setDatosVendedor(datosVendedorResponse);

            // Establecer el precio total y la fecha de generación en la respuesta
            response.setPrecioTotal(factura.getPrecioTotal());
            response.setFechaGeneracion(factura.getFechaGeneracion());
        } else {
            // Manejar caso donde la factura no se encuentra
            response.setUUID("Factura no encontrada");
        }

        return response;
    }

}
