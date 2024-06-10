package mx.uv.facturas.Modelo;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Factura {

    @Id
    private String uuid;
    private BigDecimal precioTotal;
    private LocalDateTime fechaGeneracion;

    
    @ElementCollection(fetch = FetchType.EAGER)
    private List<Producto> productos;


    @Embedded
    private DatosVendedor datosVendedor;

    // Getters y Setters

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public BigDecimal getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(BigDecimal precioTotal) {
        this.precioTotal = precioTotal;
    }

    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(LocalDateTime fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    public DatosVendedor getDatosVendedor() {
        return datosVendedor;
    }

    public void setDatosVendedor(DatosVendedor datosVendedor) {
        this.datosVendedor = datosVendedor;
    }

    @Embeddable
    public static class Producto {
        private String nombre;
        private int cantidad;
        private BigDecimal precioUnitario;

        public Producto() {
        }

        public Producto(String nombre, int cantidad, BigDecimal precioUnitario) {
            this.nombre = nombre;
            this.cantidad = cantidad;
            this.precioUnitario = precioUnitario;
        }

        // Getters y Setters

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public int getCantidad() {
            return cantidad;
        }

        public void setCantidad(int cantidad) {
            this.cantidad = cantidad;
        }

        public BigDecimal getPrecioUnitario() {
            return precioUnitario;
        }

        public void setPrecioUnitario(BigDecimal precioUnitario) {
            this.precioUnitario = precioUnitario;
        }
    }

    @Embeddable
    public static class DatosVendedor {
        private String nombre;
        private String direccion;

        public DatosVendedor() {
        }

        public DatosVendedor(String nombre, String direccion) {
            this.nombre = nombre;
            this.direccion = direccion;
        }

        // Getters y Setters

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getDireccion() {
            return direccion;
        }

        public void setDireccion(String direccion) {
            this.direccion = direccion;
        }
    }
}
