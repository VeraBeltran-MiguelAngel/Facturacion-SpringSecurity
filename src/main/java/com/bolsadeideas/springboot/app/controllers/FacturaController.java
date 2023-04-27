package com.bolsadeideas.springboot.app.controllers;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bolsadeideas.springboot.app.models.entity.Cliente;
import com.bolsadeideas.springboot.app.models.entity.Factura;
import com.bolsadeideas.springboot.app.models.entity.ItemFactura;
import com.bolsadeideas.springboot.app.models.entity.Producto;
import com.bolsadeideas.springboot.app.models.service.IClienteService;

@Controller
@RequestMapping("/factura")
@SessionAttributes("factura")
public class FacturaController {

	@Autowired
	private IClienteService clienteService;
	// *Para hacer un debug y mostrar valores en consola
	private final Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * Metodo para crear una factura
	 * 
	 * @param clienteId
	 * @param model     para enviar los datos a la vista
	 * @param flash     para enviar mensajes de confirmacion
	 * @return te lleva a la ruta del formulario factura
	 */
	@GetMapping("/form/{clienteId}")
	public String crear(@PathVariable(value = "clienteId") Long clienteId, Map<String, Object> model,
			RedirectAttributes flash) {

		Cliente cliente = clienteService.findOne(clienteId);
		if (cliente == null) {
			flash.addAttribute("error", "El cliente no existe en la base de datos");
			return "redirect:/listar";
		}

		Factura factura = new Factura();
		factura.setCliente(cliente);
		model.put("factura", factura);
		model.put("titulo", "Crear factura");
		return "factura/form";
	}

	/**
	 * Metodo para ir mostrando los productos al momento que escribes
	 * en el campo "Buscar producto", genera una salida de tipo json
	 * 
	 * @ResponseBody suprime el cargar una vista de thymeleaf, en vez de eso toma el
	 *               return convertido a JSON y eso lo va a poblar dentro del body
	 *               de la respuesta
	 * @param term valor ingresado en el campo "BuscarProd"
	 * @return el listado de productos
	 */
	@GetMapping(value = "/cargar-productos/{term}", produces = { "application/json" })
	// transforma la salida en JSON, la guarda dentro del response en el body
	public @ResponseBody List<Producto> cargarProductos(@PathVariable String term) {

		return clienteService.findByNombre(term);
	}

	/**
	 * Metodo para guardar una factura, se obtienen los datos de 'plantilla.html'
	 * y se van recorriendo los input
	 * 
	 * @param factura  objeto factura mapeado al formulario
	 * @param itemId   arreglo que hace referencia al name del input "item_id[]"
	 * @param cantidad arreglo que hace referencia al name del input "cantidad[]"
	 * @param flash    para enviar mensajes a la vista
	 * @param status   finalizar el session attribute
	 * @return  nos dirige a la vista ver.html/ID del cliente
	 */
	@PostMapping("/form")
	public String guardar(Factura factura,
			@RequestParam(name = "item_id[]", required = false) Long[] itemId,
			@RequestParam(name = "cantidad[]", required = false) Integer[] cantidad,
			RedirectAttributes flash,
			SessionStatus status) {
		// recorremos los iD
		for (int i = 0; i < itemId.length; i++) {
			/*  por cada linea de la factura obtenemos el producto, pasamos el ID atraves del
			 arreglo */
			Producto producto = clienteService.findProductoById(itemId[i]);
			// creamos una instancia de ItemFactura y les pasamos la cantidad y el producto
			ItemFactura linea = new ItemFactura();
			linea.setCantidad(cantidad[i]);
			linea.setProducto(producto);
			//agregamos la linea a la actura
			factura.addItemFactura(linea);

			//Debug mostramos los valores de ID y Cantidad en consola 
			log.info("ID: " + itemId[i].toString() + ", cantidad: " + cantidad[i].toString());
		}
		//guardar factura en la base de datos 
		clienteService.saveFactura(factura);
		// finalizar el session attribute y eliminar la factura de la sesion 
		status.setComplete();
		//enviamos mensaje a la vista
		flash.addFlashAttribute("success", "Factura creada con éxito!");

		return "redirect:/ver/" + factura.getCliente().getId();
	}

}
