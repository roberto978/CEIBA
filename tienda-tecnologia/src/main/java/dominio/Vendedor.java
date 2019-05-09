package dominio;

import dominio.repositorio.RepositorioProducto;

import java.util.Calendar;
import java.util.Date;

import dominio.excepcion.GarantiaExtendidaException;
import dominio.repositorio.RepositorioGarantiaExtendida;

public class Vendedor {

    public static final String EL_PRODUCTO_TIENE_GARANTIA = "El producto ya cuenta con una garantia extendida";
    public static final String EL_PRODUCTO_NO_CUENTA_CON_GARANTIA = "Este producto no cuenta con garantía extendida";

    private RepositorioProducto repositorioProducto;
    private RepositorioGarantiaExtendida repositorioGarantia;

    public Vendedor(RepositorioProducto repositorioProducto, RepositorioGarantiaExtendida repositorioGarantia) {
        this.repositorioProducto = repositorioProducto;
        this.repositorioGarantia = repositorioGarantia;

    }

    public void generarGarantia(String codigo, String nombreCliente) {

    	if(tieneGarantia(codigo)){
    		throw new GarantiaExtendidaException(Vendedor.EL_PRODUCTO_TIENE_GARANTIA);
    	}
    	
    	long total = codigo.toUpperCase().chars().mapToObj(i -> (char) i).filter((l)->l=='A' || l=='E' || l=='I' || l=='O' || l=='U').count();
        
    	if(total == 3){
    		throw new GarantiaExtendidaException(EL_PRODUCTO_NO_CUENTA_CON_GARANTIA);
    	}
    	
    	Producto producto = repositorioProducto.obtenerPorCodigo(codigo);
    	
    	double precioGarantia = generarPrecioGarantia(producto);
    	Date fechaInicioGarantia  = new Date();
    	Date fechaFinGarantia = generarFechaFinGarantia(fechaInicioGarantia, producto);

    	GarantiaExtendida garantia = new GarantiaExtendida(producto, fechaInicioGarantia, fechaFinGarantia, precioGarantia, nombreCliente );
    	repositorioGarantia.agregar(garantia);
        //throw new UnsupportedOperationException("Método pendiente por implementar");

    }

    public boolean tieneGarantia(String codigo) {
    	if(repositorioGarantia.obtenerProductoConGarantiaPorCodigo(codigo) == null){
    		return false;
    	}else{
    		return true;
    	}
    }
    
    public Date generarFechaFinGarantia(Date fechaInicio, Producto producto){
    	int dias = 0;
    	
    	if(producto.getPrecio() > 500000){
    		dias = 200;
    	}else{
    		dias = 100;
    	}
    	
    	Calendar fecha = Calendar.getInstance();
    	fecha.setTime(fechaInicio);

    	int i = 1;
    	while(i <= dias){
    		if(fecha.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY){
    			fecha.add(Calendar.DAY_OF_YEAR, 1);
    		}
    		fecha.add(Calendar.DAY_OF_YEAR, 1);
    		i++;
    	}
    	
    	if(fecha.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
    		fecha.add(Calendar.DAY_OF_YEAR, 2);
    	}
    	return fecha.getTime();
    }

    public double generarPrecioGarantia(Producto producto){
    	double precioGarantia = 0.0;
    	if(producto.getPrecio() > 500000){
    		precioGarantia = producto.getPrecio() * 0.20;
    	}else{
    		precioGarantia = producto.getPrecio() * 0.10;
    	}
    	return precioGarantia;
    }
}
