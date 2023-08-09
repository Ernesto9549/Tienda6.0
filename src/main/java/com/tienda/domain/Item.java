package com.tienda.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class Item extends Producto{
     int cantidad;  //Para saber la cantidad de productos que quiere

    public Item() {
    }

    public Item(Producto producto) {
        super(producto.idProducto, producto.descripcion, producto.detalle, producto.precio, producto.existencias, producto.rutaImagen, producto.activo, producto.categoria);
        cantidad=0;
    }

    
    
}
