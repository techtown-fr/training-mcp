package fr.techtown.geocalc.mcp;

import java.util.List;
import java.util.Map;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
public class GeoCalcTools {

    private final ShapeCatalog catalog;

    public GeoCalcTools(ShapeCatalog catalog) {
        this.catalog = catalog;
    }

    @McpTool(
        name = "list_shapes",
        description = "Liste les formes supportées par GéoCalc",
        generateOutputSchema = true,
        annotations = @McpTool.McpAnnotations(
            readOnlyHint = true,
            destructiveHint = false,
            openWorldHint = false
        )
    )
    public ShapeList listShapes() {
        // MCP outputSchema doit être un object (pas un array racine) pour les clients stricts
        return new ShapeList(catalog.listShapes());
    }

    public record ShapeList(List<ShapeCatalog.ShapeDefinition> shapes) {
    }

    @McpTool(
        name = "get_formula",
        description = "Retourne la formule et les dimensions requises pour une forme",
        generateOutputSchema = true,
        annotations = @McpTool.McpAnnotations(
            readOnlyHint = true,
            destructiveHint = false,
            openWorldHint = false
        )
    )
    public ShapeCatalog.ShapeDefinition getFormula(
            @McpToolParam(
                description = "Identifiant de forme : circle, rectangle ou triangle",
                required = true
            )
            String shape) {
        return catalog.getFormula(shape);
    }

    @McpTool(
        name = "validate_calculation",
        description = "Compare un résultat aux dimensions et à la formule GéoCalc",
        generateOutputSchema = true,
        annotations = @McpTool.McpAnnotations(
            readOnlyHint = true,
            destructiveHint = false,
            openWorldHint = false
        )
    )
    public ShapeCatalog.ValidationResult validateCalculation(
            @McpToolParam(
                description = "Identifiant : circle, rectangle ou triangle",
                required = true
            )
            String shape,
            @McpToolParam(
                description = "Dimensions numériques, par exemple {\"radius\": 3}",
                required = true
            )
            Map<String, Double> dimensions,
            @McpToolParam(description = "Résultat à vérifier", required = true)
            double result) {
        return catalog.validate(shape, dimensions, result);
    }
}
