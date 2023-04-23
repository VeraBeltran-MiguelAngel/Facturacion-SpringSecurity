package com.bolsadeideas.springboot.app.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bolsadeideas.springboot.app.models.entity.Cliente;
import com.bolsadeideas.springboot.app.models.entity.Factura;
import com.bolsadeideas.springboot.app.models.entity.Producto;
import com.bolsadeideas.springboot.app.models.service.IClienteService;

@Controller
@RequestMapping("/factura")
@SessionAttributes("factura")
public class FacturaController {

	@Autowired
	private IClienteService clienteService;
/**
 * Metodo para crear una factura 
 * @param clienteId 
 * @param model para enviar los datos a la vista 
 * @param flash para enviar mensajes de confirmacion 
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
	 * @ResponseBody suprime el cargar una vista de thymeleaf, en vez de eso toma el 
	 * return convertido a JSON y eso lo va a poblar dentro del body de la respuesta
	 * @param term valor ingresado en el campo "BuscarProd"
	 * @return el listado de productos
	 */
	@GetMapping(value="/cargar-productos/{term}",produces= {"application/json"})
	//transforma la salida en JSON, la guarda dentro del response en el body
	public @ResponseBody List<Producto> cargarProductos(@PathVariable String term){
		
		return clienteService.findByNombre(term);
	}

}
