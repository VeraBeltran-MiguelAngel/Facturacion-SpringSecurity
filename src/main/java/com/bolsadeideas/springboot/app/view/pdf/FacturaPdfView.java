package com.bolsadeideas.springboot.app.view.pdf;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.bolsadeideas.springboot.app.models.entity.Factura;
import com.bolsadeideas.springboot.app.models.entity.ItemFactura;
import com.lowagie.text.Document;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component("factura/ver") // para que se pueda renderizar le damos el mismo nombre del return de 'ver' en
                          // facturacontroller, para que en vez de mostrar el contenido en una pagina html
                          // lo muestre en un PDF
public class FacturaPdfView extends AbstractPdfView {

    /**
     * @param model    guarda datos a la vista
     * @param document representa al PDF
     */
    @Override
    protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        // obtenenmos la factura a traves de los datos que manda la vista (model), se
        // tiene que llamar igual al objeto que se guarda en 'ver ' de facturacontroller
        Factura factura = (Factura) model.get("factura");

        //tabla con una columna y tres filas 
        PdfPTable tabla = new PdfPTable(1);
        //espacio despues de la tabla 
        tabla.setSpacingAfter(20);
        tabla.addCell("Datos del cliente");
        tabla.addCell(factura.getCliente().getNombre() + "" + factura.getCliente().getApellido());
        tabla.addCell(factura.getCliente().getEmail());

        //tabla 2
        PdfPTable tabla2 = new PdfPTable(1);
        tabla2.setSpacingAfter(20);
        tabla2.addCell("Datos de la Factura");
        tabla2.addCell("Folio: " + factura.getId());
        tabla2.addCell("Descripcion: " + factura.getDescripcion());
        tabla2.addCell("Fecha: " + factura.getCreateAt());

        document.add(tabla);
        document.add(tabla2);

        //tabla 3 

        PdfPTable tabla3 = new PdfPTable(4);
        tabla3.addCell("Producto");
        tabla3.addCell("Precio");
        tabla3.addCell("Cantidad");
        tabla3.addCell("Total");

        for (ItemFactura item : factura.getItems()) {
            tabla3.addCell(item.getProducto().getNombre());
            tabla3.addCell(item.getProducto().getPrecio().toString());
            tabla3.addCell(item.getCantidad().toString());
            tabla3.addCell(item.calcularImporte().toString());
        }

        PdfPCell cell = new PdfPCell(new Phrase("Total: "));
        //que ocupe tres columnas
        cell.setColspan(3);
        // alinear a la derecha
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        tabla3.addCell(cell);
        tabla3.addCell(factura.getTotal().toString());

        document.add(tabla3);
    }

}
