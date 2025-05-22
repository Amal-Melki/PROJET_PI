package com.esprit.controllers;

import com.esprit.modules.Materiels;
import com.esprit.services.ServiceMateriel;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@RestController
@RequestMapping("/api/materiels")
public class ExportMaterielCSV {

    @Autowired
    private ServiceMateriel serviceMateriel;

    @GetMapping(value = "/export-csv", produces = "text/csv")
    public void exportMaterielsCSV(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=materiels.csv");

        List<Materiels> materiels = serviceMateriel.recuperer();

        try (PrintWriter writer = response.getWriter()) {
            writer.println("ID,Nom,Type,Quantité,État,Description,Image,Prix");

            for (Materiels m : materiels) {
                writer.printf("%d,%s,%s,%d,%s,%s,%s,%.2f%n",
                        m.getId(),
                        m.getNom(),
                        m.getType(),
                        m.getQuantite(),
                        m.getEtat(),
                        m.getDescription(),
                        m.getImage(),
                        m.getPrix());
            }
        }
    }
}
