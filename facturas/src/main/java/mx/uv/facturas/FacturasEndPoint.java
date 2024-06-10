package mx.uv.facturas;

import mx.uv.facturas.Modelo.Factura;
import mx.uv.facturas.ORM.IFactura;
import mx.uv.t4is.facturas.GenerarFacturaRequest;
import mx.uv.t4is.facturas.GenerarFacturaResponse;
import mx.uv.t4is.facturas.GenerarFacturaResponse.Productos;
import mx.uv.t4is.facturas.GenerarFacturaResponse.Productos.Producto;
import mx.uv.t4is.facturas.RecuperarFacturaRequest;
import mx.uv.t4is.facturas.RecuperarFacturaResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Endpoint
public class FacturasEndPoint {

    @Autowired
    private IFactura iFactura;

    private Map<String, GenerarFacturaResponse> facturas = new HashMap<>();

    @PayloadRoot(localPart = "generarFacturaRequest", namespace = "t4is.uv.mx/facturas")
    @ResponsePayload
    public GenerarFacturaResponse generarFactura(@RequestPayload GenerarFacturaRequest request) {
        GenerarFacturaResponse response = new GenerarFacturaResponse();
        Productos productosResponse = new Productos();

        String uuid = UUID.randomUUID().toString();
        double precioTotal = 0;
        List<Factura.Producto> productos = new ArrayList<>();

        for (GenerarFacturaRequest.Productos.Producto productoRequest : request.getProductos().getProducto()) {
            Producto producto = new Producto();
            producto.setNombre(productoRequest.getNombre());
            producto.setCantidad(productoRequest.getCantidad());
            producto.setPrecioUnitario(productoRequest.getPrecioUnitario());

            double totalProducto = productoRequest.getPrecioUnitario() * productoRequest.getCantidad();
            precioTotal += totalProducto;

            productosResponse.getProducto().add(producto);
            productos.add(new Factura.Producto(productoRequest.getNombre(), (int) productoRequest.getCantidad(), BigDecimal.valueOf(productoRequest.getPrecioUnitario())));
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

        facturas.put(uuid, response);

        Factura factura = new Factura();
        factura.setUuid(uuid);
        factura.setPrecioTotal(BigDecimal.valueOf(precioTotal));
        factura.setFechaGeneracion(LocalDateTime.now());
        factura.setProductos(productos);
        factura.setDatosVendedor(new Factura.DatosVendedor(request.getDatosVendedor().getNombre(), request.getDatosVendedor().getDireccion()));

        iFactura.save(factura);

        return response;
    }

    @PayloadRoot(localPart = "recuperarFacturaRequest", namespace = "t4is.uv.mx/facturas")
    @ResponsePayload
    public RecuperarFacturaResponse recuperarFactura(@RequestPayload RecuperarFacturaRequest request) {
        RecuperarFacturaResponse response = new RecuperarFacturaResponse();

        Factura factura = iFactura.findByUuid(request.getUUID());
        if (factura != null) {
            response.setUUID(factura.getUuid());
            RecuperarFacturaResponse.Productos productosResponse = new RecuperarFacturaResponse.Productos();

            for (Factura.Producto producto : factura.getProductos()) {
                RecuperarFacturaResponse.Productos.Producto nuevoProducto = new RecuperarFacturaResponse.Productos.Producto();
                nuevoProducto.setNombre(producto.getNombre());
                nuevoProducto.setCantidad(producto.getCantidad());
                nuevoProducto.setPrecioUnitario(producto.getPrecioUnitario().doubleValue());
                productosResponse.getProducto().add(nuevoProducto);
            }

            response.setProductos(productosResponse);

            RecuperarFacturaResponse.DatosVendedor datosVendedorResponse = new RecuperarFacturaResponse.DatosVendedor();
            datosVendedorResponse.setNombre(factura.getDatosVendedor().getNombre());
            datosVendedorResponse.setDireccion(factura.getDatosVendedor().getDireccion());

            response.setDatosVendedor(datosVendedorResponse);
            response.setPrecioTotal(factura.getPrecioTotal().doubleValue());
            response.setFechaGeneracion(factura.getFechaGeneracion().toString());
        } else {
            response.setUUID("Factura no encontrada");
        }

        return response;
    }
}
