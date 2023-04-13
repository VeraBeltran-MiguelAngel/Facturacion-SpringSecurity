package com.bolsadeideas.springboot.app.models.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bolsadeideas.springboot.app.models.entity.Cliente;
// como la interfaz JPAREpository ya es un componente de spring no hay que 
//colocar ninguna anotacion
public interface IClienteDao extends JpaRepository<Cliente, Long>{
	
// podriamos crear metodos con consultas personalizadas 
	// que no cubran los metodos default de la interfaz
}
