package mx.uv.facturas.ORM;

import org.springframework.data.repository.CrudRepository;
import mx.uv.facturas.Modelo.Factura;

public interface IFactura extends CrudRepository<Factura, Long> {
    Factura findByUuid(String uuid);
}
